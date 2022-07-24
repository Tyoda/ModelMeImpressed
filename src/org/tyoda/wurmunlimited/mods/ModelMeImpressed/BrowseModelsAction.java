//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.tyoda.wurmunlimited.mods.ModelMeImpressed;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.questions.BrowseModelsQuestion;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class BrowseModelsAction implements ModAction, ActionPerformer, BehaviourProvider {
    private final short actionId = (short)ModActions.getNextActionId();
    private final ActionEntry actionEntry;

    public BrowseModelsAction() {
        this.actionEntry = ActionEntry.createEntry(this.actionId, "Browse", "browsing models", MiscConstants.EMPTY_INT_ARRAY);
        ModActions.registerAction(this.actionEntry);
    }

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
        return this.makeWindow(performer);
    }

    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        return this.action(action, performer, target, num, counter);
    }

    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return this.makeWindow(performer);
    }

    private boolean makeWindow(Creature performer) {
        if (performer instanceof Player && performer.getPower() >= ModelMeImpressed.options.getGmPowerNeeded()) {
            BrowseModelsQuestion q = new BrowseModelsQuestion(performer, "Browse Models", "No questions.");
            q.sendQuestion();
            return true;
        } else {
            return false;
        }
    }
}
