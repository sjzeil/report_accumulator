package edu.odu.cs.zeil.report_accumulator

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
class ReportsDeploy extends DefaultTask {

	/**
	 * Where are reports being deployed to? May be a path or an 
	 * ssh URL.  
	 */
	String deployDestination = null;
	

	/**
	 * Where are sources for the reports stored, defaults to
	 * build/reports.
	 */
	File reportsDir = null;

	
	/**
	 * ssh key file to use when deploying to a remote server.
	 * If null, relies on an externally configured ssh key agent.
	 */
	File deploySshKey = null;

	/**
	 * Source directory for files to be included in the report,
	 * defaults to src/main/html/reports
	 */
	File htmlSourceDir = null;
	
	/**
	 * Destination directory where contents of htmlSourceDir
	 * should be placed. 
	 */
	String htmlDestDir = 'main';
	
	
	ReportsDeploy (Project project) {
		
		// Add a Course object as a property of the project
		reportsDir = project.file('build/reports');
		htmlSourceDir = project.file('src/main/html/reports');
		deployDestination = project.file('build/website');
	}

}