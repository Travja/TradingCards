package me.Travja.TradingCards;

import me.Travja.TradingCards.util.CardUtils;
import me.Travja.TradingCards.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Precraft implements Listener {
    public Main plugin;

    public Precraft(Main main) {
        this.plugin = main;
    }

    @EventHandler
    public void precraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getRecipe().getResult();
        boolean next = false;
        if (result.getType() == Material.BOOK && ItemUtils.isNamed(result, ChatColor.RED + "Card Binder")) {
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && item.getType() == Material.BOOK) {
                    if (ItemUtils.startsWith(item, ChatColor.RED + "Card Binder ")) {
                        if (!next) {
                            next = true;
                            ItemStack newi = new ItemStack(result);
                            ItemMeta im = newi.getItemMeta();
                            im.setDisplayName(ChatColor.RED + "Card Binder " + (Integer.valueOf(item.getItemMeta().getDisplayName().substring(14)) + 1));
                            newi.setItemMeta(im);
                            event.getInventory().setResult(newi);
                        }
                    }
                }
                if (!next) {
                    event.getInventory().setResult(null);
                }
            }
        }
        if (result.getType() == Main.crd.getType() && result.getAmount() == 1) {
            String name = event.getInventory().getMatrix()[4].getType().toString().substring(0, 1) +
                    event.getInventory().getMatrix()[4].getType().toString().substring(1).toLowerCase();

            event.getInventory().setResult(CardUtils.getRandomCraftingCard(name));
        }
    }
}
