package org.coding4coffee.diaspora.api.upload;

/**
 * @author Benjamin Neff
 */
public interface ProgressListener {

	/**
	 * Transferred Callback.
	 * 
	 * @param bytes
	 *            transferred bytes
	 */
	void transferred(long bytes);
}
