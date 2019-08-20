package org.preesm.commons;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author anmorvan
 *
 */
public class CollectionUtil {

  /**
   * Retrieves all the map values of the selected keys in a list. If selectedKeys is ordered, the resulting list will be
   * ordered accordingly. If a key is absent, the result will omit the value.
   */
  public static final <V, T> List<V> mapGetAll(final Map<T, V> map, final Collection<T> selectedKeys) {
    final List<V> result = new LinkedList<>();
    for (final T key : selectedKeys) {
      if (map.containsKey(key)) {
        result.add(map.get(key));
      }
    }
    return result;
  }

}
