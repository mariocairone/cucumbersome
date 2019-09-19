package com.mariocairone.cucumbersome.template.aspect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.mariocairone.cucumbersome.template.parser.TemplateParser;

@Aspect
public class ParseArgsAspect {

	@Around("@annotation(com.mariocairone.cucumbersome.template.aspect.ParseArgs) ")
	public void before(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed(parseArguments(joinPoint));
	}

	@SuppressWarnings("unchecked")
	public Object[] parseArguments(ProceedingJoinPoint joinPoint) throws IOException {

		Object[] arguments = joinPoint.getArgs();

		TemplateParser parser = getTemplateParser(joinPoint.getTarget());
		if (parser == null)
			return arguments;

		List<Object> argumentsList = Arrays.asList(arguments);
		List<Object> newArgumentList = new ArrayList<>();

		for (Object argument : argumentsList) {

			if (argument instanceof String) {
				String stringArg = (String) argument;
				String newArg = parser.parse(stringArg);
				newArgumentList.add(newArg);
			} else if (argument instanceof Map) {
				Map<String, String> stringArg = (Map<String, String>) argument;
				Map<String, String> newArg = parser.parse(stringArg);
				newArgumentList.add(newArg);
			} else if (argument instanceof List) {
				List<List<String>> stringArg = (List<List<String>>) argument;
				List<List<String>> newArg = parser.parse(stringArg);
				newArgumentList.add(newArg);
			} else {
				newArgumentList.add(argument);
			}

		}

		return newArgumentList.toArray();
	}

	private TemplateParser getTemplateParser(Object object) {

		List<Field> fields = getAllFields(object.getClass());

		for (Field f : fields) {
			f.setAccessible(true);
			// use equals to compare the data type.
			if (TemplateParser.class.isAssignableFrom(f.getType())) {

				try {
					return TemplateParser.class.cast(f.get(object));
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	private List<Field> getAllFields(Class<?> clazz) {
		if (clazz == null) {
			return Collections.emptyList();
		}

		List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
		List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers()))

				.collect(Collectors.toList());
		result.addAll(filteredFields);
		return result;
	}
}