package org.preesm.model.slam.utils;

import java.util.Comparator;

/**
 * Compares name of component instance (compares digits only if text is different).
 */
public class ComponentInstanceNameComparator implements Comparator<String> {

  @Override
  public int compare(String o1, String o2) {
    final String o1StringPart = o1.replaceAll("\\d", "");
    final String o2StringPart = o2.replaceAll("\\d", "");

    if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
      return Long.compare(extractInt(o1), extractInt(o2));
    } else {
      return o1.compareTo(o2);
    }
  }

  static long extractInt(String s) {
    String num = s.replaceAll("\\D", "");
    // return 0 if no digits found
    return num.isEmpty() ? 0 : Long.parseLong(num);
  }

}
