package snykkk.itemgui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.items.ItemManager;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import snykkk.itemgui.libs.FItem;
import snykkk.itemgui.libs.FSkull;

public class ItemGUI extends JavaPlugin implements Listener {
	
	public static ItemGUI main;
	public String server_version = "";
	
	@Override
	public void onEnable() {
		
		main = this;
		
		server_version = Bukkit.getServer().getClass().getPackage().getName();
		server_version = server_version.substring(server_version.lastIndexOf(".") + 1);
		server_version = server_version.substring(1, server_version.length());
		server_version = server_version.toUpperCase();
		
		if(Bukkit.getPluginManager().getPlugin("MythicMobs") == null){
			Bukkit.getPluginManager().disablePlugin(this);
			
			Bukkit.getConsoleSender().sendMessage("");
			Bukkit.getConsoleSender().sendMessage("§eMythicMobs not found!");
			Bukkit.getConsoleSender().sendMessage("§bDisable ItemGUI Add-on");
			Bukkit.getConsoleSender().sendMessage("");
		}
		
		Bukkit.getConsoleSender().sendMessage("§e----------§6===== §bMCF §6=====§e----------");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("§aPlugin: MythicMobs | ItemGUI Add-on");
		Bukkit.getConsoleSender().sendMessage("§aServer version: " + server_version);
		Bukkit.getConsoleSender().sendMessage("§aMythicMobs version: " + MythicMobs.inst().getVersion());
		Bukkit.getConsoleSender().sendMessage("§aPlugin version: " + this.getDescription().getVersion());
		Bukkit.getConsoleSender().sendMessage("§aAuthor: Snykkk");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("§e------------- §b=========== §e-------------");
		
		Bukkit.getPluginManager().registerEvents(this, this);

		saveDefaultItem();
	}

	@Override
	public void onDisable() {

		Bukkit.getConsoleSender().sendMessage("§bMythicMobs | ItemGUI Add-on disabling ...");
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String a, String[] args) {
		
		if (a.equalsIgnoreCase("mmi") && (sender.isOp() || sender.hasPermission("*") || sender.hasPermission("itemgui.command"))) {
				
			if (sender instanceof Player) {
				Player p = (Player) sender;
				
				if (args.length == 0) {
					open(p, 1, "");
				}
				
				if (args.length == 1) {
					open(p, 1, args[0]);
				}
				
				if (args.length == 2 && args[0].equalsIgnoreCase("save")) {

					saveDefaultItem();
					
					ItemStack i = p.getInventory().getItemInMainHand();
					String id = args[1];
					
					itemfc.set(id + ".Id", i.getType().toString());
					itemfc.set(id + ".Data", i.getData().getData());
					if (i.hasItemMeta()) {
						if (i.getItemMeta().hasDisplayName()) {
							itemfc.set(id + ".Display", i.getItemMeta().getDisplayName());
						}
						if (i.getItemMeta().hasLore()) {
							itemfc.set(id + ".Lore", i.getItemMeta().getLore());
						}
						Damageable im = (Damageable) i.getItemMeta();
						if (im.hasDamage()) {
							itemfc.set(id + ".Durability", im.getDamage());
						}
						if (i.getItemMeta().hasEnchants()) {
							List<String> ecs = new ArrayList<String>();
							Map<Enchantment, Integer> enchants = i.getItemMeta().getEnchants();
							for (Enchantment ec : enchants.keySet()) {
								ecs.add(ec.getName().toString() + ":" + enchants.get(ec));
							}
							
							itemfc.set(id + ".Enchantments", ecs);
						}
						if (i.getItemMeta().isUnbreakable()) {
							itemfc.set(id + ".Options.Unbreakable", true);
						}
					}
					
					save();
					
					p.sendMessage("§7[§aMMI§7] §aSaved " + id + "!");
				}
			} else {
				sender.sendMessage("§7[§aMMI§7] §cYou can execute this command only as player!");
			}
		}

		return true;
	}
	
	File item;
	FileConfiguration itemfc;
	
	public void saveDefaultItem() {
		this.item = new File(MythicMobs.inst().getDataFolder() + "/Items", "ItemGUI.yml");
		
		if (!this.item.exists()) {
			try {this.itemfc.save(this.item);}
			catch (Exception ex) {}
		}
		
		this.itemfc = YamlConfiguration.loadConfiguration(this.item);
	}
	
	public void save() {
		try {this.itemfc.save(this.item);} catch (Exception ex) {}
	}
	public FileConfiguration getItemFileConfiguration() { return this.itemfc; }
	public void reloadItem() { saveDefaultItem(); }
	
