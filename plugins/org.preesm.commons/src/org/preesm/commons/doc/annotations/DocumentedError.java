package org.preesm.commons.doc.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface DocumentedError {
  String message();

  String explanation() default "";
}
