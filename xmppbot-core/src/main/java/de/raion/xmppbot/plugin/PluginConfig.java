package de.raion.xmppbot.plugin;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * holds the configuration for the PluginManager. used for serialization/deserialization
 * @see PluginManager
 * @author b.kiefer
 *
 */
public class PluginConfig {
	
	private TreeMap<String, Boolean> pluginStatusMap = new TreeMap<String, Boolean>();

	public void setStatus(String key, Boolean value) {
		pluginStatusMap.put(key, value);
		
	}

	/**
	 * @return true if no configuration is present, otherwise false
	 */
	public boolean isEmpty() {
		return pluginStatusMap.isEmpty();
	}

	/**
	 * @return state of the plugin configuration (enabled/disabled)
	 */
	public Map<String, Boolean> getStatusMap() {
		return pluginStatusMap;
	}

	/**
	 * @param pluginName the plugin to check
	 * @return true if configuration for this plugin is available otherwise false
	 */
	public boolean containsKey(String pluginName) {
		return pluginStatusMap.containsKey(pluginName);
	}

	/**
	 * @return a set of pluginNames
	 */
	public Set<String> keySet() {
		return pluginStatusMap.keySet();
	}

	/**
	 * @param pluginName
	 * @return the state (enabled/disabled) for the plugin
	 */
	public Boolean get(String pluginName) {
		return pluginStatusMap.get(pluginName);
	}

	@Override
	public String toString() {
		return "PluginConfig [pluginStatusMap=" + pluginStatusMap + "]";
	}
	
}
