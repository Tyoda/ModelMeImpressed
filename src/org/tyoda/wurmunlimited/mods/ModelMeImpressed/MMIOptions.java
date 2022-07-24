package org.tyoda.wurmunlimited.mods.ModelMeImpressed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class MMIOptions {
    private int gmPowerNeeded = 2;
    private boolean keepCustomModelInMemory = true;
    private boolean relogPlayers = true;
    private RemodelAction remodelAction = null;
    private ResetModelAction resetModelAction = null;
    private RandomModelAction randomModelAction = null;
    private BrowseModelsAction browseModelsAction = null;
    private MMIBehaviour mmiBehaviour = null;
    public HashMap<Long, String> customModels = null;
    public ArrayList<String> structurePM = new ArrayList<>();
    public ArrayList<String> structureMM = new ArrayList<>();
    public ArrayList<String> armourPM = new ArrayList<>();
    public ArrayList<String> armourMM = new ArrayList<>();
    public ArrayList<String> containerPM = new ArrayList<>();
    public ArrayList<String> containerMM = new ArrayList<>();
    public ArrayList<String> corpsePM = new ArrayList<>();
    public ArrayList<String> corpseMM = new ArrayList<>();
    public ArrayList<String> creaturePM = new ArrayList<>();
    public ArrayList<String> creatureMM = new ArrayList<>();
    public ArrayList<String> decorationPM = new ArrayList<>();
    public ArrayList<String> decorationMM = new ArrayList<>();
    public ArrayList<String> pilePM = new ArrayList<>();
    public ArrayList<String> pileMM = new ArrayList<>();
    public ArrayList<String> resourcePM = new ArrayList<>();
    public ArrayList<String> resourceMM = new ArrayList<>();
    public ArrayList<String> toolPM = new ArrayList<>();
    public ArrayList<String> toolMM = new ArrayList<>();
    public ArrayList<String> weaponPM = new ArrayList<>();
    public ArrayList<String> weaponMM = new ArrayList<>();
    public ArrayList<String> tutorialPM = new ArrayList<>();
    public ArrayList<String> tutorialMM = new ArrayList<>();
    public ArrayList<String> othersPM = new ArrayList<>();
    public ArrayList<String> othersMM = new ArrayList<>();
    public ArrayList<String> customPM = new ArrayList<>();
    public ArrayList<String> customMM = new ArrayList<>();
    public boolean isKeepCustomModelInMemory() {
        return keepCustomModelInMemory;
    }
    public int getGmPowerNeeded() {
        return gmPowerNeeded;
    }

    public boolean isRelogPlayers() {
        return relogPlayers;
    }

    public RemodelAction getRemodelAction() {
        return remodelAction;
    }

    public void setRemodelAction(RemodelAction remodelAction) {
        this.remodelAction = remodelAction;
    }

    public ResetModelAction getResetModelAction() {
        return resetModelAction;
    }

    public void setResetModelAction(ResetModelAction resetModelAction) {
        this.resetModelAction = resetModelAction;
    }

    public RandomModelAction getRandomModelAction() {
        return randomModelAction;
    }

    public void setRandomModelAction(RandomModelAction randomModelAction) {
        this.randomModelAction = randomModelAction;
    }

    public MMIBehaviour getMmiBehaviour() {
        return mmiBehaviour;
    }

    public void setMmiBehaviour(MMIBehaviour mmiBehaviour) {
        this.mmiBehaviour = mmiBehaviour;
    }

    public void configure(Properties p){
        ModelMeImpressed.logger.info("Starting config");

        gmPowerNeeded = Integer.parseInt(p.getProperty("gmPowerNeeded", "2"));

        keepCustomModelInMemory = Boolean.parseBoolean(p.getProperty("keepCustomModelInMemory", "true"));

        relogPlayers = Boolean.parseBoolean(p.getProperty("relogPlayers", "true"));

        configureHashMap(p, "custom",     customPM,     customMM);

        configureHashMap(p, "structure",  structurePM,  structureMM );
        configureHashMap(p, "armour",     armourPM,     armourMM );
        configureHashMap(p, "container",  containerPM,  containerMM );
        configureHashMap(p, "corpse",     corpsePM,     corpseMM );
        configureHashMap(p, "creature",   creaturePM,   creatureMM );
        configureHashMap(p, "decoration", decorationPM, decorationMM );
        configureHashMap(p, "pile",       pilePM,       pileMM );
        configureHashMap(p, "resource",   resourcePM,   resourceMM );
        configureHashMap(p, "tool",       toolPM,       toolMM );
        configureHashMap(p, "weapon",     weaponPM,     weaponMM );
        configureHashMap(p, "tutorial",   tutorialPM,   tutorialMM );
        configureHashMap(p, "others",     othersPM,     othersMM );
        ModelMeImpressed.logger.info("Done with config");
    }
    private void configureHashMap(Properties p, String name, ArrayList<String> pretty, ArrayList<String> model){
        String pString = p.getProperty(name + "_pretty");
        String mString = p.getProperty(name + "_models");
        if(pString.equals("") || mString.equals("")) return;

        String[] parr = pString.split(",");
        String[] marr = mString.split(",");
        for(int i = 0; i < marr.length; ++i){
            pretty.add(parr[i]);
            model.add(marr[i]);
        }
    }

    public BrowseModelsAction getBrowseModelsAction() {
        return browseModelsAction;
    }

    public void setBrowseModelsAction(BrowseModelsAction browseModelsAction) {
        this.browseModelsAction = browseModelsAction;
    }
}
