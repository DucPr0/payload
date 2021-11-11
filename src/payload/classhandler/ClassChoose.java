package payload.classhandler;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import payload.XMaterial;
import payload.main.Payload.GameInfo;
import payload.main.Payload;

public class ClassChoose {
	private Payload plugin;
	public ClassChoose(Payload plugin) {
		this.plugin = plugin;
	}
	void borderInventory(Inventory inv) {
		int sz = inv.getSize();
		ItemStack blackglass = new ItemStack(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), 1);
		blackglass.setDurability((short) 15);
		for (int i = 0; i < 9; i++) inv.setItem(i, blackglass);
		for (int i = sz - 9; i < sz; i++) inv.setItem(i, blackglass);
		for (int i = 0; i < sz; i += 9) inv.setItem(i, blackglass);
		for (int i = 8; i < sz; i += 9) inv.setItem(i, blackglass);
	}
	public void openClassChoose(Player player) {
		GameInfo ginfo = plugin.maps.get(plugin.playerinfo.get(player.getUniqueId()).getGame());
		String prefix = ChatColor.RED.toString();
		if (ginfo.getTeam(player) == "BLU") prefix = ChatColor.AQUA.toString();
		Inventory inv = Bukkit.createInventory(null, 54, prefix + "Choose your class:");
		borderInventory(inv);
		Set<String> set = plugin.classes.getKeys(false);
		for (String s: set) {
			ConfigurationSection sec = plugin.classes.getConfigurationSection(s);
			ItemStack istack = new ItemStack(XMaterial.fromString(sec.getString("display")).parseMaterial(), 1);
			istack.setDurability((short) sec.getInt("displaydata"));
			ItemMeta meta = istack.getItemMeta();
			meta.setDisplayName(prefix + ChatColor.BOLD + s);
			istack.setItemMeta(meta);
			inv.addItem(istack);
		}
		player.openInventory(inv);
	}
}
