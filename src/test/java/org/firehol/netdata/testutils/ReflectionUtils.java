package org.firehol.netdata.testutils;

import java.lang.reflect.Field;

public abstract class ReflectionUtils {
	/**
	 * Getter for private field fieldName of object.
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
	 *
	 *             <ul>
	 *
	 *             <li>the caller's class loader is not the same as the class
	 *             loader of this class and invocation of
	 *             {@link SecurityManager#checkPermission s.checkPermission}
	 *             method with
	 *             {@code RuntimePermission("accessDeclaredMembers")} denies
	 *             access to the declared field
	 *
	 *             <li>the caller's class loader is not the same as or an
	 *             ancestor of the class loader for the current class and
	 *             invocation of {@link SecurityManager#checkPackageAccess
	 *             s.checkPackageAccess()} denies access to the package of this
	 *             class
	 * 
	 *             <li>if s denies making the field accessible.
	 *
	 *             </ul>
	 */
	public static Object getPrivateField(Object object, String fieldName)
			throws NoSuchFieldException, IllegalAccessException, SecurityException {
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}
}
