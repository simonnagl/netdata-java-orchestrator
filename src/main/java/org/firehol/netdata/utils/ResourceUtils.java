// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ResourceUtils {

	private ResourceUtils() {
	}

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
