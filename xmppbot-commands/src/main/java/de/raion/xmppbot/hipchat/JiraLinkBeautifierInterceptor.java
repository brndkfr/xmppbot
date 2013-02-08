package de.raion.xmppbot.hipchat;

import java.net.URLEncoder;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.XHTMLManager;
import org.jivesoftware.smackx.XHTMLText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

import de.raion.xmppbot.AbstractPacketInterceptor;
import de.raion.xmppbot.annotation.PacketInterceptor;
import de.raion.xmppbot.plugin.JiraIssuePlugin;

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
		
		JiraIssuePlugin plugin = getContext().getPluginManager().get(JiraIssuePlugin.class);
		
		if(plugin.matches(message.getBody())) {
			String line = message.getBody();
			int index = line.indexOf("http");
			String link = line.substring(index).trim().replace("\n", "");
			String[] tokens = line.substring(0, index).split(" - ");
			String issue = tokens[0];
			String text = tokens[1];
			
			XHTMLText xhtmlText = new XHTMLText(null, null);
			
			xhtmlText.appendOpenAnchorTag(link, "color:blue");
			xhtmlText.append(issue);
			xhtmlText.appendCloseAnchorTag();
			xhtmlText.append(" - ");
			xhtmlText.append(text);
				    
		      
		      // Add the XHTML text to the message
		     XHTMLManager.addBody(message, xhtmlText.toString());
			
			
			String beautifiedText = beautifyText(issue, link, text);
			
			message.setBody(xhtmlText.toString());
			//message.setBody(beautifiedText);
			//message.setProperty("favoriteColor", "red");
			
			//log.debug(message.toXML());
			
			// b2b1ca1091f0709e60253b76db73ea
			
			
			
			Client client = Client.create();
			client.addFilter(new LoggingFilter());
			WebResource resource = client.resource("https://api.hipchat.com/v1/rooms/message")
					                      .queryParam("room_id", "")
					                      .queryParam("from", "Enbot Botson")
					                      .queryParam("message", beautifiedText)
					                      .queryParam("message_format", "html")
					                      .queryParam("color", "green")
					                      .queryParam("auth_token", "");
			
			ClientResponse response = resource.post(ClientResponse.class);
			
			
			
			
			
			
		}
		
	}

	private String beautifyText(String issue, String link, String text) {
		StringBuilder builder= new StringBuilder();
		builder.append("<a href=\"").append(link).append("\">");
		builder.append(issue).append("</a> - ");
		builder.append(text).append("<br>");
		return builder.toString();
	}

	@Override
	public PacketFilter getPacketFilter() {
		return new PacketTypeFilter(Message.class);
	}

}
