package de.raion.xmppbot.utils;

import static org.junit.Assert.assertEquals;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.junit.Test;

import de.raion.xmppbot.util.PacketUtils;

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


@SuppressWarnings("javadoc")
public class PacketUtilsTest {

	@Test
	public void shouldGetAddressAndNickFromPacket() throws Exception {

		//given
		String fromAddress="multiuserchat@conf.hipchat.com";
		String fromNick = "Enbot Botson";
		String from = fromAddress+"/"+fromNick;

		String toAddress="multiuserchat@conf.hipchat.com";
		String toNick = "Jane Doe";
		String to = toAddress+"/"+toNick;

		Packet packet = new Message();
		packet.setPacketID("12345");
		packet.setFrom(from);
		packet.setTo(to);

		//then
		assertEquals(fromAddress, PacketUtils.getFromAddress(packet));
		assertEquals(fromNick, PacketUtils.getFromNick(packet));
		assertEquals(fromNick, PacketUtils.getNick(from));
		assertEquals(toNick, PacketUtils.getNick(to));




	}
}
