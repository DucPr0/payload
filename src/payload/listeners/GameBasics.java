package payload.listeners;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;

import payload.XMaterial;
import payload.main.Payload;
import payload.main.Payload.GameInfo;
import payload.playerhandler.DeathMode;
import payload.playerhandler.PlayerQuit;

public class GameBasics implements Listener {
	private Payload plugin;
	public GameBasics(Payload plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void cancelPush(VehicleEntityCollisionEvent e) {
		//Cancel the vanilla push on the minecart
		if (e.getVehicle().getType().equals(EntityType.MINECART_TNT) == false) return;
		List<Entity> list = e.getVehicle().getPassengers();
		if (list.size() == 1 && list.get(0).getType().equals(EntityType.ARMOR_STAND)) e.setCancelled(true);
	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		PlayerQuit.quit(e.getPlayer(), plugin);
	}
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		//Handles chat in a game
		if (plugin.playerinfo.containsKey(e.getPlayer().getUniqueId()) == false) return;
		String gamename = plugin.playerinfo.get(e.getPlayer().getUniqueId()).getGame();
		GameInfo ginfo = plugin.maps.get(gamename);
		Set<UUID> set = ginfo.getPlayers();
		String team = ginfo.getTeam(e.getPlayer());
		String prefix, prefix2;
		if (team == "BLU") {
			prefix = ChatColor.AQUA + "(Team)";
			prefix2 = ChatColor.AQUA.toString();
		}
		else {
			prefix = ChatColor.RED + "(Team)";
			prefix2 = ChatColor.RED.toString();
		}
		boolean teamchaton = plugin.playerinfo.get(e.getPlayer().getUniqueId()).getTeamChat();
		for (UUID uuid: set) {
			Player p = Bukkit.getPlayer(uuid);
			if (teamchaton) {
				if (ginfo.getTeam(p) == team) {
					p.sendMessage(prefix + prefix2 + " " + e.getPlayer().getName() + ChatColor.WHITE + ": " + e.getMessage());
				}
			} else {
				p.sendMessage(prefix2 + e.getPlayer().getName() + ChatColor.WHITE + ": " + e.getMessage());
			}
		}
		e.setCancelled(true);
	}
	@EventHandler
	public void onFoodLoss(FoodLevelChangeEvent e) {
		//Cancel food loss in a game
		if ((e.getEntity() instanceof Player) == false) return;
		Player player = (Player) e.getEntity();
		if (plugin.playerinfo.containsKey(player.getUniqueId()) == false) return;
		e.setCancelled(true);
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		//Immediately respawn player and put them into spectator mode
		if (plugin.playerinfo.containsKey(e.getEntity().getUniqueId()) == false) return;
		e.setKeepInventory(true);
		e.setKeepLevel(true);
		Location loc = e.getEntity().getLocation();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run() {
				e.getEntity().spigot().respawn();
				DeathMode dead = new DeathMode(plugin);
				dead.putInDeath(e.getEntity(), loc);
			}
		}, 1L);
	}
	@EventHandler
	public void onHealthRegen(EntityRegainHealthEvent e) {
		//Prevent players in - game from regenerating health from food
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (plugin.playerinfo.containsKey(p.getUniqueId())) {
				if (e.getRegainReason() == RegainReason.SATIATED) e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onPlaceChooseClass(BlockPlaceEvent e) {
		ItemStack istack = e.getItemInHand();
		if (istack.getType() == XMaterial.WHITE_WOOL.parseMaterial()) {
			if (istack.hasItemMeta() && istack.getItemMeta().hasDisplayName()) {
				if (ChatColor.stripColor(istack.getItemMeta().getDisplayName()).equals("Choose a class")) {
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		//Disable spectators from hitting players.
		if ((e.getDamager() instanceof Player) && (e.getEntity() instanceof Player)) {
			Player damager = (Player) e.getDamager(), damagee = (Player) e.getEntity();
			if (plugin.playerinfo.containsKey(damager.getUniqueId()) && plugin.playerinfo.containsKey(damagee.getUniqueId())) {
				if (damager.getGameMode() == GameMode.ADVENTURE) e.setCancelled(true);
				GameInfo ginfo = plugin.maps.get(plugin.playerinfo.get(damager.getUniqueId()).getGame());
				if (ginfo.getTeam(damager).equals(ginfo.getTeam(damagee))) e.setCancelled(true);
			}
		}
	}
}
