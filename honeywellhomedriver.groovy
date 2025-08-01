/*
Hubitat Driver For Honeywell Thermistate

Copyright 2020 - Taylor Brown

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Major Releases:
11-25-2020 :  Initial 
11-27-2020 :  Alpha Release (0.1)
12-15-2020 :  Beta 1 Release (0.2.0)

*/
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

metadata {
    definition (
        name: "Honeywell Home Thermostat", 
        description:"Driver for Lyric (LCC) and T series (TCC) Honeywell Thermostats, Requires corisponding Honeywell Home App.",
        importUrl:"https://raw.githubusercontent.com/thecloudtaylor/hubitat-honeywell/main/honeywellhomedriver.groovy",
        namespace: "thecloudtaylor", 
        author: "Taylor Brown") {
            capability "Actuator"
            capability "Temperature Measurement"
            capability "Relative Humidity Measurement"
            capability "Thermostat"
            capability "Refresh"

        //Maybe?
        capability "Sensor"
        attribute "thermostatFanState", "enum", ["true", "false"]
        attribute "emergencyHeatActive", "enum", [null, "true", "false"]
        attribute "autoChangeoverActive", "enum", ["unsupported", "true", "false"]
        attribute "allowedModes", "enum", ["EmergencyHeat", "Heat", "Off", "Cool","Auto"]
        attribute "units", "enum", ["F", "C"]

    }
    preferences{
        input ("heatModeEnabled", "bool", 
                title: "Allow Heat Mode, if false heat mode will be iqnored",
                defaultValue:true)
        input ("coolModeEnabled", "bool", 
                title: "Allow Cool Mode, if false cool mode will be iqnored",
                defaultValue:true)
        input ("debugLogs", "bool", 
               title: "Enable debug logging", 
               defaultValue: false)
        input ("descriptionText", "bool", 
               title: "Enable description text logging", 
               defaultValue: true)
    }
}

void LogDebug(logMessage)
{
    if(debugLogs)
    {
        log.debug "${device.displayName} ${logMessage}";
    }
}

void LogInfo(logMessage)
{
    log.info "${device.displayName} ${logMessage}";
}

void LogWarn(logMessage)
{
    log.warn "${device.displayName} ${logMessage}";
}

void disableDebugLog() 
{
    LogWarn("Disabling Debug Logging.");
    device.updateSetting("debugLogs",[value:"false",type:"bool"]);
}

void installed()
{
    LogInfo("Installing.");
    heatModeEnabled = true
    coolModeEnabled = true
    debugLogs = false
    descriptionText = true
    refresh()
}

void uninstalled()
{
    LogInfo("Uninstalling.");
}

void updated() 
{
    LogInfo("Updating.");
    refresh()
}

void parse(String message) 
{
    LogDebug("ParseCalled: ${message}");
}

void auto()
{
    LogDebug("auto called");

    if(device.currentValue("allowedModes").contains("Auto"))
    {
        setThermostatMode("auto")
    }
    else
    {
        LogWarn("Auto not in the supported modes.")
    }
}

void cool()
{
    LogDebug("cool called");

    setThermostatMode("cool")
}

void emergencyHeat()
{
    LogDebug("emergencyHeat called");

    LogWarn("EmergancyHeat Not Supported")
    
}

void fanAuto()
{
    LogDebug("fanAuto called");
    setThermostatFanMode("auto")
}

void fanCirculate()
{
    LogDebug("fanCirculate called");
    setThermostatFanMode("circulate")
}

void fanOn()
{
    LogDebug("fanOn called");
    setThermostatFanMode("on")
}

void heat()
{
    LogDebug("heat called");

    setThermostatMode("heat")
}

void off()
{
    LogDebug("off called");

    setThermostatMode("off")
}

