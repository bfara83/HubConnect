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
metadata 
{
	definition(name: "HubConnect FanSpeed Controller", namespace: "shackrat", author: "Steve White", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/UniversalDrivers/HubConnect-FanSpeed-Controller.groovy")
	{
		capability "Fan Control"
		capability "Switch"
		capability "SwitchLevel"
		capability "Refresh"

		attribute "version", "string"

		command "cycleSpeed"
		command "sync"
	}
}


/*
	installed
    
	Doesn't do much other than call initialize().
*/
def installed()
{
	initialize()
}


/*
	updated
    
	Doesn't do much other than call initialize().
*/
def updated()
{
	initialize()
}


/*
	initialize
    
	Doesn't do much other than call refresh().
*/
def initialize()
{
	refresh()
}


/*
	parse
    
	In a virtual world this should never be called.
*/
def parse(String description)
{
	log.trace "Msg: Description is $description"
}


/*
	setSpeed
    
	Sets the fan speed to <value>.
*/
def setSpeed(value)
{
	// The server will update status
	log.debug "setSpeed: $value"
	state.lastSpeed = value
	parent.sendDeviceEvent(device.deviceNetworkId, "setSpeed", [value])
//	if (value == "off") 
//	{
//		parent.sendDeviceEvent(device.deviceNetworkId, "off")
//	} else {
//		parent.sendDeviceEvent(device.deviceNetworkId, "on")
//	}
}


/*
	setLevel
    
	Sets the fan speed to <level>.
*/
def setLevel(level)
{
	// The server will update status
	def ranges = ["off": 0..1, "low": 2..19, "medium-low": 20..39, "medium": 40..59, "medium-high": 60..79, "high": 80..100]
	ranges.each
	{
		k, v -> if (level >= v.from && level <= v.to)  state.lastSpeed = k
	}
	log.debug "setLevel: $level, $state.lastSpeed"
	parent.sendDeviceEvent(device.deviceNetworkId, "setLevel", [level])
//	if (state.lastSpeed == "off") 
//	{
//		parent.sendDeviceEvent(device.deviceNetworkId, "off")
//	} else {
//		parent.sendDeviceEvent(device.deviceNetworkId, "on")
//	}
}


/*
	cycleSpeed
    
	cycle through the fan speeds. 
*/
def cycleSpeed()
{
	if (state.lastSpeed == null) state.lastSpeed = "off"
	// current: next state map
	def speeds = ["low": "medium-low", "medium-low": "medium", "medium": "medium-high", "medium-high": "high", "high": "auto", "auto": "off", "off": "low"]
	state.lastSpeed = speeds.find{ it.key == state.lastSpeed }?.value
	setSpeed(state.lastSpeed)
}


/*
	on
    
	Turns the device on.
*/
def on()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "on")
	if (state.lastSpeed == "off") state.lastSpeed = "low"
	setSpeed(state.lastSpeed)
}


/*
	off
    
	Turns the device off.
*/
def off()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "off")
	state.lastSpeed = "off"
	log.debug "off: $state.lastSpeed"
}


/*
	refresh
    
	Refreshes the device by requesting an update from the client hub.
*/
def refresh()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "refresh")
}


/*
	sync
    
	Synchronizes the device details with the parent.
*/
def sync()
{
	// The server will respond with updated status and details
	parent.syncDevice(device.deviceNetworkId, "fancontrol")
	sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}
def getDriverVersion() {[platform: "Universal", major: 1, minor: 5, build: 0]}