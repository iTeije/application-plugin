package eu.iteije.apps.commands;

import eu.iteije.apps.utils.ParticleGenerator;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VortexCommand implements CommandExecutor {

    private final ParticleGenerator generator;

    public VortexCommand(Plugin plugin) {
        this.generator = new ParticleGenerator(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available to players.");
            return true;
        }

        Player player = (Player) sender;

        this.generator.spawnTimedVortex(Particle.CLOUD, player.getLocation(), 2.5f, .08f, 30, 2f, 15, 5, 10);

        return true;
    }
}
