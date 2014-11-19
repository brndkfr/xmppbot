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
import de.raion.xmppbot.command.TfsConfig;
import de.raion.xmppbot.plugin.PluginManager;
import de.raion.xmppbot.plugin.TfsIssuePlugin;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

/**
 * @author Sandeep <ruhil@github>
 */
@PacketInterceptor(service = "hipchat")
public class TfsLinkBeautifierInterceptor extends AbstractPacketInterceptor {
    private static Logger log = LoggerFactory.getLogger(TfsLinkBeautifierInterceptor.class);
    private HipChatAPIConfig apiConfig;

    private Client client;

    public TfsLinkBeautifierInterceptor() {
        client = Client.create();
        client.addFilter(new LoggingFilter());
    }

    @Override
    public void interceptPacket(Packet packet) {

        // PacketFilter is set to MessageType
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

        if(pluginManager.isEnabled(TfsIssuePlugin.class) ) {
            TfsIssuePlugin plugin = getContext().getPluginManager().get(TfsIssuePlugin.class);

            if(plugin.matches(xmppMessage.getBody())) {

                JsonNode issue = plugin.getIssueNode();

                if (issue != null) {

                    String issueKey = issue.findValue("__wrappedArray").get(0).findValue
                            ("fields").findValue("-3").textValue();

                    String messageText;
                    try {
                        messageText = createMessageText(issue, plugin.getConfig());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        messageText = xmppMessage.getBody();
                    }

                    // todo do better
                    String roomId = getRoomId();

                    if (roomId == null) {
                        return;
                    }

                    // nickname used by the bot for the configuration 'hipchat'
                    String nickName = getContext().getBot().getNickName(getClass().getAnnotation(PacketInterceptor.class).service());

                    WebResource resource = client.resource("https://api.hipchat.com/v1/rooms/message")
                            .queryParam("room_id", roomId)
                            .queryParam("from", nickName)
                            .queryParam("message", messageText)
                            .queryParam("message_format", "html")
                            .queryParam("auth_token", apiConfig.getAuthenticationToken());

                    ClientResponse response = resource.post(ClientResponse.class);

                    if (response.getClientResponseStatus() == Status.OK) {
                        log.info("sent message for issue [{}] to room {}", issueKey, roomId);

                        // this is a hack :(
                        xmppMessage.setBody(null);
                        throw new IllegalArgumentException("TfsLinkBeautifier: preventing message sending via xmpp. message already sent via hipchat web api");
                    } else {
                        log.warn("sending message for {} failed, status = {}", "[" + issueKey + "] to " + roomId, response.getStatus());
                        log.warn(response.getEntity(String.class));
                    }
                }
            }
        }
    }

    private String createMessageText(JsonNode issue, TfsConfig config) throws URISyntaxException {
        issue = issue.findValue("__wrappedArray").get(0).findValue("fields");
        String id = issue.findValue("-3").toString();
        String title = issue.findValue("1").textValue();
        String state = issue.findValue("2").textValue();
        String user;
        if(issue.findValue("24") != null) {
            user = issue.findValue("24").textValue();
        } else {
            user="-";
        }
        String url = config.getIssueBrowseURI(id).toString();
        return String.format("<a href=\"%s\">[TFS-%s] %s</a> State: %s, Assignee: %s", url, id,
                title, state, user);
    }


    private String getRoomId() {
        if(getContext().isMultiUserChatBound()) {
            String roomId = getContext().getMultiUserChatKey(getContext().getMultiUserChat());
            int index = roomId.indexOf("_");
            if (index != -1)
                roomId = roomId.substring(index + 1);
            return roomId;
        }

        return null;
    }

    @Override
    public PacketFilter getPacketFilter() {
        return new PacketTypeFilter(Message.class);
    }
}
