/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2023)
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
package org.preesm.ui.pisdf.decorators;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.util.DependencyCycleDetector;

// TODO: Auto-generated Javadoc
/**
 * Class providing methods to retrieve the {@link IDecorator} of a {@link Port}.
 *
 * @author jheulot
 */
public class PortDecorators {
  /**
   * Private constructor to ensure that this utility class cannot be instantiated.
   */
  private PortDecorators() {
    throw new IllegalStateException("This is an utility class that should not be instantiated");
  }

  /**
   * Methods that returns all the {@link IDecorator} for a given {@link Port} .
   *
   * @param port
   *          the treated {@link Port}
   * @param pe
   *          the {@link PictogramElement} to decorate
   * @return the {@link IDecorator} table.
   */
  public static IDecorator[] getDecorators(final Port port, final PictogramElement pe) {

    final List<IDecorator> decorators = new ArrayList<>();
    final DependencyCycleDetector detector = new DependencyCycleDetector();
    final PiGraph graph = (PiGraph) port.eContainer().eContainer();
    detector.doSwitch(graph);
    if (!detector.cyclesDetected()) {
      // Check if the actor is a configuration actor
      final IDecorator portDecorator = PortDecorators.getPortExpressionDecorator(port, pe);
      if (portDecorator != null) {
        decorators.add(portDecorator);
      }
    }

    final IDecorator[] result = new IDecorator[decorators.size()];
    decorators.toArray(result);

    return result;
  }

  /**
   * Get the {@link IDecorator} indicating if the {@link Port} have a valid expression.
   *
   * @param port
   *          the {@link Port} to test
   * @param pe
   *          the {@link PictogramElement} of the {@link Port}
   * @return the {@link IDecorator} or <code>null</code>.
   */
  protected static IDecorator getPortExpressionDecorator(final Port port, final PictogramElement pe) {
    final ImageDecorator errorRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
    final ImageDecorator warningRenderingDecorator = new ImageDecorator(
        IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);

    if (port instanceof ConfigOutputPort) {
      return null;
    }

    final String message = "Problems in parameter resolution: ";

    final BoxRelativeAnchor a = (BoxRelativeAnchor) pe;

    final int xCoord;
    final int yCoord = (int) (a.getRelativeHeight() * a.getReferencedGraphicsAlgorithm().getHeight()) - 1;

    if (port instanceof final DataInputPort) {
      xCoord = -5;
    } else if (port instanceof final DataOutputPort) {
      xCoord = a.getReferencedGraphicsAlgorithm().getWidth() - 13;
    } else {
      return null; // never there ?
    }

    final Expression expression = ((DataPort) port).getPortRateExpression();

    try {
      expression.evaluateAsDouble();

      if (!expression.isExpressionInteger()) {
        warningRenderingDecorator.setX(xCoord);
        warningRenderingDecorator.setY(yCoord);
        warningRenderingDecorator.setMessage("Port expression resolution will default to rounded integer.");

        return warningRenderingDecorator;
      }

    } catch (final ExpressionEvaluationException e) {
      errorRenderingDecorator.setX(xCoord);
      errorRenderingDecorator.setY(yCoord);
      errorRenderingDecorator.setMessage(message + e.getMessage());

      return errorRenderingDecorator;
    }

    return null;
  }
}
