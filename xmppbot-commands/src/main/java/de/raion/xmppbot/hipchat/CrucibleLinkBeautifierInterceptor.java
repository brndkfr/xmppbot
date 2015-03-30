package de.raion.xmppbot.hipchat;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import de.raion.xmppbot.AbstractPacketInterceptor;
import de.raion.xmppbot.annotation.PacketInterceptor;
import de.raion.xmppbot.plugin.CruciblePlugin;
import de.raion.xmppbot.plugin.PluginManager;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@PacketInterceptor(service = "hipchat")
public class CrucibleLinkBeautifierInterceptor extends AbstractPacketInterceptor {

    private Logger log = LoggerFactory.getLogger(CrucibleLinkBeautifierInterceptor.class);

    private HipChatAPIConfig apiConfig;

    private Client client;

    public CrucibleLinkBeautifierInterceptor() {
        client = Client.create();
        client.addFilter(new LoggingFilter());
    }


    @Override
    public void interceptPacket(Packet packet) {
        // packetfilter is set to MessageType
        Message xmppMessage = (Message) packet;
        PluginManager pluginManager = getContext().getPluginManager();

        if(apiConfig == null) {
            apiConfig = getContext().loadConfig(HipChatAPIConfig.class);

            String authToken = apiConfig.getAuthenticationToken();

            if(authToken == null || authToken.equals("")) {
                log.warn("no authToken configured for Hipchat, please update your hipchatapiconfig.json");
                return;
            }

        }

        if(pluginManager.isEnabled(CruciblePlugin.class) ) {
            CruciblePlugin plugin = getContext().getPluginManager().get(CruciblePlugin.class);

            if(plugin.matches(xmppMessage.getBody())) {

                JsonNode searchNode = plugin.getCurrentQuery();


                if(searchNode != null) {

                    String text = searchNode.findValue("value").textValue();
                    String href = searchNode.findValue("href").textValue();
                    CruciblePlugin.Category category = CruciblePlugin.Category.valueOf(searchNode.findValue("category").textValue());

                    //String messageText = createMessageText(xmppMessage, issue, plugin);

                    // todo do better
                    // todo do better
                    String roomId = getRoomId();

                    int index = roomId.indexOf("_");
                    if(index != -1)
                        roomId = roomId.substring(index+1);

                    // nickname used by the bot for the configuration 'hipchat'
                    String nickName = getContext().getBot().getNickName(getClass().getAnnotation(PacketInterceptor.class).service());

                    String color = "purple";


                    WebResource resource = client.resource("https://api.hipchat.com/v1/rooms/message")
                            .queryParam("room_id", roomId)
                            .queryParam("from", nickName)
                            .queryParam("message", "dudi")
                            .queryParam("message_format", "html")
                            .queryParam("color", color)
                            .queryParam("auth_token", apiConfig.getAuthenticationToken());

                    ClientResponse response = resource.post(ClientResponse.class);

                    if(response.getClientResponseStatus() == ClientResponse.Status.OK) {
                        log.info("sent message for issue [{}] to room {}", text, roomId );

                        // this is a hack :(
                        xmppMessage.setBody(null);
                        throw new IllegalArgumentException("CrucibleLinkBeautifier: " +
                                "preventing message sending via xmpp. message already sent via hipchat web api");
                    }
                    else {
                        log.warn("sending message for {} failed, status = {}", "["+text+"] to "+roomId, response.getStatus());
                        log.warn(response.getEntity(String.class));
                    }
                }
            }
        }
    }

    /**
     * @return the filter for packets the interceptor is willing to accept
     */
    @Override
    public PacketFilter getPacketFilter() {
        return new PacketTypeFilter(Message.class);
    }

    private String getRoomId() {
        if(getContext().isMultiUserChatBound())
            return getContext().getMultiUserChatKey(getContext().getMultiUserChat());

        return null;
    }
}
