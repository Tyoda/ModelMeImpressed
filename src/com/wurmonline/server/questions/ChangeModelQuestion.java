package com.wurmonline.server.questions;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PermissionsPlayerList;
import net.coldie.tools.BmlForm;
import org.tyoda.wurmunlimited.mods.ModelMeImpressed.MMIOptions;
import org.tyoda.wurmunlimited.mods.ModelMeImpressed.ModelMeImpressed;

import java.util.ArrayList;
import java.util.Properties;

public class ChangeModelQuestion extends Question {

    private final PermissionsPlayerList.ISettings target;
    private final String targetname;
    public ChangeModelQuestion(Creature aResponder, String aTitle, String aQuestion, long aTarget, PermissionsPlayerList.ISettings target){
        super(aResponder, aTitle, aQuestion, 79, aTarget);
        this.target = target;
        if(target instanceof Creature){
            targetname = ((Creature)target).getModelName();
        }else if(target instanceof Item){
            targetname = ((Item)target).getModelName();
        }else{
            targetname = "current model name not found";
        }
    }

    public void answer(Properties answer) {
        //for(String e : answer.stringPropertyNames()) ModelMeImpressed.logger.info(e + ": " + answer.getProperty(e));
        MMIOptions options = ModelMeImpressed.options;

        String modelName = null;
        if(answer.containsKey("submitString") && Boolean.parseBoolean(answer.getProperty("submitString")))
            modelName = answer.getProperty("textfield");
        else if(answer.containsKey("submitCreatures") && Boolean.parseBoolean(answer.getProperty("submitCreatures")))
            modelName = options.creatureMM.get(Integer.parseInt(answer.getProperty("creaturesNumber")));
        else if(answer.containsKey("submitDecorations") && Boolean.parseBoolean(answer.getProperty("submitDecorations")))
            modelName = options.decorationMM.get(Integer.parseInt(answer.getProperty("decorationsNumber")));
        else if(answer.containsKey("submitOthers") && Boolean.parseBoolean(answer.getProperty("submitOthers")))
            modelName = options.othersMM.get(Integer.parseInt(answer.getProperty("othersNumber")));
        else if(answer.containsKey("submitContainers") && Boolean.parseBoolean(answer.getProperty("submitContainers")))
            modelName = options.containerMM.get(Integer.parseInt(answer.getProperty("containersNumber")));
        else if(answer.containsKey("submitWeapons") && Boolean.parseBoolean(answer.getProperty("submitWeapons")))
            modelName = options.weaponMM.get(Integer.parseInt(answer.getProperty("weaponsNumber")));
        else if(answer.containsKey("submitArmour") && Boolean.parseBoolean(answer.getProperty("submitArmour")))
            modelName = options.armourMM.get(Integer.parseInt(answer.getProperty("armourNumber")));
        else if(answer.containsKey("submitCorpses") && Boolean.parseBoolean(answer.getProperty("submitCorpses")))
            modelName = options.corpseMM.get(Integer.parseInt(answer.getProperty("corpsesNumber")));
        else if(answer.containsKey("submitTools") && Boolean.parseBoolean(answer.getProperty("submitTools")))
            modelName = options.toolMM.get(Integer.parseInt(answer.getProperty("toolsNumber")));
        else if(answer.containsKey("submitTutorial") && Boolean.parseBoolean(answer.getProperty("submitTutorial")))
            modelName = options.tutorialMM.get(Integer.parseInt(answer.getProperty("tutorialNumber")));
        else if(answer.containsKey("submitResources") && Boolean.parseBoolean(answer.getProperty("submitResources")))
            modelName = options.resourceMM.get(Integer.parseInt(answer.getProperty("resourcesNumber")));
        else if(answer.containsKey("submitPiles") && Boolean.parseBoolean(answer.getProperty("submitPiles")))
            modelName = options.pileMM.get(Integer.parseInt(answer.getProperty("pilesNumber")));
        else if(answer.containsKey("submitStructures") && Boolean.parseBoolean(answer.getProperty("submitStructures")))
            modelName = options.structureMM.get(Integer.parseInt(answer.getProperty("structuresNumber")));
        else if(answer.containsKey("submitCustom") && Boolean.parseBoolean(answer.getProperty("submitCustom")))
            modelName = options.structureMM.get(Integer.parseInt(answer.getProperty("customNumber")));

        if(modelName == null) throw new RuntimeException("String was null during model change in mod ModelMeImpressed");
        ModelMeImpressed.logger.info("Applying model " + modelName + " to " + target.getTypeName());
        ModelMeImpressed.setCustomModel(target, modelName);
    }

