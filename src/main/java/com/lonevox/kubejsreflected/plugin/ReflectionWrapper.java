package com.lonevox.kubejsreflected.plugin;

import dev.latvian.mods.rhino.util.HideFromJS;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public interface ReflectionWrapper {
	static Class<?> getClass(Object object) throws ClassNotFoundException {
		if (object instanceof String stringObject) {
			return ReflectionWrapper.getClass(stringObject);
		}
		if (object instanceof Class<?> classObject) {
			return classObject;
		}
		return object.getClass();
	}

	static Class<?> getClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	@HideFromJS
	static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field;
			}
		}
		var superClass = clazz.getSuperclass();
		if (superClass != Object.class) {
			return ReflectionWrapper.getField(clazz.getSuperclass(), fieldName);
		}
		throw new NoSuchFieldException(fieldName);
	}

	static Field getField(Object object, String fieldName) throws ClassNotFoundException, NoSuchFieldException {
		if (object instanceof Class<?> clazz) {
			return ReflectionWrapper.getField(clazz, fieldName);
		}
		return ReflectionWrapper.getField(ReflectionWrapper.getClass(object), fieldName);
	}

	static Object getFieldValue(Object object, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		return ReflectionWrapper.getField(object, fieldName).get(object);
	}

	static Object getFieldValue(Object object, Field field) throws IllegalAccessException {
		return field.get(object);
	}

	static Object getFieldValue(Field field) throws IllegalAccessException {
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Field must be static to use this getFieldValue function shape. You probably want to instead use getFieldValue(Object object, Field field).");
		}
		return field.get(null);
	}

	static void setFieldValue(Object object, Field field, Object value) throws IllegalAccessException {
		if (Modifier.isFinal(field.getModifiers())) {
			throw new IllegalArgumentException("Attempted to set a final field.");
		}
		var fieldType = field.getType();
		// Get primitive value if the field expects a primitive
		if (fieldType.isPrimitive()) {
			switch (fieldType.getName()) {
				case "float" -> field.setFloat(object, ((Double)value).floatValue());
				case "byte" -> field.setByte(object, ((Double)value).byteValue());
				case "short" -> field.setShort(object, ((Double)value).shortValue());
				case "int" -> field.setInt(object, ((Double)value).intValue());
				case "long" -> field.setLong(object, ((Double)value).longValue());
				default -> field.set(object, value);
			}
		} else {
			field.set(object, value);
		}
	}

	static void setFieldValue(Object object, String fieldName, Object value) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
		ReflectionWrapper.setFieldValue(object, ReflectionWrapper.getField(object, fieldName), value);
	}

	static void setFieldValue(Field field, Object value) throws IllegalAccessException {
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Field must be static to use this setFieldValue function shape. You probably want to instead use setFieldValue(Object object, Field field, Object value).");
		}
		ReflectionWrapper.setFieldValue(null, field, value);
	}

	@HideFromJS
	static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		var method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method;
	}

	static Method getMethod(Object object, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
		if (object instanceof Class<?> clazz) {
			return ReflectionWrapper.getMethod(clazz, methodName, parameterTypes);
		}
		return ReflectionWrapper.getMethod(ReflectionWrapper.getClass(object), methodName, parameterTypes);
	}

	static Class<?>[] objectsToClasses(Object... args) {
		return Arrays.stream(args)
				.map(Object::getClass)
				.toArray(Class<?>[]::new);
	}

	static Object invokeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
		var argumentClasses = ReflectionWrapper.objectsToClasses(args);
		var method = ReflectionWrapper.getMethod(object, methodName, argumentClasses);
		return method.invoke(object, args);
	}
}
