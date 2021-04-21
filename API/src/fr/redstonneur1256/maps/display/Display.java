package fr.redstonneur1256.maps.display;

import fr.redstonneur1256.maps.listener.TouchListener;
import fr.redstonneur1256.maps.render.Renderer;
import fr.redstonneur1256.maps.task.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

/**
 * @param <P> the Player type for implementation
 */
public interface Display<P> {

    /**
     * Get the amount of maps needed to complete the full width of this display
     */
    int getWidth();

    /**
     * Get the amount of maps needed to complete the full height of this display
     */
    int getHeight();

    /**
     * The width of the display in pixels
     * @see Display#getWidth()
     */
    int getWidthResolution();

    /**
     * The height of the display in pixels
     * @see Display#getHeight() ()
     */
    int getHeightResolution();

    /**
     * @param force if true the map will be sent to the player even if they weren't modified
     */
    void update(boolean force);

    /**
     * @param force if true the map will be sent to the player even if they weren't modified
     */
    void update(P player, boolean force);

    /**
     * Update the display at a fixed framerate
     *
     * @param framerate the display framerate from 1 to 20
     * @param async false if it should use the main server thread for rendering
     * @return a Cancellable task of this update
     */
    @NotNull
    Cancellable scheduleUpdate(int framerate, boolean async);

    /**
     * Disposes this display and cancel the updates
     */
    void dispose();

    /**
     * Check if the display contains a map with the specified ID
     */
    boolean containsMap(short mapID);

    /**
     * Get the location of the map on this display
     */
    @Nullable
    Point getMapLocation(short mapID);

    /**
     * Add a renderer that will be called at every update of the display
     */
    void addRenderer(@NotNull Renderer<P> renderer);

    /**
     * Remove a renderer
     */
    void removeRenderer(@NotNull Renderer<P> renderer);

    /**
     * Get the list of all current renderers
     */
    @NotNull
    List<Renderer<P>> getRenderers();

    /**
     * Add a listener for when this Display is touched
     */
    void addListener(@NotNull TouchListener<P> listener);

    /**
     * Remove a listener
     */
    void removeListener(@NotNull TouchListener<P> listener);

    /**
     * Get the list of all current listeners
     */
    @NotNull
    List<TouchListener<P>> getListeners();

}
