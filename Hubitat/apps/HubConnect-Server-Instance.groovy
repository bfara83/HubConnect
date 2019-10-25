/**
 * HubConnect Server Instance
 *
 * Copyright 2019 Steve White, Retail Media Concepts LLC.
 *
 * HubConnect for Hubitat is a software package created and licensed by Retail Media Concepts LLC.
 * HubConnect, along with associated elements, including but not limited to online and/or electronic documentation are
 * protected by international laws and treaties governing intellectual property rights.
 *
 * This software has been licensed to you. All rights are reserved. You may use and/or modify the software.
 * You may not sublicense or distribute this software or any modifications to third parties in any way.
 *
 * By downloading, installing, and/or executing this software you hereby agree to the terms and conditions set forth in the HubConnect license agreement.
 * <http://irisusers.com/hubitat/hubconnect/HubConnect_License_Agreement.html>
 * 
 * Hubitat is the trademark and intellectual property of Hubitat, Inc. Retail Media Concepts LLC has no formal or informal affiliations or relationships with Hubitat.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License Agreement
 * for the specific language governing permissions and limitations under the License.
 *
 */
import groovy.transform.Field
import groovy.json.JsonOutput
definition(
	name: "HubConnect Server Instance",
	namespace: "shackrat",
	author: "Steve White",
	description: "Synchronizes devices and events across multiple hubs..",
	category: "My Apps",
	parent: "shackrat:HubConnect Server for Hubitat",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches@2x.png"
)


// Preference pages
preferences
{
	page(name: "mainPage")
	page(name: "connectPage")
	page(name: "devicePage")
	page(name: "customDevicePage")
	page(name: "dynamicDevicePage")
}


// Map containing driver and attribute definitions for each device class
@Field static NATIVE_DEVICES =
[
	"arlocamera":		[driver: "Arlo Camera", selector: "arloProCameras", attr: ["switch", "motion", "sound", "rssi", "battery"]],
	"arloqcamera":		[driver: "Arlo Camera", selector: "arloQCameras", attr: ["switch", "motion", "sound", "rssi", "battery"]],
	"arrival":			[driver: "Arrival Sensor", selector: "smartThingsArrival", capability: "presenceSensor", prefGroup: "other", attr: ["presence", "battery", "tone"]],
	"audioVolume":		[driver: "AVR", selector: "audioVolume", capability: "audioVolume", prefGroup: "other", attr: ["switch", "mediaInputSource", "mute", "volume"]],
	"button":			[driver: "Button", selector: "genericButtons", capability: "pushableButton", prefGroup: "other", attr: ["numberOfButtons", "pushed", "held", "doubleTapped", "button", "temperature", "battery"]],
	"contact":			[driver: "Contact Sensor", selector: "genericContacts", capability: "contactSensor", prefGroup: "sensors", attr: ["contact", "temperature", "battery"]],
	"dimmer":			[driver: "Dimmer", selector: "genericDimmers", capability: "switchLevel", prefGroup: "switches", attr: ["switch", "level"]],
	"domemotion":		[driver: "Dome Motion Sensor", selector: "domeMotions", capability: "motionSensor", prefGroup: "sensors", attr: ["motion", "temperature", "illuminance", "battery"]],
	"energyplug":		[driver: "DomeAeon Plug", selector: "energyPlugs", capability: "energyMeter", prefGroup: "switches", attr: ["switch", "power", "voltage", "current", "energy", "acceleration"]],
	"fancontrol":		[driver: "Fan Controller", selector: "fanControl", capability: "fanControl", prefGroup: "switches", attr: ["speed"]],
	"fanspeed":			[driver: "FanSpeed Controller", selector: "fanSpeedControl", capability: "fanControl", prefGroup: "switches", attr: ["speed"]],
	"garagedoor":		[driver: "Garage Door", selector: "garageDoors", capability: "garageDoorControl", prefGroup: "other", attr: ["door", "contact"]],
	"gvomnisensor":		[driver: "GvOmniSensor", selector: "gvOmniSensor", capability: "waterSensor", prefGroup: "sensors", attr: ["acceleration", "carbonDioxide", "carbonMonoxide", "contact", "humidity", "illuminance", "motion", "presence", "smoke", "temperature", "variable", "water"]],
	"irissmartplug":		[driver: "Iris Smart Plug", selector: "smartPlugs", capability: "device.IrisSmartPlug", prefGroup: "shackrat", attr: ["switch", "power", "voltage", "ACFrequency"]],
	"irisv3motion":		[driver: "IrisV3 Motion Sensor", selector: "irisV3Motions", capability: "motionSensor", prefGroup: "sensors", attr: ["motion", "temperature", "humidity", "battery"]],
	"keypad":			[driver: "Keypad", selector: "genericKeypads", capability: "securityKeypad", prefGroup: "safety", attr: ["motion", "temperature", "battery", "tamper", "alarm", "lastCodeName"]],
	"lock":			[driver: "Lock", selector: "genericLocks", capability: "lock", prefGroup: "safety", attr: ["lock", "lockCodes", "lastCodeName", "codeChanged", "codeLength", "maxCodes", "battery"]],
	"mobileApp":		[driver: "Mobile App", selector: "mobileApp", capability: "notification", prefGroup: "other", attr: ["presence", "notificationText"]],
	"moisture":			[driver: "Moisture Sensor", selector: "genericMoistures", capability: "waterSensor", prefGroup: "safety", attr: ["water", "temperature", "battery"]],
	"motion":			[driver: "Motion Sensor", selector: "genericMotions", capability: "motionSensor", prefGroup: "sensors", attr: ["motion", "temperature", "battery"]],
	"multipurpose":		[driver: "Multipurpose Sensor", selector: "genericMultipurposes", capability: "accelerationSensor", prefGroup: "sensors", attr: ["contact", "temperature", "battery", "acceleration", "threeAxis"]],
	"omnipurpose":		[driver: "Omnipurpose Sensor", selector: "genericOmnipurposes", capability: "relativeHumidityMeasurement", prefGroup: "sensors", attr: ["motion", "temperature", "humidity", "illuminance", "ultravioletIndex", "tamper", "battery"]],
	"pocketsocket":		[driver: "Pocket Socket", selector: "pocketSockets", capability: "switch", prefGroup: "switches", attr: ["switch", "power"]],
	"power":			[driver: "Power Meter", selector: "powerMeters", capability: "powerMeter", prefGroup: "switches", attr: ["power"]],
	"presence":			[driver: "Presence Sensor", selector: "genericPresences", capability: "presenceSensor", prefGroup: "other", attr: ["presence", "battery"]],
	"ringdoorbell":		[driver: "Ring Doorbell", selector: "ringDoorbellPros", attr: ["numberOfButtons", "pushed", "motion"]],
	"rgbbulb":			[driver: "RGB Bulb", selector: "genericRGBs", capability: "colorControl", prefGroup: "switches", attr: ["switch", "level", "hue", "saturation", "RGB", "color", "colorMode", "colorTemperature"]],
	"rgbwbulb":			[driver: "RGBW Bulb", selector: "genericRGBW", capability: "colorMode", prefGroup: "switches", attr: ["switch", "level", "hue", "saturation", "RGB(w)", "color", "colorMode", "colorTemperature"]],
	"shock":			[driver: "Shock Sensor", selector: "genericShocks", capability: "shockSensor", prefGroup: "sensors", attr: ["shock", "battery"]],
	"siren":			[driver: "Siren", selector: "genericSirens", capability: "alarm", prefGroup: "safety", attr: ["switch", "alarm", "battery"]],
	"smartsmoke":		[driver: "Smart Smoke/CO", selector: "smartSmokeCO", capability: "device.HaloSmokeAlarm", prefGroup: "safety", attr: ["smoke", "carbonMonoxide", "battery", "temperature", "humidity", "switch", "level", "hue", "saturation", "pressure"]],
	"smoke":			[driver: "Smoke/CO Detector", selector: "genericSmokeCO", capability: "smokeDetector", prefGroup: "safety", attr: ["smoke", "carbonMonoxide", "battery"]],
	"speechSynthesis":	[driver: "SpeechSynthesis", selector: "speechSynth", capability: "speechSynthesis", prefGroup: "other", attr: ["mute", "version", "volume"]],
	"switch":			[driver: "Switch", selector: "genericSwitches", capability: "switch", prefGroup: "switches", attr: ["switch"]],
	"thermostat":		[driver: "Thermostat", selector: "genericThermostats", capability: "thermostat", prefGroup: "other", attr: ["coolingSetpoint", "heatingSetpoint", "schedule", "supportedThermostatFanModes", "supportedThermostatModes", "temperature", "thermostatFanMode", "thermostatMode", "thermostatOperatingState", "thermostatSetpoint"]],
	"windowshade":		[driver: "Window Shade", selector: "windowShades", capability: "windowShade", prefGroup: "other", attr: ["switch", "position", "windowShade"]],
	"valve":			[driver: "Valve", selector: "genericValves", capability: "valve", prefGroup: "other", attr: ["valve"]],
	"zwaverepeater":		[driver: "Iris Z-Wave Repeater", selector: "zwaveRepeaters", capability: "device.IrisZ-WaveRepeater", prefGroup: "shackrat", attr: ["status", "lastRefresh", "deviceMSR", "lastMsgRcvd"]]
]


