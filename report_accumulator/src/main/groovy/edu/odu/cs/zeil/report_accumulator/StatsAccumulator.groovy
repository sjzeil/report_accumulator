package edu.odu.cs.zeil.report_accumulator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip

import org.hidetake.gradle.ssh.plugin.SshPlugin


/**
 * A plugin for describing a course website.
 */
class StatsAccumulator implements Plugin<Project> {

	void apply (Project project) {
		
		
		new org.hidetake.gradle.ssh.plugin.SshPlugin().apply(project);

		// Add a Course object as a property of the project
		project.extensions.create ("reportStats", ReportStats, project);
		
		
		project.remotes {
			remotehost {
				// Values will be filled in from course settings
				host = 'placeholder'
				user = 'placeHolder'
				agent = true
			}
		}

		


		project.task ('zip', type: Zip, dependsOn: 'build') {
			description 'Prepare a zip file of the website.'
			group 'Packaging'
			from 'build/website'
			//into '.'
			destinationDir = project.file('build/packages')
			archiveName 'website.zip'
			dirMode 0775
			fileMode 0664
			includeEmptyDirs true
		}


		

		
		project.task ('deploy', type: Sync, dependsOn: 'build') {
			description 'Copy course website to a local deployDestination directory.'
			group 'Deployment'
			from 'build/website'
			into { return project.course.deployDestination; }
			dirMode 0775
			includeEmptyDirs true
		}

		project.task ('deployBySsh', dependsOn: 'zip') {
			description 'Copy course website to a remote machine.'
			group 'Deployment'
			inputs.file 'build/packages/website.zip'
		} << {
			int k0 = project.course.sshDeployURL.indexOf('@')
			int k1 = project.course.sshDeployURL.indexOf(':')
			def hostName = project.course.sshDeployURL.substring(k0+1,k1)
			project.remotes.remotehost.host = hostName
			def userName = project.course.sshDeployURL.substring(0, k0)
			project.remotes.remotehost.user = userName
			def remotePath = project.course.sshDeployURL.substring(k1+1)
			if (project.course.sshDeployKey != null) {
				project.remotes.remotehost.identity =
						project.file(project.course.sshDeployKey)
			}

			project.ssh.run {
				settings {
					dryRun = false
				}
				session (project.remotes.remotehost) {
					put from: project.file('build/packages/website.zip'),
					into: remotePath
					execute "unzip -u -q -o ${remotePath}/website.zip -d ${remotePath}"
					execute "/bin/rm -f ${remotePath}/website.zip"
				}
				println "Sent to " + project.course.sshDeployURL
			}
}