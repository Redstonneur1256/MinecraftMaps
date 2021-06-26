package fr.redstonneur1256.maps.spigot;

import fr.redstonneur1256.maps.DisplayManager;
import fr.redstonneur1256.maps.render.MapPalette;
import fr.redstonneur1256.maps.spigot.adapter.Call;
import fr.redstonneur1256.maps.spigot.commands.MapsCommand;
import fr.redstonneur1256.maps.utils.Logger;
import fr.redstonneur1256.redutilities.TimeUtils;
import fr.redstonneur1256.redutilities.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
            
            Call.setup();

            if(!Call.isEnabled()) {
                Logger.log(ChatColor.DARK_RED + "Disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            File paletteFile = new File(getDataFolder(), "palette.bin");
            
            if(!paletteFile.exists()) {
                Logger.log(ChatColor.WHITE + "The plugin needs to generate so data in order to work");
                Logger.log(ChatColor.WHITE + "This operation may take few minutes");
                Logger.log(ChatColor.WHITE + "Generating file " + ChatColor.BLUE + paletteFile.getPath());

                long start = System.currentTimeMillis();
                try(FileOutputStream output = new FileOutputStream(paletteFile)) {
                    MapPalette.generatePalette(getResource("palette.bin"), output);
                }
                long end = System.currentTimeMillis();

                Logger.log(ChatColor.WHITE + "File generated in " + ChatColor.BLUE + TimeUtils.english.formatMaxSeconds(end - start, true));
            }

            Logger.log(ChatColor.WHITE + "Loading palette...");
            long start = System.currentTimeMillis();
            try(InputStream input = new FileInputStream(paletteFile)) {
                MapPalette.loadPalette(input);
            }
            long end = System.currentTimeMillis();
            Logger.log(ChatColor.WHITE + "Loaded palette in " + ChatColor.BLUE + TimeUtils.english.formatMaxMillis(end - start, true));

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
