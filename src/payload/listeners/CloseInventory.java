package payload.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import payload.main.Payload;

public class CloseInventory implements Listener {
	private Payload plugin;
	public CloseInventory(Payload plugin) {
		this.plugin = plugin;
	}
	private String revertColorCodes(String text) {
	    char[] array = text.toCharArray();
	    for (int i = 0; i < array.length - 1; i++) {
	        if (array[i] == ChatColor.COLOR_CHAR && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(array[i + 1]) != -1) {
	            array[i] = '&';
	            array[i + 1] = Character.toLowerCase(array[i + 1]);
	        }
	    }
	    return new String(array);
	}
	public void onClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		if (ChatColor.stripColor(inv.getName()).equals("Editing Icon")) {
			ItemStack istack = inv.getItem(4);
			if (istack == null) return;
			String classname = plugin.editing.get(e.getPlayer().getUniqueId());
			ConfigurationSection sec = plugin.classes.getConfigurationSection(classname);
			sec.set("display", istack.getType().toString());
			sec.set("displaydata", istack.getDurability());
			plugin.saveClass();
			e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set new display icon for " + classname);
		}
	}
	public void onWeaponClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		if (ChatColor.stripColor(inv.getName()).equals("Editing Weapons")) {
			String classname = plugin.editing.get(e.getPlayer().getUniqueId());
			ConfigurationSection sec = plugin.classes.getConfigurationSection(classname);
			sec.set("contents", null);
			sec.createSection("contents");
			ConfigurationSection sec2 = sec.getConfigurationSection("contents");
			ItemStack[] items = inv.getContents();
			int current = 0;
			for (ItemStack istack: items) {
				if (istack == null) continue;
				ConfigurationSection sec3 = sec2.createSection(current + "");
				sec3.set("material", istack.getType().toString());
				sec3.set("data", istack.getDurability());
				if (istack.getItemMeta().hasDisplayName()) sec3.set("name", revertColorCodes(istack.getItemMeta().getDisplayName()));
				else sec3.set("name", istack.getType().toString());
				List<String> list = new ArrayList<>();
				Map<Enchantment, Integer> map = istack.getEnchantments();
				Set<Enchantment> enchants = map.keySet();
				for (Enchantment en: enchants) {
					int level = map.get(en);
					list.add(en.getName() + ":" + level);
				}
				sec3.set("enchantments", list);
				List<String> listlore = istack.getItemMeta().getLore();
				if (listlore == null) listlore = new ArrayList<>();
				sec3.set("lores", listlore);
				sec3.set("command-mode", false);
				List<String> listcommands = new ArrayList<>();
				sec3.set("commands", listcommands);
				current++;
			}
			plugin.saveClass();
			e.getPlayer().sendMessage(ChatColor.GREEN + "Updated inventory for class " + classname);
		}
	}
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		onClose(e);
		onWeaponClose(e);
	}
}
