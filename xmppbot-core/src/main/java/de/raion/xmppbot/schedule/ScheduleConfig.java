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


import java.util.HashMap;

/**
 * holds the configuration of all scheduled commands
 *
 */
public class ScheduleConfig {

	private HashMap<Integer, String> schedules = new HashMap<Integer, String>();

	
	/**
	 * @return a map with the configured schedules mapped by command id
	 */
	public HashMap<Integer, String> getSchedules() {
		return schedules;
	}

	public boolean put(int id, String commandString) {
		
		if(!schedules.containsValue(commandString)) {
			schedules.put(id, commandString);
			return true;
		}
		
		return false;
	}

	public String remove(int id) {
		return schedules.remove(id);
	}



	
}
