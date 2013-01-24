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



/**
 * interface which notifies about executed and finished ScheduleCommands
 *
 */
public interface ScheduleExecutionListener {

	/**
	 * called after the ScheduledCommand is executed and finished via the ScheduledCommandExecutor
	 * @param scheduleConfig the new state of the scheduleConfig
	 */
	public void configurationUpdated(ScheduleConfig scheduleConfig);
}
