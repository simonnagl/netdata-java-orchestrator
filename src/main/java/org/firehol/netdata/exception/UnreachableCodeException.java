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

package org.firehol.netdata.exception;

/**
 * Thrown to indicate that code has been reached which the author did not think
 * it could be reached.
 * 
 * @author Simon Nagl
 * @since 1.0
 */
public class UnreachableCodeException extends RuntimeException {
	private static final long serialVersionUID = 9126523210158499016L;

	public UnreachableCodeException() {
		super();
	}

	public UnreachableCodeException(String message, Throwable cause) {
		super(message, cause);
	}
}
