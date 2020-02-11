package me.Travja.TradingCards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class Duel implements Listener {
    private ArrayList<UUID> players = new ArrayList<>();
    private HashMap<ItemStack, UUID> cards = new HashMap<>();
    private HashMap<UUID, ArrayList<ItemStack>> exhaust = new HashMap<>();
    private Inventory arena = Bukkit.createInventory(null, 27, "§c§lBattle Mat");

    public Duel() {
        arena.setItem(4, new ItemStack(Material.IRON_BARS));
        arena.setItem(13, new ItemStack(Material.RED_WOOL));
        arena.setItem(22, new ItemStack(Material.IRON_BARS));
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void addPlayer(Player p) {
        players.add(p.getUniqueId());
        cards.put(null, p.getUniqueId());
        exhaust.put(p.getUniqueId(), new ArrayList<>());
    }

    public void removePlayer(Player p) {
        ItemStack card = null;
        players.remove(p.getUniqueId());
        for (ItemStack is : cards.keySet())
            if (cards.get(is).equals(p.getUniqueId()))
                card = is;
        cards.remove(card);
        /**ItemMeta cm = card.getItemMeta(); TODO Put this somewhere for the cards :D
         *m.getLore().add(hideText(UUID.randomUUID().toString()));
         * card.setItemMeta(cm);**/
    }

    public Collection<Player> getPlayers() {
        ArrayList<Player> ret = new ArrayList<>();
        for (UUID id : players)
            ret.add(Bukkit.getPlayer(id));
        return ret;
    }

    public void battle(ItemStack card1, ItemStack card2) {
        ItemMeta im1 = card1.getItemMeta();
        ItemMeta im2 = card2.getItemMeta();
        int atk1 = Integer.parseInt(im1.getLore().get(2));
        int def1 = Integer.parseInt(im1.getLore().get(3));
        int atk2 = Integer.parseInt(im2.getLore().get(2));
        int def2 = Integer.parseInt(im2.getLore().get(3));
        double p1 = def2 / atk1;
        double p2 = def1 / atk2;
        if (p2 == p1) {
            exhaust.get(cards.get(card1)).add(card1);
            exhaust.get(cards.get(card2)).add(card2);
            getPlayers().forEach(pl -> pl.sendMessage(ChatColor.RED + "Both cards lose!"));
        } else {
            Player winner = (p1 < p2) ? Bukkit.getPlayer(cards.get(card1)) : Bukkit.getPlayer(cards.get(card2));
            Player loser = (p1 < p2) ? Bukkit.getPlayer(cards.get(card2)) : Bukkit.getPlayer(cards.get(card1));

            ItemStack winningCard = p1 < p2 ? card1 : card2;
            ItemStack losingCard = p1 < p2 ? card2 : card1;

            winner.sendMessage(ChatColor.GREEN + "You have defeated " + losingCard.getItemMeta().getDisplayName() + "!");
            loser.sendMessage(ChatColor.RED + "You have been defeated by " + winningCard.getItemMeta().getDisplayName() + "!");
            if (Main.getInstance().config.getBoolean("wincard")) {
                loser.sendMessage(ChatColor.RED + "Your card now goes to " + winner.getName() + "!");
                exhaust.get(winner.getUniqueId()).add(losingCard);
            }
        }
    }

    public void winner() { //TODO Implement this. Give winner their cards, give the loser back whatever cards they get back.
        HandlerList.unregisterAll(this);
    }


    private String hideText(String s) {
        String d = "";
        for (Character c : s.toCharArray()) {
            d = d + ChatColor.COLOR_CHAR + c;
        }
        return d;
    }
}