//Defined Command : temperature required (NUMBER) - Cooling setpoint in degrees
void setCoolingSetpoint(temperature)
{
    LogDebug("setCoolingSetpoint() - autoChangeoverActive: ${device.currentValue("autoChangeoverActive")}");
    
    //setThermosatSetPoint(com.hubitat.app.DeviceWrapper device, mode=null, autoChangeoverActive=false, heatPoint=null, coolPoint=null)
    if (!parent.setThermosatSetPoint(device, null, device.currentValue("autoChangeoverActive"), device.currentValue("emergencyHeatActive"), null, temperature))
    {
        LogWarn("Set cooling point failed, attempting a re-try.")
        parent.setThermosatSetPoint(device, null, device.currentValue("autoChangeoverActive"), device.currentValue("emergencyHeatActive"), null, temperature)
    }
    else
    {
        LogInfo("Cooling setpoint changed to ${temperature}")
    }
}

//Defined Command : temperature required (NUMBER) - Heating setpoint in degrees
void setHeatingSetpoint(temperature)
{
    LogDebug("setHeatingSetpoint() - autoChangeoverActive: ${device.currentValue("autoChangeoverActive")}");

    //setThermosatSetPoint(com.hubitat.app.DeviceWrapper device, mode=null, autoChangeoverActive=false, heatPoint=null, coolPoint=null)
    if (!parent.setThermosatSetPoint(device, null, device.currentValue("autoChangeoverActive"), device.currentValue("emergencyHeatActive"), temperature, null))
    {
        LogWarn("Set heating point failed, attempting a re-try.")
        parent.setThermosatSetPoint(device, null, device.currentValue("autoChangeoverActive"), device.currentValue("emergencyHeatActive"), temperature, null)
    }
    else
    {
        LogInfo("Heating setpoint changed to ${temperature}")
    }
}

//Defined Command : JSON_OBJECT (JSON_OBJECT) - JSON_OBJECT
void setSchedule(JSON_OBJECT)
{
    LogDebug("setSchedule called");
}

//Defined Command : fanmode required (ENUM) - Fan mode to set
void setThermostatFanMode(fanmode)
{
    LogDebug("setThermostatFanMode called");
    
    if(device.currentValue("supportedThermostatFanModes").contains(fanmode))
    {
        if(!parent.setThermosatFan(device, fanmode))
        {
            LogWarn("Set fan mode failed, attempting a re-try.")
            parent.setThermosatFan(device, fanmode);
        }
        else
        {
            LogInfo("Fan Mode Chanaged to: ${fanmode}")
        }
    }
    else
    {
        LogWarn("${fanmode} not in the supported fan modes.")
    }
}

//Defined Command : thermostatmode required (ENUM) - Thermostat mode to set
void setThermostatMode(thermostatmode)
{
    LogDebug("setThermostatMode() - autoChangeoverActive: ${device.currentValue("autoChangeoverActive")}");

    if (thermostatmode == "heat" && !heatModeEnabled)
    {
        LogInfo("Heating Mode Requested but Not Allowed, Iqnoring")
        return
    }

    if (thermostatmode == "cool" && !coolModeEnabled)
    {
        LogInfo("Cooling Mode Requested but Not Allowed, Iqnoring")
        return
    }

    //setThermosatSetPoint(com.hubitat.app.DeviceWrapper device, mode=null, autoChangeoverActive=false, heatPoint=null, coolPoint=null)
    if (!parent.setThermosatSetPoint(device, thermostatmode, device.currentValue("autoChangeoverActive"), device.currentValue("emergencyHeatActive"), null, null))
    {
        LogWarn("Set point failed, attempting a re-try.")
        parent.setThermosatSetPoint(device, thermostatmode, device.currentValue("autoChangeoverActive"), device.currentValue("emergencyHeatActive"), null, null);
    }
    else
    {
        LogInfo("mode changed to ${thermostatmode}")
    }
}

void refresh()
{
    LogDebug("Refresh called");
    parent.refreshThermosat(device)
}
