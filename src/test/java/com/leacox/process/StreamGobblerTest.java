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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.leacox.process.StreamGobbler.GobblerThread;

/**
 * Unit tests for {@link StreamGobbler}.
 * 
 * @author John Leacox
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ StreamGobbler.class, GobblerThread.class })
public class StreamGobblerTest {
	@SuppressWarnings("resource")
	@Test(expected = NullPointerException.class)
	public void testConstructorThrowsNullPointerExceptionForNullInputStream() {
		new StreamGobbler((InputStream) null, false);
	}

	@Test
	public void testClose() throws IOException {
		InputStream inputStream = mock(InputStream.class);
		StreamGobbler gobbler = new StreamGobbler(inputStream, false);
		gobbler.close();

		verify(inputStream).close();
	}

	@Test
	public void testGobble() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(
				" [ ? ]  acpi-support\n [ ? ]  acpid\n [ ? ]  alsa-restore\n [ ? ]  alsa-store".getBytes("UTF-8"));
		boolean enableLogging = true;

		GobblerThread thread = new GobblerThread(inputStream, enableLogging);
		PowerMockito.whenNew(GobblerThread.class).withArguments(inputStream, enableLogging).thenReturn(thread);

		StreamGobbler gobbler = null;

		gobbler = new StreamGobbler(inputStream, enableLogging);
		gobbler.gobble();
		thread.join();

		gobbler.close();

		// Verify that there is nothing left to read on the input stream
		assertEquals(0, inputStream.available());
	}
}
