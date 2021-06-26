package fr.redstonneur1256.maps.spigot.adapter;

import org.bukkit.entity.Player;

import java.util.List;

public class EmptyAdapter implements Call.VersionAdapter {

    @Override
    public void sendPacket(Player player, Object packet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendMap(Player player, short id, byte scale, List<?> icons, byte[] data, int x, int y, int w, int h) {
        throw new UnsupportedOperationException();
    }

}
