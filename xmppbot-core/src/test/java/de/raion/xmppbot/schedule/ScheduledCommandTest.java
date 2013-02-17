package de.raion.xmppbot.schedule;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.XmppContext;

public class ScheduledCommandTest {

	protected static Logger log = LoggerFactory.getLogger(ScheduledCommandTest.class);
	
	@Test
	public void shouldInitCorrectWithoutParams() throws Exception {
		
		// when
		ScheduledCommand command = new ScheduledCommand();
		
		// then
		assertTrue(command.getId() > 0);
		assertNotNull(command.getOptions());
	}
	
	@Test
	public void shouldInitCorrectWithParams() throws Exception {
		
		// given
		XmppContext context = mock(XmppContext.class);
		String cmd = "echo";
		List<String> paramList = Arrays.asList("p1");
		
		
		// when
		ScheduledCommand command = new ScheduledCommand(context, cmd, paramList);
		
		// then
		assertTrue(command.getId() > 0);
		
		assertEquals(cmd, command.getCommand());
		assertEquals(paramList, command.getParamList());
		assertNotNull(command.getOptions());
	}
	
	
	@Test
	public void shouldBuildCommandStringCorrect() throws Exception {
		
		// given
		XmppContext context = mock(XmppContext.class);
		String cmd = "echo";
		List<String> paramList = Arrays.asList("p1");
		
		ScheduledCommand command = new ScheduledCommand(context, cmd, paramList);
		command.setMultiUserChatName("multiuserchat");
		command.setParticipant("participant");
		command.putOption("-daily", "23:17");
		
		String expectedString = "schedule -daily 23:17 -muc multiuserchat -chat participant -cmd echo p1";
		
		
		// then
		assertEquals(expectedString, command.toCommandString());
		
	}
	
}
