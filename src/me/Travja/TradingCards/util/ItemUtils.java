package me.Travja.TradingCards.util;

import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    public static boolean validateDisplay(ItemStack it) {
        return (it != null && it.hasItemMeta() && it.getItemMeta().hasDisplayName());
    }

    public static boolean isNamed(ItemStack it, String name) {
        if(!validateDisplay(it))
            return false;
        return it.getItemMeta().getDisplayName().equals(name);
    }

    public static boolean startsWith(ItemStack it, String name) {
        if(!validateDisplay(it))
            return false;
        return it.getItemMeta().getDisplayName().startsWith(name);
    }

}
