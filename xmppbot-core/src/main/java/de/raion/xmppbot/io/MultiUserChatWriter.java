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

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for writing in MultiUserChat via the Writer
 *
 */
public class MultiUserChatWriter extends Writer {

	private static Logger log = LoggerFactory
			.getLogger(MultiUserChatWriter.class);

	private MultiUserChat multiUserChat;

	/**
	 * @param aMultiUserChat the MultiUserChat to write to
	 */
	public MultiUserChatWriter(MultiUserChat aMultiUserChat) {
		multiUserChat = aMultiUserChat;
	}

	/**
	 * empty implementation, does nothing
	 */
	@Override
	public void close() throws IOException {
		// empty implementation, does nothing
	}

	/**
	 * empty implementation, does nothing
	 */
	@Override
	public void flush() throws IOException {
		// empty implementation, does nothing
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		if (cbuf == null || cbuf.length == 0) {
			return;
		}
		try {
			multiUserChat.sendMessage(new String(cbuf, off, len));
		}
		catch (XMPPException e) {
			log.error("write(char[[], int, int) - ", e);
		}
	}

	@Override
	public void write(String aString) throws IOException {
		if(aString == null) {
			return;
		}

		super.write(aString);
	}
}
