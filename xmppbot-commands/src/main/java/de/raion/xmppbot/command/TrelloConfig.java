package de.raion.xmppbot.command;


/**
 * @author b.kiefer
 * @see https://trello.com/docs/gettingstarted/authorize.html
 */
public class TrelloConfig {

	private String applicationKey;
	
	private String authorizeUrl = "https://trello.com/1/authorize";
	
	private String applicationSecret;

	private String applicationName = "Enbot Botson";

	private String accessToken;

	private String acessTokenSecret;
	
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

	public String getApplicationSecret() {
		return applicationSecret;
	}

	public void setApplicationSecret(String applicationSecret) {
		this.applicationSecret = applicationSecret;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setAccessToken(String token) {
		accessToken = token;
		
	}

	public void setAcessTokenSecret(String tokenSecret) {
		acessTokenSecret = tokenSecret;
		
	}
}
