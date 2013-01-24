package de.raion.xmppbot;
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;

import net.dharwin.common.tools.cli.api.CLIContext;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.raion.xmppbot.command.core.AbstractXmppCommand;
import de.raion.xmppbot.plugin.PluginManager;
import de.raion.xmppbot.schedule.ScheduleConfig;
import de.raion.xmppbot.schedule.ScheduleExecutionListener;
import de.raion.xmppbot.schedule.ScheduledCommandExecutor;

/**
 * XmppContext provides access to the most needed methods and configurations.
 * It also provide Thread specific functions.<br>
 *
 *
 */
public class XmppContext extends CLIContext implements ScheduleExecutionListener {

	private static Logger log = LoggerFactory.getLogger(XmppContext.class);

	private static InheritableThreadLocal<MultiUserChat> multiUserChatThreadLocal = new InheritableThreadLocal<MultiUserChat>();

	private static InheritableThreadLocal<Chat> chatThreadLocal = new InheritableThreadLocal<Chat>();

	private static InheritableThreadLocal<PrintWriter> printWriterThreadLocal = new InheritableThreadLocal<PrintWriter>();

	private static InheritableThreadLocal<Message> messageThreadLocal =  new InheritableThreadLocal<Message>();

	private XmppBot xmppBot;

	@SuppressWarnings("javadoc")
	protected  ScheduledCommandExecutor scheduleExecutor;

	private PluginManager pluginManager;





	/**
	 * Constructor
	 * @param bot reference to the XmppBot
	 */
	@SuppressWarnings("unchecked")
	public XmppContext(XmppBot bot) {
		super(bot);
		xmppBot = bot;
		scheduleExecutor = initScheduler(loadConfig(ScheduleConfig.class));
	}

	/**
	 * initializes the pluginManager and refreshes the Scheduler after the Startup with configured Commands
	 */
	public void init() {

		pluginManager = new PluginManager(this);

		final Collection<String> cmdCollection = scheduleExecutor.getConfig().getSchedules().values();

		for (String cmd : cmdCollection)
		{
			log.debug("processing command "+cmd);
			getBot().processCommand(cmd);
		}


		//scheduleExecutor.schedule(runnable, 10, TimeUnit.SECONDS);
	}


	/**
	 * initializes the ScheduledCommandExecutor with the given config if exist
	 * @param config the ScheduleConfig to use, can be null
	 * @return the initialized ScheduleCommandExecutor
	 */
	protected ScheduledCommandExecutor initScheduler(ScheduleConfig config) {

		ScheduledCommandExecutor executor = null;

		if(config != null) {
			executor = new ScheduledCommandExecutor(5, config);
		}
		else {
			executor = new ScheduledCommandExecutor(5);
		}
		executor.addScheduleExecutionListener(this);

		return executor;
	}

	@Override
	protected String getEmbeddedPropertiesFilename() {
		return "hipchat.properties";
	}


	/**
	 * sends a Message to Chat/MultiUserChat set in the current Thread the Context is running in
	 * @param message the Message to sent
	 */
	public void sendMessage(String message) {
		try {
			print(message);
		} catch (Exception e) {
			log.error("sendMessage {}", message, e);
		}
	}

	/**
	 * sets the MultiUserChannel to use for in the Thread the Context is running in
	 * @param muc the MultiUserChannel to use
	 */
	public void setMultiUserChat(MultiUserChat muc) {

		multiUserChatThreadLocal.set(muc);
	}

	/**
	 * sets the Chat to use for in the Thread the Context is running in
	 * @param aChat the Chat to use
	 */
	public void setChat(Chat aChat) {
		chatThreadLocal.set(aChat);
	}


	/**
	 * prints a String to the underlying thread specific printwriter
	 * @param aString the String to write
	 */
	public void print(String aString) {

		if(XmppContext.printWriterThreadLocal.get() != null ) {
			printWriterThreadLocal.get().print(aString);
		}

	}


	/**
	 * Prints a String and then terminates the line. The String is printed to the underlying
	 * MultiUserChat or Chat (Thread specific)
	 * @param aString the String to print
	 */
	public void println(String aString) {

		if(XmppContext.printWriterThreadLocal.get() != null ) {
			printWriterThreadLocal.get().println(aString);
		}
	}




