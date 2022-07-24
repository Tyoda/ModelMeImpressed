package org.tyoda.wurmunlimited.mods.ModelMeImpressed;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.TempItem;
import com.wurmonline.server.players.PermissionsPlayerList;
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
        return getMyBehaviours(performer, target);
    }

    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        return this.getBehavioursFor(performer, target);
    }

    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        if(performer.getPower() < ModelMeImpressed.options.getGmPowerNeeded() || (target instanceof TempItem && !(target.isBodyPart()))) return null;
        return getMyBehaviours(performer, target);
    }

    private List<ActionEntry> getMyBehaviours(Creature performer, PermissionsPlayerList.ISettings target) {
        List<ActionEntry> list = new ArrayList<>();
        MMIOptions options = ModelMeImpressed.options;
        if (performer.getPower() > ModelMeImpressed.options.getGmPowerNeeded()) {
            if (options.getRemodelAction() != null) list.add(options.getRemodelAction().getActionEntry());
            if (options.getRandomModelAction() != null) list.add(options.getRandomModelAction().getActionEntry());
            if (options.getBrowseModelsAction() != null) list.add(options.getBrowseModelsAction().getActionEntry());
            if (options.getResetModelAction() != null && ModelMeImpressed.getCustomModel(target) != null) list.add(options.getResetModelAction().getActionEntry());
        }

        short menuItems = (short)(list.size() * -1);
        if (menuItems < 0) {
            list.add(0, new ActionEntry(menuItems, "Remodel", "Remodel"));
        } else {
            list = null;
        }

        return list;
    }
}
