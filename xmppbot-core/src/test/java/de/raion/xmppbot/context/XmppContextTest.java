package de.raion.xmppbot.context;
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


import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.config.BotConfiguration;

@SuppressWarnings("javadoc")
public class XmppContextTest {

	@Test
	public void shouldInitContextCorrect() throws Exception {

		//given
		XmppBot mockBot = mock(XmppBot.class);
		doNothing().when(mockBot).init(any(BotConfiguration.class));

		XmppContext context = new XmppContext(mockBot);

		TimeUnit.SECONDS.sleep(1);

		assertNotNull(context.getBot());
		assertNotNull(context.getScheduler());
	}

	@Test
	public void shouldInitSchedulerWithGivenConfigCorrect() throws Exception {

		//given
		XmppBot mockBot = mock(XmppBot.class);
		doNothing().when(mockBot).init(any(BotConfiguration.class));

		XmppContext context = new XmppContext(mockBot);
		//when

		//then
		//fail();
	}
}
