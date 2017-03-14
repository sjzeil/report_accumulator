# Report Accumulator

## Summary

Monitor a variety of unit testing and static analysis reports and
accumulate statistics that can be plotted to portray development
trends.

This package will support interaction between a report directory (as
commonly constructed in Gradle Java projects) and a project
website to which those reports are regularly copied. The program
maintains accumulated statistics (e.g., tests passed and failed, test
coverage, number of warning messages issued) is simple .csv files on
the course website.

## Project Status

Under development

[Reports](http://www.cs.odu.edu/~zeil/gitlab/reportAccumulator/reports/reportsSummary/projectReports.html)

## Usage

In build.gradle:

    apply plugin: 'java'
    apply plugin: 'checkstyle' // and/or pmd, jacoco, findbugs
    apply plugin: 'org.hidetake.ssh'  // required for deploying reports to remote servers

    buildscript {
        repositories {
            jcenter()  // for ssh-plugin
            ivy { // for report_accumulator
                url 'https://secweb.cs.odu.edu/~zeil/ivyrepo'
            }
        }
        dependencies {
            classpath 'org.hidetake:gradle-ssh-plugin:2.7.0+'
            classpath 'edu.odu.cs.zeil:report_accumulator:1.1+'
        }
    }
    
    import edu.odu.cs.zeil.report_accumulator.ReportStats
    import edu.odu.cs.zeil.report_accumulator.ReportsDeploy
    
    // ... normal build tasks
    
    task collectStats (type: ReportStats, dependsOn: ['build') {
        reportsURL = 'http://project-reports-url'
    }

    task deployReports (type: ReportsDeploy, dependsOn: 'collectStats') {
        description 'Deploy reports and statistics to project website'
        deployDestination = '/path-for-copying-to-above-named-website'
        // or
        //deployDestination = 'ssh://yourName@hostAddr:path'
        // or
        //deployDestination = 'rsync://yourName@hostAddr:path'
        deploySshKey = 'path/to/ssh/key' // optional
    }
    
The `ReportStats` task will examine the `build/reports/` directory for
reports generated by JUnit, Jacoco, PMD, FindBugs, & Checkstyle. If found, it
extracts one or more point statistics (e.g., # of failures) from the report. It
then checks to see if the website at the reportsURL has CSV files from prior
runs of this task. If so, it downloads the current CSV file and appends the
new point statistics to it. If not, it creates a new CSV file to hold the
statistics.

The  `ReportsDeploy` task uploads the contents of the `build/reports`
directory (including any CSV files created or updated by the `ReportStats`
task to the web server either by local file copy, ssh, or rsync.

* Authentication for ssh & rsync relies, by default, on an already launched
  ssh key agent.  Optionally, a password-free ssh key may be specified.
  
    * Future versions may provide support for password authentication
      with or without the explicit key. 

* Rsync deployment presumes that an external rsync (and ssh)
  command is available.
    * ssh deployment does not require an external ssh command.  
 