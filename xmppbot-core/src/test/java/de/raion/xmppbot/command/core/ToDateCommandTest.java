package de.raion.xmppbot.command.core;
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
import static org.junit.Assert.assertNull;

import java.util.SimpleTimeZone;

import org.junit.Test;

import de.raion.xmppbot.command.core.ToDateCommand;

@SuppressWarnings("javadoc")
public class ToDateCommandTest {

	@Test
	public void shouldParseGMTimeZone() {

		String tz = "gmt";
		String offset = "+1";

		ToDateCommand command = new ToDateCommand();
		SimpleTimeZone timezone = command.parseTimeZone(tz+offset);

		assertNotNull(timezone);
		assertEquals(tz.toUpperCase(), timezone.getID());
		assertEquals(Integer.parseInt(offset.replace("+", ""))*1000*60*60, timezone.getRawOffset());
	}


	@Test
	public void shouldParseUTCTimeZone() {

		String tz = "UTc";
		String offset = "+0";

		ToDateCommand command = new ToDateCommand();
		SimpleTimeZone timezone = command.parseTimeZone(tz+offset);

		assertNotNull(timezone);
		assertEquals(tz.toUpperCase(), timezone.getID());
		assertEquals(Integer.parseInt(offset.replace("+", ""))*1000*60*60, timezone.getRawOffset());
	}


	@Test
	public void shouldReturnNull() {

		String tz = "gmtxx";
		String offset = "+0";

		ToDateCommand command = new ToDateCommand();
		SimpleTimeZone timezone = command.parseTimeZone(tz+offset);

		assertNull(timezone);
	}


	@Test
	public void shouldReturnNullWithNullParam() {

		ToDateCommand command = new ToDateCommand();
		SimpleTimeZone timezone = command.parseTimeZone(null);

		assertNull(timezone);
	}
}
