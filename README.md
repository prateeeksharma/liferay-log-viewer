
# Liferay Log Viewer Portlet

This project aims to provide flexibility to Liferay Admin Users to view Logs from the portal.

They don't need to log in to the Liferay Server using putty or any shell to view the logs. Now users with Administrator access can use this app present in the control panel to view the Logs in real-time.

This app uses the Log4j APIs to fetch the portal logs.

Since, Liferay versions later than 7.4 uses Log4j2, therefore this app is only compatible with Liferay 7.4.x versions.

## Supported Products

* Liferay Portal 7.4

## Usage

Administrators will see a "Log Viewer" portlet in the Configuration area of the Control Panel in 7.4

The portlet polls the log buffer every 2 seconds(can be configured using Liferay's Portal Properties) to update the page with the latest logs.
You can also attach or detach the logger from the portal log4j. (when detached, the portlet does not cause any overhead on portal operations).

Three portal properties can be set:
* *liferay.log.viewer.pattern* to configure the log4j pattern, default "%d{ABSOLUTE} %-5p \[%c{1}:%L\] %m%n"
* *liferay.log.viewer.refreshIntervalMs* to configure the Logs refresh duration on frontend, default "2000l"
* *liferay.log.viewer.sleepIntervalMs* to configure the Logs fetching duration from Log4j API, default "1000l"

## Downloads

Download bundle jar for 7.1.x CE: [com.liferay.log.viewer.portlet-1.0.0.jar](https://github.com/prateeeksharma/liferay-log-viewer/releases/download/v1.0.0/com.liferay.log.viewer.portlet-1.0.0.jar)

## References

https://github.com/baxtheman/liferay-log-viewer