// Mapping to receive events
mappings
{
	// Server Mappings
    path("/devices/save")
	{
		action: [POST: "saveDevices"]
	}
    path("/devices/get")
	{
		action: [GET: "getAllDevices"]
	}
    path("/device/:deviceId/event/:event")
	{
		action: [GET: "deviceEvent"]
	}
    path("/ping")
	{
		action: [GET: "registerPing"]
	}
    path("/connect/:data")
	{
		action: [GET: "connectRemoteHub"]
	}
	

	// Client mappings
    path("/event/:deviceId/:deviceCommand/:commandParams")
	{
		action: [GET: "remoteDeviceCommand"]
	}
    path("/device/:deviceId/sync/:type")
	{
		action: [GET: "syncRemoteDevice"]
	}
    path("/modes/get")
	{
		action: [GET: "getAllModes"]
	}
	path("/modes/set/:name")
	{
		action: [GET: "clientModeChangeEvent"]
	}
    path("/hsm/get")
	{
		action: [GET: "getAllHSMStates"]
	}
    path("/hsm/set/:name")
	{
		action: [GET: "hsmReceiveEvent"]
	}
    path("/hsm/alert/:text")
	{
		action: [GET: "hsmReceiveAlert"]
	}		
}


/*
	syncDevice
    
	Purpose: Sync virtual device on this hub with the physcial (remote) device by requesting an update of all attribute values.

	URL Format: GET /device/:deviceId/sync/:type

	Notes: CALLED FROM CHILD DEVICE DRIVER
*/
def syncDevice(deviceNetworkId, deviceType)
{
	def dniParts = deviceNetworkId.split(":")
	def childDevice = childDevices?.find { it.deviceNetworkId == deviceNetworkId }
	if (childDevice != null)
	{
		if (enableDebug) log.debug "Requesting device sync from ${clientName}: ${childDevice.label}"

		def data = httpGetWithReturn("/device/${dniParts[1]}/sync/${deviceType}")
		if (data?.status == "success")
		{
			childDevice.setLabel(data.label)
			
			data?.currentValues.each
			{
			  attr ->
				childDevice.sendEvent([name: attr.name, value: attr.value, unit: attr.unit, descriptionText: "Sync: ${childDevice.displayName} ${attr.name} is ${attr.value} ${attr.unit}", isStateChange: true])
			}
		}
	}
}


/*
	sendDeviceEvent
    
	Purpose: Send an event to a client device.

	URL format: GET /event/:deviceId/:deviceCommand/:commandParams

	Notes: CALLED FROM CHILD DEVICE
*/
def sendDeviceEvent(deviceId, deviceCommand, List commandParams=[])
{
	if (state.commDisabled) return

	def dniParts = deviceId.split(":")
	def paramsEncoded = commandParams ? URLEncoder.encode(new groovy.json.JsonBuilder(commandParams).toString()) : null
	sendGetCommand("/event/${dniParts[1]}/${deviceCommand}/${paramsEncoded}")
}


