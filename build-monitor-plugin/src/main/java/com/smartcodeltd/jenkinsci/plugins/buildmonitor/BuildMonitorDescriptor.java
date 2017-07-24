package com.smartcodeltd.jenkinsci.plugins.buildmonitor;

import hudson.Util;
import hudson.model.ViewDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class BuildMonitorDescriptor extends ViewDescriptor {

//    Stores user provided nicknames
    private HashMap<String, String> nicknameMap;

    public BuildMonitorDescriptor() {
        super(BuildMonitorView.class);
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
    @SuppressWarnings("unused")
//    Used in 'configure-entries.jelly' adds job name and nickname to map
    public void doAddNickname(@QueryParameter("nickname") String nickname, @QueryParameter("jobName") String jobName){
        this.nicknameMap.put(jobName, nickname);
    }

//    Retrieves this.nicknameMap
    public HashMap<String, String> getNicknameMap(){
        return this.nicknameMap;
    }
}