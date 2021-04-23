package eu.iteije.apps.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicInteger;

public class ParticleGenerator {

    private final Plugin plugin;

    public ParticleGenerator(Plugin plugin) {
        this.plugin = plugin;
    }

    public void spawnTimedVortex(Particle particle, Location spawn, float baseRadius, float widthGrowth, float maxHeight, float spacing, int density, int stay, int respawnInTicks) {
        if (stay < 0) stay = 1;
        AtomicInteger secs = new AtomicInteger();
        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        int finalStay = stay * (20 / respawnInTicks);
        int id = scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            if (secs.get() >= finalStay) return;

            this.spawnVortex(particle, spawn, baseRadius, widthGrowth, maxHeight, spacing, density);
            secs.getAndIncrement();
        }, 0L, respawnInTicks);

        scheduler.runTaskLater(plugin, () -> scheduler.cancelTask(id), stay * 20L);
    }

    public void spawnVortex(Particle particle, Location spawn, float baseRadius, float widthGrowth, float maxHeight, float spacing, int density) {
        int ratio = 100 / density;

        spacing = (spacing / 360) * ratio;
        int rotations = (int) Math.ceil(maxHeight / spacing);

        float currentHeight = 0f;

        for (int i = 0; i < rotations; i++) {
            for (int degree = 0; degree < 360; degree += ratio) {
                double radians = Math.toRadians(degree);

                double x = (Math.cos(radians) * baseRadius);
                x += x * (widthGrowth * currentHeight);

                double z = (Math.sin(radians) * baseRadius);
                z += z * (widthGrowth * currentHeight);

                spawn.getWorld().spawnParticle(particle, spawn.clone().add(x, currentHeight, z), 0);

                widthGrowth += (currentHeight / maxHeight) / (maxHeight / spacing);
                currentHeight += spacing;
            }
        }
    }

}
