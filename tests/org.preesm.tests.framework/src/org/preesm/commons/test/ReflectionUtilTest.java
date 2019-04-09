package org.preesm.commons.test;

import java.util.Collection;
import org.junit.Test;
import org.preesm.commons.ReflectionUtil;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 *
 * @author anmorvan
 *
 */
public class ReflectionUtilTest {

  @Test
  public void testRefelctionClasses() {
    final Collection<Class<? extends AbstractTaskImplementation>> lookupChildClassesOf = ReflectionUtil
        .lookupChildClassesOf("org.preesm.workflow.tasks", AbstractTaskImplementation.class);
    for (final Class<?> t : lookupChildClassesOf) {
      System.out.println(t);
    }
  }

}
