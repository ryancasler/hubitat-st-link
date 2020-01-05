/*
 * LED Alerts-Multi
 *
 * 
 */
metadata {
    definition(name: "Child Led Alerts", namespace: "ryancasler", author: "Ryan Casler", mnmn: "SmartThings", vid: "generic-switch") {
        capability "Actuator"
        capability "Switch"
        capability "Sensor"
		command "red"
		command "blue"
		command "green"
		command "purple"
		command "orange"
		command "yellow"
    }
    simulator {

	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 3, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState:"turningOn"
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#00A0DC", nextState:"turningOff"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
 			tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
    				attributeState("default", label:'    Last updated ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        standardTile("red", "device.red", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "red", label:'Red', action:"red",backgroundColor: "#ff0000"
		}
        standardTile("blue", "device.blue", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "blue", label:'Blue', action:"blue",backgroundColor: "#0000ff"
		}
        standardTile("green", "device.green", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "green", label:'Green', action:"green",backgroundColor: "#00ff00"
		}
        standardTile("purple", "device.purple", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "purple", label:'Purple', action:"purple",backgroundColor: "#ff00ff"
		}
        standardTile("orange", "device.orange", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "orange", label:'Orange', action:"orange",backgroundColor: "#ff4d00"
		}
        standardTile("yellow", "device.yellow", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "yellow", label:'Yellow', action:"yellow",backgroundColor: "#ffee00"
		}
	}
}

def initialize(){
}

def parse(String description) {
    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
        // Update device
        sendEvent(name: name, value: value)
        // Update lastUpdated date and time
        def nowDay = new Date().format("MMM dd", location.timeZone)
        def nowTime = new Date().format("h:mm a", location.timeZone)
        sendEvent(name: "lastUpdated", value: nowDay + " at " + nowTime, displayed: false)
    }
    else {
    	log.debug "Missing either name or value.  Cannot parse!"
    }
}

def on() {
    sendData("on")
}

def off() {
    sendData("off")
}

def sendData(String value) {
    def name = device.deviceNetworkId.split("-")[-1]
//    parent.sendData("$deviceNumber/$value")  
	parent.sendCmdParam(name, "ledAlert", value)
    log.debug"command sent for $value $name"
}

def red() {
    sendData("red")
}

def blue() {
    sendData("blue")
}


def green() {
    sendData("green")
}

def purple() {
    sendData("purple")
}

def orange() {
    sendData("orange")
}


def yellow() {
    sendData("yellow")
}

def resendStatus(){
	lof.debug"nothing to send"
}

