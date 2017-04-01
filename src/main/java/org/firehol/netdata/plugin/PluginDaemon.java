/*
 * Copyright (C) 2017 Simon Nagl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.firehol.netdata.plugin;

import java.util.logging.Logger;

import org.firehol.netdata.utils.AlignToTimeIntervalService;
import org.firehol.netdata.utils.UnitConversion;

/**
 * Netdata Java Plugin Daemon.
 * 
 * Holds the main Function of the daemon.
 *
 */
public class PluginDaemon
{
	private static final Logger log = Logger.getLogger("org.firehol.netdata.plugin");
	
    public static void main( String[] args )
    {
        log.fine("Parse command line");
        int updateEverySecond = Integer.valueOf(args[0]);
        long updateEveryNSec = updateEverySecond * UnitConversion.NANO_PER_PLAIN;

        PluginHolder pluginHolder = new PluginHolder();
        // TODO: Add Plugins.

        
        pluginHolder.readConfiguration();
        
        pluginHolder.initializeCharts();

        if(pluginHolder.getAllPluginSize() < 1) {
        	log.severe("No Java Plugins avaiable. Disabling Java Plugin Daemon.");
        	Printer.disable();
        	System.exit(1);
        }
        
        AlignToTimeIntervalService timeService = new AlignToTimeIntervalService(updateEveryNSec);
        while(true) {
        	timeService.alignToNextInterval();
        	
        	pluginHolder.collect();
        }
    }

	private static boolean verifyWeCanCollectValues() {
		log.warning("TODO: Implement verifyWeCanCollectValues()");
		return true;
	}

}
