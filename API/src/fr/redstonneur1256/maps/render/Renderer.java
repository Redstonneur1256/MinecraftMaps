package fr.redstonneur1256.maps.render;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;

@FunctionalInterface
public interface Renderer<P> {

    void render(@NotNull BufferedImage image, @NotNull Graphics2D graphics, @Nullable P player);

}
