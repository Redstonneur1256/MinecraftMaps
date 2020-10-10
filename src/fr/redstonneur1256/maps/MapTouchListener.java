package fr.redstonneur1256.maps;

import fr.redstonneur1256.maps.display.Display;
import fr.redstonneur1256.maps.utils.Ray;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.Optional;

public class MapTouchListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(hasInteracted(event.getPlayer(), event.getAction())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        if(hasInteracted(event.getPlayer(), Action.RIGHT_CLICK_AIR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        Entity killer = event.getDamager();
        if(!(killer instanceof Player)) {
            return;
        }

        if(hasInteracted((Player) killer, Action.LEFT_CLICK_AIR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        MinecraftMaps minecraftMaps = MinecraftMaps.getInstance();
        for(Display display : minecraftMaps.getDisplays()) {
            display.update(event.getPlayer());
        }
    }

    private boolean hasInteracted(Player player, Action action) {
        if(player.getGameMode() == GameMode.SPECTATOR)
            return false;

        // TODO: Better detection (add a box around displays and check from displays)
        if(player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10)
                .stream()
                .noneMatch(entity -> entity instanceof ItemFrame)) {
            return false;
        }

        MinecraftMaps minecraftMaps = MinecraftMaps.getInstance();
        Location location = player.getLocation().clone().add(0, 1.5, 0); // Eye location
        Vector direction = location.getDirection().normalize();

        double distance = player.getGameMode() == GameMode.CREATIVE ? 5 : 3;

        Ray ray = new Ray(location, direction, distance);
        Ray.Result result = ray.trace(0.0001);

        if(result.getType() != Ray.Type.hit) {
            return false;
        }

        Location mapLocation = result.getHitLocation();
        ItemFrame itemFrame = result.getEntity();
        BlockFace face = itemFrame.getAttachedFace();
        ItemStack item = itemFrame.getItem();
        if(item == null || item.getType() != Material.MAP)
            return false;

        short mapId = item.getDurability();

        Location diff = itemFrame.getLocation().subtract(0.5 * face.getModZ(), 0.5, 0.5 * face.getModX());
        mapLocation.subtract(diff);

        Optional<Display> displayOptional = minecraftMaps.getDisplayByMap(mapId);
        if(!displayOptional.isPresent())
            return false;
        Display display = displayOptional.get();

        Point point = display.getLocation(mapId);
        assert point != null : "The display is from this map";

        double x1 = mapLocation.getX() * face.getModZ();
        double x2 = mapLocation.getZ() * face.getModX();
        if(x1 != 0)
            x1 = 1 - x1;

        int hitPixelX = (int) Math.round((x1 + x2) * 128);
        int hitPixelY = (int) Math.round((1 - mapLocation.getY()) * 128);

        int clickX = hitPixelX + point.x * 128;
        int clickY = hitPixelY + point.y * 128;

        display.onClick(player, clickX, clickY, action);

        return true;
    }

}
