/**
 */
metadata {
	definition (name: "Child Presence Sensor", namespace: "ryancasler", author: "Ryan Casler") {
		capability "Sensor"
		capability "Presence Sensor"

		attribute "lastUpdated", "String"
        attribute "level", "Number"
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

def parse(String description) {
    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
        if (value.isNumber()) {
            sendEvent(name: "level", value: value)
            if (presenceTriggerValue) {
                log.debug "Presence received a numeric value. Perform comparison of value: ${Float.valueOf(value.trim())} versus presenceTriggerValue: ${presenceTriggerValue}"
                if (Float.valueOf(value.trim()) >= presenceTriggerValue) {
                    value = invertTriggerLogic?"not present":"present"
                } 
                else {
                    value = invertTriggerLogic?"present":"not present"
                }
            }
            else {
                log.error "Please configure the Presence Trigger Value in device settings!"
            }
        }
        else {
            log.debug "Presence received a string.  value = ${value}"
            if (value != "present") { value = "not present" }
        }
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


def installed() {
}