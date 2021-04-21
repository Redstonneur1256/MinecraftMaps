package fr.redstonneur1256.maps.display;

import fr.redstonneur1256.maps.listener.ButtonType;
import fr.redstonneur1256.maps.listener.TouchListener;
import fr.redstonneur1256.maps.render.RenderMode;
import fr.redstonneur1256.maps.render.Renderer;
import fr.redstonneur1256.maps.utils.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DefaultDisplay<P> implements Display<P> {

    public static final int MAP_SIZE = 128;

    protected int width;
    protected int height;
    protected short mapStart;
    protected RenderMode mode;
    protected List<Renderer<P>> renderers;
    protected List<TouchListener<P>> listeners;
    protected BufferedImage image;
    protected Graphics2D graphics;
    public DefaultDisplay(int width, int height, short mapStart, RenderMode mode) {
        this.width = width;
        this.height = height;
        this.mapStart = mapStart;
        this.mode = mode;
        this.renderers = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.image = new BufferedImage(width * MAP_SIZE, height * MAP_SIZE, BufferedImage.TYPE_INT_ARGB);
        this.graphics = image.createGraphics();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidthResolution() {
        return getWidth() * MAP_SIZE;
    }

    @Override
    public int getHeightResolution() {
        return getHeight() * MAP_SIZE;
    }

    @Override
    public boolean containsMap(short mapID) {
        return mapID >= mapStart && mapID < mapStart + width * height;
    }

    @Override
    public Point getMapLocation(short mapID) {
        if(!containsMap(mapID)) {
            return null;
        }
        int position = mapID - mapStart;
        return new Point(position % width, position / width);
    }

    @Override
    public void addRenderer(@NotNull Renderer<P> renderer) {
        Objects.requireNonNull(renderer, "Renderer cannot be null");
        renderers.add(renderer);
    }

    @Override
    public void removeRenderer(@NotNull Renderer<P> renderer) {
        renderers.remove(renderer);
    }

    @Override
    public @NotNull List<Renderer<P>> getRenderers() {
        return renderers;
    }

    @Override
    public void addListener(@NotNull TouchListener<P> listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull TouchListener<P> listener) {
        listeners.remove(listener);
    }

    @Override
    public @NotNull List<TouchListener<P>> getListeners() {
        return listeners;
    }

    public void onTouch(P player, int x, int y, ButtonType button) {
        for(TouchListener<P> listener : listeners) {
            try {
                listener.touched(player, x, y, button);
            }catch(Throwable exception) {
                Logger.log("&cFailed to handle listener " + listener + ":", exception);
            }
        }
    }

    protected void updateRender(P player) {
        BufferedImage image = this.image;
        Graphics2D graphics = this.graphics;

        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        for(Renderer<P> renderer : renderers) {
            renderer.render(image, graphics, player);
        }
    }

    protected abstract void checkDisposed();

}
