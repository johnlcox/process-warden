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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A {@link Closeable} wrapper for {@link Process} for running a native process.
 * 
 * 
 * @author John Leacox
 * 
 */
public class FinalizedProcess implements Closeable {
	private final Process process;
	private final boolean keepProcess;

	FinalizedProcess(Process process, boolean keepProcess) {
		if (process == null) {
			throw new NullPointerException("process: null");
		}

		this.process = process;
		this.keepProcess = keepProcess;
	}

	@Override
	public void close() throws IOException {
		if (process != null) {
			try {
				process.getErrorStream().close();
			} catch (IOException e) {
			}

			try {
				process.getInputStream().close();
			} catch (IOException e) {
			}

			try {
				process.getOutputStream().close();
			} catch (IOException e) {
			}

			if (!keepProcess) {
				process.destroy();
			}
		}
	}

	public OutputStream getOutputStream() {
		return process.getOutputStream();
	}

	public InputStream getInputStream() {
		return process.getInputStream();
	}

	public InputStream getErrorStream() {
		return process.getErrorStream();
	}

	public int waitFor(int timeoutMilliseconds) throws InterruptedException {
		Timer timer = null;
		try {
			timer = new Timer(true);
			InterruptTimerTask interrupter = new InterruptTimerTask(Thread.currentThread());
			timer.schedule(interrupter, timeoutMilliseconds);
			return process.waitFor();
		} finally {
			timer.cancel();
			Thread.interrupted();
		}
	}

	public int exitValue() {
		return process.exitValue();
	}

	public void destroy() {
		process.destroy();
	}

	private static class InterruptTimerTask extends TimerTask {
		private final Thread thread;

		public InterruptTimerTask(Thread t) {
			this.thread = t;
		}

		@Override
		public void run() {
			thread.interrupt();
		}
	}
}
