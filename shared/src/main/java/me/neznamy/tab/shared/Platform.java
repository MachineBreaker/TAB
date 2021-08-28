package me.neznamy.tab.shared;

import java.io.File;
import java.util.List;

import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.protocol.PacketBuilder;
import me.neznamy.tab.shared.permission.PermissionPlugin;

/**
 * An interface with methods that are called in universal code, but require platform-specific API calls
 */
public interface Platform {

	/**
	 * Detects permission plugin and returns it's representing object
	 * @return the interface representing the permission hook
	 */
	public PermissionPlugin detectPermissionPlugin();
	
	/**
	 * Loads features
	 */
	public void loadFeatures();
	
	/**
	 * Sends a message into console
	 * @param message - the message
	 * @param translateColors - if color codes should be translated
	 */
	public void sendConsoleMessage(String message, boolean translateColors);
	
	/**
	 * Creates an instance of me.neznamy.tab.shared.placeholders.Placeholder to handle this unknown placeholder (typically a PAPI placeholder)
	 * @param identifier - placeholder's identifier
	 * @return new placeholder
	 */
	public void registerUnknownPlaceholder(String identifier);
	
	/**
	 * Returns server's version
	 * @return server's version
	 */
	public String getServerVersion();
	
	/**
	 * Returns the word used to separate config options. It's value is "world" for bukkit and "server" for proxies
	 * @return "world" on bukkit, "server" on proxies
	 */
	public String getSeparatorType();
	
	/**
	 * Returns plugin's data folder
	 * @return plugin's data folder
	 */
	public File getDataFolder();
	
	/**
	 * Calls platform-specific event
	 * This method is called when plugin is fully enabled
	 */
	public void callLoadEvent();
	
	/**
	 * Calls platform-specific event
	 * This method is called when player is fully loaded
	 */
	public void callLoadEvent(TabPlayer player);
	
	/**
	 * Returns max player count configured in server files
	 * @return max player count
	 */
	public int getMaxPlayers();
	
	/**
	 * Returns name of config file in the jar file on specific platform
	 * @return name of config file of the platform
	 */
	public String getConfigName();
	
	/**
	 * Returns platform-specific packet builder
	 * @return platform-specific packet builder
	 */
	public PacketBuilder getPacketBuilder();
	
	/**
	 * Converts value-signature array into platform-specific skin object
	 * @param properties - value and signature
	 * @return platform-specific skin object
	 */
	public Object getSkin(List<String> properties);
}