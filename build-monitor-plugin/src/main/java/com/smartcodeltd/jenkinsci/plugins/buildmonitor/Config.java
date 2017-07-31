package com.smartcodeltd.jenkinsci.plugins.buildmonitor;

import com.google.common.base.Objects;
import com.mongodb.util.Hash;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.order.ByName;
import hudson.model.Job;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;

import static com.smartcodeltd.jenkinsci.plugins.buildmonitor.functions.NullSafety.getOrElse;

public class Config {

    private boolean displayCommitters;

    private Properties renameFilters = new Properties();

    public static Config defaultConfig() {
        return new Config();
    }

    public Comparator<Job<?, ?>> getOrder() {
        /*
         * Jenkins unmarshals objects from config.xml by setting their private fields directly and without invoking their constructors.
         * In order to retrieve a potentially already persisted field try to first get the field, if that didn't work - use defaults.
         *
         * This is defensive coding to avoid issues such as this one:
         *  https://github.com/jan-molak/jenkins-build-monitor-plugin/issues/43
         */

        return getOrElse(order, new ByName());
    }

    public void setOrder(Comparator<Job<?, ?>> order) {
        this.order = order;
    }

    public boolean shouldDisplayCommitters() {
        return getOrElse(displayCommitters, true);
    }

    public void setDisplayCommitters(boolean flag) {
        this.displayCommitters = flag;
    }

    @Override
    public String toString() {
        this.renameFilters.setProperty("test", "test");

        return Objects.toStringHelper(this)
                .add("renameFilters", this.renameFilters.toString())
                .add("order", order.getClass().getSimpleName())
                .toString();
    }

    public void setRenameFilters(HashMap<String, String> map){
        this.renameFilters.clear();
        if(map != null){
            for(int i = 0; i < map.size(); ++i){
                this.renameFilters.put((String)map.keySet().toArray()[i], (String)map.values().toArray()[i]);
            }
        }
    }

    public HashMap<String, String> getRenameFilters(){
        HashMap<String, String> map = new HashMap<String, String>();
        for(int i = 0; i < renameFilters.size(); ++i){
            map.put((String)this.renameFilters.keySet().toArray()[i], (String)this.renameFilters.values().toArray()[i]);
        }
        return map;
    }

    // --

    private Comparator<Job<?, ?>> order;
}