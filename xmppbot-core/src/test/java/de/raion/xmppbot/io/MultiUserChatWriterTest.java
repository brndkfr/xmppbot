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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.raion.xmppbot.AbstractTestUsingMockito;

@SuppressWarnings("javadoc")
public class MultiUserChatWriterTest extends AbstractTestUsingMockito{

	@Mock
    private MultiUserChat multiUserChat;

	@InjectMocks
	private MultiUserChatWriter writer;

	@Test
	public void shouldWriteString() throws Exception {

		//given
        doNothing().when(multiUserChat).sendMessage(anyString());

        ArgumentCaptor<String> stringArgument = ArgumentCaptor.forClass(String.class);

        String expectedString = "a test string written via MultiUserChatWriter";

		//when
        writer.write(expectedString);

		//then
		verify(multiUserChat).sendMessage(stringArgument.capture());
		assertEquals(expectedString, stringArgument.getValue());
	}


	@Test
	public void shouldNotWriteNullStringToMultiUserChat() throws Exception {

		//given
        doNothing().when(multiUserChat).sendMessage(anyString());

        String nullString = null;

		//when
        writer.write(nullString);

		//then
		verify(multiUserChat, never()).sendMessage(anyString());
	}


	@Test
	public void shouldNotWritCharArrayWithLenghZeroToMultiUserChat() throws Exception {

		//given
        doNothing().when(multiUserChat).sendMessage(anyString());

        char[] cbuf = new char[0];

		//when
        writer.write(cbuf);

		//then
		verify(multiUserChat, never()).sendMessage(anyString());
	}

}
