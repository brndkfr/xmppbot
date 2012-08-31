package de.raion.xmppbot.command;
/*
 * #%L
 * XmppBot Core
 * %%
 * Copyright (C) 2012 Bernd Kiefer
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

import java.util.Date;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import com.beust.jcommander.ParameterException;

@SuppressWarnings("javadoc")
public class ISODateTimeConverterTest {

	@Test
	public void shouldConvertISODateTimeString() throws Exception {

		//given
		String dateTimeString = "20120827T224028.191+0200";
		Date expectedDate = ISODateTimeFormat.basicDateTime().parseDateTime(dateTimeString).toDate();

		// When
		ISODateTimeConverter converter = new ISODateTimeConverter("timestamp");

		// then
		assertEquals(expectedDate, converter.convert(dateTimeString));
	}

	@Test (expected=ParameterException.class)
	public void shouldThrowParameterException() throws Exception {

		//given
		String dateTimeString = "nonsensstring";


		// When
		ISODateTimeConverter converter = new ISODateTimeConverter("timestamp");

		// then
		converter.convert(dateTimeString);
	}
}
