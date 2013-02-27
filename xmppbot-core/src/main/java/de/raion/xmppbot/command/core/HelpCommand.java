package de.raion.xmppbot.command.core;
/*
 * #%L
 * XmppBot
 * %%
 * Copyright (C) 2012
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.dharwin.common.tools.cli.api.CLIContext;
import net.dharwin.common.tools.cli.api.Command;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;
import net.dharwin.common.tools.cli.api.console.Console;
import net.dharwin.common.tools.cli.api.utils.CommandUtils;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;

/**
 * Prints all known commands. A parameter "c" can be specified to print the
 * usage for a specific command.<br>
 * <pre>
 * {@code
 * Usage: help [options]
 * Options:
 *   -c, --command   The command name to get specific help for.
 * }
 * </pre>
 * 
 * modification of: net.dharwin.common.tools.cli.api.defaultcommands.HelpCommand
 * @see net.dharwin.common.tools.cli.api.defaultcommands.HelpCommand
 */
@CLICommand(name = "help", description = "Prints known commands and usage.")
public class HelpCommand extends AbstractXmppCommand {

    @Parameter(names = { "-c", "--command" }, description = "The command name to get specific help for.")
    private String commandName;


	@Override
	public void executeCommand(XmppContext context) {
		if (commandName == null) {
			// Print command list.
			Set<String> knownCommandsSet = context.getHostApplication().getCommandNames();

			List<String> knownCommands = new ArrayList<String>(knownCommandsSet);
			Collections.sort(knownCommands);

			Map<String, String> commandHelpMap = new TreeMap<String, String>();

			// Find the longest command name and description.
            // Used in formatting.
            int longestCommandName = 0;
            int longestDescription = 0;

			// Create a map of command name to description, while also
            // finding the longest command name/description.
			for (String cmdName : knownCommands) {
			    if (cmdName.length() > longestCommandName) {
                    longestCommandName = cmdName.length();
                }
			    Class<? extends Command<? extends CLIContext>> c = CommandUtils.getCommandClass(context, cmdName);

			    String description = c.getAnnotation(CLICommand.class).description();
			    if (description.length() > longestDescription) {
			        longestDescription = description.length();
			    }
			    commandHelpMap.put(cmdName, description);
			}

			println("Known commands:");
			for (String command : knownCommands) {
			    String desc = commandHelpMap.get(command);
			    if (desc == null) {
			        desc = "";
			    }
			   println(String.format(getOutputString(command, desc, longestCommandName, longestDescription),
			                                         command, desc));
			}
		}
		else {
			// Print help for a specific command.
			Command<? extends CLIContext> command = null;
			try {
				Class<? extends Command<? extends CLIContext>> commandClass =  CommandUtils.getCommandClass(context, commandName);
				if (commandClass == null) {
					Console.error("Command ["+commandName+"] not recognized.");
				}
				command = commandClass.newInstance();
			}
			catch (Exception e) {
				Console.error("Error loading command help for ["+commandName+"].");
			}
			command.usage();
		}
	}

	
    /**
     * Get a format string for a help line.
     * This creates a string formattable to two columns, where the column
     * widths are dictated by the column width (calculated above as max
     * command name/description sizes). The columns are separated by two tabs
     * with a colon in the middle.
     * @param commandName The command name.
     * @param description The command description.
     * @param nameColWidth The column width for the name column.
     * @param descColWidth The column width for the description column.
     * @return
     */
    private String getOutputString(String commandName, String description,
            int nameColWidth, int descColWidth) {
        return new StringBuilder().append("%1$-").
        		                   append(nameColWidth).
        		                   append("s\t:\t%2$-").
        		                   append(descColWidth).
        		                   append("s").
        		                   toString();
    }

}
