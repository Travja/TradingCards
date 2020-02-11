package me.Travja.TradingCards.util;

import me.Travja.TradingCards.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigUtils {

    private static FileConfiguration config;
    private static HashMap<String, Object> cache = new HashMap<>();

    static {
        config = Main.getInstance().config;
    }

    public static boolean isDebug() {
        if(checkCache("debug", Boolean.class)) {
            return (boolean) cache.get("debug");
        }

        boolean debug = config.getBoolean("debug");
        cache.put("debug", debug);

        return debug;
    }

    public static String getString(String str) {
        if (checkCache(str, String.class))
            return (String) cache.get(str);

        if (config.contains(str)) {
            String ret = config.getString(str);
            cache.put(str, ret);
            return ret;
        }
        return str + " not found.";
    }

    public static List<String> getStringList(String str) {
        if (checkCache(str, List.class))
            return (List<String>) cache.get(str);

        if (config.contains(str)) {
            List<String> ret = config.getStringList(str);
            cache.put(str, ret);
            return ret;
        }
        return Arrays.asList(str + " not found.");
    }

    public static int getInt(String str) {
        if (checkCache(str, Integer.class))
            return (int) cache.get(str);

        if (config.contains(str)) {
            int ret = config.getInt(str);
            cache.put(str, ret);
            return ret;
        }
        return -1;
    }

    private static boolean checkCache(String str, Class type) {
        boolean found = cache.containsKey(str) && type.isInstance(cache.get(str));
        if (found)
            Main.getInstance().getLogger().info("Found cached value for " + str);
        return found;
    }


    public static ArrayList<String> serializeLore(String names) {
        ArrayList<String> lore = getStats(names);

        String details = ConfigUtils.getString("Cards." + names + ".Details");
        if (details != null) {
            lore.add(ChatColor.DARK_PURPLE + "************");
            lore.addAll(wrapDetails(details));
        }

        return lore;
    }

    private static ArrayList<String> wrapDetails(String details) {
        ArrayList<String> lore = new ArrayList<>();
        if (details.length() > 25) {
            boolean first = true;
            while (details.length() > 25) {
                lore.add((first ? ChatColor.AQUA + "Details: " + ChatColor.BLUE : ChatColor.BLUE) + details.substring(0, 14));
                details = details.substring(14);
                first = false;
            }
        } else {
            lore.add(ChatColor.AQUA + "Details: " + ChatColor.BLUE + details);
        }
        return lore;
    }

    private static ArrayList<String> getStats(String names) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "Type: " + ConfigUtils.getString("Cards." + names + ".Type"));
        lore.add(ChatColor.YELLOW + "Series: " + ConfigUtils.getString("Cards." + names + ".Series"));
        lore.add(ChatColor.RED + "ATK: " + ChatColor.DARK_RED + ConfigUtils.getString("Cards." + names + ".ATK"));
        lore.add(ChatColor.GREEN + "DEF: " + ChatColor.DARK_GREEN + ConfigUtils.getString("Cards." + names + ".DEF"));
        lore.add(ChatColor.GRAY + "Rarity: " + ChatColor.DARK_GRAY + ConfigUtils.getString("Cards." + names + ".Rarity"));
        return lore;
    }

}
