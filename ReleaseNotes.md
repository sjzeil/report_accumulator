= Release Notes

== v1.0 March 14, 2016

Initial release.

Java classes providing basic functionality for collecting statistics
and appending to CSV files.

== v1.1 August 13, 2016

Provides Gradle tasks to collect statistics and to deploy
project reports via ssh/rsync to a web server.

== v1.2 October 15, 2017

Adds support for SpotBugs

== v1.3 September 13, 2019

Becomes a "true" plugin, supporting the new plugin id style

Automatically adds new tasks reportStats and deployReports to the gradle build
instead of just delcalring the task types.

== v1.4 November 23, 2021

* Updated code for compatibility with Gradle 7.x
* Dropped the `deployReports` task, as uploading to the website can be better handled by other plugins & tasks.
* Fixed a bug that prevented finding statistics in Jacoco reports.
* Dropped support for FindBugs.

 
