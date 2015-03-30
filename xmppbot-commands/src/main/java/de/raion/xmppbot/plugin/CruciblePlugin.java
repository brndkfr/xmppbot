package de.raion.xmppbot.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.CrucibleConfig;
import de.raion.xmppbot.filter.MessageBodyMatchesFilter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@MessageListenerPlugin(name = "crucible", description = "Parses messages and provide details " +
        "about posted commit hashes and reviews.")
public class CruciblePlugin extends AbstractMessageListenerPlugin<CruciblePlugin> {

    private static Logger log = LoggerFactory.getLogger(CruciblePlugin.class);

    private ObjectMapper mapper;

    private MessageBodyMatchesFilter acceptFilter;

    private Pattern pattern;

    private CrucibleConfig config;

    private Client client;

    private JsonNode searchNode;


    /**
     * @param aXmppBot reference to the bot
     */
    public CruciblePlugin(XmppBot aXmppBot) {
        super(aXmppBot);

        mapper = new ObjectMapper();
        client = Client.create();
        acceptFilter = new MessageBodyMatchesFilter(""); // correct initialization in init
        init();
    }

    private void init() {

        config  = getContext().loadConfig(CrucibleConfig.class);

        if(config == null)
            return;

        String user = config.getAuthenticationUser();
        String pwd = config.getAuthenticationPassword();

        if(user != null && pwd != null) {
            HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter(user, pwd);
            client.addFilter(authFilter);
            log.info("credentials for basic authentication added");
        }

        if(log.isDebugEnabled()) {
            client.addFilter(new LoggingFilter());
            log.info("loggingfilter added for http requests");
        }

        String regex = config.getMatchingPattern();

        if(regex != null) {
            acceptFilter.setPattern(regex);
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            log.info("using pattern '{}' for matching", regex);
        }
    }

    private void processMessage(XmppContext xmppContext, Message message) {

        Matcher matcher = pattern.matcher(message.getBody());

        while(matcher.find()) {

            String query = matcher.group();
            matcher.group("dudi");
            log.debug("search for: {}", query);

            try {
                ClientResponse response = client.resource(config.getQueryPath()).
                        queryParam("q", query).get(ClientResponse.class);

                if(response.getClientResponseStatus() == ClientResponse.Status.OK) {
                    searchNode = mapper.readValue(response.getEntityInputStream(), JsonNode.class);
                    if(searchNode.size() > 0) {

                       String text = searchNode.findValue("value").textValue();
                       String href = searchNode.findValue("href").textValue();
                       Category category = Category.valueOf(searchNode.findValue("category").textValue());

                        StringBuilder builder = new StringBuilder();
                        if(category == Category.Reviews) {
                            builder.append("[").append(query).append("] - ");
                            builder.append(text).append(" : ");
                        }
                        String url = config.link(href).toString();
                        //url = url.replace("?", "\u003F");
                        builder.append(url).append("\n");

                        log.debug(builder.toString());

                        xmppContext.println(builder.toString());
                    }
                }
            } catch (Exception e) {
                log.error("processMessage(XmppContext, Message)", e.getMessage());
            }
        }
    }

    /**
     * @return the filter which defines what messages the listener consumes
     */
    @Override
    public PacketFilter getAcceptFilter() {
        return acceptFilter;
    }

    /**
     * processes a message
     *
     * @param xmppContext context the listener is running
     * @param chat        the chat the message came from
     * @param message
     */
    @Override
    public void processMessage(XmppContext xmppContext, Chat chat, Message message) {
        processMessage(xmppContext, message);

    }

    /**
     * processes a message
     *
     * @param xmppContext context the listener is running
     * @param muc         multiuserchat the message came from
     * @param message
     */
    @Override
    public void processMessage(XmppContext xmppContext, MultiUserChat muc, Message message) {
        processMessage(xmppContext, message);

    }

    /**
     * checks if the given string matches the configured pattern
     * @param aString strint to test
     * @return true/false
     */
    public boolean matches(String aString) {
        if(pattern != null)
            return pattern.matcher(aString).find();
        return Boolean.FALSE;
    }

    public JsonNode getCurrentQuery() {
        return searchNode;
    }

    public enum Category {
        Reviews,
        Changesets,

    }
}
