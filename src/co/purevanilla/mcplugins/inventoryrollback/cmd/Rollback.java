package co.purevanilla.mcplugins.inventoryrollback.cmd;

import co.purevanilla.mcplugins.inventoryrollback.Main;
import co.purevanilla.mcplugins.inventoryrollback.api.obj.InventoryBackup;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Rollback implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){

            Player sender = (Player) commandSender;
            Player player = null;
            String fileid = null;
            if(strings.length==2){
                if(strings[0].contains("-")){
                    player = Bukkit.getServer().getPlayer(UUID.fromString(strings[0]));
                } else {
                    player = Bukkit.getServer().getPlayer(strings[0]);
                }
                fileid = strings[1];
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED+"Please, specify a player and a valid file id"));
            }

            if(player!=null && fileid!=null){
                List<InventoryBackup> inventoryBackupList = Main.inventoryRollbackAPI.inventoryBackupList(player);
                InventoryBackup backup = null;
                try{

                    for (InventoryBackup backupPre:inventoryBackupList) {
                        if(backupPre.fileid.equals(fileid)){
                            backup=backupPre;
                        }
                    }

                    if(backup!=null){
                        try{

                            Main.inventoryRollbackAPI.restore(backup);
                            sender.sendMessage(new TextComponent(ChatColor.GREEN+"Rolled back inventory successfully"));

                        } catch (Exception err){
                            sender.sendMessage(new TextComponent(ChatColor.RED+"Error while restoring the inventory: "+err.getMessage()));
                        }
                    }

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
