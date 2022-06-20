package com.wurmonline.server.questions;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.PermissionsPlayerList;
import net.coldie.tools.BmlForm;
import org.tyoda.wurmunlimited.mods.MMIOptions;
import org.tyoda.wurmunlimited.mods.ModelMeImpressed;

import java.util.HashMap;
import java.util.Properties;

public class ChangeModelQuestion extends Question {

    private final PermissionsPlayerList.ISettings target;
    public ChangeModelQuestion(Creature aResponder, String aTitle, String aQuestion, long aTarget, PermissionsPlayerList.ISettings target){
        super(aResponder, aTitle, aQuestion, 79, aTarget);
        this.target = target;
    }

    public void answer(Properties answer) {
        /*
        for(String e : answer.stringPropertyNames())
            ModelMeImpressed.logger.info(e + ": " + answer.getProperty(e));*/

        String modelName = null;
        if(answer.containsKey("submitCreatures")   && Boolean.parseBoolean(answer.getProperty("submitCreatures")))          modelName = MMIOptions.creatureMM.get(Integer.parseInt(answer.getProperty("creaturesNumber")));
        else if(answer.containsKey("submitDecorations") && Boolean.parseBoolean(answer.getProperty("submitDecorations")))   modelName = MMIOptions.decorationMM.get(Integer.parseInt(answer.getProperty("decorationsNumber")));
        else if(answer.containsKey("submitOthers") && Boolean.parseBoolean(answer.getProperty("submitOthers")))             modelName = MMIOptions.othersMM.get(Integer.parseInt(answer.getProperty("othersNumber")));
        else if(answer.containsKey("submitContainers") && Boolean.parseBoolean(answer.getProperty("submitContainers")))     modelName = MMIOptions.containerMM.get(Integer.parseInt(answer.getProperty("containersNumber")));
        else if(answer.containsKey("submitWeapons") && Boolean.parseBoolean(answer.getProperty("submitWeapons")))           modelName = MMIOptions.weaponMM.get(Integer.parseInt(answer.getProperty("weaponsNumber")));
        else if(answer.containsKey("submitArmour") && Boolean.parseBoolean(answer.getProperty("submitArmour")))             modelName = MMIOptions.armourMM.get(Integer.parseInt(answer.getProperty("armourNumber")));
        else if(answer.containsKey("submitCorpses") && Boolean.parseBoolean(answer.getProperty("submitCorpses")))           modelName = MMIOptions.corpseMM.get(Integer.parseInt(answer.getProperty("corpsesNumber")));
        else if(answer.containsKey("submitTools") && Boolean.parseBoolean(answer.getProperty("submitTools")))               modelName = MMIOptions.toolMM.get(Integer.parseInt(answer.getProperty("toolsNumber")));
        else if(answer.containsKey("submitTutorial") && Boolean.parseBoolean(answer.getProperty("submitTutorial")))         modelName = MMIOptions.tutorialMM.get(Integer.parseInt(answer.getProperty("tutorialNumber")));
        else if(answer.containsKey("submitResources") && Boolean.parseBoolean(answer.getProperty("submitResources")))       modelName = MMIOptions.resourceMM.get(Integer.parseInt(answer.getProperty("resourcesNumber")));
        else if(answer.containsKey("submitPiles") && Boolean.parseBoolean(answer.getProperty("submitPiles")))               modelName = MMIOptions.pileMM.get(Integer.parseInt(answer.getProperty("pilesNumber")));
        else if(answer.containsKey("submitStructures") && Boolean.parseBoolean(answer.getProperty("submitStructures")))     modelName = MMIOptions.structureMM.get(Integer.parseInt(answer.getProperty("structuresNumber")));
        else if(answer.containsKey("submitCustom") && Boolean.parseBoolean(answer.getProperty("submitCustom")))             modelName = MMIOptions.structureMM.get(Integer.parseInt(answer.getProperty("customNumber")));
        if(modelName == null) throw new RuntimeException("String was null during model change in mod ModelMeImpressed");
        ModelMeImpressed.logger.info("Applying model " + modelName + " to " + target.getTypeName());
        ModelMeImpressed.setCustomModel(target, modelName);
    }

    public void sendQuestion() {
        BmlForm f = new BmlForm("");
        f.addHidden("id", String.valueOf(this.id));

        f.addText("Hello there, fellow person!");
        f.addText("");
        f.addText("Please choose which model you wish to set this Creature/item to be!");
        f.addText("");
        if(MMIOptions.creaturePM.size() > 0)    addDropdown(f, "submitCreatures", "Creatures", "creaturesNumber", MMIOptions.creaturePM);
        if(MMIOptions.decorationPM.size() > 0)  addDropdown(f, "submitDecorations", "Decorations", "decorationsNumber", MMIOptions.decorationPM);
        if(MMIOptions.othersPM.size() > 0)      addDropdown(f, "submitOthers", "Misc", "othersNumber", MMIOptions.othersPM);
        if(MMIOptions.containerPM.size() > 0)   addDropdown(f, "submitContainers", "Containers", "containersNumber", MMIOptions.containerPM);
        if(MMIOptions.weaponPM.size() > 0)      addDropdown(f, "submitWeapons", "Weapons", "weaponsNumber", MMIOptions.weaponPM);
        if(MMIOptions.armourPM.size() > 0)      addDropdown(f, "submitArmour", "Armour", "armourNumber", MMIOptions.armourPM);
        if(MMIOptions.corpsePM.size() > 0)      addDropdown(f, "submitCorpses", "Corpses", "corpsesNumber", MMIOptions.corpsePM);
        if(MMIOptions.toolPM.size() > 0)        addDropdown(f, "submitTools", "Tools", "toolsNumber", MMIOptions.toolPM);
        if(MMIOptions.tutorialPM.size() > 0)    addDropdown(f, "submitTutorial", "Tutorial", "tutorialNumber", MMIOptions.tutorialPM);
        if(MMIOptions.resourcePM.size() > 0)    addDropdown(f, "submitResources", "Resources", "resourcesNumber", MMIOptions.resourcePM);
        if(MMIOptions.pilePM.size() > 0)        addDropdown(f, "submitPiles", "Piles", "pilesNumber", MMIOptions.pilePM);
        if(MMIOptions.structurePM.size() > 0)   addDropdown(f, "submitStructures", "Structures", "structuresNumber", MMIOptions.structurePM);
        if(MMIOptions.customPM.size() > 0)      addDropdown(f, "submitCustom", "Custom", "customNumber", MMIOptions.customPM);
        this.getResponder().getCommunicator().sendBml(400, 500, true, true, f.toString(), 150, 150, 200, this.title);
    }

    private void addDropdown(BmlForm f, String submit, String text, String dropdownID, HashMap<Integer, String> map){
        f.addRaw("harray{label{text='" + text + ":'}dropdown{id='" + dropdownID + "';options='");
        f.addRaw(this.getXOptions(map));
        f.addRaw("'}}");
        f.beginHorizontalFlow();
        f.addButton("Accept", submit);
        f.endHorizontalFlow();
        f.addText(""); // end
    }

    private String getXOptions(HashMap<Integer, String> map){
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for(int i = 0; i < map.size(); ++i){
            if(first) first = false;
            else sb.append(',');
            sb.append(map.get(i));
        }

        return sb.toString();
    }

}
