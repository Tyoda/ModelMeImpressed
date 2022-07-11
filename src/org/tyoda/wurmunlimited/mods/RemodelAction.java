package org.tyoda.wurmunlimited.mods;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.questions.ChangeModelQuestion;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;


public class RemodelAction implements ModAction, ActionPerformer, BehaviourProvider {
    private final short actionId = (short) ModActions.getNextActionId();
    private final ActionEntry actionEntry;

    public RemodelAction(){
        this.actionEntry = ActionEntry.createEntry(this.actionId, "Remodel", "remodeling", MiscConstants.EMPTY_INT_ARRAY);
        ModActions.registerAction(this.actionEntry);
    }
    @Override
    public short getActionId() {
        return this.actionId;
    }
    public ActionEntry getActionEntry(){
        return this.actionEntry;
    }

    public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
        return this.action(action, performer, target, num, counter);
    }

    public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
        return this.makeWindow(performer, target);
    }

    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        return this.action(action, performer, target, num, counter);
    }

    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return this.makeWindow(performer, target);
    }


    private boolean makeWindow(Creature performer, PermissionsPlayerList.ISettings target){
        if(target instanceof Item && ((Item)target).isBodyPart()) target = performer;
        if(performer instanceof Player && performer.getPower() >= ModelMeImpressed.options.getGmPowerNeeded()) {
            ChangeModelQuestion q = new ChangeModelQuestion(performer, "Model Me Impressed", "What model would you like to change it to?", performer.getWurmId(), target);
            q.sendQuestion();
            return true;
        }
        return false;
    }
}
