/**
 * HubConnect Server for Hubitat
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
definition(
	name: "HubConnect Server for Hubitat",
	namespace: "shackrat",
	author: "Steve White",
	description: "Synchronizes devices and events across hubs..",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches@2x.png"
)

// Preference pages
preferences
{
	page(name: "mainPage")
	page(name: "aboutPage")
	page(name: "customDriverPage")
	page(name: "createCustomDriver")
	page(name: "editCustomDriver")
	page(name: "saveCustomDriverPage")
	page(name: "utilitesPage")
	page(name: "reportsPage")
	page(name: "modeReportPage")
	page(name: "versionReportLoadingPage")
	page(name: "versionReportPage")
	page(name: "hubUtilitiesPage")
	page(name: "diagnosticReportPage")
	page(name: "stubDriverPage")
	page(name: "supportPage")
	page(name: "uninstallPage")
	page(name: "restartPage")
	page(name: "resetCustomDriverPage")
  page(name: "deviceReportPage")
}


// Map containing driver and attribute definitions for each device class
@Field ATTRIBUTE_TO_SELECTOR =
[
	"alarm":					"alarm",
	"audioVolume":				"audioVolume",
	"battery":					"battery",
	"button":					"button",
	"bulb":						"bulb",
	"carbonMonoxide":			"carbonMonoxideDetector",
	"colorMode":				"colorMode",
	"colorTemperature":			"colorTemperature",
	"contact":					"contactSensor",
	"coolingSetpoint":			"thermostatCoolingSetpoint",
	"door":						"garageDoorControl",
	"doubleTapped":				"doubleTapableButton",
	"energy":					"energyMeter",
	"heatingSetpoint":			"thermostatHeatingSetpoint",
	"held":						"holdableButton",
	"humidity":					"relativeHumidityMeasurement",
	"illuminance":				"illuminanceMeasurement",
	"level":					"switchLevel",
	"lock":						"lock",
	"motion":					"motionSensor",
	"power":					"powerMeter",
	"powerSource":				"powerSource",
	"pushed":					"pushableButton",
	"presence":					"presenceSensor",
	"refresh":					"refresh",
	"securityKeypad":			"securityKeypad",
	"shock":					"shockSensor",
	"smoke":					"smokeDetector",
	"speechSynthesis":			"speechSynthesis",
	"speed":					"fanControl",
	"switch":					"switch",
	"temperature":				"temperatureMeasurement",
	"thermostat":				"thermostat",
	"thermostatFanMode":		"thermostatFanMode",
	"thermostatMode":			"thermostatMode",
	"thermostatOperatingState": "thermostatOperatingState",
	"thermostatSchedule":		"thermostatSchedule",
	"thermostatSetpoint":		"thermostatSetpoint",
	"threeAxis":				"threeAxis",
	"touch":					"touchSensor",
	"ultravioletIndex":			"ultravioletIndex",
	"valve":					"valve",
	"voltage":					"voltageMeasurement",
	"water":					"waterSensor",
	"windowshade":				"windowShade"
]


@Field ATTRIBUTE_TO_COMMAND =
[
	"door":			["open": [], "close": []],
	"level":		["on": [], "off": [], "setLevel": ["level"]],
	"lock":			["lock": [], "unlock": []],
	"lockCodes":	["setCodeLength": ["length"], "deleteCode": ["codeNumber"], "setCode": ["codeNumber", "code", "name = null"], "getCodes": []],
	"speed":		["setSpeed": ["fanSpeed"]],
	"switch":		["on": [], "off": []]
]


/*
	mainPage

	Purpose: Displays the main (landing) page.

	Notes: 	Not very exciting.
*/
def mainPage()
{
	if (state.serverInstalled && state.installedVersion != appVersion) return upgradePage()
	dynamicPage(name: "mainPage", title: app.label, uninstall: false, install: true)
	{
		if (state?.serverInstalled == null || state.serverInstalled == false)
		{
			section("<b style=\"color:green\">HubConnect Installed!</b>")
			{
				paragraph "Click <i>Done</i> to save then return to the HubConnect app to connect a remote hub."
			}
			return
		}

		section(menuHeader("Connected Hubs"))
		{
			app(name: "hubClients", appName: "HubConnect Server Instance", namespace: "shackrat", title: "Connect a Hub...", multiple: true)
		}
		section(menuHeader("Custom Drivers"))
		{
			href "customDriverPage", title: "Manage Custom Drivers", required: false
		}
		section(menuHeader("Communications"))
		{
			input "haltComms", "bool", title: "Suspend all communications between this server and all remote hubs?  (Resets at reboot)", required: false, defaultValue: false
		}
		section(menuHeader("Utilities &amp; Reports"))
		{
			href "utilitesPage", title: "HubConnect Utilites", required: false
			href "reportsPage", title: "HubConnect Reports", required: false
		}
		section(menuHeader("Support"))
		{
			href "supportPage", title: "Technical Support", description: "Get help with HubConnect, download code, or read the docs."
			href "aboutPage", title: "Help Support HubConnect!", description: "HubConnect is provided free of charge for the benefit the Hubitat community.  If you find HubConnect to be a valuable tool, please help support the project."
			paragraph "<span style=\"font-size:.8em\">Server Container v${appVersion.major}.${appVersion.minor}.${appVersion.build} ${appCopyright}</span>"
		}
	}
}


