/**
 */
metadata {
	definition (name: "Child Presence Sensor", namespace: "ryancasler", author: "Ryan Casler") {
		capability "Sensor"
		capability "Presence Sensor"
        command "arrived"
        command "departed"
        command "toggle"

		attribute "lastUpdated", "String"
	}

	simulator {

	}
    
	preferences {
	}
    
	tiles(scale: 2) {
		multiAttributeTile(name: "presence", type: "generic", width: 2, height: 2, canChangeBackground: true) {
			tileAttribute ("device.presence", key: "PRIMARY_CONTROL") {
            	attributeState "present", label: 'Present', icon:"st.presence.tile.present", backgroundColor:"#00A0DC"
				attributeState "not present", label: 'Away', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff"
            }
 			tileAttribute("device.level", key: "SECONDARY_CONTROL") {
    				attributeState("default", label:'    Level ${currentValue}')
            }
		}
        valueTile("lastUpdated", "device.lastUpdated", inactiveLabel: false, decoration: "flat", width: 6, height: 2) {
    			state "default", label:'Last Updated ${currentValue}', backgroundColor:"#ffffff"
		}
	}
}

def parse(name, value) {
//    log.debug "parse(${description}) called"
//	def parts = description.split(" ")
//    def name  = parts.length>0?parts[0].trim():null
//    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
        // Update device
        if(value=="not_present"){value= "not present"}
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


def installed() {
}

def arrived() {
    sendData("present")
}

def departed() {
    sendData("not present")
}

def sendData(String value) {
    def String deviceNumber = device.deviceNetworkId.split("-")[-1]
//    parent.sendData("$deviceNumber/$value")
	parent.sendCmdParam(deviceNumber, "presence", value)
}

def toggle(){
	if(device.currentValue("presence")=="present"){
    	departed()
    }else{
    	arrived()
    }
}
