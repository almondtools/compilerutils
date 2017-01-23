package net.amygdalum.util.io;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {

	public IORuntimeException(IOException cause) {
		super(cause);
	}

}
