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



/**
 * @author b.kiefer
 * @see https://trello.com/docs/gettingstarted/index.html#getting-an-application-key
 * @see https://trello.com/1/appKey/generate
 * @see https://trello.com/docs/gettingstarted/authorize.html
 * 
 */
public class TrelloConfig {

	private String applicationKey;
	
	private String authorizeUrl = "https://trello.com/1/authorize";

	private String accessToken;
		
	
	public String getApplicationKey() {
		return applicationKey;
	}

	public void setApplicationKey(String applicationKey) {
		this.applicationKey = applicationKey;
	}

	public String getAuthorizeUrl() {
		return authorizeUrl;
	}

	public void setAuthorizeUrl(String authorizeUrl) {
		this.authorizeUrl = authorizeUrl;
	}

	public void setAccessToken(String token) {
		accessToken = token;
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	
}
