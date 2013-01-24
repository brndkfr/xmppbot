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

import static de.raion.xmppbot.util.PacketUtils.getNick;

import java.util.Iterator;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.AbstractPacketInterceptor;
import de.raion.xmppbot.annotation.PacketInterceptor;

/**
 * Interceptor for HipChat which checks Messages to MultiUserChats for containing Nicknames
 * and replaces them with the Hipchat @mention feature, for getting the attention of the
 * highlighted User.<br>
 * A Message containing the Nick 'Enbot Botson' would be highlighted as '@Enbot Botson'    
 */
@PacketInterceptor(service = "hipchat")
public class MentionNameHighlighterInterceptor extends
		AbstractPacketInterceptor {

	private static Logger log = LoggerFactory
			.getLogger(MentionNameHighlighterInterceptor.class);

	@Override
	public PacketFilter getPacketFilter() {
		return new PacketTypeFilter(Message.class);
	}

	@Override
	public void interceptPacket(Packet packet) {

		// packetfilter is set to MessageType
		addNickNameHighlighting((Message) packet);

	}

	private void addNickNameHighlighting(Message message) {

		if (getContext().getBot().getMultiUserChat(message.getTo()) != null) {

			Iterator<String> nicknameIterator = getContext().getBot()
					.getMultiUserChat(message.getTo()).getOccupants();

			String body = message.getBody();

			if (body != null) {
				while (nicknameIterator.hasNext()) {
					String user = nicknameIterator.next();
					String nick = getNick(user);
					String newNick = "@" + nick.replace(" ", "");

					if (body.toLowerCase().contains(nick.toLowerCase())) {
						body = body.replace(nick, newNick);

						message.setBody(null); // implementation otherwise
												// second
												// body added
						message.setBody(body);

						log.debug(message.toXML());
					}
				}
			}
		}
	}
}
