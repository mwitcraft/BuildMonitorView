package com.smartcodeltd.jenkinsci.plugins.buildmonitor;

import com.google.common.base.Objects;
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.order.ByName;
import hudson.model.Job;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;

import static com.smartcodeltd.jenkinsci.plugins.buildmonitor.functions.NullSafety.getOrElse;

public class Config {

    private boolean displayCommitters;

    private Properties props = new Properties();

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
        props.setProperty("test", "test");

        return Objects.toStringHelper(this)
                .add("props", props.toString())
                .add("order", order.getClass().getSimpleName())
                .toString();
    }

    public void setProps(HashMap<String, String> map){
        props.clear();
        if(map != null){
            for(int i = 0; i < map.size(); ++i){
                props.put((String)map.keySet().toArray()[i], (String)map.values().toArray()[i]);
            }
        }
    }

    // --

    private Comparator<Job<?, ?>> order;
}