/*
	deviceReportPage

	Purpose: Shows status of all devices on this hub

	Notes: 	Not very exciting.
*/
def deviceReportPage()
{
	def hub = location.hubs[0]
    def serverIP = hub.getDataValue('localIP')

	String html = "<style>.in{background-color:red}.ot{background-color:green}.ns{background-color:#cccccc}</style><table style=\"width:100%;\"><tr><th>ID</th><th>Name</th>"
	def serverHubDevices = httpGetWithReturn("http://${serverIP}:8080/device/listJson?capability=capability.*")

	List serverInstanceData = []

	// Collect all devices from remotes
	childApps.each
	{
	  child ->
		serverInstanceData << [id: child.id, label: child.label, outgoingDevices: child.getSelectedDeviceIds(), incomingDevices: child.getChildDevices()?.collect{it.id}]
		html += "<th>${child.label}</th>"
	}
	html += "<tbody>"

	serverHubDevices?.each
	{
	  dev ->
		html += "<tr><td><a href=\"/device/edit/${dev.id}\">${dev.id}</a></td><td>${dev.name}</td>"

		// Compile outgoing device data
		serverInstanceData.each
		{
		  inst ->

			if (inst.outgoingDevices.find{it == dev.id.toString()}) html += "<td class=\"ot\"></td>"
			else if (inst.incomingDevices.find{it == dev.id.toString()}) html += "<td class=\"in\"></td>"
			else html += "<td class=\"ns\"></td>"
		}
		html += "</tr>"
	}

	dynamicPage(name: "deviceReportPage", title: "HubConnect Device Report", uninstall: false, install: true)
	{
		section(menuHeader("Device Report for: ${app.label}"))
		{
			paragraph "${html}</tbody></table>"
			paragraph "<table><tr><td>Legend: </td><td class=\"in\">incoming</td><td>&nbsp;</td><td class=\"ot\">outgoing</td></tr></table>"
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
		section(menuHeader("New Version Detected!"))
		{
			paragraph "<b style=\"color:green\">HubConnect Server has detected an upgrade that has been installed...</b> <br /> Please click [Done] to complete the installation on the server and all remotes."
		}
	}
}


/*
	customDriverPage

	Purpose: Displays the page where shackrat's custom device drivers (SmartPlug, Z-Wave Repeater) are selected to be linked to the controller hub.

	Notes: 	First attempt at organization.
*/
def customDriverPage()
{
	resetDriverForm()
	dynamicPage(name: "customDriverPage", uninstall: false, install: false, nextPage: "mainPage")
	{
		section(menuHeader("Currently Installed Custom Drivers"))
		{
			state.customDrivers.each
			{
			  groupname, driver ->
				href "editCustomDriver", title: "${driver.driver} (${groupname})", description: "${driver.attr}", params: [groupname: groupname]
			}

			href "createCustomDriver", title: "Add Driver", description: "Define a custom driver type."
		}
		section(menuHeader("Navigation"))
		{
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
	}
}


/*
	editCustomDriver

	Purpose: Loads the custom driver details into preferences so they can be edited in the UI.

	Notes: 	This requires a page refresh in order to work properly.  Hubitat cache issue??
*/
def editCustomDriver(params = [:])
{
	if (params?.groupname)
	{
		def dtMap = state?.customDrivers[params.groupname]
		if (dtMap)
		{
			app.updateSetting("newDev_AttributeGroup", [type: "string", value: params.groupname])
			app.updateSetting("newDev_DriverName", [type: "string", value: dtMap.driver])
			state.cdFields = 0
			dtMap.attr.eachWithIndex
			{
			  attr, idx ->
				app.updateSetting("attr_${idx+1}", [type: idx == 0 ? "enum" : "string", value: attr])
				state.cdFields++
			}
		}
		params.editgroup = params.groupname
		params.groupname = null
		dynamicPage(name: "editCustomDriver", uninstall: false, install: false, refreshInterval: 1)
		{
			section("Loading, please wait..."){}
		}
	}
	else driverPage("editCustomDriver", params?.editgroup)
}


/*
	createCustomDriver

	Purpose: Displays a blank custom driver form.
*/
def createCustomDriver()
{
	driverPage("createCustomDriver")
}


/*
	driverPage

	Purpose: Displays the page where shackrat's custom device drivers (SmartPlug, Z-Wave Repeater) are selected to be linked to the controller hub.

	Notes: 	First attempt at organization.
*/
def driverPage(pageName, groupName = null)
{
	dynamicPage(name: pageName, uninstall: false, install: false)
	{
		section(menuHeader("Configure Driver"))
		{
			if (groupName) paragraph "Attribute Class Name (letters & numbers only):<br />${groupName}"
			else input "newDev_AttributeGroup", "text", title: "Attribute Class Name (letters & numbers only):", required: true, defaultValue: null, submitOnChange: true
			if (groupName || newDev_AttributeGroup?.length() > 0) input "newDev_DriverName", "text", title: "Device Driver Name:", required: true, defaultValue: "HubConnect <replace with your name>", submitOnChange: true
		}
		if (settings.newDev_AttributeGroup?.size() > 0 && settings.newDev_DriverName?.size() > 0)
		{
			section(menuHeader("Supported Attributes"))
			{
				input "attr_1", "enum", title: "Attribute 1 (This will act as the primary capability for selecting devices):", required: true, multiple: false, options: ATTRIBUTE_TO_SELECTOR, defaultValue: null, submitOnChange: true
				if (attr_1?.size())
				{
					(state.cdFields-1).times
					{
						input "attr_"+(it+2), "string", title: "Attribute ${it+2}:", required: false, multiple: false, defaultValue: null
					}
				}
				input "addField", "button", title: "Add Attribute", submitOnChange: true
			}
			if (attr_1?.size())
			{
				section(menuHeader("Save Custom Device Type"))
				{
					href "saveCustomDriverPage", title: "Save", description: "Save this custom device type.",  params: [update: groupName ?: newDev_AttributeGroup]
					if (groupName != null) href "saveCustomDriverPage", title: "Delete", description: "Delete this custom device type.", params: [delete: groupName]
				}
				if (groupName != null)
				{
					section(menuHeader("HubConnect Driver Builder (BETA)"))
					{
						href "stubDriverPage", title: "Create Hubitat Driver", description: "Create a driver to use with this custom device.",  params: [groupname: groupName]
					}
				}
			}
		}
	}
}


/*
	saveCustomDriverPage

	Purpose: Displays the page where shackrat's custom device drivers (SmartPlug, Z-Wave Repeater) are selected to be linked to the controller hub.

	Notes: 	First attempt at organization.
*/
def saveCustomDriverPage(params)
{
	if (params?.delete)
	{
		state?.customDrivers.remove(params.delete)
		resetDriverForm()
		saveCustomDrivers()
		return customDriverPage()
	}

	def deviceClass = params?.update != null ? params?.update : newDev_AttributeGroup

	if (deviceClass.size() && newDev_DriverName?.size() && attr_1?.size())
	{
		def attr = []
		50.times
		{
			if (settings."attr_${it+1}"?.size())
			{
				attr << settings."attr_${it+1}"
			}
		}

		def selector = ATTRIBUTE_TO_SELECTOR.find{it.key == attr_1}
		def randString = Long.toUnsignedString(new Random().nextLong(), 16).toUpperCase()
		state.customDrivers[deviceClass] =
		[
			driver: newDev_DriverName,
			selector: state.customDrivers[deviceClass]?.selector ?: "${randString}_${selector.value}",
			attr: attr
		]
		saveCustomDrivers()
		resetDriverForm()
	}
	else return customDriverPage()

	log.debug "Saving custom driver map: ${state.customDrivers}"

	dynamicPage(name: "saveCustomDriverPage", uninstall: false, install: false)
	{
		section()
		{
			paragraph title: "Driver Saved!", "The driver definition has been saved."
			href "stubDriverPage", title: "Create Stub Driver", description: "Create a stub driver for this device...", params: [groupname: params.update]
			href "customDriverPage", title: "Custom Drivers", description: "Manage Custom Drivers"
		}
	}
}


/*
	saveCustomDrivers

	Purpose: Saves custom defined drivers to each child app instance.

	Notes: Calls like-named method in child app
*/
def saveCustomDrivers()
{
	childApps.each
	{
	  child ->
		child.saveCustomDrivers(state.customDrivers)
	}
}


/*
	resetDriverForm

	Purpose: Clears out all preferences so the UI page displays as if it were new.
*/
def resetDriverForm()
{
	app.updateSetting("newDev_AttributeGroup", [type: "string", value: ""])
	app.updateSetting("newDev_DriverName", [type: "string", value: ""])
	app.updateSetting("attr_1", [type: "enum", value: ""])
	50.times
	{
		app.removeSetting("attr_${it+2}")
	}

	// Driver builder
	app.updateSetting("hdbCapability_actuator", [type: "bool", value: false])
	app.updateSetting("hdbCapability_battery", [type: "bool", value: false])
	app.updateSetting("hdbCapability_refresh", [type: "bool", value: false])
	app.updateSetting("hdbCapability_lockCodes", [type: "bool", value: false])
	app.updateSetting("hdbAttribute_lastCodeName", [type: "bool", value: false])
	state.cdFields = 1
}


/*
	installed

	Purpose: Standard install function.

	Notes: Doesn't do much.
*/
def installed()
{
	log.info "${app.name} Installed"

	state.customDrivers = [:]
	state.serverInstalled = true
	state.installedVersion = appVersion

	initialize()
}


/*
	updated

	Purpose: Standard update function.

	Notes: Still doesn't do much.
*/
def updated()
{
	log.info "${app.name} Updated"

	if (state?.customDrivers == null)
	{
		state.customDrivers = [:]
	}

	if (state?.serverInstalled == null)
	{
		state.serverInstalled = true
	}

	unsubscribe()
	initialize()

	// Upgrade the system if necessary
	if (state.installedVersion != appVersion)
	{
		log.info "An upgrade has been detected...  Updating the HubConnect system..."
		childApps.each
		{
		  child ->
			log.info "Running updated() on ${child.label}..."
			child.updated()
		}
	}

	state.installedVersion = appVersion
}


/*
	uninstalled

	Purpose: Standard uninstall function.

	Notes: Tries to clean up just in case Hubitat misses something.
*/
def uninstalled()
{
	log.info "HubConnect server has been uninstalled."

	childApps.each
	{
	  child ->
		child.updateSetting("removeDevices", [type: "bool", value: true])
		child.uninstalled() // Just in case
		app.deleteChildApp(child.id)
	}
}


/*
	initialize

	Purpose: Initialize the server.

	Notes: Subscribes to the systemStart event to synchronize modes.
*/
def initialize()
{
	log.info "${app.name} Initialized"

	childApps.each
	{
	  child ->
		log.debug "Found server instance: ${child.label}"
	}

	subscribe(location, "systemStart", systemStartEventHandler)
}


/*
	systemStartEventHandler

	Purpose: Event handler for the systemStart event.

	Notes: May do some cleanup in the future, but for now just schedules a mode sync.
*/
def systemStartEventHandler(evt)
{
	app.updateSetting("haltComms",[type: "bool", value: false])
	childApps.each
	{
	  child ->
		child.setCommStatus(false)
		log.debug "System Start: Restoring communications to remote instance: ${child.label}..."
	}

	// Push out a mode sync in 20 seconds, after other automations have a chance to determine the correct mode
	runIn(20, "pushSystemMode")
}


/*
	pushSystemMode

	Purpose: Pushes the current system mode to all remote hubs.

	Notes: Calls pushCurrentMode() in each server instance.
*/
def pushSystemMode()
{
	log.debug "System Start: Synchronizing modes across all hubs..."
	childApps.each
	{
	  child ->
		child.pushCurrentMode()
	}
}


/*
	hsmSetState

	Purpose: Sets the HSM state for each child instance to <state>.

	Notes: Calls realtimeHSMChangeHandler with a "fake" event.
*/
def setHSMState(hsmState, appId)
{
	log.debug "Setting HSM state across all hubs to ${hsmState}..."
	childApps.each
	{
	  child ->
		if (child.id != appId) child.realtimeHSMChangeHandler([value: hsmState, data: 0])
	}
}


/*
	hsmSendAlert

	Purpose: Pushes an HSM alert to all remotes.

	Notes: Calls pushCurrentMode() in each server instance.
*/
def hsmSendAlert(hsmAlertText, appId)
{
	log.debug "Sending HSM alert state across all hubs to ${hsmState}..."
	childApps.each
	{
	  child ->
		if (child.id != appId) child.realtimeHSMChangeHandler([value: hsmState, data: 0])
	}
}


/*
	remoteHubControl

	Purpose: Helper function to power off/reboot the remote hub.

	Notes: Supported remote commands: reboot, shutdown
*/
def remoteHubControl(child, command)
{
	def port = child.remoteType == "homebridge" ? ":20009" : ""
	def requestParams =
	[
		uri:  "http://${child.clientIP}${port}/hub/${command}",
		requestContentType: "text/html",
		body: []
	]

	httpPost(requestParams)
	{
	  response ->
		if (response?.status != 200)
		{
			log.error "httpPost() request failed with error ${response?.status}"
		}
	}
}


/*
	utilitiesPage

	Purpose: Displays a menu of utilities.
*/
def utilitesPage(params)
{
	if (params?.debug != null)
	{
		childApps.each
		{
		  child ->
			child.updateSetting("enableDebug", [type: "bool", value: params.debug])
			log.debug "${params.debug ? "Enabling" : "Disabling"} debug logging on ${child.label}"
		}
	}
	dynamicPage(name: "utilitesPage", title: "HubConnect Utilites", uninstall: false, install: false)
	{
		section(menuHeader("Utilites"))
		{
			href "hubUtilitiesPage", title: "Remote Hub Utilities", description: "Shutdown/reboot hubs on the same LAN..."
			href "resetCustomDriverPage", title: "Reset Custom Drivers", description: "Reset and re-initialize the custom defined device driver system..."
			href "restartPage", title: "Restart HubConnect", description: "Restart all HubConnect drivers and services..."
		}
	   	section(menuHeader("Master Debug Control"))
	   	{
	   		if (params?.debug != null) paragraph "Debug has been ${params.debug ? "enabled" : "disabled"} for all hubs."
	   		href "utilitesPage", title: "Enable debug logging for all instances?", description: "Click to enable", params: [debug: true]
	   		href "utilitesPage", title: "Disable debug logging for all instances?", description: "Click to disable",  params: [debug: false]
	   	}
	   	section(menuHeader("Uninstall HubConnect"))
	   	{
			href "uninstallPage", title: "Uninstall HubConnect from this hub.", description: "", state: null
		}
		section()
		{
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
		state.utilityStatus = ""
		params = [:]
	}
}


/*
	resetCustomDriverPage

	Purpose: Confirms reset of custom drivers.
*/
def resetCustomDriverPage()
{
	dynamicPage(name: "resetCustomDriverPage", title: "Reset Custom Drivers", uninstall: false, install: false)
	{
		if (state.utilityStatus != "") section(menuHeader("Reset Status")) { paragraph "${state.utilityStatus}" }
		else
		{
			section(menuHeader("Reset Custom Drivers"))
			{
				paragraph "This will DELETE all custom driver definitiopns from all hubs.\nIt will NOT remove any virtual devices that have been shared."
				input "resetCustomDrivers", "button", title: "Reset Custom Drivers", submitOnChange: false
			}
		}
		section()
		{
			href "utilitesPage", title: "Utilities", description: "Return to Utilities menu..."
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
		state.utilityStatus = ""
	}
}


/*
	reportsPage

	Purpose: Displays a menu of reports.
*/
def reportsPage(params)
{
	atomicState.versionReportStatus = null
	dynamicPage(name: "reportsPage", title: "HubConnect Reports", uninstall: false, install: false)
	{
		section(menuHeader("Reports"))
		{
      href "deviceReportPage", title: "Device Usage Report", description: "A grid of all devices on this hub and their usage in HubConnect...", required: false
			href "modeReportPage", title: "System Mode Report", description: "Lists all modes configured on each remote hub..."
			href "versionReportLoadingPage", title: "App & Driver Version Report", description: "Displays all app and driver versions configured on each hub...  (May be slow to load)"
			href "diagnosticReportPage", title: "Technical Support Report", description: "Export HubConnect configuration data for technical support."
		}
		section()
		{
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
	}
}


/*
	modeReportPage

	Purpose: Displays a report of configured system modes.
*/
def modeReportPage()
{
	// Query all children for modes
	def allModes = [:]

	allModes[0] = [label: "Server Hub", modes: location.modes.collect{it?.name}.sort().join(", "), active: location.mode, pushModes: "-", receiveModes: "-"]
	childApps.each
	{
	  child ->
		def remoteModes = [:]
		if (child.getAppHealthStatus() == "online")
		{
			remoteModes = child.getAllRemoteModes()
			allModes[child.id] = [label: child.label, modes: remoteModes?.status == "error" ? ["<span style=\"color:red\">error</span>"] : remoteModes?.modes.collect{it?.name}.sort().join(", "), active: remoteModes?.active, pushModes: child.pushModes, receiveModes: child.receiveModes]
		}
		else allModes[child.id] = [label: child.label, modes: "", active: false, pushModes: child.pushModes, receiveModes: child.receiveModes]
	}

	dynamicPage(name: "modeReportPage", title: "System Mode Report", uninstall: false, install: false)
	{
		section(menuHeader("Modes"))
		{
			def html = "<table style=\"width:100%\"><thead><tr><th>Hub</th><th>Active</th><th>TX</th><th>RX</th><th>Configured Modes</th></tr></thead><tbody>"
			allModes.each
			{
			  k, v->
				html += "<tr><td>${v?.label}</td><td>${v?.active}</td><td>${v.pushModes}</td><td>${v.receiveModes}</td><td>${v?.modes}</td></tr>"
			}
			paragraph "${html}</tbody></table>"
			paragraph "TX = Send Mode Change to Remote"
			paragraph "RX = Receive Mode Change from Remote"
		}
		section(menuHeader("Navigation"))
		{
			href "reportsPage", title: "Reports", description: "Return to Reports menu..."
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
	}
}


/*
	getVersionReportData

	Purpose: Queries all hubs for current app & driver version data.
*/
def getVersionReportData()
{
	// Get the latest available versions
	versionCheck()

	// Server hub apps & drivers
	def serverDrivers = [:]
	childApps.each
	{
  	  child ->
		child.getChildDevices()?.each
		{
		  device ->
			if (serverDrivers[device.typeName] == null) serverDrivers[device.typeName] = device.getDriverVersion()
		}
	}

	def allHubs = [:]
	allHubs[0] = [name: "Server Hub", report: [apps: [[appName: app.label, appVersion: appVersion], [appName: childApps?.first()?.name, appVersion: childApps?.first()?.getAppVersion()]], drivers: serverDrivers]]

	// Remote hubs apps & drivers
	childApps.each
	{
	  child ->
		if (child.getAppHealthStatus() == "online")
		{
			atomicState.versionReportStatus = "Gathering report data for ${child.label}..."

			Object report = (Object) child.getAllVersions()
			if (report.apps != "" || report.drivers != "")
			{
				allHubs[child.id] = [name: child.label, report: report]
			}
		}
		else allHubs[child.id] = [name: child.label, report: [drivers:[], apps: []]]
	}
	state.versionReport = allHubs
	atomicState.versionReportStatus = "Checking for latest versions..."

	// This is to allow another 5 seconds for the version check to complete...  It should never be needed!
	def lp = 0
	while (atomicState.currentVersions == null)
	{
		pauseExecution(1000)
		if (++lp > 5) break
	}

	atomicState.versionReportStatus = "Rendering..."
}


/*
	versionReportLoadingPage

	Purpose: Displays loading page while data is pulled from hubs.
*/
def versionReportLoadingPage()
{
	if (atomicState.versionReportStatus == null)
	{
		atomicState.versionReportStatus = "Gathering report data for for Server Hub..."
		runIn(1, "getVersionReportData")
	}
	else if (atomicState.versionReportStatus == "Rendering...") return versionReportPage()

	dynamicPage(name: "versionReportLoadingPage", title: "Loading system version report...", uninstall: false, install: false, refreshInterval: 1)
	{
		section()
		{
			paragraph "${atomicState.versionReportStatus}"
		}
	}
}


/*
	versionReportPage

	Purpose: Displays a report of app & driver versions.
*/
def versionReportPage()
{
	atomicState.versionReportStatus = null
	dynamicPage(name: "versionReportPage", title: "System Version Report", uninstall: false, install: false)
	{
		state.versionReport.each
		{
		  k, v ->
			section(menuHeader("${v?.name}"))
			{
				if (v?.report == null)
				{
					paragraph "Hub is not reachable or remote client is not reporting version information."
					return
				}
				def html = "<table style=\"width:100%\"><thead><tr><th style=\"width:46%\">Component</th><th style=\"width:10%\">Type</th><th style=\"width:16%\">Platform</th><th style=\"width:14%\">Installed</th><th style=\"width:14%\">Latest</th></tr></thead><tbody>"
				v?.report?.apps?.sort()?.each
				{
					def latest = (it?.appVersion?.platform != null && state?.currentVersions != null) ? atomicState.currentVersions?.(it.appVersion.platform.toLowerCase())?.app?.(it.appName) : null
					def latestVersion = latest ?  "<span style=\"color:${isNewer(latest, it?.appVersion) ? "red" : "green"}\">${latest.major}.${latest.minor}.${latest.build}</span>" : ""
					def currentVersion = latest ?  "<span style=\"color:${isNewer(latest, it?.appVersion) ? "red" : "black"}\">${it?.appVersion?.major}.${it?.appVersion?.minor}.${it?.appVersion?.build}</span>" : ""
					html += "<tr><td>${it?.appName}</td><td>app</td><td>${it?.appVersion?.platform}</td><td>${currentVersion}</td><td>${latestVersion}</td></tr>"
				}
				v?.report?.drivers?.sort()?.each
				{
				  dk, dv ->
					def latest = (dv?.platform != null && state?.currentVersions != null) ? atomicState?.currentVersions?.(dv?.platform?.toLowerCase())?.driver?.(dk?.toString()) : null
					def latestVersion = latest ?  "<span style=\"color:${isNewer(latest, dv) ? "red" : "green"}\">${latest.major}.${latest?.minor}.${latest?.build}</span>" : ""
					def currentVersion = latest ?  "<span style=\"color:${isNewer(latest, dv) ? "red" : "black"}\">${dv?.major}.${dv?.minor}.${dv?.build}</span>" : ""
					html += "<tr><td>${dk}</td><td>driver</td><td>${dv?.platform}</td><td>${currentVersion}</td><td>${latestVersion}</td></tr>"
				}
				paragraph "${html}</tbody></table>"
			}
		}
		section()
		{
			paragraph "Note: The version report can only report on drivers that are currently in use by HubConnect devices."
			href "versionReportLoadingPage", title: "Refresh", description: "Refresh this report with the latest data...  (May be slow to load)"
		}
		section()
		{
			href "reportsPage", title: "Utilities", description: "Return to Reports menu..."
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
	}
}


/*
	restartPage

	Purpose: Restarts HubConnect services.
*/
def restartPage()
{
	dynamicPage(name: "restartPage", title: "Restart HubConnect Services", uninstall: false, install: false)
	{
		if (state.utilityStatus != "") section(menuHeader("Restart Status")) { paragraph "${state.utilityStatus}" }
		else
		{
			section(menuHeader("Restart All Services"))
			{
				input "restartAllServices", "button", title: "Restart All", submitOnChange: false
			}
			section(menuHeader("Restart Services on Select Hubs"))
			{
				childApps.each
				{
			  	  child ->
					if (child.getState()?.connectStatus == "offline")
					{
						paragraph "${child.label} is offline and cannot be controlled."
					}
					else if (child.remoteType != "homebridge")
					{
						input "restart_${child.id}", "button", title: "${child.label}", submitOnChange: false, params: [t: now()]
					}
					else paragraph "${child.label} cannot be remotely restarted."
				}
			}
		}
		section(menuHeader("Navigation"))
		{
			href "utilitesPage", title: "Utilities", description: "Return to Utilities menu..."
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
		state.utilityStatus = ""
	}
}





/*
	hubUtilitiesPage

	Purpose: Reboots a remote hub that is on the same LAN.
*/
def hubUtilitiesPage()
{
	// Rebooting or shutting down a hub?
	childApps.each
	{
	  child ->
		if (settings."reboot_${child.id}" == true)
		{
			log.warn "Sending the reboot command to ${child.label}."
			app.updateSetting("reboot_${child.id}",[type: "bool", value: false])
			remoteHubControl(child, "reboot")
		}
		else if (settings."shutdown_${child.id}" == true)
		{
			log.warn "Sending the shutdown command to ${child.label}."
			app.updateSetting("shutdown_${child.id}",[type: "bool", value: false])
			remoteHubControl(child, "shutdown")
		}
	}

	dynamicPage(name: "hubUtilitiesPage", title: "Remote Hub Utilities", uninstall: false, install: false)
	{
		section()
		{
			paragraph "<b>All commands take effect immediately!</b>"
		}

		childApps.each
		{
		  child ->
			section(menuHeader("${child.label}"))
			{
				if (child.remoteType == "local" || child.remoteType == "homebridge")
				{
					input "reboot_${child.id}", "bool", title: "Reboot this hub?", required: false, defaultValue: false, submitOnChange: true
					if (child.remoteType == "local") input "shutdown_${child.id}", "bool", title: "Shutdown this hub?", required: false, defaultValue: false, submitOnChange: true
				}
				else paragraph "This is a remote hub and cannot be controlled."
			}
		}
		section(menuHeader("Navigation"))
		{
			href "utilitesPage", title: "Utilities", description: "Return to Utilities menu..."
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
	}
}


/*
	diagnosticReportPage

	Purpose: Exports diagnostic data for support.
*/
def diagnosticReportPage()
{
	String hcTopology = (String) """
<textarea rows="20" style="width:100%; font-family:Consolas,Monaco,Lucida Console,Liberation Mono,DejaVu Sans Mono,Bitstream Vera Sans Mono,Courier New, monospace;" onclick="this.select()" onclick="this.focus()">
HubConnect Technical System Report
Created on ${new Date(now()).format("MM-dd-yyyy HH:mm Z", location.timeZone)}

${"=".multiply(32)} HubConnect Server ${"=".multiply(33)}
appId: ${app.id}
appVersion: ${appVersion.toString()}
installedVersion: ${state?.installedVersion}
remoteClients: ${childApps?.size()}

---------- Hub ----------
hardwareID: ${location?.hubs[0]?.data?.hardwareID}
firmwareVersion: ${location?.hubs[0]?.firmwareVersionString}
localIP: ${location?.hubs[0]?.data?.localIP}

-------- Topology -------

HubConnect Server
"""
	String tsReport = (String) """

--------- Modes ---------
activeMode: ${location.mode}
definedModes: ${location.modes}

---------- HSM ----------
activeState: ${location.hsmStatus ?: "Not Installed"}
definedStates: [armAway, armHome, armNight, disarm, armRules, disarmRules, disarmAll, armAll, cancelAlerts]

${"=".multiply(80)}

"""
	childApps.each
	{
	  child ->

		def hubName = child.label.replaceAll("<(.|\n)*?>|(Online|Offline|Warning)", '')
		def modeReport = child.getAllRemoteModes()
		def hsmReport = child.getAllRemoteHSMStates()
		def remoteHub = child.getHubDevice()
		def remoteTSR = child.getRemoteTSReport()

		hcTopology += "\t|\n\t+---${hubName}(${child?.localConnectionType ?: "http"})\n"
		tsReport +=
"""
${"=".multiply(35-(hubName.length()/2))} ${hubName} (Instance) ${"=".multiply(35-(hubName.length()/2))}
appId: ${child.id}
appVersion: ${child.getAppVersion()?.toString()}
installedVersion: ${child.getState()?.installedVersion}

-------- Prefs ----------
pushModes: ${child?.pushModes}
receiveModes: ${child?.receiveModes}
pushHSM: ${child?.pushHSM}
receiveHSM: ${child?.receiveHSM}
enableDebug: ${child?.enableDebug}

-------- Status ---------
appHealth: ${child.getState()?.connectStatus}
commDisabled: ${child.getState()?.commDisabled}

-------- Comms ----------
remoteType: ${child?.remoteType}
localConnectionType: ${child?.localConnectionType}
clientURI: ${child.getState()?.clientURI}
connectionKey: ${child.getConnectString()}

-------- Inbound Devices --------
incomingDevices: ${child.getChildDevices()?.size()-1}
deviceIdList: ${child.getState()?.deviceIdList}

------ Virtual Hub ------
-Attributes-
deviceStatus: ${remoteHub == null ? "Not Installed" : "Installed"}
connectionType: ${remoteHub?.currentValue("connectionType")}
eventSocketStatus: ${remoteHub?.currentValue("eventSocketStatus")}
hsmStatus: ${remoteHub?.currentValue("hsmStatus")}
modeStatus: ${remoteHub?.currentValue("modeStatus")}
presence: ${remoteHub?.currentValue("presence")}
switch: ${remoteHub?.currentValue("switch")}
version: ${remoteHub?.currentValue("version")}

-State-
subscribedDevices: ${remoteHub?.getState()?.subscribedDevices}
connectionAttempts: ${remoteHub?.getState()?.connectionAttempts}

-Preferences-
refreshSocket: ${remoteHub?.getPref("refreshSocket")}
refreshHour: ${remoteHub?.getPref("refreshHour")}
refreshMinute: ${remoteHub?.getPref("refreshMinute")}

---- Custom Drivers -----
customDrivers: ${remoteHub?.getState()?.customDrivers?.toString()}

${"=".multiply(80)}


${"=".multiply(35-(hubName.length()/2))} ${hubName} (Remote) ${"=".multiply(35-(hubName.length()/2))}
appId: ${remoteTSR?.app?.appId}
appVersion: ${remoteTSR?.app?.appVersion}
installedVersion: ${remoteTSR?.app?.installedVersion}

---------- Hub ----------
hardwareID: ${remoteTSR?.hub?.hardwareID}
firmwareVersion: ${remoteTSR?.hub?.firmwareVersion}
localIP: ${remoteTSR?.hub?.localIP}

-------- Prefs ----------
pushModes: ${remoteTSR?.prefs?.pushModes}
pushHSM: ${remoteTSR?.prefs?.pushHSM}
enableDebug: ${remoteTSR?.prefs?.enableDebug}

-------- Status ---------
commDisabled: ${remoteTSR?.state?.commDisabled}

-------- Comms ----------
connectionType: ${remoteTSR?.state?.connectionType}
clientURI: ${remoteTSR?.state?.clientURI}
serverKey: ${remoteTSR?.prefs?.serverKey}

-------- Incoming Devices --------
incomingDevices: ${remoteTSR?.devices?.incomingDevices}
deviceIdList: ${remoteTSR?.devices?.deviceIdList}

--------- Modes ---------
activeMode: ${modeReport?.active}
definedModes: ${modeReport?.modes?.name?.toString()}

---------- HSM ----------
activeState: ${hsmReport?.hsmStatus ?: "Not Installed"}
definedStates: ${hsmReport?.hsmSetArm}

------ Virtual Hub ------
Attributes:
deviceStatus: ${remoteTSR?.hub?.deviceStatus}
connectionType: ${remoteTSR?.hub?.connectionType}
eventSocketStatus: ${remoteTSR?.hub?.eventSocketStatus}
hsmStatus: ${remoteTSR?.hub?.hsmStatus}
modeStatus: ${remoteTSR?.hub?.modeStatus}
presence: ${remoteTSR?.hub?.presence}
switch: ${remoteTSR?.hub?.switch}
version: ${remoteTSR?.hub?.version}

State:
subscribedDevices: ${remoteTSR?.hub?.subscribedDevices}
connectionAttempts: ${remoteTSR?.hub?.connectionAttempts}

Preferences:
refreshSocket: ${remoteTSR?.hub?.refreshSocket}
refreshHour: ${remoteTSR?.hub?.refreshHour}
refreshMinute: ${remoteTSR?.hub?.refreshMinute}

---- Custom Drivers -----
customDrivers: ${remoteTSR?.state?.customDrivers}

${"=".multiply(80)}
"""
	}
	tsReport += "</textarea>"

	dynamicPage(name: "diagnosticReportPage", title: "Technical Support Report", uninstall: false, install: false)
	{
		section(menuHeader("Your HubConnect Report..."))
		{
			paragraph "This report contains specific technical information about your HubConnect installation.\nPlease share via e-mail or private message (PM) only. Do not post this report to a public forum!"
			paragraph hcTopology + tsReport
		}
		section(menuHeader("Navigation"))
		{
			href "reportsPage", title: "Utilities", description: "Return to Reports menu..."
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
	}
}


/*
	stubDriverPage

	Purpose: Displays a stub driver for a custom driver.
*/
def stubDriverPage(params)
{
	Map dtMap = state?.customDrivers[(params?.groupname ?: state.dbGroup)]
	String attributes = (String) ""
	if (dtMap != null)
	{
		{
		  attr, idx ->
			if (idx && attr != null) attributes += "\t\tattribute \"${attr.toString()}\", \"string\"\n"
		}
		state.dbGroup = params?.groupname
	}

	def commands = ""
	ATTRIBUTE_TO_COMMAND.find{attr -> attr.key == dtMap.attr[0]}.each
	{
	   command ->
		command.value.each{cmd ->
			commands += (String) "\n/*\n\t${cmd.key.toString().replaceAll("\\(.*?\\)","")}\n*/\ndef ${cmd.key}(${cmd.value.join(', ')})\n{\n\t// The server will update status\n\tparent.sendDeviceEvent(device.deviceNetworkId, \"${cmd.key.toString()}\"" + ((cmd.value.size() > 0) ? ", ${cmd.value.toString().replaceAll(" = null", "")}" : "") + ")\n}\n\n"
		}
	}
	def extraCapabilities = "" +
		(hdbCapability_actuator ? "\t\tcapability \"Actuator\"\n" : "") +
		(hdbCapability_battery ? "\t\tcapability \"Battery\"\n" : "") +
		(hdbCapability_refresh ? "\t\tcapability \"Refresh\"\n" : "") +
		(hdbCapability_lockCodes ? "\t\tcapability \"Lock Codes\"\n" : "")

	def extraAttributes =  "" +
		(hdbAttribute_lastCodeName ? "\t\tattribute \"lastCodeName\", \"string\"\n" : "")

	dynamicPage(name: "stubDriverPage", title: "HubConnect Driver Builder (BETA)", uninstall: false, install: false, nextPage: "customDriverPage")
	{
		if (params?.groupname == null || dtMap == null)
		{
			section()
			{
				paragraph "Error: The custom driver could not be located!"
			}
		}
		else
		{
			section(menuHeader("Driver Code"))
			{
				paragraph "Create a new driver on each hub where this device will be used, paste the code into the editor, then click save..."
				paragraph "" +
"""
<textarea rows="20" wrap="off" style="width:100%; font-family:Consolas,Monaco,Lucida Console,Liberation Mono,DejaVu Sans Mono,Bitstream Vera Sans Mono,Courier New, monospace;" onclick="this.select()" onclick="this.focus()">
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
	definition(name: "${dtMap.driver}", namespace: "shackrat", author: "Steve White")
	{
		capability "${ATTRIBUTE_TO_SELECTOR.find{it.key == dtMap.attr[0]}.value.capitalize()}"
		capability "Refresh"
${extraCapabilities}

		// Autogenerated attributes ${attributes}
		attribute "version", "string"
${extraAttributes}

		command "sync"
	}
}


/*
	installed
*/
def installed()
{
	initialize()
}


/*
	updated
*/
def updated()
{
	initialize()
}


/*
	initialize
*/
def initialize()
{
	refresh()
}


/*
	refresh
*/
def refresh()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "refresh")
}

${commands}
/*
	sync
*/
def sync()
{
	// The server will respond with updated status and details
	parent.syncDevice(device.deviceNetworkId, "omnipurpose")
	sendEvent([name: "version", value: "v\${driverVersion.major}.\${driverVersion.minor}.\${driverVersion.build}"])
}
def getDriverVersion() {[platform: "Hubitat", major: 1, minor: 0, build: 0]}
</textarea>
"""
				paragraph "Disclaimer: This driver builder is considered to be a beta-level feature.\nThe code generated may not work in all instances."
				paragraph "Please help improve this tool by <a href=\"https://community.hubitat.com/t/release-hubconnect-share-devices-across-multiple-hubs-even-smartthings/12028/1601\" target=\"_blank\">reporting</a> any issues with the code generated."
			}
			section(menuHeader("Customize Driver Code"))
			{
				input "hdbCapability_actuator", "bool", title: "Add Actuator capability?", required: false, defaultValue: false, submitOnChange: true
				input "hdbCapability_battery", "bool", title: "Add Battery capability?", required: false, defaultValue: false, submitOnChange: true
				input "hdbCapability_refresh", "bool", title: "Add Refresh capability?", required: false, defaultValue: false, submitOnChange: true
				if (dtMap?.attr[0] == "lock") input "hdbCapability_lockCodes", "bool", title: "Add Lock Codes capability?", required: false, defaultValue: false, submitOnChange: true
				if (dtMap?.attr[0] == "lock") input "hdbAttribute_lastCodeName", "bool", title: "Add lastCodeName attribute?", required: false, defaultValue: false, submitOnChange: true
			}
			section(menuHeader("Navigation"))
			{
				href "customDriverPage", title: "Custom Drivers", description: "Manage Custom Drivers"
				href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
			}
		}
	}
}


/*
	supportPage

	Purpose: Displays the technical support page.
*/
def supportPage()
{
	dynamicPage(name: "supportPage", title: "HubConnect v${appVersion.major}.${appVersion.minor} Technical Support", uninstall: false, install: false)
	{
		section(menuHeader("Get Help"))
		{
			href "hubitat", style:"external", title: "Get help with HubConnect!", description: "Post a question in the discussion thread at the Hubitat community. (hubitat.com)", url: "https://community.hubitat.com/t/release-hubconnect-share-devices-across-multiple-hubs-even-smartthings/"
		}
		section(menuHeader("Documentation"))
		{
			href "install", style:"external", title: "HubConnect Installation Instructions", description: "Read the installation instructions (github.com).", url: "https://github.com/HubitatCommunity/HubConnect/blob/master/HubConnect%20Installation%20Instructions.pdf"
			href "upgrade", style:"external", title: "HubConnect v1.5 Upgrade Instructions", description: "Read the upgrade instructions (github.com).", url: "https://github.com/HubitatCommunity/HubConnect/blob/master/HubConnect%20v1.5%20Upgrade%20Instructions.pdf"
		}
		section(menuHeader("How-to Videos by @csteele"))
		{
			href "client", style:"external", title: "HubConnect Server Installation", description: "Watch the step-by-step installation of the HubConnect server app (hubitatcommunity.com).", url: "http://hubconnect.hubitatcommunity.com/HubConnect_Install_Videos/HubConnect_Server_Install/index.html"
			href "server", style:"external", title: "HubConnect Remote Client Installation", description: "Watch the step-by-step installation of the HubConnect server app (hubitatcommunity.com).", url: "http://hubconnect.hubitatcommunity.com/HubConnect_Install_Videos/HubConnect_Remote_Install/index.html"
		}
		section(menuHeader("Technical Stuff"))
		{
			href "attributes", style:"external", title: "HubConnect Supported Attributes", description: "A list of supported attributes by device type (github.com).", url: "https://github.com/HubitatCommunity/HubConnect/blob/master/HubConnect%20Attributes.pdf"
			href "importurls", style:"external", title: "HubConnect Code URLs", description: "A list of HubConnect source code URL's (hubitatcommunity.com).", url: "http://hubconnect.hubitatcommunity.com/import.php"
		}
		section()
		{
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
		}
	}
}


/*
	aboutPage

	Purpose: Displays the about page with credits.
*/
def aboutPage()
{
	dynamicPage(name: "aboutPage", title: "HubConnect v${appVersion.major}.${appVersion.minor}", uninstall: false, install: false)
	{
		section()
		{
			paragraph "HubConnect is provided free for personal and non-commercial use.  Countless hours went into the development and testing of this project.  If you like it and would like to see it succeed, or see more apps like this in the future, please consider making a small donation to the cause."
			href "donate", style:"embedded", title: "Please consider making a \$20 or \$40 donation to show your support!", image: "http://irisusers.com/hubitat/hubconnect/donate-icon.png", url: "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=T63P46UYH2TJC&source=url"
		}
		section()
		{
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
			paragraph "<span style=\"font-size:.8em\">Server Container  v${appVersion.major}.${appVersion.minor}.${appVersion.build} ${appCopyright}</span>"
		}
	}
}


/*
	uninstallPage

	Purpose: Displays options for removing an instance.

	Notes: 	Really should create a proper token exchange someday.
*/
def uninstallPage()
{
	dynamicPage(name: "uninstallPage", title: "Uninstall HubConnect", uninstall: true, install: false)
	{
		section(menuHeader("Warning!"))
		{
			paragraph "It is strongly recommended to back up your hub before proceeding.\n\nThis will remove ALL HubConnect instances and devices from this hub!!!\n\nThis action cannot be undone!\n\nClick the [Remove] button below completely remove HubConnect."
		}
		section(menuHeader("Options"))
		{
			input "removeDevices", "bool", title: "Remove virtual HubConnect shadow devices on this hub?", required: false, defaultValue: true, submitOnChange: true
		}
		section()
		{
			href "mainPage", title: "Cancel and return to the main menu..", description: "", state: null
		}
	}
}


/*
	appButtonHandler

	Purpose: Handles button events for various pages.
*/
def appButtonHandler(btnPressed)
{
	switch(btnPressed)
	{
		// Restart all hub services
		case "restartAllServices":
			childApps.each
			{
		  	  child ->
				if (child.getState()?.connectStatus != "offline" && child.remoteType != "homebridge")
				{
					log.warn "Re-initializing ${child.label}..."
					child.initialize()
					if (child.remoteInitialize()?.status == "success")
						state.utilityStatus += "${child.label} reinitialized\n"
					else state.utilityStatus += "ERROR: ${child.label} could not be reinitialized\n"
				}
				else log.warn "Could not restart services on ${child.label} because the hub is offline or does not support the command..."
			}
			break
		case "resetCustomDrivers":
			state.customDrivers = [:]
			log.info "Custom driver system has been reset."
			state.utilityStatus = "Custom driver definitions have been reset!"
			saveCustomDrivers()
			break;
		case "addField":
			if (state.cdFields++ > 50) state.cdFields = 50
			break
	}

	// Individual hub service restarts
	if (btnPressed.startsWith("restart_"))
	{
		def appId = btnPressed.replace("restart_", "")
		def child = childApps?.find{it.id == Integer.parseInt(appId)}
		if (child != null)
		{
			log.warn "Re-initializing ${child.label}..."
			child.initialize()
			if (child.remoteInitialize()?.status == "success")
				state.utilityStatus += "${child.label} reinitialized\n"
			else state.utilityStatus += "ERROR: ${child.label} could not be reinitialized\n"
		}
	}
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
		uri:  command,
		requestContentType: "application/json",
		headers:
		[
			Authorization: "Bearer ${state.clientToken}"
		],
		timeout: 10
	]

	try
	{
		httpGet(requestParams)
		{
	  	  response ->
			if (response?.status == 200)
			{
				return response.data
			}
			else
			{
				log.warn "httpGet() request failed with status ${response?.status}"
				return [status: "error", message: "httpGet() request failed with status code ${response?.status}"]
			}
		}
	}
	catch (Exception e)
	{
		log.error "httpGet() failed with error ${e.message}"
		return [status: "error", message: e.message]
	}
}


/*
	versionCheck

	Purpose: Fetches the latest available module versions.
*/
def versionCheck()
{
	atomicState.currentVersions = null
	def token = URLEncoder.encode(location.hubs[0].toString())

	def requestParams =
	[
		uri: "http://irisusers.com/hubitat/hubconnect/latest.php?s=${token}",
		requestContentType: 'application/json',
		contentType: 'application/json',
		timeout: 10
	]

	try
	{
		asynchttpGet("versionCheckResponse", requestParams)
	}
	catch (Exception e)
	{
		log.error "asynchttpGet() failed with error ${e.message}"
	}
}


/*
	versionCheckResponse

	Purpose: Stores the retreived version information to state.
*/
def versionCheckResponse(response, data)
{
	if (response?.status == 200 && response.json.versions != null)
	{
		atomicState.currentVersions = response.json.versions
	}
}

def menuHeader(titleText){"<div style=\"width:102%;background-color:#696969;color:white;padding:4px;font-weight: bold;box-shadow: 1px 2px 2px #bababa;margin-left: -10px\">${titleText}</div>"}
def isNewer(latest, installed) { (latest.major.toInteger() > installed.major ||  (latest.major.toInteger() == installed.major && latest.minor.toInteger() > installed.minor) || (latest.major.toInteger() == installed.major && latest.minor.toInteger() == installed.minor && latest.build.toInteger() > installed.build)) ? true : false }
def getAppVersion() {[platform: "Hubitat", major: 1, minor: 6, build: 3]}
def getAppCopyright(){"&copy; 2019 Steve White, Retail Media Concepts LLC <a href=\"https://github.com/shackrat/Hubitat-Private/blob/master/HubConnect/License%20Agreement.md\" target=\"_blank\">HubConnect License Agreement</a>"}
