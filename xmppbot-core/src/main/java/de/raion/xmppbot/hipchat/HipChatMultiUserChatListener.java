package de.raion.xmppbot.hipchat;
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

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.AbstractMultiUserChatListener;
import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.annotation.MultiUserChatListener;
import de.raion.xmppbot.config.BotConfiguration;
import de.raion.xmppbot.config.XmppConfiguration;
import de.raion.xmppbot.filter.MessageBodyContainsFilter;
import de.raion.xmppbot.filter.MessageFilter;

/**
 * HipChat specific implementation of the AbstractMultiUserChatListener
 * *
 */

@MultiUserChatListener(service = "hipchat")
public class HipChatMultiUserChatListener extends AbstractMultiUserChatListener {

	// static variables
	/** default logger */
	private static Logger log = LoggerFactory
			.getLogger(HipChatMultiUserChatListener.class);

	private MessageFilter acceptFilter;

    private String nickName;

	/**
	 * @param reference
	 * @param anAcceptFilter
	 */
	public HipChatMultiUserChatListener(XmppBot reference,
			MessageFilter anAcceptFilter) {
		super(reference);

		// TODO provide configuration for this
		acceptFilter = anAcceptFilter;
	}


	/**
	 * @param reference
	 * @param anAcceptFilter
	 */
	public HipChatMultiUserChatListener(XmppBot reference) {
		super(reference);

		acceptFilter = initFilter(reference);
	}


	private MessageFilter initFilter(XmppBot reference) {

		BotConfiguration config = reference.getConfiguration();
		XmppConfiguration xmppConfig = config.getConfigurations().get("hipchat");
		nickName = xmppConfig.getNickName().split(" ")[0].toLowerCase();

		return new MessageBodyContainsFilter("@"+nickName, true);
	}


	@Override
	public boolean accept(Message aMessage) {
		return acceptFilter.accept(aMessage);
	}

	@Override
	public void processMessage(MultiUserChat multiUserChat, Message message) {

		log.debug("Thread = " + Thread.currentThread().getName());
		String body = message.getBody();

		if (body.toLowerCase().contains("@"+nickName)) {

			log.debug("body before filter: " + body);

			body = body.toLowerCase().replace("@" + nickName, "");

			log.debug("body after filter: " + body);

			xmppBot.processCommand(body);

			log.debug("after xmppBot.processComand({})", body);
		}

	}



}
