package fr.redstonneur1256.maps;

import fr.redstonneur1256.maps.display.Display;
import fr.redstonneur1256.maps.render.RenderMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface DisplayManager<P> {

    /**
     * Create a new display with the specified width and height in maps using the map IDs [mapStart; mapStart + width * height]
     *
     * @param width    the amount of horizontal maps to use
     * @param height   the amount of vertical maps to use
     * @param mode     the rendering mode of the display
     * @param mapStart the ID of the first map
     * @return the newly created display
     * @see DisplayManager#createDisplay(int, int, RenderMode, short...)
     */
    @NotNull
    default Display<P> createDisplay(int width, int height, @NotNull RenderMode mode, short mapStart) {
        short[] mapIDs = new short[width * height];
        for(int i = 0; i < width * height; i++) {
            mapIDs[i] = (short) (mapStart + i);
        }
        return createDisplay(width, height, mode, mapIDs);
    }

    /**
     * Create a new display with the specified width and height in maps using the specified map IDs.
     *
     * @param width  the amount of horizontal maps to use
     * @param height the amount of vertical maps to use
     * @param mode   the rendering mode of the display
     * @param mapIDs an array of the IDs of maps to use
     * @return the newly created display
     * @throws IllegalArgumentException if the {@code mapIDs} length is not equal to {@code width*height}
     */
    @NotNull
    Display<P> createDisplay(int width, int height, @NotNull RenderMode mode, short... mapIDs);

    @NotNull
    List<? extends Display<P>> getDisplays();

    @NotNull
    Optional<? extends Display<P>> getDisplayByMap(short mapID);

}
