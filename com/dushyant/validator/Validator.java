package com.dushyant.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import com.dushyant.annotation.NotNull;
import com.dushyant.annotation.Pattern;

public class Validator {
  private static Logger LOG = Logger.getLogger(Validator.class.getSimpleName());
  final static WeakHashMap<String, java.util.regex.Pattern> weakMap =
      new WeakHashMap<String, java.util.regex.Pattern>();

  public Validator() {}

  public static void is(Object x) throws IllegalArgumentException, IllegalAccessException {
    hasValidFields(x);
  }

  private static List<Field> extractFields(Object o) {
    List<Field> fieldList = new ArrayList<Field>();
    Class<?> class2 = o.getClass();
    do {
      System.out.println(class2);
      fieldList.addAll(Arrays.asList(class2.getDeclaredFields()));
      class2 = class2.getSuperclass();
    } while (!class2.equals(Object.class));
    return fieldList;
  }


  public static void hasValidFields(Object x) throws IllegalArgumentException,
      IllegalAccessException {
    try {
      if (x == null) {
        LOG.info("Validator.hasValidFields()");
        throw new NullPointerException();
      }

      final List<Field> declaredFields = extractFields(x);
      for (Field field : declaredFields) {
        field.setAccessible(true);
        boolean hasNotNullAnnotataion = field.isAnnotationPresent(NotNull.class);
        boolean hasPatternAnnotataion = field.isAnnotationPresent(Pattern.class);
        final boolean b = (hasNotNullAnnotataion) || (hasPatternAnnotataion);
        if (!b) {
          continue;
        }

        final NotNull annotation = field.getAnnotation(NotNull.class);
        Object object = null;
        if ((object = field.get(x)) == null) {
          if (hasNotNullAnnotataion) {
            final String message = annotation.message();
            final String fieldName =
                (annotation.fieldName() != null && !annotation.fieldName().equals("")) ? annotation
                    .fieldName().trim() : field.getName();
            if (message instanceof String && !message.trim().equals("")) {
              throw new IllegalArgumentException(message);
            } else {
              throw new IllegalArgumentException(String.format("%s can not be null", fieldName));
            }
          } else {
            throw new IllegalArgumentException(String.format("%s can not be null", field.getName()));
          }
        }

        if (object instanceof String && hasPatternAnnotataion) {

          final Pattern annotationPattern = field.getAnnotation(Pattern.class);
          final String patternString = annotationPattern.pattern();

          java.util.regex.Pattern p = null;
          if (weakMap.containsKey(patternString)) {
            p = weakMap.get(patternString);
          } else {
            p = java.util.regex.Pattern.compile(patternString);
            weakMap.put(patternString, p);
          }
          String stringObject = (String) object;
          final boolean matches = p.matcher(stringObject).matches();
          if (!matches) {
            final String message = annotationPattern.message();
            if (message instanceof String && !message.trim().equals("")) {
              throw new IllegalArgumentException(message);
            } else {
              throw new IllegalArgumentException(String.format("%s is invalid", field.getName()));
            }
          }
        }
      }
    } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
      throw e;
    }
  }
}

