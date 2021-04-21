package fr.redstonneur1256.maps.spigot;

import fr.redstonneur1256.maps.DisplayManager;
import fr.redstonneur1256.maps.render.RenderMode;
import fr.redstonneur1256.maps.display.Display;
import fr.redstonneur1256.maps.spigot.map.BukkitDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SpigotDisplayManager implements DisplayManager<Player> {

    private MinecraftMaps plugin;
    private List<BukkitDisplay> displays;
    public SpigotDisplayManager(MinecraftMaps plugin) {
        this.plugin = plugin;
        this.displays = new ArrayList<>();
    }

    public void onDisable() {
        while(!displays.isEmpty()) {
            displays.get(0).dispose();
        }
    }

    @NotNull
    @Override
    public Display<Player> createDisplay(int width, int height, short mapStart, @NotNull RenderMode mode) {
        BukkitDisplay display = new BukkitDisplay(plugin, width, height, mapStart, mode);
        displays.add(display);
        return display;
    }

    @NotNull
    @Override
    public List<BukkitDisplay> getDisplays() {
        return displays;
    }

    @NotNull
    @Override
    public Optional<BukkitDisplay> getDisplayByMap(short mapID) {
        return displays.stream()
                .filter(display -> display.containsMap(mapID))
                .findFirst();
    }

}
