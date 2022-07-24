package org.tyoda.wurmunlimited.mods.ModelMeImpressed;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class ResetModelAction implements ModAction, ActionPerformer {
    private final short actionId = (short) ModActions.getNextActionId();
    private final ActionEntry actionEntry;

    public ResetModelAction(){
        this.actionEntry = ActionEntry.createEntry(this.actionId, "Reset model", "resetting model", MiscConstants.EMPTY_INT_ARRAY);
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
        return this.reset(performer, target);
    }

    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        return this.action(action, performer, target, num, counter);
    }

    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return this.reset(performer, target);
    }

    private boolean reset(Creature performer, PermissionsPlayerList.ISettings target){
        if(target instanceof Item && ((Item)target).isBodyPart()) target = performer;
        if(performer instanceof Player && performer.getPower() >= ModelMeImpressed.options.getGmPowerNeeded()) {
            ModelMeImpressed.deleteEntry(target.getWurmId());
            return true;
        }
        return false;
    }
}