	/**
	 * removes the current thread's multiuserchat
	 */
	public void removeMultiUserChat() {
		multiUserChatThreadLocal.remove();

	}

	/**
	 * @return the current thread's multiuserchat
	 */
	public MultiUserChat getMultiUserChat() {
		return multiUserChatThreadLocal.get();

	}

	/**
	 * set the printwriter for the current thread in the XmppContext
	 * and also in the XmppCommands
	 * @param aPrintWriter the printwriter to use
	 * @see AbstractXmppCommand#setPrintWriter(PrintWriter)
	 */
	public void setPrintWriter(PrintWriter aPrintWriter) {
		printWriterThreadLocal.set(aPrintWriter);
		AbstractXmppCommand.setPrintWriter(aPrintWriter);

	}

	/**
	 * removes the printwriter of the current thread
	 */
	public void removePrintWriter() {
		printWriterThreadLocal.remove();

	}


	/**
	 * the usernames currently available in the multiuserchat
	 * @param multiUserChat the multiuserchat to retrieves the avaible user
	 * @return the usernames currently available in the multiuserchat
	 */
	public Set<String> getAvailableUser(MultiUserChat multiUserChat) {
		return xmppBot.getAvailableUser(multiUserChat);

	}

	/**
	 * sets the xmpp message for the current thread
	 * @param message the message to use in the current thread
	 */
	public void setMessage(Message message) {
		messageThreadLocal.set(message);
	}

	/**
	 * @return the current Message set in the Context, can be null
	 */
	public Message getMessage() {
		return messageThreadLocal.get();
	}

	/**
	 * @return reference to the bot
	 */
	public XmppBot getBot() { return xmppBot; }


	/**
	 * loads the configuration for the specified class from the workingdirectory.<br>
	 * example: if the class is <code>MyConfig.class</code> and the workingdir is
	 * <code>xmppbot/config/</code>, then
	 * the context tries to load the file <code>xmppbot/config/myconfig.json</code> and
	 * mapps it into an instance of the class
	 * @param aClass configuration class
	 * @return	instance of aClass with mapped configuration or a none mapped Instance when
	 * 			no configuration file is available or <code>null</null> if the class or its nullary
	 * 			constructor is not accessible
	 */
	public <T>T loadConfig(Class<T> aClass) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(new File(aClass.getSimpleName().toLowerCase()+".json"), aClass);
		} catch (Exception  e) {

			log.error("loadConfig - {}", e.getMessage());
			try {
				return aClass.newInstance();
			} catch (Exception e1) {
				log.error("loadconfig - {}", e1.getMessage());
				return null;
			}
		}



	}

	/**
	 * saves the given Object via Jackson Object Mapper as json file in the
	 * working directory of the XmppBot.<br>
	 * example: if the objects class is <code>MyConfig.class</code> and the workingdir is
	 * <code>xmppbot/config/</code>, then the context tries to save the configuration to
	 * the file <code>xmppbot/config/myconfig.json</code>
	 * @param config the Object to save
	 * @throws IOException if IOException occure
	 */
	public void saveConfig(Object config) throws IOException {

		ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

		String filename = config.getClass().getSimpleName().toLowerCase()+".json";

		writer.writeValue(new File(filename), config);

	}


	/**
	 * @return reference to the internal Scheduler of the XmppBot
	 */
	public ScheduledCommandExecutor getScheduler() {
		return scheduleExecutor;

	}

	/**
	 * @return the thread specific Chat, can be null if in this thread the MultiUserChat is not set
	 */
	public Chat getChat() {
		return chatThreadLocal.get();
	}


	public void configurationUpdated(ScheduleConfig aConfig) {
		try {
			saveConfig(aConfig);
			log.info("configuration of the ScheduledCommandExecutor saved");
		} catch (IOException e) {
		    log.error("afterExecute(ScheduledCommand) - saveconfig", e);
		}
	}



	/**
	 * @return pluginmanager
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}



//	/**
//	 * @return
//	 */
//	public <T extends AbstractMessageListenerPlugin> T  getPluginInstance(Class<? extends AbstractMessageListenerPlugin> pluginClass) {
//		MessageListenerPlugin pluginAnnotation = pluginClass.getAnnotation(MessageListenerPlugin.class);
//		return (T) xmppBot.pluginMap.get(pluginAnnotation.name().toLowerCase());
//
//	}


}