/*
	deviceEvent
    
	Purpose: Handler for events received from physical devices on remote hubs.

	URL Format: (GET) /device/:deviceId/event/:event

	Notes: 	Buttons require the state change flag to be set to true in order for events to fire.
			(For other types, pass value through as-is.)
*/
def deviceEvent()
{
	def eventraw = params.event ? URLDecoder.decode(params.event) : null
	if (eventraw == null) return

	def event = parseJson(eventraw)
	def data = event?.data ?: ""
	def unit = event?.unit ?: ""

	// We can do this faster if we don't need info on the device
	if (state.deviceIdList.contains(params.deviceId))
	{
		sendEvent("${clientIP}:${params.deviceId}", (Map) [name: event.name, value: event.value, unit: unit, descriptionText: "${event?.displayName} ${event.name} is ${event.value} ${unit}", isStateChange: true, data: data])
		if (enableDebug) log.info "Received event from ${clientName}/${event.displayName}: [${event.name}, ${event.value} ${unit}]"
		return jsonResponse([:])
	}

	if (enableDebug) log.warn "Ignoring Received event from ${clientName}: Device Not Found!"
}


/*
	wsSendEvent
    
	Purpose: Handler for events received from physical devices through the websocket interface.

	Notes: 	This is only called by the hub device for events received through its local websocket.
			Also, this does not warn when a device cannot be found as websockets get ALL events.
*/
def wsSendEvent(event)
{
	// We can do this faster if we don't need info on the device, so defer that for logging
	if (state.deviceIdList.contains((int) event.deviceId))
	{
		sendEvent("${clientIP}:${event.deviceId}", (Map) [name: event.name, value: event.value, unit: event.unit, descriptionText: event.descriptionText, isStateChange: true])
		if (enableDebug) log.info "Received event from ${clientName}/${event.displayName}: [${event.name}, ${event.value} ${event.unit}]"
	}
}


/*
	realtimeModeChangeHandler
    
	URL Format: GET /modes/set/modeName

	Purpose: Event handler for mode change events on the controller hub (this one).
*/
def realtimeModeChangeHandler(evt)
{
	if (!pushModes) return

	if (enableDebug) log.debug "Sending mode change event to ${clientName}: ${evt.value}"
	sendGetCommand("/modes/set/${URLEncoder.encode(evt.value)}")

	getChildDevices()?.find{it.deviceNetworkId == "hub-${clientIP}"}?.sendEvent(name: "modeStatus", value: evt.value)
}


/*
	realtimeHSMChangeHandler
    
	URL Format: GET /hsm/set/hsmStateName

	Purpose: Event handler for HSM state change events on the controller hub (this one).
*/
def realtimeHSMChangeHandler(evt)
{
	if (!pushHSM) return
	def newState = evt.value

	if (evt?.data?.toInteger() != app.id && atomicState.lastHSMChange != evt.value)
	{
		if (enableDebug) log.debug "Sending HSM state change event to ${clientName}: ${newState}"
		sendGetCommand("/hsm/set/${URLEncoder.encode(newState)}")
		atomicState.lastHSMChange = evt.value
	}
	else if (enableDebug) log.info "Filtering duplicate HSM state change event."
}


/*
	realtimeHSMStatusHandler

	Purpose: Updates virtual hub device with correct HSM status.
*/
def realtimeHSMStatusHandler(evt)
{
	getChildDevices()?.find{it.deviceNetworkId == "hub-${clientIP}"}?.sendEvent(name: "hsmStatus", value: evt.value)
}


/*
	hsmSendAlert
    
	URL Format: GET /hsm/set/hsmStateName

	Purpose: Event handler for HSM state change events on the controller hub (this one).
*/
def hsmSendAlert(hsmAlertText)
{
	if (!pushHSM) return

	if (enableDebug) log.debug "Sending HSM alert change event to ${clientName}: ${hsmAlertText}"
	sendGetCommand("/hsm/alert/${URLEncoder.encode(hsmAlertText)}")
}


/*
	saveDevices
    
	Purpose: Creates virtual, linked devices as received from the remote client hub.

	URL Format: (POST) /devices/save

	Notes: 	Thank god this isn't SmartThings, or this would time out after creating three devices!
*/
def saveDevices()
{	
	// Find the device class
	if (!request?.JSON?.deviceclass || !request?.JSON?.devices)
	{
		return jsonResponse([status: "error"])
	}

	if (NATIVE_DEVICES.find {it.key == request.JSON.deviceclass})
	{
		// Create the devices
		request.JSON.devices.each { createLinkedChildDevice(it, "HubConnect ${NATIVE_DEVICES[request.JSON.deviceclass].driver}") }
	}
	else if (state.customDrivers.find {it.key == request.JSON.deviceclass})
	{
		// Get the custom device type and create the devices
		request.JSON.devices.each { createLinkedChildDevice(it, "${state.customDrivers[request.JSON.deviceclass].driver}") }		
	}

	// Build a lookup list
	state.deviceIdList = new HashSet<>()
	childDevices.each
	{
		def parts = it.deviceNetworkId.split(":")
		if (parts.size() > 1) state.deviceIdList << (localConnectionType != "socket" ? parts[1].toString() : parts[1].toInteger())
	}

	jsonResponse([status: "complete"])
}


/*
	createLinkedChildDevice
    
	Purpose: Helper function to create child devices. 

	Notes: 	Called from saveDevices()
*/
private createLinkedChildDevice(dev, driverType)
{
	def childDevice = getChildDevices()?.find{it.deviceNetworkId == "${clientIP}:${dev.id}"}
	if (childDevice != null)
	{
		// Device exists
		if (enableDebug) log.trace "${driverType} ${dev.label} exists... Skipping creation.."
		return
	}
	else
	{
		if (enableDebug) log.trace "Creating Device ${driverType} - ${dev.label}... ${clientIP}:${dev.id}..."
		try
		{
			childDevice = addChildDevice("shackrat", driverType, "${clientIP}:${dev.id}", null, [name: dev.label, label: dev.label])
		}
		catch (errorException)
		{
			log.error "... Uunable to create device ${dev.label}: ${errorException}."
			childDevice = null
		}
	}

	// Set the value of the primary attributes
	if (childDevice != null)
	{
		dev.attr.each
		{
	 	 attribute ->
			childDevice.sendEvent([name: attribute.name, value: attribute.value, unit: attribute.unit])
		}
	}
}


