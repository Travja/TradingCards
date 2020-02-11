package me.Travja.TradingCards.util;

import me.Travja.TradingCards.Main;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CardUtils {

    private static Random rand = new Random();

    public static ItemStack getRandomCraftingCard(String rarity) {
        List<String> cards = ConfigUtils.getStringList("Cards.Rarity." + ConfigUtils.getString("Crafting." + rarity));
        return getCardFromList(cards);
    }


    public static ItemStack getRandomMobCard(String mob, MobType type) {
        List<String> cards = ConfigUtils.getStringList("Cards.Rarity." + ConfigUtils.getString("Drops." + type.string() + "." + mob));
        return getCardFromList(cards);
    }

    private static ItemStack getCardFromList(List<String> cards) {
        ItemStack card = Main.crd;
        if (cards != null && !cards.isEmpty()) {
            String names = cards.get(rand.nextInt(cards.size()));

            card.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
            ItemMeta im = card.getItemMeta();
            im.setDisplayName(ChatColor.GOLD + names);
            List<String> lore = ConfigUtils.serializeLore(names);
            im.setLore(lore);

            card.setItemMeta(im);
        } else
            card = null;

        return card;
    }

    public static void replaceCards(Inventory inv) {
        LinkedList<String> cards = new LinkedList<>();
        for (String s : ConfigUtils.getStringList("Cards.Replace")) {
            cards.add(s);
        }
        for (String str : cards) {
            String[] args = str.split(",");
            for (ItemStack item : inv.getContents()) {
                if (ItemUtils.isNamed(item, ChatColor.GOLD + args[0])) {
                    ItemMeta im = item.getItemMeta();
                    im.setDisplayName(ChatColor.GOLD + args[1]);
                    item.setItemMeta(im);
                }
            }
        }
    }

    public enum MobType {
        HOSTILE("Hostile"),
        PASSIVE("Passive"),
        NEUTRAL("Neutral"),
        BOSS("Bosses");

        private String str;

        MobType(String str) {
            this.str = str;
        }
        public String string() {
            return str;
        }
    }

}
