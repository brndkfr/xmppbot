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


import java.util.List;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;

/**
 * echoes text
 *
 */
@CLICommand(name="echo", description="echo a line of text")
public class EchoCommand extends AbstractXmppCommand{

	@Parameter(description = "line of text")
	private List<String> tokenList;

	@Override
	public void executeCommand(XmppContext context) {

		for (String token : tokenList) {
			print(token+" ");
		}
		println("");
	}
}
