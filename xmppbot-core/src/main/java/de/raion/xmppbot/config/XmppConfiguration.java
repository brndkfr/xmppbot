package de.raion.xmppbot.config;
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
import java.util.Map;



/**
 * contains the configuration for the bot
 *
 */
public class XmppConfiguration {

	private String serviceType = "default";

	private String host;

	private int port = 5222;

	private String serviceName;

	private String jabberId;

	private String password;


	private String nickName;



	private Map<String, String> multiUserChats = new HashMap<String,String>();

	private Map<String, String> chats = new HashMap<String,String>();

	//private List<MessageFilter> multiUserChatFilters = new ArrayList<MessageFilter>();




	


	/**
	 * jabberId getter method.
	 * @return the jabberId
	 */
	public String getJabberId() {
		return jabberId;
	}

	/**
	 * jabberId getter method.
	 * @return the jabberId
	 */
	public void setJabberId(String jabberId) {
		this.jabberId = jabberId;
	}

	/**
	 * nickName getter method.
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * nickName getter method.
	 * @return the nickName
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * host getter method.
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * host getter method.
	 * @return the host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * port getter method.
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * port getter method.
	 * @return the port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * serviceName getter method.
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * serviceName getter method.
	 * @return the serviceName
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * multiUserChats getter method.
	 * @return the multiUserChats
	 */
	public Map<String, String> getMultiUserChats() {
		return multiUserChats;
	}

	public void addMultiUserChat(String key, String channelName) {
		this.multiUserChats.put(key, channelName);
	}

	/**
	 * multiUserChats getter method.
	 * @return the multiUserChats
	 */
	public void setMultiUserChats(Map<String, String> multiUserChats) {
		this.multiUserChats = multiUserChats;
	}

	/**
	 * chats getter method.
	 * @return the chats
	 */
	public Map<String, String> getChats() {
		return chats;
	}

	/**
	 * chats getter method.
	 * @return the chats
	 */
	public void setChats(Map<String, String> chats) {
		this.chats = chats;
	}


	public void setPassword(String aPwd) {
		this.password = aPwd;

	}


	@Override
	public String toString() {
		return "XmppConfiguration [type=" + serviceType + ", host=" + host + ", port=" + port
				+ ", serviceName=" + serviceName + ", jabberId=" + jabberId + ", password= *****"
				+ ", nickName=" + nickName + ", multiUserChats=" + multiUserChats
				+ ", chats=" + chats + "]";
	}

	/**
	 * password getter method.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	public String getServiceType() {
		return serviceType.toLowerCase();
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}


//
//	/**
//	 * filter getter method.
//	 * @return the filter
//	 */
//	public List<MessageFilter> getMultiUserChatFilter() {
//		return multiUserChatFilters;
//	}
//
//	/**
//	 * filter getter method.
//	 * @return the filter
//	 */
//	public void setMultiUserChatFilter(List<MessageFilter> filter) {
//		this.multiUserChatFilters = filter;
//	}
//
//	public void addMultiUserChatFilter(MessageFilter filter) {
//		this.multiUserChatFilters.add(filter);
//
//	}




}
