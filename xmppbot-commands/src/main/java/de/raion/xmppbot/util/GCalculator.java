package de.raion.xmppbot.util;
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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

/**
 * @author bkiefer
 *
 */
public class GCalculator {
private static final String GOOGLE_CALCULATOR_URL = "http://www.google.com/ig/calculator?hl=en";
// static variables
/** default logger */
private static Logger log = LoggerFactory.getLogger(GCalculator.class);

private Client client;
private String baseUrl;


public GCalculator() {
	this.client = Client.create();
	this.baseUrl = GOOGLE_CALCULATOR_URL;
}

public String convert(String from, String fromUnit, String toUnit) {

	String requestUrl = createRequestUrl(from, fromUnit, toUnit);

	WebResource request = client.resource(requestUrl);

	ClientResponse response = request.get(ClientResponse.class);
	String responseString = response.getEntity(String.class);

	log.debug(responseString);

	Map<String, String> resultMap = parseResponse(responseString);

	StringBuilder builder = new StringBuilder();
	return builder.append(from).append(" ").append(fromUnit).append(" -> ").append(resultMap.get("rhs")).toString();


}

private  Map<String, String> convertCurrency(Double fromAmount, String fromCurrencyUnit, String toCurrencyUnit) {

	if(log.isDebugEnabled()) {
		client.addFilter(new LoggingFilter());
	}


	String requestUrl = createRequestUrl(fromAmount, fromCurrencyUnit, toCurrencyUnit);

	WebResource request = client.resource(requestUrl);

	ClientResponse response = request.get(ClientResponse.class);
	String responseString = response.getEntity(String.class);

	log.debug(responseString);

	Map<String, String> resultMap = parseResponse(responseString);


	return resultMap;

}

private String createRequestUrl(Object from, String fromUnit, String toUnit) {
	StringBuilder builder = new StringBuilder(this.baseUrl);
	builder.append("&q=").append(from).append(fromUnit.toUpperCase());
	builder.append("=?").append(toUnit.toUpperCase());
	return builder.toString();
}


private static Map<String, String> parseResponse(String responseString) {

	HashMap <String, String> map = new HashMap<String, String>();

	responseString = responseString.replace("{", "").replace("}", "");

	String[] keyValueTokens = responseString.split(",");

	for (String token : keyValueTokens) {
		String[] keyValuePair = token.split(":");

		map.put(keyValuePair[0], keyValuePair[1].replaceAll("\"", ""));

	}
	return map;
}


public static void main(String[] args) throws Exception {

	GCalculator calc = new GCalculator();

	System.out.println(calc.convertCurrency(1.0, "eur", "chf"));
}

}
