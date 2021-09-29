package snykkk.itemgui.libs;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import snykkk.itemgui.ItemGUI;

@SuppressWarnings("deprecation")
public class FSkull {
	
	public static ItemStack byValue(String skinValue) {
		
		ItemStack head = null;
		if (ItemGUI.main.server_version.equals("1_12")) {
			head = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
		} else {
			head = new ItemStack(Material.PLAYER_HEAD);
		}
		
		if (skinValue.isEmpty()) {return head;}

		ItemMeta headMeta = head.getItemMeta();
		
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		
		profile.getProperties().put("textures", new Property("textures", skinValue));
		
		Field profileField = null;
		
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
		}
		catch (NoSuchFieldException | SecurityException ex) {}
		profileField.setAccessible(true);
		try {profileField.set(headMeta, profile);
		
		}
		catch (IllegalArgumentException | IllegalAccessException ex) {}
		
		head.setItemMeta(headMeta);
		
		return head;
	}
}
