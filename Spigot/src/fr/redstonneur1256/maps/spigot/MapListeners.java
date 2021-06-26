package fr.redstonneur1256.maps.spigot;

import fr.redstonneur1256.maps.display.DefaultDisplay;
import fr.redstonneur1256.maps.display.Display;
import fr.redstonneur1256.maps.listener.ButtonType;
import fr.redstonneur1256.maps.spigot.map.BukkitDisplay;
import fr.redstonneur1256.maps.spigot.util.Ray;
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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.Optional;

public class MapListeners implements Listener {

    private MinecraftMaps plugin;

    public MapListeners(MinecraftMaps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for(Display<Player> display : plugin.getManager().getDisplays()) {
            display.update(event.getPlayer(), true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getHand() == EquipmentSlot.HAND && hasInteracted(event.getPlayer(), event.getAction())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if(event.getHand() == EquipmentSlot.HAND && hasInteracted(event.getPlayer(), Action.RIGHT_CLICK_AIR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if(!(attacker instanceof Player)) {
            return;
        }

        if(hasInteracted((Player) attacker, Action.LEFT_CLICK_AIR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        for(Display<Player> display : plugin.getManager().getDisplays()) {
            display.update(event.getPlayer(), true);
        }
    }

    private boolean hasInteracted(Player player, Action action) {
        if(plugin.getBypassMode().contains(player.getUniqueId()) || player.getGameMode() == GameMode.SPECTATOR) {
            return false;
        }

        // TODO: Better detection (add a box around displays and check from displays)
        if(player.getWorld()
                .getNearbyEntities(player.getLocation(), 10, 10, 10)
                .stream()
                .noneMatch(entity -> entity instanceof ItemFrame)) {
            return false;
        }

        double distance = player.getGameMode() == GameMode.CREATIVE ? 5 : 3;

        Ray.Result result = Ray.traceRay(player.getLocation().add(0, player.getEyeHeight(), 0), distance, 0.0001, 0.2);

        if(result.getType() != Ray.Type.HIT) {
            return false;
        }

        Location mapLocation = result.getHitLocation();
        ItemFrame itemFrame = result.getEntity();
        BlockFace face = itemFrame.getAttachedFace();
        ItemStack item = itemFrame.getItem();
        if(item == null || item.getType() != Material.MAP) {
            return false;
        }

        short mapId = item.getDurability();

        Location diff = itemFrame.getLocation().subtract(0.5 * face.getModZ(), 0.5, 0.5 * face.getModX());
        mapLocation.subtract(diff);

        Optional<BukkitDisplay> displayOptional = plugin.getManager().getDisplayByMap(mapId);
        if(!displayOptional.isPresent()) {
            return false;
        }
        BukkitDisplay display = displayOptional.get();

        Point point = display.getMapLocation(mapId);
        assert point != null : "The display is from this map";

        double x1 = mapLocation.getX() * face.getModZ();
        double x2 = mapLocation.getZ() * face.getModX();
        
        if(x1 != 0) {
            x1 = 1 - x1;
        }

        int hitPixelX = (int) Math.floor((x1 + x2) * DefaultDisplay.MAP_SIZE);
        int hitPixelY = (int) Math.floor((1 - mapLocation.getY()) * DefaultDisplay.MAP_SIZE);

        int clickX = hitPixelX + point.x * DefaultDisplay.MAP_SIZE;
        int clickY = hitPixelY + point.y * DefaultDisplay.MAP_SIZE;

        ButtonType button = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK ?
                ButtonType.LEFT :
                ButtonType.RIGHT;

        display.onTouch(player, clickX, clickY, button);

        return true;
    }

}
