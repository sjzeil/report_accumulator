package edu.odu.cs.zeil.report_accumulator

import org.gradle.api.Plugin
import org.gradle.api.Project


class ReportAccumulatorPlugin implements Plugin<Project> {

    @Override
    void apply (Project project) {

        //new org.hidetake.gradle.ssh.plugin.SshPlugin().apply(project);

        project.task ('reportStats', type: edu.odu.cs.zeil.report_accumulator.ReportStats) {
            dependsOn 'reports'
            // reportsURL = 'http://www.cs.odu.edu/~zeil/gitlab/reportAccumulator/reports'
        }

        project.task ('deployReports', type: edu.odu.cs.zeil.report_accumulator.ReportsDeploy) {
            dependsOn  'reportStats'
            //deployDestination = '/home/zeil/temp'
            //deployDestination = 'ssh://zeil@atria.cs.odu.edu:/home/zeil/public_html/gitlab/'
            // deployDestination = 'rsync://zeil@atria.cs.odu.edu:reportAccumulator/reports/'
        }
    }
}