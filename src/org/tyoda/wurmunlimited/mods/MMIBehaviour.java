package org.tyoda.wurmunlimited.mods;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.TempItem;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;

import java.util.ArrayList;
import java.util.List;

public class MMIBehaviour implements BehaviourProvider, ModAction {
    public MMIBehaviour() {
    }

    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Creature target) {
        return this.getBehavioursFor(performer, target);
    }

    public List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
        if(performer.getPower() < ModelMeImpressed.options.getGmPowerNeeded()) return null;
        return getMyBehaviours(performer);
    }

    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        return this.getBehavioursFor(performer, target);
    }

    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        if(performer.getPower() < ModelMeImpressed.options.getGmPowerNeeded() || (target instanceof TempItem && !(target.isBodyPart()))) return null;
        return getMyBehaviours(performer);
    }

    private List<ActionEntry> getMyBehaviours(Creature performer) {
        List<ActionEntry> list = new ArrayList<>();
        MMIOptions options = ModelMeImpressed.options;
        short menuItems = 0;
        if (performer.getPower() > ModelMeImpressed.options.getGmPowerNeeded()) {
            menuItems = -3;
            list.add(options.getRemodelAction().getActionEntry());
            list.add(options.getRandomModelAction().getActionEntry());
            list.add(options.getResetModelAction().getActionEntry());
        }

        if (menuItems < 0) {
            list.add(0, new ActionEntry(menuItems, "Remodel", "Remodel"));
        }

        if (list.isEmpty()) {
            list = null;
        }

        return list;
    }
}
