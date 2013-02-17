package de.raion.xmppbot.filter;
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


import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import de.raion.xmppbot.XmppContext;

/**
 * Convinience Implementation of the MessageFilter Interface, providing Access
 * to the XmppContext
 *
 */
public abstract class AbstractMessageFilter implements MessageFilter, PacketFilter {

	private XmppContext context;
	
	
	/**
	 * initializes the filter without context
	 * @see #setContext(XmppContext)
	 */
	public AbstractMessageFilter() {};
	
	/**
	 * @param aContext the available context
	 */
	public AbstractMessageFilter(XmppContext aContext) {
		context = aContext;
	}
	
	/**
	 * @return the context, can be null
	 */
	public XmppContext getContext() {
		return context;
	}


	public void setContext(XmppContext context) {
		this.context = context;
	}

	/**
	 * Default implementation of {@link PacketFilter#accept(Packet)}, if the 
	 * packet is an instance of Message then {@link #accept(Message)} is called.
	 * @param packet the Packet to check
	 * @return	true if packet is a Message and {@link #accept(Message)} returns true,
	 * 			otherwise false
	 */
	public boolean accept(Packet packet) {
		if (packet instanceof Message) 
			return accept((Message)packet);
		return false;
	}

	public abstract boolean accept(Message message);

}
