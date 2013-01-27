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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Unit tests for {@link FinalizedProcessBuilder}.
 * 
 * @author John Leacox
 * 
 */
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
}
