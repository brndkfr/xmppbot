package de.raion.xmppbot.hipchat;
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


import java.io.File;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class HipChatAPIConfig {

	private String authenticationToken;
	
	private URL baseURL;
	
	private String roomCreatePath;
	
	private String roomDeletePath;
	
	private String roomHistoryPath;
	
	private String roomMessagePath;
	
	private String apiVersion;
	

	public String getApiVersion() {
		return apiVersion;
	}


	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}


	public String getAuthenticationToken() { return authenticationToken; }


	public URL getBaseURL() {
		return baseURL;
	}


	public void setBaseURL(URL baseURL) {
		this.baseURL = baseURL;
	}


	public String getRoomCreatePath() {
		return roomCreatePath;
	}


	public void setRoomCreatePath(String roomCreatePath) {
		this.roomCreatePath = roomCreatePath;
	}


	public String getRoomDeletePath() {
		return roomDeletePath;
	}


	public void setRoomDeletePath(String roomDeletePath) {
		this.roomDeletePath = roomDeletePath;
	}


	public String getRoomHistoryPath() {
		return roomHistoryPath;
	}


	public void setRoomHistoryPath(String roomHistoryPath) {
		this.roomHistoryPath = roomHistoryPath;
	}


	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}
	
	public String getRoomMessagePath() {
		return roomMessagePath;
	}


	public void setRoomMessagePath(String roomMessagePath) {
		this.roomMessagePath = roomMessagePath;
	}


	public static void main(String[] args) throws Exception {
		HipChatAPIConfig config = new HipChatAPIConfig();
		config.setAuthenticationToken("b2b1ca1091f0709e60253b76db73ea");
		config.setBaseURL(new URL("https://api.hipchat.com"));
		config.setApiVersion("v1");
		config.setRoomCreatePath("rooms/create");
		config.setRoomDeletePath("rooms/delete");
		config.setRoomHistoryPath("rooms/history");
		
		ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

		String filename = config.getClass().getSimpleName().toLowerCase()+".json";

		writer.writeValue(new File(filename), config);
	}
}
