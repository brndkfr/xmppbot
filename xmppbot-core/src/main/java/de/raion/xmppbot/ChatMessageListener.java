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

import java.io.PrintWriter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.io.ChatWriter;

public class ChatMessageListener implements MessageListener {
	// static variables
	/** default logger */
	private static Logger log = LoggerFactory.getLogger(ChatMessageListener.class);
	private XmppBot xmppBot;

	/**
	 * @param reference
	 *            reference to the bot
	 */
	public ChatMessageListener(XmppBot reference) {
		xmppBot = reference;
	}

	public void processMessage(Chat aChat, Message aMessage) {

		final XmppBot bot = this.xmppBot;
		final Chat chat = aChat;
		final Message message = aMessage;

		log.debug("message received, type {}", message.getType().toString());


		if (message.getBody() != null) {

			Runnable runnable = new Runnable() {

				public void run() {

					Thread.currentThread().setName("Chat: "+chat.getParticipant()+ " Message: "+ message.getBody());

					PrintWriter threadPrintWriter = null;

					threadPrintWriter = new PrintWriter(new ChatWriter(chat));

					bot.getContext().setChat(chat);
					bot.getContext().setPrintWriter(threadPrintWriter);

					bot.processCommand(message.getBody());

					bot.getContext().removeMultiUserChat();
					bot.getContext().removePrintWriter();

				}
			};

			Thread t = new Thread(runnable);
			t.start();
		}

	}

}
