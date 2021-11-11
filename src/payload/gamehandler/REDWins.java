package payload.gamehandler;

import org.bukkit.Bukkit;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import payload.main.Payload;
import payload.main.Payload.GameInfo;
import java.util.Set;
import org.bukkit.util.Vector;

public class REDWins {
	private Payload plugin;
	public REDWins(Payload plugin) {
		this.plugin = plugin;
	}
	public void redwins(String name) {
		GameInfo ginfo = plugin.maps.get(name);
		ginfo.setcountdown(15);
		ginfo.setStage(2);
		Set<UUID> set = ginfo.getPlayers();
		String win = ChatColor.GREEN.toString() + ChatColor.BOLD + "VICTORY!";
		String lose = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "You failed!";
		String maintitle = ChatColor.translateAlternateColorCodes('&', "&c&lRED WINS!");
		ginfo.getCart().setVelocity(new Vector(0, 0, 0));
		for (UUID uuid: set) {
			Player p = Bukkit.getPlayer(uuid);
			if (ginfo.getTeam(p) == "RED") {
				ItemStack[] istack = p.getInventory().getContents();
				for (ItemStack is: istack){
					if (is == null) continue;
					is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 127);
					is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 127);
				}
				p.setWalkSpeed((float)(p.getWalkSpeed() * 1.2));
				p.sendTitle(maintitle, win, 10, 70, 20);
			} else {
				p.getInventory().clear();
				p.setWalkSpeed((float)(p.getWalkSpeed() * 0.75));
				p.sendTitle(maintitle, lose, 10, 70, 20);
			}
		}
	}
}
