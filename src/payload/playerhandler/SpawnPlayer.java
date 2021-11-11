package payload.playerhandler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import payload.main.Payload;
import net.md_5.bungee.api.ChatColor;
import payload.XMaterial;
import org.bukkit.inventory.meta.ItemMeta;
import payload.main.Payload.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import payload.classhandler.ClassEquip;

public class SpawnPlayer {
	private Payload plugin;
	public SpawnPlayer(Payload plugin) {
		this.plugin = plugin;
	}
	void setColor(ItemStack istack, Color color) {
		LeatherArmorMeta meta = (LeatherArmorMeta) istack.getItemMeta();
		meta.setColor(color);
		meta.setUnbreakable(true);
		istack.setItemMeta(meta);
	}
	public void spawnPlayer(Player player, Location spawnred, Location spawnblu, String getteam) {
		ItemStack helmet = new ItemStack(XMaterial.LEATHER_HELMET.parseMaterial(), 1);
		ItemStack chestplate = new ItemStack(XMaterial.LEATHER_CHESTPLATE.parseMaterial(), 1);
		ItemStack leggings = new ItemStack(XMaterial.LEATHER_LEGGINGS.parseMaterial(), 1);
		ItemStack boots = new ItemStack(XMaterial.LEATHER_BOOTS.parseMaterial(), 1);
		if (getteam == "BLU") {
			player.teleport(spawnblu);
			setColor(helmet, Color.AQUA);
			setColor(chestplate, Color.AQUA);
			setColor(leggings, Color.AQUA);
			setColor(boots, Color.AQUA);
		} else {
			player.teleport(spawnred);
			setColor(helmet, Color.RED);
			setColor(chestplate, Color.RED);
			setColor(leggings, Color.RED);
			setColor(boots, Color.RED);
		}
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		PlayerInfo pinfo = plugin.playerinfo.get(player.getUniqueId());
		if (pinfo.getCurclass() == null) {
			player.setHealth(20.0);
			player.setFoodLevel(20);
		} else {
			ClassEquip cman = new ClassEquip(plugin);
			cman.equipClass(player, pinfo.getCurclass());
		}
		String prefix;
		ItemStack chooseclass = new ItemStack(XMaterial.WHITE_WOOL.parseMaterial(), 1);
		if (getteam == "BLU") {
			prefix = ChatColor.AQUA.toString();
			chooseclass.setDurability((short) 3);
		}
		else {
			prefix = ChatColor.RED.toString();
			chooseclass.setDurability((short) 14);
		}
		ItemMeta meta = chooseclass.getItemMeta();
		meta.setDisplayName(prefix + "Choose a class"); 
		chooseclass.setItemMeta(meta);
		player.getInventory().setItem(8, chooseclass);
	}
}
