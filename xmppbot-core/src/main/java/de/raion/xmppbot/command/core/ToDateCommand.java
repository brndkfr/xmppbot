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



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;

/**
 * Converts millis to Date in 'yyyy-MM-dd HH:mm:ss.SSS' format <br>
 * <pre>
 * {@code
 * Usage: todate [options] timestamp in millis
 * Options:
 *  -tz, -timezone   <3-letter-timezone><offset> (GMT+2, CET-2, ...)
 * }
 * </pre>
 */
@CLICommand(name = "todate", description = "converts millis to date in 'yyyy-MM-dd HH:mm:ss.SSS' format")
public class ToDateCommand extends AbstractXmppCommand {

	/** default logger */
	private static Logger log = LoggerFactory.getLogger(ToDateCommand.class);

	/** ([a-zA-Z]{3})(\\+|-)(\\d+) */
	public static final String TIMEZONE_PATTERN = "([a-zA-Z]{3})(\\+|-)(\\d+)";


	@Parameter(description = "timestamp in millis")
	private List<Long> millisList;

	@Parameter(names = { "-tz", "-timezone" }, description = "<3-letter-timezone><offset> (GMT+2, CET-2, ...)")
	private String timeZone= null;;

	private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";



	@Override
	public void executeCommand(XmppContext context) {

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		log.debug(sdf.getTimeZone().getDisplayName());

		if(timeZone != null) {
			SimpleTimeZone stz = parseTimeZone(timeZone);

			if(stz != null) {
				sdf.setTimeZone(stz);
			}
		}

		for (Long millis : millisList) {
			log.debug(millis+" = "+new SimpleDateFormat(DATE_FORMAT).format(new Date(millis)));
			log.debug(millis+" = "+millis+" = "+sdf.format(new Date(millis)));
			println(millis+" = "+sdf.format(new Date(millis)));
		}
	}


	/**
	 * parses a timezone string into a simpletimezone
	 * @param aTimeZone the timezone string to parse
	 * @return the parsed simpletimezone or null if aTimeZone not matches the {@link #TIMEZONE_PATTERN}
	 */
	protected SimpleTimeZone parseTimeZone(String aTimeZone) {
		try{

			Pattern pattern = Pattern.compile(TIMEZONE_PATTERN);
			Matcher matcher = pattern.matcher(aTimeZone);

			if(matcher.matches()) {

				String id = matcher.group(1).toUpperCase();
				String parseString = (new StringBuilder(matcher.group(2)).append(matcher.group(3)).toString());

				Integer offset = Integer.parseInt(parseString.replace("+", ""));

				SimpleTimeZone stz = new SimpleTimeZone(offset*60*60*1000, id);
				return stz;
			}

			println("invalid timezone format '"+aTimeZone+"'");
			return null;
		}
		catch(Exception e) {
			log.error("parseTimeZone(String) - invalid timezone format '{}'", aTimeZone);
			return null;
		}
	}
}
