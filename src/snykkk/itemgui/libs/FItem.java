package snykkk.itemgui.libs;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FItem {
	
	ItemStack i = null;
	Material type = Material.AIR;
	String name = "";
	List<String> lore = new LinkedList<String>();
	int amount = 1;
	
	public FItem(Material type) {

		this.type = type;
	}

	public FItem(ItemStack i) {

		this.i = i;
		this.type = i.getType();
		this.amount = i.getAmount();
		
		if (i.hasItemMeta()) {
			ItemMeta im = i.getItemMeta();
			
			if (im.hasDisplayName()) {name = im.getDisplayName();}
			if (im.hasLore()) {lore = im.getLore();}

		}
	}
	
	public FItem setType(Material a) {this.type = a; return this;}
	public FItem setType(boolean condition, Material a) {if (condition) {this.type = a;} return this;}
	
	public FItem setName(String s) {this.name = s;return this;}
	public FItem setName(boolean condition, String s) {if (condition) {this.name = s;} return this;}

	public FItem setAmount(int s) {this.amount = s; return this;}
	public FItem setAmount(boolean condition, int s) {if (condition) {this.amount = s;} return this;}
	
	public FItem addLore(String s) {this.lore.add(s); return this;}
	public FItem addLore(boolean add, String s) {if (add) {this.lore.add(s);} return this;}
	
	public FItem addLore(double s) {this.lore.add(s + ""); return this;}
	
	public String getName() {return this.name;}
	
	public List<String> getLore() {return this.lore;}
	
	public ItemStack toItemStack() {
		if (i == null) {i = new ItemStack(type, amount);}
		
		ItemMeta im = i.getItemMeta();
		
		if (!name.equals("")) {im.setDisplayName(name);}
		im.setLore(lore);
		
		i.setItemMeta(im);
		
		i.setAmount(amount);

		return i;
	}
	
	public FItem clone() {
		FItem fi = new FItem(this.type);
		fi.setAmount(this.amount);
		
		return fi;
	}
	
	
	public void onClick (InventoryClickEvent e) {
		
	}
}