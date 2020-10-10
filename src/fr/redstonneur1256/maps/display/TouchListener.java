package fr.redstonneur1256.maps.display;

import org.bukkit.entity.Player;

public interface TouchListener {

    void onTouch(Display display, Player player, int x, int y, boolean left);

}
