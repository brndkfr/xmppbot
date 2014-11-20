package de.raion.xmppbot.plugin;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.TfsConfig;
import de.raion.xmppbot.filter.MessageBodyMatchesFilter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Listen for messages with OneNote links and in reply post a nicely formatted HTML message with
 * links to web-view and one one-note URL for the link.
 *
 * @author Sandeep <ruhil@github>
 */
@MessageListenerPlugin(name = "tfs-issue", description = "Parses messages and provide details " +
        "about posted TFS issues.")
public class TfsIssuePlugin extends AbstractMessageListenerPlugin<TfsIssuePlugin> {
    private static Logger log = LoggerFactory.getLogger(TfsIssuePlugin.class);
    private Client client;
    private MessageBodyMatchesFilter acceptFilter;
    private Pattern pattern;
    private TfsConfig config;
    private JsonNode issueNode;
    private ObjectMapper mapper;

    /**
     * @param aXmppBot reference to the bot
     */
    public TfsIssuePlugin(XmppBot aXmppBot) {
        super(aXmppBot);
        mapper = new ObjectMapper();
        init();
    }

    /**
     * Instantiates config related stuff.
     */
    private void init() {
        config = getContext().loadConfig(TfsConfig.class);
        acceptFilter = new MessageBodyMatchesFilter(config.getRegex());
        pattern = Pattern.compile(config.getRegex(), Pattern.CASE_INSENSITIVE);
        client = Client.create();
        HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter(config.getUser(), config.getPassword());
        client.addFilter(authFilter);
    }

    /**
     * reloads configuration
     */
    public void updateConfiguration() {
        init();
    }

    @Override
    public PacketFilter getAcceptFilter() {
        return acceptFilter;
    }

    /**
     * checks if the given string matches the configured pattern
     * @param aString string to test
     * @return true/false
     */
    public boolean matches(String aString) {
        if(pattern != null)
            return pattern.matcher(aString).find();
        return Boolean.FALSE;
    }

    @Override
    public void processMessage(XmppContext xmppContext, Chat chat, Message message) {
        processMessage(xmppContext, message);
    }

    @Override
    public void processMessage(XmppContext xmppContext, MultiUserChat muc, Message message) {
        processMessage(xmppContext, message);
    }

    public JsonNode getIssueNode() {
        return issueNode;
    }

    public TfsConfig getConfig() {
        return config;
    }

    private void processMessage(XmppContext xmppContext, Message message) {

        Matcher matcher = pattern.matcher(message.getBody());

        while(matcher.find()) {

            String issue = matcher.group(2);
            log.debug("issue: {}", issue);

            try {

                URI issueUri = config.getIssueURI(issue);
                log.debug("issueURI={}", issueUri.toString());

                ClientResponse response = client.resource(issueUri).get(ClientResponse.class);

                if(response.getClientResponseStatus() == Status.OK) {
                    issueNode = mapper.readValue(response.getEntityInputStream(), JsonNode.class);
                    String issueSummary = issueNode.findValue("__wrappedArray").get(0).findValue
                            ("fields").findValue("1").textValue();

                    StringBuilder builder = new StringBuilder();
                    builder.append("[TFS-").append(issue).append("] - ");
                    builder.append(issueSummary).append(" : ");
                    builder.append(config.getIssueBrowseURI(issue).toString())
                            .append("\n");

                    log.debug(builder.toString());

                    xmppContext.print(builder.toString());
                }
            } catch (Exception e) {
                log.error("processMessage(XmppContext, Message)", e.getMessage());

            }
        }
    }
}
