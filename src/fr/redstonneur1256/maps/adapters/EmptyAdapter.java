package fr.redstonneur1256.maps.adapters;

import org.bukkit.entity.Player;

import java.util.List;

public class EmptyAdapter implements Call.VersionAdapter {
    @Override
    public void sendPacket(Player player, Object packet) {

    }

    @Override
    public void sendMap(Player player, short id, byte scale, List<?> icons, byte[] data, int x, int y, int w, int h) {

    }

}
