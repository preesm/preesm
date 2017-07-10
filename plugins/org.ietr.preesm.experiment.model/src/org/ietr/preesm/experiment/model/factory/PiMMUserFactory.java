package org.ietr.preesm.experiment.model.factory;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;

/**
 *
 * @author anmorvan
 *
 */
public final class PiMMUserFactory {

  public static final PiMMUserFactory instance = new PiMMUserFactory();

  private PiMMUserFactory() {

  }

  /**
   * Copy an existing Vertex
   */
  public final AbstractVertex copy(final AbstractVertex vertex) {
    final EcoreUtil.Copier copier = new EcoreUtil.Copier(false);
    return (AbstractVertex) copier.copy(vertex);
  }
}
