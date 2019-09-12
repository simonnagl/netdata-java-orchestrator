// SPDX-License-Identifier: GPL-3.0-or-later

package org.firehol.netdata.testutils;

import java.lang.reflect.Field;

public final class ReflectionUtils {

	private ReflectionUtils() {
	}

	/**
	 * Getter for private field {@code filedName} of {@code object}.
	 *
	 * @param object
	 *            to access
	 * @param fieldName
	 *            Name of field to read.
	 * @return the value of the field.
	 * @throws NoSuchFieldException
	 *             if a field with the specified name is not found
	 * @throws IllegalAccessException
	 *             if this Field object is enforcing Java language access
	 *             control and the underlying field is inaccessible.
	 * @throws SecurityException
	 *             If a security manager, <i>s</i>, is present and any of the
	 *             following conditions is met:
	 *             <ul>
	 *             <li>the caller's class loader is not the same as the class
	 *             loader of this class and invocation of
	 *             {@link SecurityManager#checkPermission s.checkPermission}
	 *             method with
	 *             {@code RuntimePermission("accessDeclaredMembers")} denies
	 *             access to the declared field
	 *             <li>the caller's class loader is not the same as or an
	 *             ancestor of the class loader for the current class and
	 *             invocation of {@link SecurityManager#checkPackageAccess
	 *             s.checkPackageAccess()} denies access to the package of this
	 *             class
	 *             <li>if <i>s</i> denies making the field accessible.
	 *             </ul>
	 */
	public static Object getPrivateField(Object object, String fieldName)
			throws NoSuchFieldException, IllegalAccessException, SecurityException {
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}

	/**
	 * Setter for private filed {@code filedName} of {@code object}.
	 *
	 * @param object
	 *            to modify
	 * @param fieldName
	 *            Name of field to set.
	 * @param value
	 *            to set
	 * @throws NoSuchFieldException
	 *             if a field with the specified name is not found.
	 * @throws NullPointerException
	 *             If any of the following conditions is met:
	 *             <ul>
	 *             <li>if {@code filedName} is null</li>
	 *             <li>if the {@code object} is {@code null} and the field is an
	 *             instance field.</li>
	 *             </ul>
	 * @throws SecurityException
	 *             If a security manager, <i>s</i>, is present and any of the
	 *             following conditions is met:
	 *
	 *             <ul>
	 *             <li>the caller's class loader is not the same as the class
	 *             loader of this class and invocation of
	 *             {@link SecurityManager#checkPermission s.checkPermission}
	 *             method with
	 *             {@code RuntimePermission("accessDeclaredMembers")} denies
	 *             access to the declared field
	 *             <li>the caller's class loader is not the same as or an
	 *             ancestor of the class loader for the current class and
	 *             invocation of {@link SecurityManager#checkPackageAccess
	 *             s.checkPackageAccess()} denies access to the package of this
	 *             class
	 *             <li>if <i>s</i> denies making the field accessible.
	 *             </ul>
	 * @throws IllegalArgumentException
	 *             if the specified object is not an instance of the class or
	 *             interface declaring the underlying field (or a subclass or
	 *             implementor thereof), or if an unwrapping conversion fails.
	 * @throws IllegalAccessException
	 *             if this Field object is enforcing Java language access
	 *             control and the underlying field is either inaccessible or
	 *             final.
	 * @throws ExceptionInInitializerError
	 *             if the initialization provoked by this method fails.
	 */
	public static void setPrivateFiled(Object object, String fieldName, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			NullPointerException, ExceptionInInitializerError {
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(object, value);
	}
}
