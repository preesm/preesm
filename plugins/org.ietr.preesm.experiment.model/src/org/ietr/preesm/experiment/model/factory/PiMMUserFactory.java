package org.ietr.preesm.experiment.model.factory;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;

/**
 *
 * @author anmorvan
 *
 */
public final class PiMMUserFactory {

  public static final PiMMUserFactory instance = new PiMMUserFactory();

  private static final PiMMFactory factory = PiMMFactory.eINSTANCE;

  private static final EcoreUtil.Copier copier = new EcoreUtil.Copier(false);

  private PiMMUserFactory() {

  }

  /**
   * Copy an existing Vertex
   */
  public final AbstractVertex copy(final AbstractVertex vertex) {
    return (AbstractVertex) copier.copy(vertex);
  }

  /**
   *
   */
  public Dependency createDependency(ISetter setter, ConfigInputPort target) {
    final Dependency dep = factory.createDependency();
    dep.setGetter(target);
    dep.setSetter(setter);
    return dep;
  }
}
