package co.purevanilla.mcplugins.inventoryrollback.cmd;

import co.purevanilla.mcplugins.inventoryrollback.Main;
import co.purevanilla.mcplugins.inventoryrollback.api.obj.InventoryBackup;
import co.purevanilla.mcplugins.inventoryrollback.event.Death;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Preview implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){

            Player sender = (Player) commandSender;
            Player player = null;
            int index = -1;
            if(strings.length==2){
                if(strings[0].contains("-")){
                    player = Bukkit.getServer().getPlayer(UUID.fromString(strings[0]));
                } else {
                    player = Bukkit.getServer().getPlayer(strings[0]);
                }
                index = Integer.parseInt(strings[1]);
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED+"Please, specify a player and a valid index"));
            }

            if(player!=null && index!=-1){
                List<InventoryBackup> inventoryBackupList = Main.inventoryRollbackAPI.inventoryBackupList(player);
                InventoryBackup backup = null;
                try{

                    backup = inventoryBackupList.get(index);
                    co.purevanilla.mcplugins.inventoryrollback.event.Preview preview = new co.purevanilla.mcplugins.inventoryrollback.event.Preview(backup);
                    Bukkit.getServer().getPluginManager().registerEvents(preview, Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("InventoryRollback")));
                    sender.openInventory(preview.getInventory());

                } catch (NullPointerException | IndexOutOfBoundsException err){
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Unknown backup"));
                }
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.RED+"Please, specify a player and a valid index"));
            }

        } else {
            commandSender.sendMessage("Must be executed by a player");
        }
        return true;
    }

}
