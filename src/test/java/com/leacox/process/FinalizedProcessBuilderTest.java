/*
 * Copyright 2013 John Leacox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.leacox.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link FinalizedProcessBuilder}.
 * 
 * @author John Leacox
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FinalizedProcessBuilder.class, ProcessBuilder.class, StreamGobbler.class })
public class FinalizedProcessBuilderTest {
	@Test(expected = NullPointerException.class)
	public void testListConstructorThrowsNullPointerExceptionForNullCommand() {
		new FinalizedProcessBuilder((List<String>) null);
	}

	@Test
	public void testListConstructor() {
		List<String> command = Arrays.asList("myCommand", "myArgs");
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder(command);

		assertEquals(command, pb.command());
	}

	@Test
	public void testVarArgConstructor() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand", "myArg");

		List<String> command = pb.command();
		assertEquals(2, command.size());
		assertEquals("myCommand", command.get(0));
		assertEquals("myArg", command.get(1));
	}

	@Test
	public void testListCommandThrowsNullPointerExceptionForNullCommand() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		try {
			pb.command((List<String>) null);
			fail("Expected NullPointerException");
		} catch (NullPointerException e) {
		}
	}

	@Test
	public void testListCommand() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.command(Arrays.asList("myCommand", "myArg1", "myArg2"));

		List<String> command = pb.command();
		assertEquals(3, command.size());
		assertEquals("myCommand", command.get(0));
		assertEquals("myArg1", command.get(1));
		assertEquals("myArg2", command.get(2));
	}

	@Test
	public void testVarArgsCommand() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.command("myCommand", "myArg1", "myArg2");

		List<String> command = pb.command();
		assertEquals(3, command.size());
		assertEquals("myCommand", command.get(0));
		assertEquals("myArg1", command.get(1));
		assertEquals("myArg2", command.get(2));
	}

	@Test
	public void testDirectoryDefaultIsNull() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		assertNull(pb.directory());
	}

	@Test
	public void testDirectoryCustom() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		File cwd = new File("/");
		pb.directory(cwd);

		assertEquals(cwd, pb.directory());
	}

	@Test
	public void testEnvironmentDefault() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		Map<String, String> env = pb.environment();

		assertEquals(System.getenv(), env);
	}

	@Test
	public void testRedirectErrorStreamDefaultIsFalse() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();

		assertFalse(pb.redirectErrorStream());
	}

	@Test
	public void testRedirectErrorStreamSetTrue() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.redirectErrorStream(true);

		assertTrue(pb.redirectErrorStream());
	}

	@Test
	public void testKeepProcessDefaultIsFalse() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();

		assertFalse(pb.keepProcess());
	}

	@Test
	public void testKeepProcessSetTrue() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.keepProcess(true);

		assertTrue(pb.keepProcess());
	}

	@Test
	public void testGobbleInputStreamDefaultIsFalse() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();

		assertFalse(pb.gobbleInputStream());
	}

	@Test
	public void testGobbleInputStream() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.gobbleInputStream(true);

		assertTrue(pb.gobbleInputStream());
	}

	@Test
	public void testGobbleInputStreamWithLoggingDefaultIsFalse() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();

		assertFalse(pb.gobbleInputStreamWithLogging());
	}

	@Test
	public void testGobbleInputStreamWithLogging() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.gobbleInputStreamWithLogging(true);

		assertTrue(pb.gobbleInputStreamWithLogging());
	}

	@Test
	public void testGobbleErrorStreamDefaultIsFalse() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();

		assertFalse(pb.gobbleErrorStream());
	}

	@Test
	public void testGobbleErrorStream() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.gobbleErrorStream(true);

		assertTrue(pb.gobbleErrorStream());
	}

	@Test
	public void testGobbleErrorStreamWithLoggingDefaultIsFalse() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();

		assertFalse(pb.gobbleErrorStreamWithLogging());
	}

	@Test
	public void testGobbleErrorStreamWithLogging() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.gobbleErrorStreamWithLogging(true);

		assertTrue(pb.gobbleErrorStreamWithLogging());
	}

	@Test
	public void testGobbleStreamsDefaultIsFalse() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();

		assertFalse(pb.gobbleStreams());
	}

	@Test
	public void testGobbleStreams() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.gobbleStreams(true);

		assertTrue(pb.gobbleStreams());
	}

	@Test
	public void testGobbleStreamsWithLoggingDefaultIsFalse() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();

		assertFalse(pb.gobbleStreamsWithLogging());
	}

	@Test
	public void testGobbleStreamsWithLogging() {
		FinalizedProcessBuilder pb = new FinalizedProcessBuilder();
		pb.gobbleStreamsWithLogging(true);

		assertTrue(pb.gobbleStreamsWithLogging());
	}

	@Test
	public void testStartWithNoGobbling() throws Exception {
		Process mockProcess = mock(Process.class);

		ProcessBuilder mockProcessBuilder = PowerMockito.mock(ProcessBuilder.class);
		when(mockProcessBuilder.start()).thenReturn(mockProcess);

		PowerMockito.whenNew(ProcessBuilder.class).withArguments("myCommand").thenReturn(mockProcessBuilder);

		PowerMockito.whenNew(StreamGobbler.class).withAnyArguments()
				.thenThrow(new RuntimeException("StreamGobbler should not have been instantiated"));

		FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand");
		FinalizedProcess process = pb.start();
		process.close();
	}

	@Test
	public void testStartWithInputGobbling() throws Exception {
		InputStream inputStream = mock(InputStream.class);

		Process mockProcess = mock(Process.class);
		when(mockProcess.getInputStream()).thenReturn(inputStream);

		ProcessBuilder mockProcessBuilder = PowerMockito.mock(ProcessBuilder.class);
		when(mockProcessBuilder.start()).thenReturn(mockProcess);

		PowerMockito.whenNew(ProcessBuilder.class).withArguments("myCommand").thenReturn(mockProcessBuilder);

		StreamGobbler inputGobbler = mock(StreamGobbler.class);
		PowerMockito.whenNew(StreamGobbler.class).withArguments(inputStream, false).thenReturn(inputGobbler);

		FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand");
		pb.gobbleInputStream(true);
		FinalizedProcess process = pb.start();
		process.close();

		verify(inputGobbler).gobble();
	}

	@Test
	public void testStartWithInputGobblingWithLogging() throws Exception {
		InputStream inputStream = mock(InputStream.class);

		Process mockProcess = mock(Process.class);
		when(mockProcess.getInputStream()).thenReturn(inputStream);

		ProcessBuilder mockProcessBuilder = PowerMockito.mock(ProcessBuilder.class);
		when(mockProcessBuilder.start()).thenReturn(mockProcess);

		PowerMockito.whenNew(ProcessBuilder.class).withArguments("myCommand").thenReturn(mockProcessBuilder);

		StreamGobbler inputGobbler = mock(StreamGobbler.class);
		PowerMockito.whenNew(StreamGobbler.class).withArguments(inputStream, true).thenReturn(inputGobbler);

		FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand");
		pb.gobbleInputStreamWithLogging(true);
		FinalizedProcess process = pb.start();
		process.close();

		verify(inputGobbler).gobble();
	}

	@Test
	public void testStartWithErrorGobbling() throws Exception {
		InputStream errorStream = mock(InputStream.class);

		Process mockProcess = mock(Process.class);
		when(mockProcess.getErrorStream()).thenReturn(errorStream);

		ProcessBuilder mockProcessBuilder = PowerMockito.mock(ProcessBuilder.class);
		when(mockProcessBuilder.start()).thenReturn(mockProcess);

		PowerMockito.whenNew(ProcessBuilder.class).withArguments("myCommand").thenReturn(mockProcessBuilder);

		StreamGobbler errorGobbler = mock(StreamGobbler.class);
		PowerMockito.whenNew(StreamGobbler.class).withArguments(errorStream, false).thenReturn(errorGobbler);

		FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand");
		pb.gobbleErrorStream(true);
		FinalizedProcess process = pb.start();
		process.close();

		verify(errorGobbler).gobble();
	}

	@Test
	public void testStartWithErrorGobblingWithLogging() throws Exception {
		InputStream errorStream = mock(InputStream.class);

		Process mockProcess = mock(Process.class);
		when(mockProcess.getErrorStream()).thenReturn(errorStream);

		ProcessBuilder mockProcessBuilder = PowerMockito.mock(ProcessBuilder.class);
		when(mockProcessBuilder.start()).thenReturn(mockProcess);

		PowerMockito.whenNew(ProcessBuilder.class).withArguments("myCommand").thenReturn(mockProcessBuilder);

		StreamGobbler errorGobbler = mock(StreamGobbler.class);
		PowerMockito.whenNew(StreamGobbler.class).withArguments(errorStream, true).thenReturn(errorGobbler);

		FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand");
		pb.gobbleErrorStreamWithLogging(true);
		FinalizedProcess process = pb.start();
		process.close();

		verify(errorGobbler).gobble();
	}

	@Test
	public void testStartWithStreamsGobbling() throws Exception {
		InputStream inputStream = mock(InputStream.class);
		InputStream errorStream = mock(InputStream.class);

		Process mockProcess = mock(Process.class);
		when(mockProcess.getInputStream()).thenReturn(inputStream);
		when(mockProcess.getErrorStream()).thenReturn(errorStream);

		ProcessBuilder mockProcessBuilder = PowerMockito.mock(ProcessBuilder.class);
		when(mockProcessBuilder.start()).thenReturn(mockProcess);

		PowerMockito.whenNew(ProcessBuilder.class).withArguments("myCommand").thenReturn(mockProcessBuilder);

		StreamGobbler inputGobbler = mock(StreamGobbler.class);
		PowerMockito.whenNew(StreamGobbler.class).withArguments(inputStream, false).thenReturn(inputGobbler);

		StreamGobbler errorGobbler = mock(StreamGobbler.class);
		PowerMockito.whenNew(StreamGobbler.class).withArguments(errorStream, false).thenReturn(errorGobbler);

		FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand");
		pb.gobbleStreams(true);
		FinalizedProcess process = pb.start();
		process.close();

		verify(inputGobbler).gobble();
		verify(errorGobbler).gobble();
	}

	@Test
	public void testStartWithStreamsGobblingWithLogging() throws Exception {
		InputStream inputStream = mock(InputStream.class);
		InputStream errorStream = mock(InputStream.class);

		Process mockProcess = mock(Process.class);
		when(mockProcess.getInputStream()).thenReturn(inputStream);
		when(mockProcess.getErrorStream()).thenReturn(errorStream);

		ProcessBuilder mockProcessBuilder = PowerMockito.mock(ProcessBuilder.class);
		when(mockProcessBuilder.start()).thenReturn(mockProcess);

		PowerMockito.whenNew(ProcessBuilder.class).withArguments("myCommand").thenReturn(mockProcessBuilder);

		StreamGobbler inputGobbler = mock(StreamGobbler.class);
		PowerMockito.whenNew(StreamGobbler.class).withArguments(inputStream, true).thenReturn(inputGobbler);

		StreamGobbler errorGobbler = mock(StreamGobbler.class);
		PowerMockito.whenNew(StreamGobbler.class).withArguments(errorStream, true).thenReturn(errorGobbler);

		FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand");
		pb.gobbleStreamsWithLogging(true);
		FinalizedProcess process = pb.start();
		process.close();

		verify(inputGobbler).gobble();
		verify(errorGobbler).gobble();
	}
}
