package fr.redstonneur1256.maps.spigot.adapter;

import fr.redstonneur1256.maps.utils.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Call {

    private static VersionAdapter adapter;

    public static void setup() {
        Throwable error = null;

        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String version = packageName.substring("org.bukkit.craftbukkit.".length());

            String className = "fr.redstonneur1256.maps.spigot.adapter." + version;

            Class<?> targetAdapter = Class.forName(className);

            adapter = (VersionAdapter) targetAdapter.getConstructor().newInstance();
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
            // If version is not supported, try using reflection:

            try {
                adapter = new ReflectiveAdapter();
            }catch(ExceptionInInitializerError initializationError) {
                adapter = new EmptyAdapter();
                error = initializationError;
            }

        }catch(Exception exception) {
            // If another error occurs:

            error = exception;
            adapter = new EmptyAdapter();
        }

        if(error == null) {
            Logger.log(ChatColor.BLUE + "Using version adapter " + ChatColor.DARK_AQUA + adapter.getClass().getSimpleName());
        }else {
            Logger.log(ChatColor.RED + "Failed to find version adapter, please report this issue, stacktrace:", error);
        }
    }

    public static boolean isEnabled() {
        return adapter != null && !(adapter instanceof EmptyAdapter);
    }

    public static void sendPacket(Player player, Object packet) {
        adapter.sendPacket(player, packet);
    }

    public static void sendMap(Player player, short id, byte scale, List<?> icons, byte[] data,
                               int x, int y, int w, int h) {
        adapter.sendMap(player, id, scale, icons, data, x, y, w, h);
    }


    public interface VersionAdapter {

        void sendPacket(Player player, Object packet);

        void sendMap(Player player, short id, byte scale, List<?> icons, byte[] data, int x, int y,
                     int w, int h);

    }

}
