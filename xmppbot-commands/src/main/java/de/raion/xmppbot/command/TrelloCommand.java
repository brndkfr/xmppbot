package de.raion.xmppbot.command;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;


import de.raion.xmppbot.XmppContext;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;


@CLICommand(name="trello", description="configures the trello plugin")
public class TrelloCommand extends de.raion.xmppbot.command.core.AbstractXmppCommand {

	private static Logger log = LoggerFactory.getLogger(TrelloCommand.class);
	
	@Parameter(names = { "-k", "--consumer-key" }, description = "sets the application key used for authorization")
	String applicationKey;

	@Parameter(names = { "-s", "--consumer-secret" }, description = "sets the application  secret key used for authorization")
	String applicationSecret;

	@Parameter(names = { "-a", "--auth" }, description = "performs the authorization")
	Boolean authorize = false;
	
	
	@Override
	public void executeCommand(XmppContext context) {
		if(applicationKey != null) {
			setApplicatonKey(applicationKey, context);
			println("application key set");
		}
		if(applicationSecret != null) {
			setApplicationSecret(applicationSecret, context);
			println("application secret set");
			
		}
		if(authorize) {
			doAuthorize(context);
		}
		
	}


	private void setApplicationSecret(String applicationSecret, XmppContext context) {
		TrelloConfig config = context.loadConfig(TrelloConfig.class);
		config.setApplicationSecret(applicationSecret);
		saveConfig(config, context);
		
	}


	private void doAuthorize(final XmppContext context) {
		
		log.debug("start authorization");
		
		final TrelloConfig config = context.loadConfig(TrelloConfig.class);
		
		Client client = Client.create();
		client.addFilter(new LoggingFilter());
		
		//OAuthClientFilter authFilter = new OAu
		// http://www.jarvana.com/jarvana/view/com/sun/jersey/contribs/jersey-oauth/oauth-client/1.9/oauth-client-1.9-javadoc.jar!/index.html
		//client.addFilter()
		
		 OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
				 										  new OAuthParameters().consumerKey(config.getApplicationKey()),
				 										  new OAuthSecrets().consumerSecret(config.getApplicationSecret()),
				 										  config.getAuthorizeUrl(),
				 										  "http://access.token.uri",
				 										  "http://authorization.uri",
				 										  new OAuthClientFilter.AuthHandler() {
															
			 												 public String authorize(URI uri) {
																log.debug("authorize({}", uri.toString());
																return uri.toString();
															 }

															public void authorized(String token, String tokenSecret) {
																config.setAccessToken(token);
																config.setAcessTokenSecret(tokenSecret);
																try {
																	context.saveConfig(config);
																} catch (IOException e) {
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																}
															}
														  });
		
		 //client.addFilter(filter);
		 WebResource resource = client.resource(config.getAuthorizeUrl())
				 					  .queryParam("callback_method", "fragment")
				 					  .queryParam("scope", "read")
				 					  .queryParam("expiration", "never")
				 					  .queryParam("name", "Enbot Botson")
				 					  .queryParam("key", config.getApplicationKey());
				
		 // https://trello.com/docs/api/card/index.html
		 
		 // https://trello.com/1/cards/4ds9DMLl?members=true&member_fields=all
		 
		 
		 ClientResponse response = resource.get(ClientResponse.class);
		 
		 context.println(response.getEntity(String.class));
		 
	}


	private void setApplicatonKey(String aKey, XmppContext context) {
		TrelloConfig config = context.loadConfig(TrelloConfig.class);
		config.setApplicationKey(aKey);
		saveConfig(config, context);
		
	}


	private void saveConfig(TrelloConfig config, XmppContext context) {
		try {
			context.saveConfig(config);
		} catch (IOException e) {
			log.warn("error occured when trying to save trelloconfig.json, message = {}", e.getMessage());
		}
		
	}

}
