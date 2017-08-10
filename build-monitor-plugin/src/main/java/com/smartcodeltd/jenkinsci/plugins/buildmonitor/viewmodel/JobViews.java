package com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel;

import com.smartcodeltd.jenkinsci.plugins.buildmonitor.facade.StaticJenkinsAPIs;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.features.*;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.features.headline.HeadlineConfig;
import hudson.matrix.MatrixProject;
import hudson.model.Job;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Jan Molak
 */
public class JobViews {
    private static final String Claim                  = "claim";
    private static final String Build_Failure_Analyzer = "build-failure-analyzer";
    private static final String Groovy_Post_Build      = "groovy-postbuild";
    private static final String Pipeline               = "workflow-aggregator";

    private final StaticJenkinsAPIs jenkins;
    private final com.smartcodeltd.jenkinsci.plugins.buildmonitor.Config config;

    public JobViews(StaticJenkinsAPIs jenkins, com.smartcodeltd.jenkinsci.plugins.buildmonitor.Config config) {
        this.jenkins = jenkins;
        this.config  = config;
    }

    public List<JobView> viewsOf(Job<?, ?> job) {
        List<Feature> viewFeatures = newArrayList();

        // todo: a more elegant way of assembling the features would be nice
        viewFeatures.add(new HasHeadline(new HeadlineConfig(config.shouldDisplayCommitters())));
        viewFeatures.add(new KnowsLastCompletedBuildDetails());
        viewFeatures.add(new KnowsCurrentBuildsDetails());

        if (jenkins.hasPlugin(Claim)) {
            viewFeatures.add(new CanBeClaimed());
        }

        if (jenkins.hasPlugin(Build_Failure_Analyzer)) {
            viewFeatures.add(new CanBeDiagnosedForProblems());
        }

        if (jenkins.hasPlugin(Groovy_Post_Build)) {
        	viewFeatures.add(new HasBadges());
        }

        if (job instanceof MatrixProject && true /*config.displayAllMatrixConfigurations*/){
            viewFeatures.add(new ShowMatrixConfigurations());
        }

        boolean isPipelineJob = jenkins.hasPlugin(Pipeline) && job instanceof WorkflowJob;

        return JobView.of(job, viewFeatures, isPipelineJob);
    }
}
