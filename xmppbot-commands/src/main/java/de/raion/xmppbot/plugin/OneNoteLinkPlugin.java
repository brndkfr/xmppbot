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

import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.filter.MessageBodyMatchesFilter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Listen for messges with OneNote links and in reply post a nicely formatted HTML message with
 * links to web-view and one one-note URL for the link.
 */
@MessageListenerPlugin(name = "onenote-links", description = "Parses messages and provide a good " +
        "linking for OneNote links.")
public class OneNoteLinkPlugin extends AbstractMessageListenerPlugin<OneNoteLinkPlugin> {
    private static Logger log = LoggerFactory.getLogger(OneNoteLinkPlugin.class);
    private MessageBodyMatchesFilter acceptFilter;
    private Pattern pattern;

    /*
     * took it from
	 * http://stackoverflow.com/questions/163360/regular-expresion-to-match-urls-java
	 * and modified it
	 */
    /**
     * (https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])
     */
    private final String regex = "(https?://[-a-zA-Z0-9+&@#/%?=~_|!:," +
            ".;\\{\\}]*[-a-zA-Z0-9+&@#/%=~_|\\{\\}])";

    /**
     * @param aXmppBot reference to the bot
     */
    public OneNoteLinkPlugin(XmppBot aXmppBot) {
        super(aXmppBot);
        acceptFilter = new MessageBodyMatchesFilter(regex);
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    }

    @Override
    public PacketFilter getAcceptFilter() {
        return acceptFilter;
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

    @Override
    public void processMessage(XmppContext xmppContext, Chat chat, Message message) {
        processMessage(xmppContext, message);
    }

    @Override
    public void processMessage(XmppContext xmppContext, MultiUserChat muc, Message message) {
        processMessage(xmppContext, message);
    }

    private void processMessage(XmppContext xmppContext, Message message) {
        Matcher matcher = pattern.matcher(message.getBody());

        while (matcher.find()) {
            String link = matcher.group();
            xmppContext.println(link);
        }
    }

    private Map<String, String> getParams(String oneNoteUrl) {
        URL url;
        try {
            url = new URL(oneNoteUrl);
        } catch (MalformedURLException e) {
            return Collections.emptyMap();
        }

        String query = url.getQuery();
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return query_pairs;
    }

    public String getHtmlString(Message message) {
        Matcher matcher = pattern.matcher(message.getBody());
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        while (matcher.find()) {
            String oneNoteUrl = matcher.group();
            log.debug("oneNoteUrl={}", oneNoteUrl);

            // XXX : hack to know if it's actually a onenote url.
            if(!oneNoteUrl.contains("OneNote.aspx")) {
                return "";
            }

            Map<String, String> params = getParams(oneNoteUrl);

            String[] names = params.get("id").split("/");
            String noteBookName = names[names.length-1];
            boolean hasSection = params.containsKey("wd");
            boolean hasPage = false;
            String sectionName="";
            String pageName="";

            if(hasSection) {

                String target = params.get("wd");
                target = target.substring(target.indexOf("(")+1, target.indexOf(")"));
                String[] crumbs = target.split("\\|");
                hasPage = crumbs.length == 3;
                sectionName = crumbs[0].substring(0, crumbs[0].length()-4);
                if(hasPage) {
                    pageName = crumbs[1].substring(crumbs[1].indexOf("/")+1);
                }
            }

            log.debug("hasSection={}, hasPage={}, noteBookName={}, sectionName={}, pageName={}",
                    new Object[]{hasSection, hasPage, noteBookName, sectionName, pageName});

            if(!first) {
                builder.append("<br/>");
            } else {
                first = false;
            }

            builder.append(noteBookName);
            if(hasSection) {
                builder.append(" - ").append(sectionName);
            }
            if(hasPage) {
                builder.append(" - ").append(pageName);
            }
            builder.append(" (<a href=\"").append(oneNoteUrl).append("\">").append("Web View").append("</a>)");
        }
        return builder.toString();
    }

    /**
     * Gets name of notebook/section/page whatever url is provided
     *
     * @return
     */
    public static String getName(String oneNoteUrl) {
        try {
            oneNoteUrl = URLDecoder.decode(oneNoteUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "onenotelink";
        }

        // Sample OneNote Urls:
        //    Notebook : onenote:https://website.com/path/path/Notebook Name/
        //    Section  : .../Section Name.one#section-id={1231}
        //    Page     : .../Section Name.one#Page Name&section-id={1231}

        if (!oneNoteUrl.contains("#")) {
            // It's a notebook url
            oneNoteUrl = oneNoteUrl.substring(0, oneNoteUrl.length() - 1); // Trim last /
            return oneNoteUrl.substring(oneNoteUrl.lastIndexOf("/"), oneNoteUrl.length());
        } else if (oneNoteUrl.contains("#section-id=")) {
            // It's a section url
            int start = oneNoteUrl.lastIndexOf("/") + 1;
            return oneNoteUrl.substring(start, oneNoteUrl.indexOf(".", start));
        } else {
            // It's a page url
            int start = oneNoteUrl.indexOf("#") + 1;
            return oneNoteUrl.substring(start, oneNoteUrl.indexOf("&", start));
        }
    }

}
