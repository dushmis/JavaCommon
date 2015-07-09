package com.dushyant.services;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dushyant.annotation.DBField;

/**
 * @param <E>
 */
public class Extractor<E extends DBTable> extends ExtractorService<E> {

  /**
   * @param oyeClass
   * @param resultSet
   * @return
   */
  public static <E extends DBTable> ExtractorService<E> createExtractor(Class<E> oyeClass,
      ResultSet resultSet) {
    return new Extractor<E>(oyeClass, resultSet, true);
  }

  /**
   * @param oyeClass
   * @param resultSet
   * @param closeOnExit
   * @return
   */
  public static <E extends DBTable> ExtractorService<E> createExtractor(Class<E> oyeClass,
      ResultSet resultSet, boolean closeOnExit) {
    return new Extractor<E>(oyeClass, resultSet, closeOnExit);
  }

  final ResultSet resultSet;
  final Class<E> myType;
  final boolean closeOnExit;


  /**
   * @param oyeClass
   * @param resultSet
   * @param closeOnExit
   */
  private Extractor(Class<E> oyeClass, ResultSet resultSet, boolean closeOnExit) {
    this.myType = oyeClass;
    this.resultSet = resultSet;
    this.closeOnExit = closeOnExit;
  }

  /**
   * @param o
   * @return
   */
  private List<Field> extractFields(Object o) {
    System.out.println(o);
    List<Field> fieldList = null;
    if (o == null) {
      // NullPointerException
      return fieldList;
    }
    fieldList = new ArrayList<Field>();
    Class<?> class2 = o.getClass();
    System.out.println(class2);
    do {
      fieldList.addAll(Arrays.asList(class2.getDeclaredFields()));
      class2 = class2.getSuperclass();
    } while (!class2.equals(Object.class));
    return fieldList;
  }


  /*
   * (non-Javadoc)
   * 
   * @see com.youbb.services.ExtractorService#extract()
   */
  @Override
  public List<E> extract() throws SQLException, SecurityException, IllegalArgumentException,
      IllegalAccessException, InstantiationException {
    if (resultSet == null || resultSet.isClosed()) {
      throw new SQLException("Invalid ResultSet");
    }
    List<E> objects = null;
    List<Field> declaredFields = null;
    try {
      objects = new ArrayList<E>();
      while (resultSet.next()) {
        E newInstance = myType.newInstance();
        declaredFields = extractFields(newInstance);
        System.out.println(declaredFields);
        for (Field field : declaredFields) {
          field.setAccessible(true);
          Class<?> type = field.getType();
          int index = 0;
          try {
            index =
                resultSet.findColumn(field.isAnnotationPresent(DBField.class) ? field
                    .getAnnotation(DBField.class).field() : field.getName());
          } catch (SQLException ex) {
            index = -1;
          }

          if (index != -1) {
            Object object = resultSet.getObject(index);
            try {
              Object raw = type.isPrimitive() && object == null ? 0 : object;
              Object castedObject = type.cast(raw);
              field.set(newInstance, castedObject);
            } catch (ClassCastException e) {
              throw new ClassCastException(String.format("%s for field %s",
                  e.getLocalizedMessage(), field));
            }
          }
        }
        objects.add(newInstance);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      if (closeOnExit) {
        if (resultSet != null) {
          try {
            resultSet.close();
          } catch (Exception e) {}
        }
      }
    }
    return objects;
  }
}
