package payload.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import java.util.Arrays;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import payload.XMaterial;
import payload.classhandler.*;
import payload.main.Payload.GameInfo;
import payload.main.Payload.PlayerInfo;
import payload.main.Payload;
import payload.gamehandler.EndGame;
import payload.gamehandler.StartGame;
import payload.playerhandler.PlayerQuit;
import payload.playerhandler.PlayerJoin;

import java.util.Set;

public class Commands implements CommandExecutor {
	private Payload plugin;
	public Commands(Payload plugin) {
		this.plugin = plugin;
	}
	boolean isNumeric(String s) {
		if ((s.charAt(0) < '0' || s.charAt(0) > '9') && s.charAt(0) != '-') return false;
		for (int i = 1; i < s.length(); i++) if (s.charAt(i) < '0' || s.charAt(i) > '9') return false;
		return true;
	}
	String missargs = ChatColor.RED + "Not enough arguments";
	String mapnotfound = ChatColor.RED + "Map not found";
	String playeronly = ChatColor.RED + "This command can only be performed by a player";
	String invalidargs = ChatColor.RED + "Invalid arguments";
	String active = ChatColor.RED + "Game is currently in progress";
	String inactive = ChatColor.RED + "Game hasn't started yet";
	String removeall = ChatColor.RED + "Please remove all players from the map before modifying this information";
	String noaccess = ChatColor.DARK_RED + "You do not have access to this command";
	String admin[] = {
		ChatColor.translateAlternateColorCodes('&', "&e/payload add {name}: &7Add map {name}"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload delete {name}: &7Delete map {name}"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload setcart {name} {x} {y} {z} {x2} {y2} {z2}: &7Set"
				+ "position of map {name}'s payload along with its direction"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload addpoint {mapname} {x} {y} {z} {time} {name}: &7Add"
				+ " a checkpoint named {name} at position, also adding {time} seconds to team BLU"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload delpoint {mapname} {name}: &7Delete control"
				+ " point {name}"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload start {name}: &7Force - start the game on map {name}"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload end {name}: &7Force - end the game on map {name}"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload setlimit {name} {limit}: &7Set the limit on a "
				+ "team's size in map {name}. Default is 12."),
		ChatColor.translateAlternateColorCodes('&', "&e/payload setspawnred {name}: &7Set spawn for the "
				+ "defenders"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload setspawnblu {name}: &7Set spawn for the "
				+ "attackers"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload settimer {name} {limit}: &7Set the duration of the"
				+ " game in seconds"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload setrespawnred {name} {time}: &7Set respawn time for"
				+ "team RED"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload setrespawnblu {name} {time}: &7Set respawn time for"
				+ "team BLU"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload addclass {name}: &7Add a class"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload manageclass: &7Open a GUI to manage classes"),
		ChatColor.translateAlternateColorCodes('&', "&e/payloa delclass {name}: &7Delete a class"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload reload: &7Reload the plugin")
	};
	String playerhelp[] = {
		ChatColor.translateAlternateColorCodes('&', "&e/payload list: &7List all the available maps"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload quit: &7Quit current game"),
		ChatColor.translateAlternateColorCodes('&', "&e/payload teamchat: &7Toggle teamchat"),
	};
	void helpCommand(CommandSender sender, String args[]) {
		if (sender.hasPermission("payload.player") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.YELLOW + "Payload Help:");
			for (String s: playerhelp) {
				sender.sendMessage(s);
			}
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Payload Admin Help:");
			if (args[1].equalsIgnoreCase("admin")) {
				for (String s: admin) {
					sender.sendMessage(s);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Help section not recognized");
			}
		}
	}
	void addCommand(CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.games.contains(args[1])) {
			sender.sendMessage(ChatColor.RED + "Map already existed!");
			return;
		}
		plugin.maps.put(args[1], new GameInfo());
		ConfigurationSection sec = plugin.games.createSection(args[1]);
		sec.set("limit", 12);
		sec.set("timer", 240);
		sec.createSection("points");
		sender.sendMessage(ChatColor.GREEN + "Added map " + args[1]);
		plugin.saveGames();
	}
	void delCommand(CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.maps.containsKey(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		EndGame endgame = new EndGame(plugin);
		endgame.end(args[1]);
		plugin.games.set(args[1], null);
		plugin.maps.remove(args[1]);
		sender.sendMessage(ChatColor.GREEN + "Deleted map " + args[1]);
		plugin.saveGames();
	}
	void setCartCommand(CommandSender sender, String args[]) {
		if ((sender instanceof Player) == false) {
			sender.sendMessage(playeronly);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (args.length < 8) {
			sender.sendMessage(missargs);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		GameInfo ginfo = plugin.maps.get(args[1]);
		if (ginfo.getStage() > 0) {
			sender.sendMessage(active);
			return;
		}
		ConfigurationSection sec = plugin.games.getConfigurationSection(args[1]);
		Player player = (Player) sender;
		World world = player.getWorld();
		for (int i = 2; i < 8; i++) if (isNumeric(args[i]) == false) {
			sender.sendMessage(invalidargs);
			return;
		}
		int x = Integer.parseInt(args[2]), y = Integer.parseInt(args[3]), z = Integer.parseInt(args[4]);
		int nxtx = Integer.parseInt(args[5]), nxty = Integer.parseInt(args[6]), nxtz = Integer.parseInt(args[7]);
		sec.set("world", world.getName());
		sec.set("cartx", x);
		sec.set("carty", y);
		sec.set("cartz", z);
		sec.set("nextx", nxtx);
		sec.set("nexty", nxty);
		sec.set("nextz", nxtz);
		sender.sendMessage(ChatColor.GREEN + "Payload position set on map " + args[1]);
		plugin.saveGames();
	}
	void addPointCommand(CommandSender sender, String args[]) {
		if ((sender instanceof Player) == false) {
			sender.sendMessage(playeronly);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (args.length < 7) {
			sender.sendMessage(missargs);
			return;
		}
		for (int i = 2; i < 6; i++) if (isNumeric(args[i]) == false) {
			sender.sendMessage(invalidargs);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		GameInfo ginfo = plugin.maps.get(args[1]);
		if (ginfo.getStage() > 0) {
			sender.sendMessage(active);
			return;
		}
		if (plugin.games.getConfigurationSection(args[1]).contains("world") == false) {
			sender.sendMessage(ChatColor.RED + "Please set the payload's position first");
			return;
		}
		World world = Bukkit.getWorld(plugin.games.getConfigurationSection(args[1]).getString("world"));
		ConfigurationSection sec = plugin.games.getConfigurationSection(args[1]).getConfigurationSection("points");
		ConfigurationSection sec2 = sec.createSection(args[6]);
		int x = Integer.parseInt(args[2]), y = Integer.parseInt(args[3]), z = Integer.parseInt(args[4]);
		sec2.set("x", x);
		sec2.set("y", y);
		sec2.set("z", z);
		sec2.set("seconds", Integer.parseInt(args[5]));
		sender.sendMessage(ChatColor.GREEN + "Control point " + args[6] + " added");
		plugin.saveGames();
	}
	void delPointCommand(CommandSender sender, String args[]) {
		if (args.length < 3) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		GameInfo ginfo = plugin.maps.get(args[1]);
		if (ginfo.getStage() > 0) {
			sender.sendMessage(active);
			return;
		}
		ConfigurationSection sec = plugin.games.getConfigurationSection(args[1]).getConfigurationSection("points");
		if (sec.getKeys(false).contains(args[2]) == false) {
			sender.sendMessage(ChatColor.RED + "Control point not found");
			return;
		}
		sec.set(args[2], null);
		sender.sendMessage(ChatColor.GREEN + "Successfully deleted control point " + args[2]);
		plugin.saveGames();
	}
	void borderInventory(Inventory inv) {
		int sz = inv.getSize();
		ItemStack blackglass = new ItemStack(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), 1);
		blackglass.setDurability((short) 15);
		for (int i = 0; i < 9; i++) inv.setItem(i, blackglass);
		for (int i = sz - 9; i < sz; i++) inv.setItem(i, blackglass);
		for (int i = 0; i < sz; i += 9) inv.setItem(i, blackglass);
		for (int i = 8; i < sz; i += 9) inv.setItem(i, blackglass);
	}
	void listCommand(CommandSender sender) {
		if ((sender instanceof Player) == false) {
			sender.sendMessage(playeronly);
			return;
		}
		Player p = (Player) sender;
		if (p.hasPermission("payload.player") == false) {
			p.sendMessage(noaccess);
			return;
		}
		Inventory inv = Bukkit.createInventory(null, 54, "List of maps");
		borderInventory(inv);
		Set<String> set = plugin.maps.keySet();
		for (String s: set) {
			ItemStack istack = new ItemStack(XMaterial.TNT_MINECART.parseMaterial(), 1);
			ItemMeta meta = istack.getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + s);
			GameInfo ginfo = plugin.maps.get(s);
			int curplayer, lim;
			curplayer = ginfo.getPlayers().size();
			lim = plugin.games.getConfigurationSection(s).getInt("limit") * 2;
			String displaycount = ChatColor.GRAY.toString() + curplayer + " / " + lim + " players";
			String status = ChatColor.GREEN + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "CLICK TO JOIN";
			if (curplayer == lim) status = ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "GAME IS FULL";
			meta.setLore(Arrays.asList(displaycount, status));
			istack.setItemMeta(meta);
			inv.addItem(istack);
		}
		p.openInventory(inv);
	}
	void toggleTeamchat(CommandSender sender) {
		if ((sender instanceof Player) == false) return;
		if (sender.hasPermission("payload.player") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		Player player = (Player) sender;
		if (plugin.playerinfo.containsKey(player.getUniqueId()) == false) {
			sender.sendMessage(ChatColor.RED + "You are currently not in a game");
			return;
		}
		PlayerInfo pinfo = plugin.playerinfo.get(player.getUniqueId());
		if (pinfo.getTeamChat() == false) {
			pinfo.setTeamChat(true);
			sender.sendMessage(ChatColor.GREEN + "Toggled teamchat ON");
		} else {
			sender.sendMessage(ChatColor.GREEN + "Toggled teamchat " + ChatColor.RED + "OFF");
			pinfo.setTeamChat(false);
		}
	}
	void setLimitCommand(CommandSender sender, String args[]) {
		if (args.length < 3) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		if (isNumeric(args[2]) == false) {
			sender.sendMessage(invalidargs);
			return;
		}
		GameInfo ginfo = plugin.maps.get(args[1]);
		if (ginfo.gettot() > 0) {
			sender.sendMessage(removeall);
			return;
		}
		ConfigurationSection sec = plugin.games.getConfigurationSection(args[1]);
		sec.set("limit", Integer.parseInt(args[2]));
		plugin.saveGames();
	}
	void setTimerCommand(CommandSender sender, String args[]) {
		if (args.length < 3) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		if (isNumeric(args[2]) == false) {
			sender.sendMessage(invalidargs);
			return;
		}
		GameInfo ginfo = plugin.maps.get(args[1]);
		if (ginfo.getStage() > 0) {
			sender.sendMessage(active);
			return;
		}
		ConfigurationSection sec = plugin.games.getConfigurationSection(args[1]);
		sec.set("timer", Integer.parseInt(args[2]));
		sender.sendMessage(ChatColor.GREEN + "Timer of game " + args[1] + " set to " + args[2] + " seconds");
		plugin.saveGames();
	}
	void setSpawnTeam(CommandSender sender, String args[]) {
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		String teamname, secname;
		if (args[0].equals("setspawnred")) {
			teamname = "RED";
			secname = "spawnred";
		} else {
			teamname = "BLU";
			secname = "spawnblu";
		}
		if ((sender instanceof Player) == false) {
			sender.sendMessage(playeronly);
			return;
		}
		if (args.length < 2) {
			sender.sendMessage(missargs);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		Player player = (Player) sender;
		Location loc = player.getLocation();
		ConfigurationSection sec = plugin.games.getConfigurationSection(args[1]);
		ConfigurationSection sec2 = sec.createSection(secname);
		sec2.set("x", loc.getX());
		sec2.set("y", loc.getY());
		sec2.set("z", loc.getZ());
		sec2.set("yaw", loc.getYaw());
		sec2.set("pitch", loc.getPitch());
		GameInfo ginfo = plugin.maps.get(args[1]);
		if (teamname == "RED") ginfo.setDefRed(loc);
		else ginfo.setDefBlu(loc);
		ginfo.reset();
		sender.sendMessage(ChatColor.GREEN + "Spawn set for " + teamname + " team!");
		plugin.saveGames();
	}
	public void joinCommand(Player player, String gamename) {
		GameInfo ginfo = plugin.maps.get(gamename);
		ConfigurationSection sec = plugin.games.getConfigurationSection(gamename);
		Set<String> set = sec.getKeys(false);
		if (set.contains("world") == false || set.contains("spawnred") == false ||
				set.contains("spawnblu") == false) {
			player.sendMessage(ChatColor.RED + "Map is not fully setup");
			return;
		}
		if (sec.getConfigurationSection("points").getKeys(false).size() == 0) {
			player.sendMessage(ChatColor.RED + "Map is not fully setup");
			return;
		}
		if (set.contains("respawnred") == false || set.contains("respawnblu") == false) {
			player.sendMessage(ChatColor.RED + "Map is not fully setup");
			return;
		}
		int tot = ginfo.gettot();
		int lim = sec.getInt("limit");
		if (tot == lim * 2) {
			player.sendMessage(ChatColor.RED + "Game is full");
			return;
		}
		if (plugin.playerinfo.containsKey(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "Leave the current game first");
			return;
		}
		PlayerJoin.join(player, gamename, plugin);
	}
	void quitCommand(CommandSender sender) {
		if ((sender instanceof Player) == false) {
			sender.sendMessage(playeronly);
			return;
		}
		if (sender.hasPermission("payload.player") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		PlayerQuit.quit((Player) sender, plugin);
	}
	void startCommand(CommandSender sender, String[] args) {
		if (args.length < 2) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		if (plugin.maps.get(args[1]).getStage() > 0) {
			sender.sendMessage(active);
			return;
		}
		ConfigurationSection sec = plugin.games.getConfigurationSection(args[1]);
		Set<String> set = sec.getKeys(false);
		if (set.contains("world") == false || set.contains("spawnred") == false ||
				set.contains("spawnblu") == false) {
			sender.sendMessage(ChatColor.RED + "Map is not fully setup");
			return;
		}
		if (sec.getConfigurationSection("points").getKeys(false).size() == 0) {
			sender.sendMessage(ChatColor.RED + "Map need at least 1 control point to be the end");
			return;
		}
		if (set.contains("respawnred") == false || set.contains("respawnblu") == false) {
			sender.sendMessage(ChatColor.RED + "Map is not fully setup");
			return;
		}
		StartGame start = new StartGame(plugin);
		if (plugin.maps.get(args[1]).gettot() == 0) {
			sender.sendMessage(ChatColor.RED + "No one is playing this map");
			return;
		}
		start.start(args[1]);
		sender.sendMessage(ChatColor.GREEN + "Game started on map " + args[1]);
	}
	void endCommand(CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		GameInfo ginfo = plugin.maps.get(args[1]);
		if (ginfo.getStage() == 0) {
			sender.sendMessage(inactive);
			return;
		}
		EndGame endgame = new EndGame(plugin);
		endgame.end(args[1]);
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEnded game on map " + args[1]));
	}
	void setRespawnTime(CommandSender sender, String args[]) {
		if (args.length < 3) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.games.contains(args[1]) == false) {
			sender.sendMessage(mapnotfound);
			return;
		}
		if (isNumeric(args[2]) == false) {
			sender.sendMessage(invalidargs);
			return;
		}
		ConfigurationSection sec = plugin.games.getConfigurationSection(args[1]);
		String team;
		if (args[0].equals("setrespawnred")) {
			sec.set("respawnred", Integer.parseInt(args[2]));
			team = ChatColor.RED + "RED";
		} else {
			team = ChatColor.AQUA + "BLU";
			sec.set("respawnblu", Integer.parseInt(args[2]));
		}
		sender.sendMessage(ChatColor.GREEN + "Successfully set respawn time for team " + team);
		plugin.saveGames();
	}
	void addClass(CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.classes.contains(args[1])) {
			sender.sendMessage(ChatColor.RED + "Class already created");
			return;
		}
		ConfigurationSection sec = plugin.classes.createSection(args[1]);
		sec.set("display", XMaterial.STONE.toString());
		sec.set("displaydata", 0);
		sec.set("health", (double) 20);
		sec.set("speed", (double) 100);
		sec.createSection("contents");
		sender.sendMessage(ChatColor.GREEN + "Successfully created class " + args[1]);
		plugin.saveClass();
	}
	void manageClass(CommandSender sender, String args[]) {
		if ((sender instanceof Player) == false) {
			sender.sendMessage(playeronly);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		Player p = (Player) sender;
		ClassList manage = new ClassList(plugin);
		manage.openClassList((Player) sender);
	}
	void delClass(CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(missargs);
			return;
		}
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		if (plugin.classes.contains(args[1])) {
			plugin.classes.set(args[1], null);
			sender.sendMessage(ChatColor.GREEN + "Successfully deleted class " + args[1]);
			return;
		}
		sender.sendMessage(ChatColor.RED + "Class does not exist");
		plugin.saveClass();
	}
	public void reloadCommand(CommandSender sender) {
		if (sender.hasPermission("payload.admin") == false) {
			sender.sendMessage(noaccess);
			return;
		}
		plugin.games = YamlConfiguration.loadConfiguration(plugin.gamefile);
		plugin.classes = YamlConfiguration.loadConfiguration(plugin.classfile);
		sender.sendMessage(ChatColor.GREEN + "Successfully reloaded plugin");
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (args.length == 0 || args[0].equals("help")) {
			helpCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("add")) {
			addCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("setcart")) {
			setCartCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("delete")) {
			delCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("list")) {
			listCommand(sender);
		} else if (args[0].equalsIgnoreCase("start")) {
			startCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("end")) {
			endCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("quit")) {
			quitCommand(sender);
		} else if (args[0].equalsIgnoreCase("setlimit")) {
			setLimitCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("setspawnred") || args[0].equalsIgnoreCase("setspawnblu")) {
			setSpawnTeam(sender, args);
		} else if (args[0].equalsIgnoreCase("settimer")) {
			setTimerCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("teamchat") || args[0].equalsIgnoreCase("tc")) {
			toggleTeamchat(sender);
		} else if (args[0].equalsIgnoreCase("addpoint")) {
			addPointCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("delpoint")) {
			delPointCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("setrespawnred") || args[0].equalsIgnoreCase("setrespawnblu")) {
			setRespawnTime(sender, args);
		} else if (args[0].equalsIgnoreCase("addclass")) {
			addClass(sender, args);
		} else if (args[0].equalsIgnoreCase("delclass")) {
			delClass(sender, args);
		} else if (args[0].equalsIgnoreCase("manageclass")) {
			manageClass(sender, args);
		} else if (args[0].equalsIgnoreCase("reload")) {
			reloadCommand(sender);
		} else {
			sender.sendMessage(ChatColor.RED + "Command not recognized");
		}
		return true;
	}
}