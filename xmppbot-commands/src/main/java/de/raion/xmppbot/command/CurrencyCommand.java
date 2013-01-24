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


import java.util.Arrays;
import java.util.List;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.core.AbstractXmppCommand;
import de.raion.xmppbot.util.GCalculator;

/**
 * @author bkiefer
 *
 */
@CLICommand(name="convert", description="converts {number} {unit} in|to|into {targetUnit}")
public class CurrencyCommand extends AbstractXmppCommand {


	private static final String[] FILL_TOKENS = {"in", "to", "into", };

	private static Logger log = LoggerFactory.getLogger(CurrencyCommand.class);



	@Parameter(description = "from currency")
	private List<String> fromTokens;

	@Override
	public void executeCommand(XmppContext context) {

		fromTokens = removeTokens(fromTokens);

		GCalculator calc = new GCalculator();
		println(calc.convert(fromTokens.get(0), fromTokens.get(1), fromTokens.get(2)));
	}

	private List<String> removeTokens(List<String> tokenList) {

		tokenList.removeAll(Arrays.asList(FILL_TOKENS));
		return tokenList;
	}

}
