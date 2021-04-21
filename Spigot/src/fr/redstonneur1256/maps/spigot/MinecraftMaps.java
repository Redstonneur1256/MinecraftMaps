package fr.redstonneur1256.maps.spigot;

import fr.redstonneur1256.maps.DisplayManager;
import fr.redstonneur1256.maps.render.MapPalette;
import fr.redstonneur1256.maps.spigot.adapter.Call;
import fr.redstonneur1256.maps.spigot.commands.MapsCommand;
import fr.redstonneur1256.maps.utils.Logger;
import fr.redstonneur1256.redutilities.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MinecraftMaps extends JavaPlugin {

    private SpigotDisplayManager manager;
    private List<UUID> bypassMode;

    public MinecraftMaps() {
        manager = new SpigotDisplayManager(this);
        bypassMode = new ArrayList<>();
    }

    @Override
    public void onLoad() {
        Logger.setImpl(((message, throwable) -> {
            message = message == null ? "" : ChatColor.translateAlternateColorCodes('&', message);

            ConsoleCommandSender console = Bukkit.getConsoleSender();
            String prefix = ChatColor.AQUA + "[MinecraftMaps] ";
            console.sendMessage(prefix + message);
            if(throwable != null) {
                for(String line : Utils.errorMessage(throwable).split("\n")) {
                    console.sendMessage(prefix + ChatColor.RED + line);
                }
            }
        }));
    }

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();

            boolean useCache = getConfig().getBoolean("render.colorCache");

            Call.setup();

            if(!Call.isEnabled()) {
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            try(InputStream input = getResource("palette.bin")) {
                MapPalette.loadPalette(input, useCache);
            }

            getCommand("minecraftMaps").setExecutor(new MapsCommand(this));

            getServer().getPluginManager().registerEvents(new MapListeners(this), this);

            getServer().getServicesManager().register(DisplayManager.class, manager, this, ServicePriority.High);
            
            Metrics metrics = new Metrics(this, 11099);

            Logger.log(ChatColor.BLUE + "Enabled " + ChatColor.DARK_AQUA + getDescription().getVersion());
        }catch(Exception exception) {
            Logger.log(ChatColor.DARK_RED + "Failed to enable the plugin:", exception);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        manager.onDisable();
    }

    public SpigotDisplayManager getManager() {
        return manager;
    }

    public List<UUID> getBypassMode() {
        return bypassMode;
    }

}
