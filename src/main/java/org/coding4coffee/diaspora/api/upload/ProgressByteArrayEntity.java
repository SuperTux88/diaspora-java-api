package org.coding4coffee.diaspora.api.upload;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.ByteArrayEntity;

/**
 * @author Benjamin Neff
 */
public class ProgressByteArrayEntity extends ByteArrayEntity {

	private final ProgressListener listener;

	public ProgressByteArrayEntity(final byte[] b, final ProgressListener listener) {
		super(b);
		this.listener = listener;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, listener));
	}
}
