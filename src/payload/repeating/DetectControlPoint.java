package payload.repeating;

import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;
import java.util.Set;
import payload.main.Payload.GameInfo;
import payload.main.Payload;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.material.MaterialData;
import payload.XMaterial;
import payload.gamehandler.BLUWins;

public class DetectControlPoint extends BukkitRunnable {
	private Payload plugin;
	public DetectControlPoint(Payload plugin) {
		this.plugin = plugin;
	}
	String convert(String prefix, int newtime) {
		String minutes = Integer.toString(newtime / 60), secs = Integer.toString(newtime % 60);
		if (secs.length() == 1) secs = "0" + secs;
		return prefix + ChatColor.BOLD + minutes + ":" + secs;
	}
	public void run() {
		Set<String> set = plugin.maps.keySet();
		for (String s: set) {
			GameInfo ginfo = plugin.maps.get(s);
			if (ginfo.getStage() != 1) continue;
			ExplosiveMinecart cart = ginfo.getCart();
			Location loc = cart.getLocation();
			double x = loc.getX(), y = loc.getY(), z = loc.getZ();
//			Bukkit.broadcastMessage(x + " " + y + " " + z);
			ConfigurationSection sec = plugin.games.getConfigurationSection(s).getConfigurationSection("points");
			Set<String> pointlist = sec.getKeys(false);
			World world = Bukkit.getWorld(plugin.games.getConfigurationSection(s).getString("world"));
			Block block = world.getBlockAt(loc);
			for (String pointname: pointlist) {
				if (ginfo.haveCapped(pointname)) continue;
				ConfigurationSection sec2 = sec.getConfigurationSection(pointname);
				int pointx = sec2.getInt("x"), pointy = sec2.getInt("y"), pointz = sec2.getInt("z"), add = sec2.getInt("seconds");
//				Bukkit.broadcastMessage(pointx + " " + pointy + " " + pointz);
				if (block.getX() == pointx && block.getY() == pointy && block.getZ() == pointz) {
					Set<UUID> setp = ginfo.getPlayers();
					ginfo.setPrv(block, null);
					Location loc2 = new Location(world, pointx, pointy - 1, pointz);
					BlockState bs = world.getBlockAt(loc2).getState();
					bs.setData(new MaterialData(XMaterial.WHITE_WOOL.parseMaterial(), (byte) 3));
					bs.update();
					ginfo.addcountdown(add);
					BossBar barblu = ginfo.getBarBlu(), barred = ginfo.getBarRed();
					barblu.setTitle(convert(ChatColor.AQUA.toString(), ginfo.getcountdown() + 1));
					barred.setTitle(convert(ChatColor.RED.toString(), ginfo.getcountdown() + 1));
					for (UUID uuid: setp) {
						Player p = Bukkit.getPlayer(uuid);
						p.sendMessage(ChatColor.AQUA + "BLU team has captured point " + pointname);
					}
					ginfo.addCap(pointname);
					int tmp = ginfo.getCapped();
					if (tmp == pointlist.size()) {
						BLUWins bluwin = new BLUWins(plugin);
						bluwin.bluwins(s);
					}
					break;
				}
			}
		}
	}
}
