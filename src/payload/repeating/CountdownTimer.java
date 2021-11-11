package payload.repeating;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import payload.main.Payload.GameInfo;
import payload.main.Payload;
import payload.gamehandler.*;

public class CountdownTimer extends BukkitRunnable {
	private Payload plugin;
	public CountdownTimer(Payload plugin) {
		this.plugin = plugin;
	}
	String convert(ChatColor prefix, int time) {
		int min = time / 60, sec = time % 60;
		String ans;
		if (sec >= 10) ans = min + ":" + sec;
		else ans = min + ":0" + sec;
		return prefix.toString() + ChatColor.BOLD + ans;
	}
	@Override
	public void run() {
		Set<String> set = plugin.maps.keySet();
		for (String s: set) {
			GameInfo ginfo = plugin.maps.get(s);
			if (ginfo.gettot() == 0) continue;
			BossBar barblu = ginfo.getBarBlu(), barred = ginfo.getBarRed();
			Set<UUID> players = ginfo.getPlayers();
			if (ginfo.getStage() < 2) {
				if (ginfo.getcountdown() <= 5 && ginfo.getcountdown() > 0) {
					for (UUID uuid: players) {
						Player p = Bukkit.getPlayer(uuid);
						String pref1, pref2;
						if (ginfo.getTeam(p) == "RED") {
							pref1 = ChatColor.RED.toString();
							pref2 = ChatColor.DARK_RED.toString();
						} else {
							pref1 = ChatColor.AQUA.toString();
							pref2 = ChatColor.DARK_AQUA.toString();
						}
						int opening = 10;
						if (ginfo.getcountdown() < 5) opening = 0;
						if (ginfo.getStage() == 0)
							p.sendTitle(pref1 + "Mission begins in", pref2 + Integer.toString(ginfo.getcountdown()), opening, 30, 20);
						else
							p.sendTitle(pref1 + "Mission ends in", pref2 + Integer.toString(ginfo.getcountdown()), opening, 30, 20);
						p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, (float) 1.0, (float) 1.0);
					}
				}
			}
			if (ginfo.getcountdown() == 0) {
				if (ginfo.getStage() == 0) {
					StartGame startgame = new StartGame(plugin);
					startgame.start(s);
				} else if (ginfo.getStage() == 1) {
					REDWins redwin = new REDWins(plugin);
					barblu.setTitle(convert(ChatColor.AQUA, 0));
					barred.setTitle(convert(ChatColor.RED, 0));
					redwin.redwins(s);
				} else {
					EndGame endgame = new EndGame(plugin);
					endgame.end(s);
				}
			}
			if (ginfo.getStage() < 2) {
				barblu.setTitle(convert(ChatColor.AQUA, ginfo.getcountdown()));
				barred.setTitle(convert(ChatColor.RED, ginfo.getcountdown()));
			}
			ginfo.addcountdown(-1);
		}
	}
}
