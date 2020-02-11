package me.Travja.TradingCards;

import me.Travja.TradingCards.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

public class Commands implements CommandExecutor {
    public Main plugin;

    public Commands(Main main) {
        this.plugin = main;
    }

    private enum DataType {
        TYPE, SERIES, ATTACK, DEFENCE, RARITY, DETAILS
    }

    private ArrayList<Duel> duels = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("Help")) {
            sender.sendMessage(ChatColor.BLUE + "------==[ Help ]==------");
            sender.sendMessage(ChatColor.AQUA + "/tc help " + ChatColor.BLUE + "- Displays this page!");
            sender.sendMessage(ChatColor.AQUA + "/tc list " + ChatColor.BLUE + "- Shows a list of available cards");
            sender.sendMessage(ChatColor.AQUA + "/tc create <Card Name> <Type> <Series> <Attack> <Defense> <Rarity> [Details] " + ChatColor.BLUE + "- Creates a new card!");
            sender.sendMessage(ChatColor.AQUA + "/tc edit <Card Name> <Property> <Property Value> " + ChatColor.BLUE + "- Edits the card, properties are Type, Series, ATK, DEF, Rarity, and Details");
            sender.sendMessage(ChatColor.AQUA + "/tc delete <Card Name> " + ChatColor.BLUE + "- Deletes the given card! " + ChatColor.RED + "THIS REMOVES ALL DATA FROM THE CARD AND YOU CAN NO LONGER USE IT!");
            sender.sendMessage(ChatColor.AQUA + "/tc rename <Card Name> <New Name> " + ChatColor.BLUE + "- Renames the new card!");
            sender.sendMessage(ChatColor.AQUA + "/tc reload " + ChatColor.BLUE + "- Reloads the plugin!");
            sender.sendMessage(ChatColor.AQUA + "/tc give <Card Name> [Player] " + ChatColor.BLUE + "- Gives you the specified card!");
            sender.sendMessage(ChatColor.AQUA + "/tc giveaway [Rarities]" + ChatColor.BLUE + "- Gives all online players a random card!");
            //sender.sendMessage(ChatColor.AQUA + "/tc duel <Player> " + ChatColor.BLUE + "- Sends a duel request to a player!");
            sender.sendMessage(ChatColor.BLUE + "----------------------");
            return true;
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("Create")) {
                if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.hasPermission("tradingcards.create"))) {
                    if (args.length > 6) {
                        List<String> cards = plugin.config.getStringList("Cards.List");
                        cards.add(args[1].replaceAll("_", " "));
                        plugin.config.set("Cards.List", cards);
                        plugin.config.set("Cards." + args[1].replaceAll("_", " ") + ".Type", args[2]);
                        plugin.config.set("Cards." + args[1].replaceAll("_", " ") + ".Series", args[3]);
                        plugin.config.set("Cards." + args[1].replaceAll("_", " ") + ".ATK", args[4]);
                        plugin.config.set("Cards." + args[1].replaceAll("_", " ") + ".DEF", args[5]);
                        plugin.config.set("Cards." + args[1].replaceAll("_", " ") + ".Rarity", args[6]);
                        List<String> rarity = plugin.config.getStringList("Cards.Rarity." + args[6]);
                        rarity.add(args[1].replaceAll("_", " "));
                        plugin.config.set("Cards.Rarity." + args[6], rarity);
                        if (args.length >= 8) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 7; i < args.length; i++) {
                                if (i != 7)
                                    builder.append(' ');
                                builder.append(args[i]);
                            }
                            String details = builder.toString();
                            plugin.config.set("Cards." + args[1].replaceAll("_", " ") + ".Details", details);
                        }
                        plugin.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "You have successfully created " + ChatColor.DARK_GREEN + args[1].replaceAll("_", " "));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "You are missing some args, type /tc help for info");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("Rename")) {
                if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.hasPermission("tradingcards.rename"))) {
                    if (args.length > 2) {
                        List<String> cards = plugin.config.getStringList("Cards.List");
                        if (cards.contains(args[1].replaceAll("_", " "))) {
                            cards.add(args[2].replaceAll("_", " "));
                            plugin.config.set("Cards.List", cards);
                            plugin.config.set("Cards." + args[2].replaceAll("_", " "), plugin.config.getConfigurationSection("Cards." + args[1].replaceAll("_", " ")));
                            plugin.saveConfig();
                            cards.remove(args[1].replaceAll("_", " "));
                            boolean two = false;
                            int num = 0;
                            for (String c : cards) {
                                if (c.equals(args[2].replaceAll("_", " "))) {
                                    num = num + 1;
                                    if (num == 2)
                                        two = true;
                                }
                            }
                            if (two)
                                cards.remove(args[2].replaceAll("_", " "));
                            plugin.config.set("Cards.List", cards);
                            plugin.config.set("Cards." + args[1].replaceAll("_", " "), null);
                            List<String> replace = new ArrayList<String>();
                            for (String s : plugin.config.getStringList("Cards.Replace")) {
                                replace.add(s);
                            }
                            replace.remove(args[2].replaceAll("_", " ") + "," + args[1].replaceAll("_", " "));
                            replace.remove(args[1].replaceAll("_", " ") + "," + args[2].replaceAll("_", " "));
                            replace.remove(args[1].replaceAll("_", " ") + "," + args[2].replaceAll("_", " "));
                            replace.add(args[1].replaceAll("_", " ") + "," + args[2].replaceAll("_", " "));
                            List<String> defaults = plugin.config.getStringList("Cards.defaults");
                            if (defaults.contains(args[1].replaceAll("_", " "))) {
                                defaults.remove(args[1].replaceAll("_", " "));
                                defaults.add(args[2].replaceAll("_", " "));
                            }
                            if (plugin.config.getConfigurationSection("Cards.Rarity") != null) {
                                Map<String, Object> temp = plugin.config.getConfigurationSection("Cards.Rarity").getValues(false);
                                for (String entry : temp.keySet()) {
                                    List<String> card = plugin.config.getStringList("Cards.Rarity." + entry);
                                    if (card.contains(args[1].replaceAll("_", " "))) {
                                        card.remove(args[1].replaceAll("_", " "));
                                        card.add(args[2].replaceAll("_", " "));
                                    }
                                    plugin.config.set("Cards.Rarity." + entry, card);
                                    plugin.saveConfig();
                                }
                            }
                            plugin.config.set("Cards.defaults", defaults);
                            plugin.config.set("Cards.Replace", replace);
                            plugin.saveConfig();
                            for (Inventory invs : plugin.binders.values()) {
                                for (ItemStack item : invs.getContents()) {
                                    if (item != null) {
                                        if (item.getItemMeta().hasDisplayName()) {
                                            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + args[1].replaceAll("_", " "))) {
                                                ItemMeta im = item.getItemMeta();
                                                im.setDisplayName(ChatColor.GOLD + args[2].replaceAll("_", " "));
                                                item.setItemMeta(im);
                                            }
                                        }
                                    }
                                }
                            }
                            for (Player p : plugin.getServer().getOnlinePlayers()) {
                                List<String> rcards = plugin.config.getStringList("Cards.Replace");
                                Inventory inv = p.getInventory();
                                for (String str : rcards) {
                                    String[] cardinfo = str.split(",");
                                    for (ItemStack item : inv.getContents()) {
                                        if (item != null) {
                                            if (item.getItemMeta().hasDisplayName()) {
                                                if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + cardinfo[0])) {
                                                    ItemMeta im = item.getItemMeta();
                                                    im.setDisplayName(ChatColor.GOLD + cardinfo[1]);
                                                    item.setItemMeta(im);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            sender.sendMessage(ChatColor.DARK_GREEN + args[1].replaceAll("_", " ") + ChatColor.GREEN + " renamed to " + ChatColor.DARK_GREEN + args[2].replaceAll("_", " "));
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "That card doesn't exist, type /tc list to see a list of cards!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You are missing some args, type /tc help for info");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("Give")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (args.length > 1) {
                        if (p.hasPermission("tradingcards.give")) {
                            if (plugin.config.getStringList("Cards.List").contains(args[1].replaceAll("_", " "))) {
                                String names = args[1].replaceAll("_", " ");
                                ItemStack card = Main.crd;
                                card.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
                                ItemMeta im = card.getItemMeta();
                                im.setDisplayName(ChatColor.GOLD + names);
                                im.setLore(ConfigUtils.serializeLore(names));
                                card.setItemMeta(im);
                                if (args.length < 3) {
                                    p.getInventory().addItem(card);
                                    p.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.DARK_GREEN + names);
                                } else {
                                    Player t = Bukkit.getPlayer(args[2]);
                                    if (t != null && t.isOnline()) {
                                        t.getInventory().addItem(card);
                                        t.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.DARK_GREEN + names + ChatColor.GREEN + " by an admin!");
                                    } else {
                                        p.sendMessage(ChatColor.RED + "That player isn't online!");
                                    }
                                }
                                return true;
                            } else {
                                p.sendMessage(ChatColor.RED + "That is not a valid card, type /tc list for a list of cards");
                                return true;
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You don't have permission");
                            return true;
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "You are missing some arguments, type /tc help for info!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "This can't be sent from the console!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("List")) {
                sender.sendMessage(ChatColor.BLUE + "------==[ Cards ]==------");
                for (String cards : plugin.config.getStringList("Cards.List")) {
                    sender.sendMessage(ChatColor.DARK_AQUA + "- " + cards);
                }
                sender.sendMessage(ChatColor.BLUE + "-----------------------");
                return true;
            } else if (args[0].equalsIgnoreCase("Giveaway")) {
                if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.hasPermission("tradingcards.rename"))) {
                    for (Player online : plugin.getServer().getOnlinePlayers()) {
                        Random r = new Random();
                        Inventory inv = online.getInventory();
                        List<String> cards = plugin.config.getStringList("Cards.List");
                        if (!cards.isEmpty()) {
                            boolean fits = false;
                            int o = 0;
                            do {
                                o++;
                                if (o >= 1500)
                                    fits = true;
                                String names = cards.get(r.nextInt(cards.size()));
                                if (args.length > 1) {
                                    ArrayList<String> rares = new ArrayList<>();
                                    for (int i = 1; i < args.length; i++) {
                                        rares.add(args[i].replaceAll("_", " ").toLowerCase());
                                    }
                                    if (rares.contains(String.valueOf(plugin.config.get("Cards." + names + ".Rarity")).toLowerCase())) {
                                        fits = true;
                                    }
                                } else
                                    fits = true;
                                if (fits) {
                                    ItemStack card = Main.crd;
                                    card.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
                                    ItemMeta im = card.getItemMeta();
                                    im.setDisplayName(ChatColor.GOLD + names);
                                    im.setLore(ConfigUtils.serializeLore(names));
                                    card.setItemMeta(im);
                                    inv.addItem(card);
                                    online.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.DARK_GREEN + names + ChatColor.GREEN + " as a reward from the admins!");
                                }
                            } while (!fits);
                        }
                        online.updateInventory();
                    }
                    plugin.getLogger().log(Level.INFO, sender.getName() + " Gave rewards :D");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("Delete")) {
                if (args.length == 2) {
                    if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.hasPermission("tradingcards.delete"))) {
                        List<String> cards = plugin.config.getStringList("Cards.List");
                        if (cards.contains(args[1].replaceAll("_", " "))) {
                            cards.remove(args[1].replaceAll("_", " "));
                            plugin.config.set("Cards.List", cards);
                            plugin.config.set("Cards." + args[1].replaceAll("_", " "), null);
                            plugin.saveConfig();
                            plugin.config.set("Cards.List", cards);
                            plugin.config.set("Cards." + args[1].replaceAll("_", " "), null);
                            List<String> replace = plugin.config.getStringList("Cards.Replace");
                            if (replace.contains(args[1].replaceAll("_", " "))) {
                                replace.add(args[1].replaceAll("_", " "));
                            }
                            plugin.config.set("Cards.Replace", replace);
                            plugin.saveConfig();
                            List<String> defaults = plugin.config.getStringList("Cards.defaults");
                            if (defaults.contains(args[1].replaceAll("_", " "))) {
                                defaults.remove(args[1].replaceAll("_", " "));
                            }
                            plugin.config.set("Cards.defaults", defaults);
                            plugin.saveConfig();
                            if (plugin.config.getConfigurationSection("Cards.Rarity") != null) {
                                Map<String, Object> temp = plugin.config.getConfigurationSection("Cards.Rarity").getValues(false);
                                for (String entry : temp.keySet()) {
                                    List<String> card = plugin.config.getStringList("Cards.Rarity." + entry);
                                    if (card.contains(args[1].replaceAll("_", " "))) {
                                        card.remove(args[1].replaceAll("_", " "));
                                    }
                                    plugin.config.set("Cards.Rarity." + entry, card);
                                    plugin.saveConfig();
                                }
                            }
                            sender.sendMessage(ChatColor.GREEN + "You have successfully deleted " + ChatColor.DARK_GREEN + args[1].replaceAll("_", " "));
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "That isn't a card!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Missing args!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("Edit")) {
                if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.hasPermission("tradingcards.rename"))) {
                    if (args.length > 3) {
                        if (plugin.config.getStringList("Cards.List").contains(args[1].replaceAll("_", " "))) {
                            try {
                                if (DataType.valueOf(args[2]) == DataType.DETAILS) {
                                    if (args.length >= 3) {
                                        StringBuilder builder = new StringBuilder();
                                        for (int i = 3; i < args.length; i++) {
                                            if (i != 3)
                                                builder.append(' ');
                                            builder.append(args[i]);
                                        }
                                        String details = builder.toString();
                                        plugin.config.set("Cards." + args[1].replaceAll("_", " ") + ".Details", details);
                                        sender.sendMessage(ChatColor.GREEN + "You have successfully changed " + ChatColor.DARK_GREEN +
                                                args[1].replaceAll("_", " ") + "'s " + ChatColor.GREEN + "details to " + ChatColor.DARK_GREEN + details);
                                        plugin.saveConfig();
                                        return true;
                                    }
                                } else if (DataType.valueOf(args[2]) == DataType.RARITY) {
                                    plugin.config.set("Cards." + args[1].replaceAll("_", " ") + DataType.valueOf(args[2]), args[3]);
                                    if (plugin.config.getConfigurationSection("Cards.Rarity") != null) {
                                        Map<String, Object> temp = plugin.config.getConfigurationSection("Cards.Rarity").getValues(false);
                                        for (Entry<String, Object> entry : temp.entrySet()) {
                                            if (plugin.config.getStringList("Cards.Rarity." + entry.getKey()) != null) {
                                                List<String> temp2 = plugin.config.getStringList("Cards.Rarity." + entry.getKey());
                                                if (temp2.contains(args[1].replaceAll("_", " "))) {
                                                    temp2.remove(args[1].replaceAll("_", " "));
                                                }
                                                plugin.config.set("Cards.Rarity." + entry.getKey(), temp2);
                                            }
                                        }
                                    }
                                    List<String> rarity = plugin.config.getStringList("Cards.Rarity." + args[3]);
                                    rarity.add(args[1].replaceAll("_", " "));
                                    plugin.config.set("Cards." + args[1].replaceAll("_", " ") + ".Rarity", args[3]);
                                    plugin.config.set("Cards.Rarity." + args[3], rarity);
                                    plugin.saveConfig();
                                    sender.sendMessage(ChatColor.GREEN + "You have successfully changed " + ChatColor.DARK_GREEN +
                                            args[1].replaceAll("_", " ") + "'s " + ChatColor.GREEN + "rarity to " + ChatColor.DARK_GREEN + args[3]);
                                    return true;
                                } else {
                                    plugin.config.set("Cards." + args[1].replaceAll("_", " ") + "." + DataType.valueOf(args[2]), args[3]);
                                    plugin.saveConfig();
                                    sender.sendMessage(ChatColor.GREEN + "You have successfully changed " + ChatColor.DARK_GREEN +
                                            args[1].replaceAll("_", " ") + "'s " + ChatColor.GREEN + DataType.valueOf(args[2]).toString().toLowerCase() + " to " + ChatColor.DARK_GREEN + args[3]);
                                    return true;
                                }
                            } catch (Exception e) {
                                sender.sendMessage(ChatColor.RED + "Invalid property, properties are: Type, Series, ATK, DEF, Rarity and Details.");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "That card doesn't exist, type /tc list for a list of cards!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You are missing some args, type /tc help for info!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("Reload")) {
                if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.hasPermission("tradingcards.reload"))) {
                    plugin.binders.clear();
                    plugin.reloadConfig();
                    plugin.onEnable();
                    sender.sendMessage(ChatColor.GREEN + "Successfully reloaded TradingCards!");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("Duel")) {
                if (!(sender instanceof ConsoleCommandSender)) {
                    if (sender instanceof Player && sender.hasPermission("tradingcards.reload")) {
                        Player p = (Player) sender;
                        Player d = Bukkit.getPlayer(args[1]);
                        if (d != null) {
                            Duel bm = new Duel();
                            bm.addPlayer(p);
                            duels.add(bm);
                            return true;
                        } else {
                            p.sendMessage(ChatColor.RED + args[1] + " is not online!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "This can only be run as a player!");
                    return true;
                }
            }
        }
        return false;
    }
}