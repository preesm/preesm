package org.preesm.commons.annotations;

/**
 *
 * @author anmorvan
 *
 */
public @interface Parameter {

  String name();

  String description() default "";

  Value[] values() default {};

}
