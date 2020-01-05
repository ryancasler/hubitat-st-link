/**
 * v1.0.0 - 11/25/2019
 *
 */

definition(
    name:"Send To SmartThnigs",
    namespace: "ryancasler",
    author: "Ryan Casler",
    description: "",
    category: "Convenience",
    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
)

preferences {
    page(name: "pageConfig")
    mappings{
        path("/command") {
            action: [
                GET: "remoteCommand"
            ]
        }
    }
}

def installed() {
    log.debug "Installed"
}

def updated(){
    log.debug"Updated"
    subscribeNow()
    setDefaults()
}
 
def pageConfig() {
    
    dynamicPage(name: "pageConfig", title: "<h2 style='color:#0000FF;font-weight: bold'>Send to SmartThings</h2>", nextPage: null, install: true, uninstall: true, refreshInterval:0) {	
        section(getFormat("header-blue", "  Select Devices")) {
            input "myDevices", "capability.*", title: "Select Devices", submitOnChange: true, hideWhenEmpty: true, required: true, multiple: true
        }
        section(getFormat("header-blue", "  SmartThings Settings")) {
            input(name: "stIp", type: "string", defaultValue: "", title: "SmartThings IP Address", description: "")
            input(name: "stPort", type: "number", defaultValue: "39500", title: "SmartThings Port Number", description: "")
        }
        section(getFormat("header-blue", "  General")) {label title: "Enter a name for this app", required: false}
        section() {
            input(name: "logEnable", type: "bool", defaultValue: "true", title: "Enable Debug Logging", description: "Enable extra logging for debugging.")
        }
    }
}


def getFormat(type, myText=""){
	if(type == "header-blue") return "<div style='color:#ffffff;font-weight: bold;background-color:#0000ff;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
	if(type == "line") return "\n<hr style='background-color:#00CED1; height: 1px; border: 0;'></hr>"
	if(type == "title") return "<h2 style='color:#0000FF;font-weight: bold;font-style: italic'>${myText}</h2>"
}

def uninstalled(){
    unschedule()
    unsubscribe()
}

def subscribeNow(){
    unsubscribe()
    subscribe(myDevices, "switch", eventHandler)
    subscribe(myDevices, "contact", eventHandler) 
    subscribe(myDevices, "presence", eventHandler)
    if(logEnable)log.debug "Subscribed"
}

def setDefaults(){
	if(logEnable == null){logEnable = false}
    if(state.remoteUrl == null) {state.remoteUrl = getFullApiServerUrl()}
    if(state.localUrl == null){state.localUrl= getFullLocalApiServerUrl()}
    if(state.token == null){state.token = createAccessToken()}
    if(state.enable == null){state.enable = true}
}


def eventHandler(event){
    if(logEnable)log.debug "${event.device}  ${event.deviceId} ${event.name} ${event.value}"
    def data = "{ \"device\": \"${event.device}\",\"deviceId\": \"${event.deviceId}\",\"name\": \"${event.name}\",\"value\": \"${event.value}\" }"
    sendData(data)
}




def sendData(data) {
    try {
         if(logEnable)log.debug "Sending ${data} to ${stIp}:${stPort}"

        def headers = [:]
        headers.put("HOST", "${stIp}:${stPort}")
        headers.put("Content-Type", "application/json")
        def method = "POST"
        def msg = new hubitat.device.HubAction(
            method: "POST",
            body: data,
            headers: headers
        )
        sendHubCommand(msg)

    } catch (Exception e) {
        log.error "Error = ${e}"
    } 
}


def remoteCommand(){
    if(logEnable) log.debug params
    def id = params.device.toInteger()
    def value = params.param
    def device = myDevices.find {it.deviceId == id}
    switch(params.command){
        case{it=="on"}:
            if(device.currentSwitch == "off"){
                device.on()
            }
        break
        case{it=="off"}:            
            if(device.currentSwitch == "on"){
                device.off()
            }
        break
        case{it=="setLevel"}:
            if(device.currentValue("level") != params.param){
                device.setLevel(params.param)
            }
        break
        case{it=="ledAlert"}:
            device.ledAlert(value)
        break
        case{it=="presence"}:
            if(value=="present"){
                device.arrived()
            }else{
                device.departed()
            }
        break
    }
    def response = "received"
    return response
}
