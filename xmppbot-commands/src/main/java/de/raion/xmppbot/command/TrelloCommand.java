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
import java.util.HashMap;
import java.util.Set;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

import de.raion.xmppbot.XmppContext;


@CLICommand(name="trello", description="configures the trello plugin")
public class TrelloCommand extends de.raion.xmppbot.command.core.AbstractXmppCommand {

	private static Logger log = LoggerFactory.getLogger(TrelloCommand.class);
	
	@Parameter(names = { "-k", "--application-key" }, description = "sets the application key used for authorization")
	String applicationKey;
	
	@Parameter(names = { "-t", "--acess-token" }, description = "sets the acess token to use")
	String accessToken;
	
	@Parameter(names = { "-v", "--validate" }, description = "validates the given configuration")
	Boolean validate = false;
	
	@Parameter(names = { "-a", "--auth" }, description = "starts the authorization process")
	Boolean authorize = false;
	
	@Parameter(names = { "-f", "--force" }, description = "BETA!: starts the authorization process")
	Boolean force = false;
	
	@Parameter(names = { "-u", "--update" }, description = "update boards and cards")
	Boolean update = false;
	
	@Override
	public void executeCommand(XmppContext context) {
		
		TrelloConfig config = context.loadConfig(TrelloConfig.class);
		
		if(applicationKey != null) {
			config.setApplicationKey(applicationKey);
			if(context.isMultiUserChatBound())
				println("application key set to xxxxxxxx");
			else
				println("application key set to '"+applicationKey+"'");
		}
		
		
		if(accessToken != null) {
			config.setAccessToken(accessToken);
			println("access-token set");
		}
				
		if(validate) {
			validateConfiguration(context, config);
		}
		
		if(authorize) {
			doAuthorize(context, config, force);
		}
		
		saveConfig(config, context);
	}

	private void doAuthorize(XmppContext context, TrelloConfig config, Boolean force) {
				
		println("following link will ask you to allow me read-only access forever to your organizations and boards");
		println(createAccessTokenRequestLink(config.getAuthorizeUrl(), config.getApplicationKey()));
		println("please use following command:  trello -v -t The64CharTokenProvidedFromTheTrelloPage");
		println("or:  trello --validate --acces-token The64CharactersTokenProvidedFromTheTrelloPage");
		
	}

	private String createAccessTokenRequestLink(String authorizeUrl, String appKey) {
		// key=ed2fc62db0f1351c8126ca06a7c94cb8&name=Enbot+Botson&
		
		//https://trello.com/1/authorize?key=substitutewithyourapplicationkey&name=My+Application&expiration=never&response_type=token
		StringBuilder builder = new StringBuilder(authorizeUrl);
		builder.append("?").append("key=").append(appKey);
		// TODO retrieve name from context/config ....
		builder.append("&").append("name=").append("Enbot+Botson");
		builder.append("&").append("expiration=never");
		builder.append("&").append("response_type=token");
		return builder.toString();
				
	}











	private void validateConfiguration(final XmppContext context, TrelloConfig config) {
		
		println("starting validation");
		
		if(isConfigProper(config)) {
			
			// https://trello.com/1/members/me?key=substitutewithyourapplicationkey&token=substitutethispartwiththeauthorizationtokenthatyougotfromtheuser
			
			Client client = Client.create();
			client.addFilter(new LoggingFilter());
			WebResource resource = client.resource("https://trello.com/1/members/my/boards?boards=open&board_fields=name,shortUrl")
										 .queryParam("key", config.getApplicationKey())
										 .queryParam("token", config.getAccessToken());
			
			println("trying to access board informations");
			ClientResponse response = resource.get(ClientResponse.class);
			
			if(response.getClientResponseStatus() == Status.OK) {
				println("reading board infos");
				
				ObjectMapper mapper = new ObjectMapper();
				try {
						JsonNode rootNode = mapper.readValue(response.getEntityInputStream(), JsonNode.class);
						int size = rootNode.size();
											
						for(int i=0; i<size; i++) {
							JsonNode boardNode = rootNode.get(i);
							String id	= boardNode.path("id").asText();
							String name = boardNode.path("name").asText();
							String url  = boardNode.path("shortUrl").asText();
							boolean closed = Boolean.parseBoolean(boardNode.path("closed").asText());
							
							if(!closed) {
								println(name+" - "+url);
								
								config.addBoard(id, name);
								config.addBoardUrl(id, url);
							}
						}
						
						println("validation done");
						
						Set<String> boardIdSet = config.getBoards().keySet();
						
						for (String id : boardIdSet) {
							config = addCardInformations(id, config, client, mapper);
						}
						saveConfig(config, context);
				
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	private TrelloConfig addCardInformations(String id, TrelloConfig config, Client client, ObjectMapper mapper) {
		
		WebResource boardResource = client.resource(config.getBoardBaseUrl())
				                          .path(id)
				                          .queryParam("key", config.getApplicationKey())
				                          .queryParam("token", config.getAccessToken())
				                          .queryParam("cards", "all");
		
		ClientResponse response = boardResource.get(ClientResponse.class);
		
		if(response.getClientResponseStatus() == Status.OK) {
			
			try {
				JsonNode rootNode = mapper.readValue(response.getEntityInputStream(), JsonNode.class);
				JsonNode cardsNode = rootNode.path("cards");
				
				HashMap<String, TrelloConfig.TrelloCard> map = new HashMap<String, TrelloConfig.TrelloCard>();
							
				int size = cardsNode.size();
				
				
				for(int i=0; i<size; i++) {
					
					JsonNode json = cardsNode.get(i);
					TrelloConfig.TrelloCard card = new TrelloConfig.TrelloCard();
					
					card.setShortId(json.path("idShort").asText());
					card.setShortUrl(json.path("shortUrl").asText());
					card.setName(json.path("name").asText());
					
					map.put(card.getShortId(), card);
				}
			
				config.addCards(id, map);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			log.warn("couldn't GET card informations for board {}, status={}", id, response.getClientResponseStatus().getStatusCode());
		}
		
		return config;
	}

	private boolean isConfigProper(TrelloConfig config) {
		
		boolean isConfigProper = true;
		
		if(config.getApplicationKey() != null)
			println("application-key available");
		else {
			println("application-key missing, see https://trello.com/docs/gettingstarted/index.html#getting-an-application-key");
			isConfigProper = false;
		}
		
		if(config.getAccessToken() != null) {
			println("access-token available");
		} else {
			println("acess-token missing, try: 'trello -a' or 'trello -auth'. type trello --help for more infos");
			isConfigProper = false;
		}
		
		
		return isConfigProper;
	}

	private void saveConfig(TrelloConfig config, XmppContext context) {
		try {
			context.saveConfig(config);
		} catch (IOException e) {
			log.warn("error occured when trying to save trelloconfig.json, message = {}", e.getMessage());
		}
		
	}

}
