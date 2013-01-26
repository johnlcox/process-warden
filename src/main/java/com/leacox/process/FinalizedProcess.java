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
 * <p>
 * This wrapper provides some additional functionality around {@code Process}
 * for some of the gotchas and common pitfalls with using {@code Process}.
 * 
 * <ul>
 * 
 * <li>It implements {@link Closeable}. The {@link #close()} method will make
 * sure that all of the processes' streams are closed, and if the
 * {@code keepProcess} flag was not set, the process is destroyed via
 * {@link Process#destroy()}.</li>
 * 
 * <li>It provides the {@link #waitFor(int)} method that invokes
 * {@link Process#waitFor()} with a timeout period. If the process execution
 * takes longer than the timeout, then the thread is interrupted. This method
 * also makes sure that the thread interrupt flag is cleared</li>
 * 
 * </ul
 * 
 * @author John Leacox
 * @see Process
 * @see FinalizedProcessBuilder
 * @see ProcessBuilder
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

	public void destroy() {
		process.destroy();
	}

	public int exitValue() {
		return process.exitValue();
	}

	public InputStream getErrorStream() {
		return process.getErrorStream();
	}

	public InputStream getInputStream() {
		return process.getInputStream();
	}

	public OutputStream getOutputStream() {
		return process.getOutputStream();
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
