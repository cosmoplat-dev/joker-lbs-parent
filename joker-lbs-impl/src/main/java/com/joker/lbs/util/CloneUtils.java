package com.joker.lbs.util;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloneUtils {
	private static final Logger logger = LoggerFactory.getLogger(CloneUtils.class);

	@SuppressWarnings("unchecked")
	public static <T extends Cloneable> T shallowClone(T object) {
		String methodName = "clone";
		Method method = Reflections.getAccessibleMethod(object, methodName);
		if (method == null) {
			try {
				method = Object.class.getDeclaredMethod(methodName);
				Reflections.makeAccessible(method);
			} catch (Exception e) {
				logger.error(null, e);
			}
		}
		try {
			return (T) method.invoke(object);
		} catch (Exception e) {
			logger.error(null, e);
			return null;
		}
	}
}
