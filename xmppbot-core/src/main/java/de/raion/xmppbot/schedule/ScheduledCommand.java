package de.raion.xmppbot.schedule;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.io.ChatWriter;
import de.raion.xmppbot.io.MultiUserChatWriter;

/**
 * @author bkiefer
 *
 */
public class ScheduledCommand implements Runnable {

	/**  static logger */
	protected static Logger log = LoggerFactory.getLogger(ScheduledCommand.class);

	private static AtomicInteger ID_COUNTER = new AtomicInteger();

	private int id;
	private XmppContext context;
	private List<String> commandParamList;
	private String command;
	private String multiUserChatName;
	private String participant;

	private Map<String, String> optionMap;


	/**
	 * empty ScheduledCommand
	 */
	public ScheduledCommand() {
		id = generateId();
		optionMap = new HashMap<String, String>();
	}

	/**
	 * @param aContext the XmppContext to use
	 * @param aCommand the Command Name to execute
	 * @param aCommandParamList Parameters belonging to the Command
	 */
	public ScheduledCommand(XmppContext aContext, String aCommand,	List<String> aCommandParamList) {

		context = aContext;
		command = aCommand;
		commandParamList = aCommandParamList;
		id = generateId();
		optionMap = new HashMap<String, String>();
	}

	public void setContext(XmppContext context) {
		this.context = context;
	}

	public void setCommandParamList(List<String> aParamList) {
		commandParamList = aParamList;

	}

	private int generateId() {
		
		return ID_COUNTER.incrementAndGet();
	}

	
	public void run() {

		if(multiUserChatName != null) {
			runMultiUserChatContext();
			return;
		}
		else if(participant != null) {
			runChatContext();
			return;
		}
	}

	private void runChatContext() {

		Chat chat = context.getBot().getChat(participant);

		if(chat == null) {
			log.warn("Chat '{}' currently not available, skipping execution", chat);
			return;
		}


		context.setChat(chat);
		context.setPrintWriter(new PrintWriter(new ChatWriter(chat)));
		context.getBot().processCommand(createCommand(command, commandParamList));

	}

	private void runMultiUserChatContext() {

		MultiUserChat muc = context.getBot().getMultiUserChat(multiUserChatName);

		if(muc == null) {
			log.warn("MultiUserChat '{}' currently not available, skipping execution", muc);
			return;
		}


		context.setMultiUserChat(muc);
		context.setPrintWriter(new PrintWriter(new MultiUserChatWriter(muc)));
		context.getBot().processCommand(createCommand(command, commandParamList));

	}

	private String createCommand(String aCommand, List<String> aParamList) {

		StringBuilder builder = new StringBuilder(aCommand);

		for(String param: aParamList) {
			builder.append(" ").append(param);
		}

		return builder.toString();
	}

	

	/**
	 * sets the Name of the MultiUserChat the command is executed to
	 * @param aName
	 */
	public void setMultiUserChatName(String aName) {
		multiUserChatName = aName;

	}

	public void setParticipant(String aParticipant) {
		participant = aParticipant;

	}

	

	public void putOption(String key, String value) {
		optionMap.put(key, value);
		
	}

	public int getId() {
		return id;
	}

	
	public String getCommand() {
		return command;
	}

	public List<String> getParamList() {
		return commandParamList;
	}

	public String getMultiUserChatName() {
		return multiUserChatName;
	}

	public String getParticipant() {
		return participant;
	}


	
	public Map<String, String> getOptions() {
		return optionMap;
	}

	

	public String toCommandString() {
	
		StringBuilder builder = new StringBuilder();
		builder.append("schedule ");
		
		Set<String> keySet = optionMap.keySet();
		for (String key : keySet) {
			builder.append(key).append(" ").append(optionMap.get(key));
			builder.append(" ");
		}
		
		if(multiUserChatName != null)
			builder.append("-muc ").append(multiUserChatName).append(" ");
		
		if(participant != null)
			builder.append("-chat ").append(participant).append(" ");
		
		builder.append("-cmd ");
		builder.append(createCommand(command, commandParamList));
		
		return builder.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(id).append("] - ");
		if (command != null)
			builder.append("command=").append(command).append(", ");
		if (commandParamList != null)
			builder.append("commandParamList=").append(commandParamList)
					.append(", ");
		if (multiUserChatName != null)
			builder.append("multiUserChatName=").append(multiUserChatName)
					.append(", ");
		if (participant != null)
			builder.append("participant=").append(participant).append(", ");
		if (optionMap != null)
			builder.append("optionMap=").append(optionMap);
		return builder.toString();
	}

	

	







}
