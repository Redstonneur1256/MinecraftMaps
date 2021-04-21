package fr.redstonneur1256.maps.listener;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TouchListener<P> {

    void touched(@NotNull P player, int x, int y, @NotNull ButtonType button);

}
