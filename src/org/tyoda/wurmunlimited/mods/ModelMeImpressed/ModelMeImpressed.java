package org.tyoda.wurmunlimited.mods.ModelMeImpressed;


import com.wurmonline.server.*;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.NoSuchCreatureException;
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

public class ModelMeImpressed implements WurmServerMod, ServerStartedListener, PreInitable, Configurable {
    //public static int gmcounter = 0;
    public static final Logger logger = Logger.getLogger(ModelMeImpressed.class.getName());
    public static final MMIOptions options = new MMIOptions();
    private static boolean dbLoaded = false;
    private static final String dbName = "MMI_IDS";
    public static final String version = "v2.1";
    public void configure(Properties properties){
        options.configure(properties);
    }
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
        //org.tyoda.wurmunlimited.mods.ModelMeImpressed.ModelMeImpressed.logger.info("How many: "+(++(org.tyoda.wurmunlimited.mods.ModelMeImpressed.ModelMeImpressed.gmcounter)));
        getModelName.insertBefore("{if(org.tyoda.wurmunlimited.mods.ModelMeImpressed.ModelMeImpressed.isDbLoaded()){String modelName=org.tyoda.wurmunlimited.mods.ModelMeImpressed.ModelMeImpressed.getCustomModel(this);if(modelName!=null){return modelName;}}}");
    }
    public void onServerStarted() {
        ModActions.init();
        options.setMmiBehaviour(new MMIBehaviour());
        options.setRemodelAction(new RemodelAction());
        options.setRandomModelAction(new RandomModelAction());
        options.setResetModelAction(new ResetModelAction());
        options.setBrowseModelsAction(new BrowseModelsAction());
        ModActions.registerAction(options.getMmiBehaviour());
        ModActions.registerAction(options.getRemodelAction());
        ModActions.registerAction(options.getRandomModelAction());
        ModActions.registerAction(options.getResetModelAction());
        ModActions.registerAction(options.getBrowseModelsAction());

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
        }catch(SQLException e) {throw new RuntimeException(e);}
        finally{
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(con);
        }

        //retrieve remodeled objects
        con = ModSupportDb.getModSupportDb();
        HashMap<Long, String> map = new HashMap<>();
        sql = "SELECT * FROM "+dbName;
        ResultSet rs = null;
        try{
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                long wurmID = Long.parseLong(rs.getString("WURMID"));
                String modelName = rs.getString("MODELNAME");
                map.put(wurmID, modelName);
            }
        }catch(SQLException e){throw new RuntimeException(e);}
        finally{
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(con);
        }
        //check if modeled object exists
        for(Iterator<Long> set = map.keySet().iterator(); set.hasNext();) {
            long wurmID = set.next();
            if (!checkExists(wurmID)) {
                set.remove();
                deleteEntry(wurmID);
            }
        }
        if(options.isKeepCustomModelInMemory()) options.customModels = map;
    }
    private static boolean checkExists(long wurmId){
        return getISettings(wurmId) != null;
    }
    public static PermissionsPlayerList.ISettings getISettings(long wurmId) {
        PermissionsPlayerList.ISettings obj = null;

        try {
            obj = Items.getItem(wurmId);
        } catch (NoSuchItemException var8) {
            try {
                obj = Creatures.getInstance().getCreature(wurmId);
            } catch (NoSuchCreatureException var7) {
                try {
                    obj = Players.getInstance().getPlayer(wurmId);
                } catch (NoSuchPlayerException var6) {
                    logger.warning("Object not found via ID: " + wurmId);
                }
            }
        }

        return obj;
    }
    public static void deleteEntry(long wurmId){
        Connection con = ModSupportDb.getModSupportDb();
        PreparedStatement ps = null;
        String sql = "DELETE FROM "+dbName+" WHERE WURMID=?";
        try{
            ps = con.prepareStatement(sql);
            ps.setLong(1, wurmId);
            ps.executeUpdate();
        }catch(SQLException e){ throw new RuntimeException(e); }
        finally{
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(con);
        }
        if(options.isKeepCustomModelInMemory()){
            options.customModels.remove(wurmId);
        }

        try{
            Item item = Items.getItem(wurmId);
            refreshModel(item);
        }catch(NoSuchItemException e1){
            try {
                Creature c = Creatures.getInstance().getCreature(wurmId);
                refreshModel(c);
            }catch(NoSuchCreatureException e2){
                try{
                    Player player = Players.getInstance().getPlayer(wurmId);
                    refreshModel(player);
                }catch(NoSuchPlayerException e3){
                    logger.warning("object with Id not found: " + wurmId);
                }
            }
        }
    }
    public static String getCustomModel(PermissionsPlayerList.ISettings obj){
        if(obj == null) return null;
        String modelName = null;
        if(options.isKeepCustomModelInMemory() && options.customModels != null){
            modelName = options.customModels.get(obj.getWurmId());
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
    private static void refreshModel(PermissionsPlayerList.ISettings target){
        if (target instanceof Player && options.isRelogPlayers()){
            Server.getInstance().addCreatureToRemove((Creature)target);
            Server.getInstance().addPlayer((Player)target);
        }
        else if(target instanceof Creature) {
            ((Creature)target).refreshVisible();
        }
        else if (target instanceof DbItem) {
            ((Item) target).updateIfGroundItem();
        }
    }
    public static void setCustomModel(PermissionsPlayerList.ISettings obj, String modelName) {
        //Item and Creature(and therefore Player) all implement ISettings, which has the interface method getWurmId()
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
        } catch (SQLException e) {throw new RuntimeException(e);}
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(con);
        }

        if (options.isKeepCustomModelInMemory() && options.customModels != null) {
            options.customModels.put(obj.getWurmId(), modelName);
        }

        refreshModel(obj);
    }
    public static boolean isDbLoaded(){
        return dbLoaded;
    }
    public String getVersion(){
        return version;
    }
}




