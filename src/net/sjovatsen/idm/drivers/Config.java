/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sjovatsen.idm.drivers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sjovatsen.idm.drivers.ddt.TracerDSTrace;

/**
 *
 * @author Frode Sjovatsen <frode @ sjovatsen.net>
 */
public class Config {

    private Map<String, String> configMap; // = new HashMap<String, String>();
    private String configString;
    private static final String DEFAULT_VALUE = "na";

    /**
     * Default constructor
     */
    public Config() {

        this.configMap = new HashMap<String, String>();
        this.configString = null;

    }

    /**
     * 
     * @param configString
     */
    public Config(String configString) {

        this.configMap = new HashMap<String, String>();
        this.configString = configString;

        parseConfigString();
    }

    /**
     * 
     * @param configString
     */
    public void setConfigString(String configString) {

        this.configString = configString;
        parseConfigString();

    }

    /**
     * 
     * @param key
     * @return
     */
    public String get(String key) {
        return (configMap.containsKey(key)) ? configMap.get(key) : DEFAULT_VALUE;
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        configMap.put(key, value);
    }

    /**
     * 
     */
    private void parseConfigString() {

        String[] configList = configString.split(" ");
        String[] keyValue = null;

        for (int i = 0; i < configList.length; i++) {
            keyValue = configList[i].split("=");
            set(keyValue[0], keyValue[1]);
        }

    }

    /**
     *
     * @return
     */
    public void dumpConfig(TracerDSTrace dstrace) {

        Iterator it = configMap.entrySet().iterator();
        Map.Entry pairs = null;

        dstrace.traceMessage(" Extention config:");
        while (it.hasNext()) {
            pairs = (Map.Entry) it.next();
            dstrace.traceMessage("   " + pairs.getKey() + " = " + pairs.getValue());
        }

    }
}