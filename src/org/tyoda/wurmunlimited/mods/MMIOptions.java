package org.tyoda.wurmunlimited.mods;

import java.util.HashMap;
import java.util.Properties;

public class MMIOptions {
    private static int gmPowerNeeded = 2;
    private static boolean keepCustomModelInMemory = true;
    private static boolean relogPlayers = true;
    public static HashMap<Long, String> customModels = null;
    public static HashMap<Integer, String> structurePM = new HashMap<>();
    public static HashMap<Integer, String> structureMM = new HashMap<>();
    public static HashMap<Integer, String> armourPM = new HashMap<>();
    public static HashMap<Integer, String> armourMM = new HashMap<>();
    public static HashMap<Integer, String> containerPM = new HashMap<>();
    public static HashMap<Integer, String> containerMM = new HashMap<>();
    public static HashMap<Integer, String> corpsePM = new HashMap<>();
    public static HashMap<Integer, String> corpseMM = new HashMap<>();
    public static HashMap<Integer, String> creaturePM = new HashMap<>();
    public static HashMap<Integer, String> creatureMM = new HashMap<>();
    public static HashMap<Integer, String> decorationPM = new HashMap<>();
    public static HashMap<Integer, String> decorationMM = new HashMap<>();
    public static HashMap<Integer, String> pilePM = new HashMap<>();
    public static HashMap<Integer, String> pileMM = new HashMap<>();
    public static HashMap<Integer, String> resourcePM = new HashMap<>();
    public static HashMap<Integer, String> resourceMM = new HashMap<>();
    public static HashMap<Integer, String> toolPM = new HashMap<>();
    public static HashMap<Integer, String> toolMM = new HashMap<>();
    public static HashMap<Integer, String> weaponPM = new HashMap<>();
    public static HashMap<Integer, String> weaponMM = new HashMap<>();
    public static HashMap<Integer, String> tutorialPM = new HashMap<>();
    public static HashMap<Integer, String> tutorialMM = new HashMap<>();
    public static HashMap<Integer, String> othersPM = new HashMap<>();
    public static HashMap<Integer, String> othersMM = new HashMap<>();
    public static HashMap<Integer, String> customMM = new HashMap<>();
    public static HashMap<Integer, String> customPM = new HashMap<>();
    public static boolean isKeepCustomModelInMemory() {
        return keepCustomModelInMemory;
    }
    public static int getGmPowerNeeded() {
        return gmPowerNeeded;
    }

    public static boolean isRelogPlayers() {
        return relogPlayers;
    }

    public static void setRelogPlayers(boolean relogPlayers) {
        MMIOptions.relogPlayers = relogPlayers;
    }

    public void configure(Properties p){
        ModelMeImpressed.logger.info("Starting config");

        gmPowerNeeded = Integer.parseInt(p.getProperty("gmPowerNeeded", "2"));

        keepCustomModelInMemory = Boolean.parseBoolean(p.getProperty("keepCustomModelInMemory", "true"));

        relogPlayers = Boolean.parseBoolean(p.getProperty("relogPlayers", "true"));

        configureHashMap(p, "custom",     MMIOptions.customPM,     MMIOptions.customMM);

        configureHashMap(p, "structure",  MMIOptions.structurePM,  MMIOptions.structureMM );
        configureHashMap(p, "armour",     MMIOptions.armourPM,     MMIOptions.armourMM );
        configureHashMap(p, "container",  MMIOptions.containerPM,  MMIOptions.containerMM );
        configureHashMap(p, "corpse",     MMIOptions.corpsePM,     MMIOptions.corpseMM );
        configureHashMap(p, "creature",   MMIOptions.creaturePM,   MMIOptions.creatureMM );
        configureHashMap(p, "decoration", MMIOptions.decorationPM, MMIOptions.decorationMM );
        configureHashMap(p, "pile",       MMIOptions.pilePM,       MMIOptions.pileMM );
        configureHashMap(p, "resource",   MMIOptions.resourcePM,   MMIOptions.resourceMM );
        configureHashMap(p, "tool",       MMIOptions.toolPM,       MMIOptions.toolMM );
        configureHashMap(p, "weapon",     MMIOptions.weaponPM,     MMIOptions.weaponMM );
        configureHashMap(p, "tutorial",   MMIOptions.tutorialPM,   MMIOptions.tutorialMM );
        configureHashMap(p, "others",     MMIOptions.othersPM,     MMIOptions.othersMM );
        ModelMeImpressed.logger.info("Done with config");
    }
    private void configureHashMap(Properties p, String name, HashMap<Integer, String> prettyMap, HashMap<Integer, String> modelMap){
        String pString = p.getProperty(name + "_pretty");
        String mString = p.getProperty(name + "_models");
        if(pString.equals("") || mString.equals("")) return;

        String[] parr = pString.split(",");
        String[] marr = mString.split(",");
        for(int i = 0; i < marr.length; ++i){
            prettyMap.put(i, parr[i]);
            modelMap.put(i, marr[i]);
        }
    }

}
