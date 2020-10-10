package fr.redstonneur1256.maps.display;

import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;

public interface Renderer {

    void render(BufferedImage image, Player player);

}
