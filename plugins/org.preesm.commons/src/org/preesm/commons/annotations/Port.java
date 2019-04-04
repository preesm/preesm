package org.preesm.commons.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface Port {
  String name();

  Class<?> type();

  String description() default "";
}
