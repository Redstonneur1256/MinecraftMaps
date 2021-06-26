package fr.redstonneur1256.maps.spigot.map;

import fr.redstonneur1256.maps.display.DefaultDisplay;
import fr.redstonneur1256.maps.render.MapPalette;
import fr.redstonneur1256.maps.spigot.adapter.Call;
import fr.redstonneur1256.maps.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class BukkitMap {

    private short id;
    private int[] rawColors;
    private byte[] data;
    private int[] tempColors;
    private boolean modified;

    public BukkitMap(short id) {
        int count = DefaultDisplay.MAP_SIZE * DefaultDisplay.MAP_SIZE;

        this.id = id;
        this.rawColors = new int[count];
        this.tempColors = new int[count];
        this.data = new byte[count];

        Arrays.fill(rawColors, 0);
        Arrays.fill(tempColors, 0);
        Arrays.fill(data, (byte) 0);

        MapView map = Bukkit.getMap(id);
        if(map == null) {
            Logger.log(ChatColor.RED + "Failed to remove default renderers for map " + id + ", map may blink in game.");
            return;
        }

        for(MapRenderer mapRenderer : new ArrayList<>(map.getRenderers())) {
            map.removeRenderer(mapRenderer);
        }
        map.setScale(MapView.Scale.FARTHEST);
    }

    public short getID() {
        return id;
    }

    public void draw(BufferedImage image) {
        draw(image, 0, 0, Math.min(image.getWidth(), 128), Math.min(image.getHeight(), 128));
    }

    public void draw(BufferedImage image, int x, int y, int width, int height) {
        int[] rgb = this.tempColors;
        int[] rawColors = this.rawColors;
        byte[] data = this.data;
        boolean modified = this.modified;
        byte[] palette = MapPalette.getPalette();

        // Use a scan width of DefaultDisplay.MAP_SIZE because the array is made for this size
        image.getRGB(x, y, width, height, rgb, 0, DefaultDisplay.MAP_SIZE);

        for(int i = 0; i < rgb.length; i++) {
            int color = rgb[i];
            if(rawColors[i] != color) {
                rawColors[i] = color;
                byte newColor = palette[rgb[i] & 0xFFFFFF];

                if(newColor != data[i]) {
                    data[i] = newColor;
                    modified = true;
                }
            }
        }

        this.modified = modified;
    }

    public void send(Player player) {
        MapView.Scale scale = MapView.Scale.FARTHEST;
        List<?> icons = Collections.emptyList();

        Call.sendMap(player, id, scale.getValue(), icons, data, 0, 0, 128, 128);
    }

    public void markModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isModified() {
        return modified;
    }

}
