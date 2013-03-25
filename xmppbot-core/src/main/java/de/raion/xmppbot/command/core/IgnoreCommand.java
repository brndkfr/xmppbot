package de.raion.xmppbot.command.core;


import com.beust.jcommander.Parameter;
import de.raion.xmppbot.XmppBot;
import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.config.XmppConfiguration;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@CLICommand(name = "ignore", description = "ignores messages whose from-header matches a token in the ignore list")
public class IgnoreCommand extends AbstractXmppCommand{

    private static Logger log = LoggerFactory.getLogger(IgnoreCommand.class);

    @Parameter(description = "tokens to ignore, messages whose from headers contains the token will be ignored")
    private List<String> tokenList;


    /**
     * will be called for command execution with given context
     *
     * @param context the command should use during execution
     */
    @Override
    public void executeCommand(XmppContext context) {
       if(context.isMultiUserChatBound() && (tokenList.size() > 0)) {
           XmppConfiguration config = context.getConnectionConfiguration(context.getConnectionKey(context.getMultiUserChat()));
           Set<String> ignoreSet = config.getIgnoreMessagesFrom();
           ignoreSet.addAll(tokenList);
           XmppBot bot = context.getBot();
           bot.ignoreMessagesFrom(tokenList);
           log.info(tokenList.toString());
           try {
               context.saveConfig(context.getBot().getConfiguration(), context.getBot().getConfigFileName());
               println(tokenList.toString()+" added to ignore list");
           } catch (IOException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           }


       }
    }
}
