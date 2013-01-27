package com.leacox.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

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
}
