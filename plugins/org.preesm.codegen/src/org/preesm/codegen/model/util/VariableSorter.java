package org.preesm.codegen.model.util;

import java.util.Comparator;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;

/**
 *
 * @author anmorvan
 *
 */
public class VariableSorter implements Comparator<Variable> {

  @Override
  public int compare(Variable o1, Variable o2) {
    if ((o1 instanceof Buffer) && (o2 instanceof Buffer)) {
      int sublevelO1 = 0;
      if (o1 instanceof SubBuffer) {
        Buffer b1 = (Buffer) o1;
        while (b1 instanceof SubBuffer) {
          sublevelO1++;
          b1 = ((SubBuffer) b1).getContainer();
        }
      }

      int sublevelO2 = 0;
      if (o2 instanceof SubBuffer) {
        Buffer b2 = (Buffer) o2;
        while (b2 instanceof SubBuffer) {
          sublevelO2++;
          b2 = ((SubBuffer) b2).getContainer();
        }
      }

      return sublevelO1 - sublevelO2;
    }
    if (o1 instanceof Buffer) {
      return 1;
    }
    if (o2 instanceof Buffer) {
      return -1;
    }
    return 0;
  }

}
