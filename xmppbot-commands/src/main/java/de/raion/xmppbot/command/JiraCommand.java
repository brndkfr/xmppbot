package de.raion.xmppbot.command;
/*
 * #%L
 * XmppBot Commands
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.core.AbstractXmppCommand;
import de.raion.xmppbot.plugin.JiraIssuePlugin;

/**
 * @author bkiefer
 *
 */
@CLICommand(name="jira", description="configures the jira-issue plugin")
public class JiraCommand extends AbstractXmppCommand {

	private static Logger log = LoggerFactory.getLogger(JiraCommand.class);

	@Parameter(names = { "-d", "--domain" }, description = "sets the domain jira is running")
	String domain;

	@Parameter(names = { "-u", "--update" }, description = "updates the configuration")
	Boolean update = false;

	@Parameter(names = { "-a", "--auth" }, description = "sets the authentication credentials, -a <usr> <pwd>")
	List<String> authentication;

	@Override
	public void executeCommand(XmppContext context) {
		if(authentication != null) {
			if(authentication.size() != 2) {
				println("invalid number of parameters for -a --auth, use -a <usr> <pwd> or --auth <usr> <pwd>");
			} else {
				setAuthentication(context);
			}
		}
		if(domain != null) {
			setDomain(domain, context);
		}
		if(update) {
			updateConfiguration(context);
		}

	}

	private void setAuthentication(XmppContext context) {
		try {
			JiraConfig config = context.loadConfig(JiraConfig.class);
			config.setAuthenticationUser(authentication.get(0));
			config.setAuthenticationPassword(authentication.get(1));
			context.saveConfig(config);
			JiraIssuePlugin plugin = context.getPluginManager().get(JiraIssuePlugin.class);
			plugin.updateConfiguration();


		} catch(IOException e) {
			log.error("setAuthentication(XmppContext)", e);
			println("error occured, couldn't set authentication credentials");
		}


	}

	private void updateConfiguration(XmppContext context) {
		try {
			JiraConfig config = context.loadConfig(JiraConfig.class);
			JiraIssuePlugin plugin = context.getPluginManager().get(JiraIssuePlugin.class);

			Map<String, String> oldProjects = config.getProjects();
			Map<String, String> newProjects = plugin.getProjects(config.getProjectURI());




		}catch(Exception e) {
			log.error("updateConfiguration(XmppContext)", e);
			println("couldn't update configuration, error occured, sry :(");
		}


	}

	private void setDomain(String aDomain, XmppContext context) {
		try {
			JiraConfig config = context.loadConfig(JiraConfig.class);
			config.setJiraDomain(aDomain);
			context.saveConfig(config);
			println("set jira domain to '"+domain+"'\n");

			if(context.getPluginManager().isEnabled(JiraIssuePlugin.class)) {
				JiraIssuePlugin plugin = context.getPluginManager().get(JiraIssuePlugin.class);
				Map<String, String> projectMap = plugin.getProjects(config.getProjectURI());
				String pattern = plugin.createMatchingPattern(projectMap.keySet());
				config.setMatchingPattern(pattern);
				config.setProjects(projectMap);
				context.saveConfig(config);
				plugin.updateConfiguration();
				Set<Entry<String, String>> set = projectMap.entrySet();
				StringBuilder builder = new StringBuilder("available projects:\n");

				if(projectMap.size() == 0) {
					builder.append("none");
				}

				for (Entry<String, String> entry : set) {
					builder.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
				}
				println(builder.toString());
			}
		}catch(Exception e) {
			log.error("setDomain(String, XmppContext)", e);
			println("couldn't configure domain, error occured");
		}
	}
}
