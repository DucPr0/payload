package payload.repeating;

import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import payload.main.Payload.GameInfo;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.Set;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import payload.main.Payload;

public class PayloadPush extends BukkitRunnable {
	private Payload plugin;
	public PayloadPush(Payload plugin) {
		this.plugin = plugin;
	}
	boolean collide(Location a, Location b) {
		double x1 = a.getX(), y1 = a.getY(), z1 = a.getZ();
		double x2 = b.getX(), y2 = b.getY(), z2 = b.getZ();
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2) <= 7;
	}
	@Override
	public void run() {
		Set<String> set = plugin.maps.keySet();
		for (String s: set) {
			GameInfo ginfo = plugin.maps.get(s);
			if (plugin.maps.get(s).getStage() != 1) continue;
			ExplosiveMinecart cart = plugin.maps.get(s).getCart();
			Set<UUID> set2 = ginfo.getPlayers();
			int cntblu = 0;
			boolean nearred = false;
			for (UUID uuid: set2) {
				Player p = Bukkit.getPlayer(uuid);
				if (collide(p.getLocation(), cart.getLocation())) {
					if (ginfo.getTeam(p) == "RED") {
						if (plugin.playerinfo.get(p.getUniqueId()).getRespawnTime() == 0) nearred = true;
					}
					else {
						if (plugin.playerinfo.get(p.getUniqueId()).getRespawnTime() == 0) {
							cntblu++;
							p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
						}
					}
				}
			}
			World world = Bukkit.getWorld(plugin.games.getConfigurationSection(s).getString("world"));
			if (cntblu == 0) {
				ginfo.addSecs(0.25);
			}
			if (cntblu > 0) {
				ginfo.setSecs(0);
			}
			if (nearred) {
				cart.setVelocity(new Vector(0, 0, 0));
			} else {
				Block nxt;
				if (ginfo.getSecs() < 5) {
					nxt = ginfo.getNxt(world.getBlockAt(cart.getLocation()));
				} else {
					nxt = ginfo.getPrv(world.getBlockAt(cart.getLocation()));
				}
				if (nxt == null) {
					cart.setVelocity(new Vector(0, 0, 0));
					return;
				}
				Block cur = world.getBlockAt(cart.getLocation());
				int tot = ginfo.getTotalRail(), curorder = ginfo.getOrder(cur);
				ginfo.getBarBlu().setProgress((double) curorder / tot);
				ginfo.getBarRed().setProgress((double) curorder / tot); 
				double velox = 0, veloz = 0, veloy = 0;
				if (nxt.getX() > cur.getX()) velox = 0.1;
				if (nxt.getX() < cur.getX()) velox = -0.1;
				if (nxt.getZ() > cur.getZ()) veloz = 0.1;
				if (nxt.getZ() < cur.getZ()) veloz = -0.1;
				if (nxt.getY() > cur.getY()) veloy = 0.1;
				if (nxt.getY() < cur.getY()) veloy = -0.1;
				veloy = 0;
				if (ginfo.getSecs() < 5) cart.setVelocity(new Vector(velox, veloy, veloz).multiply(cntblu));
				else cart.setVelocity(new Vector(velox, veloy, veloz));
			}
		}
	}
}
