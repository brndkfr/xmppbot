package de.raion.xmppbot.hipchat;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import de.raion.xmppbot.AbstractPacketInterceptor;
import de.raion.xmppbot.annotation.PacketInterceptor;
import de.raion.xmppbot.plugin.JiraIssuePlugin;
import de.raion.xmppbot.plugin.OneNoteLinkPlugin;
import de.raion.xmppbot.plugin.PluginManager;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sandeep <ruhil@github>
 */
@PacketInterceptor(service = "hipchat")
public class OneNoteLinkInterceptor extends AbstractPacketInterceptor {
    private static Logger log = LoggerFactory.getLogger(OneNoteLinkInterceptor.class);
    private HipChatAPIConfig apiConfig;

    private Client client;

    public OneNoteLinkInterceptor() {
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

        if(pluginManager.isEnabled(OneNoteLinkPlugin.class) ) {
            OneNoteLinkPlugin plugin = getContext().getPluginManager().get(OneNoteLinkPlugin.class);

            if(plugin.matches(xmppMessage.getBody())) {

                String messageText = plugin.getHtmlString(xmppMessage);
                if(messageText.equals("")) {
                    // this is a hack :(
                    xmppMessage.setBody(null);
                    throw new IllegalArgumentException("OneNoteLinkBeautifier: preventing message sending via xmpp. message already sent via hipchat web api");
                }

                // todo do better
                String roomId = getRoomId();

                int index = roomId.indexOf("_");
                if(index != -1)
                    roomId = roomId.substring(index+1);

                // nickname used by the bot for the configuration 'hipchat'
                String nickName = getContext().getBot().getNickName(getClass().getAnnotation(PacketInterceptor.class).service());

                WebResource resource = client.resource("https://api.hipchat.com/v1/rooms/message")
                        .queryParam("room_id", roomId)
                        .queryParam("from", nickName)
                        .queryParam("message", messageText)
                        .queryParam("message_format", "html")
                        .queryParam("auth_token", apiConfig.getAuthenticationToken());

                ClientResponse response = resource.post(ClientResponse.class);

                if(response.getClientResponseStatus() == Status.OK) {
                    log.info("sent message for link [{}] to room {}", xmppMessage, roomId );

                    // this is a hack :(
                    xmppMessage.setBody(null);
                    throw new IllegalArgumentException("OneNoteLinkBeautifier: preventing message sending via xmpp. message already sent via hipchat web api");
                }
                else {
                    log.warn("sending message for {} failed, status = {}",
                            "["+xmppMessage+"] to "+roomId, response.getStatus());
                    log.warn(response.getEntity(String.class));
                }
            }
        }
    }


    private String getRoomId() {
        if(getContext().isMultiUserChatBound())
            return getContext().getMultiUserChatKey(getContext().getMultiUserChat());

        return null;
    }

    @Override
    public PacketFilter getPacketFilter() {
        return new PacketTypeFilter(Message.class);
    }
}
