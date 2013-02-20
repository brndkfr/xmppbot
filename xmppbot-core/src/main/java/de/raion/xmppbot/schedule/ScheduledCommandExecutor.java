package de.raion.xmppbot.schedule;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 *
 */
public class ScheduledCommandExecutor extends ScheduledThreadPoolExecutor {

	/** default logger */
	private static Logger log = LoggerFactory.getLogger(ScheduledCommandExecutor.class);

	private static final Long DAY_PERIOD = 1000L * 60L * 60L * 24L;

	private HashMap<Integer, ScheduledCommand> scheduledCommandMap;

	private HashMap<RunnableScheduledFuture<?>, ScheduledCommand> scheduleMap;

	private HashMap<ScheduledCommand, RunnableScheduledFuture<?>> scheduledFutureMap;

	private ScheduleConfig scheduleConfig;

	private List<ScheduleExecutionListener> executionListenerList;



	/**
	 *
	 * @param corePoolSize the number of threads to keep in the pool, even they are idle
	 * @throws IllegalArgumentException if corePoolSize < 0
	 */
	public ScheduledCommandExecutor(int corePoolSize) {
		super(corePoolSize);

		scheduledCommandMap = new HashMap<Integer, ScheduledCommand>();
		scheduleMap = new HashMap<RunnableScheduledFuture<?>, ScheduledCommand>();
		scheduledFutureMap = new HashMap<ScheduledCommand, RunnableScheduledFuture<?>>();
		scheduleConfig = new ScheduleConfig();
		executionListenerList = new ArrayList<ScheduleExecutionListener>();
	}

	/**
	 *
	 * @param corePoolSize corePoolSize the number of threads to keep in the pool, even they are idle
	 * @param aConfig the configuration to use
	 * @throws IllegalArgumentException if corePoolSize < 0
	 */
	public ScheduledCommandExecutor(int corePoolSize, ScheduleConfig aConfig) {
		super(corePoolSize);

		scheduledCommandMap = new HashMap<Integer, ScheduledCommand>();
		scheduleMap = new HashMap<RunnableScheduledFuture<?>, ScheduledCommand>();
		scheduledFutureMap = new HashMap<ScheduledCommand, RunnableScheduledFuture<?>>();
		scheduleConfig = aConfig;
		executionListenerList = new ArrayList<ScheduleExecutionListener>();

	}




	/**
	 * Creates and executes a one-shot Command that becomes enabled after the given delay.
	 * @param command the command to execute
	 * @param delay the time from now to delay execution
	 * @param unit the TimeUnit
	 * @return a ScheduledFuture representing pending completion of the task and whose
	 * 			 get() method will return null upon completion
	 * @throws RejectedExecutionException if the task cannot be scheduled for execution
	 * @throws NullPointerException if command is null
	 */
	public ScheduledFuture<?> schedule(ScheduledCommand command, long delay, TimeUnit unit) {

		scheduledCommandMap.put(command.getId(), command);
		scheduleConfig.put(command.getId(), command.toCommandString());
		ScheduledFuture<?> future = super.schedule(command, delay, unit);
		notifyListener();
		return future;

	}

	/**
	 * executes the ScheduledCommand at a certain Time of Day
	 * @param command ScheduledCommand to execute
	 * @param time LocalTime representing Hour of Day and Min of Hour without Timezone
	 */
	public void scheduleAtFixedRate(ScheduledCommand command, LocalTime time) {

		long initialDelay = calculateInitialDelay(System.currentTimeMillis(), time);
		scheduleAtFixedRate(command, initialDelay, DAY_PERIOD, TimeUnit.MILLISECONDS);

		log.debug("scheduledAtFixedRate, command={}, LocalTime={}", command, time);



	}

	/**
	 * @param command the ScheduledCommand to schedule at a fix rate
	 * @param initialDelay the time to delay first execution
	 * @param period the period between sucessive execution
	 * @param unit the TimeUnit to use
	 * @return ScheduledFuture<?>
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(ScheduledCommand command, long initialDelay,
			long period, TimeUnit unit) {

		scheduledCommandMap.put(command.getId(), command);
		scheduleConfig.put(command.getId(), command.toCommandString());
		ScheduledFuture<?> future =  super.scheduleAtFixedRate(command, initialDelay, period, unit);
		notifyListener();
		return future;
	}


	/**
	 * a List of currently scheduled Commands
	 * @return a List of currently scheduled Commands
	 */
	public List<ScheduledCommand> scheduledCommands() {

		ArrayList<ScheduledCommand> list = new ArrayList<ScheduledCommand>();
		list.addAll(scheduledCommandMap.values());
		return list;
	}

	/**
	 * @param id the ID of the Command
	 * @return true if there is a Command with ID id, otherwise false
	 */
	public boolean isCommandScheduled(Integer id) {

		boolean isScheduled = false;

		isScheduled = scheduledCommandMap.containsKey(id);

		if(!isScheduled) {
			isScheduled = scheduledCommandMap.containsKey(id);
		}

		return isScheduled;

	}

