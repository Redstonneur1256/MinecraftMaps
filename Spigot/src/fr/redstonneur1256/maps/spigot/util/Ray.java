package fr.redstonneur1256.maps.spigot.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.Vector;

import java.util.Optional;

public class Ray {

    public static Result traceRay(Location location, double length, double maxPrecision, double baseStep) {
        Vector vector = location.getDirection().multiply(baseStep);
        double step = baseStep;

        double travelled = 0;

        while(travelled < length) {
            travelled += step;
            location.add(vector);

            if(location.getBlock().getType().isSolid()) {
                travelled -= step;
                location.subtract(vector);

                vector.multiply(0.5);
                step *= 0.5;

                if(step < maxPrecision) {
                    break;
                }
            }
        }

        Optional<ItemFrame> optional = location.getWorld()
                .getNearbyEntities(location, 0.2, 0.2, 0.2)
                .stream()
                .filter(entity -> entity.getType() == EntityType.ITEM_FRAME)
                .map(entity -> (ItemFrame) entity)
                .findFirst(); ItemFrame frame = optional
                .orElse(null);

        if(frame != null) {
            BlockFace face = frame.getAttachedFace();

            Location targetLocation = location.clone().add(0.0625 * face.getModX(), 0, 0.0625 * face.getModZ());
            double distance = targetLocation.distance(location);

            vector.normalize().multiply(distance);

            location.subtract(vector);
        }

        boolean hit = frame != null;
        return new Result(hit ? Type.HIT : Type.MISS, location, frame);
    }

    public enum Type {

        HIT,
        MISS

    }

    public static class Result {

        private Type type;
        private Location hitLocation;
        private ItemFrame entity;

        public Result(Type type, Location hitLocation, ItemFrame entity) {
            this.type = type;
            this.hitLocation = hitLocation;
            this.entity = entity;
        }

        public Type getType() {
            return type;
        }

        public Location getHitLocation() {
            return hitLocation;
        }

        public ItemFrame getEntity() {
            return entity;
        }

    }

}
