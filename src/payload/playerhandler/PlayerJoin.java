package payload.playerhandler;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import payload.main.Payload.PlayerInfo;
import java.util.UUID;
import payload.main.Payload;
import payload.main.Payload.GameInfo;

public class PlayerJoin {
	public static void join(Player player, String name, Payload plugin) {
		GameInfo ginfo = plugin.maps.get(name);
		ConfigurationSection sec = plugin.games.getConfigurationSection(name);
		World world = Bukkit.getWorld(sec.getString("world"));
		int redcnt = ginfo.getred(), blucnt = ginfo.getblu();
		String playerteam;
		if (redcnt >= blucnt) {
			ginfo.inc("BLU", player);
			playerteam = ChatColor.AQUA + "BLU";
		} else {
			ginfo.inc("RED", player);
			playerteam = ChatColor.RED + "RED";
		}
		int lim = sec.getInt("limit"), tot = ginfo.gettot();
		if (tot == lim * 2 && ginfo.getStage() == 0) ginfo.setcountdown(Math.min(ginfo.getcountdown(), 10));
		//On event player join check if half the game is filled up. If yes begin a countdown, and when countdown end start the
		//game. If the game is fully filled shorten the countdown to min(current, 10)
		PlayerInfo pinfo = new PlayerInfo(player);
		pinfo.setGame(name);
		pinfo.setTeamChat(false);
		plugin.playerinfo.put(player.getUniqueId(), pinfo);
		player.getInventory().clear();
		String team;
		if (redcnt >= blucnt) team = "BLU";
		else team = "RED";
		SpawnPlayer spawnplayer = new SpawnPlayer(plugin);
		spawnplayer.spawnPlayer(player, ginfo.getSpawnRed(), ginfo.getSpawnBlu(), team);
		Set<UUID> set = ginfo.getPlayers();
		for (UUID uuid: set)
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Player " + player.getName() + " was assigned to team " + playerteam);
		if (ginfo.gettot() == 1 && ginfo.getStage() == 0) {
			ginfo.setcountdown(120);
		}
		for (UUID uuid: set) {
			if (Bukkit.getPlayer(uuid).getGameMode() == GameMode.ADVENTURE) {
				player.hidePlayer(plugin, Bukkit.getPlayer(uuid));
			}
		}
	}
}
