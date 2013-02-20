package de.raion.xmppbot.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.TrelloConfig;
import de.raion.xmppbot.command.TrelloConfig.TrelloCard;
import de.raion.xmppbot.filter.MessageBodyMatchesFilter;


@MessageListenerPlugin(name="trello-cards", description="provides summary and link to trello-cards when mentioned in chat")
public class TrelloPlugin  extends AbstractMessageListenerPlugin<TrelloPlugin> {

	private static Logger log = LoggerFactory.getLogger(TrelloPlugin.class);
	
	private MessageBodyMatchesFilter acceptFilter;
	private TrelloConfig config;
	private Pattern pattern;

	public TrelloPlugin(XmppBot aXmppBot) {
		super(aXmppBot);
	 		
		acceptFilter = new MessageBodyMatchesFilter(""); // correct initialization in init
		init();
	}

	@Override
	public PacketFilter getAcceptFilter() {
		return acceptFilter;
	}

	@Override
	public void processMessage(XmppContext xmppContext, Chat chat, Message message) {
		processMessage(xmppContext, message);
	}

	@Override
	public void processMessage(XmppContext xmppContext, MultiUserChat muc,	Message message) {
		processMessage(xmppContext, message);
	}

	
	public boolean matches(String aString) {
		if(pattern != null)
			return pattern.matcher(aString).find();
		return Boolean.FALSE;
	}
	
	

	public java.util.List<String> getMatches(String body) {
		ArrayList<String> matches = new ArrayList<String>();
		
		Matcher matcher = pattern.matcher(body);

		while(matcher.find()) {
			matches.add(matcher.group());
		}
		
		return matches;
	}

	private void processMessage(XmppContext xmppContext, Message message) {
		
		Matcher matcher = pattern.matcher(message.getBody());

		while(matcher.find()) {

			String cardIdShort = matcher.group().substring(1);

			try {

				Map<String, TrelloCard> map = getMatchingCards(cardIdShort);
				
				Set<String> set = map.keySet();
				
				for(String boardName : set) {
					
					TrelloCard card = map.get(boardName);
					
					StringBuilder builder = new StringBuilder();
					builder.append("#").append(card.getShortId()).append(" - ");
					builder.append(card.getName()).append(" : ");
					builder.append(card.getShortUrl()).append("\n");

					xmppContext.println(builder.toString());
				}
			 
			} catch (Exception e) {
				log.error("processMessage(XmppContext, Message) - {}", e.getMessage());
			}
		}
		
	}

	private void init() {
	
		config  = getContext().loadConfig(TrelloConfig.class);
	
		if(config == null)
			return;
			
		String regex = config.getMatchingPattern();
	
		if(regex != null) {
			acceptFilter.setPattern(regex);
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			log.info("using pattern '{}' for matching", regex);
		}
	}

	private HashMap<String, TrelloCard> getMatchingCards(String cardIdShort) {
		HashMap<String, TrelloCard> map = new HashMap<String, TrelloCard>(); 
		
		Set<String> boardIdSet = config.getCards().keySet();
		
		for (String boardId : boardIdSet) {
			String name = config.getBoards().get(boardId);
			if(config.getCards().get(boardId).containsKey(cardIdShort))
				map.put(name, config.getCards().get(boardId).get(cardIdShort));
		}
	
		return map;
	}
}
