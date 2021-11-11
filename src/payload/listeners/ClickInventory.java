package payload.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import payload.XMaterial;
import payload.classhandler.ManageClass;
import payload.classhandler.OpenChange;
import payload.commands.Commands;
import payload.main.Payload.PlayerInfo;
import payload.main.Payload;

public class ClickInventory implements Listener {
	private Payload plugin;
	public ClickInventory(Payload plugin) {
		this.plugin = plugin;
	}
	public void onClickHead(InventoryClickEvent e) {
		//Handle clicking a head in spectate inventory
		if (plugin.playerinfo.containsKey(e.getWhoClicked().getUniqueId()) == false) return;
		Inventory inv = e.getInventory();
		if (ChatColor.stripColor(inv.getName()).equals("Spectate:") == false) return;
		e.setCancelled(true);
		if (e.getRawSlot() >= e.getInventory().getSize()) return;
		ItemStack skull = e.getCurrentItem();
		if (skull == null) return;
		if (skull.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial()) && skull.getDurability() == 3) {
			PlayerInfo pinfo = plugin.playerinfo.get(e.getWhoClicked().getUniqueId());
			Player p = Bukkit.getPlayer(e.getWhoClicked().getName());
			String newspectate = ChatColor.stripColor(skull.getItemMeta().getDisplayName());
			if (plugin.playerinfo.get(Bukkit.getPlayer(newspectate).getUniqueId()).getRespawnTime() > 0) {
				e.getWhoClicked().sendMessage(ChatColor.RED + "The selected player is dead");
				return;
			}
			p.teleport(Bukkit.getPlayer(newspectate).getLocation());
		}
	}
	public void onEditorClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (ChatColor.stripColor(inv.getName()).equals("Class Editor")) {
			if (e.getRawSlot() >= e.getInventory().getSize()) {
				e.setCancelled(true);
				return;
			}
			ItemStack clicked = e.getCurrentItem();
			if (clicked == null) return;
			e.setCancelled(true);
			if (clicked.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) return;
			if (clicked.hasItemMeta() == false) return;
			ManageClass cman = new ManageClass(plugin);
			cman.openManage(Bukkit.getPlayer(e.getWhoClicked().getName()), ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
		}
	}
	public void onManageClick(InventoryClickEvent e) {
		if (ChatColor.stripColor(e.getInventory().getName()).equals("Editing class")) {
			if (e.getRawSlot() >= e.getInventory().getSize()) {
				e.setCancelled(true);
				return;
			}
			if (e.getCurrentItem() == null) return;
			if (e.getCurrentItem().hasItemMeta() == false) return;
			String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
			OpenChange cman = new OpenChange(plugin);
			if (name.equals("Modify display icon")) {
				cman.openIconChange(Bukkit.getPlayer(e.getWhoClicked().getName()));
			} else if (name.equals("Modify speed")) {
				cman.openSpeedChange(Bukkit.getPlayer(e.getWhoClicked().getName()));
			} else if (name.equals("Modify health")) {
				cman.openHealthChange(Bukkit.getPlayer(e.getWhoClicked().getName()));
			} else {
				cman.openWeaponChange(Bukkit.getPlayer(e.getWhoClicked().getName()));
			}
		}
	}
	//Display icon listeners
	public void onClickGlass(InventoryClickEvent e) {
		if (ChatColor.stripColor(e.getInventory().getName()).equals("Editing Icon")) {
			if (e.getCurrentItem() == null) return;
			if (e.getCurrentItem().getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
	//Health listener
	public void onClickHealth(InventoryClickEvent e) {
		if (ChatColor.stripColor(e.getInventory().getName()).equals("Editing Health")) {
			if (e.getRawSlot() >= e.getInventory().getSize()) {
				e.setCancelled(true);
				return;
			}
			if (e.getCurrentItem() == null) return;
			if (e.getCurrentItem().hasItemMeta() == false) return;
			String classname = plugin.editing.get(e.getWhoClicked().getUniqueId());
			String name = e.getCurrentItem().getItemMeta().getDisplayName();
			name = ChatColor.stripColor(name);
			double curhealth = plugin.classes.getConfigurationSection(classname).getDouble("health");
			if (name.equals("Add 10")) {
				curhealth += 10;
			} else if (name.equals("Add 1")) {
				curhealth++;
			} else if (name.equals("Delete 10")) {
				if (curhealth <= 10) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "Class's health is smaller than 10");
				} else curhealth -= 10;
			} else {
				if (curhealth <= 1) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "Class's health is smaller than 1");
				} else curhealth--;
			}
			plugin.classes.getConfigurationSection(classname).set("health", curhealth);
			plugin.saveClass();
			OpenChange cman = new OpenChange(plugin);
			cman.openHealthChange(Bukkit.getPlayer(e.getWhoClicked().getUniqueId()));
		}
	}
	//Speed listener
	public void onClickSpeed(InventoryClickEvent e) {
		if (ChatColor.stripColor(e.getInventory().getName()).equals("Editing Speed")) {
			if (e.getRawSlot() >= e.getInventory().getSize()) {
				e.setCancelled(true);
				return;
			}
			if (e.getCurrentItem() == null) return;
			if (e.getCurrentItem().hasItemMeta() == false) return;
			String classname = plugin.editing.get(e.getWhoClicked().getUniqueId());
			String name = e.getCurrentItem().getItemMeta().getDisplayName();
			name = ChatColor.stripColor(name);
			double curspeed = plugin.classes.getConfigurationSection(classname).getDouble("speed");
			if (name.equals("Add 10%")) {
				curspeed += 10;
			} else if (name.equals("Add 1%")) {
				curspeed++;
			} else if (name.equals("Delete 10%")) {
				if (curspeed <= 10) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "Class's speed is smaller than 10%");
				} else curspeed -= 10;
			} else {
				if (curspeed <= 1) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "Class's speed is smaller than 1%");
				} else curspeed--;
			}
			plugin.classes.getConfigurationSection(classname).set("speed", curspeed);
			plugin.saveClass();
			OpenChange cman = new OpenChange(plugin);
			cman.openSpeedChange(Bukkit.getPlayer(e.getWhoClicked().getUniqueId()));
		}
	}
	//Class weapons listener
	public void onClassClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (ChatColor.stripColor(inv.getName()).equals("Choose your class:")) {
			ItemStack istack = e.getCurrentItem();
			if (istack == null) return;
			if (istack.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) {
				e.setCancelled(true);
				return;
			}
			if (e.getRawSlot() >= e.getInventory().getSize()) {
				e.setCancelled(true);
				return;
			}
			if (istack.hasItemMeta() && istack.getItemMeta().hasDisplayName()) {
				String classname = ChatColor.stripColor(istack.getItemMeta().getDisplayName());
				e.getWhoClicked().closeInventory();
				plugin.playerinfo.get(e.getWhoClicked().getUniqueId()).setCurclass(classname);
				e.getWhoClicked().sendMessage(ChatColor.GREEN + "You will respawn as " + classname + " on your next life");
			}
		}
	}
	public void onClickGame(InventoryClickEvent e) {
		if (e.getInventory().getName() == "List of maps") {
			if (e.getRawSlot() >= e.getInventory().getSize()) {
				e.setCancelled(true);
				return;
			}
			ItemStack istack = e.getCurrentItem();
			if (istack == null) return;
			if (istack.hasItemMeta() && istack.getItemMeta().hasDisplayName()) {
				String gamename = ChatColor.stripColor(istack.getItemMeta().getDisplayName());
				Commands joingame = new Commands(plugin);
				joingame.joinCommand(Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), gamename);
				e.getWhoClicked().closeInventory();
			}
		}
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		onClickHead(e);
		onEditorClick(e);
		onManageClick(e);
		onClickGlass(e);
		onClickHealth(e);
		onClickSpeed(e);
		onClassClick(e);
		onClickGame(e);
	}
}
