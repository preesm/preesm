package org.preesm.commons.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface Input {
  String name();

  Class<?> type();

  String description() default "";
}
