package de.raion.xmppbot.config;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for the bot
 *
 *
 */
public class BotConfiguration {


	private boolean xmppConnectionDebuggingEnabled = false;

	private Map<String, XmppConfiguration> configurations = new HashMap<String, XmppConfiguration>();

	/**
	 * get a specific configuration by name
	 *
	 * @return the configurations
	 */
	public Map<String, XmppConfiguration> getConfigurations() {
		return configurations;
	}

	/**
	 * configurations getter method.
	 *
	 * @return the configurations
	 */
	public void setConfigurations(Map<String, XmppConfiguration> configurations) {
		this.configurations = configurations;
	}

	public void add(String key, XmppConfiguration aConfig){
		this.configurations.put(key, aConfig);
	}

	/**
	 * xmppConnectionDebuggingEnabled getter method.
	 * @return the xmppConnectionDebuggingEnabled
	 */
	public boolean isXmppConnectionDebuggingEnabled() {
		return xmppConnectionDebuggingEnabled;
	}

	/**
	 * xmppConnectionDebuggingEnabled getter method.
	 * @return the xmppConnectionDebuggingEnabled
	 */
	public void setXmppConnectionDebuggingEnabled(boolean xmppConnectionDebuggingEnabled) {
		this.xmppConnectionDebuggingEnabled = xmppConnectionDebuggingEnabled;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BotConfiguration [configurations=" + configurations + "]";
	}
}
