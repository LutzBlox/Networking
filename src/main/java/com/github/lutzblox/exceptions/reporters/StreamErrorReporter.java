package com.github.lutzblox.exceptions.reporters;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * An {@code ErrorReporter} that writes errors to an {@code OutputStream}
 * 
 * @author Christopher Lutz
 */
public class StreamErrorReporter extends ErrorReporter implements Closeable,
		Flushable {

	private OutputStream stream;
	private PrintStream printStream;

	/**
	 * Creates a new {@code StreamErrorReporter} that writes to the specified
	 * {@code OutputStream}
	 * 
	 * @param stream
	 *            The stream to write to
	 */
	public StreamErrorReporter(OutputStream stream) {

		this.stream = stream;
		this.printStream = new PrintStream(stream);
	}

	/**
	 * Gets the output stream used to write errors
	 * 
	 * @return The reporting stream
	 */
	public OutputStream getOutputStream() {

		return stream;
	}

	@Override
	protected void report(String toReport) {

		printStream.println(toReport);
	}

	@Override
	public void flush() throws IOException {

		printStream.flush();
	}

	@Override
	public void close() throws IOException {

		printStream.flush();
	}
}