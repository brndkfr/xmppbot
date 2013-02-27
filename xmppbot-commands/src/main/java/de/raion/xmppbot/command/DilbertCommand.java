package de.raion.xmppbot.command;
/*
 * #%L
 * XmppBot Commands
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


import java.io.IOException;
import java.util.Random;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.core.AbstractXmppCommand;

/**
 * posts dilbert strips (default: strip of the day)<br><br>
 * <pre>
 * {@code
 *		Usage: dilbert [options]
 *		Options:
 *   	-r, --random   posts a random strip
 *                     Default: false
 * }                 
 * @author b.kiefer
 *
 */
@CLICommand(name="dilbert", description="posts dilbert strips (default: strip of the day)")
public class DilbertCommand extends AbstractXmppCommand {

	private static final String BASE_URL = "http://www.dilbert.com";

	private static final String RANDOM_BASE_URL = "http://www.dilbert.com/strips/comic/";

	private static Logger log = LoggerFactory.getLogger(DilbertCommand.class);


	@Parameter(names = { "-r", "--random" }, description = "posts a random strip")
    private boolean random = false;




	@Override
	public void executeCommand(XmppContext context) {

		String postUrl = null;

		if(random) {
			postUrl = getImageUrl(createRandomUrl());
		} else {
			postUrl = getImageUrl(BASE_URL);
		}

		if(postUrl != null) {
			println(postUrl);
		}
	}


	private String getImageUrl(String url) {
		try {

			Client client = new Client();
			ClientResponse response = client.resource(url).get(ClientResponse.class);

			if(response.getClientResponseStatus() == Status.OK) {

				Document doc = Jsoup.parse(response.getEntityInputStream(), "UTF-8", BASE_URL);

				Element imageDiv = doc.select("div.STR_Image").first();
				Element imageSrc = imageDiv.select("img[src]").first();

				return imageSrc.absUrl("src");
			}

			println("couldn't fetch random url, sry!");
			return null;

		} catch (IOException e) {
			log.error("getRandomUrl()", e);
			println("couldn't fetch random url, sry!");
			return null;
		}
	}


	private String createRandomUrl() {

		 DateTime minDate = new DateTime(2006, 1, 1, 0, 0 ,0);
		 DateTime maxDate = new DateTime(System.currentTimeMillis());

		 int randomYear  = getRandomInRange(minDate.getYear(), maxDate.getYear());
		 int randomMonth = getRandomInRange(1, 12);
		 int randomDay   = getRandomInRange(1,getDaysInMonth(randomYear, randomMonth));

		 DateTime randomDate = new DateTime(randomYear, randomMonth, randomDay, 0, 0, 0);
		 DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");


		 StringBuilder builder = new StringBuilder(RANDOM_BASE_URL);
		 builder.append(formatter.print(randomDate));
		 return builder.toString();
	}


	private  int getDaysInMonth(int year, int month)
    {
        Chronology chrono = ISOChronology.getInstance();
        DateTimeField dayField = chrono.dayOfMonth();
        LocalDate monthDate = new LocalDate(year, month, 1);
        return dayField.getMaximumValue(monthDate);
    }


	private int getRandomInRange(int min, int max) {
		Random r = new Random(System.currentTimeMillis());
		return r.nextInt(max - min + 1) + min;
	}
}
