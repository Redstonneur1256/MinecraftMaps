package fr.redstonneur1256.maps.adapters;

import net.minecraft.server.v1_12_R1.MapIcon;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutMap;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("unchecked")
public class v1_12_R1 implements Call.VersionAdapter {

    @Override
    public void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>) packet);
    }

    @Override
    public void sendMap(Player player, short id, byte scale, boolean b, List<?> icons, byte[] data, int x, int y,
                        int w, int h) {

        Object packet = new PacketPlayOutMap(id, scale, b, (List<MapIcon>) icons, data, x, y, w, h);
        sendPacket(player, packet);
    }

}
