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

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;



/**
 * Abstract class for intercepting incomming Xmpp Packets / Messages with access
 * to the XmppContext of the Bot
 */
public abstract class AbstractPacketInterceptor implements PacketInterceptor {

	private XmppContext context;

	
	public abstract void interceptPacket(Packet packet);

	/**
	 * @return the filter for packets the interceptor is willing to accept
	 */
	public abstract PacketFilter getPacketFilter();

	
	/**
	 * @param ctxt the XmppContext of the Bot
	 */
	public void setContext(XmppContext ctxt) {
		context = ctxt;
	}


	/**
	 * @return used XmppContext
	 */
	public XmppContext getContext() {
		return context;
	}

}
