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


import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.command.core.ScheduleCommand;

@SuppressWarnings("javadoc")
public class ScheduleCommandTest {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ScheduleCommandTest.class);



	@Test
	public void shouldMatchTimeFormatPattern() throws Exception {

		// given
		String timeString = "23:09";

		// when
		Matcher matcher = Pattern.compile(ScheduleCommand.TIME_FORMAT_PATTERN).matcher(timeString);

		// then
		assertTrue(matcher.matches());
	}



}
