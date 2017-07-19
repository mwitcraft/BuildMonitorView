package com.smartcodeltd.jenkinsci.plugins.buildmonitor;

import hudson.Util;
import hudson.model.Job;
import hudson.model.ViewDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class BuildMonitorDescriptor extends ViewDescriptor {

    private ArrayList<String> nicknames;
    private HashMap<String, String> nicknameMap;

    public BuildMonitorDescriptor() {
        super(BuildMonitorView.class);
        nicknames = new ArrayList<String>();
        nicknameMap = new HashMap<String, String>();
        load();
    }

    @Override
    public String getDisplayName() {
        return "Build Monitor View";
    }

    // Copy-n-paste from ListView$Descriptor as sadly we cannot inherit from that class
    public FormValidation doCheckIncludeRegex(@QueryParameter String value) {
        String v = Util.fixEmpty(value);
        if (v != null) {
            try {
                Pattern.compile(v);
            } catch (PatternSyntaxException pse) {
                return FormValidation.error(pse.getMessage());
            }
        }
        return FormValidation.ok();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json.getJSONObject("build-monitor"));
        save();

        return true;
    }

    private boolean permissionToCollectAnonymousUsageStatistics = true;

    public boolean getPermissionToCollectAnonymousUsageStatistics() {
        return this.permissionToCollectAnonymousUsageStatistics;
    }

    @SuppressWarnings("unused") // used in global.jelly
    public void setPermissionToCollectAnonymousUsageStatistics(boolean collect) {
        this.permissionToCollectAnonymousUsageStatistics = collect;
    }
    public void doAddNickname(@QueryParameter("nickname") String nickname, @QueryParameter("jobName") String jobName){
        this.nicknameMap.put(jobName, nickname);
        for(int i = 0; i < nicknameMap.size(); ++i){
            System.out.println("#####" + "Key: " + nicknameMap.keySet().toArray()[i] + "#####");
            System.out.println("#####" + "Value: " + nicknameMap.values().toArray()[i] + "#####");
        }
    }

    public ArrayList<String> getNicknames(){
        return this.nicknames;
    }

    public HashMap<String, String> getNicknameMap(){
        return this.nicknameMap;
    }
}