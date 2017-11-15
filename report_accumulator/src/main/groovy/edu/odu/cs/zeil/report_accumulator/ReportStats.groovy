package edu.odu.cs.zeil.report_accumulator

import java.nio.file.Path
import java.time.Instant

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.TaskAction


import org.hidetake.gradle.ssh.plugin.SshPlugin


/**
 * Properties for modifying report statistics accumulator.
 */
class ReportStats extends DefaultTask {

	/**
	 * Where are sources for the reports stored, defaults to
	 * rootproject/build/reports.
	 */
	File reportsDir = null
	
	/**
	 * http(s) URL of the location to which these reports are
	 * being deploy. Specifically, the URL to which the reportsDir
	 * is mapped.  This is used to check for statistics recorded
	 * from prior builds.
	 */
	URL reportsURL = null
	
	/**
	 * Source directory for files to be included in the report,
	 * typically the overall summary page,
	 * defaults to src/main/html/reports
	 */
	File htmlSourceDir = null
	
	/**
	 * Destination directory where contents of htmlSourceDir
	 * should be placed. 
	 */
	String htmlDestDir = 'main'
	
	
	/**
	 * Identifier for current build. Defaults to a time-stamp
	 * but can be overridden to match, for example, a git commit ID.
	 */
	String buildID = null
	
	ReportStats () {
		reportsDir = project.file('build/reports')
		htmlSourceDir = project.file('src/main/html/reports')
		buildID = Instant.now().toString();
		group = 'reporting'
	}
	
	void setReportsURL (String str) {
		reportsURL = new URL(str)
	}
	
	@TaskAction
	def perform() {
		Accumulator accum = new Accumulator (buildID, reportsURL,
			reportsDir.toPath())

        // accum.register(...);
        accum.register(new JUnitScanner())
        accum.register(new CheckstyleScanner())
        accum.register(new FindBugsScanner())
        accum.register(new SpotBugsScanner())
        accum.register(new PMDScanner())
        accum.register(new JacocoBranchCoverageScanner())

        accum.accumulateStatistics()
		
		project.copy {
			from htmlSourceDir
			into new File(reportsDir, htmlDestDir)
		}
	}
	

}