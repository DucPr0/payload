package payload.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import payload.main.Payload;
import payload.XMaterial;

public class CancelDrops implements Listener {
	private Payload plugin;
	public CancelDrops(Payload plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onSpectatorDrop(PlayerDropItemEvent e) {
		if (plugin.playerinfo.containsKey(e.getPlayer().getUniqueId())) {
			if (e.getPlayer().getGameMode() == GameMode.ADVENTURE) e.setCancelled(true);
		}
	}
	@EventHandler
	public void onDropClassChoose(PlayerDropItemEvent e) {
		ItemStack istack = e.getItemDrop().getItemStack();
		if (istack.getType() == XMaterial.WHITE_WOOL.parseMaterial()) {
			if (istack.hasItemMeta() && istack.getItemMeta().hasDisplayName()) {
				String name = ChatColor.stripColor(istack.getItemMeta().getDisplayName());
				if (name.equals("Choose a class")) e.setCancelled(true);
			}
		}
	}
}
