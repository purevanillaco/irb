package co.purevanilla.mcplugins.inventoryrollback.api.obj;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InventoryBackup {

    public long timestamp;
    public double tps;
    public int ping;
    public UUID uuid;
    public String deathCause;
    public String fileid;

    public Date getDate(){
        return new Date(timestamp);
    }

    public String RandomAlphanum(int length)
    {
        String charstring = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randalphanum = new StringBuilder();
        double randroll;
        String randchar;
        for
        (double i = 0; i < length; i++)
        {
            randroll = Math.random();
            randchar = "";
            for
            (int j = 1; j <= 35; j++)
            {
                if
                (randroll <= (1.0 / 36.0 * j))
                {
                    randchar = Character.toString(charstring.charAt(j - 1));
                    break;
                }
            }
            randalphanum.append(randchar);
        }
        return randalphanum.toString();
    }

    public InventoryBackup(Player player) throws IOException {
        this.fileid=this.RandomAlphanum(16);
        this.saveInventory(player.getInventory());
        this.uuid=player.getUniqueId();
        this.timestamp= new Date().getTime();

        EntityDamageEvent.DamageCause cause = Objects.requireNonNull(player.getLastDamageCause()).getCause();
        int ping = -1;
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            // unknown ping error
        }

        double tps = -1;
        try {
            tps = Bukkit.getServer().getTPS()[Bukkit.getServer().getTPS().length-1];
            tps = round(tps,2);
        } catch (NullPointerException e) {
            // not using paper, to-do: improve call
        }

        this.ping=ping;
        this.tps=tps;
        this.deathCause=cause.toString();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public InventoryBackup(String json){
        Gson gson = new Gson();
        InventoryBackup inventoryBackup = gson.fromJson(json,InventoryBackup.class);
        this.timestamp=inventoryBackup.timestamp;
        this.tps=inventoryBackup.tps;
        this.ping=inventoryBackup.ping;
        this.uuid=inventoryBackup.uuid;
        this.fileid=inventoryBackup.fileid;
        this.deathCause=inventoryBackup.deathCause;
    }

    public Player getPlayer(){
        return Bukkit.getServer().getPlayer(this.uuid);
    }

    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getServer().getOfflinePlayer(this.uuid);
    }

    public String asJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void saveInventory(Inventory inventory) throws IOException {
        File f = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("InventoryRollback")).getDataFolder().getAbsolutePath()+"/data/", fileid + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("inventory.content", inventory.getContents());
        c.save(f);
    }

    public ItemStack[] getInventory() throws IOException {
        File f = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("InventoryRollback")).getDataFolder().getAbsolutePath()+"/data/", fileid + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        return ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
    }

    public void restore() throws Exception {
        if(getPlayer().isOnline()){
            getPlayer().getInventory().setContents(this.getInventory());
            File f = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("InventoryRollback")).getDataFolder().getAbsolutePath()+"/data/", fileid + ".yml");
            f.delete();
        } else {
            throw new Exception("player not online");
        }
    }


}
