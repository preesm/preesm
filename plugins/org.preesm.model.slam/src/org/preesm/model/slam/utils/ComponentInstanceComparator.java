package org.preesm.model.slam.utils;

import java.util.Comparator;
import org.preesm.model.slam.ComponentInstance;

/**
 * Comparing two components using their names.
 */
public class ComponentInstanceComparator implements Comparator<ComponentInstance> {

  /*
   * (non-Javadoc)
   *
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(ComponentInstance cb1, ComponentInstance cb2) {
    final String o1 = cb1.getInstanceName();
    final String o2 = cb2.getInstanceName();

    final String o1StringPart = o1.replaceAll("\\d", "");
    final String o2StringPart = o2.replaceAll("\\d", "");

    if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
      return extractInt(o1) - extractInt(o2);
    } else {
      return o1.compareTo(o2);
    }
  }

  int extractInt(String s) {
    String num = s.replaceAll("\\D", "");
    // return 0 if no digits found
    return num.isEmpty() ? 0 : Integer.parseInt(num);
  }

}
