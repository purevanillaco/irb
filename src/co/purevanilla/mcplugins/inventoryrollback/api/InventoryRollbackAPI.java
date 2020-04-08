package co.purevanilla.mcplugins.inventoryrollback.api;

import co.purevanilla.mcplugins.inventoryrollback.Main;
import co.purevanilla.mcplugins.inventoryrollback.api.obj.InventoryBackup;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryRollbackAPI {

    Plugin plugin;

    public InventoryRollbackAPI(Plugin plugin){
        this.plugin=plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public List<EntityDamageEvent.DamageCause> getDamageCauseList(){
        List<EntityDamageEvent.DamageCause> damageCauseList = new ArrayList<>();
        List<String> stringList = this.plugin.getConfig().getStringList("save.death");
        for (String deathCause:stringList) {
            try{
                EntityDamageEvent.DamageCause damageCause = EntityDamageEvent.DamageCause.valueOf(deathCause);
                damageCauseList.add(damageCause);
            } catch (NullPointerException err){
                // ignore, the provided damage cause is invalid/unknown
            }
        }
        return damageCauseList;
    }

    public boolean shouldBackup(EntityDamageEvent.DamageCause cause){
        return this.getDamageCauseList().contains(cause);
    }

    public void restore(InventoryBackup backup) throws Exception {

        List<String> backupHistory = new ArrayList<>();
        try{
            backupHistory = plugin.getConfig().getStringList("history."+backup.getPlayer().getUniqueId());
        } catch (NullPointerException err){
            // ignore, will be created later
        }

        int toRemove = -1;
        int i = 0;
        for (String backupJSON:backupHistory) {
            InventoryBackup backupTemp = new InventoryBackup(backupJSON);
            if(backupTemp.fileid.equals(backup.fileid)){
                toRemove = i;
            }
            i++;
        }
        if(toRemove==-1){
            backupHistory.clear();
        } else {
            backupHistory.remove(toRemove);
        }

        plugin.getConfig().set("history."+backup.getPlayer().getUniqueId(),backupHistory);
        plugin.saveConfig();

        backup.restore();

    }

    public InventoryBackup backup(Player player) throws IOException {

        InventoryBackup inventoryBackup = new InventoryBackup(player);
        String json = inventoryBackup.asJson();
        List<String> backupHistory = new ArrayList<>();
        try{
            backupHistory = plugin.getConfig().getStringList("history."+player.getUniqueId());
        } catch (NullPointerException err){
            // ignore, will be created later
        }

        backupHistory.add(json);

        plugin.getConfig().set("history."+player.getUniqueId(),backupHistory);
        plugin.saveConfig();
        return inventoryBackup;

    }

    public List<InventoryBackup> inventoryBackupList(Player player){
        List<String> backupHistory = new ArrayList<>();
        try{
            backupHistory = plugin.getConfig().getStringList("history."+player.getUniqueId());
        } catch (NullPointerException err){
            // ignore, will be empty
        }

        List<InventoryBackup> inventoryBackupList = new ArrayList<>();
        for (String backupJson:backupHistory) {
            inventoryBackupList.add(new InventoryBackup(backupJson));
        }

        Collections.reverse(inventoryBackupList);

        return inventoryBackupList;
    }

}
