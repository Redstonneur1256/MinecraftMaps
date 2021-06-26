package fr.redstonneur1256.maps.spigot.commands;

import fr.redstonneur1256.maps.render.MapPalette;
import fr.redstonneur1256.maps.spigot.MinecraftMaps;
import fr.redstonneur1256.redutilities.graphics.Palette;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MapsCommand implements CommandExecutor {

    private MinecraftMaps plugin;

    public MapsCommand(MinecraftMaps plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <bypass>");
            return false;
        }

        if(args[0].equalsIgnoreCase("bypass")) {
            List<UUID> bypassMode = plugin.getBypassMode();
            UUID uuid = ((Player) sender).getUniqueId();

            if(!bypassMode.contains(uuid)) {
                bypassMode.add(uuid);

                sender.sendMessage(ChatColor.WHITE + "You " + ChatColor.GREEN + "entered" + ChatColor.WHITE + " the " +
                        ChatColor.AQUA + "bypass" + ChatColor.WHITE + " mode.");
                sender.sendMessage(ChatColor.WHITE + "You are now ignoring the click on the displays");
            }else {
                bypassMode.remove(uuid) ;
                sender.sendMessage(ChatColor.WHITE + "You " + ChatColor.RED + "exited" + ChatColor.WHITE + " the " +
                        ChatColor.AQUA + "bypass" + ChatColor.WHITE + " mode.");
            }
        }

        return false;
    }

}
