package org.preesm.commons.doc.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface Parameter {

  String name();

  String description() default "Undocumented";

  Value[] values();

}
