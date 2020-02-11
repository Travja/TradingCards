package me.Travja.TradingCards;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Deck {
	private ItemStack[] cards = null;
	private Inventory deck = Bukkit.createInventory(null, 27);

	@SuppressWarnings("unchecked")
	public Deck(UUID uuid, Player p){
		if(Main.getInstance().decks.getList(uuid.toString())!= null){
			Object o = Main.getInstance().decks.getList(uuid.toString());
			if(o instanceof ItemStack[]){
				cards = (ItemStack[]) o;
			}else if(o instanceof List){
				cards = (ItemStack[]) ((List<ItemStack>) o).toArray(new ItemStack[0]);
			}
			deck.setContents(cards);
			//TODO give a way to create a deck.
			Main.getInstance().decklist.add(this);
		}
	}
	public void open(){

	}

	public void add(ItemStack is){

	}
}
