package de.raion.xmppbot.command;
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
import java.util.List;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.command.core.AbstractXmppCommand;
import de.raion.xmppbot.plugin.TfsIssuePlugin;

/**
 * configures the tfs-issue plugin<br><br>
 * <pre>
 * {@code
Usage: tfs [options]
Options:
-a, --auth     sets the authentication credentials, -a <usr> <pwd>
-h, --tfsHome  sets home url for TFS e.g. http://tfsserver:8080/tfs/PROJECT/
-u, --update   updates the configuration
Default: false
 * }
 * @author Sandeep <ruhil@github>
 * @see TfsIssuePlugin
 *
 */
@CLICommand(name="tfs", description="configures the tfs-issue plugin")
public class TfsCommand extends AbstractXmppCommand {

    private static Logger log = LoggerFactory.getLogger(TfsCommand.class);

    @Parameter(names = { "-h", "--tfsHome" }, description = "sets home url for TFS e.g. http://tfsserver:8080/tfs/PROJECT/")
    String tfsHome;

    @Parameter(names = { "-a", "--auth" }, description = "sets the authentication credentials, -a <usr> <pwd>")
    List<String> authentication;

    @Override
    public void executeCommand(XmppContext context) {

        TfsConfig config = context.loadConfig(TfsConfig.class);

        // update config
        if(authentication != null) {
            if(authentication.size() != 2) {
                println("invalid number of parameters for -a --auth, use -a <usr> <pwd> or --auth <usr> <pwd>");
            } else {
                config.setUser(authentication.get(0));
                config.setPassword(authentication.get(1));
            }
        }
        if(tfsHome != null) {
            config.setTfsHome(tfsHome);
        }

        // Save new config and update the plugin.
        try {
            context.saveConfig(config);
            TfsIssuePlugin plugin = context.getPluginManager().get(TfsIssuePlugin.class);
            plugin.updateConfiguration();
            println("Config updated for TFS plugin.");
        } catch (IOException e) {
            log.error("executeCommand(XmppContext", e);
            println("Error occurred, couldn't update config.");
        }
    }

}
