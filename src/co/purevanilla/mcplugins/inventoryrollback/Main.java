package co.purevanilla.mcplugins.inventoryrollback;

import co.purevanilla.mcplugins.inventoryrollback.api.InventoryRollbackAPI;
import co.purevanilla.mcplugins.inventoryrollback.cmd.History;
import co.purevanilla.mcplugins.inventoryrollback.cmd.Preview;
import co.purevanilla.mcplugins.inventoryrollback.cmd.Rollback;
import co.purevanilla.mcplugins.inventoryrollback.event.Death;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static InventoryRollbackAPI inventoryRollbackAPI;

    @Override
    public void onEnable() {
        super.onEnable();
        inventoryRollbackAPI = new InventoryRollbackAPI(this);

        // config
        saveDefaultConfig();

        // cmd
        this.getCommand("irbrollback").setExecutor(new Rollback());
        this.getCommand("irbpreview").setExecutor(new Preview());
        this.getCommand("irb").setExecutor(new History());

        // events
        getServer().getPluginManager().registerEvents(new Death(),this);

    }
}
