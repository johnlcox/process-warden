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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;

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
}
