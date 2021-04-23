package eu.iteije.apps.listeners;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerJoinListener implements Listener {

    @Getter private final ItemStack wand;

    public PlayerJoinListener() {
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(translate("&a&lSpinach Beam"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setLore(translate(
                    "&7",
                    "&7This tool allows you to throw clumps of",
                    "&7spinach up to &e50 &7blocks away.",
                    "&7",
                    "&eLeft-click to throw spinach.",
                    "&bRight-click to use your destructive laser beam."));
            wand.setItemMeta(meta);

            this.wand = wand;
        } else {
            this.wand = null;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (wand != null) {
            event.getPlayer().getInventory().setItem(0, this.wand);
        }
    }

    private String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private List<String> translate(String... messages) {
        return Arrays.stream(messages).map(s -> s = translate(s)).collect(Collectors.toList());
    }

}
