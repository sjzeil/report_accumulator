package edu.odu.cs.zeil.report_accumulator

import org.gradle.api.Plugin
import org.gradle.api.Project


class ReportAccumulatorPlugin implements Plugin<Project> {

    @Override
    void apply (Project project) {

        project.getPluginManager().apply(new org.hidetake.gradle.ssh.plugin.SshPlugin().class)
        project.task ('reportStats', type: edu.odu.cs.zeil.report_accumulator.ReportStats) {
            dependsOn 'reports'
            // reportsURL = 'http://www.cs.odu.edu/~zeil/gitlab/reportAccumulator/reports'
        }

    }
}