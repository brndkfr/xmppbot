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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.filter.MessageBodyMatchesFilter;

/**
 * listen to messages which containing links, grabs title and description from the site and post them
 * to the Chat / MultiUserChat the message came frome.
 *
 */
@MessageListenerPlugin(name="httpinfo", description="shows title and description when links are posted (html only)")
public class HttpInfoPlugin extends AbstractMessageListenerPlugin<HttpInfoPlugin> {


	// static variables
	private static Logger log = LoggerFactory.getLogger(HttpInfoPlugin.class);

	 /*
	 * took it from
	 * http://stackoverflow.com/questions/163360/regular-expresion-to-match-urls-java
	 * and modified it
	 */
	/** (https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]) */
	protected String regex = "(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";

	private Pattern pattern = Pattern.compile(regex);


	/**
	 * constructor
	 * @param aXmppBot reference to the bot
	 */
	public HttpInfoPlugin(XmppBot aXmppBot) {
		super(aXmppBot);
	}


	@Override
	public PacketFilter getAcceptFilter() {
		return new MessageBodyMatchesFilter(regex);
	}

	@Override
	public void processMessage(XmppContext xmppContext, Chat chat,	Message message) {
		processMessage(xmppContext, message);
	}

	@Override
	public void processMessage(XmppContext xmppContext, MultiUserChat muc,Message message) {
		processMessage(xmppContext, message);
	}



	private void processMessage(XmppContext xmppContext, Message message) {

		Matcher matcher = pattern.matcher(message.getBody());

		StringBuilder builder = new StringBuilder();
		
		while(matcher.find()) {
			String url = matcher.group();

			try {

				Document doc = Jsoup.connect(url).get();
				String title = doc.select("title").first().text();
				
				builder = new StringBuilder(title);

				
				String meta  = doc.select("meta[name=description]").attr("content");
				//String meta = e.attr("content");

				if(meta != null)
				builder.append(" - ").append(meta);

			} catch (IOException e) {
				log.error("processMessage(XmppContext, Message) - {}", e.getMessage());
			} finally {
				xmppContext.println(builder.toString());
			}
		}
	}
}
