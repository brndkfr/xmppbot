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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dharwin.common.tools.cli.api.annotations.CLICommand;

import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import de.raion.xmppbot.XmppContext;
import de.raion.xmppbot.schedule.ScheduledCommand;
import de.raion.xmppbot.schedule.ScheduledCommandExecutor;
import de.raion.xmppbot.schedule.WorkingDayScheduledCommand;

/**
 * <pre>
 * {@code
 * Usage: schedule [options] parameters belonging to the cmd/command
   Options:
    -cmd, -command    the command to execute, see help for list of commands
    -d, -daily        schedules daily at HH:mm, (HH 0-23)
    -l, -list         shows all configured schedules
                      Default: false
    -m, -minutes      schedules once in x minutes
    -r, -remove       deletes the scheduled command with the given id
    -w, -workingday   schedules MO-FR at HH:mm
 *}
 *</pre>
 *
 */
@CLICommand(name = "schedule", description = "schedules another command")
public class ScheduleCommand extends AbstractXmppCommand {

	/** '([0-9]{2}):([0-9]{2})'  */
	protected static final String TIME_FORMAT_PATTERN = "([0-9]{2}):([0-9]{2})";

	private static Logger log = LoggerFactory.getLogger(AbstractXmppCommand.class);

	@Parameter(names = { "-d", "-daily" }, required = false, description = "schedules daily at HH:mm, (HH 0-23)")
	String daily;

	@Parameter(names = { "-w", "-workingday" }, required = false, description = "schedules MO-FR at HH:mm")
	String workingDay;

	@Parameter(names = { "-m", "-minutes" }, required = false, description = "schedules once in x minutes")
	Integer minutes;

	@Parameter(names = { "-cmd", "-command" }, required = false, description = "the command to execute, see help for list of commands")
	String command;

//	@Parameter(names = { "-e", "-every" }, required = false, description = "schedules every x(min)utes |(hour)s| (day)s (1m, 2h, 3d")
//	PeriodParam everyOption;

	@Parameter(names = { "-l", "-list" }, required = false, description = "shows all configured schedules")
	Boolean showSchedules = false;

	@Parameter(names = { "-r", "-remove" }, required = false, description = "deletes the scheduled command with the given id")
	List<String> idsToRemoveList;

	@Parameter(description = "parameters belonging to the cmd/command")
	List<String> commandParamList;

	@Parameter(names = "-muc", description="name of the multiuserchat to execute the command to", hidden=true)
	String multiUserChatName;

	@Parameter(names = "-chat", description="name of the chat to execute the command to", hidden=true)
	String chatName;

	@Parameter(names = "-timestamp", description="timestamp when the command was received/parsed", hidden=true, converter = ISODateTimeConverter.class)
	Date creationDate = new Date(System.currentTimeMillis());


	@Override
	public void executeCommand(XmppContext context) {

		if (showSchedules) {
			printSchedules(context);
		} else if (idsToRemoveList != null) {
			removeFromSchedule(context);
		} else {
			schedule(context);
		}
	}


	private void removeFromSchedule(XmppContext context) {

		ScheduledCommandExecutor scheduleExecutor = context.getScheduler();

		List<Integer> idList = convertToIntegerList(idsToRemoveList);

		for (Integer id : idList) {
			if (scheduleExecutor.isCommandScheduled(id)) {
				boolean removed = scheduleExecutor.remove(id);

				if (removed) {
					println("command " + id + " removed from schedule");
					log.info("command " + id + " removed from schedule");
				}
			}
		}
	}


	private List<Integer> convertToIntegerList(List<String> aList) {

		List<Integer> list = new ArrayList<Integer>(aList.size());

		for (String sid : aList) {
			try {
				list.add(new Integer(sid));
			} catch (NumberFormatException e) {
				println(sid + " is not a valid id");
			}
		}
		return list;
	}


	private void printSchedules(XmppContext context) {

		ScheduledCommandExecutor scheduleExecutor = context.getScheduler();
		List<ScheduledCommand> list = scheduleExecutor.scheduledCommands();

		for (ScheduledCommand cmd : list) {
			println(cmd.toString());
		}
	}


	private void schedule(XmppContext context) {
		ScheduledCommandExecutor scheduleExecutor = context.getScheduler();

		if (isCommandAvailable(context) && scheduleTimeIsAvailable()) {

			if (minutes != null) {
				scheduleIn(context, scheduleExecutor, minutes, TimeUnit.MINUTES);
			}
			else if (daily != null) {
				scheduleDaily(context, scheduleExecutor);
			}
			else if (workingDay != null) {
				scheduleWorkingDay(context, scheduleExecutor);
			}
		}
	}


