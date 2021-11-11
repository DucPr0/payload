package payload.repeating;

import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;
import java.util.Set;
import payload.main.Payload;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import payload.main.Payload.PlayerInfo;
import payload.main.Payload.GameInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import payload.playerhandler.DeathMode;

public class ActionBarHandler extends BukkitRunnable {
	private Payload plugin;
	public ActionBarHandler(Payload plugin) {
		this.plugin = plugin;
	}
	@Override
	public void run() {
		Set<String> games = plugin.maps.keySet();
		for (String s: games) {
			GameInfo ginfo = plugin.maps.get(s);
			Set<UUID> set = ginfo.getPlayers();
			for (UUID uuid: set) {
				Player p = Bukkit.getPlayer(uuid);
				String team = ginfo.getTeam(p);
				PlayerInfo pinfo = plugin.playerinfo.get(p.getUniqueId());
				int respawnin = pinfo.getRespawnTime();
				String prefix = ChatColor.RED.toString();
				if (team == "BLU") prefix = ChatColor.AQUA.toString();
				if (respawnin > 0) {
					if (ginfo.getStage() < 2) {
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
								new TextComponent(prefix + "Respawning in " + respawnin + " seconds"));
						pinfo.setRespawnTime(respawnin - 1);
						if (respawnin == 1) {
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
								@Override
								public void run() {
									DeathMode dead = new DeathMode(plugin);
									dead.putOutDeath(p);
								}
							}, 20);
						}
					} else {
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(prefix + "Wait until next round"));
					}
				} else {
					if (team == "RED") p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Objective: " + ChatColor.RED + "Defend"
									+ " the base"));
					else {
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW.toString() + 
								ChatColor.BOLD + "Objective: " + ChatColor.AQUA + "Push the payload into the enemy base"));
					}
				}
			}
		}
	}
}
