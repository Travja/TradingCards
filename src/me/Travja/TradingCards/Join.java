package me.Travja.TradingCards;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

public class Join implements Listener {
    public Main plugin;

    public Join(Main main) {
        this.plugin = main;
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        File f = plugin.getFolder(p.getName());
        for (File b : f.listFiles()) {
            plugin.reloadBinder(f.getName(), Integer.valueOf(b.getName().substring(0, b.getName().indexOf('.'))));
        }
    }
}