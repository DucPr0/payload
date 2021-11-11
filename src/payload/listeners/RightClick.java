package payload.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import payload.XMaterial;
import payload.classhandler.ClassChoose;
import payload.main.Payload;
import payload.main.Payload.GameInfo;

public class RightClick implements Listener {
	private Payload plugin;
	public RightClick(Payload plugin) {
		this.plugin = plugin;
	}
	public void onCompassClick(PlayerInteractEvent e) {
		//Handle right - clicking on Spectate compass
		if (plugin.playerinfo.containsKey(e.getPlayer().getUniqueId()) == false) return;
		ItemStack item = e.getPlayer().getItemInHand();
		String gamename = plugin.playerinfo.get(e.getPlayer().getUniqueId()).getGame();
		GameInfo ginfo = plugin.maps.get(gamename);
		int lim = plugin.games.getConfigurationSection(gamename).getInt("limit");
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Spectate")
				&& e.getPlayer().getGameMode() == GameMode.ADVENTURE && item.getType() == XMaterial.COMPASS.parseMaterial()) {
			String prefix = ChatColor.RED.toString();
			if (ginfo.getTeam(e.getPlayer()) == "BLU") prefix = ChatColor.AQUA.toString();
			Inventory inv = Bukkit.createInventory(null, 54, prefix + "Spectate:");
			Set<UUID> set = ginfo.getPlayers();
			String alive = ChatColor.GREEN + "" + ChatColor.BOLD + "ALIVE", dead = ChatColor.DARK_RED + "" + ChatColor.BOLD + "DEAD";
			List<String> listalive = new ArrayList<>(), listdead = new ArrayList<>();
			listalive.add(alive);
			listdead.add(dead);
			for (UUID uuid: set) {
				Player p = Bukkit.getPlayer(uuid);
//				if (uuid.equals(e.getPlayer().getUniqueId())) continue;
				String prefix2 = ChatColor.RED.toString();
				if (ginfo.getTeam(p) == "BLU") prefix2 = ChatColor.AQUA.toString();
				ItemStack skull = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1);
				skull.setDurability((short) 3);
				SkullMeta itemmeta = (SkullMeta) skull.getItemMeta();
				itemmeta.setDisplayName(prefix2 + ChatColor.ITALIC + p.getName());
				if (plugin.playerinfo.get(p.getUniqueId()).getRespawnTime() > 0) itemmeta.setLore(listdead);
				else itemmeta.setLore(listalive);
				itemmeta.setOwner(p.getName());
				skull.setItemMeta(itemmeta);
				inv.addItem(skull);
			}
			e.getPlayer().openInventory(inv);
		}
	}
	public void onClassChoose(PlayerInteractEvent e) {
		if (plugin.playerinfo.containsKey(e.getPlayer().getUniqueId()) == false) return;
		ItemStack item = e.getPlayer().getItemInHand();
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
				ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Choose a class")) {
			ClassChoose cman = new ClassChoose(plugin);
			cman.openClassChoose(e.getPlayer());
		}
	}
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		onCompassClick(e);
		onClassChoose(e);
	}
}
