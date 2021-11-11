package payload.playerhandler;

import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import payload.main.Payload;
import payload.main.Payload.GameInfo;
import payload.main.Payload.PlayerInfo;
import payload.gamehandler.EndGame;

public class PlayerQuit {
	public static void quit(Player player, Payload plugin) {
		if (plugin.playerinfo.containsKey(player.getUniqueId()) == false) {
			player.sendMessage(ChatColor.RED + "You are not currently in a game");
			return;
		}
		PlayerInfo pinfo = plugin.playerinfo.get(player.getUniqueId());
		if (pinfo.getRespawnTime() > 0) {
			DeathMode dead = new DeathMode(plugin);
			dead.putOutDeath(player);
		}
		String mapname = pinfo.getGame();
		GameInfo ginfo = plugin.maps.get(mapname);
		ginfo.rem(player);
		if (ginfo.gettot() == 0) {
			EndGame endgame = new EndGame(plugin);
			endgame.end(mapname);
		}
		Set<UUID> set = ginfo.getPlayers();
		for (UUID uuid: set) Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "Player " + player.getName() + " has left the game");
		for (UUID uuid: set) {
			player.showPlayer(plugin, Bukkit.getPlayer(uuid));
		}
		player.sendMessage(ChatColor.GREEN + "You have been dropped from the game");
		Location loc = pinfo.getLoc();
		ItemStack[] istack = pinfo.getInv();
		double health = pinfo.getHealth();
		int food = pinfo.getFood();
		player.teleport(loc);
		player.getInventory().setContents(istack);
		player.updateInventory();
		player.setMaxHealth(20);
		player.setHealth(health);
		player.setFoodLevel(food);
		plugin.playerinfo.remove(player.getUniqueId());
		player.setWalkSpeed((float) 0.2);
	}
}
