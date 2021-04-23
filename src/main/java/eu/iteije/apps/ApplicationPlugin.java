package eu.iteije.apps;

import eu.iteije.apps.commands.VortexCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ApplicationPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("vortex").setExecutor(new VortexCommand(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
