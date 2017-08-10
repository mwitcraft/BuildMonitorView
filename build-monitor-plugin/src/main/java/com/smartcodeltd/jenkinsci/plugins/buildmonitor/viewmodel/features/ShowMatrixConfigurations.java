package com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.features;

import com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.JobView;

public class ShowMatrixConfigurations implements Feature{
    private JobView job;

    @Override
    public ShowMatrixConfigurations of(JobView jobView) {
        this.job = jobView;

        return this;
    }

    @Override
    public Object asJson() {
        return null;
    }
}
