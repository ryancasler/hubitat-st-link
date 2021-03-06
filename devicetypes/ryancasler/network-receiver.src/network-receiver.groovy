/**
 *
 */

import groovy.transform.Field
import org.json.JSONObject
import groovy.json.JsonSlurper
 
metadata {
	definition (name: "network_receiver", namespace: "ryancasler", author: "Ryan Casler") {
        capability "Configuration" 
        capability "Refresh"
        command "sendData", ["string"]
        attribute "lastUpdated", "String"
}

    simulator {
    }

    // Preferences
	preferences {
		input "ip", "text", title: "Hubitat IP Address", description: "IP Address in form 192.168.1.226", required: true, displayDuringSetup: true
		input "mac", "text", title: "Hubitat MAC Addr", description: "MAC Address in form of 02A1B2C3D4E5", required: true, displayDuringSetup: true
		input "app", "text", title: "Maker App Number", description: "App number of the Maker API that appears between api and devices (leave out /)", required: true, displayDuringSetup: true
		input "token", "text", title: "Hubitat Maker API Token", description: "Access Token for the Maker API that appears at the end of the URL", required: true, displayDuringSetup: true
	}

	// Tile Definitions
	tiles (scale: 2){
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "refresh", label:'Refresh', action:"refresh.refresh", icon:"st.secondary.tools"
		}
		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label:'Configure', action:"configuration.configure", icon:"st.secondary.tools"
		}
		childDeviceTiles("all")
  		valueTile("lastUpdated", "device.lastUpdated", width:5, height: 1){
        	state("lastUpdated", label: '${currentValue}')
        }
        main("lastUpdated")
        
	}
}

// parse events into attributes
def parse(String description) {
    def msg = parseLanMessage(description)
//    def slurper = new JsonSlurper()
//	msg = slurper.parseText(msg.body)
	msg = msg.data
    log.debug msg
    if(msg){
    	def deviceName = msg.device
	    def value = msg.value
		def deviceNumber = msg.deviceId
    	def command = msg.name
   	    def eventSend = true
  	    if(command == "lastUpdate") eventSend = false
    	if(deviceNumber == null) eventSend = false
	    if(eventSend){
	    	def childDevice = null
			try {
            	childDevices.each {
					try{
                		if (it.deviceNetworkId == "${device.deviceNetworkId}-${deviceNumber}") {
                		childDevice = it
                		}
            		} catch (e) {
            		}
        		}            
        		if (childDevice == null) {
        			log.debug "no child found - Auto Add it!"            
            		createChildDevice(deviceName, deviceNumber, command)
            	//find child again, since it should now exist!
            		childDevices.each {
						try{
            			//log.debug "Looking for child with deviceNetworkID = ${device.deviceNetworkId}-${name} against ${it.deviceNetworkId}"
                			if (it.deviceNetworkId == "${device.deviceNetworkId}-${deviceNumber}") {
                				childDevice = it
                    		//log.debug "Found a match!!!"
                			}
            			} catch (e) {
            			}
        			}
        		}
            	if (childDevice != null) {
                //log.debug "parse() found child device ${childDevice.deviceNetworkId}"
                	childDevice.parse(command, value)
					log.debug "${childDevice.deviceNetworkId} - name: ${command}, value: ${value}"
            	}
			} catch (e) {
        		log.error "Error in parse() routine, error = ${e}"
        	}
    	}
    	def nowDay = new Date().format("MMM dd", location.timeZone)
		def nowTime = new Date().format("h:mm a", location.timeZone)
    	sendEvent(name: "lastUpdated", value: nowDay + " , " + nowTime, displayed: false)
	}
}


// handle commands
def configure() {
	log.debug "Executing 'configure()'"
    updateDeviceNetworkID()
    state.app = settings.app
    state.token = settings.token
}


def installed() {
}


def updated() {
}