    public void sendQuestion() {
        BmlForm f = new BmlForm("");
        f.addHidden("id", String.valueOf(this.id));
        MMIOptions options = ModelMeImpressed.options;
        f.addText("Hello there, fellow person!");
        f.addText("");
        f.addText("Please choose which model you wish to set this Creature/item to be!");
        f.addText("");

        f.addText("");
        f.addText("");
        f.addLabel("Write the model name:");
        f.addInput("textfield", 256, targetname);
        f.beginHorizontalFlow();
        f.addButton("Accept", "submitString");
        f.endHorizontalFlow();

        f.addText("Or Choose from the lists:");
        f.addText("");
        if(options.creaturePM.size() > 0)    addDropdown(f, "submitCreatures", "Creatures", "creaturesNumber", options.creaturePM);
        if(options.decorationPM.size() > 0)  addDropdown(f, "submitDecorations", "Decorations", "decorationsNumber", options.decorationPM);
        if(options.othersPM.size() > 0)      addDropdown(f, "submitOthers", "Misc", "othersNumber", options.othersPM);
        if(options.containerPM.size() > 0)   addDropdown(f, "submitContainers", "Containers", "containersNumber", options.containerPM);
        if(options.weaponPM.size() > 0)      addDropdown(f, "submitWeapons", "Weapons", "weaponsNumber", options.weaponPM);
        if(options.armourPM.size() > 0)      addDropdown(f, "submitArmour", "Armour", "armourNumber", options.armourPM);
        if(options.corpsePM.size() > 0)      addDropdown(f, "submitCorpses", "Corpses", "corpsesNumber", options.corpsePM);
        if(options.toolPM.size() > 0)        addDropdown(f, "submitTools", "Tools", "toolsNumber", options.toolPM);
        if(options.tutorialPM.size() > 0)    addDropdown(f, "submitTutorial", "Tutorial", "tutorialNumber", options.tutorialPM);
        if(options.resourcePM.size() > 0)    addDropdown(f, "submitResources", "Resources", "resourcesNumber", options.resourcePM);
        if(options.pilePM.size() > 0)        addDropdown(f, "submitPiles", "Piles", "pilesNumber", options.pilePM);
        if(options.structurePM.size() > 0)   addDropdown(f, "submitStructures", "Structures", "structuresNumber", options.structurePM);
        if(options.customPM.size() > 0)      addDropdown(f, "submitCustom", "Custom", "customNumber", options.customPM);
        this.getResponder().getCommunicator().sendBml(400, 500, true, true, f.toString(), 150, 150, 200, this.title);
    }

    private void addDropdown(BmlForm f, String submit, String text, String dropdownID, ArrayList<String> list){
        f.addRaw("harray{label{text='" + text + ":'}dropdown{id='" + dropdownID + "';options='");
        f.addRaw(this.getXOptions(list));
        f.addRaw("'}}");
        f.beginHorizontalFlow();
        f.addButton("Accept", submit);
        f.endHorizontalFlow();
        f.addText(""); // end
    }

    private String getXOptions(ArrayList<String> list){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < list.size(); ++i){
            if(i != 0) sb.append(',');
            sb.append(list.get(i));
        }

        return sb.toString();
    }

}
