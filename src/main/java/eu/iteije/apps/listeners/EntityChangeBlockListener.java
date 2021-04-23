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
        if (event.getTo() != Material.GREEN_TERRACOTTA) return;

        Location loc = event.getBlock().getLocation();

        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        int randomRadius = ThreadLocalRandom.current().nextInt(2, 5);

        for(int x = cx - randomRadius; x <= cx + randomRadius; x++) {
            for(int y = cy - randomRadius; y <= cy + randomRadius; y++) {
                for(int z = cz - randomRadius; z <= cz + randomRadius; z++) {
                    double distance = ((cx-x) * (cx-x) + ((cz-z) * (cz-z)) + ((cy-y) * (cy-y)));

                    if(distance < randomRadius * randomRadius) {

                        Location l = new Location(loc.getWorld(), x, y, z);
                        loc.getWorld().getBlockAt(l).setType(ThreadLocalRandom.current().nextInt(4) >= 1 ? Material.GREEN_TERRACOTTA : Material.GREEN_WOOL);
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
