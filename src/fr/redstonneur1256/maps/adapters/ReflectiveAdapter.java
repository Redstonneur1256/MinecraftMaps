package fr.redstonneur1256.maps.adapters;

import fr.redstonneur1256.redutilities.reflection.RConstructor;
import fr.redstonneur1256.redutilities.reflection.RField;
import fr.redstonneur1256.redutilities.reflection.RMethod;
import fr.redstonneur1256.redutilities.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class ReflectiveAdapter implements Call.VersionAdapter {


    private static final String bukkitPackage;
    private static final String nmsPackage;
    private static final String version;
    private static final RMethod getHandleMethod;
    private static final RField connectionField;
    private static final RMethod sendPacketMethod;
    private static final RConstructor packetConstructor;

    static {
        bukkitPackage = "org.bukkit.craftbukkit.";
        nmsPackage = "net.minecraft.server.";
        version = Bukkit.getServer().getClass().getPackage().getName().substring(bukkitPackage.length());

        Class<?> craftPlayer = getBukkitClass("entity.CraftPlayer");
        Class<?> entityPlayer = getNMSClass("EntityPlayer");
        Class<?> playerConnection = getNMSClass("PlayerConnection");
        Class<?> packet = getNMSClass("Packet");
        Class<?> packetMap = getNMSClass("PacketPlayOutMap");

        getHandleMethod = Reflection.getMethod(craftPlayer, "getHandle", true).setAccessible(true);
        connectionField = Reflection.getField(entityPlayer, "playerConnection", true).setAccessible(true);
        sendPacketMethod = Reflection.getMethod(playerConnection, "sendPacket", true, packet).setAccessible(true);
        packetConstructor = Reflection.getConstructor(packetMap, true, int.class, byte.class, boolean.class, Collection.class,
                byte[].class, int.class, int.class, int.class, int.class).setAccessible(true);
    }

    private static Class<?> getBukkitClass(String name) {
        return Reflection.getClass(bukkitPackage + version + "." + name);
    }

    private static Class<?> getNMSClass(String name) {
        return Reflection.getClass(nmsPackage + version + "." + name);
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        Object handle = getHandleMethod.invoke(player);
        Object playerConnection = connectionField.get(handle);
        sendPacketMethod.invoke(playerConnection, packet);
    }

    @Override
    public void sendMap(Player player, short id, byte scale, boolean b, List<?> icons, byte[] data, int x, int y, int w, int h) {
        sendPacket(player, createPacket(id, scale, b, icons, data, x, y, w, h));
    }

    private Object createPacket(short id, byte scale, boolean b, List<?> icons, byte[] data, int x, int y, int w, int h) {
        return packetConstructor.build(id, scale, b, icons, data, x, y, w, h);
    }


}