	public static void open (Player p, int page, String keyword) {
		Inventory inv = null;
		
		if (keyword.equals("")) {
			inv = Bukkit.createInventory(null, 54, "MythicMobs Item GUI - " + page);
		} else {
			inv = Bukkit.createInventory(null, 54, "MythicMobs Item GUI - " + page + " - " + keyword);
		}
		
		List<MythicItem> mmi = new LinkedList<MythicItem>(MythicMobs.inst().getItemManager().getItems());
	    Collections.sort(mmi);

    	HashMap<Integer, List<ItemStack>> save = new HashMap<Integer, List<ItemStack>>();
		
		int pa = 1;
		
		List<ItemStack> list = new LinkedList<ItemStack>();
		
	    for (MythicItem mi : mmi) {
			
	    	if (!keyword.equals("")) {
	    		if (mi.getInternalName().contains(keyword) || mi.getInternalName().equalsIgnoreCase(keyword)) {
	    			list.add(get(mi.getInternalName()));
	    		}
	    		else continue;
	    	}
	    	else list.add(get(mi.getInternalName()));
	    	
	    	if (list.size() >= 45) {
	    		save.put(pa, list);
				list = new LinkedList<ItemStack>();
				pa++;
	    	}
	    }
	    
	    if (list.size() < 45) {
	    	save.put(pa, list);
	    }
	    
	    for (ItemStack i : save.get(page)) {
			inv.addItem(i);
		}
	    
	    for (int i = 45; i <= 53; i++) {
	    	if (main.server_version.startsWith("1_12")) {
	    		inv.setItem(i, new FItem(Material.valueOf("STAINED_GLASS_PANE")).setName("§b").toItemStack());
	    	} else {
	    		inv.setItem(i, new FItem(Material.WHITE_STAINED_GLASS_PANE).setName("§b").toItemStack());
	    	}
	    }

	    inv.setItem(48, new FItem(FSkull.byValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJmMDQyNWQ2NGZkYzg5OTI5MjhkNjA4MTA5ODEwYzEyNTFmZTI0M2Q2MGQxNzViZWQ0MjdjNjUxY2JlIn19fQ=="))
	    		.setName("§eBack").toItemStack());
	    inv.setItem(50, new FItem(FSkull.byValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ4NjVhYWUyNzQ2YTliOGU5YTRmZTYyOWZiMDhkMThkMGE5MjUxZTVjY2JlNWZhNzA1MWY1M2VhYjliOTQifX19"))
	    		.setName("§eNext").toItemStack());
	    
	    p.openInventory(inv);
	}
	
	@EventHandler
	public void click (InventoryClickEvent e) {
		if (e.getView().getTitle().contains("MythicMobs Item GUI - ")) {
			Player p = (Player) e.getWhoClicked();
			int page = Integer.valueOf(e.getView().getTitle().split(" - ")[1]);
			try {
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§b")) {
					e.setCancelled(true);
				}
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§eBack")) {
					e.setCancelled(true);
					if (page == 1) {
					} else {
						if (e.getView().getTitle().split(" - ").length == 3) {
							String keyword = e.getView().getTitle().split(" - ")[2];
							open(p, page - 1, keyword);
						} else {
							open(p, page - 1, "");
						}
					}
				}
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§eNext")) {
					e.setCancelled(true);
					if (e.getView().getTitle().split(" - ").length == 3) {
						String keyword = e.getView().getTitle().split(" - ")[2];
						open(p, page + 1, keyword);
					} else {
						open(p, page + 1, "");
					}
				}
			} catch (Exception ex) {}
		}
	}
	
	public static ItemStack get (String id) {
		ItemManager aa = MythicMobs.inst().getItemManager();
		
		ItemStack i = BukkitAdapter.adapt(aa.getItem(id).get().generateItemStack(1));
		
		return i;
	}
	
	/**
	 * @param id
	 * @param amount
	 * @return
	 */
	public static ItemStack get (String id, int amount) {
		ItemManager aa = MythicMobs.inst().getItemManager();
		
		ItemStack i = BukkitAdapter.adapt(aa.getItem(id).get().generateItemStack(1));
		
		i.setAmount(amount);
		
		return i;
	}
	
	public static void addItem(Player p, String id, int soluong) {
	    
	    Optional<MythicItem> mi = MythicMobs.inst().getItemManager().getItem(id);
	    
	    p.getInventory().addItem(new ItemStack[] 
	    { 
	    		BukkitAdapter.adapt(((MythicItem)mi.get()).generateItemStack(soluong)) 
	    });
	}
}
