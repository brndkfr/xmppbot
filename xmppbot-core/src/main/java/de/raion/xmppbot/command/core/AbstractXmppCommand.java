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


import java.io.PrintWriter;

import net.dharwin.common.tools.cli.api.Command;
import net.dharwin.common.tools.cli.api.CommandResult;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import de.raion.xmppbot.XmppContext;

/**
 * Base Class for all XmppCommands
 *
 */
public abstract class AbstractXmppCommand extends Command<XmppContext> {

	private static Logger log = LoggerFactory.getLogger(AbstractXmppCommand.class);

	private static InheritableThreadLocal<PrintWriter> printStreamThreadLocal = new InheritableThreadLocal<PrintWriter>();


	/**
	 * Print the usage for the command.
	 * By default, this prints the description and available parameters.
	 */
	@Override
	public void usage() {
		CLICommand commandAnnotation = this.getClass().getAnnotation(CLICommand.class);
		println("Help for ["+commandAnnotation.name()+"].\n");
		String description = commandAnnotation.description();
		if (description != null && !description.isEmpty()) {
			println("Description: " + description);
		}
		JCommander comm = new JCommander(this);
		comm.setProgramName(commandAnnotation.name());

		StringBuilder builder = new StringBuilder();
		comm.usage(builder);

		println(builder.toString());
	}


	/**
	 * will be called for command execution with given context
	 * @param context the command should use during execution
	 */
	public abstract void executeCommand(XmppContext context);


	@Override
	public  CommandResult execute(XmppContext context) {
		try {
			CLICommand commandAnnotation = this.getClass().getAnnotation(CLICommand.class);
			log.info("executing {}", commandAnnotation.name());
			return innerExecute(context);
		}
		catch (Exception e) {
			log.error("innerExecute(XmppContext)", e);
			return CommandResult.ERROR;
		}
	}


	@Override
	protected CommandResult innerExecute(XmppContext context) {
		executeCommand(context);
		return CommandResult.OK;
	}


	/** prints a line to the underlying thread specific printstream and
	 *  appends a linefeed
	 * @param aString the message to print
	 */
	public void println(String aString) {
		if(printStreamThreadLocal.get() != null) {
			printStreamThreadLocal.get().println(aString);
		}
	}


	/**
	 * prints a line to the underlying thread specific printstream
	 * @param aString the message to print
	 */
	public void print(String aString) {
		if(printStreamThreadLocal.get() != null) {
			printStreamThreadLocal.get().print(aString);
		}
	}

	
	/**
	 * sets the thread specific printwriter to use
	 * @param aPrintWriter the printwriter to use
	 */
	public static void setPrintWriter(PrintWriter aPrintWriter) {
		printStreamThreadLocal.set(aPrintWriter);
	}


}
