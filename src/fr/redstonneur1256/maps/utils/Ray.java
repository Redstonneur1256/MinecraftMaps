package fr.redstonneur1256.maps.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.Vector;

import java.util.Optional;

public class Ray {

    private Location location;
    private Vector direction;
    private double maxDistance;
    private double moveDistance;

    public Ray(Location location, Vector direction, double maxDistance, double moveDistance) {
        this.location = location;
        this.direction = direction;
        this.maxDistance = maxDistance;
        this.moveDistance = moveDistance;
    }

    public Ray(Location location, Vector direction, double maxDistance) {
        this(location, direction, maxDistance, 1);
    }

    public Result trace(double closeDistance) {
        Location location = this.location.clone();
        Vector vector = this.direction.clone();
        double moveDistance = this.moveDistance;
        double moved = 0;

        Block block;
        ItemFrame itemFrame = null;
        Location hitLocation = null;
        do {
            location.add(vector);
            block = location.getBlock();


            // TODO: Optimize this
            Optional<ItemFrame> itemFrameOptional = location
                    .getWorld()
                    .getNearbyEntities(location, 0.5, 0.5, 0.5)
                    .stream()
                    .filter(entity -> entity.getType() == EntityType.ITEM_FRAME)
                    .map(ItemFrame.class::cast)
                    .min((itemFrameA, itemFrameB) -> {
                        double distA = itemFrameA.getLocation().distance(location);
                        double distB = itemFrameB.getLocation().distance(location);
                        return Double.compare(distA, distB);
                    });
            if(itemFrameOptional.isPresent()) {
                itemFrame = itemFrameOptional.get();
                hitLocation = location.clone();
            }

            if(block.getType().isSolid()) {
                location.subtract(vector);
                moveDistance /= 2;
                vector.normalize().multiply(moveDistance);
            }

            moved += vector.length();

        } while(moveDistance > closeDistance && moved < maxDistance);

        boolean hit = itemFrame != null;

        return new Result(hit ? Type.hit : Type.miss, hit ? hitLocation : location, itemFrame);
    }

    public enum Type {
        hit,
        miss
    }

    public static class Result {
        private final Type type;
        private final Location hitLocation;
        private final ItemFrame entity;

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
