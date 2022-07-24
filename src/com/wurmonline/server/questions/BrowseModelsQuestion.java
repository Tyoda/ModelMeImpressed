//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wurmonline.server.questions;

import com.wurmonline.server.DbConnector;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.utils.DbUtilities;
import net.coldie.tools.BmlForm;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.tyoda.wurmunlimited.mods.ModelMeImpressed.ModelMeImpressed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BrowseModelsQuestion extends Question {
    public BrowseModelsQuestion(Creature aResponder, String aTitle, String aQuestion) {
        super(aResponder, aTitle, aQuestion, 79, aResponder.getWurmId());
    }

    public void answer(Properties answer) {
        if (answer.containsKey("accept") && Boolean.parseBoolean(answer.getProperty("accept", "false"))) {
            for (Object key : answer.keySet()) {
                if (key instanceof String && ((String) key).startsWith("object") && Boolean.parseBoolean(answer.getProperty((String) key, "false"))) {
                    long wurmId = Long.parseLong(((String) key).substring(6));
                    ModelMeImpressed.deleteEntry(wurmId);
                    ModelMeImpressed.logger.info("Deleting custom entry for id: " + wurmId);
                }
            }
        }

    }

    public void sendQuestion() {
        BmlForm f = new BmlForm("");
        f.addHidden("id", String.valueOf(this.id));
        f.addText("Here you can see each remodeled item's name and their current model.");
        f.addText("You can tick the checkbox next to each entry to have that item's model reset.");
        f.addText("");
        HashMap<PermissionsPlayerList.ISettings, String> entries = new HashMap<>();
        Connection db = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM MMI_IDS";

        String name;
        try {
            db = ModSupportDb.getModSupportDb();
            ps = db.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()) {
                long wurmId = rs.getLong("WURMID");
                name = rs.getString("MODELNAME");
                PermissionsPlayerList.ISettings obj = ModelMeImpressed.getISettings(wurmId);
                if (obj != null) {
                    entries.put(obj, name);
                }
            }
        } catch (SQLException var16) {
            throw new RuntimeException(var16);
        } finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(db);
        }

        for (Map.Entry<PermissionsPlayerList.ISettings, String> entry : entries.entrySet()) {
            name = "Name not found";

            try {
                name = ItemTemplateFactory.getInstance().getTemplate(entry.getKey().getTemplateId()).getName();
            } catch (NoSuchTemplateException var15) {
                ModelMeImpressed.logger.warning(var15.getMessage());
            }

            f.beginHorizontalFlow();
            f.addLabel(name + " | " + entry.getValue() + " ");
            f.addRaw("checkbox{id='object" + entry.getKey().getWurmId() + "';}");
            f.endHorizontalFlow();
        }

        f.beginHorizontalFlow();
        f.addButton("Accept", "accept");
        f.endHorizontalFlow();
        this.getResponder().getCommunicator().sendBml(600, 500, true, true, f.toString(), 150, 150, 200, this.title);
    }
}
