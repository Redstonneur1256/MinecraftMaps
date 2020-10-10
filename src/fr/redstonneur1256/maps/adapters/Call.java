package fr.redstonneur1256.maps.adapters;

import fr.redstonneur1256.maps.MinecraftMaps;
import fr.redstonneur1256.redutilities.Utils;
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

            String className = "fr.redstonneur1256.maps.adapters." + version;

            Class<?> targetAdapter = Class.forName(className);

            adapter = (VersionAdapter) targetAdapter.newInstance();
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
            MinecraftMaps.log(ChatColor.BLUE + "Using version adapter " + ChatColor.DARK_AQUA + adapter.getClass().getName());
        }else{
            MinecraftMaps.log(ChatColor.RED + "Failed to find version adapter, please report this issue, stacktrace:");
            String message = Utils.errorMessage(error);
            for(String line : message.split("\n")) {
                MinecraftMaps.log(ChatColor.RED + line);
            }
        }

    }

    public static void sendPacket(Player player, Object packet) {
        adapter.sendPacket(player, packet);
    }

    public static void sendMap(Player player, short id, byte scale, boolean b, List<?> icons, byte[] data,
                               int x, int y, int w, int h) {
        adapter.sendMap(player, id, scale, b, icons, data, x, y, w, h);
    }


    public interface VersionAdapter {

        void sendPacket(Player player, Object packet);

        void sendMap(Player player, short id, byte scale, boolean b, List<?> icons, byte[] data, int x, int y,
                     int w, int h);

    }

}
