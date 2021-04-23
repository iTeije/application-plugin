package eu.iteije.apps.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.concurrent.ThreadLocalRandom;

public class EntityChangeBlockListener implements Listener {

    public EntityChangeBlockListener() {

    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        // Make sure a green terracotta block has landed
        if (event.getTo() != Material.GREEN_TERRACOTTA) return;

        // Save the location
        Location loc = event.getBlock().getLocation();

        // Save the x, y and z coordinates of the location
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        // Generate a random sphere radius
        int randomRadius = ThreadLocalRandom.current().nextInt(2, 5);

        // Loop through all x, y and z coordinates within the cube
        for(int x = cx - randomRadius; x <= cx + randomRadius; x++) {
            for(int y = cy - randomRadius; y <= cy + randomRadius; y++) {
                for(int z = cz - randomRadius; z <= cz + randomRadius; z++) {
                    // Calculate the distance from the current coordinates to the center
                    double distance = ((cx - x) * (cx - x) + ((cy - y) * (cy - y)) + ((cz - z) * (cz - z)));

                    // Make sure the current coordinates are within the sphere by checking whether the distance is within radius squared
                    if(distance < Math.pow(randomRadius, 2)) {
                        Location sphereLoc = new Location(loc.getWorld(), x, y, z);
                        loc.getWorld().getBlockAt(sphereLoc).setType(ThreadLocalRandom.current().nextInt(4) >= 1 ? Material.GREEN_TERRACOTTA : Material.GREEN_WOOL);
                    }
                }
            }
        }

        Location signLoc = loc.clone().add(0, randomRadius, 0);
        loc.getWorld().getBlockAt(signLoc).setType(Material.OAK_SIGN);

        Sign sign = (Sign) signLoc.getBlock().getState();
        sign.setLine(1, "A tasty pile");
        sign.setLine(2, "of spinache");

        sign.update();
    }
}
