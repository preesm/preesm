package org.preesm.commons.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface Value {
  String name();

  String effect() default "";
}
