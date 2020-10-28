package fr.redstonneur1256.maps.maps;

import fr.redstonneur1256.maps.MinecraftMaps;
import fr.redstonneur1256.maps.adapters.Call;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleMap {

    private short id;
    private byte[] data;

    public SimpleMap(World world, short id) {
        this.id = id;
        this.data = new byte[128 * 128];

        Arrays.fill(data, (byte) 0);

        MapView map = Bukkit.getMap(id);
        if(map == null) {
            MinecraftMaps.log(ChatColor.RED + "Failed to remove default renderers for map " + id + ", map can blink.");
            return;
        }
        for(MapRenderer renderer : new ArrayList<>(map.getRenderers())) {
            map.removeRenderer(renderer);
        }
        map.setScale(MapView.Scale.FARTHEST);
    }

    public void draw(BufferedImage image) {
        int[] rgb = new int[data.length];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());

        for(int i = 0; i < rgb.length; i++) {
            data[i] = MapColors.palette.matchColor(rgb[i]).id;
        }
    }

    public void send(Player player) {
        MapView.Scale scale = MapView.Scale.FARTHEST;
        List<?> icons = new ArrayList<>();

        Call.sendMap(player, id, (byte) scale.ordinal(), icons, data, 0, 0, 128, 128);

    }

    public short getId() {
        return id;
    }

}
