package fr.redstonneur1256.maps;

import fr.redstonneur1256.maps.adapters.Call;
import fr.redstonneur1256.maps.display.Display;
import fr.redstonneur1256.maps.maps.MapColors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MinecraftMaps extends JavaPlugin {

    private static MinecraftMaps instance;
    private List<Display> displays;
    private boolean loaded;

    public MinecraftMaps() {
        if(instance != null)
            throw new IllegalStateException("Plugin initialized twice.");
        instance = this;

        displays = new ArrayList<>();
    }

    public static MinecraftMaps getInstance() {
        return instance;
    }

    public static void log(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[MinecraftMaps] " + text);
    }

    @Override
    public void onLoad() {
        try {
            Call.setup();
        }catch(Exception exception) {
            exception.printStackTrace();
            log(ChatColor.DARK_RED + "Your server version is not supported by the plugin. Disabling");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            InputStream paletteInput = getResource("palette.bin");
            MapColors.load(paletteInput);
        }catch(IOException exception) {
            log(ChatColor.DARK_RED + "Failed to import the map colors. Disabling");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        log(ChatColor.DARK_GREEN + "Plugin successfully loaded");

        loaded = true;
    }

    @Override
    public void onEnable() {
        if(!loaded) {
            return;
        }

        getServer().getPluginManager().registerEvents(new MapTouchListener(), this);
    }

    @Override
    public void onDisable() {
        for(Display display : displays) {
            display.delete();
        }
    }

    public List<Display> getDisplays() {
        return displays;
    }

    public Optional<Display> getDisplayByMap(short mapId) {
        return displays.stream()
                .filter(display -> display.hasMap(mapId))
                .findFirst();
    }

}
