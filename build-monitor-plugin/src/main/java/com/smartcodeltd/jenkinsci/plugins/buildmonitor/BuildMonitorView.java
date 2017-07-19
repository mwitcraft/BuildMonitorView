/*
 * The MIT License
 *
 * Copyright (c) 2013-2015, Jan Molak, SmartCode Ltd http://smartcodeltd.co.uk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.smartcodeltd.jenkinsci.plugins.buildmonitor;

import com.mongodb.util.Hash;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.api.Respond;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.facade.StaticJenkinsAPIs;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.installation.BuildMonitorInstallation;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.JobView;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.JobViews;
import hudson.Extension;
import hudson.model.Descriptor.FormException;
import hudson.model.Job;
import hudson.model.ListView;
import jdk.nashorn.internal.scripts.JO;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import sun.reflect.generics.tree.Tree;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import static hudson.Util.filter;

/**
 * @author Jan Molak
 */
public class BuildMonitorView extends ListView {
    @Extension
    public static final BuildMonitorDescriptor descriptor = new BuildMonitorDescriptor();

    private String title;
    private String nickname;
    private ArrayList<String> nicknameList;
    private String[] nicknameArray;
    private HashMap<String, String> jobMap;
    private String findName = "Mason";

    /**
     * @param name  Name of the view to be displayed on the Views tab
     * @param title Title to be displayed on the Build Monitor; defaults to 'name' if not set
     */
    @DataBoundConstructor
    public BuildMonitorView(String name, String title) {
        super(name);

        this.title = title;

        jobMap = new HashMap<String, String>();
    }

    @SuppressWarnings("unused")
    public void populateJobList(String jobName){
        if(jobMap.containsKey(jobName)){
            System.out.println("_____________________________________________________________________Already Exists");
            return;
        }
        else if(jobName == null){
            return;
        }
        else{
            jobMap.put(jobName, jobName);
        }
    }

    public String nowFindName(){
        return "hello";
    }

    @SuppressWarnings("unused")
    public void printJobMap(){
        for(int i = 0; i < jobMap.size(); ++i){
            System.out.println("Key: " +  jobMap.keySet().toArray()[i]);
            System.out.println("Value: " + jobMap.values().toArray()[i]);
        }
    }

    public Map<String, String> getJobMap(){
        jobMap.remove("");
        jobMap.remove(null);
        printMap(this.jobMap);
        Map<String,String> map = new TreeMap<String, String>(this.jobMap);
        return map;
    }

    public HashMap<String, String> getJobMap2(){
        jobMap.remove("");
        jobMap.remove(null);
        printMap(this.jobMap);
        return this.jobMap;
    }

    public String findValue(Job job){
        String val = this.jobMap.get(job.getName());
        System.out.println(val);
        return val;
    }

    public void printMap(HashMap<String, String> newMap){
        for(int i = 0; i < newMap.size(); ++i){
            System.out.println("Key: " +  newMap.keySet().toArray()[i]);
            System.out.println("Value: " + newMap.values().toArray()[i]);
        }
    }

    public void updateMap(){
        ArrayList<String> nicknames = descriptor.getNicknames();
        for(String name : nicknames){
            System.out.println("*****" + name + "*****");
        }
    }

    public void descNicknameMap(){
        HashMap<String, String> newMap = descriptor.getNicknameMap();

        System.out.println("NewMap keys: " + newMap.keySet().toString());
        System.out.println("JobMap keys: " + this.jobMap.keySet().toString());

        for(int i = 0; i < newMap.size(); ++i){
            for(int j = 0; j < this.jobMap.size(); ++j){
                if(newMap.keySet().toArray()[i].equals(this.jobMap.keySet().toArray()[j])){
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + newMap.keySet().toArray()[i] + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                    System.out.println("\n\n\nKey: " + newMap.keySet().toArray()[i].toString() + "\nValue: " + newMap.get(newMap.keySet().toArray()[i].toString()));

                    this.jobMap.put(newMap.keySet().toArray()[i].toString(), newMap.get(newMap.keySet().toArray()[i].toString()));
                }
            }
        }
    }

    @SuppressWarnings("unused") // used in .jelly
    public String getTitle() {
        return isGiven(title) ? title : getDisplayName();
    }

    @SuppressWarnings("unused")
    public String getNickname(){
        return isGiven(nickname) ? nickname : "Empty";
    }

    @SuppressWarnings("unused")
    public void updateNicknameList(){
        System.out.println("Updated");
        nicknameList = new ArrayList<String>();
        for(JobView v : jobViews()){
            nicknameList.add(v.name());
        }
    }

    @SuppressWarnings("unused")
    public String[] getListAsArray(){
        String[] nicknameArray = new String[this.nicknameList.size()];
        for(int i = 0; i < this.nicknameList.size(); ++i){
            nicknameArray[i] = nicknameList.get(i);
        }
        return nicknameArray;
    }


