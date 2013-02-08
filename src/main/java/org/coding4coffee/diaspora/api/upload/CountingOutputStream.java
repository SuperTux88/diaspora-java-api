package org.coding4coffee.diaspora.api.upload;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Benjamin Neff
 */
class CountingOutputStream extends FilterOutputStream {

	private final ProgressListener listener;
	private long transferred;

	protected CountingOutputStream(final OutputStream out, final ProgressListener listener) {
		super(out);
		this.listener = listener;
		transferred = 0;
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		out.write(b, off, len);
		transferred += len;
		listener.transferred(transferred);
	}

	@Override
	public void write(final int b) throws IOException {
		out.write(b);
		transferred++;
		listener.transferred(transferred);
	}

}
