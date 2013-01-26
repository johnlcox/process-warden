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

	/**
	 * Kills the subprocess. The subprocess represented by this
	 * {@code FinalizedProcess} object is forcibly terminated.
	 */
	public void destroy() {
		process.destroy();
	}

	/**
	 * Returns the exit value for the subprocess.
	 * 
	 * @return the exit value of the subprocess represented by this
	 *         {@code FinalizedProcess} object. By convention, the value
	 *         {@code 0} indicates normal termination.
	 * @throws IllegalThreadStateException
	 *             if the subprocess represented by this
	 *             {@code FinalizedProcess} object has not yet terminated
	 */
	public int exitValue() {
		return process.exitValue();
	}

	/**
	 * Returns the input stream connected to the error output of the subprocess.
	 * The stream obtains data piped from the error output of the process
	 * represented by this {@code FinalizedProcess} object.
	 * 
	 * <p>
	 * If the standard error of the subprocess has been redirected using
	 * {@link FinalizedProcessBuilder#redirectErrorStream(boolean)} then this
	 * method will return a null input stream</a>.
	 * 
	 * <p>
	 * Implementation note: It is a good idea for the returned input stream to
	 * be buffered.
	 * 
	 * @return the input stream connected to the error output of the subprocess
	 */
	public InputStream getErrorStream() {
		return process.getErrorStream();
	}

	/**
	 * Returns the input stream connected to the normal output of the
	 * subprocess. The stream obtains data piped from the standard output of the
	 * process represented by this {@code FinalizedProcess} object.
	 * 
	 * <p>
	 * If the standard error of the subprocess has been redirected using
	 * {@link FinalizedProcessBuilder#redirectErrorStream(boolean)} then the
	 * input stream returned by this method will receive the merged standard
	 * output and the standard error of the subprocess.
	 * 
	 * <p>
	 * Implementation note: It is a good idea for the returned input stream to
	 * be buffered.
	 * 
	 * @return the input stream connected to the normal output of the subprocess
	 */
	public InputStream getInputStream() {
		return process.getInputStream();
	}

	/**
	 * Returns the output stream connected to the normal input of the
	 * subprocess. Output to the stream is piped into the standard input of the
	 * process represented by this {@code FinalizedProcess} object.
	 * 
	 * <p>
	 * Implementation note: It is a good idea for the returned output stream to
	 * be buffered.
	 * 
	 * @return the output stream connected to the normal input of the subprocess
	 */
	public OutputStream getOutputStream() {
		return process.getOutputStream();
	}

	/**
	 * Causes the current thread to wait, if necessary, until the process
	 * represented by this {@code FinalizedProcess} object has terminated. This
	 * method returns immediately if the subprocess has already terminated. If
	 * the subprocess has not yet terminated, the calling thread will be blocked
	 * until the subprocess exits. If the subprocess execution takes longer than
	 * the specified {@code timeoutMilliseconds}, then the blocked thread will
	 * be interrupted.
	 * 
	 * @param timeoutMilliseconds
	 *            time, in milliseconds, to wait on the subprocess blocking
	 *            thread before timing out. (must be greater than 0)
	 * @return the exit value of the subprocess represented by this
	 *         {@code Process} object. By convention, the value {@code 0}
	 *         indicates normal termination.
	 * @throws IllegalArgumentException
	 *             if timeoutMilliseconds is negative or zero.
	 * @throws InterruptedException
	 *             if the subprocess execution times out or the current thread
	 *             is interrupted by another thread while it is waiting.
	 */
	public int waitFor(int timeoutMilliseconds) throws InterruptedException {
		if (timeoutMilliseconds <= 0) {
			throw new IllegalArgumentException("timeoutMilliseconds: <= 0");
		}

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
