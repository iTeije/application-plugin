package eu.iteije.apps.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ParticleGenerator {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public ParticleGenerator(Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    /**
     * Spawn a stationary vortex for a certain amount of seconds and respawn it every given amount of ticks.
     *
     * To make it look smooth, lowering the {@param respawnInTicks} parameter will make it respawn more often and thus make it smoother.
     * In order to prevent issues with this method, I advise against keeping the same density, as it is not needed as more
     *   particles will be spawned anyways and it will significantly improve performance.
     *
     * @param particle particle to use in the vortex
     * @param spawn location the vortex will spin/build around
     * @param baseRadius radius from the spawn location to the first particle
     * @param widthGrowth the exponent to grow the vortex with (updated in this method, might want to take a look at that)
     * @param maxHeight the height the vortex will reach from the spawn point
     * @param spacing amount of spacing (in blocks) in between each 'row' of particles
     * @param density how densely packed the particles are (1-20 recommended)
     * @param stay amount of seconds the vortex should be visible in seconds
     * @param respawnInTicks amount of ticks in between every vortex
     */
    public void spawnTimedVortex(Particle particle, Location spawn, float baseRadius, float widthGrowth, float maxHeight, float spacing,
                                 int density, int stay, int respawnInTicks) {
        // Make sure the vortex is visible for at least a second
        if (stay < 0) stay = 1;
        // Keep track of the current time of visibility
        AtomicInteger secs = new AtomicInteger();

        // Calculate the amount of executions before hitting the given visibility time
        int finalStay = stay * (20 / respawnInTicks);

        // Create and run a bukkit scheduler and execute it every given amount of ticks
        int id = scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            // Exit requirement
            if (secs.get() >= finalStay) return;

            // Spawn the stationary vortex
            this.spawnVortex(particle, spawn, baseRadius, widthGrowth, maxHeight, spacing, 0, density, 80);

            // Update the current visibility time
            secs.getAndIncrement();
        }, 0L, respawnInTicks);

        // Cancel the vortex spawns
        scheduler.runTaskLater(plugin, () -> scheduler.cancelTask(id), stay * 20L);
    }

    /**
     * Spawn a spinning vortex and edit its main properties by tweaking the speed and duration parameters.
     * The {@param densityChange} parameter is a very useful parameter to use when you want to make the vortex
     *   a little less static, as every side parallel to each other when not changed. The higher (20-99 recommended) this variable, the denser
     *   the vortex will get higher up.
     *
     * @param particle particle to use in the vortex
     * @param spawn location the vortex will spin/build around
     * @param baseRadius radius from the spawn location to the first particle
     * @param widthGrowth the exponent to grow the vortex with (updated in this method, might want to take a look at that)
     * @param maxHeight the height the vortex will reach from the spawn point
     * @param spacing amount of spacing (in blocks) in between each 'row' of particles
     * @param speed speed to spin the vortex with, utilizing the offset in degrees (1 - 20)
     * @param duration how long to spin the vortex for in seconds
     * @param density how densely packed the particles are (1-20 recommended)
     * @param densityChange the change in density the higher the vortex reaches (max 100)
     */
    public void spawnSpinningVortex(Particle particle, Location spawn, float baseRadius, float widthGrowth, float maxHeight, float spacing,
                                    float speed, int duration, int density, int densityChange) {
        // Holder for keeping track of the current offset
        AtomicReference<Float> totalOffset = new AtomicReference<>(0.1f);

        // Create and run a bukkit runnable and execute it a given amount of time every second (up to 20, which is the maximum TPS)
        int id = scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            // Spawn the vortex with the current offset
            this.spawnVortex(particle, spawn, baseRadius, widthGrowth, maxHeight, spacing, totalOffset.get(), density, densityChange);

            // Update total offset, applying it to the next vortex
            totalOffset.updateAndGet(v -> v + 1f);
        }, 0L, (int) (20 / speed));

        // Cancel the vortex spawn task after the given amount of time
        scheduler.runTaskLater(plugin, () -> scheduler.cancelTask(id), duration * 20L);
    }

    /**
     * Spawn a vortex.
     *
     * @param particle particle to use in the vortex
     * @param spawn location the vortex will spin/build around
     * @param baseRadius radius from the spawn location to the first particle
     * @param widthGrowth the exponent to grow the vortex with (updated in this method, might want to take a look at that)
     * @param maxHeight the height the vortex will reach from the spawn point
     * @param spacing amount of spacing (in blocks) in between each 'row' of particles
     * @param offset the offset in degrees, ignoring the ratio
     * @param density how densely packed the particles are (1-20 recommended)
     * @param densityChange the change in density the higher the vortex reaches (20-99 recommended)
     */
    public void spawnVortex(Particle particle, Location spawn, float baseRadius, float widthGrowth, float maxHeight, float spacing,
                            float offset, int density, int densityChange) {
        // Calculate how many degrees to skip at each particle in the first rotation
        int ratio = 100 / density;

        // Calculate what distance to add to each particles y axis
        // When the density is lower than a 100%, the ratio affects the height, since height is added to the currentHeight variable
        //   each time a particle spawns
        float originalSpacing = spacing;
        spacing = (spacing / 360) * ratio;
        // Calculate the amount of rotations needed to reach the given max height
        int rotations = (int) Math.ceil(maxHeight / spacing);

        float currentHeight = 0f;

        // Loop through all rotations
        for (int i = 0; i < rotations; i++) {
            // Create a circle and increase the selected point by the given ratio
            for (int degree = 0; degree < 360; degree += ratio) {
                // Convert degrees to radians
                double radians = Math.toRadians(degree + offset);

                // Calculate the x and z coordinates based on the:
                //   baseRadius - the radius from the spawn location to the very first particle
                //   widthGrowth - vortexes increase in size the higher you go. It does not scale automatically so you'll have to play
                //                 it for a bit
                //   currentHeight - related to widthGrowth; the vortex increases in size the higher it goes
                double x = (Math.cos(radians) * baseRadius);
                x += x * (widthGrowth * currentHeight);

                double z = (Math.sin(radians) * baseRadius);
                z += z * (widthGrowth * currentHeight);

                // Spawn the particle at the calculated location (use the spawn location as base)
                spawn.getWorld().spawnParticle(particle, spawn.clone().add(x, currentHeight, z), 0);

                // Increase the radius from the next particle to the center
                widthGrowth += (currentHeight / maxHeight / 2) / (maxHeight / spacing);
                // Add height to the next particle (this will affect the location on the x and y axis)
                currentHeight += spacing;
            }

            // Decrease the offset the higher the vortex goes, you might want to remove this or add it as a variable to this method,
            //   because it might get a little whacky using it on either extremely large, or extremely small vortexes
            offset = offset / 1.1f;
            // Change the density of particles the wider the vortex gets in order to prevent unwanted empty space in spinning vortexes
            int difference = 100 - density;
            if (difference != 0) {
                ratio = (int) (100 / (difference * (1d / densityChange)));
                spacing = (originalSpacing / 360) * ratio;
            }

            if (currentHeight >= maxHeight) break;
        }
    }

}