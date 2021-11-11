package payload.gamehandler;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.material.MaterialData;
import payload.main.Payload;
import payload.playerhandler.PlayerQuit;
import payload.main.Payload.GameInfo;

public class EndGame {
	private Payload plugin;
	public EndGame(Payload plugin) {
		this.plugin = plugin;
	}
	public void end(String name) {
		GameInfo ginfo = plugin.maps.get(name);
		if (ginfo.getStage() == 0) return;
		ginfo.setStage(0);
		Set<Location> setloc = ginfo.allLoc();
		for (Location loc: setloc) {
			Block block = loc.getBlock();
			MaterialData matdata = ginfo.getBlock(loc);
			block.setType(matdata.getItemType());
			block.getState().setType(matdata.getItemType());
			block.getState().setData(matdata);
			block.getState().update();
		}
		ExplosiveMinecart cart = ginfo.getCart();
		List<Entity> list = cart.getPassengers();
		for (Entity e: list) e.remove();
		cart.remove();
		Set<UUID> players = ginfo.getPlayers();
		for (UUID uuid: players) PlayerQuit.quit(Bukkit.getPlayer(uuid), plugin);
		ginfo.getBarBlu().removeAll();
		ginfo.getBarRed().removeAll();
		ginfo.getBarBlu().setProgress(1);
		ginfo.getBarRed().setProgress(1);
//		Bukkit.broadcastMessage("Ended game " + name);
	}
}
