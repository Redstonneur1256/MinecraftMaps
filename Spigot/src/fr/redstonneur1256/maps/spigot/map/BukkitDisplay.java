package fr.redstonneur1256.maps.spigot.map;

import fr.redstonneur1256.maps.display.DefaultDisplay;
import fr.redstonneur1256.maps.render.RenderMode;
import fr.redstonneur1256.maps.spigot.MinecraftMaps;
import fr.redstonneur1256.maps.spigot.util.BukkitCancellable;
import fr.redstonneur1256.maps.task.Cancellable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitDisplay extends DefaultDisplay<Player> {

    protected int id;
    protected MinecraftMaps plugin;
    protected BukkitMap[] maps;
    protected boolean disposed;
    protected Cancellable task;

    public BukkitDisplay(MinecraftMaps plugin, int id, int width, int height, RenderMode mode, short... mapIDs) {
        super(width, height, mode, mapIDs);
        this.id = id;
        this.plugin = plugin;
        this.maps = new BukkitMap[width * height];
        this.disposed = false;
        this.task = null;

        for(int i = 0; i < maps.length; i++) {
            maps[i] = new BukkitMap(mapIDs[i]);
        }
    }

    @Override
    public void update(boolean force) {
        checkDisposed();

        if(mode == RenderMode.GLOBAL) {
            for(BukkitMap map : maps) {
                map.markModified(false);
            }

            updateRender(null);
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            update(player, force);
        }
    }

    @Override
    public void update(Player player, boolean force) {
        checkDisposed();

        if(mode == RenderMode.PLAYER) {
            for(BukkitMap map : maps) {
                map.markModified(false);
            }

            updateRender(player);
        }

        for(BukkitMap map : maps) {
            if(map.isModified() || force) {
                map.send(player);
            }
        }
    }

    @NotNull
    @Override
    public Cancellable scheduleUpdate(int framerate, boolean async) {
        checkDisposed();

        if(task != null && !task.isCancelled()) {
            task.cancel();
        }

        framerate = Math.min(20, Math.max(framerate, 1));
        long ticks = 20 / framerate;
        return task = new BukkitCancellable(() -> update(false)).run(plugin, ticks, async);
    }

    @Override
    public void dispose() {
        checkDisposed();

        if(task != null && !task.isCancelled()) {
            task.cancel();
        }

        plugin.getManager().getDisplays().remove(this);
    }

    protected void updateRender(Player player) {
        checkDisposed();

        super.updateRender(player);

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                maps[x + y * width].draw(image, x * MAP_SIZE, y * MAP_SIZE, MAP_SIZE, MAP_SIZE);
            }
        }
    }

    @Override
    protected void checkDisposed() {
        if(disposed) {
            throw new IllegalStateException("The display has been disposed");
        }
    }

    public int getID() {
        return id;
    }

}
