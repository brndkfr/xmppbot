package de.raion.xmppbot.command;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

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
	
	private String authorizeUrl = "https://trello.com/1/authorize";

	private String boardBaseUrl = "https://trello.com/1/boards/";

	private String applicationKey;
		
	private String accessToken;

	private HashMap<String, String> boards  = new HashMap<String, String>();
	
	private HashMap<String, String> boardUrls = new HashMap<String, String>();

	private HashMap<String, HashMap<String, TrelloCard>> cards = new HashMap<String, HashMap<String, TrelloCard>>();

			
	
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

	public String getBoardBaseUrl() {
		return boardBaseUrl;
	}

	public void addBoard(String id, String name) {
		
		if(boards == null)
			boards  = new HashMap<String, String>();
		
		boards.put(id, name);
		
	}

	public void addBoardUrl(String id, String url) {
		
		if(boardUrls == null)
			boardUrls = new HashMap<String, String>();
		
		boardUrls.put(id, url);
		
	}

	public HashMap<String, String> getBoards() {
		return boards;
		
	}

	public void addCards(String id,	HashMap<String, TrelloCard> map) {
		
		if(cards == null)
			 cards = new HashMap<String, HashMap<String, TrelloCard>>();
		
		cards.put(id, map);
	}

	public HashMap<String, String> getBoardUrls() {
		return boardUrls;
	}

	public void setBoardUrls(HashMap<String, String> boardUrls) {
		this.boardUrls = boardUrls;
	}

	
	public HashMap<String, HashMap<String, TrelloCard>> getCards() {
		return cards;
	}

	public void setCards(HashMap<String, HashMap<String, TrelloCard>> cards) {
		this.cards = cards;
	}

	public void setBoardBaseUrl(String boardBaseUrl) {
		this.boardBaseUrl = boardBaseUrl;
	}

	public void setBoards(HashMap<String, String> boards) {
		this.boards = boards;
	}

	
	public static class TrelloCard {
		
		private String shortId;
		private String name;
		private String shortUrl;
		
		public String getShortId() {
			return shortId;
		}
		public void setShortId(String shortId) {
			this.shortId = shortId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getShortUrl() {
			return shortUrl;
		}
		public void setShortUrl(String shortUrl) {
			this.shortUrl = shortUrl;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("TrelloCard [");
			if (shortId != null)
				builder.append("shortId=").append(shortId).append(", ");
			if (name != null)
				builder.append("name=").append(name).append(", ");
			if (shortUrl != null)
				builder.append("shortUrl=").append(shortUrl);
			builder.append("]");
			return builder.toString();
		}
	}
}
