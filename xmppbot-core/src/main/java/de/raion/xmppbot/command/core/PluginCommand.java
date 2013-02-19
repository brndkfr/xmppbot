package de.raion.xmppbot.command.core;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.plugin.AbstractMessageListenerPlugin;
import de.raion.xmppbot.plugin.MessageListenerPlugin;
import de.raion.xmppbot.plugin.PluginManager;

/**
 * @TODO implement enabling/disabling of plugins
 *
 */
@CLICommand(name = "plugin", description = "managing available plugins")
public class PluginCommand extends AbstractXmppCommand {

	/** default logger */
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PluginCommand.class);

	@Parameter(names = { "-l", "-list" }, description = "shows all available plugins")
	boolean showList = false;

	@Parameter(names = { "-s", "-status" }, description = "shows the status of the plugins (enabled/disabled)")
	boolean showStatus = false;

	@Parameter(names = { "-d", "-disable" }, description = "disables the plugins (-d name ...)")
	List<String> plugins2Disable;

	@Parameter(names = { "-e", "-enable" }, description = "enables the plugins (-e name1 name2 ...)")
	List<String> plugins2Enable;

	
	public PluginCommand() {
		plugins2Disable = new ArrayList<String>();
		plugins2Enable = new ArrayList<String>();
	}

	@Override
	public void executeCommand(XmppContext context) {

		if(showList) {
			printPluginList(context);
		}
		if(showStatus) {
			printStatusList(context);
		}
		if(plugins2Disable.size() > 0) {
			disablePlugins(plugins2Disable, context);
		}

		if(plugins2Enable.size() > 0) {
			enablePlugins(plugins2Enable, context);
		}
	}


	private void enablePlugins(List<String> plugins2Disable2, XmppContext context) {
		PluginManager pluginManager = context.getPluginManager();
		for (String pluginName : plugins2Disable2) {
			Boolean enabled = pluginManager.enablePlugin(pluginName);
			if(enabled) {
				println(pluginName+" enabled");
			} else {
				println("couldn't enable plugin");
			}
		}
	}


	private void disablePlugins(List<String> plugins2Disable2, XmppContext context) {
		PluginManager pluginManager = context.getPluginManager();
		for (String pluginName : plugins2Disable2) {
			Boolean enabled = pluginManager.disablePlugin(pluginName);
			if(enabled) {
				println(pluginName+" disabled");
			} else {
				println("couldn't disable plugin");
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void printPluginList(XmppContext context) {

		Set<Entry<String, AbstractMessageListenerPlugin>> entrySet = context.getPluginManager().getPlugins().entrySet();

		for (Entry<String, AbstractMessageListenerPlugin> entry : entrySet) {
			MessageListenerPlugin annotation = entry.getValue().getClass().getAnnotation(MessageListenerPlugin.class);
			println(annotation.name()+" - "+annotation.description());
		}
	}

	private void printStatusList(XmppContext context) {

		Map<String, Boolean> statusMap = context.getPluginManager().getStatusMap();
		Set<String> keySet = statusMap.keySet();

		if(statusMap.size() == 0) {
			println("no plugins available!");
			return;
		}

		StringBuilder builder = new StringBuilder();

		for (String key : keySet) {

			builder.append(key).append("  - ");
			Boolean enabled = statusMap.get(key);
			if(enabled) {
				builder.append("enabled");
			} else {
				builder.append("disabled");
			}
			builder.append("\n");
		}
		println(builder.toString());
	}
}
