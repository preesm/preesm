package org.preesm.commons.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface Output {
  String name();

  Class<?> type();

  String description() default "";
}