def updateDeviceNetworkID() {
	log.debug "Executing 'updateDeviceNetworkID'"
    def formattedMac = mac.toUpperCase()
    formattedMac = formattedMac.replaceAll(":", "")
    if(device.deviceNetworkId!=formattedMac) {
        log.debug "setting deviceNetworkID = ${formattedMac}"
        device.setDeviceNetworkId("${formattedMac}")
	}
    //Need deviceNetworkID updated BEFORE we can create Child Devices
	//Have the Arduino send an updated value for every device attached.  This will auto-created child devices!
}


private void createChildDevice(String deviceName, String deviceNumber, String type) {    
		log.trace "createChildDevice:  Creating Child Device $device.displayName $deviceName $type $deviceNumber"
      try {
        	def deviceHandlerName = ""
        	switch (type) {
         		case "contact": 
                		deviceHandlerName = "Child Contact Sensor" 
                	break
         		case "switch": 
                		deviceHandlerName = "Child Switch" 
                	break
         		case "level": 
                		deviceHandlerName = "Child Dimmer Switch" 
                	break
/*         		case "color": 
                		deviceHandlerName = "Child RGB Switch" 
                	break
				case "colorTemperature": 
                		deviceHandlerName = "Child RGBW Switch" 
                	break
         		case "temperature": 
                		deviceHandlerName = "Child Temperature Sensor" 
                	break
         		case "illuminance": 
                		deviceHandlerName = "Child Illuminance Sensor" 
                	break
        		case "smoke": 
                		deviceHandlerName = "Child Smoke Detector" 
                	break    
         		case "carbonMonoxide": 
                		deviceHandlerName = "Child Carbon Monoxide Detector" 
                	break    
*/         		case "motion": 
                		deviceHandlerName = "Child Motion Sensor" 
                	break
                case "presence": 
                		deviceHandlerName = "Child Presence Sensor" 
                	break
         		case "pushed": 
                		deviceHandlerName = "Child Button" 
                	break
         		case "held": 
                		deviceHandlerName = "Child Button" 
                	break
         		case "released": 
                		deviceHandlerName = "Child Button" 
                	break
			default: 
                		log.error "No Child Device Handler case for ${type}"
      		}
            log.debug("Creating device with: $deviceHandlerName")
            if (deviceHandlerName != "") {
         		addChildDevice(deviceHandlerName, "${device.deviceNetworkId}-${deviceNumber}", null,
         			[completedSetup: true, label: "${deviceName}", 
                	isComponent: false, componentName: "${deviceName}", componentLabel: "${deviceName}"])
        	}   
    	} catch (e) {
        	log.error "Child device creation failed with error = ${e}"
        	log.error = "Child device creation failed. Please make sure that the '${deviceHandlerName}' is installed and published."
    	}
}


def sendData(String value) {
    if (settings.ip != null) {
        sendHubCommand(new physicalgraph.device.HubAction(
            method: "GET",
            path: "/apps/api/$state.app/devices/$value?access_token=$state.token",
            headers: [ HOST: "$ip:80" ]
        ))
    } 
}




def refresh(){
	def children = getChildDevices()
    children.each{ child ->
    	child.resendStatus()
    }
}


def sendCommand(String device, String command) {
	log.debug "Sending $command to $device"
    if (settings.ip != null) {
        sendHubCommand(new physicalgraph.device.HubAction(
            method: "GET",
            path: "/apps/api/$state.app/command?access_token=$state.token&device=$device&command=$command",
            headers: [ HOST: "$ip:80" ]
        ))
    } 
}

def sendCmdParam(device, command, param) {
	log.debug "Sending $command to $device"
    if (settings.ip != null) {
        sendHubCommand(new physicalgraph.device.HubAction(
            method: "GET",
            path: "/apps/api/$state.app/command?access_token=$state.token&device=$device&command=$command&param=$param",
            headers: [ HOST: "$ip:80" ]
        ))
    } 
}