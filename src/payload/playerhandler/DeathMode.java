package payload.playerhandler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import java.util.Set;
import payload.main.Payload.PlayerInfo;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.ChatColor;
import payload.main.Payload.GameInfo;
import org.bukkit.inventory.meta.ItemMeta;
import payload.main.Payload;
import payload.XMaterial;

public class DeathMode {
	private Payload plugin;
	public DeathMode(Payload plugin) {
		this.plugin = plugin;
	}
	private String prefix(Player p) {
		String ans = ChatColor.RED.toString();
		if (plugin.maps.get(plugin.playerinfo.get(p.getUniqueId()).getGame()).getTeam(p) == "BLU") ans = ChatColor.AQUA.toString();
		return ans;
	}
	public void putInDeath(Player p, Location deadloc) {
		int resptime = plugin.games.getConfigurationSection(plugin.playerinfo.get(p.getUniqueId()).getGame()).getInt("respawnred");
		GameInfo ginfo = plugin.maps.get(plugin.playerinfo.get(p.getUniqueId()).getGame());
		if (ginfo.getTeam(p) == "BLU") {
			resptime = plugin.games.getConfigurationSection(plugin.playerinfo.get(p.getUniqueId()).getGame()).getInt("respawnblu");
		}
		plugin.playerinfo.get(p.getUniqueId()).setRespawnTime(resptime);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.setFlying(true);
		p.getInventory().clear();
		ItemStack spectate = new ItemStack(Material.COMPASS, 1);
		ItemMeta spectatemeta = spectate.getItemMeta();
		spectatemeta.setDisplayName(prefix(p) + "Spectate");
		spectate.setItemMeta(spectatemeta);
		p.getInventory().addItem(spectate);
		p.teleport(deadloc);
		Set<UUID> set = ginfo.getPlayers();
		for (UUID uuid: set) {
			Player player = Bukkit.getPlayer(uuid);
			if (player.equals(p)) continue;
			player.hidePlayer(plugin, p);
		}
		String prefix;
		ItemStack chooseclass = new ItemStack(XMaterial.WHITE_WOOL.parseMaterial(), 1);
		if (ginfo.getTeam(p) == "BLU") {
			prefix = ChatColor.AQUA.toString();
			chooseclass.setDurability((short) 3);
		} else {
			prefix = ChatColor.RED.toString();
			chooseclass.setDurability((short) 14);
		}
		ItemMeta meta = chooseclass.getItemMeta();
		meta.setDisplayName(prefix + "Choose a class"); 
		chooseclass.setItemMeta(meta);
		p.getInventory().setItem(8, chooseclass);
	}
	public void putOutDeath(Player p) {
		GameInfo ginfo = plugin.maps.get(plugin.playerinfo.get(p.getUniqueId()).getGame());
		p.setGameMode(GameMode.SURVIVAL);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.getInventory().clear();
		SpawnPlayer spawnplayer = new SpawnPlayer(plugin);
		spawnplayer.spawnPlayer(p, ginfo.getSpawnRed(), ginfo.getSpawnBlu(), ginfo.getTeam(p));
		Set<UUID> set = ginfo.getPlayers();
		for (UUID uuid: set) {
			Player player = Bukkit.getPlayer(uuid);
			if (player.equals(p)) continue;
			player.showPlayer(plugin, p);
		}
	}
}
