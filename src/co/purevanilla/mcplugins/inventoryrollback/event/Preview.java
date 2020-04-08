package co.purevanilla.mcplugins.inventoryrollback.event;

import co.purevanilla.mcplugins.inventoryrollback.api.obj.InventoryBackup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Preview implements InventoryHolder, Listener {

    Inventory inv;
    InventoryBackup backup;

    @Override
    public Inventory getInventory() {
        return inv;
    }


    public Preview(InventoryBackup inventoryBackup)
    {
        this.backup=inventoryBackup;
        inv = Bukkit.createInventory(this, 54, "Ping: "+ ChatColor.RED+inventoryBackup.ping+"ms"+ChatColor.RESET+", TPS: "+ChatColor.RED+inventoryBackup.tps+ChatColor.RESET+" | "+ChatColor.DARK_RED+inventoryBackup.deathCause);
        try {
            ItemStack[] itemStackList = inventoryBackup.getInventory();
            for (ItemStack item:itemStackList) {
                if(item!=null){
                    inv.addItem(item);
                }
            }
            inv.setItem(53,confirmItemstack());
        } catch (IOException e) {
            ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RED+"No data, perhaps already restored?");
            item.setItemMeta(meta);
            inv.addItem(item);
        }
    }

    public ItemStack confirmItemstack(){
        ItemStack confirmItemstack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = confirmItemstack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("%confirm");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DAMAGE_ARTHROPODS,1,true);
        meta.setDisplayName(ChatColor.GREEN+"Restore");
        confirmItemstack.setItemMeta(meta);
        return confirmItemstack;
    }

    public boolean isConfirmStack(ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        boolean is = false;
        for (String line:lore) {
            if (line.contains("%confirm")) {
                is = true;
                break;
            }
        }
        return is;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e)
    {
        if(e.getInventory()==this.inv){

            final ItemStack clickedItem = e.getCurrentItem();
            final Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);

            try{
                if(isConfirmStack(clickedItem)){
                    p.performCommand("irbrollback "+backup.getPlayer().getUniqueId().toString()+" "+backup.fileid);
                    p.closeInventory();
                }
            } catch (NullPointerException err){
                // I dont do anything bc im too lazy
            }


        }
    }


}
