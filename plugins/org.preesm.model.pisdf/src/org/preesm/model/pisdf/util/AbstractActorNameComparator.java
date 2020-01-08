package org.preesm.model.pisdf.util;

import java.util.Comparator;
import org.preesm.model.pisdf.AbstractActor;

/**
 * Compare AbstractActor by name.
 * 
 * @author ahonorat
 *
 */
public class AbstractActorNameComparator implements Comparator<AbstractActor> {

  @Override
  public int compare(AbstractActor arg0, AbstractActor arg1) {
    int res = arg0.getName().compareTo(arg1.getName());
    if (res == 0) {
      return arg0.hashCode() - arg1.hashCode();
    }
    return res;
  }

}
