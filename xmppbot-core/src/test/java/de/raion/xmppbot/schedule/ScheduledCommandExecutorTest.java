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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.joda.time.LocalTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.raion.xmppbot.XmppContext;

@SuppressWarnings("javadoc")
public class ScheduledCommandExecutorTest {

	private static Logger log = LoggerFactory.getLogger(ScheduledCommandExecutorTest.class);


	@Test
	public void shouldInitCorrect() throws Exception {

		// given
		int corePoolSize = 5;
		// when
		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(
				corePoolSize);
		// then
		assertNotNull(executor.getConfig());
		assertNotNull(executor.getSchedule());
		assertEquals(corePoolSize, executor.getCorePoolSize());
	}

	@Test
	public void shouldInitCorrectWithEmptyConfig() throws Exception {

		// given
		int corePoolSize = 5;
		ScheduleConfig config = new ScheduleConfig();
		// when
		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(
				corePoolSize, config);
		// then
		assertNotNull(executor.getConfig());
		assertNotNull(executor.getSchedule());
		assertEquals(corePoolSize, executor.getCorePoolSize());
		assertEquals(config, executor.getConfig());
	}

	@Test
	public void shouldSchedule() throws Exception {

		// given
		ScheduledCommand cmd = new ScheduledCommand(null, "test-cmd", Arrays.asList("p1"));
		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(5);

		long delay = 1L;

		// when
		ScheduledFuture<?> sf = executor.schedule(cmd, delay, TimeUnit.HOURS);

		// then
		assertNotNull(sf);
		assertNotNull(executor.getSchedule().get(cmd.getId()));
		assertNotNull(executor.getConfig().getSchedules().get(cmd.getId()));
		assertEquals(1, executor.getQueue().size());
		assertEquals(cmd, executor.getSchedule().get(cmd.getId()));

		sf.cancel(true);
		executor.shutdownNow();
	}



	@Test
	public void shouldCalculateInitialDelayOnSameDay() throws Exception {

		//given
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		long current = sdf.parse("2012-12-31 12:00:00").getTime();
		log.info(new Date(current).toString());

		int hour = 12;
		int min  = 5;
		LocalTime time = new LocalTime(hour, min);

		int expected = 1000*60*min;

		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(1);

		//when
		long result = executor.calculateInitialDelay(current, time);

		// then
		assertEquals(expected, result);
	}

	@Test
	public void shouldCalculateInitialDelayOnNextDay() throws Exception {

		//given
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		long current = sdf.parse("2012-12-31 12:00:00").getTime();
		log.info(new Date(current).toString());

		int hour = 10;
		int min  = 0;
		LocalTime time = new LocalTime(hour, min);

		int expected = 1000*60*60*22;

		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(1);

		//when
		long result = executor.calculateInitialDelay(current, time);

		// then
		assertEquals(expected, result);

	}

	@Test
	public void shouldRemoveScheduledCommandById() throws Exception {
		// given
		ScheduledCommand cmd = new ScheduledCommand(null, "test-cmd", Arrays.asList("p1"));
		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(5);

		executor.schedule(cmd, 1L, TimeUnit.HOURS);

		// when
		boolean removed = executor.remove(cmd.getId());

		assertTrue(removed);
		assertNull(executor.getSchedule().get(cmd.getId()));
		assertEquals(0, executor.getQueue().size());
	}


	@Test
	public void shouldRemoveScheduledCommandByCommandString() throws Exception {
		// given
		ScheduledCommand cmd = new ScheduledCommand(null, "test-cmd", Arrays.asList("p1"));
		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(5);

		executor.schedule(cmd, 1L, TimeUnit.HOURS);

		// when
		boolean removed = executor.remove(cmd.toCommandString());

		assertTrue(removed);
		assertNull(executor.getSchedule().get(cmd.getId()));
		assertEquals(0, executor.getQueue().size());
	}



	@Test
	public void shouldScheduleAtFixedRate() throws Exception {

		// given
		ScheduledCommand cmd = new ScheduledCommand(null, "test-cmd", Arrays.asList("p1"));
		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(5);

		Long initialDelay = 1000L;
		Long period = 1000L;
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;

		// when
		ScheduledFuture<?> sf = executor.scheduleAtFixedRate(cmd, initialDelay,
				period, timeUnit);

		// then
		assertNotNull(sf);
		assertNotNull(executor.getSchedule().get(cmd.getId()));
		assertNotNull(executor.getConfig().getSchedules().get(cmd.getId()));
		assertEquals(1, executor.getQueue().size());
		assertEquals(cmd, executor.getSchedule().get(cmd.getId()));

		TimeUnit.SECONDS.sleep(5);

		assert (executor.getCompletedTaskCount() > 3);

		sf.cancel(true);
		executor.shutdownNow();
	}

	@Test
	public void shouldNotifyScheduleExecutionListener() throws Exception {

		// given
		XmppContext mockedContext = mock(XmppContext.class);

		ScheduledCommand command = new ScheduledCommand(mockedContext, "echo",
				Arrays.asList("dudidu"));

		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(1);
		log.debug("Executor created");

		executor.addScheduleExecutionListener(mockedContext);
		log.debug("Listener added");

		// when
		executor.schedule(command, 1, TimeUnit.SECONDS);
		log.debug("command scheduled");

		// then
		verify(mockedContext).configurationUpdated(executor.getConfig());

		TimeUnit.SECONDS.sleep(2);

		verify(mockedContext, times(2)).configurationUpdated(executor.getConfig());
	}

	@Test
	public void shouldRemoveScheduleExecutionListener() throws Exception {

		// given
		XmppContext mockedContext = mock(XmppContext.class);

		ScheduledCommandExecutor executor = new ScheduledCommandExecutor(1);
		executor.addScheduleExecutionListener(mockedContext);

		// when / then
		assertTrue(executor.removeScheduleExecutionListener(mockedContext));
	}






}
