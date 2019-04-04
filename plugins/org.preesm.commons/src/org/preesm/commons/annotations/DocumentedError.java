package org.preesm.commons.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface DocumentedError {
  String message();

  String explanation() default "";
}
