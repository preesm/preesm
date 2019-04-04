package org.preesm.commons.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author anmorvan
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreesmTask {
  String id();

  String category() default "Other";

  String shortDescription() default "";

  String description() default "";

  Input[] inputs() default {};

  Output[] outputs() default {};

  Parameter[] parameters() default {};

  DocumentedError[] documentedErrors() default {};

  String[] seeAlso() default {};
}
