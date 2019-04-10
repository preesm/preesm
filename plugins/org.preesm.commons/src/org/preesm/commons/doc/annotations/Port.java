package org.preesm.commons.doc.annotations;

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
