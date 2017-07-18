package org.ietr.preesm.experiment.model.factory;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
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
   * Copy an existing Delay
   */
  public final Delay copy(final Delay delay) {
    return (Delay) copier.copy(delay);
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

  /**
   *
   */
  public Fifo createFifo(DataOutputPort sourcePortCopy, DataInputPort targetPortCopy, String type) {
    final Fifo res = factory.createFifo();
    res.setSourcePort(sourcePortCopy);
    res.setTargetPort(targetPortCopy);
    res.setType(type);
    return res;
  }
}
