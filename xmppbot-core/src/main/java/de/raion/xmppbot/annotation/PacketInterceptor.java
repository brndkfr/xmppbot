package de.raion.xmppbot.annotation;
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.raion.xmppbot.AbstractPacketInterceptor;

/**
 * Defines that the Class is implementing an PacketInterceptor for a specific Service.
 * If a SubClass of AbstractPacketInterceptor is annotated with @PacketInterceptor
 * it will automatically registered to the XMPPConnection with the specified Service
 * on start up
 * @see Service
 * @see AbstractPacketInterceptor
 * @see PacketInterceptor
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketInterceptor {

	@SuppressWarnings("javadoc")
	String service();
}