/*
	createHubChildDevice
    
	Purpose: Create child device for the remote hub so up/down status can be managed with rules.

	Notes: 	Called from initialize()
*/
private createHubChildDevice()
{
	def hubDevice = getChildDevices()?.find{it.deviceNetworkId == "hub-${clientIP}"}
	if (hubDevice != null)
	{
		// Hub exists
		log.error "Hub device exists... Skipping creation.."
		hubDevice = null
	}
	else
	{
		if (enableDebug) log.trace "Creating hub Device ${clientName}... hub-${clientIP}..."
		
		try
		{
			hubDevice = addChildDevice("shackrat", "HubConnect Remote Hub", "hub-${clientIP}", null, [name: "HubConnect Hub", label: clientName])
		}
		catch (errorException)
		{
			log.error "Unable to create the Hub monitoring device: ${errorException}.   Support Data: [id: \"hub-${clientIP}\", name: \"HubConnect Hub\", label: \"${clientName}\"]"
			hubDevice = null
		}
		
		// Set the value of the primary attributes
		if (hubDevice != null) sendEvent("hub-${clientIP}", [name: "presence", value: "present"])
	}

	hubDevice
}


/*
	syncRemoteDevice
    
	Purpose: Retrieves the physical (local) device details and returns them to the client (remote) hub.

	Notes: Called from HTTP request from client (remote) hub.
*/
def syncRemoteDevice()
{
	log.info "Received device update request from client: [${params.deviceId}, type ${params.type}]"
	
	def device = getDevice(params)
	if (device != null)
	{
		def currentAttributes = getAttributeMap(device, params.type)	
		def label = device.label ?: device.name
		jsonResponse([status: "success", name: "${device.name}", label: "${label}", currentValues: currentAttributes])
	}
}


/*
	getDevice
    
	Purpose: Helper function to retreive a specific device from all selected devices. 
*/
def getDevice(params)
{
	def foundDevice = null

	NATIVE_DEVICES.each
	{
	  groupname, device ->
		if (foundDevice != null) return
		foundDevice = settings."${device.selector}"?.find{it.id == params.deviceId}
	}

	// Custom devices drivers
	if (foundDevice == null)
	{
		state.customDrivers?.each
		{
	 	  groupname, device ->
			if (foundDevice != null) return
			foundDevice = settings."custom_${groupname}".find{it.id == params.deviceId}
		}
	}
	return foundDevice
}


/*
	remoteDeviceCommand
    
	Purpose: Event handler for remote hub device events.

	URL format: GET /event/:deviceId/:deviceCommand/:commandParams

	Notes: Called from HTTP request from client (remote) hub.
*/
def remoteDeviceCommand()
{
	def commandParams = params.commandParams != "null" ? parseJson(URLDecoder.decode(params.commandParams)) : null

	// Get the device
	def device = getDevice(params)
	if (device == null)
	{
		log.error "Could not locate a device with an id of ${device.deviceId}"
		return jsonResponse([status: "error"])
	}
	
	if (enableDebug) log.info "Received command from client: [\"${device.label ?: device.name}\": ${params.deviceCommand}]"
	
	// Make sure the physical device supports the command
	if (!device.hasCommand(params.deviceCommand))
	{
		log.warn "The device [${device.label ?: device.name}] does not support the command ${params.deviceCommand}."
		return jsonResponse([status: "error"])
	}

	// Execute the command
	device."${params.deviceCommand}"(*commandParams)
	
	jsonResponse([status: "success"])
}


/*
	clientModeChangeEvent
    
	Purpose: Event handler for client (remote) mode change events.

	URL Format: (GET) /modes/set/:name

	Notes: Called from HTTP request from remote hub.
*/
def clientModeChangeEvent()
{
	if (!receiveModes) return
    def modeName = params?.name ? URLDecoder.decode(params?.name) : ""

    if (location.modes?.find{it.name == modeName})
	{
		if (enableDebug) log.debug "Received mode event from ${clientName}: ${modeName}"
		setLocationMode(modeName)
		jsonResponse([status: "complete"])		
	}
	else
	{
		log.error "Received mode event from client: ${modeName} does not exist!"
		jsonResponse([status: "error"])	
    }
}


/*
	hsmReceiveEvent
    
	Purpose: Event handler for server (controller) HSM status change events.

	URL Format: (GET) /hsm/set/:name

	Notes: Called from HTTP request from remote hub.
*/
def hsmReceiveEvent()
{
	if (!receiveHSM) return
    def hsmState = params?.name ? URLDecoder.decode(params?.name) : ""

    if (["armAway", "armHome", "armNight", "disarm", "armRules", "disarmRules", "disarmAll", "armAll", "cancelAlerts"].find{it == hsmState})
	{
		if (enableDebug) log.debug "Received HSM event from server: ${hsmState}"

		if (location.hsmStatus != null || location.hsmStatus != "") sendLocationEvent(name: "hsmSetArm", value: hsmState, data: app.id)
		else parent.hsmSetState(hsmState, app.id)
		atomicState.lastHSMChange = hsmState
		jsonResponse([status: "complete"])		
	}
	else
	{
		log.error "Received HSM event from server: ${hsmState} does not exist!"
		jsonResponse([status: "error"])	
    }
}


/*
	hsmReceiveAlert
    
	Purpose: Receives HSM alert events from remote HSM instances.

	URL Format: (GET) /hsm/alert/:text

	Notes: Called from HTTP request from remote hub.
*/
def hsmReceiveAlert()
{
	if (!receiveHSM) return
    def hsmAlertText = params?.text ? URLDecoder.decode(params?.text) : ""
	
	parent.hsmSendAlert(hsmAlertText, appId)
}


/*
	subscribeLocalEvents
    
	Purpose: Subscribes to all device events for all attribute returned by getSupportedAttributes()

	Notes: 	Thank god this isn't SmartThings, or this would time out after about 10 subscriptions!

*/
def subscribeLocalEvents()
{
	unsubscribe()
	if (localConnectionType == "socket")
	{
		log.info "Skipping event subscriptions...  Using event socket to send events to server."
		return
	}
	log.info "Subscribing to events.."

	NATIVE_DEVICES.each
	{
	  groupname, device ->
		def selectedDevices = settings."${device.selector}"
		if (selectedDevices?.size()) getSupportedAttributes(groupname).each { subscribe(selectedDevices, it, realtimeEventHandler) }
	}

	// Special handling for Smart Plugs & Power Meters - Kinda Kludgy
	if (!sp_EnablePower && smartPlugs?.size()) unsubscribe(smartPlugs, "power", realtimeEventHandler)
	if (!sp_EnableVolts && smartPlugs?.size()) unsubscribe(smartPlugs, "voltage", realtimeEventHandler)
	if (!pm_EnableVolts && powerMeters?.size()) unsubscribe(powerMeters, "voltage", realtimeEventHandler)

	// Custom defined drivers
	state.customDrivers?.each
	{
	  groupname, driver ->
		if (settings."custom_${groupname}"?.size()) getSupportedAttributes(groupname).each { subscribe(settings."custom_${groupname}", it, realtimeEventHandler) }	
	}
}


