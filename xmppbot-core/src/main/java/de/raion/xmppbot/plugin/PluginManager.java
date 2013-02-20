package de.raion.xmppbot.plugin;
/*
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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.dharwin.common.tools.cli.api.utils.CLIAnnotationDiscovereryListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impetus.annovention.ClasspathDiscoverer;
import com.impetus.annovention.Discoverer;

import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.XmppContext;

/**
 * @author bkiefer
 *
 */
public class PluginManager {
	// static variables
	/** default logger */
	private static Logger log = LoggerFactory.getLogger(PluginManager.class);

	/** mapps all running plugins by their annotated name */
	@SuppressWarnings("rawtypes")
	protected Map<String, AbstractMessageListenerPlugin> plugins;
	
	private List<PluginStatusListener> statusListenerList;

	//private TreeMap<String, Boolean> pluginStatusMap;

	private XmppContext context;

	private PluginConfig pluginConfig;

	/**
	 *
	 *
	 */
	public PluginManager(XmppContext aContext) {
		context = aContext;
		//pluginStatusMap = new TreeMap<String, Boolean>();
		statusListenerList = new ArrayList<PluginStatusListener>();
		plugins = loadPlugins();
		pluginConfig = aContext.loadConfig(PluginConfig.class);

		if(pluginConfig.isEmpty()) {
			Set<String> keySet = plugins.keySet();
			for (String key : keySet) {
				//pluginStatusMap.put(key, Boolean.TRUE);
				pluginConfig.setStatus(key, Boolean.TRUE);
			}
		} else {
			Set<String> keySet = pluginConfig.keySet();
			for (String pluginName : keySet) {
				boolean state = pluginConfig.get(pluginName);
				if(state == Boolean.TRUE) {
					enablePlugin(pluginName);
				}
				if(state == Boolean.FALSE) {
					disablePlugin(pluginName);
				}
			}
		}
		log.info(pluginConfig.toString());
		
	}

	
	public boolean addPluginStatusListener(PluginStatusListener aListener) {
		return statusListenerList.add(aListener);
	}
	
	public boolean removePluginStatusListener(PluginStatusListener aListener) {
		return statusListenerList.remove(aListener);
	}

	@SuppressWarnings("rawtypes")
	public Map<String, AbstractMessageListenerPlugin> getEnabledPlugins() {
		return getPlugins(Boolean.TRUE);
	}

	/**
	 * unmodifiable map of loaded plugins
	 * @return unmodifiable map of loaded plugins mapped by their annotated name (lowercase)
	 * @see MessageListenerPlugin#name()
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, AbstractMessageListenerPlugin> getPlugins() {
		return Collections.unmodifiableMap(plugins);
	}


	@SuppressWarnings("rawtypes")
	public Map<String, AbstractMessageListenerPlugin> getDisabledPlugins() {
		return getPlugins(Boolean.FALSE);
	}

	/**
	 * enables the plugin
	 * @param pluginName the name of the plugin {@link MessageListenerPlugin#name()}
	 * @return true if plugin is available and enabled, otherwise false
	 * @see MessageListenerPlugin#name()
	 */
	@SuppressWarnings("unchecked")
	public Boolean enablePlugin(String pluginName) {
		boolean enabled =  setPluginState(pluginName, Boolean.TRUE);
		
		if(enabled) {
			for (PluginStatusListener listener : statusListenerList) {
				listener.pluginEnabled(pluginName, plugins.get(pluginName));
			}
		}
		
		return enabled;
	}

	/**
	 * disables the plugin
	 * @param pluginName the name of the plugin {@link MessageListenerPlugin#name()}
	 * @return true if plugin is available and disabled, otherwise false
	 * @see MessageListenerPlugin#name()
	 */
	@SuppressWarnings("unchecked")
	public Boolean disablePlugin(String pluginName) {
		boolean disabled = setPluginState(pluginName, Boolean.FALSE);
		
		if(disabled) {
			for (PluginStatusListener listener : statusListenerList) {
				listener.pluginDisabled(pluginName, plugins.get(pluginName));
			}
		}
		
		return disabled;
	}

