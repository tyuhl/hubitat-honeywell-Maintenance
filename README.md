# hubitat-honeywell
An app and driver for the Honeywell Home thermostats (Lyric, T5, T9 etc…). This uses the official Honeywell Home APIs and services.

## Notes
'This code is a fork of @thecloudtaylor's original work for ongoing improvements and bug fixes. It has been in use and stable since about 2022, so it is essentially production code.

Current Version: 1.0.5 (versioning added by me)

## Install Using Hubitat Package Manager (Preferred)
HPM is an app that allows you to easily install and update custom drivers and apps on your Hubitat hub. To use HPM, you need to have it installed on your hub.

Once you have HPM installed, follow these steps to install the "Honeywell Home Intergraion Lyric and T Series Devices (cloud)" application/driver:

1. In the Hubitat interface, go to Apps and select Hubitat Package Manager.
2. Select Install, then Search by Keywords.
3. Enter Honeywll in the search box and click Next.
4. Select Honeywell Home Integration Lyric and T Series Devices (cloud) by Tim Yuhl and click Next. 
> *Be careful to select the right package, as thecloudtaylor's original work is still in HPM and it does not have important bug fixes.*
5. Follow the install instructions.

## Manually Installing

1. Install the 2 drivers code (Thermostat and Remote Sensor) in the "Drivers Code" section and the Application code in the "Application Code" section of Hubitat.

## Getting Started

- Create a new instance of the app.
- Authenticate with Honeywell (establish OAuth Link)
- Discover devices

### Not Implemented/Tested
- Events 
- Schedules via Honeywelll

## Credits
- I got a lot of example and inspiration from @dkilgore90 (https://github.com/dkilgore90/google-sdm-api/blob/develop/sdm-api-app.groovy)
- Credit to Tim in the Hubitat community for patently testing and improving this with me over days/weeks.
