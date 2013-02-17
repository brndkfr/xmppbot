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


import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BaseConverter;

/**
 * convert Date Strings in format 'yyyyMMdd'T'HHmmss.SSS' into Date Objects
 *
 */
public class ISODateTimeConverter extends BaseConverter<Date> {

	  private DateTimeFormatter isoDateTimeFormat = ISODateTimeFormat.basicDateTime();

	 /**
	 * @param optionName a Name for the option
	 */
	public ISODateTimeConverter(String optionName) {
	    super(optionName);
	  }

	  public Date convert(String value) {
	    try {
	      return isoDateTimeFormat.parseDateTime(value).toDate();
	    } catch (Exception pe) {
	      throw new ParameterException("invalid value for pattern \"yyyyMMdd'T'HHmmss.SSSZ\", value="+value);
	    }
	  }
}

