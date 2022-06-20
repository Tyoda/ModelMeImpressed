package org.tyoda.wurmunlimited.mods;


import com.wurmonline.server.DbConnector;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.utils.DbUtilities;
import javassist.*;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class ModelMeImpressed implements WurmServerMod, ServerStartedListener, PreInitable, Initable, Configurable {
    //public static int gmcounter = 0;
    public static final Logger logger = Logger.getLogger(ModelMeImpressed.class.getName());
    public static final MMIOptions options = new MMIOptions();
    public static boolean db_loaded = false;
    public void preInit(){
        try {
            ClassPool classPool = HookManager.getInstance().getClassPool();
            CtClass ctCreature = classPool.getCtClass("com.wurmonline.server.creatures.Creature");
            CtClass ctItem = classPool.getCtClass("com.wurmonline.server.items.Item");
            CtClass ctPlayer = classPool.getCtClass("com.wurmonline.server.players.Player");

            inject(ctCreature);
            logger.info("Creature injected");
            inject(ctItem);
            logger.info("Item injected");
            inject(ctPlayer);
            logger.info("Player injected");
        }catch(NotFoundException e){ e.printStackTrace(); }
        catch(javassist.CannotCompileException e){
            logger.severe("Could not compile bytecode injection");
            throw new RuntimeException(e);
        }
        // TODO: Walls could probably be changed as well
    }
    private void inject(CtClass cls) throws NotFoundException, CannotCompileException{
        CtMethod getModelName = cls.getDeclaredMethod("getModelName");
        //org.tyoda.wurmunlimited.mods.ModelMeImpressed.logger.info("How many: "+(++(org.tyoda.wurmunlimited.mods.ModelMeImpressed.gmcounter)));
        getModelName.insertBefore("{if(org.tyoda.wurmunlimited.mods.ModelMeImpressed.db_loaded){String modelName=org.tyoda.wurmunlimited.mods.ModelMeImpressed.getCustomModel(this);if(modelName!=null){return modelName;}}}");
    }
    public String getVersion(){
        return "v1.0";
    }
    public void onServerStarted() {
        ModActions.init();
        ModActions.registerAction(new ModelMeImpressedAction());

        //create db if not exists
        Connection con = ModSupportDb.getModSupportDb();
        PreparedStatement ps = null;
        String sql = "CREATE TABLE MMI_IDS (WURMID LONG NOT NULL UNIQUE, MODELNAME VARCHAR(127) NOT NULL DEFAULT \"\")";
        try{
            if(!ModSupportDb.hasTable(con, "MMI_IDS")){
                ps = con.prepareStatement(sql);
                ps.execute();
                if(ModSupportDb.hasTable(con, "MMI_IDS")){db_loaded = true;}
                else{ throw new RuntimeException("MMI_IDS database creation has failed."); }
            }else{db_loaded = true;}
        }catch(SQLException e) { DbConnector.returnConnection(con); throw new RuntimeException(e); }
        finally{
            DbUtilities.closeDatabaseObjects(ps, null);
        }

        if(MMIOptions.isKeepCustomModelInMemory()){
            HashMap<Long, String> map = new HashMap<>();
            sql = "SELECT * FROM MMI_IDS";
            ResultSet rs = null;

            try{
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();

                while(rs.next()){
                    map.put(Long.parseLong(rs.getString("WURMID")), rs.getString("MODELNAME"));
                }
            }catch(SQLException e){ DbConnector.returnConnection(con); throw new RuntimeException(e); }
            finally{
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(con);
            }

            MMIOptions.customModels = map;
        }
        // TODO: Check if creature/item with wurmid still exists
    }

    public static String getCustomModel(PermissionsPlayerList.ISettings obj){
        String modelName = null;
        if(MMIOptions.isKeepCustomModelInMemory() && MMIOptions.customModels != null){
            modelName = MMIOptions.customModels.get(obj.getWurmId());
        }else {
            Connection con = ModSupportDb.getModSupportDb();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                String sql = String.format("SELECT MODELNAME FROM MMI_IDS WHERE WURMID=%d", obj.getWurmId());
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelName = rs.getString("MODELNAME");
                }
            } catch (SQLException e) {DbConnector.returnConnection(con); throw new RuntimeException(e);}
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(con);
            }
        }
        return modelName;
    }

    public static void setCustomModel(PermissionsPlayerList.ISettings obj, String modelName){
        //Item and Creature(and therefore Player) all implement ISettings, which has the virtual method getWurmId()
        if(obj instanceof Creature){ ((Creature)obj).refreshVisible(); }

        Connection con = ModSupportDb.getModSupportDb();
        PreparedStatement ps = null;
        String prevModelName = ModelMeImpressed.getCustomModel(obj);
        try{
            String sql;
            String word;
            if(prevModelName == null){
                word = "Inserting";
                sql = "INSERT INTO MMI_IDS VALUES (?, ?)";
                ps = con.prepareStatement(sql);
                ps.setLong(1, obj.getWurmId());
                ps.setString(2, modelName);
            }else{
                word = "Updating";
                sql = "UPDATE MMI_IDS SET MODELNAME=? WHERE WURMID=?";
                ps = con.prepareStatement(sql);
                ps.setString(1, modelName);
                ps.setLong(2, obj.getWurmId());
            }
            int rs = ps.executeUpdate();
            if(rs != 1){ logger.info(word + " modelname was unsuccessful for wurmid " + obj.getWurmId() + " and modelname " + modelName +". rs was " + rs); }
        }catch(SQLException e){DbConnector.returnConnection(con); throw new RuntimeException(e);}
        finally{
            if(obj instanceof Creature){ ((Creature)obj).refreshVisible(); }
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(con);
        }
        if(MMIOptions.isKeepCustomModelInMemory() && MMIOptions.customModels != null) MMIOptions.customModels.put(obj.getWurmId(), modelName);
        //if(obj instanceof Player){ ((Player)obj).sendEquipment(); }
        if(obj instanceof Item){ ((Item)obj).setRotation(((Item)obj).getRotation()-0.001f);((Item)obj).setRotation(((Item)obj).getRotation()+0.001f); }
        if(obj instanceof Creature){ ((Creature)obj).refreshVisible(); }
        // TODO: rotate item left and back to refresh it
    }

    public void configure(Properties properties){
        options.configure(properties);
    }
    public void init() { }
}




