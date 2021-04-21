package fr.redstonneur1256.maps.spigot.util;

import fr.redstonneur1256.maps.task.Cancellable;
import fr.redstonneur1256.maps.spigot.MinecraftMaps;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class BukkitCancellable extends BukkitRunnable implements Cancellable {

    private Runnable runnable;

    public BukkitCancellable(Runnable runnable) {
        this.runnable = Objects.requireNonNull(runnable, "Runnable cannot be null");
    }

    public BukkitCancellable run(MinecraftMaps plugin, long ticks, boolean async) {
        if(async) {
            runTaskTimerAsynchronously(plugin, 0, ticks);
        }else {
            runTaskTimer(plugin, 0, ticks);
        }

        return this;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        }catch(Exception exception) {
            exception.printStackTrace();
        }
    }

}
