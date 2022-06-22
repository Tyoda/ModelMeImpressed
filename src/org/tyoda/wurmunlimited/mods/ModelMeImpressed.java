package org.tyoda.wurmunlimited.mods;


import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.DbItem;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.players.Player;
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
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

public class ModelMeImpressed implements WurmServerMod, ServerStartedListener, PreInitable, Initable, Configurable {
    //public static int gmcounter = 0;
    public static final Logger logger = Logger.getLogger(ModelMeImpressed.class.getName());
    public static final MMIOptions options = new MMIOptions();
    private static boolean dbLoaded = false;
    private static final String dbName = "MMI_IDS";
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
    }

    private void inject(CtClass cls) throws NotFoundException, CannotCompileException{
        CtMethod getModelName = cls.getDeclaredMethod("getModelName");
        //org.tyoda.wurmunlimited.mods.ModelMeImpressed.logger.info("How many: "+(++(org.tyoda.wurmunlimited.mods.ModelMeImpressed.gmcounter)));
        getModelName.insertBefore("{if(org.tyoda.wurmunlimited.mods.ModelMeImpressed.isDbLoaded()){String modelName=org.tyoda.wurmunlimited.mods.ModelMeImpressed.getCustomModel(this);if(modelName!=null){return modelName;}}}");
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
        String sql = "CREATE TABLE "+dbName+" (WURMID LONG NOT NULL UNIQUE, MODELNAME VARCHAR(127) NOT NULL DEFAULT \"\")";
        try{
            if(!ModSupportDb.hasTable(con, dbName)){
                ps = con.prepareStatement(sql);
                ps.execute();
                if(ModSupportDb.hasTable(con, dbName)){dbLoaded = true;}
                else{ throw new RuntimeException(dbName+" database creation has failed."); }
            }else{dbLoaded = true;}
        }catch(SQLException e) { DbConnector.returnConnection(con); throw new RuntimeException(e); }
        finally{
            DbUtilities.closeDatabaseObjects(ps, null);
        }

        HashMap<Long, String> map = new HashMap<>();
        sql = "SELECT * FROM " + dbName;
        ResultSet rs = null;

        try{
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                long wurmID = Long.parseLong(rs.getString("WURMID"));
                String modelName = rs.getString("MODELNAME");
                map.put(wurmID, modelName);
            }
        }catch(SQLException e){ DbConnector.returnConnection(con); throw new RuntimeException(e); }
        finally{
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(con);
        }


        for(Iterator<Long> set = map.keySet().iterator(); set.hasNext();) {
            Long wurmID = set.next();
            if (!checkExists(wurmID)) {
                set.remove();
                deleteEntry(wurmID);
            }
        }
        if(MMIOptions.isKeepCustomModelInMemory()) MMIOptions.customModels = map;
    }
    private boolean checkExists(long wurmID){
        boolean exists = false;
        Connection creatureCon = null;
        PreparedStatement creaturePS = null;
        ResultSet creatureRS = null;
        Connection itemCon = null;
        PreparedStatement itemPS = null;
        ResultSet itemRS = null;
        Connection playerCon = null;
        PreparedStatement playerPS = null;
        ResultSet playerRS = null;
        try {
            creatureCon = DbConnector.getCreatureDbCon();
            String sql = "SELECT * FROM CREATURES WHERE WURMID=?";
            creaturePS = creatureCon.prepareStatement(sql);
            creaturePS.setLong(1, wurmID);
            creatureRS = creaturePS.executeQuery();

            if(creatureRS.next()) exists = true;

            if(!exists){
                itemCon = DbConnector.getItemDbCon();
                sql = "SELECT * FROM ITEMS WHERE WURMID=?";
                itemPS = itemCon.prepareStatement(sql);
                itemPS.setLong(1, wurmID);
                itemRS = itemPS.executeQuery();

                if(itemRS.next()) exists = true;
            }

            if(!exists){
                playerCon = DbConnector.getPlayerDbCon();
                sql = "SELECT * FROM PLAYERS WHERE WURMID=?";
                playerPS = playerCon.prepareStatement(sql);
                playerPS.setLong(1, wurmID);
                playerRS = playerPS.executeQuery();

                if(playerRS.next()) exists = true;
            }
        }catch(SQLException e){ throw new RuntimeException(e); }
        finally {
            DbUtilities.closeDatabaseObjects(creaturePS, creatureRS);
            DbUtilities.closeDatabaseObjects(itemPS, itemRS);
            DbUtilities.closeDatabaseObjects(playerPS, playerRS);
            DbConnector.returnConnection(creatureCon);
        }
        //logger.info("Item with id "+wurmID+(exists?"exists":"does not exist"));
        return exists;
    }
    private void deleteEntry(long wurmID){
        Connection con = ModSupportDb.getModSupportDb();
        PreparedStatement ps = null;
        String sql = "DELETE FROM "+dbName+" WHERE WURMID=?";
        try{
            ps = con.prepareStatement(sql);
            ps.setLong(1, wurmID);
            int rs = ps.executeUpdate();
            if(rs == 1) logger.info("Deleted item from database: "+wurmID);
            else logger.warning("Something went wrong while trying to delete from database: "+wurmID+"result was"+rs);
        }catch(SQLException e){ throw new RuntimeException(e); }
        finally{
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(con);
        }
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
                String sql = String.format("SELECT MODELNAME FROM "+dbName+" WHERE WURMID=%d", obj.getWurmId());
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

    public static void setCustomModel(PermissionsPlayerList.ISettings obj, String modelName) {
        //Item and Creature(and therefore Player) all implement ISettings, which has the virtual method getWurmId()
        if (obj instanceof Creature) {
            ((Creature) obj).refreshVisible();
        }
        Connection con = ModSupportDb.getModSupportDb();
        PreparedStatement ps = null;
        String prevModelName = ModelMeImpressed.getCustomModel(obj);
        try {
            String sql;
            String word;
            if (prevModelName == null) {
                word = "Inserting";
                sql = "INSERT INTO "+dbName+" VALUES (?, ?)";
                ps = con.prepareStatement(sql);
                ps.setLong(1, obj.getWurmId());
                ps.setString(2, modelName);
            } else {
                word = "Updating";
                sql = "UPDATE "+dbName+" SET MODELNAME=? WHERE WURMID=?";
                ps = con.prepareStatement(sql);
                ps.setString(1, modelName);
                ps.setLong(2, obj.getWurmId());
            }
            int rs = ps.executeUpdate();
            if (rs != 1) {
                logger.info(word + " modelname was unsuccessful for wurmid " + obj.getWurmId() + " and modelname " + modelName + ". rs was " + rs);
            }
        } catch (SQLException e) {
            DbConnector.returnConnection(con);
            throw new RuntimeException(e);
        } finally {
            if (obj instanceof Creature) {
                ((Creature) obj).refreshVisible();
            }
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(con);
        }
        if (MMIOptions.isKeepCustomModelInMemory() && MMIOptions.customModels != null)
            MMIOptions.customModels.put(obj.getWurmId(), modelName);
        if (obj instanceof DbItem) {
            ((Item) obj).updateIfGroundItem();
        }
        if (obj instanceof Creature) {
            ((Creature) obj).refreshVisible();
        }
        if (obj instanceof Player && MMIOptions.isRelogPlayers()){
            Server.getInstance().addCreatureToRemove((Creature)obj);
            Server.getInstance().addPlayer((Player)obj);
        }
    }
    public void configure(Properties properties){
        options.configure(properties);
    }
    public void init() { }

    public static boolean isDbLoaded(){
        return dbLoaded;
    }
}




