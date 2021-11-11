package payload.classhandler;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import payload.main.Payload;
import net.md_5.bungee.api.ChatColor;
import payload.XMaterial;

public class ClassList {
	private Payload plugin;
	public ClassList(Payload plugin) {
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
	public void openClassList(Player player) {
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.YELLOW + "Class Editor");
		borderInventory(inv);
		Set<String> set = plugin.classes.getKeys(false);
		for (String s: set) {
			String display = plugin.classes.getConfigurationSection(s).getString("display");
			Material mat = XMaterial.fromString(display).parseMaterial();
			ItemStack istack = new ItemStack(mat, 1);
			istack.setDurability((short) plugin.classes.getConfigurationSection(s).getInt("displaydata"));
			ItemMeta meta = istack.getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + s);
			istack.setItemMeta(meta);
			inv.addItem(istack);
		}
		player.openInventory(inv);
	}
}
