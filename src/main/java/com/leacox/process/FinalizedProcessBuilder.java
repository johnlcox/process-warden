package com.leacox.process;

import java.io.File;
import java.io.IOException;

/**
 * This class wraps {@link ProcessBuilder} for creating operating system
 * processes using the safer {@link FinalizedProcess}.
 * 
 * <p>
 * Like {@code ProcessBuilder}, each @{code FinalizedProcessBuilder} instances
 * manages a collection of process attributes. The {@link #start()} method
 * creates a new {@link FinalizedProcess} instance with this attributes. The
 * {@link #start()} method can be invoked multiple times from the same instance
 * to create new subprocesses with identical or related attributes.
 * 
 * <p>
 * Each process builder manages the following attributes, in addition to the
 * attributes from {@link ProcessBuilder}:
 * 
 * 
 * <ul>
 * 
 * <li>a <i>keepProcess</i> indicator, a boolean indicator as to whether the
 * process should be destroyed during cleanup or not. By default the process
 * will be destroyed during cleanup.</li>
 * 
 * </ul>
 * 
 * <p>
 * Starting a new process with the default attributes is just as easy as
 * {@link ProcessBuilder}:
 * 
 * {@code FinalizedProcess process = new ProcessBuilder("myCommand", "myArg").start();}
 * 
 * @author John Leacox
 * 
 */
public class FinalizedProcessBuilder {
	private final ProcessBuilder processBuilder;

	private boolean keepProcess = false;

	public FinalizedProcessBuilder(String... command) {
		this.processBuilder = new ProcessBuilder(command);
	}

	public FinalizedProcessBuilder redirectErrorStream(boolean redirectErrorStream) {
		processBuilder.redirectErrorStream(redirectErrorStream);
		return this;
	}

	public FinalizedProcessBuilder directory(File file) {
		processBuilder.directory(file);
		return this;
	}

	public FinalizedProcessBuilder inheritIO() {
		processBuilder.inheritIO();
		return this;
	}

	/**
	 * Sets the process builder to keep the process when it is cleaned up via
	 * the {@code FinalizedProcess#close()} method.
	 * <p>
	 * 
	 * By default when the {@code FinalizedProcess} is closed the
	 * {@link Process#destroy()} method will be called. Calling this method
	 * indicates that the process should not be destroyed when closed.
	 * 
	 * @return this process builder
	 */
	public FinalizedProcessBuilder keepProcess() {
		keepProcess = true;
		return this;
	}

	public FinalizedProcess start() throws IOException {
		Process process = processBuilder.start();
		return new FinalizedProcess(process, keepProcess);
	}

}
