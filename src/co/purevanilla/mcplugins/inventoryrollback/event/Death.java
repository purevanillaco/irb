package co.purevanilla.mcplugins.inventoryrollback.event;

import co.purevanilla.mcplugins.inventoryrollback.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class Death implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        if(e.getEntity() instanceof Player){
            if(e.getEntity().getLastDamageCause()!=null){
                EntityDamageEvent.DamageCause cause = e.getEntity().getLastDamageCause().getCause();
                if(Main.inventoryRollbackAPI.shouldBackup(cause)){
                    try {
                        Main.inventoryRollbackAPI.backup((Player) e.getEntity());
                    } catch (IOException ex) {
                        Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("InventoryRollback")).getLogger().log(Level.WARNING,"Couldn't save "+e.getEntity().getName()+"'s inventory");
                    }
                }
            }
        }
    }

}
