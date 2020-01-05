/**
 * 
 */
metadata {
	definition (name: "Child Contact Sensor", namespace: "ryancasler", author: "Ryan Casler", mnmn: "SmartThings", vid: "generic-contact") {
		capability "Contact Sensor"
		capability "Sensor"

		attribute "lastUpdated", "String"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"contact", type: "generic"){
			tileAttribute ("device.contact", key: "PRIMARY_CONTROL") {
				attributeState "open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
				attributeState "closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
            }
 			tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
    				attributeState("default", label:'    Last updated ${currentValue}',icon: "st.Health & Wellness.health9")
            }
        }
	}

}

def parse(name, value) {
//    log.debug "parse(${description}) called"
//	def parts = description.split(" ")
//    def name  = parts.length>0?parts[0].trim():null
//     def value = parts.length>1?parts[1].trim():null
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
	log.debug"Not resending, nothing to send."
}