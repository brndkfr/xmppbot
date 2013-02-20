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

/**
 * the Configuration for the SlapCommand
 * @see SlapCommand
 *
 */
public class SlapConfig {

	protected ArrayList<String> slapObjects = new ArrayList<String>();
	
	public ArrayList<String> getSlapObjects() {
		return slapObjects;
	}

	

	
	public SlapConfig() {
		slapObjects.add("a large trout");
	}
	
	
	
	public void addSlapObject(String slapObject) {
		if(!slapObjects.contains(slapObject))
				slapObjects.add(slapObject);
	}

	public boolean containsSlapObject(String aSlapObject) {
		return slapObjects.contains(aSlapObject);
	}
}
