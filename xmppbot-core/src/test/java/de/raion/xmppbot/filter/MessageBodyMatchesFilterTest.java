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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.regex.PatternSyntaxException;

import org.jivesoftware.smack.packet.Message;
import org.junit.Test;


public class MessageBodyMatchesFilterTest {

	@Test(expected = PatternSyntaxException.class)
	public void shouldThrowPatternSyntaxExceptionByNullArgument() {
		new MessageBodyMatchesFilter(null);
	}

	@Test(expected = PatternSyntaxException.class)
	public void shouldThrowPatternSyntaxExceptionByInvalidArgument() {
		String invalidRegex = "(a"; // right parenthesis missing
		new MessageBodyMatchesFilter(invalidRegex);
	}
	
	@Test
	public void shouldNotMatchWhenMessageHasNoBody() {
				
		// given
		Message message = new Message();
		MessageBodyMatchesFilter filter = new MessageBodyMatchesFilter("regex");
		assertFalse(filter.accept(message));
		
	}

	@Test
	public void shouldMatchUrl() {

		// given

		/*
		 * took it from
		 * http://stackoverflow.com/questions/163360/regular-expresion
		 * -to-match-urls-java and modified
		 */
		String regex = "(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";

		Message message = new Message();
		message.setBody("a text with a link http://stackoverflow.com/questions/163360/regular-expresion-to-match-urls-java inside");

		MessageBodyMatchesFilter filter = new MessageBodyMatchesFilter(regex);

		assertTrue(filter.accept(message));
		
		message = new Message();
		message.setBody("http://dict.leo.org/ende?lp=ende&p=DOKJAA&search=parenthesis&trestr=0x8040");
		
		assertTrue(filter.accept(message));
	}
}