/*
	realtimeEventHandler
    
	Purpose: Event handler for all local device events.

	URL Format: GET /device/:deviceId/sync/:type

	Notes: Send local events to the remote hub. (Filters out urlencoded Degree symbols foir ST)
*/
def realtimeEventHandler(evt)
{
	if (state.commDisabled) return

	def event =
	[
		name:			evt.name,
		value:			evt.value,
		unit:			remoteType != "smartthings" ? evt.unit : evt.unit?.replace("°", "")?.replace("%", ""),
		displayName:	evt.device.label ?: evt.device.name,
		data:			evt.data
	]

	def data = URLEncoder.encode(JsonOutput.toJson(event), "UTF-8")

	if (enableDebug) log.debug "Sending event to ${clientName}: ${evt.device.label ?: evt.device.name} [${evt.name}: ${evt.value} ${evt.unit}]"
	sendGetCommand("/device/${evt.deviceId}/event/${data}")
}


/*
	getAttributeMap
    
	Purpose: Returns a map of current attribute values for (device) with the device class (deviceClass).

	Notes: Calls getSupportedAttributes() to obtain list of attributes.
*/
def getAttributeMap(device, deviceClass)
{
	def deviceAttributes = getSupportedAttributes(deviceClass)
	def currentAttributes = []
	deviceAttributes.each
	{
		if (device.supportedAttributes.find{attr -> attr.toString() == it})  // Filter only attributes the device supports
			currentAttributes << [name: "${it}", value: device.currentValue("${it}"), unit: it == "temperature" ? "°"+getTemperatureScale() : it == "power" ? "W" :  it == "voltage" ? "V" : ""]
	}
	return currentAttributes
}


/*
	getCommandMap

	Purpose: Returns a map of support commands for (device)

	Note: This applies to the device as it exists on this hub.
		  Virtual devices may expose commands that the physical device does not support.
*/
def getCommandMap(device)
{
    return device.supportedCommands.collectEntries { command-> [ (command?.name): (command?.arguments) ] }
}


/*
	getSupportedAttributes
    
	Purpose: Returns a list of supported attribute values for the device class (deviceClass).

	Notes: Called from getAttributeMap().
*/
private getSupportedAttributes(deviceClass)
{
	if (NATIVE_DEVICES.find{it.key == deviceClass}) return NATIVE_DEVICES[deviceClass].attr
	if (state.customDrivers.find{it.key == deviceClass}) return state.customDrivers[deviceClass].attr
	return null
}


/*
	saveDevicesToClient
    
	Purpose: Sends all of the devices selected (& current attribute values) from this hub to the client (remote) hub.

	URL Format: POST /devices/save

	Notes: Makes a single POST request for each group of devices.
*/
def saveDevicesToClient()
{
	if (state.saveDevices == false) return

	// Fetch all devices and attributes for each device group and send them to the master.
	NATIVE_DEVICES.each
	{
	  groupname, device ->

		def devices = []
		settings."${device.selector}".each
		{
			log.debug getAttributeMap(it, groupname)
			devices << [id: it.id, label: it.label ?: it.name, attr: getAttributeMap(it, groupname)]
		}
		if (devices != [])
		{
			if (enableDebug) log.info "Sending devices to remote: ${groupname} - ${devices}"
			sendPostCommand("/devices/save", [deviceclass: groupname, devices: devices])
		}
	}

	// Custom defined device drivers
	state.customDrivers.each
	{
	  groupname, driver ->
		def customSel = settings?."custom_${groupname}"
		if (customSel != null)
		{
			if (enableDebug) log.info "Sending custom devices to remote..."
			sendPostCommand("/devices/save", [deviceclass: groupname, devices: customSel])
		}
	}
	state.saveDevices = false
}


/*
	httpGetWithReturn
    
	Purpose: Helper function to format GET requests with the proper oAuth token.

	Notes: 	Command is absolute and must begin with '/'
			Returns JSON Map if successful.
*/
def httpGetWithReturn(command)
{
	def requestParams =
	[
		uri:  state.clientURI + command,
		requestContentType: "application/json",
		headers:
		[
			Authorization: "Bearer ${state.clientToken}"
		]
	]
    
	httpGet(requestParams)
	{
	  response ->
		if (response?.status == 200)
		{
			return response.data
		}
		else
		{
			log.error "httpGet() request failed with error ${response?.status}"
		}
	}
}


/*
	sendGetCommand
    
	Purpose: Helper function to format GET requests with the proper oAuth token.

	Notes: 	Executes async http request and does not return data.
*/
def sendGetCommand(command)
{
	def requestParams =
	[
		uri:  state.clientURI + command,
		requestContentType: "application/json",
		headers:
		[
			Authorization: "Bearer ${state.clientToken}"
		]
	]
    
	asynchttpGet("asyncHTTPHandler", requestParams)
}


/*
	asyncHTTPHandler
    
	Purpose: Helper function to handle returned data from asyncHttpGet.

	Notes: 	Does not return data, only logs errors.
*/
def asyncHTTPHandler(response, data)
{
	if (response?.status != 200)
	{
		log.error "httpGet() request failed with error ${response?.status}"
	}
}


/*
	sendPostCommand
    
	Purpose: Helper function to format POST requests with the proper oAuth token.

	Notes: 	Returns JSON Map if successful.
*/
def sendPostCommand(command, data)
{
	def requestParams =
	[
		uri:  state.clientURI + command + "?access_token=" + state.clientToken,
		requestContentType: "application/json",
		body: data
	]

	httpPostJson(requestParams)
	{
	  response ->
		if (response?.status == 200)
		{
			return response.data
		}
		else
		{
			log.error "httpPost() request failed with error ${response?.status}"
		}
	}
}


