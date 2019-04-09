package org.preesm.commons.doc.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface Value {
  String name();

  String effect() default "Undocumented";
}
