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

import java.util.List;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.core.AbstractXmppCommand;

/**
 * @author bkiefer
 *
 */
@CLICommand(name="calc", description="calculates the given expression")

public class CalcCommand extends AbstractXmppCommand {
// static variables
/** default logger */
@SuppressWarnings("unused")
private static Logger log = LoggerFactory.getLogger(CalcCommand.class);

@Parameter (description="the expression to calculate")
private List<String> expression;

	@Override
	public void executeCommand(XmppContext context) {
		System.out.println("expression = "+expression.get(0));

		context.sendMessage("i will not do your math!");
	}

}
