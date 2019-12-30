HubConnect for Hubitat v1.0
Copyright 2019 Steve White, Retail Media Concepts LLC

HubConnect replaces the native HubLink/Link to Hub apps and includes the following enhancements:

- Support for multiple device attributes (i.e switch, power, voltage for a Zigbee Plug).
- Battery status is available for any device that supports it.
- Switches, Dimmers, RGB Bulbs, and Buttons are 2-way devices which can be controlled from the coordinator hub as well as the remote hub in which they are connected to.
- Fully bi-directional.  Connect physical devices from remote hubs to the coordinator AND devices on the coordinator to remote hubs!
- When a remote device is controlled from master, status updates (i.e. switch) are instantly sent by the remote device to ensure the device actually responded to the command.
- Full bi-directional SmartThings support including 2-way devices.
- Hub Health ensures each remote hub checks in with the coordinator every minute. If a hub fails to respond for 5 minutes it is declared offline.
- A virtual "hub presence" device is created for every linked remote hub. If the remote hub is responding, the status is "present", if it's offline the status is "not present".
- This uses the "HubConnect Beacon Sensor" and is done so rules can be created to notify when a hub is not responding.
- Flexible oAuth endpoints; Hubs do not need to be on the same LAN or even same location. 
- Remote hubs can be located anywhere with an internet connection.
- Communications between the master and remote hubs can be suspended for maintenance.  (i.e. rebooting the master hub as it prevents the remotes from logging http errors)
- Publish user-defined device drivers without modifying HubConnect source code.
- Mode changes can be pushed from the Server to Remote hubs, including SmartThings.
- Remote hub "Reboot-Recovery" checks-in with the Server hub to set the current system mode anytime a remote hub is rebooted.


Some things to note:

1. BACK UP YOUR HUB(s) before installing!!

2. HubConnect apps are licensed under a private license and are NOT open source. Please do not distribute code outside of this repository.

3. You are granted permission to use HubConnect for non-commercial purposes, for as many hubs as you control. 

4. HubConnect Stub Drivers are released under the Apache 2.0 license to encourage the development of custom drivers.  

5. Use HubConnect at your own risk!!  HubConnect nor its authors are responsible for crashing your hub!


SPECIAL THANKS
--------------

Special thanks to @CSteele from the Hubitat Community for his extensive efforts in alpha/beta testing HubConnect and for the numerous improvements
He has suggested.  (And for his fancy drawing too!)