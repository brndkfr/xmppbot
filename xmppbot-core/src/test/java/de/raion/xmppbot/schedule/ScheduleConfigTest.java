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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ScheduleConfigTest {

	@Test
	public void shouldInitCorrect() throws Exception {

		ScheduleConfig config = new ScheduleConfig();
		assertNotNull(config.getSchedules());
	}

	@Test
	public void shouldPutCommandString() throws Exception {
		
		ScheduleConfig config = new ScheduleConfig();
		assertTrue(config.put(1, "teststring"));
	}
	
	
	@Test
	public void shouldNotPutDuplicateCommandString() throws Exception {
		
		ScheduleConfig config = new ScheduleConfig();
		assertTrue(config.put(1, "teststring"));
		assertFalse(config.put(2, "teststring"));
	}


	@Test
	public void shouldRemoveCommandString() throws Exception {
		
		String cmd = "atestcmd";
		
		ScheduleConfig config = new ScheduleConfig();
		assertTrue(config.put(1, cmd));
		assertEquals(cmd, config.remove(1));
	}
	


	


	
}
