/*
 * LED Alerts-Multi
 *
 * 
 */
metadata {
    definition(name: "Child Led Alerts", namespace: "ryancasler", author: "Ryan Casler") {
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
    def String deviceNumber = device.deviceNetworkId.split("-")[-1]
    parent.sendData("$deviceNumber/$value")  
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

