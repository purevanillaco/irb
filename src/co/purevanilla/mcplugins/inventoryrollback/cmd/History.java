package co.purevanilla.mcplugins.inventoryrollback.cmd;

import co.purevanilla.mcplugins.inventoryrollback.Main;
import co.purevanilla.mcplugins.inventoryrollback.api.obj.InventoryBackup;
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
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Locale;

public class History implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){

            Player sender = (Player) commandSender;
            Player player;
            if(strings.length==1){
                player = Bukkit.getServer().getPlayer(strings[0]);
            } else {
                player = (Player) commandSender;
            }

            List<InventoryBackup> inventoryBackupList = Main.inventoryRollbackAPI.inventoryBackupList(player);
            if(player!=null){
                sender.sendMessage(new TextComponent(ChatColor.YELLOW+"Got "+inventoryBackupList.size()+" backups for "+player.getName()));
                PrettyTime p = new PrettyTime(new Locale("en"));
                int i = 0;
                for (InventoryBackup backup:inventoryBackupList) {

                    TextComponent textComponent = new TextComponent(ChatColor.GOLD+""+ChatColor.BOLD+" → "+ChatColor.YELLOW+backup.fileid+", TPS "+backup.tps+", "+backup.ping+"ms Ping");

                    TextComponent hoverText = new TextComponent(ChatColor.GRAY+p.format(backup.getDate())+", click to preview");
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[]{hoverText}));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/irbpreview "+player.getUniqueId()+" "+i));

                    sender.sendMessage(textComponent);

                    i++;

                }
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED+"Unknown player"));
            }

        } else {
            commandSender.sendMessage("Must be executed by a player");
        }
        return true;
    }

}
