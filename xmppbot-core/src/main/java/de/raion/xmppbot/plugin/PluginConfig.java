package de.raion.xmppbot.plugin;/*
 * #%L
 * XmppBot Core
 * %%
 * Copyright (C) 2012 - 2013 Bernd Kiefer <b.kiefer@raion.de>
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
