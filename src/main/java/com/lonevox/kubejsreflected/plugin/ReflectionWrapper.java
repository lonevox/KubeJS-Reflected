package com.lonevox.kubejsreflected.plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public interface ReflectionWrapper {
	static Class<?> getClass(Object object) throws ClassNotFoundException {
		if (object instanceof String stringObject) {
			return ReflectionWrapper.getClass(stringObject);
		}
		return object.getClass();
	}

	static Class<?> getClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

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
		return ReflectionWrapper.getField(ReflectionWrapper.getClass(object), fieldName);
	}

	static Object getFieldValue(Class<?> clazz, Object object, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		return ReflectionWrapper.getField(clazz, fieldName).get(object);
	}

	static Object getFieldValue(Object object, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		return ReflectionWrapper.getField(object, fieldName).get(object);
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

//	static void setFieldValue(Class<?> clazz, String fieldName, Object value) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
//		ReflectionWrapper.setFieldValue(object, ReflectionWrapper.getField(object, fieldName), value);
//	}

	static void setFieldValue(Object object, String fieldName, Object value) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
		ReflectionWrapper.setFieldValue(object, ReflectionWrapper.getField(object, fieldName), value);
	}

	static void setFieldValue(Field field, Object value) throws IllegalAccessException {
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Field must be static to use this setFieldValue function shape. You probably want to instead use setFieldValue(Object object, Field field, Object value).");
		}
		ReflectionWrapper.setFieldValue(null, field, value);
	}

	static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		var method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method;
	}

	static Method getMethod(Object object, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
		return ReflectionWrapper.getMethod(ReflectionWrapper.getClass(object), methodName, parameterTypes);
	}

	static Object invokeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
		Class<?>[] argumentClasses = Arrays.stream(args)
				.map(Object::getClass)
				.toArray(Class<?>[]::new);
		var method = ReflectionWrapper.getMethod(object, methodName, argumentClasses);
		return method.invoke(object, args);
	}
}