/*
	getDevicePageStatus
    
	Purpose: Helper function to set flags for configured devices.
*/
def getDevicePageStatus()
{
	def status = [:]
	NATIVE_DEVICES.each
	{  groupname, device ->
		status["${device.prefGroup}"] = status["${device.prefGroup}"] != null ?: settings?."${device.selector}"?.size()
	}
	status["all"] = status.find{it.value == true} ? true : null
	status
}


/*
	mainPage
    
	Purpose: Displays the main (landing) page.

	Notes: 	Not very exciting.
*/
def mainPage()
{
	if (settings?.clientName && app.label == "")
	{
		app.updateLabel(clientName)
	}
	
	if (state.clientURI != null && state.installedVersion != appVersion) return upgradePage()
	
	dynamicPage(name: "mainPage", uninstall: true, install: true)
	{
		if (state.clientURI)
		{
			section("<h2>${app.label}</h2>"){}
		}
		section("-= <b>Main Menu</b> =-")
		{
			href "connectPage", title: "Connect to Client Hub...", description: "", state: state.clientURI ? "complete" : null
			if (state.clientURI)
			{
				href "devicePage", title: "Connect local devices to Client Hub...", description: "", state: devicePageStatus.all ? "complete" : null
			}
		}
		section("-= <b>Mode Menu</b> =-")
		{
			paragraph "Synchronize mode changes on the server to this remote (client) hub."
			input "pushModes", "bool", title: "Send mode changes to Client Hub?", defaultValue: false
			paragraph "Synchronize mode changes from this remote (client) hub to the Server hub.."
			input "receiveModes", "bool", title: "Receive mode changes from Client Hub?", description: "", defaultValue: false
		}
		section("-= <b>Hubitat Safety Monitor (HSM) Menu</b> =-")
		{
			paragraph "Synchronize HSM status on the server to this remote (client) hub."
			input "pushHSM", "bool", title: "Send HSM changes to Client Hub?", defaultValue: false
			paragraph "Synchronize HSM status from this remote (client) hub to the Server hub.."
			input "receiveHSM", "bool", title: "Receive HSM changes from Client Hub?", description: "", defaultValue: false
		}
		section("-= <b>Debug Menu</b> =-")
		{
			input "enableDebug", "bool", title: "Enable debug output?", required: false, defaultValue: false
		}
		section("-= <b>HubConnect v${appVersion.major}.${appVersion.minor}</b> =-")
		{
			paragraph "<span style=\"font-size:.8em\">Server Instance v${appVersion.major}.${appVersion.minor}.${appVersion.build} ${appCopyright}</span>"
		}
	}
}


/*
	upgradePage
    
	Purpose: Displays the splash page to force users to initialize the app after an upgrade.
*/
def upgradePage()
{
	dynamicPage(name: "upgradePage", uninstall: false, install: true)
	{
		section("New Version Detected!")
		{
			paragraph "<b style=\"color:green\">This HubConnect Server Instance has an upgrade that has been installed...</b> <br /> Please click [Done] to complete the installation."
		}
	}
}


/*
	connectPage
    
	Purpose: Displays the local & remote oAuth links.

	Notes: 	Really should create a proper token exchange someday.
*/
def connectPage()
{
	if (!state?.accessToken)
	{
		createAccessToken()
	}

	if (remoteType == "homebridge") app.updateSetting("localConnectionType", [type: "enum", value: "http"])
	def connectString = remoteType ? new groovy.json.JsonBuilder([uri: (remoteType == "local" || remoteType == "homebridge") ? getFullLocalApiServerUrl() : getFullApiServerUrl(), type: remoteType, token: state.accessToken, connectionType: localConnectionType ?: ""]).toString().bytes.encodeBase64() : ""

	dynamicPage(name: "connectPage", uninstall: false, install: false)
	{
		section("Client Details")
		{ 
			input "clientName", "string", title: "Friendly Name of Client Hub:", required: false, defaultValue: null, submitOnChange: true
			if (clientName) input "clientIP", "string", title: "Private LAN IP Address of Client Hub:", required: false, defaultValue: null, submitOnChange: true
			if (clientIP) input "remoteType", "enum", title: "Type of Remote Hub:", options: [local: "Hubitat (LAN)", remote: "Hubitat (Internet)", homebridge: "HomeBridge", smartthings: "SmartThings"], required: false, defaultValue: null, submitOnChange: true			
			if (remoteType == "local") input "localConnectionType", "enum", title: "Local connect type:", options: [http: "Hubitat oAuth (http)", socket: "Hubitat Event Socket"], required: false, defaultValue: null, submitOnChange: true	
		}
		if (remoteType)
		{
			section("Connection Key")
			{ 
				if (remoteType == "local" || remoteType == "homebridge") paragraph("Local LAN Hub: Copy &amp; Paste this Connection Key into the Remote hub's configuration: <input type=\"text\" size=\"100\" value=\"${connectString}\" />")
				else paragraph("Internet Hub: Copy &amp; Paste this Connection Key into the Remote hub's configuration: <input type=\"text\" size=\"100\" value=\"${connectString}\" />")
			}
		}
	}
}


/*
	connectRemoteHub
    
	Purpose: Receives URL and token from client (remote) hub.

	URL Format: GET /connect/:data

	Notes: 	This happens only after a successful installation of the server hubs connection key.
*/
def connectRemoteHub()
{
	if (params.data == null) return
	
	def accessData = parseJson(new String(params.data.decodeBase64()))
	if (!accessData || !accessData?.token || !accessData?.mac)
	{
		return jsonResponse([status: "error", message: "Invalid connect Key"])
	}

	if (state?.clientMac && (state.clientMac != accessData.mac))
	{
		return jsonResponse([status: "error", message: "Instance in use by another hub"])
	}
	
	log.info "Setting remote hub URI: ${accessData.uri} with token ${accessData.token}"
	state.clientURI = accessData.uri
	state.clientToken = accessData.token
	state.clientMac = accessData.mac
	
	jsonResponse([status: "success"])
}


