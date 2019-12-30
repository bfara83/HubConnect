/*
 *	Copyright 2019 Steve White
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *	use this file except in compliance with the License. You may obtain a copy
 *	of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *	License for the specific language governing permissions and limitations
 *	under the License.
 *
 *
 */
import hubitat.helper.InterfaceUtils
metadata
{
	definition(name: "HubConnect Remote Hub", namespace: "shackrat", author: "Steve White", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/Hubitat/drivers/HubConnect-Remote-Hub.groovy")
	{
		capability "Presence Sensor"
		capability "Switch"
		capability "Initialize"

		attribute "eventSocketStatus", "string"
		attribute "connectionType", "string"
		attribute "version", "string"
		attribute "hsmStatus", "enum", ["armedAway", "armingAway",  "armedHome", "armingHome", "armedNight", "armingNight", "disarmed", "allDisarmed"]
		attribute "modeStatus", "string"

		command "pushCurrentMode"

		preferences
		{
			input(name: "refreshSocket", type: "bool", title: "Re-connect webscocket daily at...", required: true)
			input(name: "refreshHour", type: "number", title: "... hour (0-23)", range: "0...23", defaultValue: 3, required: false)
			input(name: "refreshMinute", type: "number", title: "... minute (0-59)", range: "0...59", defaultValue: 0, required: false)
		}
	}
}


/*
	installed

	Doesn't do much other than call initialize().
*/
def installed()
{
	sendEvent([name: "switch", value: "off"])
	initialize()
	state.connectionType = "http"
}


/*
	updated

	Doesn't do much other than call initialize().
*/
def updated()
{
	initialize()
	if (state.connectionType == null) state.connectionType = "http"

	unschedule()
	if (state.connectionType == "socket" && refreshSocket) schedule("0 ${refreshMinute} ${refreshHour} * * ?", initialize)
}


/*
	initialize

	Doesn't do much other than call refresh().
*/
def initialize()
{
	log.trace "Initialize virtual Hub device..."

	// Make sure we're not an orphaned device
	if (checkParent() == false) return

	state.connectionAttempts = 0

	if (state.connectionType == "socket")
	{
		// Connect to the remote hubs event socket
		try
		{
			log.info "Attempting socket connection to ${device.label ?: device.name} (${state.connectionAttempts})"
			InterfaceUtils.webSocketConnect(device, "ws://${getDataValue("remoteIP")}:${getDataValue("remotePort")}/eventsocket")
		}
		catch(errorException)
		{
			log.error "WebSocket connect to remote hub failed: ${errorException.message}"
    	}
	}
	sendEvent([name: "connectionType", value: state.connectionType])
	sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}


/*
	webSocketStatus

	Called by the websocket to the remote Hubitat hub.
*/
def webSocketStatus(String socketStatus)
{
	if (socketStatus.startsWith("status: open"))
	{
		// Make sure we're not an orphaned device
		if (checkParent() == false) return

		log.info "Connected to ${device.label ?: device.name}"
		state.connectionAttempts = 0
		sendEvent([name: "eventSocketStatus", value: "connected"])
		sendEvent([name: "switch", value: "on"])
		return
    }
	else if (socketStatus.startsWith("status: closing"))
	{
		log.info "Closing connection to ${device.label ?: device.name}"
		sendEvent([name: "eventSocketStatus", value: "closed"])
		sendEvent([name: "switch", value: "off"])
		return
	}
	else if (socketStatus.startsWith("failure:"))
	{
		log.warn "Connection to ${device.label ?: device.name} has failed with error [${socketStatus}].  Attempting to reconnect..."
    }
	else
	{
		log.warn "Connection to ${device.label ?: device.name} has been lost due to an unknown error.  Attempting to reconnect..."
    }

	state.connectionAttempts = state.connectionAttempts + 1
	runIn(10, "initialize")
}


/*
	setConnectionType

	Called by Server Instance or Remote Client to set the connection type.
*/
def setConnectionType(String connType, String remoteIP, String remotePort)
{
	state.connectionType = connType
	updateDataValue("remoteIP", remoteIP)
	updateDataValue("remotePort", remotePort)

	// Switch connections
	if (connType == "http" && device.currentEventSocketStatus == "connected")
	{
		InterfaceUtils.webSocketClose(device)
		sendEvent([name: "eventSocketStatus", value: "disconnected"])
		sendEvent([name: "connectionType", value: "http"])
	}
	else if (connType == "socket" && device.currentEventSocketStatus != "connected")
	{
		initialize()
	}

	log.info "Switching connection to ${device.label ?: device.name} to ${connType}"
}


/*
	parse

	In a virtual world this should never be called.
*/
def parse(String description)
{
	Object eventData = (Object) null
	try
	{
		eventData = (Object) parseJson(description)
	}
	catch(errorException)
	{
		log.error "Failed to parse event data: ${errorException}"
		return
    }

	if (eventData.source.length() == 6 && state.subscribedDevices.contains((int) eventData.deviceId)) // "DEVICE"
	{
		eventData.isStateChange = (eventData.name == "pushed" || eventData.name ==  "held" || eventData.name == "doubleTapped" || eventData.name ==  "released") ? true : false
		parent.wsSendEvent(eventData)
	}
}


/*
	on

	Enable communications from the remote hub.
*/
def on()
{
	// Make sure we're not an orphaned device
	if (checkParent() == false) return

	parent.setCommStatus(false)
	if (state.connectionType == "socket")
	{
		sendEvent([name: "eventSocketStatus", value: "connecting"])
	}
	initialize()
}


/*
	off

	Disable communications from the remote hub.
*/
def off()
{
	// Make sure we're not an orphaned device
	if (checkParent() == false) return

	parent.setCommStatus(true)
	if (state.connectionType == "socket")
	{
		InterfaceUtils.webSocketClose(device)
	}
}


/*
	pushCurrentMode

	Pushes the current mode of the server hub to the remote hub.
*/
def pushCurrentMode()
{
	// Make sure we're not an orphaned device
	if (checkParent() == false) return

	parent.pushCurrentMode()
}


/*
	updateDeviceIdList

	Updates the list of deviceIds that this hub should listen for.
*/
def updateDeviceIdList(deviceIdList)
{
    state.subscribedDevices = deviceIdList
}


/*
	checkParent

	Checks to make sure the device is associated with a HubConnect parent app.
*/
def checkParent()
{
	// Make sure we're not an orphan
	if (parent == null)
	{
		log.error "This HubConnect Remote Hub is no longer associated with HubConnect and is being disabled.  Please remove it from this hub."
		if (state?.connectionType == "socket")
		{
			InterfaceUtils?.webSocketClose(device)
		}
		subscribedDevices = []
		state.connectionType = "DEVICE DISCONNECTED.  Please DELETE!"
		sendEvent([name: "eventSocketStatus", value: "disassociated"])
		sendEvent([name: "switch", value: "off"])
		sendEvent([name: "modeStatus", value: "invalid"])
		device.setDeviceNetworkId("hubconnect-orphan-${now()}")
		device.setLabel("HubConnect Disabled Hub")
		return false
	}
	return true
}
def getPref(setting) {return settings."${setting}"}
def getDriverVersion() {[platform: "Hubitat", major: 1, minor:6, build: 1]}
