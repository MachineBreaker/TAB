package me.neznamy.tab.platforms.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.neznamy.tab.api.PlaceholderManager;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.platforms.bukkit.nms.NMSStorage;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.features.PlaceholderManagerImpl;
import me.neznamy.tab.shared.placeholders.PlaceholderRegistry;
import me.neznamy.tab.shared.placeholders.PlayerPlaceholder;
import net.milkbowl.vault.chat.Chat;

/**
 * Bukkit registry to register bukkit-only placeholders
 */
public class BukkitPlaceholderRegistry implements PlaceholderRegistry {

	//formatter for 2 decimal places
	public final DecimalFormat decimal2 = ((DecimalFormat)NumberFormat.getNumberInstance(Locale.US));

	//plugin instance
	private JavaPlugin plugin;

	//vault chat
	private Object chat;

	private Method essGetUser;
	private Method essIsAfk;
	
	private Object server;
	private Field recentTps;
	

	/**
	 * Constructs new instance with given parameter
	 * @param plugin - plugin instance
	 */
	public BukkitPlaceholderRegistry(JavaPlugin plugin) {
		decimal2.applyPattern("#.##");
		this.plugin = plugin;
		Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
		try {
			if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
				RegisteredServiceProvider<?> rspChat = Bukkit.getServicesManager().getRegistration(Class.forName("net.milkbowl.vault.chat.Chat"));
				if (rspChat != null) chat = rspChat.getProvider();
			}
		} catch (Exception e) {
			//modded server without vault
		}
		if (essentials != null) {
			try {
				essGetUser = Class.forName("com.earth2me.essentials.Essentials").getMethod("getUser", Player.class);
				essIsAfk = Class.forName("com.earth2me.essentials.User").getMethod("isAfk");
			} catch (Exception e) {
				TAB.getInstance().getErrorManager().printError("Failed to load essentials methods", e);
			}
		}
		try {
			server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
			recentTps = server.getClass().getField("recentTps");
		} catch (Exception e) {
			//not spigot
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void registerPlaceholders(PlaceholderManager manager) {
		NumberFormat roundDown = NumberFormat.getInstance();
		roundDown.setRoundingMode(RoundingMode.DOWN);
		roundDown.setMaximumFractionDigits(2);
		manager.registerPlayerPlaceholder("%displayname%", 500, p -> ((Player) p.getPlayer()).getDisplayName());
		manager.registerServerPlaceholder("%tps%", 1000, () -> {
			try {
				return decimal2.format(Math.min(20, ((double[]) recentTps.get(server))[0]));
			} catch (Exception t) {
				return "-1";
			}
		});
		try {
			Class.forName("com.destroystokyo.paper.PaperConfig");
			manager.registerServerPlaceholder("%mspt%", 1000, () -> roundDown.format(Bukkit.getAverageTickTime()));
		} catch(Exception e){
			//not paper
		}
		if (NMSStorage.getInstance().getMinorVersion() >= 6) {
			manager.registerPlayerPlaceholder("%health%", 100, p -> (int) Math.ceil(((Player) p.getPlayer()).getHealth()));
		}
		manager.registerPlayerPlaceholder("%afk%", 500, new AFKPlaceholder().getFunction());
		registerOnlinePlaceholders(manager);
		registerVaultPlaceholders(manager);
		registerSyncPlaceholders(manager);
	}

	private void registerOnlinePlaceholders(PlaceholderManager manager) {
		manager.registerPlayerPlaceholder("%online%", 2000, p -> {
			int count = 0;
			for (TabPlayer all : TAB.getInstance().getOnlinePlayers()){
				if (((Player) p.getPlayer()).canSee((Player) all.getPlayer())) count++;
			}
			return count;
		});
		manager.registerPlayerPlaceholder("%staffonline%", 2000, p -> {
			int count = 0;
			for (TabPlayer all : TAB.getInstance().getOnlinePlayers()){
				if (all.hasPermission("tab.staff") && ((Player) p.getPlayer()).canSee((Player) all.getPlayer())) count++;
			}
			return count;
		});
		manager.registerPlayerPlaceholder("%nonstaffonline%", 2000, p -> {
			int count = 0;
			for (TabPlayer all : TAB.getInstance().getOnlinePlayers()){
				if (!all.hasPermission("tab.staff") && ((Player) p.getPlayer()).canSee((Player) all.getPlayer())) count++;
			}
			return count;
		});
	}

	/**
	 * Registers vault placeholders
	 */
	private void registerVaultPlaceholders(PlaceholderManager manager) {
		if (Bukkit.getPluginManager().isPluginEnabled("Vault") && chat != null) {
			manager.registerPlayerPlaceholder("%vault-prefix%", 500, p -> ((Chat) chat).getPlayerPrefix((Player) p.getPlayer()));
			manager.registerPlayerPlaceholder("%vault-suffix%", 500, p -> ((Chat) chat).getPlayerSuffix((Player) p.getPlayer()));
		} else {
			manager.registerServerPlaceholder("%vault-prefix%", 1000000, () -> "");
			manager.registerServerPlaceholder("%vault-suffix%", 1000000, () -> "");
		}
	}

	/**
	 * Registers synchronous placeholders
	 */
	private void registerSyncPlaceholders(PlaceholderManager manager) {
		PlaceholderManagerImpl pl = TAB.getInstance().getPlaceholderManager();
		for (String identifier : pl.getUsedPlaceholders()) {
			if (identifier.startsWith("%sync:")) {
				int refresh;
				if (pl.getServerPlaceholderRefreshIntervals().containsKey(identifier)) {
					refresh = pl.getServerPlaceholderRefreshIntervals().get(identifier);
				} else if (pl.getPlayerPlaceholderRefreshIntervals().containsKey(identifier)) {
					refresh = pl.getPlayerPlaceholderRefreshIntervals().get(identifier);
				} else {
					refresh = pl.getDefaultRefresh();
				}
				pl.registerPlaceholder(new PlayerPlaceholder(identifier, refresh) {
					
					@Override
					public Object get(TabPlayer p) {
						Bukkit.getScheduler().runTask(plugin, () -> {

							long time = System.nanoTime();
							String syncedPlaceholder = identifier.substring(6, identifier.length()-1);
							String value = ((BukkitPlatform) TAB.getInstance().getPlatform()).setPlaceholders((Player) p.getPlayer(), "%" + syncedPlaceholder + "%");
							getLastValues().put(p.getName(), value);
							if (!getForceUpdate().contains(p.getName())) getForceUpdate().add(p.getName());
							TAB.getInstance().getCPUManager().addPlaceholderTime(getIdentifier(), System.nanoTime()-time);
						});
						return getLastValues().get(p.getName());
					}
				});
			}
		}
	}
	
	public class AFKPlaceholder {

		private Plugin essentials;
		private Plugin afkplus;
		private boolean antiafkplus;
		private boolean purpur;
		
		protected AFKPlaceholder() {
			essentials = Bukkit.getPluginManager().getPlugin("Essentials");
			afkplus = Bukkit.getPluginManager().getPlugin("AFKPlus");
			antiafkplus = Bukkit.getPluginManager().isPluginEnabled("AntiAFKPlus");
			try {
				Player.class.getMethod("isAfk");
				purpur = true;
			} catch (Exception e) {
				//not purpur
			}
		}
		
		public Function<TabPlayer, Object> getFunction() {
			return p -> {
				try {
					if (essentials != null) {
						Object user = essGetUser.invoke(essentials, p.getPlayer());
						if ((boolean) essIsAfk.invoke(user)) return true;
					}
					if (afkplus != null) {
						Object afkplusplayer = afkplus.getClass().getMethod("getPlayer", UUID.class).invoke(afkplus, p.getUniqueId());
						if ((boolean) afkplusplayer.getClass().getMethod("isAFK").invoke(afkplusplayer)) return true;
					}
					if (antiafkplus) {
						Object api = Class.forName("de.kinglol12345.AntiAFKPlus.api.AntiAFKPlusAPI").getDeclaredMethod("getAPI").invoke(null);
						if ((boolean) api.getClass().getMethod("isAFK", Player.class).invoke(api, p.getPlayer())) return true;
					}
					if (purpur && ((Player)p.getPlayer()).isAfk()) return true;
				} catch (Exception e) {
					TAB.getInstance().getErrorManager().printError("Failed to check AFK status of " + p.getName(), e);
				}
				return false;
			};
		}
	}
}