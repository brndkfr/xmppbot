package de.raion.xmppbot.command.core;
/*
 * #%L
 * XmppBot Core
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.util.PacketUtils;

/**
 * <pre>
 * {@code
 * Usage: slap [options] nickname
 * Options:
 *   -w, -with   the object to slap with
 * }
 * </pre>
 *
 */
@CLICommand(name = "slap", description = "slaps {nickname} around a bit with a large trout")
public class SlapCommand extends AbstractXmppCommand {

	/** default logger */

	private static Logger log = LoggerFactory.getLogger(SlapCommand.class);

	@Parameter(names = { "-w", "-with" }, description = "the object to slap with")
	String slapWith;

	@Parameter(description = "nickname")
	private List<String> nicknameList = new ArrayList<String>();

	private Random random = new Random(System.currentTimeMillis());

	@Override
	public void executeCommand(XmppContext context) {

		if (context.getMultiUserChat() == null) {
			println("who should i slap in a private chat, you? *slap*");
		}

		SlapConfig config  = context.loadConfig(SlapConfig.class);

		slapWith = checkSlapWith(slapWith, config, context);

		Iterator<String> availableUsers = context.getMultiUserChat().getOccupants();

		String fromNick = PacketUtils.getFromNick(context.getMessage());

		List<String> matchingNickList = findMatchingNicks(nicknameList, availableUsers);

		for (String nick : matchingNickList) {
			StringBuilder builder = new StringBuilder();
			builder.append(fromNick).append(" slaps ");
			builder.append(nick).append(" around a bit with ");
			builder.append(slapWith);

			println(builder.toString());
		}
	}


	private String checkSlapWith(String slap, SlapConfig config, XmppContext context) {

		if(slap == null) {
			return getRandomSlapObject(config);
		}
		if(!config.containsSlapObject(slap)) {
			config.addSlapObject(slap);
			try {
				context.saveConfig(config);
			} catch (IOException e) {
				log.error("checkSlapWith(String, SlapConfig, XmppContext");
			}
		}
		return slap;
	}


	private List<String> findMatchingNicks(List<String> aNickNameList,Iterator<String> availableUsers) {
		ArrayList<String> list = new ArrayList<String>();

		while (availableUsers.hasNext()) {
			String user = availableUsers.next();

			for (String nick : aNickNameList) {
				if (user.toLowerCase().contains(nick.toLowerCase())) {
					list.add(PacketUtils.getNick(user));
					log.info("preparing '{}' for slapping ;)", user);
				} else {
					log.info("user '{}' not available", user);
				}
			}
		}
		return list;
	}


	/**
	 * retrieves a random String from the config
	 * @param config configuration to retrieve the random string
	 * @return random String
	 */
	protected String getRandomSlapObject(SlapConfig config) {
		return config.getSlapObjects().get(random.nextInt(config.getSlapObjects().size()));
	}
}
