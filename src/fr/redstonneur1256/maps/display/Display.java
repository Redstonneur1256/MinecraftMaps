package fr.redstonneur1256.maps.display;

import fr.redstonneur1256.maps.MinecraftMaps;
import fr.redstonneur1256.maps.maps.Mode;
import fr.redstonneur1256.maps.maps.SimpleMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Display {

    private World world;
    private int width, height;
    private SimpleMap[] maps;
    private List<Renderer> renderers;
    private List<TouchListener> listeners;
    private Mode mode;
    private BufferedImage buffer;
    private BukkitTask updateTask;

    public Display(World world, int width, int height, short idStart) {
        this(world, width, height, idStart, Mode.global);
    }

    public Display(World world, int width, int height, short idStart, Mode mode) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.maps = new SimpleMap[width * height];
        this.renderers = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.mode = mode;
        this.buffer = new BufferedImage(width * 128, height * 128, BufferedImage.TYPE_INT_ARGB);

        for(int i = 0; i < this.maps.length; i++) {
            SimpleMap map = new SimpleMap(world, (short) (idStart + i));
            maps[i] = map;
        }

        MinecraftMaps.getInstance().getDisplays().add(this);
    }

    public void update() {
        render(null);

        for(Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

    public World getWorld() {
        return world;
    }

    public int getWidth() {
        return width;
    }

    public int getWidthResolution() {
        return width * 128;
    }

    public int getHeight() {
        return height;
    }

    public int getHeightResolution() {
        return height * 128;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void update(Player player) {
        if(mode == Mode.player) {
            render(player);
        }

        for(SimpleMap map : maps) {
            map.send(player);
        }
    }

    public void onClick(Player player, int clickX, int clickY, Action action) {
        boolean isLeft = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        for(TouchListener listener : listeners) {
            try {
                listener.onTouch(this, player, clickX, clickY, isLeft);
            }catch(Exception exception) {
                MinecraftMaps.log(ChatColor.RED + "Failed to handle onTouch for listener " + ChatColor.DARK_RED + listener);
            }
        }
    }

    public BukkitTask updateAtFixedRate(long ticks, boolean async) {
        if(updateTask != null) {
            updateTask.cancel();
        }
        BukkitScheduler scheduler = Bukkit.getScheduler();
        MinecraftMaps plugin = MinecraftMaps.getInstance();
        return updateTask = async ?
                scheduler.runTaskTimerAsynchronously(plugin, this::update, 1, ticks) :
                scheduler.runTaskTimer(plugin, this::update, 1, ticks);
    }

    public void cancelUpdates() {
        if(updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    public void delete() {
        cancelUpdates();
    }

    public Point getLocation(short mapId) {
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if(maps[x + y * width].getId() == mapId) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public boolean hasMap(short mapId) {
        for(SimpleMap map : maps) {
            if(map.getId() == mapId) {
                return true;
            }
        }
        return false;
    }

    public void addRenderer(Renderer renderer) {
        Objects.requireNonNull(renderer, "Renderer cannot be null");
        renderers.add(renderer);
    }

    public void removeRenderer(Renderer renderer) {
        renderers.remove(renderer);
    }

    public List<Renderer> getRenderers() {
        return Collections.unmodifiableList(renderers);
    }

    public void addListener(TouchListener listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        listeners.add(listener);
    }

    public void removeListener(TouchListener listener) {
        listeners.remove(listener);
    }

    public List<TouchListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    private void render(Player player) {
        Graphics graphics = buffer.getGraphics();
        graphics.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
        for(Renderer renderer : renderers) {
            renderer.render(buffer, player);
        }

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                BufferedImage subImage = buffer.getSubimage(x * 128, y * 128, 128, 128);
                maps[x + y * width].draw(subImage);
            }
        }
    }

}
