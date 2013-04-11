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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object that consumes an {@link InputStream} on a daemon thread to prevent the stream from blocking.
 * 
 * <p>
 * The stream can optionally be output to a logger at an INFO level. The gobbler thread will run until the stream is
 * empty or until this {@code StreamGobbler} is closed.
 * 
 * @author John Leacox
 * 
 */
public class StreamGobbler implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(StreamGobbler.class);

	private final GobblerThread gobblerThread;
	private final InputStream inputStream;

	/**
	 * 
	 * @param inputStream
	 *            The {@link InputStream} to gobble (cannot be null)
	 * @param enableLogging
	 *            Whether to log the stream or not
	 * @throws NullPointerException
	 *             if inputStream is null
	 */
	public StreamGobbler(InputStream inputStream, boolean enableLogging) {
		if (inputStream == null) {
			throw new NullPointerException("inputStream: null");
		}

		this.inputStream = inputStream;
		this.gobblerThread = new GobblerThread(inputStream, enableLogging);
	}

	/**
	 * Starts gobbling the input stream.
	 */
	public void gobble() {
		gobblerThread.start();
	}

	static class GobblerThread extends Thread {
		private final InputStream inputStream;
		private final boolean isLoggingEnabled;

		GobblerThread(InputStream inputStream, boolean enableLogging) {
			this.inputStream = inputStream;
			this.isLoggingEnabled = enableLogging;

			setName("StreamGobbler");
			setDaemon(true);
		}

		@Override
		public void run() {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			try {
				while (!Thread.currentThread().isInterrupted() && (line = br.readLine()) != null) {
					if (isLoggingEnabled) {
						logger.info(line);
					}
				}
			} catch (IOException e) {
				if (isLoggingEnabled) {
					logger.error("Failed to gobble stream", e);
				}
			}
		}
	}

	@Override
	public void close() throws IOException {
		gobblerThread.interrupt();
		inputStream.close();
	}
}
