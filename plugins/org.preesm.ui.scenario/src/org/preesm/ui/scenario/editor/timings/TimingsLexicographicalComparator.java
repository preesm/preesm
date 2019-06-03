package org.preesm.ui.scenario.editor.timings;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.preesm.model.pisdf.AbstractActor;

/**
 * Allows to compare timings by lexicographical order of the actors.
 * 
 * @author ahonorat
 */
public class TimingsLexicographicalComparator extends ViewerComparator {

  /**
   * According to {@link org.preesm.model.pisdf.PiSDF.xcore#getVertexPath}
   */
  public static final String HIERARCHY_DELIMITER = "/";

  @Override
  public int compare(Viewer viewer, Object o1, Object o2) {
    if ((o1 instanceof AbstractActor) && (o2 instanceof AbstractActor)) {
      final AbstractActor vertex1 = (AbstractActor) o1;
      final AbstractActor vertex2 = (AbstractActor) o2;
      int nbSep1 = vertex1.getVertexPath().split(HIERARCHY_DELIMITER).length;
      int nbSep2 = vertex2.getVertexPath().split(HIERARCHY_DELIMITER).length;
      if (nbSep1 != nbSep2) {
        return Integer.compare(nbSep1, nbSep2);
      }
      return vertex1.getVertexPath().compareTo(vertex2.getVertexPath());
    }
    return 0;
  }

}
