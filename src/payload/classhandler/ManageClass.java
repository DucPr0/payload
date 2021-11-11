package payload.classhandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import payload.main.Payload;
import net.md_5.bungee.api.ChatColor;
import payload.XMaterial;

public class ManageClass {
	private Payload plugin;
	public ManageClass(Payload plugin) {
		this.plugin = plugin;
	}
	public void openManage(Player player, String classname) {
		plugin.editing.put(player.getUniqueId(), classname);
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.YELLOW + "Editing class");
		ItemStack displayicon, weapons, health, speed;
		ConfigurationSection sec = plugin.classes.getConfigurationSection(classname);
		displayicon = new ItemStack(XMaterial.fromString(sec.getString("display")).parseMaterial(), 1);
		displayicon.setDurability((short) sec.getInt("displaydata"));
		ItemMeta meta = displayicon.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Modify display icon");
		displayicon.setItemMeta(meta);
		weapons = new ItemStack(XMaterial.DIAMOND_SWORD.parseMaterial(), 1);
		meta = weapons.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Modify weapons");
		weapons.setItemMeta(meta);
		health = new ItemStack(XMaterial.REDSTONE_BLOCK.parseMaterial(), 1);
		meta = health.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Modify health");
		health.setItemMeta(meta);
		speed = new ItemStack(XMaterial.EMERALD_BLOCK.parseMaterial(), 1);
		meta = speed.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Modify speed");
		speed.setItemMeta(meta);
		inv.setItem(1, speed);
		inv.setItem(3, health);
		inv.setItem(5, weapons);
		inv.setItem(7, displayicon);
		player.openInventory(inv);
	}
}
