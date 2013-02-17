package de.raion.xmppbot.util;
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

import org.jivesoftware.smack.packet.Packet;


/**
 * utils for retrieving infos out of a message
 * @author bkiefer
 *
 */
public class PacketUtils {

	/**
	 * gets the from nick of the packet
	 * @param aPacket m
	 * @return the nickname of the sender
	 */
	public static String getFromNick(final Packet aPacket) {
		return getNick(aPacket.getFrom());
	}
	
	public static String getToNick(final Packet aPacket) {
		return getNick(aPacket.getTo());
	}
	
	public static String getToName(Packet aPacket) {
		return getName(aPacket.getTo());
	}

	private static String getName(String to) {
		if(to == null)
			return "";
		
		return to.substring(0, to.indexOf("@"));
	}

	public static String getNick(String wholeAddress) {

		if(wholeAddress != null) {
			int index = wholeAddress.lastIndexOf("/");

			if (index != -1) {
				return wholeAddress.substring(index+1, wholeAddress.length());
			}
		}

		return "";
	}

	public static String getFromAddress(Packet aPacket) { return getAddress(aPacket.getFrom());	}
	
	
	private static String getAddress(String from) {
		if(from != null) {
			int index = from.lastIndexOf("/");

			if (index != -1) {
				return from.substring(0, index);
			}
		}

		return "";
	}
}
