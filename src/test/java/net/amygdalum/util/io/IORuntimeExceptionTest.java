package net.amygdalum.util.io;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;

import org.junit.Test;

import net.amygdalum.util.io.IORuntimeException;


public class IORuntimeExceptionTest {

	@Test
	public void testIORuntimeException() throws Exception {
		assertThat(new IORuntimeException(new FileNotFoundException("file")), matchesException(IORuntimeException.class)
			.withCause(matchesException(FileNotFoundException.class).withMessage("file")));
	}

}
