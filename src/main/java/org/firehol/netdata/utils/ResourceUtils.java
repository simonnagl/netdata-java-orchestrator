package org.firehol.netdata.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class ResourceUtils {
	/**
	 * Close a Resource asynchrony.
	 * 
	 * @param resource
	 *            to close
	 * @return Async Boolean which will indicate if the close threw an
	 *         exception.
	 */
	public static CompletableFuture<Boolean> close(Closeable resource) {

		return CompletableFuture.supplyAsync(new Supplier<Boolean>() {

			@Override
			public Boolean get() {
				try {
					resource.close();
					return true;
				} catch (IOException e) {
					return false;
				}
			}
		});
	}
}
