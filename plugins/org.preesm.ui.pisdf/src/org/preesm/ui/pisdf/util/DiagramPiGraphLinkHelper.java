/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.ui.pisdf.util;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILinkService;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * This class contains methods that can be usefull when manipulating {@link Diagram} together with associated
 * {@link PiGraph}.
 *
 * @author kdesnos
 *
 */
public class DiagramPiGraphLinkHelper {

  private DiagramPiGraphLinkHelper() {
    // Prevents instaniation
  }

  /**
   * Clear all the bendpoints of the {@link Fifo} and {@link Dependency} in the diagram passed as a parameter.
   *
   * @param diagram
   *          the diagram
   */
  public static void clearBendpoints(final Diagram diagram) {
    for (final Connection connection : diagram.getConnections()) {
      ((FreeFormConnection) connection).getBendpoints().clear();
    }
  }

  /**
   * Retrieve the {@link PictogramElement} of the {@link Diagram} corresponding to the given {@link AbstractActor}.
   *
   * @param diagram
   *          the {@link Diagram} containing the {@link PictogramElement}
   * @param actor
   *          the {@link AbstractActor} whose {@link PictogramElement} is searched.
   * @return the {@link PictogramElement} of the {@link AbstractActor}.
   * @throws PreesmRuntimeException
   *           if no {@link PictogramElement} could be found in this {@link Diagram} for this {@link AbstractActor}.
   */
  public static PictogramElement getActorPE(final Diagram diagram, final AbstractActor actor)
      throws PreesmRuntimeException {
    // Get the PE
    final List<PictogramElement> pes;
    if (actor instanceof final DelayActor delayActor) {
      pes = Graphiti.getLinkService().getPictogramElements(diagram, delayActor.getLinkedDelay());
    } else {
      pes = Graphiti.getLinkService().getPictogramElements(diagram, actor);
    }

    return pes.stream().filter(ContainerShape.class::isInstance).findAny()
        .orElseThrow(() -> new PreesmRuntimeException("No PE was found for actor: " + actor.getName()));
  }

  /**
   * Retrieve the {@link PictogramElement} of the {@link Diagram} corresponding to the given {@link Fifo}.
   *
   * @param diagram
   *          the {@link Diagram} containing the {@link PictogramElement}
   * @param fifo
   *          the {@link Fifo} whose {@link PictogramElement} is searched.
   * @return the {@link PictogramElement} of the {@link Fifo}.
   * @throws PreesmRuntimeException
   *           if no {@link PictogramElement} could be found in this {@link Diagram} for this {@link Fifo}.
   */
  public static ContainerShape getDelayPE(final Diagram diagram, final Fifo fifo) throws PreesmRuntimeException {
    // Get all delays with identical attributes (may not be the
    // right delay is several delays have the same properties.)
    final Delay delay = fifo.getDelay();
    final ILinkService linkService = Graphiti.getLinkService();
    final List<PictogramElement> pes = linkService.getPictogramElements(diagram, delay);
    PictogramElement pe = null;
    for (final PictogramElement p : pes) {
      final EObject businessObjectForLinkedPictogramElement = linkService.getBusinessObjectForLinkedPictogramElement(p);
      if ((p instanceof ContainerShape) && (businessObjectForLinkedPictogramElement == delay)) {
        pe = p;
      }
    }
    // if PE is still null.. something is deeply wrong with this graph !
    if (pe == null) {
      throw new PreesmRuntimeException(
          "Pictogram element associated to delay of Fifo " + fifo.getId() + " could not be found.");
    }
    return (ContainerShape) pe;
  }

  /**
   * Get the {@link FreeFormConnection} associated to an edge of the {@link Diagram}. The Edge can either be a
   * {@link Fifo} or a {@link Dependency}.
   *
   * @param diagram
   *          the {@link Diagram} containing the edge.
   * @param edge
   *          the {@link Fifo} or the {@link Dependency} whose {@link FreeFormConnection} is searched.
   * @return the searched {@link FreeFormConnection}.
   * @throws PreesmRuntimeException
   *           if not {@link FreeFormConnection} could be found, a {@link PreesmRuntimeException} is thrown
   */
  public static FreeFormConnection getFreeFormConnectionOfEdge(final Diagram diagram, final EObject edge)
      throws PreesmRuntimeException {
    final List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram, edge);
    FreeFormConnection ffc = null;
    for (final PictogramElement pe : pes) {
      if ((Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe) == edge)
          && (pe instanceof final FreeFormConnection freeFromConn)) {
        ffc = freeFromConn;
      }
    }

    // if PE is still null.. something is deeply wrong with this graph !
    if (ffc == null) {
      throw new PreesmRuntimeException("Pictogram element associated to edge " + edge + " could not be found.");
    }
    return ffc;
  }

}
