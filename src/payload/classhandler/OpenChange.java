package payload.classhandler;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import payload.main.Payload;
import net.md_5.bungee.api.ChatColor;
import payload.XMaterial;

public class OpenChange {
	private Payload plugin;
	public OpenChange(Payload plugin) {
		this.plugin = plugin;
	}
	public void openIconChange(Player player) {
		String classname = plugin.editing.get(player.getUniqueId());
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.YELLOW + "Editing Icon");
		ItemStack blackglass = new ItemStack(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), 1);
		blackglass.setDurability((short) 15);
		for (int i = 0; i < 9; i++) inv.setItem(i, blackglass);
		Material mat = XMaterial.fromString(plugin.classes.getConfigurationSection(classname).getString("display")).parseMaterial();
		ItemStack currenticon = new ItemStack(mat, 1);
		currenticon.setDurability((short) plugin.classes.getConfigurationSection(classname).getInt("displaydata"));
		inv.setItem(4, currenticon);
		player.openInventory(inv);
	}
	private ItemStack getItem(Material mat, String name) {
		ItemStack istack = new ItemStack(mat, 1);
		ItemMeta meta = istack.getItemMeta();
		meta.setDisplayName(name);
		istack.setItemMeta(meta);
		return istack;
	}
	final ItemStack add10 = getItem(XMaterial.EMERALD_BLOCK.parseMaterial(), ChatColor.GREEN + "" + ChatColor.BOLD + "Add 10");
	final ItemStack add1 = getItem(XMaterial.EMERALD_BLOCK.parseMaterial(), ChatColor.GREEN + "" + ChatColor.BOLD + "Add 1");
	final ItemStack del10 = getItem(XMaterial.REDSTONE_BLOCK.parseMaterial(), ChatColor.RED + "" + ChatColor.BOLD + "Delete 10");
	final ItemStack del1 = getItem(XMaterial.REDSTONE_BLOCK.parseMaterial(), ChatColor.RED + "" + ChatColor.BOLD + "Delete 1");
	public void openHealthChange(Player player) {
		String classname = plugin.editing.get(player.getUniqueId());
		Inventory inv = Bukkit.createInventory(null, 18, ChatColor.YELLOW + "Editing Health");
		inv.setItem(10, add10);
		inv.setItem(12, add1);
		inv.setItem(14, del10);
		inv.setItem(16, del1);
		double curhealth = plugin.classes.getConfigurationSection(classname).getDouble("health");
		ItemStack displayhealth = new ItemStack(XMaterial.PAPER.parseMaterial(), 1);
		ItemMeta meta = displayhealth.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Current health: " + ChatColor.GRAY + curhealth);
		displayhealth.setItemMeta(meta);
		inv.setItem(4, displayhealth);
		player.openInventory(inv);
	}
	final ItemStack addspeed10 = getItem(XMaterial.EMERALD_BLOCK.parseMaterial(), ChatColor.GREEN + "" + ChatColor.BOLD + "Add 10%");
	final ItemStack addspeed1 = getItem(XMaterial.EMERALD_BLOCK.parseMaterial(), ChatColor.GREEN + "" + ChatColor.BOLD + "Add 1%");
	final ItemStack delspeed10 = getItem(XMaterial.REDSTONE_BLOCK.parseMaterial(), ChatColor.RED + "" + ChatColor.BOLD + "Delete 10%");
	final ItemStack delspeed1 = getItem(XMaterial.REDSTONE_BLOCK.parseMaterial(), ChatColor.RED + "" + ChatColor.BOLD + "Delete 1%");
	public void openSpeedChange(Player player) {
		String classname = plugin.editing.get(player.getUniqueId());
		Inventory inv = Bukkit.createInventory(null, 18, ChatColor.YELLOW + "Editing Speed");
		inv.setItem(10, addspeed10);
		inv.setItem(12, addspeed1);
		inv.setItem(14, delspeed10);
		inv.setItem(16, delspeed1);
		double curspeed = plugin.classes.getConfigurationSection(classname).getDouble("speed");
		ItemStack displayspeed = new ItemStack(XMaterial.PAPER.parseMaterial(), 1);
		ItemMeta meta = displayspeed.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Current speed: " + ChatColor.GRAY + curspeed + "%");
		displayspeed.setItemMeta(meta);
		inv.setItem(4, displayspeed);
		player.openInventory(inv);
	}
	public void openWeaponChange(Player player) {
		String classname = plugin.editing.get(player.getUniqueId());
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.YELLOW + "Editing Weapons");
		ConfigurationSection sec = plugin.classes.getConfigurationSection(classname).getConfigurationSection("contents");
		Set<String> set = sec.getKeys(false);
		for (String s: set) {
			ConfigurationSection sec2 = sec.getConfigurationSection(s);
			Material mat = XMaterial.fromString(sec2.getString("material")).parseMaterial();
			int data = sec2.getInt("data");
			ItemStack istack = new ItemStack(mat, 1);
			istack.setDurability((short) data);
			ItemMeta meta = istack.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', sec2.getString("name")));
			List<String> list = (List<String>) sec2.getList("enchantments");
			for (String enchant: list) {
				String name;
				int level;
				String parts[] = enchant.split(":");
				name = parts[0];
				level = Integer.parseInt(parts[1]);
				istack.addUnsafeEnchantment(Enchantment.getByName(name), level);
			}
			list = (List<String>) sec2.getList("lores");
			meta.setLore(list);
			istack.setItemMeta(meta);
			inv.addItem(istack);
		}
		player.openInventory(inv);
	}
}
