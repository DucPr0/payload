package payload.main;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import java.io.File;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitScheduler;
import payload.commands.Commands;
import payload.listeners.*;
import payload.gamehandler.EndGame;
import payload.repeating.*;

public class Payload extends JavaPlugin {
	public File gamefile = new File(getDataFolder(), "games.yml");
	public FileConfiguration games = YamlConfiguration.loadConfiguration(gamefile);
	public File classfile = new File(getDataFolder(), "classes.yml");
	public FileConfiguration classes = YamlConfiguration.loadConfiguration(classfile);
	public static class GameInfo {
		private HashMap<UUID, String> teams = new HashMap<UUID, String>();
		private HashMap<String, Boolean> capyet = new HashMap<String, Boolean>();
		private HashMap<Block, Block> prvblock = new HashMap<Block, Block>();
		private HashMap<Block, Block> nxtblock = new HashMap<Block, Block>();
		private HashMap<Location, MaterialData> ogblock = new HashMap<Location, MaterialData>();
		private HashMap<Block, Integer> order = new HashMap<Block, Integer>(); 
		private BossBar barblu = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
		private BossBar barred = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
		private int redcount = 0, blucount = 0, countdown = 0, capped = 0, lastsec = 0, totalrail = 0;
		private Location defred, defblu, spawnred, spawnblu;
		private double secs = 0;
		private ExplosiveMinecart cart;
		private int stage = 0;
		public int getred() { return redcount; }
		public int getblu() { return blucount; }
		public int gettot() { return redcount + blucount; }
		public void reset() {
			prvblock.clear();
			nxtblock.clear();
			capyet.clear();
			capped = 0;
			ogblock.clear();
			secs = 0;
			barblu.setProgress(0);
			barred.setProgress(0);
			spawnred = defred;
			spawnblu = defblu;
		}
		public void setSpawnRed(Location loc) {
			spawnred = loc;
		}
		public void setSpawnBlu(Location loc) {
			spawnblu = loc;
		}
		public Location getSpawnRed() {
			return spawnred;
		}
		public Location getSpawnBlu() {
			return spawnblu;
		}
		public void setDefRed(Location loc) {
			defred = loc;
		}
		public void setDefBlu(Location loc) {
			defblu = loc;
		}
		public void setOrder(Block block, int cnt) {
			order.put(block, cnt);
		}
		public int getOrder(Block block) {
			return order.get(block);
		}
		public void setTotalRail(int totalrail) {
			this.totalrail = totalrail;
		}
		public int getTotalRail() {
			return totalrail;
		}
		public BossBar getBarBlu() {
			return barblu;
		}
		public BossBar getBarRed() {
			return barred;
		}
		public Set<Location> allLoc() {
			return ogblock.keySet();
		}
		public MaterialData getBlock(Location loc) {
			return ogblock.get(loc);
		}
		public void setBlock(Location loc, MaterialData matdata) {
			ogblock.put(loc, matdata);
		}
		public void addSecs(double add) {
			secs += add;
		}
		public void setSecs(double secs) {
			this.secs = secs;
		}
		public double getSecs() {
			return secs;
		}
		public boolean haveCapped(String name) {
			return capyet.containsKey(name);
		}
		public ExplosiveMinecart getCart() {
			return cart;
		}
		public void setCart(ExplosiveMinecart cart) {
			this.cart = cart;
		}
		public int getCapped() {
			return capped;
		}
		public void addCap(String name) {
			capped++;
			capyet.put(name, true);
		}
		public void setcountdown(int countdown) {
			this.countdown = countdown;
		}
		public void addcountdown(int add) {
			countdown += add;
		}
		public int getcountdown() {
			return countdown;
		}
		public void inc(String team, Player player) {
			if (team.equals("RED")) {
				redcount++;
				barred.addPlayer(player);
			} else {
				blucount++;
				barblu.addPlayer(player);
			}
			teams.put(player.getUniqueId(), team);
		}
		public void rem(Player player) {
			String team = teams.get(player.getUniqueId());
			if (team == "RED") {
				redcount--;
				barred.removePlayer(player);
			} else {
				blucount--;
				barblu.removePlayer(player);
			}
			teams.remove(player.getUniqueId());
		}
		public HashMap<UUID, String> getInfo() {
			return teams;
		}
		public Set<UUID> getPlayers() {
			Set<UUID> ans = new HashSet<UUID>(teams.keySet());
			return ans;
		}
		public void setStage(int stage) {
			this.stage = stage;
		}
		public int getStage() { return stage; }
		public String getTeam(Player player) {
			return teams.get(player.getUniqueId());
		}
		public void setPrv(Block block1, Block block2) {
			prvblock.put(block1, block2);
		}
		public boolean isPrv(Block block1, Block check) {
			Block tmp = prvblock.get(block1);
			if (tmp.getX() == check.getX() && tmp.getY() == check.getY() && tmp.getZ() == check.getZ()) return true;
			return false;
		}
		public Block getNxt(Block block) {
			return nxtblock.get(block);
		}
		public Block getPrv(Block block) {
			return prvblock.get(block);
		}
		public void setNxt(Block block1, Block block2) {
			nxtblock.put(block1, block2);
		}
	}
	public static class PlayerInfo {
		private ItemStack[] inv;
		private Location loc;
		private double health;
		private int food, respawnin;
		private String curgame;
		private boolean teamchat;
		private String curclass = null;
		public PlayerInfo(Player player) {
			inv = player.getInventory().getContents();
			loc = player.getLocation();
			health = player.getHealth();
			food = player.getFoodLevel();
			respawnin = 0;
		}
		public String getCurclass() {
			return curclass;
		}
		public void setCurclass(String curclass) {
			this.curclass = curclass;
		}
		public void setRespawnTime(int respawnin) {
			this.respawnin = respawnin;
		}
		public int getRespawnTime() {
			return respawnin;
		}
		public void setTeamChat(boolean teamchat) {
			this.teamchat = teamchat;
		}
		public boolean getTeamChat() {
			return teamchat;
		}
		public void setGame(String curgame) {
			this.curgame = curgame;
		}
		public String getGame() {
			return curgame;
		}
		public void set(Player player) {
			player.getInventory().setContents(inv);
			player.teleport(loc);
			player.setHealth(health);
			player.setFoodLevel(food);
		}
		public ItemStack[] getInv() {
			return inv;
		}
		public Location getLoc() {
			return loc;
		}
		public double getHealth() {
			return health;
		}
		public int getFood() {
			return food;
		}
	}
	public HashMap<String, GameInfo> maps = new HashMap<String, GameInfo>();
	public HashMap<UUID, PlayerInfo> playerinfo = new HashMap<UUID, PlayerInfo>();
	public HashMap<UUID, String> editing = new HashMap<UUID, String>();
	public void saveGames() {
		try {
			games.save(gamefile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void saveClass() {
		try {
			classes.save(classfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onEnable() {
		Set<String> set = games.getKeys(false);
		for (String s: set) {
			maps.put(s, new GameInfo());
			GameInfo ginfo = maps.get(s);
			ConfigurationSection sec = games.getConfigurationSection(s);
			ConfigurationSection spawnred = sec.getConfigurationSection("spawnred");
			ConfigurationSection spawnblu = sec.getConfigurationSection("spawnblu");
			World world = Bukkit.getWorld(sec.getString("world"));
			ginfo.setDefRed(new Location(world, spawnred.getDouble("x"), spawnred.getDouble("y"), spawnred.getDouble("z"), 
					(float) spawnred.getDouble("yaw"), (float) spawnred.getDouble("pitch")));
			ginfo.setDefBlu(new Location(world, spawnblu.getDouble("x"), spawnblu.getDouble("y"), spawnblu.getDouble("z"),
					(float) spawnblu.getDouble("yaw"), (float) spawnblu.getDouble("pitch")));
			ginfo.reset();
		}
		this.getCommand("payload").setExecutor(new Commands(this));
		getServer().getPluginManager().registerEvents(new CancelDrops(this), this);
		getServer().getPluginManager().registerEvents(new GameBasics(this), this);
		getServer().getPluginManager().registerEvents(new ClickInventory(this), this);
		getServer().getPluginManager().registerEvents(new CloseInventory(this), this);
		getServer().getPluginManager().registerEvents(new RightClick(this), this);
		BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new CountdownTimer(this), 0, 20);
		scheduler.scheduleSyncRepeatingTask(this, new PayloadPush(this), 0, 1);
		scheduler.scheduleSyncRepeatingTask(this, new DetectControlPoint(this), 0, 1);
		scheduler.scheduleSyncRepeatingTask(this, new ActionBarHandler(this), 0, 20);
	}
	@Override
	public void onDisable() {
		EndGame endgame = new EndGame(this);
		Set<String> set = games.getKeys(false);
		for (String s: set) endgame.end(s);
	}
}
