package eu.iteije.apps.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerInteractListener implements Listener {

    private final ItemStack wand;
    private final int maxRadius;

    private final int particlesPerBlock;

    public PlayerInteractListener(ItemStack wand) {
        this.wand = wand;
        this.maxRadius = 50;

        this.particlesPerBlock = 2;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();

        // Stop if the the off hand interact has been triggered
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        // Stop if the selected item is NOT the spinach wand
        if (!player.getInventory().getItemInMainHand().equals(wand)) return;

        // Stop if the player has clicked a block instead of air
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
            player.sendMessage(translate("&cYou are not allowed (yet) to submerge yourself in spinach or use your laser this close."));
            return;
        }

        // Save the player location and world for later
        Location playerLocation = player.getLocation();
        World world = playerLocation.getWorld();

        // Stop if the world does not exist. This has never happened to me but IntelliJ keeps complaining
        if (world == null) return;

        // Laser beam (right click)
        if (action == Action.RIGHT_CLICK_AIR) {
            // Get the direction and multiply it so that a higher amount of particles can be spawned
            Vector direction = player.getEyeLocation().getDirection();
            direction.multiply(1f / this.particlesPerBlock);

            // Clone the player location and add eye-height to the y axis
            Location clone = playerLocation.clone();
            clone.add(0, 1.5, 0);

            // Loop for spawning particles and.. you know.. 'special effects'
            for (int i = 0; i < this.maxRadius * this.particlesPerBlock; i++) {
                // Add the direction to the clone location
                clone.add(direction);

                // Spawn the redstone particle
                world.spawnParticle(Particle.REDSTONE, clone, 1, new Particle.DustOptions(Color.RED, 1));

                // If the block coordinates have been changed, look for blocks to destroy
                if (i % this.particlesPerBlock == 0) {
                    // Clone the clone and save the block
                    Location locationClone = clone.clone();
                    Block block = locationClone.getBlock();

                    // Check whether the block is solid or not
                    if (block.getType().isSolid()) {
                        // Spawn an explosion particle at the current location
                        world.spawnParticle(Particle.EXPLOSION_NORMAL, locationClone, 1);
                        // Play the explosion sound
                        world.playSound(locationClone, Sound.ENTITY_GENERIC_EXPLODE, 1, 0);
                        // Break the block
                        block.breakNaturally();
                    }
                }

                // At the end of the laser beam, create an explosion
                // The beam gets through everything anyways, no need to do the explosion at the first block, that's not fun
                if (i == this.maxRadius * this.particlesPerBlock - 1) {
                    world.createExplosion(clone, 4);
                }
            }

        }

        // Spinach clumps
        if (action == Action.LEFT_CLICK_AIR) {
            // Clone the player location and add eye-height to the y axis
            Location clone = playerLocation.clone();
            clone.add(0, 1.5, 0);

            // Spawn a new green terracotta block as an instance of FallingBlock and make sure the block can not be picked up mid air
            FallingBlock block = world.spawnFallingBlock(clone, Material.GREEN_TERRACOTTA.createBlockData());
            block.setDropItem(false);

            // Set velocity to the block
            Vector direction = player.getEyeLocation().getDirection();
            block.setVelocity(direction.multiply(new Vector(1.5, 1.25, 1.5)));
        }

    }

    private String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
