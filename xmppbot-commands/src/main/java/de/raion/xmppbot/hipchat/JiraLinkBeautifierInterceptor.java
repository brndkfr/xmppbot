package de.raion.xmppbot.hipchat;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

import de.raion.xmppbot.AbstractPacketInterceptor;
import de.raion.xmppbot.annotation.PacketInterceptor;
import de.raion.xmppbot.plugin.JiraIssuePlugin;
import de.raion.xmppbot.plugin.PluginManager;

/**
 * @author b.kiefer
 *
 */
@PacketInterceptor(service = "hipchat")
public class JiraLinkBeautifierInterceptor extends AbstractPacketInterceptor {

	private static Logger log = LoggerFactory.getLogger(JiraLinkBeautifierInterceptor.class);
	
	@Override
	public void interceptPacket(Packet packet) {
		
		// packetfilter is set to MessageType
		Message message = (Message) packet;
		PluginManager pluginManager = getContext().getPluginManager();
		
		if(pluginManager.isEnabled(JiraIssuePlugin.class)) {
			JiraIssuePlugin plugin = getContext().getPluginManager().get(JiraIssuePlugin.class);
			
			if(plugin.matches(message.getBody())) {
				
				JsonNode issue = plugin.getCurrentIssue();
				
				if(issue != null) {
					String issueKey = issue.findValue("key").textValue();
					String issueSummary = issue.findValue("summary").textValue();
					
					String issueIconUrl = issue.path("fields").path("issuetype").get("iconUrl").asText();
					String issueType    = issue.path("fields").path("issuetype").get("name").asText();
					String priorityUrl  = issue.path("fields").path("priority").get("iconUrl").asText();
					String priority		= issue.path("fields").path("priority").get("name").asText();	
					String statusUrl    = issue.path("fields").path("status").get("iconUrl").asText();
					String status		= issue.path("fields").path("status").get("name").asText();	
					
					String line = message.getBody();
					int index = line.indexOf("http");
					String issueUrl = line.substring(index).trim().replace("\n", "");
					
					
					StringBuilder builder = new StringBuilder();
					
					
					builder.append(createImageTag(issueIconUrl, issueType));
					builder.append(createAnchorTag(" ["+issueKey+"] ", issueUrl));
					builder.append(createImageTag(priorityUrl, priority));
					builder.append(" ").append(issueSummary);
					builder.append(createImageTag(statusUrl, status));
					
					
					
					Client client = Client.create();
					client.addFilter(new LoggingFilter());
					WebResource resource = client.resource("https://api.hipchat.com/v1/rooms/message")
							                      .queryParam("room_id", "")
							                      .queryParam("from", "Enbot Botson")
							                      .queryParam("message", builder.toString())
							                      .queryParam("message_format", "html")
							                      .queryParam("color", "green")
							                      .queryParam("auth_token", "");
					
					resource.post();
					message.setBody(null);
					throw new IllegalArgumentException("preventing message sending");
				}
			}
		}
		
	}

	private String createImageTag(String imageUrl, String alt) {
		StringBuilder builder = new StringBuilder();
		builder.append("<img src=\"").append(imageUrl).append("\" ");
		builder.append("alt=\"").append(alt).append("\">");
		return builder.toString();
		
	}

	private String createAnchorTag(String issue, String link) {
		StringBuilder builder= new StringBuilder();
		builder.append("<a href=\"").append(link).append("\">");
		builder.append(issue).append("</a>");
		
		return builder.toString();
	}

	@Override
	public PacketFilter getPacketFilter() {
		return new PacketTypeFilter(Message.class);
	}

}
