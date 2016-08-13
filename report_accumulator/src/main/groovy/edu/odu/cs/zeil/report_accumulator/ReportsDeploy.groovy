package edu.odu.cs.zeil.report_accumulator

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.FileSystem
import java.nio.file.FileSystems

import java.util.zip.ZipOutputStream

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


	ReportsDeploy () {

		// Add a Course object as a property of the project
		reportsDir = project.file('build/reports');
		deployDestination = project.file('build/website');
		
		project.remotes {
			remoteReportsHost {
				// Values will be filled in later
				host = 'placeholder'
				user = 'placeHolder'
				agent = true
			}
		}
	}


	@TaskAction
	def perform() {
		if (deployDestination.startsWith("ssh://")) {
			deployBySsh()
		} else if (deployDestination.startsWith("rsync://")) {
			deployByRsync()
		} else {
		    deployByCopy()
		}
	}
	
	void deployBySsh() {
		String sshUrl = deployDestination.substring(6)
		int k0 = sshUrl.indexOf('@')
		int k1 = sshUrl.indexOf(':')
		def hostName = sshUrl.substring(k0+1,k1)
		def userName = sshUrl.substring(0, k0)
		def remotePath = sshUrl.substring(k1+1)
		project.remotes.remoteReportsHost.host = hostName
		project.remotes.remoteReportsHost.user = userName
		if (deploySshKey != null) {
			project.remotes.remoteReportsHost.identity =
					project.file(deploySshKey)
		}
		zipItAllUp(project.file('build/temp/reports.zip'))
		project.ssh.run {
			settings {
				dryRun = false
			}
			session (project.remotes.remoteReportsHost) {
				put from: project.file('build/temp/reports.zip'),
				into: remotePath
				execute "unzip -u -q -o ${remotePath}/reports.zip -d ${remotePath}"
				execute "/bin/rm -f ${remotePath}/reports.zip"
			}
			println "Sent to " + sshUrl
		}
	}
	
	void deployByRsync() {
		String rsyncUrl = deployDestination.substring(8)
		if (!rsyncUrl.endsWith('/')) {
			rsyncUrl = rsyncUrl + '/'
		}
		
		Path cwd = Paths.get('').toAbsolutePath();
		Path relativeReportsDir = cwd.relativize(reportsDir.toPath())
		String sourceDir = relativeReportsDir.toString()
		if (!sourceDir.endsWith('/')) {
			sourceDir = sourceDir + '/'
		}

		String sshCmd = "ssh";
		if (deploySshKey != null) {
			sshCmd = "ssh -i ${deploySshKey}"
		}
		def cmd = [
				'rsync',
				'-auzv',
				'-e' + sshCmd,
				sourceDir,
				rsyncUrl
				]

		println ("Issuing rsync command\n" + cmd.iterator().join(" "))
		project.exec {
			commandLine cmd
			if (deploySshKey != null) {
				environment ('SSH_AGENT_PID', '')
				environment ('SSH_AUTH_SOCK', '')
			}
		}

	}
	
	
	void deployByCopy() {
		project.copy {
			from 'build/reports'
			into deployDestination
		}
	}
	
	
	
	
	void zipItAllUp (File destination)
	{
		destination.parentFile.mkdirs()
		Path reportsBase = reportsDir.toPath()
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(destination))
		zout.close()
		Path zipfile = destination.toPath()
		Queue<File> q = new LinkedList<File>()
		q.push(reportsDir)
		FileSystem zipfs = FileSystems.newFileSystem(zipfile, null)
		if (zipfs == null) {
			logger.error ("Could not create zip file system at " + zipfile)
		}
		while (!q.isEmpty()) {
			File dir = q.remove()
			for (File child : dir.listFiles()) {
				Path relChild = reportsBase.relativize(child.toPath())
				if (child.isDirectory()) {
					q.add(child)
					Path directory = zipfs.getPath("/", relChild.toString())
					Files.createDirectories(directory)
				} else {
					Path childLoc = zipfs.getPath("/", relChild.toString())
					Files.copy(child.toPath(), childLoc)
				}
			}
		}
		zipfs.close()
	}

	
	

}