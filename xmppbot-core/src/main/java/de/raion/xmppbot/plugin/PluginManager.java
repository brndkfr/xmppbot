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

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

	private TreeMap<String, Boolean> pluginStatusMap;

	private XmppContext context;

	/**
	 *
	 *
	 */
	public PluginManager(XmppContext aContext) {
		context = aContext;
		pluginStatusMap = new TreeMap<String, Boolean>();
		plugins = loadPlugins();

		Set<String> keySet = plugins.keySet();
		for (String key : keySet) {
			pluginStatusMap.put(key, Boolean.TRUE);
		}
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
	public Boolean enablePlugin(String pluginName) {
		return setPluginState(pluginName, Boolean.TRUE);
	}

	/**
	 * disables the plugin
	 * @param pluginName the name of the plugin {@link MessageListenerPlugin#name()}
	 * @return true if plugin is available and disabled, otherwise false
	 * @see MessageListenerPlugin#name()
	 */
	public Boolean disablePlugin(String pluginName) {
		return setPluginState(pluginName, Boolean.FALSE);
	}

	/**
	 * unmodifiable map displaying the status of each plugin
	 * @return 	unmodifiable map mapping {@link MessageListenerPlugin#name()} to the status
	 * 			(true=enabled/ false=disabled)
	 *
	 */
	public Map<String, Boolean> getStatusMap() {
		return Collections.unmodifiableMap(pluginStatusMap);

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
		if(pluginStatusMap.containsKey(key)) {
			pluginStatusMap.put(key, state);
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}


	@SuppressWarnings("rawtypes")
	private Map<String, AbstractMessageListenerPlugin> getPlugins(Boolean state) {

		Map<String, AbstractMessageListenerPlugin> map = new HashMap<String, AbstractMessageListenerPlugin>();

		Set<String> keySet = pluginStatusMap.keySet();

		for (String key : keySet) {
			if(pluginStatusMap.get(key).equals(state)) {
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
