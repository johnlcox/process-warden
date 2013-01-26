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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.omg.CORBA.portable.OutputStream;

/**
 * Unit tests for {@link FinalizedProcess}.
 * 
 * @author John Leacox
 * 
 */
public class FinalizedProcessTest {
	@SuppressWarnings("resource")
	@Test(expected = NullPointerException.class)
	public void testConstructorThrowsNullPointerExceptionForNullProcess() {
		new FinalizedProcess((Process) null, false);
	}

	@Test
	public void testDestroy() throws IOException {
		Process mockProcess = mock(Process.class);
		@SuppressWarnings("resource")
		FinalizedProcess fp = new FinalizedProcess(mockProcess, true);
		fp.destroy();

		verify(mockProcess).destroy();
	}

	@Test
	public void testExitValue() throws IOException, InterruptedException {
		FinalizedProcessBuilder fpb = new FinalizedProcessBuilder("java", "-version");

		FinalizedProcess fp = fpb.start();
		try {
			assertEquals(0, fp.waitFor(2000));
			assertEquals(0, fp.exitValue());
		} finally {
			fp.close();
		}
	}

	@Test
	public void testExitValueAbnormalTermination() throws IOException, InterruptedException {
		FinalizedProcessBuilder fpb = new FinalizedProcessBuilder("/bin/sh", "-c", "exit 42");

		FinalizedProcess fp = fpb.start();
		try {
			assertEquals(42, fp.waitFor(2000));
			assertEquals(42, fp.exitValue());
		} finally {
			fp.close();
		}
	}

	@Test
	public void testGetInputStream() {
		Process mockProcess = mock(Process.class);
		@SuppressWarnings("resource")
		FinalizedProcess fp = new FinalizedProcess(mockProcess, true);
		fp.getInputStream();

		verify(mockProcess).getInputStream();
	}

	@Test
	public void testGetErrorStream() {
		Process mockProcess = mock(Process.class);
		@SuppressWarnings("resource")
		FinalizedProcess fp = new FinalizedProcess(mockProcess, true);
		fp.getErrorStream();

		verify(mockProcess).getErrorStream();
	}

	@Test
	public void testGetOutputStream() {
		Process mockProcess = mock(Process.class);
		@SuppressWarnings("resource")
		FinalizedProcess fp = new FinalizedProcess(mockProcess, true);
		fp.getOutputStream();

		verify(mockProcess).getOutputStream();
	}

	@Test
	public void testWaitForThrowsIllegalargumentExceptionForNegativeTimeout() throws IOException, InterruptedException {
		FinalizedProcessBuilder fpb = new FinalizedProcessBuilder("/bin/sh", "-c", "exit 0");

		FinalizedProcess fp = fpb.start();
		try {
			fp.waitFor(-1000);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		} finally {
			fp.close();
		}
	}

	@Test
	public void testWaitForThrowsIllegalargumentExceptionForZeroTimeout() throws IOException, InterruptedException {
		FinalizedProcessBuilder fpb = new FinalizedProcessBuilder("/bin/sh", "-c", "exit 0");

		FinalizedProcess fp = fpb.start();
		try {
			fp.waitFor(0);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		} finally {
			fp.close();
		}
	}

	@Test
	public void waitFor() throws IOException, InterruptedException {
		FinalizedProcessBuilder fpb = new FinalizedProcessBuilder("/bin/sh", "-c", "exit 0");

		FinalizedProcess fp = fpb.start();
		try {
			assertEquals(0, fp.waitFor(1000));
		} finally {
			fp.close();
		}
	}

	@Test
	public void waitForTimeout() throws IOException {
		FinalizedProcessBuilder fpb = new FinalizedProcessBuilder("/bin/sh");

		FinalizedProcess fp = fpb.start();
		try {
			assertEquals(0, fp.waitFor(100));
			fail("Expected InterruptedException");
		} catch (InterruptedException e) {
		} finally {
			fp.close();
		}
	}

	@Test
	public void waitForTimeoutClearsInterruptedFlag() throws IOException {
		FinalizedProcessBuilder fpb = new FinalizedProcessBuilder("/bin/sh");

		FinalizedProcess fp = fpb.start();
		try {
			assertEquals(0, fp.waitFor(100));
			fail("Expected InterruptedException");
		} catch (InterruptedException e) {
		} finally {
			fp.close();
		}

		assertFalse(Thread.interrupted());
	}

	@Test
	public void testCloseClosesStreamsAndDestroysProcess() throws IOException, InterruptedException {
		InputStream mockInputStream = mock(InputStream.class);
		InputStream mockErrorStream = mock(InputStream.class);
		OutputStream mockOutputStream = mock(OutputStream.class);

		Process mockProcess = mock(Process.class);
		when(mockProcess.getInputStream()).thenReturn(mockInputStream);
		when(mockProcess.getErrorStream()).thenReturn(mockErrorStream);
		when(mockProcess.getOutputStream()).thenReturn(mockOutputStream);

		FinalizedProcess fp = new FinalizedProcess(mockProcess, false);
		fp.close();

		verify(mockInputStream).close();
		verify(mockErrorStream).close();
		verify(mockOutputStream).close();
		verify(mockProcess).destroy();
	}

	@Test
	public void testCloseDoesNotDestroyProcessWithKeepProcessFlag() throws IOException, InterruptedException {
		InputStream mockInputStream = mock(InputStream.class);
		InputStream mockErrorStream = mock(InputStream.class);
		OutputStream mockOutputStream = mock(OutputStream.class);

		Process mockProcess = mock(Process.class);
		when(mockProcess.getInputStream()).thenReturn(mockInputStream);
		when(mockProcess.getErrorStream()).thenReturn(mockErrorStream);
		when(mockProcess.getOutputStream()).thenReturn(mockOutputStream);

		FinalizedProcess fp = new FinalizedProcess(mockProcess, true);
		fp.close();

		verify(mockInputStream).close();
		verify(mockErrorStream).close();
		verify(mockOutputStream).close();
		verify(mockProcess, never()).destroy();
	}
}
