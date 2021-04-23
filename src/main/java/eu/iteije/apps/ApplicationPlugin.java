package eu.iteije.apps;

import eu.iteije.apps.commands.VortexCommand;
import eu.iteije.apps.listeners.PlayerInteractListener;
import eu.iteije.apps.listeners.PlayerJoinListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ApplicationPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("vortex").setExecutor(new VortexCommand(this));

        PluginManager manager = getServer().getPluginManager();
        PlayerJoinListener joinListener = new PlayerJoinListener();
        manager.registerEvents(joinListener, this);
        manager.registerEvents(new PlayerInteractListener(joinListener.getWand()), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