/*
	registerPing
    
	Purpose: Handles a hub health ping event from a remote hub.

	Notes: 	If a hub was previously offline, the virtual hub device presence state will be set to "present".
*/
def registerPing()
{
	if (enableDebug) log.trace "Received ping from ${clientName}."
	state.lastCheckIn = now()

	if (state.connectStatus == "warning" || state.connectStatus == "offline")
	{
		state.connectStatus = "online"
		app.updateLabel(clientName + "<span style=\"color:green\"> Online</span>")
		log.info "${clientName} is online."

		sendEvent("hub-${clientIP}", [name: "presence", value: "present"])

		// A little recovery for system mode, in case the hub coming online missed a mode change
		if (pushModes)
		{	
			sendGetCommand("/modes/set/${URLEncoder.encode(location.mode)}")
		}
	}
	
	jsonResponse([status: "received"])	
}


/*
	appHealth
    
	Purpose: Scheduled health check task to ensure remote hubs are checking in regularly.

	Notes: 	Hubs are considered in a warning state after missing 2 pings (2 minutes).
			Hubs are considered offline after missing 5 pings (5 minutes).
			When a hub is offline, the virtual hub device presence state will be set to "not present".
*/
def appHealth()
{
	// There should always be an event at least every minute.  If it's been 5 minutes (4 missed pings, the hub may be offline.
	if (state.lastCheckIn < (now() - 300000)) // 5 minutes - offline
	{
		state.connectStatus = "offline"
		app.updateLabel(clientName + "<span style=\"color:red\"> Offline</span>")
		log.error "${clientName} is offline."

		sendEvent("hub-${clientIP}", [name: "presence", value: "not present"])
	}
	else if (state.lastCheckIn < (now() - 120000)) // 2 minutes - warning
	{
		state.connectStatus = "warning"
		app.updateLabel(clientName + "<span style=\"color:orange\"> Warning</span>")
		log.warn "${clientName} has missed a ping and may be offline."
	}
}


/*
	setCommStatus
    
	Purpose: Disables events communications between hubs.

	URL Format: /system/setCommStatus/true|false

	Notes: 	This is useful if the coordinator has to be rebooted to prevent HTTP errors on the remote hubs.
*/
def setCommStatus(commDisabled = false)
{
	log.info "Setting event communication status from remote hub: [status: ${commDisabled}]"
	state.commDisabled = commDisabled	
	response = httpGetWithReturn("/system/setCommStatus/${commDisabled}")
	sendEvent("hub-${clientIP}", [name: "switch", value: response.switch])
}


/*
	pushCurrentMode
    
	Purpose: Pushes the current mode out to remote hubs.

	URL Format: /modes/set/modeName

	Notes: Called by system start event to make sure the correct mode is pushed to all remote hubs.
*/
def pushCurrentMode()
{
	sendGetCommand("/modes/set/${URLEncoder.encode(location.mode)}")
}


/*
	getAllRemoteModes
    
	Purpose: Queries the remote hub for all configured modes.

	URL Format: GET /modes/get

	Notes: Called by parent app.
*/
def getAllRemoteModes()
{
	return httpGetWithReturn("/modes/get")
}


/*
	getAllModes
    
	Purpose: Returns a map of all configured modes on the server hub.

	URL Format: GET /modes/get

	Notes: Called from HTTP request on the remote hub.
*/
def getAllModes()
{
	jsonResponse(modes: location.modes, active: location.mode)
}


/*
	getAllHSMStates
    
	Purpose: Returns a map of all HSM states and current state on the server hub.

	URL Format: GET /modes/get

	Notes: Called from HTTP request on the remote hub.
*/
def getAllHSMStates()
{
	jsonResponse(hsmSetArm: ["armAway", "armHome", "armNight", "disarm", "armRules", "disarmRules", "disarmAll", "armAll", "cancelAlerts"], hsmStatus: location.hsmStatus)
}


/*
	getAllDevices
    
	Purpose: Queries the remote hub for all configured modes.

	URL Format: GET /devices/get

	Notes: Called by remote client.
*/
def getAllDevices()
{
	def response = []
	NATIVE_DEVICES.each
	{
	  groupname, device ->
		def devices = []

		settings."${device.selector}".each
		{
			devices << [id: it.id, label: it.label ?: it.name, attr: getAttributeMap(it, groupname), commands: getCommandMap(it)]
		}
		if (devices.size()) response << [deviceclass: groupname, devices: devices]
	}

	jsonResponse(response)
}


/*
	getAllVersions
    
	Purpose: Queries the remote hub for all app and driver versions.

	URL Format: GET /system/versions/get

	Notes: Called by parent app.
*/
def getAllVersions()
{
	return httpGetWithReturn("/system/versions/get")
}


/*
	saveCustomDrivers
    
	Purpose: Saves custom drivers defined in the parent app to this server instance.

	Notes: Called by parent app.
*/
def saveCustomDrivers(customDrivers)
{
	state.customDrivers = customDrivers
	sendPostCommand("/system/drivers/save", [customdrivers: customDrivers])
}


/*
	installed
    
	Purpose: Standard install function.

	Notes: Doesn't do much.
*/
def installed()
{
	log.info "${app.name} Installed"
	
	if (state.clientToken)
	{
		initialize()
	}
	else
	{
		// Set an error state
		state.lastCheckIn = 0
		state.connectStatus = "error"
	}

	state.installedVersion = appVersion
}


/*
	updated
    
	Purpose: Standard update function.

	Notes: Still doesn't do much.
*/
def updated()
{
	log.info "${app.name} Updated with settings: ${settings}"

	if (state?.customDrivers == null)
	{
		state.customDrivers = [:]
	}

	unsubscribe()
	initialize()

	sendGetCommand("/system/update")

	state.installedVersion = appVersion
}


