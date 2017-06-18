package org.firehol.netdata.utils;

/**
 * A converter converts an object of class S to an object of class D.
 * 
 * @author Simon Nagl
 *
 * @param <S>
 *            Source
 * @param <D>
 *            Destination
 */
public interface Converter<S, D> {
	D convert(S source);
}
