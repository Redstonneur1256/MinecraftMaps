package fr.redstonneur1256.maps.spigot.adapter;

import fr.redstonneur1256.maps.utils.Logger;
import fr.redstonneur1256.redutilities.function.Functions;
import fr.redstonneur1256.redutilities.reflection.RConstructor;
import fr.redstonneur1256.redutilities.reflection.RField;
import fr.redstonneur1256.redutilities.reflection.RMethod;
import fr.redstonneur1256.redutilities.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReflectiveAdapter implements Call.VersionAdapter {

    private static final String bukkitPackage;
    private static final String nmsPackage;
    private static final String version;
    private static final int versionValue;
    private static final RMethod getHandleMethod;
    private static final RField connectionField;
    private static final RMethod sendPacketMethod;
    private static final RConstructor<?> packetConstructor;

    static {
        bukkitPackage = "org.bukkit.craftbukkit.";
        nmsPackage = "net.minecraft.server.";
        version = Bukkit.getServer().getClass().getPackage().getName().substring(bukkitPackage.length());

        versionValue = Integer.parseInt(version.split("_")[1]);

        Logger.log(ChatColor.BLUE + "Detected server version " + ChatColor.DARK_AQUA + version);

        Class<?> craftPlayer = Reflection.getClass(bukkitPackage + version + "." + "entity.CraftPlayer");
        Class<?> entityPlayer = getNMSClass("EntityPlayer");
        Class<?> playerConnection = getNMSClass("PlayerConnection");
        Class<?> packet = getNMSClass("Packet");
        Class<?> packetMap = getNMSClass("PacketPlayOutMap");

        List<Class<?>> constructorBuilder = new ArrayList<>(Arrays.asList(int.class, byte.class));
        int booleans = versionValue < 9 ?
                0 :
                versionValue < 13 ?
                        1 :
                        2;
        for(int i = 0; i < booleans; i++) {
            constructorBuilder.add(boolean.class);
        }

        constructorBuilder.addAll(Arrays.asList(Collection.class, byte[].class, int.class, int.class, int.class, int.class));
        Class<?>[] constructorArgs = constructorBuilder.toArray(new Class[0]);

        getHandleMethod = Reflection.getMethod(craftPlayer, "getHandle").setAccessible(true);
        //connectionField = Functions.runtime(() -> entityPlayer.getField("playerConnection"));
        connectionField = Reflection.getField(entityPlayer, "playerConnection").setAccessible(true);
        sendPacketMethod = Reflection.getMethod(playerConnection, "sendPacket", packet).setAccessible(true);
        packetConstructor = Reflection.getConstructor(packetMap, constructorArgs).setAccessible(true);
    }

    private static Class<?> getNMSClass(String name) {
        return Reflection.getClass(nmsPackage + version + "." + name);
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        Object handle = getHandleMethod.invoke(player);
        Object playerConnection = Functions.runtime(() -> connectionField.get(handle));
        sendPacketMethod.invoke(playerConnection, packet);
    }

    @Override
    public void sendMap(Player player, short id, byte scale, List<?> icons, byte[] data, int x, int y, int w, int h) {
        sendPacket(player, createPacket(id, scale, icons, data, x, y, w, h));
    }

    private Object createPacket(short id, byte scale, List<?> icons, byte[] data, int x, int y, int w, int h) {
        return versionValue < 9 ?
                packetConstructor.build(id, scale, icons, data, x, y, w, h) :
                versionValue < 13 ?
                        packetConstructor.build(id, scale, true, icons, data, x, y, w, h) :
                        packetConstructor.build(id, scale, true, true, icons, data, x, y, w, h);
    }


}
