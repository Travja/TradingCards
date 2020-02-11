package me.Travja.TradingCards;

import me.Travja.TradingCards.util.CardUtils;
import me.Travja.TradingCards.util.ConfigUtils;
import me.Travja.TradingCards.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class InventoryL implements Listener {
    public Main plugin;

    public InventoryL(Main m) {
        this.plugin = m;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack it = event.getItem();
        if (it == null)
            return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (it.getType() == Material.BOOK && ItemUtils.startsWith(it, ChatColor.RED + "Card Binder ")) {
                event.setCancelled(true);
                plugin.openBinder(p, Integer.valueOf(it.getItemMeta().getDisplayName().substring(14)));
            }
        }
    }

    @EventHandler
    public void click(final InventoryClickEvent event) { //TODO Optimize this
        Inventory inv = event.getInventory();
        boolean isBinder = false;
        final ItemStack item = event.getCurrentItem();
        for (String binder : plugin.binderNames.values()) {
            if (binder.equals(event.getView().getTitle())) {
                isBinder = true;
            }
        }
        if (inv.getType() == InventoryType.CHEST && isBinder) {
            if (item != null)
                if (item.getType() != Material.AIR) {
                    if (!isCard(item))
                        event.setCancelled(true);
                    else if (!event.isShiftClick()) {
                        if (event.getCursor().getType() != Material.AIR)
                            if (isCard(event.getCursor()))
                                if (ItemUtils.isNamed(item, event.getCursor().getItemMeta().getDisplayName()))
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                        for (ItemStack it : event.getInventory().getContents()) {
                                            if (it != null) {
                                                if (it.getAmount() > 1) {
                                                    while (it.getAmount() > 1) {
                                                        ItemStack distribute = it.clone();
                                                        ItemStack Return = it.clone();
                                                        it.setAmount(it.getAmount() - 1);
                                                        Return.setAmount(Return.getAmount() - 1);
                                                        if (event.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty() == -1) {
                                                            event.getWhoClicked().getOpenInventory().setCursor(Return);
                                                            it.setAmount(1);
                                                        } else {
                                                            distribute.setAmount(1);
                                                            event.getWhoClicked().getOpenInventory().getTopInventory().setItem
                                                                    (event.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty(), distribute);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        ((Player) event.getWhoClicked()).updateInventory();
                                    }, 1L);
                    } else {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            for (ItemStack it : event.getInventory().getContents()) {
                                if (it != null) {
                                    if (it.getAmount() > 1) {
                                        while (it.getAmount() > 1) {
                                            ItemStack distribute = it.clone();
                                            ItemStack Return = it.clone();
                                            it.setAmount(it.getAmount() - 1);
                                            Return.setAmount(Return.getAmount() - 1);
                                            if (event.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty() == -1) {
                                                event.getWhoClicked().getOpenInventory().getBottomInventory().setItem(event.getSlot(), Return);
                                                it.setAmount(1);
                                            } else {
                                                distribute.setAmount(1);
                                                event.getWhoClicked().getOpenInventory().getTopInventory().setItem
                                                        (event.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty(), distribute);
                                            }
                                        }
                                    }
                                }
                            }
                            ((Player) event.getWhoClicked()).updateInventory();
                        }, 1L);
                    }
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        for (ItemStack it : event.getInventory().getContents()) {
                            if (it != null) {
                                if (it.getAmount() > 1) {
                                    while (it.getAmount() > 1) {
                                        ItemStack distribute = it.clone();
                                        ItemStack Return = it.clone();
                                        it.setAmount(it.getAmount() - 1);
                                        Return.setAmount(Return.getAmount() - 1);
                                        if (event.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty() == -1) {
                                            event.getWhoClicked().getOpenInventory().setCursor(Return);
                                            it.setAmount(1);
                                        } else {
                                            distribute.setAmount(1);
                                            event.getWhoClicked().getOpenInventory().getTopInventory().setItem
                                                    (event.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty(), distribute);
                                        }
                                    }
                                }
                            }
                        }
                        ((Player) event.getWhoClicked()).updateInventory();
                    }, 1L);
                }
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        ItemStack binder = p.getInventory().getItemInMainHand();
        Inventory b;
        int color = new Random().nextInt(ChatColor.values().length - 6);
        if (binder.getType() == Material.BOOK) {
            if (ItemUtils.startsWith(binder, ChatColor.RED + "Card Binder ")) {
                if (binder.getItemMeta().hasLore()) {
                    b = plugin.binders.get(p.getName() + binder.getItemMeta().getDisplayName().substring(14));
                    if (ConfigUtils.isDebug())
                        System.out.println("Updating binder " + b + " Inv has title of " + event.getView().getTitle() + " and is of type " + event.getInventory().getType());
                    updateCards(b);
                    int in = 0;
                    for (ItemStack item : b.getContents()) {
                        if (item != null) {
                            in = in + 1;
                        }
                    }
                    ItemMeta im = binder.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.values()[color] + "[" + in + " out of 54 cards]");
                    im.setLore(lore);
                    binder.setItemMeta(im);
                }
            }
        }
        for (String bi : plugin.binders.keySet()) {
            if (bi.substring(0, bi.length() - 1).equals(p.getName())) {
                int bn = Integer.valueOf(bi.substring(bi.length() - 1));
                plugin.getBinder(bi.substring(0, bi.length() - 1), bn).set("Cards", plugin.binders.get(bi).getContents());
                plugin.saveBinder(bi.substring(0, bi.length() - 1), bn);
            }
        }
    }

    public void updateCards(Inventory inv) {
		CardUtils.replaceCards(inv);
        List<String> cards = plugin.config.getStringList("Cards.List");
        for (String str : cards) {
            for (ItemStack item : inv.getContents()) {
                if (item != null) {
                    if (isCard(item)) {
                        if (ItemUtils.isNamed(item, ChatColor.GOLD + str)) {
                            ItemMeta im = item.getItemMeta();
                            String name = im.getDisplayName().substring(2);
                            im.setLore(ConfigUtils.serializeLore(name));
                            item.setItemMeta(im);
                        }
                    }
                }
            }
        }
        if (inv.getHolder() instanceof Player) {
            if (ConfigUtils.isDebug())
                System.out.println("Updating player inventory");
            ((Player) inv.getHolder()).updateInventory();
        } else if (inv.getHolder() instanceof Block) {
            if (ConfigUtils.isDebug())
                System.out.println("Updating block inventory");
            ((Block) inv.getHolder()).getState().update();
        } else if (!inv.getViewers().isEmpty() && inv.getViewers().get(0) instanceof Player) {
            if (ConfigUtils.isDebug())
                System.out.println("Updating player inventory");
            for (HumanEntity viewer : inv.getViewers()) {
                ((Player) viewer).updateInventory();
            }
        } else {
            if (ConfigUtils.isDebug())
                System.out.println("No inventory to update");
            return;
        }
    }

    public boolean isCard(ItemStack item) {
        boolean found = false;

        if (item.getType() == Main.crd.getType()) {
            List<String> cardlist = plugin.config.getStringList("Cards.List");
            if (!cardlist.isEmpty()) {
                for (String cards : cardlist) {
                    if (ItemUtils.isNamed(item, ChatColor.GOLD + cards)) {
                        found = true;
                        break;
                    }
                }
            }
        }

        return found;
    }
}