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

import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Filter checking Body content containing a specific string
 *
 */
public class MessageBodyContainsFilter extends AbstractMessageFilter {

	private static Logger log = LoggerFactory.getLogger(MessageBodyContainsFilter.class);
	private String containing;
	private boolean ignoreCase;



	/**
	 * Constructor with containing = null and ignoreCase = false
	 * @see #getContaining()
	 * @see #isIgnoreCase()
	 */
	public MessageBodyContainsFilter() {
		containing = null;
		ignoreCase = false;

	}

	/**
	 * @param token reference token the message body has to contain for acceptance
	 * @param ignoreCase shall ignore Cases
	 */
	public MessageBodyContainsFilter(String token, boolean ignoreCase) {

		this.ignoreCase = ignoreCase;

		if (ignoreCase) {
			this.containing = token.toLowerCase();
		} else {
			this.containing = token;
		}
	}




	@Override
	public boolean accept(Message aMessage) {

		log.debug(aMessage.toXML());

		if(containing == null) {
			return false;
		}

		String body = aMessage.getBody();

		if (body == null || body.equals("")) {
			return false;
		}

		if (ignoreCase) {
			return body.toLowerCase().contains(this.containing);
		}
		return body.contains(this.containing);
	}

	/**
	 * containing getter method.
	 *
	 * @return the containing
	 */
	public String getContaining() {
		return containing;
	}

	/**
	 * sets the phrase which should be contained in the message body
	 * @param phrase the phrase which should be contained in the message body
	 */
	public void setContaining(String phrase) {
		this.containing = phrase;
	}

	/**
	 * ignoreCase getter method.
	 *
	 * @return the ignoreCase
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * @param ignoreCase
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

}
