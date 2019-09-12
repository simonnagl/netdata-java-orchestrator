// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.module;

import org.firehol.netdata.orchestrator.Collector;

public interface Module extends Collector {

	String getName();
}
