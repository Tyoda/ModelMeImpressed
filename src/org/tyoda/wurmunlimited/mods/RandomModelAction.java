package org.tyoda.wurmunlimited.mods;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.ArrayList;
import java.util.Random;

public class RandomModelAction implements ModAction, ActionPerformer, BehaviourProvider {
    private final short actionId = (short) ModActions.getNextActionId();
    private final ActionEntry actionEntry;

    public RandomModelAction(){
        this.actionEntry = ActionEntry.createEntry(this.actionId, "Give random model", "giving random model", MiscConstants.EMPTY_INT_ARRAY);
        ModActions.registerAction(this.actionEntry);
    }
    @Override
    public short getActionId() {
        return this.actionId;
    }
    public ActionEntry getActionEntry() {
        return this.actionEntry;
    }

    public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
        return this.action(action, performer, target, num, counter);
    }

    public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
        return this.random(performer, target);
    }

    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        return this.action(action, performer, target, num, counter);
    }

    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return this.random(performer, target);
    }

    private boolean random(Creature performer, PermissionsPlayerList.ISettings target){
        if(target instanceof Item && ((Item)target).isBodyPart()) target = performer;
        if(performer instanceof Player && performer.getPower() >= ModelMeImpressed.options.getGmPowerNeeded()){
            String modelName = getRandomModelName();
            if(modelName != null){
                ModelMeImpressed.setCustomModel(target, modelName);
                return true;
            }
        }
        return false;
    }

    private String getRandomModelName(){
        Random random = new Random();

        ArrayList<String> list = new ArrayList<>();
        MMIOptions options = ModelMeImpressed.options;
        if(options.structureMM.size() > 0)  list.add(options.structureMM.get(random.nextInt(options.structureMM.size())));
        if(options.armourMM.size() > 0)     list.add(options.armourMM.get(random.nextInt(options.armourMM.size())));
        if(options.containerMM.size() > 0)  list.add(options.containerMM.get(random.nextInt(options.containerMM.size())));
        if(options.corpseMM.size() > 0)     list.add(options.corpseMM.get(random.nextInt(options.corpseMM.size())));
        if(options.creatureMM.size() > 0)   list.add(options.creatureMM.get(random.nextInt(options.creatureMM.size())));
        if(options.decorationMM.size() > 0) list.add(options.decorationMM.get(random.nextInt(options.decorationMM.size())));
        if(options.pileMM.size() > 0)       list.add(options.pileMM.get(random.nextInt(options.pileMM.size())));
        if(options.resourceMM.size() > 0)   list.add(options.resourceMM.get(random.nextInt(options.resourceMM.size())));
        if(options.toolMM.size() > 0)       list.add(options.toolMM.get(random.nextInt(options.toolMM.size())));
        if(options.weaponMM.size() > 0)     list.add(options.weaponMM.get(random.nextInt(options.weaponMM.size())));
        if(options.tutorialMM.size() > 0)   list.add(options.tutorialMM.get(random.nextInt(options.tutorialMM.size())));
        if(options.othersMM.size() > 0)     list.add(options.othersMM.get(random.nextInt(options.othersMM.size())));
        if(options.customMM.size() > 0)     list.add(options.customMM.get(random.nextInt(options.customMM.size())));

        String modelname = null;
        if(list.size() > 0) {
            modelname = list.get(random.nextInt(list.size()));
        }
        ModelMeImpressed.logger.info("modelname: "+modelname);
        return modelname;
    }
}
