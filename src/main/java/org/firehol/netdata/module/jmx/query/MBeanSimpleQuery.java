/*
 * Copyright (C) 2017 Simon Nagl
 *
 * netadata-plugin-java-daemon is free software: you can redistribute it and/or modify
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

package org.firehol.netdata.module.jmx.query;

import lombok.Getter;
import org.firehol.netdata.model.Dimension;
import org.firehol.netdata.module.jmx.exception.JmxMBeanServerQueryException;
import org.firehol.netdata.module.jmx.utils.MBeanServerUtils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.util.List;

/**
 * A MBeanQuery is responsible to query a attribute of a MBean and place it's value into {@link Dimension}
 */
@Getter
public class MBeanSimpleQuery extends MBeanQuery {


    private final MBeanValueStore valueStore;

    MBeanSimpleQuery(final MBeanServerConnection mBeanServer, final ObjectName name, final String attribute, final MBeanValueStore valueStore) {
        super(mBeanServer, name, attribute);
        this.valueStore = valueStore;
    }

    public List<Dimension> getDimensions() {
        return valueStore.getAllDimension();
    }

    /**
     * Add a dimension which value should be updated by this query.
     * <p>
     * Attribute must match {@link #getAttribute()} or be more precise.
     *
     * @param dimension to add to the list of dimensions
     * @param attribute of the MBean which should be queried
     */
    @Override
    public void addDimension(Dimension dimension, final String attribute) {
        if (!this.getAttribute().equals(attribute)) {
            throw new IllegalArgumentException(String.format("attribute '%s' must match this.attribute '%s'", attribute, this.getAttribute()));
        }

        this.valueStore.addDimension(dimension);
    }

    @Override
    public void query() throws JmxMBeanServerQueryException {
        Object result = MBeanServerUtils.getAttribute(getMBeanServer(), this.getName(), this.getAttribute());
        valueStore.updateValue(result);
    }
}