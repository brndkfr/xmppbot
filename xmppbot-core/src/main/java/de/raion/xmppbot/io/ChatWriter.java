package de.raion.xmppbot.io;
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

import java.io.IOException;
import java.io.Writer;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple Writer implementation for writing in Chats
 **/
public class ChatWriter extends Writer {

	/** default logger */
	private static Logger log = LoggerFactory.getLogger(ChatWriter.class);
	private Chat chat;

	/**
	 * @param aChat the chat to write to
	 */
	public ChatWriter(Chat aChat) {
		chat = aChat;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		try {
			chat.sendMessage(new String(cbuf, off, len));
		} catch (XMPPException e) {
			log.warn("Exception caught in write: " + e.getMessage(), e);
		}

	}

	/** does nothing */
	@Override
	public void flush() throws IOException { /* does nothing */ }


	/** does nothing */
	@Override
	public void close() throws IOException {  /* does nothing */ }
}
