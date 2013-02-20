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



/**
 * notifies about the status of a plugin
 *
 */
public interface PluginStatusListener {

	/**
	 * called when the plugin manager disabled a plugin
	 * @param pluginName shortname
	 * @param plugin the plugin
	 */
	public <T> void pluginDisabled(String pluginName, AbstractMessageListenerPlugin<T> plugin);


	/**
	 * called when the plugin manager enabled a plugin
	 * @param pluginName shortname
	 * @param plugin the plugin
	 */
	public <T> void pluginEnabled(String pluginName, AbstractMessageListenerPlugin<T> plugin);
}