/*
	initialize
    
	Purpose: Initialize the server instance.

	Notes: Parses the oAuth link into the token and base URL.  A real token exchange would obviate the need for this.
*/
def initialize()
{
	log.info "${app.name} Initialized"

	saveDevicesToClient()
	subscribeLocalEvents()
	if (pushModes) subscribe(location, "mode", realtimeModeChangeHandler)
	if (pushHSM)
	{
		subscribe(location, "hsmSetArm", realtimeHSMChangeHandler)
		subscribe(location, "hsmStatus", realtimeHSMStatusHandler)
	}
	
	state.lastCheckIn = now()
	state.connectStatus = "online"

	state.commDisabled = false
	
	// Build a lookup list
	state.deviceIdList = new HashSet<>()
	childDevices.each
	{
		def parts = it.deviceNetworkId.split(":")
		if (parts.size() > 1) state.deviceIdList << (localConnectionType != "socket" ? parts[1].toString() : parts[1].toInteger())
	}

	def hubDevice = getChildDevices()?.find{it.deviceNetworkId == "hub-${clientIP}"}
	if (hubDevice)
	{
		hubDevice.setConnectionType(remoteType == "local" && localConnectionType == "socket" ? "socket" : "http")
	}
	else if (state.clientToken)
	{
		hubDevice = createHubChildDevice()
		hubDevice?.setConnectionType(remoteType == "local" && localConnectionType == "socket" ? "socket" : "http")
	}

	app.updateLabel(clientName + "<span style=\"color:green\"> Online</span>")
	runEvery5Minutes("appHealth")
}


/*
	jsonResponse
    
	Purpose: Helper function to render JSON responses
*/
def jsonResponse(respMap)
{
	render contentType: 'application/json', data: JsonOutput.toJson(respMap)
}


/*
	devicePage
    
	Purpose: Displays the page where devices are selected to be linked to the controller hub.

	Notes: 	Really could stand to be better organized.
*/
def devicePage()
{
	def totalNativeDevices = 0
	def requiredDrivers = ""
	NATIVE_DEVICES.each
	{devicegroup, device ->
		if (settings."${device.selector}"?.size())
		{
			totalNativeDevices += settings."${device.selector}"?.size()
			requiredDrivers += "<li>HubConnect ${device.driver}</li>"
		}
	}

	def totalCustomDevices = 0
	state.customDrivers?.each
	{devicegroup, device ->
		///totalCustomDevices += settings."${device.selector}"?.size() ?: 0
		totalCustomDevices += settings."custom_${devicegroup}"?.size() ?: 0
	}
	
	def totalDevices = totalNativeDevices + totalCustomDevices

	dynamicPage(name: "devicePage", uninstall: false, install: false)
	{
		section("<b> Select Devices to Link to Remote Hub ${clientName}</b>  (${totalDevices} connected)")
		{ 
			href "dynamicDevicePage", title: "Sensors", description: "Contact, Motion, Multipurpose, Omnipurpose, Shock, GV Connector", state: devicePageStatus.sensors ? "complete" : null, params: [prefGroup: "sensors", title: "Sensors"]
			href "dynamicDevicePage", title: "Shackrat's Drivers", description: "Iris Smart Plug, Z-Wave Repeaters", state: devicePageStatus.shackrat ? "complete" : null, params: [prefGroup: "shackrat", title: "Shackrat's Drivers"]
			href "dynamicDevicePage", title: "Switches, Dimmers, & Fans", description: "Switch, Dimmer, Bulb, Power Meters", state: devicePageStatus.switches ? "complete" : null, params: [prefGroup: "switches", title: "Switches, Dimmers, & Fans"]
			href "dynamicDevicePage", title: "Safety & Security", description: "Locks, Keypads, Smoke & Carbon Monoxide, Leak, Sirens", state: devicePageStatus.safety ? "complete" : null, params: [prefGroup: "safety", title: "Safety & Security"]
			href "dynamicDevicePage", title: "Other Devices", description: "Presence, Button, Valves, Garage Doors, SpeechSynthesis, Window Shades", state: devicePageStatus.other ? "complete" : null, params: [prefGroup: "other", title: "Other Devices"]
			href "customDevicePage",  title: "Custom Devices", description: "Devices with user-defined drivers.", state: totalCustomDevices ? "complete" : null
		}
		if (requiredDrivers?.size())
		{
			section("<b>-= Required Drivers =-</b>")
			{
				paragraph "Please make sure the following native drivers are installed on the Remote hub before clicking \"Done\": <ul>${requiredDrivers}</ul>" 
			}
		}
	}
}


/*
	dynamicDevicePage
    
	Purpose: Displays a device selection page.
*/
def dynamicDevicePage(params)
{
	state.saveDevices = true

	dynamicPage(name: "dynamicDevicePage", title: params.title, uninstall: false, install: false)
	{
		NATIVE_DEVICES.each
		{
		  groupname, device ->
			if (device.prefGroup == params.prefGroup)
			{
				section("<b>-= Select ${device.driver}s (${settings?."${device.selector}"?.size() ?: "0"} connected) =-</b>")
				{
					def capability = device.capability.contains("device.") ? "" : "capability."
					input "${device.selector}", "${capability}${device.capability}", title: "${device.driver}s ${device.attr}:", required: false, multiple: true, defaultValue: null
				
					// Customizations
					if (groupname == "irissmartplug")
					{
						input "sp_EnablePower", "bool", title: "Enable power meter reporting?", required: false, defaultValue: true
						input "sp_EnableVolts", "bool", title: "Enable voltage reporting?", required: false, defaultValue: true
					}
					else if (groupname == "power")
					{
						input "pm_EnableVolts", "bool", title: "Enable voltage reporting?", required: false, defaultValue: true
					}
				}
			}
		}
	}
}


/*
	customDevicePage
    
	Purpose: Displays the page where custom (user-defined) devices are selected to be linked to the controller hub.

	Notes: 	First attempt at remotely defined device definitions.
*/
def customDevicePage()
{
	state.saveDevices = true

	dynamicPage(name: "customDevicePage", uninstall: false, install: false)
	{
		state.customDrivers.each
		{
		  groupname, driver ->
			def customSel = settings."custom_${groupname}"
			section("<b>-= Select ${driver.driver} Devices (${customSel?.size() ?: "0"} connected) =-</b>")
			{
				input "custom_${groupname}", "capability.${driver.selector.substring(driver.selector.lastIndexOf("_") + 1)}", title: "${driver.driver} Devices (${driver.attr}):", required: false, multiple: true, defaultValue: null
			}
		}
	}
}

def getAppVersion() {[platform: "Hubitat", major: 1, minor: 4, build: 6009]}
def getAppCopyright(){"&copy; 2019 Steve White, Retail Media Concepts LLC<br /><a href=\"https://github.com/shackrat/Hubitat-Private/blob/master/HubConnect/License%20Agreement.md\" target=\"_blank\">HubConnect License Agreement</a>"}