	/**
	 * unmodifiable map displaying the status of each plugin
	 * @return 	unmodifiable map mapping {@link MessageListenerPlugin#name()} to the status
	 * 			(true=enabled/ false=disabled)
	 *
	 */
	public Map<String, Boolean> getStatusMap() {
		return Collections.unmodifiableMap(pluginConfig.getStatusMap());

	}

	@SuppressWarnings("rawtypes")
	public Boolean isEnabled(Class<? extends AbstractMessageListenerPlugin> pluginClass) {
		Collection<AbstractMessageListenerPlugin> plugins = getEnabledPlugins().values();
		for (AbstractMessageListenerPlugin plugin : plugins) {
			if(plugin.getClass().equals(pluginClass)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * get plugin by class
	 * @param pluginClass class of the plugin
	 * @return plugin
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T get(Class<? extends AbstractMessageListenerPlugin<T>> pluginClass) {
		Collection<AbstractMessageListenerPlugin> collection = getEnabledPlugins().values();
		for (AbstractMessageListenerPlugin<?> aPlugin : collection) {
			if(aPlugin.getClass().equals(pluginClass)) {
				return (T) aPlugin;
			}
		}
		return null;
	}



	private Boolean setPluginState(String key, Boolean state) {
		if(pluginConfig.containsKey(key)) {
			pluginConfig.setStatus(key, state);
			try {
				context.saveConfig(pluginConfig);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}


	@SuppressWarnings("rawtypes")
	private Map<String, AbstractMessageListenerPlugin> getPlugins(Boolean state) {

		Map<String, AbstractMessageListenerPlugin> map = new HashMap<String, AbstractMessageListenerPlugin>();

		Set<String> keySet = pluginConfig.keySet();

		for (String key : keySet) {
			if(pluginConfig.get(key).equals(state)) {
				map.put(key, plugins.get(key));
			}
		}
		return map;
	}



	@SuppressWarnings("rawtypes")
	private Map<String, AbstractMessageListenerPlugin> loadPlugins() {
		Discoverer discoverer = new ClasspathDiscoverer();
		CLIAnnotationDiscovereryListener discoveryListener = new CLIAnnotationDiscovereryListener(
				new String[] { MessageListenerPlugin.class.getName() });
		discoverer.addAnnotationListener(discoveryListener);
		discoverer.discover();

		return loadPlugins(discoveryListener.getDiscoveredClasses());
	}

	@SuppressWarnings("rawtypes")
	private Map<String, AbstractMessageListenerPlugin> loadPlugins(List<String> pluginClasses) {

		Map<String, AbstractMessageListenerPlugin> aPluginMap = new HashMap<String, AbstractMessageListenerPlugin>();

		for (String pluginClassName : pluginClasses) {

			try {
				@SuppressWarnings("unchecked")
				Class<AbstractMessageListenerPlugin> pluginClass = (Class<AbstractMessageListenerPlugin>) Class
						.forName(pluginClassName);

				if (AbstractMessageListenerPlugin.class.isAssignableFrom(pluginClass)) {
					MessageListenerPlugin pluginAnnotation = pluginClass
							.getAnnotation(MessageListenerPlugin.class);

					Constructor<AbstractMessageListenerPlugin> constructor = pluginClass
							.getConstructor(XmppBot.class);

					AbstractMessageListenerPlugin plugin = constructor.newInstance(context.getBot());

					aPluginMap.put(pluginAnnotation.name().toLowerCase(), plugin);
					log.debug("Loaded plugin [" + pluginAnnotation.name() + "].");
				}

			} catch (ClassNotFoundException e) {
				log.error("Unable to load plugin class [{}]", pluginClassName);
			} catch (Exception e) {
				log.error("Error while trying to load class {}, message = {}", pluginClassName,
						e.getMessage());
				log.error("loadPlugins(List<String>) - ", e);
			}

		}
		return aPluginMap;
	}

}
