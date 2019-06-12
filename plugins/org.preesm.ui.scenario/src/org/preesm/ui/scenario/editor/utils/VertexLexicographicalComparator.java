package org.preesm.ui.scenario.editor.utils;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.preesm.model.pisdf.AbstractVertex;

/**
 * Allows to compare vertices by lexicographical order of their paths.
 *
 * @author ahonorat
 */
public class VertexLexicographicalComparator extends ViewerComparator {

  /**
   * According to {@link org.preesm.model.pisdf.PiSDF.xcore#getVertexPath}
   */
  public static final String HIERARCHY_DELIMITER = "/";

  @Override
  public int compare(Viewer viewer, Object o1, Object o2) {
    if ((o1 instanceof AbstractVertex) && (o2 instanceof AbstractVertex)) {
      final AbstractVertex vertex1 = (AbstractVertex) o1;
      final AbstractVertex vertex2 = (AbstractVertex) o2;
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
