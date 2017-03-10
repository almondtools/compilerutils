package net.amygdalum.util.text;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MappingExceptionTest {

    @Test
    public void testMappingException() throws Exception {
        assertThat(new MappingException(), matchesException(MappingException.class));
    }
    
}
