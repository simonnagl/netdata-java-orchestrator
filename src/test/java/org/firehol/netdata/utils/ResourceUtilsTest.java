package org.firehol.netdata.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

public class ResourceUtilsTest {

	@Test
	public void testClose() throws InterruptedException, ExecutionException {

		// Static Objects
		Closeable resource = new Closeable() {
			@Override
			public void close() throws IOException {
				// Close with success.
			}
		};

		// Test
		CompletableFuture<Boolean> result = ResourceUtils.close(resource);

		// Verify
		assertTrue(result.get());
	}

	@Test
	public void testCloseFailure() throws InterruptedException, ExecutionException {

		// Static Objects
		Closeable resource = new Closeable() {
			@Override
			public void close() throws IOException {
				throw new IOException("Can not close resource");
			}
		};

		// Test
		CompletableFuture<Boolean> result = ResourceUtils.close(resource);

		// Verify
		assertFalse(result.get());
	}

}
