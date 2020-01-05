/**
 *  Child Motion Sensor
 * 
 */
 metadata {
	definition (name: "Child Motion Sensor", namespace: "ryancasler", author: "Ryan Casler", mnmn: "SmartThings", vid: "generic-motion-2") {
		capability "Motion Sensor"
		capability "Sensor"

		attribute "lastUpdated", "String"
	}

	simulator {

	}

	tiles(scale: 2) {
		multiAttributeTile(name:"motion", type: "generic"){
			tileAttribute ("device.motion", key: "PRIMARY_CONTROL") {
				attributeState "active", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
				attributeState "inactive", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
            }
 			tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
    				attributeState("default", label:'    Last updated ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
	}
}

def parse(name, value) {
/*    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null*/
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

def installed() {
}

def resendStatus(){
	log.debug"nothing to resend"
}