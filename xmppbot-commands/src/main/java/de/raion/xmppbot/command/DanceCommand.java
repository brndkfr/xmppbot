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


import java.util.concurrent.TimeUnit;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;
import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.core.AbstractXmppCommand;

/**
 *
 *
 */
@CLICommand(name="dance!", description="le me dances!")
public class DanceCommand extends AbstractXmppCommand {

	@Override
	public void executeCommand(XmppContext context) {

		try {
			context.sendMessage("\\o/");
			TimeUnit.SECONDS.sleep(1L);
			context.sendMessage("\\o\\");
			TimeUnit.SECONDS.sleep(1L);
			context.sendMessage("/o/");
			TimeUnit.SECONDS.sleep(1L);
			context.sendMessage("\\o\\");
			TimeUnit.SECONDS.sleep(1L);
			context.sendMessage("\\o/");
			TimeUnit.SECONDS.sleep(1L);
			context.sendMessage("¯\\(°_o)/¯");

		}
		catch(Exception e) {System.out.println("dudi");}
	}
}
