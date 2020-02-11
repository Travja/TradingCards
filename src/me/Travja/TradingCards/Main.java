package me.Travja.TradingCards;

import me.Travja.TradingCards.util.CardUtils;
import me.Travja.TradingCards.util.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    public Listener Join = new Join(this);
    public Listener Precraft = new Precraft(this);
    public Listener Interact = new InventoryL(this);
    public Listener Mobs = new MobDrops(this);
    public CommandExecutor Cmds = new Commands(this);
    File folder;
    File prebinder;
    FileConfiguration binder;
    public FileConfiguration config;
    public FileConfiguration decks = null;
    public File decksFile = null;
    public HashMap<String, Inventory> binders = new HashMap<>();
    public HashMap<String, String> binderNames = new HashMap<>();
    public ArrayList<Deck> decklist = new ArrayList<>();

    public static ItemStack crd;

    public static Main instance;

    //TODO Create code for decks

    public void onEnable() {
        instance = this;
        config = this.getConfig();
        if (!new File(getDataFolder(), "config.yml").exists()) {
            config.options().copyDefaults(true);
            saveDefaultConfig();
        }

        crd = new ItemStack(Material.valueOf(ConfigUtils.getString("Card")) != null ? Material.valueOf(ConfigUtils.getString("Card")) : Material.PAPER);
        loadBinders();


        for (Player p : getServer().getOnlinePlayers()) {
            Inventory inv = p.getInventory();
            CardUtils.replaceCards(inv);
        }


        addBinderRecipe();
        addCardRecipe();
        getCommand("TC").setExecutor(Cmds);
        registerEvents();
        giveCards();
        getLogger().log(Level.INFO, "Enabled!");
    }

    private void loadBinders() {
        if (getDataFolder() != null) {
            for (File f : getDataFolder().listFiles()) {
                if (!f.isFile())
                    if (!f.getName().equalsIgnoreCase("config.yml")) {
                        for (File b : f.listFiles()) {
                            String p = f.getName();
                            int i = Integer.valueOf(b.getName().substring(0, b.getName().indexOf('.')));
                            reloadBinder(p, i);
                            Inventory inv = getServer().createInventory(null, 54, ChatColor.DARK_BLUE + "Card Binder " + i);
                            inv.clear();
                            inv.addItem(new ItemStack(Material.ARROW));
                            binders.put(p + i, inv);
                            binderNames.put(p + i, ChatColor.DARK_BLUE + "Card Binder " + i);
                            ItemStack[] itemsInBinder = null;
                            Object o = getBinder(p, i).get("Cards");
                            if (o instanceof ItemStack[]) {
                                itemsInBinder = (ItemStack[]) o;
                            } else if (o instanceof List) {
                                itemsInBinder = ((List<ItemStack>) o).toArray(new ItemStack[0]);
                            }
                            inv.setContents(itemsInBinder);
                            CardUtils.replaceCards(inv);
                            binders.put(p + i, inv);
                        }
                    }
            }
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(Precraft, this);
        getServer().getPluginManager().registerEvents(Interact, this);
        getServer().getPluginManager().registerEvents(Join, this);
        getServer().getPluginManager().registerEvents(Mobs, this);
    }

    public void onDisable() {
        closeBinders();
        getLogger().log(Level.INFO, "Disabled!");
    }

    private void closeBinders() {
        for (String b : binders.keySet()) {
            List<HumanEntity> toClose = new ArrayList<>();
            for (HumanEntity v : binders.get(b).getViewers()) {
                toClose.add(v);
            }
            for (HumanEntity v : toClose) {
                v.closeInventory();
            }
            toClose.clear();
        }
    }

    public static Main getInstance() {
        return instance;
    }
	
	/*public void reloadDecks() {
		if (decksFile == null) {
			decksFile = new File(getDataFolder(), "decks.yml");
		}
		decks = YamlConfiguration.loadConfiguration(decksFile);

		InputStream defConfigStream = this.getResource("spawns.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			decks.setDefaults(defConfig);
		}
	}
	
	public FileConfiguration getDecks() {
		if (decks == null) {
			this.reloadDecks();
		}
		return decks;
	}
	
	public void saveDecks() {
		if (decks == null || decksFile == null) {
			return;
		}
		try {
			getDecks().save(decksFile);
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE, "Could not save config to " + decksFile, ex);
		}
	}*/

    public FileConfiguration getBinder(String p, Integer i) {
        if (prebinder == null || !prebinder.getParentFile().getName().equalsIgnoreCase(p) || !prebinder.getName().equalsIgnoreCase(i + ".yml"))
            reloadBinder(p, i);
        return binder;
    }

    public void reloadBinder(String p, Integer i) {
        prebinder = getPre(p, i);
        binder = YamlConfiguration.loadConfiguration(prebinder);

        try {
            Reader defConfigStream = new InputStreamReader(this.getResource("binder.yml"), "UTF8");
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                binder.setDefaults(defConfig);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void saveBinder(String p, Integer i) {
        if (!prebinder.getParentFile().getName().equalsIgnoreCase(p) || !prebinder.getName().equalsIgnoreCase(i + ".yml"))
            reloadBinder(p, i);
        try {
            binder.save(prebinder);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + prebinder, ex);
        }
    }

    public void openBinder(Player p, Integer i) {
        if (!binders.containsKey(p.getName() + i)) {
            Inventory inv = getServer().createInventory(p, 54, ChatColor.DARK_BLUE + "Card Binder " + i);
            for (String names : ConfigUtils.getStringList("Cards.defaults")) {
                ItemStack card = crd;
                card.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
                ItemMeta im = card.getItemMeta();
                im.setDisplayName(ChatColor.GOLD + names);
                List<String> lore = ConfigUtils.serializeLore(names);
                im.setLore(lore);
                card.setItemMeta(im);
                inv.addItem(card);
            }
            CardUtils.replaceCards(inv);
            binders.put(p.getName() + i, inv);
        }
        p.openInventory(binders.get(p.getName() + i));
    }

    public File getPre(String p, Integer i) {
        prebinder = new File(getFolder(p), i + ".yml");
        if (!prebinder.exists())
            try {
                prebinder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return prebinder;
    }

    public File getFolder(String p) {
        String pname = p;
        folder = new File(getDataFolder() + File.separator + pname + File.separator);
        if (!folder.exists())
            folder.mkdirs();
        return folder;
    }

    public void addBinderRecipe() {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bm = book.getItemMeta();
        bm.setDisplayName(ChatColor.RED + "Card Binder 1");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.AQUA + "[0 out of 54 cards]");
        bm.setLore(lore);
        book.setItemMeta(bm);
        book.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        ShapedRecipe sr = new ShapedRecipe(book);
        sr.shape("LCL", "LBL", "LCL");
        sr.setIngredient('L', Material.LEATHER);
        sr.setIngredient('C', Material.CHEST);
        sr.setIngredient('B', Material.BOOK);
        getServer().addRecipe(sr);
        bm.setDisplayName(ChatColor.RED + "Card Binder");
        lore.clear();
        int color = new Random().nextInt(ChatColor.values().length - 6);
        lore.add(ChatColor.values()[color] + "[0 out of 54 cards]");
        bm.setLore(lore);
        book.setItemMeta(bm);
        book.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        ShapelessRecipe ur = new ShapelessRecipe(book);
        ur.addIngredient(Material.BOOK);
        ur.addIngredient(Material.BOOK);
        getServer().addRecipe(ur);
    }

    public void addCardRecipe() {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta pm = paper.getItemMeta();
        pm.setDisplayName(ChatColor.AQUA + "Card");
        paper.setItemMeta(pm);
        ShapedRecipe sr = new ShapedRecipe(paper);
        sr.shape(" P ", "PIP", " P ");
        sr.setIngredient('P', Material.PAPER);
        Map<String, Object> temp = config.getConfigurationSection("Crafting").getValues(false);
        for (Entry<String, Object> entry : temp.entrySet()) {
            if (ConfigUtils.getStringList("Crafting." + entry.getKey()) != null) {
                sr.setIngredient('I', Material.valueOf(entry.getKey().toUpperCase()));
                getServer().addRecipe(sr);
            }
        }
    }

    static boolean startup = true;

    public void giveCards() {
        if (ConfigUtils.getInt("Reward.Time") != 0) {
            new BukkitRunnable() {
                public void run() {
                    if (startup)
                        startup = false;
                    else {
                        for (Player online : getServer().getOnlinePlayers()) {
                            if (online.hasPermission("tradingcards.reward")) {
                                Random r = new Random();
                                Inventory inv = online.getInventory();
                                int max = 1;
                                HashMap<Integer, String> choose = new HashMap<>();
                                for (String rare : ConfigUtils.getStringList("Reward.Rarity")) {
                                    int num = Integer.valueOf(rare.split("~")[1]);
                                    System.out.println("Percent chance of being given: " + num);
                                    for (int i = max; i < max + num; i++) {
                                        choose.put(i, rare.split("~")[0]);
                                    }
                                    max += num;
                                }
                                System.out.println("Max is: " + max);
                                int number = r.nextInt(max) + 1;
                                String rarity = choose.get(number);
                                List<String> cards = ConfigUtils.getStringList("Cards.Rarity." + rarity);
                                System.out.println("Picked " + rarity + " because number was " + number);
                                if (!cards.isEmpty()) {
                                    String names = cards.get(r.nextInt(cards.size()));

                                    ItemStack card = crd;
                                    card.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
                                    ItemMeta im = card.getItemMeta();
                                    im.setDisplayName(ChatColor.GOLD + names);
                                    im.setLore(ConfigUtils.serializeLore(names));
                                    card.setItemMeta(im);

                                    inv.addItem(card);
                                    online.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.DARK_GREEN + names + ChatColor.GREEN + " as a reward for your time on the server!");
                                }
                                online.updateInventory();
                            }
                        }
                        getLogger().log(Level.INFO, "Gave rewards :D");
                    }
                }
            }.runTaskTimerAsynchronously(this, 0L, ConfigUtils.getInt("Reward.Time") * 1200L);
        }
    }
}
