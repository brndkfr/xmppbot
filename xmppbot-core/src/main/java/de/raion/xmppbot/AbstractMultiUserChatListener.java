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

import static de.raion.xmppbot.util.PacketUtils.getFromAddress;

import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.io.MultiUserChatWriter;

/**
 * Abstract Class for listening to MultiUserChats with access to the XmppContext
 * of the Bot. Implementing PacketListener and Listening for incoming Packets.
 * Via {@link #accept(Message)} the Listener decides which Packets to accept and
 * to route on {@link #processMessage(MultiUserChat, Message)} or {@link #processPresence(MultiUserChat, Presence)}
 *
 *
 */
public abstract class AbstractMultiUserChatListener implements PacketListener {

	/** logger for this class */
	protected static Logger log = LoggerFactory.getLogger(AbstractMultiUserChatListener.class);

	/** reference to the bot */
	protected XmppBot xmppBot;
		
	/**
	 * constructor
	 * @param reference to the xmppbot
	 */
	public AbstractMultiUserChatListener(XmppBot reference) {
		xmppBot = reference;
		
	}



	public void processPacket(Packet packet) {

		log.debug(packet.toXML());


		final XmppBot bot = this.xmppBot;
		final MultiUserChat muc = this.xmppBot.getMultiUserChat(getFromAddress(packet));

		Runnable runnable = null;

		if (packet instanceof Message) {
			final Message message = (Message) packet;

			if (accept(message)) {

				runnable = new Runnable() {

					public void run() {

						Thread.currentThread().setName("Message: " + message.getBody());

						PrintWriter threadPrintWriter = null;

						threadPrintWriter = new PrintWriter(new MultiUserChatWriter(muc));


						bot.getContext().setMultiUserChat(muc);
						bot.getContext().setPrintWriter(threadPrintWriter);
						bot.getContext().setMessage(message);

						log.debug("muc from context" + bot.getContext().getMultiUserChat());

						processMessage(muc, message);

						bot.getContext().removeMultiUserChat();
						bot.getContext().removePrintWriter();
					}
				};
			}
		} else if (packet instanceof Presence) {

			final Presence presence = (Presence) packet;

			runnable = new Runnable() {

				public void run() {

					Thread.currentThread().setName("Presence: " + presence.toString());

					bot.getContext().setMultiUserChat(muc);
					log.debug("muc from context " + bot.getContext().getMultiUserChat());

					processPresence(muc, presence);

					bot.getContext().removeMultiUserChat();
				}
			};
		} else {
			log.debug(packet.toXML());
		}


		if(runnable != null) {
			xmppBot.executorService.submit(runnable);
		}



	}

	/**
	 * Filter for incoming Packets. Decides which one to accept
	 * @param packet the Packet to check
	 * @return true of accepted, otherwise false
	 */
	public abstract boolean accept(Message packet);

	/**
	 * processes accepted Messages (see {@link #accept(Message)})
	 * @param muc the MultiUserChat the Message is coming from
	 * @param aMessage the Message to process
	 */
	public abstract void processMessage(MultiUserChat muc, Message aMessage);


	/**
	 * processes Packets of Type Presence. PresencePackets are not filtered via
	 * {@link #accept(Message)}
	 * @param muc the MultiUserChat the Presence Packet is coming from
	 * @param presence the Presence to process
	 */
	public void processPresence(MultiUserChat muc, Presence presence) {

		log.debug(presence.getType().toString());
		log.debug(presence.toXML());

		if (presence.getType() == Type.available) {
			xmppBot.userAvailable(muc, presence.getFrom());
		} else if (presence.getType() == Type.unavailable) {
			xmppBot.userUnavailabe(muc, presence.getFrom());
		}

		log.debug("available users " + xmppBot.getAvailableUser(muc));
	}

}
