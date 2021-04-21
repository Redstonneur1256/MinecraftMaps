package fr.redstonneur1256.maps;

import fr.redstonneur1256.maps.display.Display;
import fr.redstonneur1256.maps.render.RenderMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface DisplayManager<P> {

    @NotNull
    Display<P> createDisplay(int width, int height, short mapStart, @NotNull RenderMode mode);

    @NotNull
    List<? extends Display<P>> getDisplays();

    @NotNull
    Optional<? extends Display<P>> getDisplayByMap(short mapID);

}
