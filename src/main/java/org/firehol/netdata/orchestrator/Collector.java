// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.orchestrator;

import java.util.Collection;

import org.firehol.netdata.exception.InitializationException;
import org.firehol.netdata.model.Chart;

public interface Collector {

	Collection<Chart> initialize() throws InitializationException;

	Collection<Chart> collectValues();

	void cleanup();

}