	/**
	 * removes the Command with the given ID from the scheduling if available
	 * @param commandId ID of the command to remove
	 * @return true if sucessfully removed, otherwise false
	 */
	public boolean remove(Integer commandId) {

		ScheduledCommand command = null;
		boolean removed = false;

		/* command can be null in the case that the schedueexecuter is
		 * started with a ScheduleConfig containing expired commands
		 * which will be removed via ScheduleCommandExecutor#remove(String cmdAsString).
		 * so there is no ScheduledCommand in the maps but we have to
		 * remove it from the configuration
		 */
		command = scheduledCommandMap.get(commandId);

		if(command != null) {

			RunnableScheduledFuture<?> rsf = scheduledFutureMap.get(command);

			removed = super.remove(rsf);

			if(removed) {
				scheduledFutureMap.remove(command);
				scheduledCommandMap.remove(command.getId());
				scheduleMap.remove(command.getId());
			}
		}

		 // remove expired commands from the configuration
		String cmdAsString = scheduleConfig.remove(commandId);
		if(cmdAsString != null) {
			notifyListener();
			return true;
		}

		return removed;
	}


	/**
	 * removes the Command with the given String representation from the scheduling if available
	 * @param cmdAsString String representation of the command to remove
	 * @return true if sucessfully removed, otherwise false
	 * @see ScheduledCommand#toCommandString()
	 */
	public boolean remove(String cmdAsString) {

		Map<Integer, String> map = scheduleConfig.getSchedules();
		Set<Integer> keySet = map.keySet();

		for (Integer key : keySet) {

			String cmd = map.get(key);

			if(cmd.equalsIgnoreCase(cmdAsString)) {
				return remove(key);
			}
		}
		return false;
	}


	/**
	 * @return ScheduledCommands mapped by their id
	 */
	public HashMap<Integer, ScheduledCommand> getSchedule() { return scheduledCommandMap; }

	/**
	 * @return the configuration of all ScheduledCommands
	 */
	public ScheduleConfig getConfig() {
		return scheduleConfig;
	}

	/**
	 * @param listener the ScheduleExecutionListener to add
	 */
	public void addScheduleExecutionListener(ScheduleExecutionListener listener) {
		executionListenerList.add(listener);
	}

	/**
	 * @param listener the ScheduleExecutionListener to remove
	 * @return if sucessfully removed (listener was registered), otherwise false
	 */
	public boolean removeScheduleExecutionListener(ScheduleExecutionListener listener) {
		return executionListenerList.remove(listener);
	}



	/** maps the submitted ScheduledCommands in a schedule and scheduledFuture map
	 * @see java.util.concurrent.ScheduledThreadPoolExecutor#decorateTask(java.lang.Runnable, java.util.concurrent.RunnableScheduledFuture)
	 */
	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(
	        Runnable runnable, RunnableScheduledFuture<V> task) {

		if(runnable.getClass().isAssignableFrom(ScheduledCommand.class)) {
			scheduleMap.put(task, (ScheduledCommand)runnable);
			scheduledFutureMap.put((ScheduledCommand) runnable, task);
		}
		return task;
	}

	/** removes ScheduledCommand with one time execution from the configuration (ScheduleConfig) and
	 * informs the listers, that the configuration has changed
	 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {

		if(r instanceof RunnableScheduledFuture<?>) {

			RunnableScheduledFuture<?> rsf = (RunnableScheduledFuture<?>) r;

			ScheduledCommand cmd = (scheduleMap.get(rsf));

			if(!rsf.isPeriodic()) {

				if(scheduledCommandMap.containsKey(cmd.getId())) {
					scheduledCommandMap.remove(cmd.getId());
					scheduleConfig.remove(cmd.getId());
					scheduleMap.remove(rsf);

					notifyListener();
				}
				log.info("single execution, removed from schedule "+cmd);
			}
		}
	}

	/**
	 * notifies all registered listeners that the config has changed
	 */
	protected void notifyListener() {
		for(ScheduleExecutionListener listener : executionListenerList) {
			listener.configurationUpdated(scheduleConfig);
		}
	}

	/**
	 * calculates the initial delay out of the given parameters
	 *
	 * @param currentTimeInMillis	time in ms to use for the calculation
	 * @param time LocalTime representing Hour and Minute only
	 * @return the initial delay calculated in ms
	 */
	protected Long calculateInitialDelay(Long currentTimeInMillis,	LocalTime time) {

		DateTime currentTime = new DateTime(currentTimeInMillis);

		Long initialDelay = 0L;

		int currentHour = currentTime.getHourOfDay();
		DateTime initialTime = new DateTime(currentTimeInMillis)
				.withHourOfDay(time.getHourOfDay()).withMinuteOfHour(time.getMinuteOfHour())
				.withSecondOfMinute(0);

		// next day
		if (currentHour > time.getHourOfDay()) {
			initialTime = initialTime.plusDays(1);
		}

		initialDelay = initialTime.getMillis() - currentTime.getMillis();
		return initialDelay;
	}
}
