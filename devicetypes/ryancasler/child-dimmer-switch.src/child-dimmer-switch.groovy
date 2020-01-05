/**
 * 
 */
metadata {
	definition (name: "Child Dimmer Switch", namespace: "ryancasler", author: "Ryan Casler") {
		capability "Switch Level"
        capability "Switch"
		capability "Relay Switch"
		capability "Actuator"
		capability "Sensor"

		attribute "lastUpdated", "String"
	}

	simulator {

	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 3, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState:"turningOn"
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState:"turningOff"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
   			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
		}
        
 		valueTile("level", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
		}
 		valueTile("lastUpdated", "device.lastUpdated", inactiveLabel: false, decoration: "flat", width: 4, height: 2) {
    		state "default", label:'Last Updated ${currentValue}', backgroundColor:"#ffffff"
        }
       
		main(["switch"])
		details(["switch", "level", "lastUpdated"])       
	}
}

def on() {
	sendData("on")
}

def off() {
	sendData("off")
}

def setLevel(value) {
	log.debug "setLevel >> value: $value"
	def valueaux = value as Integer
	def level = Math.max(Math.min(valueaux, 99), 0)
    sendParam("${level}")
//	if (level > 0) {
//		sendEvent(name: "switch", value: "on")
//	} else {
//		sendEvent(name: "switch", value: "off")
//	}
}

def sendData(String value) {
    def name = device.deviceNetworkId.split("-")[-1]
    parent.sendData(name, value)  
}

def sendParam(level){
    def name = device.deviceNetworkId.split("-")[-1]
	parent.sendCmdParam(name, "setLevel", level)
}

def parse(name, value) {
/*    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null*/
    if (name && value) {    // Update device
        if ((value == "on") || (value == "off")) {
            sendEvent(name: "switch", value: value)
        }
        else
        {
            sendEvent(name: "level", value: value)
        }
        // Update lastUpdated date and time
        def nowDay = new Date().format("MMM dd", location.timeZone)
        def nowTime = new Date().format("h:mm a", location.timeZone)
        sendEvent(name: "lastUpdated", value: nowDay + " at " + nowTime, displayed: false)
    }
    else {
    	log.debug "Missing either name or value.  Cannot parse!"
    }
}

def installed() {
}

def resendStatus(){
	def main = device.currentValue("switch")
    def level = device.currentValue("level")
}