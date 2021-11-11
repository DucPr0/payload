package payload.gamehandler;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import payload.main.Payload;
import payload.main.Payload.GameInfo;
import payload.XMaterial;
import payload.playerhandler.DeathMode;
import payload.playerhandler.SpawnPlayer;

public class StartGame {
	private Payload plugin;
	public StartGame(Payload plugin) {
		this.plugin = plugin;
	}
	private void initPoint(World world, int x, int y, int z) {
		Block block = world.getBlockAt(new Location(world, x, y, z));
		block.setType(XMaterial.RED_WOOL.parseMaterial());
		BlockState bs = block.getState();
		bs.setType(XMaterial.RED_WOOL.parseMaterial());
		bs.setData(new MaterialData(XMaterial.RED_WOOL.parseMaterial(), (byte) 14));
		bs.update();
	}
	public void start(String name) {
		ConfigurationSection sec = plugin.games.getConfigurationSection(name);
		int x = sec.getInt("cartx"), y = sec.getInt("carty"), z = sec.getInt("cartz");
		World world = Bukkit.getWorld(sec.getString("world"));
		Location loc = new Location(world, x, y, z).add(0.5, 0, 0.5);
		Entity entity = world.spawnEntity(loc, EntityType.MINECART_TNT);
		entity.setGlowing(true);
		entity.setInvulnerable(true);
		int x2, y2, z2;
		x2 = sec.getInt("nextx"); y2 = sec.getInt("nexty"); z2 = sec.getInt("nextz");
		Location loc1 = new Location(world, x, y, z);
		Location loc2 = new Location(world, x2, y2, z2);
		ArmorStand as = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setCustomNameVisible(true);
		as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&b&lPayload"));
		entity.addPassenger(as);
		as.setVisible(false);
		GameInfo ginfo = plugin.maps.get(name);
		ginfo.setStage(1);
		ginfo.setCart((ExplosiveMinecart) entity);
		Set<UUID> set = ginfo.getPlayers();
		HashMap<UUID, String> playerinfo = ginfo.getInfo();
		Location spawnred, spawnblu;
		spawnred = ginfo.getSpawnRed();
		spawnblu = ginfo.getSpawnBlu();
		DeathMode dead = new DeathMode(plugin);
		for (UUID uuid: set) {
			Player p = Bukkit.getPlayer(uuid);
			String lol = playerinfo.get(uuid);
			if (plugin.playerinfo.get(p.getUniqueId()).getRespawnTime() > 0) {
				plugin.playerinfo.get(p.getUniqueId()).setRespawnTime(0);
				dead.putOutDeath(p);
			}
			SpawnPlayer spawnplayer = new SpawnPlayer(plugin);
			spawnplayer.spawnPlayer(p, spawnred, spawnblu, lol);
		}
		int timelimit = sec.getInt("timer");
		ginfo.setcountdown(timelimit);
		ginfo.reset();
		Block block1 = world.getBlockAt(loc1), block2 = world.getBlockAt(loc2);
		ginfo.setNxt(block1, block2);
		ginfo.setPrv(block2, block1);
		ginfo.setPrv(block1, null);
		Block cur = block2;
		ginfo.setOrder(block1, 0);
		int curorder = 1;
		while (cur != null) {
			ginfo.setOrder(cur, curorder);
			curorder++;
			boolean found = false;
			int curx = cur.getX(), cury = cur.getY(), curz = cur.getZ();
			for (int diffx = -1; diffx <= 1; diffx++) {
				for (int diffy = -1; diffy <= 1; diffy++) {
					for (int diffz = -1; diffz <= 1; diffz++) {
						if (found) continue;
						if (diffx != 0 && diffz != 0) continue;
						if (diffx == 0 && diffy == 0 && diffz == 0) continue;
						Block nxtblock = world.getBlockAt(new Location(world, curx + diffx, cury + diffy, curz + diffz));
						if (ginfo.isPrv(cur, nxtblock)) continue;
						if (nxtblock.getType().equals(XMaterial.RAIL.parseMaterial())) {
							found = true;
							ginfo.setNxt(cur, nxtblock);
							ginfo.setPrv(nxtblock, cur);
							cur = nxtblock;
							break;
						}
					}
				}
			}
			if (found == false) cur = null;
		}
		ginfo.setTotalRail(curorder - 1);
		ConfigurationSection pointsec = sec.getConfigurationSection("points");
		Set<String> setpoints = pointsec.getKeys(false);
		for (String s: setpoints) {
			ConfigurationSection apoint = pointsec.getConfigurationSection(s);
			Location pointloc = new Location(world, apoint.getInt("x"), apoint.getInt("y") - 1, apoint.getInt("z"));
			ginfo.setBlock(pointloc, world.getBlockAt(pointloc).getState().getData());
			initPoint(world, apoint.getInt("x"), apoint.getInt("y") - 1, apoint.getInt("z"));
		}
	}
}