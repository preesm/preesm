package org.preesm.commons.doc.annotations;

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

  String name();

  String category() default "Other";

  String shortDescription() default "";

  String description() default "";

  Port[] inputs() default {};

  Port[] outputs() default {};

  Parameter[] parameters() default {};

  DocumentedError[] documentedErrors() default {};

  String[] seeAlso() default {};
}