    @SuppressWarnings("unused") // used in .jelly
    public boolean isEmpty() {
        return jobViews().isEmpty();
    }

    @SuppressWarnings("unused") // used in .jelly
    public String getCsrfCrumbFieldName() {
        return new StaticJenkinsAPIs().crumbFieldName();
    }

    @SuppressWarnings("unused") // used in the configure-entries.jelly form
    public String currentOrder() {
        return currentConfig().getOrder().getClass().getSimpleName();
    }

    @SuppressWarnings("unused") // used in the configure-entries.jelly form
    public boolean isDisplayCommitters() {
        return currentConfig().shouldDisplayCommitters();
    }

    private static final BuildMonitorInstallation installation = new BuildMonitorInstallation();

    @SuppressWarnings("unused") // used in index.jelly
    public BuildMonitorInstallation getInstallation() {
        return installation;
    }

    @SuppressWarnings("unused") // used in .jelly
    public boolean collectAnonymousUsageStatistics() {
        return descriptor.getPermissionToCollectAnonymousUsageStatistics();
    }

    @Override
    protected void submit(StaplerRequest req) throws ServletException, IOException, FormException {
        super.submit(req);

        JSONObject json = req.getSubmittedForm();

        synchronized (this) {

            String requestedOrdering = req.getParameter("order");
            title                    = req.getParameter("title");
            nickname                 = req.getParameter("nickname");

            currentConfig().setDisplayCommitters(json.optBoolean("displayCommitters", true));

            try {
                currentConfig().setOrder(orderIn(requestedOrdering));
            } catch (Exception e) {
                throw new FormException("Can't order projects by " + requestedOrdering, "order");
            }
        }
    }

    /**
     * Because of how org.kohsuke.stapler.HttpResponseRenderer is implemented
     * it can only work with net.sf.JSONObject in order to produce correct application/json output
     *
     * @return Json representation of JobViews
     * @throws Exception
     */
    @JavaScriptMethod
    public JSONObject fetchJobViews() throws Exception {
        return Respond.withSuccess(jobViews());
    }

    // --
    private boolean isGiven(String value) {
        return ! (value == null || "".equals(value));
    }

    private List<JobView> jobViews() {
        JobViews views = new JobViews(new StaticJenkinsAPIs(), currentConfig());

        //A little bit of evil to make the type system happy.
        @SuppressWarnings("unchecked")
        List<Job<?, ?>> projects = new ArrayList(filter(super.getItems(), Job.class));
        List<JobView> jobs = new ArrayList<JobView>();

        Collections.sort(projects, currentConfig().getOrder());

        for (Job project : projects) {
            jobs.add(views.viewOf(project));
        }

        return jobs;
    }

    /**
     * When Jenkins is started up, Jenkins::loadTasks is called.
     * At that point config.xml file is unmarshaled into a Jenkins object containing a list of Views, including BuildMonitorView objects.
     *
     * The unmarshaling process sets private fields on BuildMonitorView objects directly, ignoring their constructors.
     * This means that if there's a private field added to this class (say "config"), the previously persisted versions of this class can no longer
     * be correctly un-marshaled into the new version as they don't define the new field and the object ends up in an inconsistent state.
     *
     * @return the previously persisted version of the config object, default config, or the deprecated "order" object, converted to a "config" object.
     */
    private Config currentConfig() {
        if (creatingAFreshView()) {
            config = Config.defaultConfig();
        }
        else if (deserailisingFromAnOlderFormat()) {
            migrateFromOldToNewConfigFormat();
        }

        return config;
    }

    private boolean creatingAFreshView() {
        return config == null && order == null;
    }

    // Is config.xml saved in a format prior to version 1.6+build.150 of Build Monitor?
    private boolean deserailisingFromAnOlderFormat() {
        return config == null && order != null;
    }

    // If an older version of config.xml is loaded, "config" field is missing, but "order" is present
    private void migrateFromOldToNewConfigFormat() {
        Config c = new Config();
        c.setOrder(order);

        config = c;
        order = null;
    }

    @SuppressWarnings("unchecked")
    private Comparator<Job<?, ?>> orderIn(String requestedOrdering) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String packageName = this.getClass().getPackage().getName() + ".order.";

        return (Comparator<Job<?, ?>>) Class.forName(packageName + requestedOrdering).newInstance();
    }

    private Config config;

    @Deprecated // use Config instead
    private Comparator<Job<?, ?>> order;      // note: this field can be removed when people stop using versions prior to 1.6+build.150


    public void addNickname(){
        System.out.println("*********************NICKNAME ADDED*******************************");
    }

    public void printArray(){
        for(int i = 0; i < nicknameArray.length; ++i ){
            System.out.println("************" + nicknameArray[i] + "************");
        }
        System.out.println("__________________________________");
    }













}