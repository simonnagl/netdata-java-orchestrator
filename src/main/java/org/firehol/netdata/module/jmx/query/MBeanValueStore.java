package org.firehol.netdata.module.jmx.query;

import org.firehol.netdata.model.Dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class MBeanValueStore {

    final List<Dimension> allDimension = new ArrayList<>(1);

    MBeanValueStore() {
    }

    public static MBeanValueStore newInstance(Object valueToHandle) {
        if(valueToHandle instanceof Double) {
            return new MBeanDoubleStore();
        } else if(valueToHandle instanceof Integer) {
            return new MBeanIntegerStore();
        }
        return new MBeanLongStore();
    }

    public void updateValue(final Object value) {
        final long castResult = toLong(value);
        allDimension.forEach(dimension -> dimension.setCurrentValue(castResult));
    }

    abstract long toLong(final Object value);

    public void addDimension(final Dimension dimension) {
        allDimension.add(dimension);
    }

    public Stream<Dimension> streamAllDimension() {
        return allDimension.stream();
    }

    public List<Dimension> getAllDimension() {
        return allDimension;
    }
}
