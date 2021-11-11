package payload.classhandler;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import payload.main.Payload;
import net.md_5.bungee.api.ChatColor;
import payload.XMaterial;

public class ClassEquip {
	private Payload plugin;
	public ClassEquip(Payload plugin) {
		this.plugin = plugin;
	}
	public void equipClass(Player player, String classname) {
		Inventory inv = player.getInventory();
		for (int i = 0; i < inv.getSize(); i++) {
			if (i > 35) break;
			if (i == 8) continue;
			inv.setItem(i, null);
		}
		inv.setItem(40, null);
		ConfigurationSection sec = plugin.classes.getConfigurationSection(classname);
		player.setMaxHealth(sec.getDouble("health"));
		player.setHealth(sec.getDouble("health"));
		player.setWalkSpeed((float) (0.2 * sec.getDouble("speed") / 100));
		ConfigurationSection sec2 = sec.getConfigurationSection("contents");
		Set<String> set = sec2.getKeys(false);
		for (String s: set) {
			ConfigurationSection item = sec2.getConfigurationSection(s);
			boolean commandmode = item.getBoolean("command-mode");
			if (commandmode) {
				List<String> commands = item.getStringList("commands");
				for (String comm: commands) {
					comm = comm.replaceAll("%player%", player.getName());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comm);
				}
			} else {
				ItemStack istack = new ItemStack(XMaterial.fromString(item.getString("material")).parseMaterial(), 1);
				istack.setDurability((short) item.getInt("data"));
				ItemMeta meta = istack.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', item.getString("name")));
				List<String> lorelist = item.getStringList("lores");
				meta.setLore(lorelist);
				List<String> enchants = item.getStringList("enchantments");
				for (String ench: enchants) {
					String name;
					int level;
					String[] parts = ench.split(":");
					name = parts[0];
					level = Integer.parseInt(parts[1]);
					istack.addUnsafeEnchantment(Enchantment.getByName(name), level);
				}
				istack.setItemMeta(meta);
				inv.addItem(istack);
			}
		}
	}
}
