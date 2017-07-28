package com.smartcodeltd.jenkinsci.plugins.buildmonitor;

import com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.JobView;
import hudson.Util;
import hudson.model.Job;
import hudson.model.ViewDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class BuildMonitorDescriptor extends ViewDescriptor {

//    Stores user provided nicknames
    private HashMap<String, String> nicknameMap;
    private HashMap<String, String> jobMap;
    private HashMap<String, String> regexMap;


    public BuildMonitorDescriptor() {
        super(BuildMonitorView.class);
        nicknameMap = new HashMap<String, String>();
        jobMap = new HashMap<String, String>();
        regexMap = new HashMap<String, String>();
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

    public void doAddFilter(@QueryParameter("regexFilterReplace") String replace, @QueryParameter("regexFilterWith") String replaceWith){
        for(int i = 0; i < jobMap.size(); ++i){
            String curJobName = (String)jobMap.keySet().toArray()[i];
            if(curJobName.contains(replace)){
                regexMap.put(replace, replaceWith);
                String newName = curJobName.replace(replace, replaceWith);
                nicknameMap.put(curJobName, newName);
            }
        }
    }

    public void supplyJobs(HashMap<String, String> jobs){
        this.jobMap = jobs;
    }

    public HashMap<String, String> getRegexMap(){
        return this.regexMap;
    }

    public void doRemoveRenameFilter(@QueryParameter("curKey") String key, @QueryParameter("curVal") String val){
        for(int i = 0; i < this.nicknameMap.size(); ++i){
            String curName = (String)this.nicknameMap.keySet().toArray()[i];
            String curNickname = (String)this.nicknameMap.values().toArray()[i];
            if(curName.contains(key)){
                String name = curNickname.replace(val, key);
                this.nicknameMap.put(curName, name);
            }
        }
        this.regexMap.remove(key);
    }

    public void doPrintSomething() {
        ArrayList<String> aList = new ArrayList<String>();
        aList.add("Mason");
        aList.add("Reece");

        try {
            for (int i = 0; i < 17; ++i) {
                System.out.println(aList.get(i));
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Out of Bounds");
        } finally {
            System.out.println("Finally");
        }
    }
}