	/**
	 * checks if the given command is expired by adding the timeInMillisToAdd to
	 * the {@link #creationDate} and compares it to to current system time
	 * @param timeInMillisToAdd the millis to add to {@link #creationDate}
	 * @return true if expired, otherwise false
	 */
	private boolean expired(long timeInMillisToAdd) {

		if((creationDate.getTime()+timeInMillisToAdd) < System.currentTimeMillis()) {
			return true;
		}

		return false;
	}


	private void scheduleIn(XmppContext context, ScheduledCommandExecutor scheduleExecutor,
			int	in, TimeUnit timeUnit) {

		ScheduledCommand scheduledCommand = new ScheduledCommand(context, command, commandParamList);

		scheduledCommand = setParameters(context, scheduledCommand);

		if(!expired(TimeUnit.MILLISECONDS.convert(in, TimeUnit.MINUTES))) {
			scheduleExecutor.schedule(scheduledCommand, in, timeUnit);
			println("scheduled with id [" + scheduledCommand.getId() + "]");
		}
		else {

			String cmdAsString = scheduledCommand.toCommandString();

			log.info("command is expired and will not be added to the scheduler: "+cmdAsString);

			boolean removed = scheduleExecutor.remove(cmdAsString);

			log.info("removed from schedule configuration = {}", removed);
		}
	}


	private void scheduleWorkingDay(XmppContext context, ScheduledCommandExecutor scheduleExecutor) {
		Matcher matcher = Pattern.compile(TIME_FORMAT_PATTERN).matcher(
				workingDay);

		if (matcher.matches()) {
			Integer hour = new Integer(matcher.group(1));
			Integer min = new Integer(matcher.group(2));

			LocalTime time = new LocalTime(hour, min);

			ScheduledCommand scheduledCommand = new WorkingDayScheduledCommand(
					context, command, commandParamList);

			scheduledCommand = setParameters(context, scheduledCommand);

			scheduleExecutor.scheduleAtFixedRate(scheduledCommand, time);

			//TODO print information when task is executed etc
//					log.info("Command '{}' will be executed in '{} ms'",
//							command, initialDelay);
			println("scheduled with id [" + scheduledCommand.getId()+ "]");
		}
	}


	private void scheduleDaily(XmppContext context,
			ScheduledCommandExecutor scheduleExecutor) {
		Matcher matcher = Pattern.compile(TIME_FORMAT_PATTERN).matcher(
				daily);

		if (matcher.matches()) {
			Integer hour = new Integer(matcher.group(1));
			Integer min = new Integer(matcher.group(2));

			LocalTime time = new LocalTime(hour, min);

			ScheduledCommand scheduledCommand = new ScheduledCommand(
					context, command, commandParamList);

			scheduledCommand = setParameters(context, scheduledCommand);

			scheduleExecutor.scheduleAtFixedRate(scheduledCommand, time);

			println("scheduled with id [" + scheduledCommand.getId()+ "]");
		}
		else {
			println("invalid value '"+daily+"' for parameter -d -daily");
		}
	}


	private ScheduledCommand setParameters(XmppContext context,
			ScheduledCommand command) {

		// command received from a multiuserchannel
		if (context.getMultiUserChat() != null && (multiUserChatName == null)) {
			command.setMultiUserChatName(context.getMultiUserChat()
					.getRoom());
		} else if (context.getChat() != null && (chatName == null)) {
			command.setParticipant(context.getChat().getParticipant());
		} else if(multiUserChatName != null) {
			command.setMultiUserChatName(multiUserChatName);
		} else if(chatName != null) {
			command.setParticipant(chatName);
		}

		if(daily != null) {
			command.putOption("-daily", daily);
		}
		if(workingDay != null) {
			command.putOption("-workingday", workingDay);
		}
		if(minutes != null) {
			command.putOption("-minutes", minutes.toString());
		}

		command.putOption("-timestamp", ISODateTimeFormat.basicDateTime().print(creationDate.getTime()));

		return command;
	}


	private boolean isCommandAvailable(XmppContext context) {
		if (!context.hasCommand(command)) {
			println("can't schedule, command '" + command + "' does not exist");
			return false;
		}
		return true;
	}


	private boolean scheduleTimeIsAvailable() {
		if (daily != null) {
			return true;
		} else if (workingDay != null) {
			return true;
		} else if (minutes != null) {
			return true;
		}

		println("schedule time is missing, try options -d, -w, -m, -e or --help");
		return false;
	}
}
