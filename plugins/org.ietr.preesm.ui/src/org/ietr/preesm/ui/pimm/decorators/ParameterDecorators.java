/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.ui.pimm.decorators;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.util.DependencyCycleDetector;
import org.ietr.preesm.ui.pimm.diagram.PiMMImageProvider;

// TODO: Auto-generated Javadoc
/**
 * Class providing methods to retrieve the {@link IDecorator} of an {@link Parameter}.<br>
 * <b> This decorators only works for Parameters and not for configuration input interfaces.</b>
 *
 * @author kdesnos
 * @author jheulot
 *
 */
public class ParameterDecorators {

  /**
   * Methods that returns all the {@link IDecorator} for a given {@link Parameter}.
   *
   * @param parameter
   *          the treated {@link Parameter}
   * @param pe
   *          the {@link PictogramElement} to decorate
   * @return the {@link IDecorator} table.
   */
  public static IDecorator[] getDecorators(final Parameter parameter, final PictogramElement pe) {

    final List<IDecorator> decorators = new ArrayList<>();

    // Check if the parameter belongs to a cycle
    final IDecorator cycleDecorator = ParameterDecorators.getCycleDecorators(parameter, pe);
    if (cycleDecorator != null) {
      decorators.add(cycleDecorator);
    } else {
      // Check if the parameter expression is correct
      final IDecorator expressionDecorator = ParameterDecorators.getExpressionDecorator(parameter, pe);
      if (expressionDecorator != null) {
        decorators.add(expressionDecorator);
      }
      // Check if the parameter is locally static if
      final IDecorator staticDecorator = ParameterDecorators.getLocallyStaticDecorator(parameter, pe);
      if (staticDecorator != null) {
        decorators.add(staticDecorator);
      }
    }

    final IDecorator[] result = new IDecorator[decorators.size()];
    decorators.toArray(result);

    return result;
  }

  /**
   * Get the {@link IDecorator} indicating if the {@link Parameter#isLocallyStatic()}.
   *
   * @param parameter
   *          the {@link Parameter} to test
   * @param pe
   *          the {@link PictogramElement} of the tested {@link Parameter}
   * @return the {@link IDecorator} if the {@link Parameter#isLocallyStatic()} , else <code>null</code>.
   */
  protected static IDecorator getLocallyStaticDecorator(final Parameter parameter, final PictogramElement pe) {
    if (!parameter.isLocallyStatic()) {
      final ImageDecorator imageRenderingDecorator = new ImageDecorator(PiMMImageProvider.IMG_WHITE_DOT_BLUE_LINE);

      imageRenderingDecorator.setMessage("Dynamically Configurable Parameter");
      imageRenderingDecorator.setX((pe.getGraphicsAlgorithm().getWidth() / 2) - 5);
      imageRenderingDecorator.setY(8);

      return imageRenderingDecorator;
    }

    return null;
  }

  /**
   * Get {@link IDecorator} indicating that the {@link Parameter} belongs to a cycle or depends on {@link Parameter}s belonging to a cycle.
   *
   * @param parameter
   *          the {@link Parameter} to test
   * @param pe
   *          the {@link PictogramElement} of the tested {@link Parameter}
   * @return the {@link IDecorator} for the {@link Parameter} or <code>null</code> if the {@link Parameter} does not belong nor depends on a {@link Dependency}
   *         cycle.
   */
  protected static IDecorator getCycleDecorators(final Parameter parameter, final PictogramElement pe) {
    final DependencyCycleDetector detector = new DependencyCycleDetector();
    detector.doSwitch(parameter);
    if (detector.cyclesDetected()) {
      for (final List<Parameter> cycle : detector.getCycles()) {
        if (cycle.contains(parameter)) {
          final ImageDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
          String message = "Parameter belongs to a cycle: ";
          for (final Parameter param : cycle) {
            message += param.getName() + ">";
          }
          message += parameter.getName();
          imageRenderingDecorator.setMessage(message);
          imageRenderingDecorator.setX((pe.getGraphicsAlgorithm().getWidth() / 2) - 8);
          imageRenderingDecorator.setY(8);

          return imageRenderingDecorator;
        }

        // If the parameter is not contained in a detected cycle but
        // cycles were detected
        // its locally static status cannot be determined
        final ImageDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);

        imageRenderingDecorator.setMessage("Parameter depends on parameters contained in a cycle.");
        imageRenderingDecorator.setX((pe.getGraphicsAlgorithm().getWidth() / 2) - 8);
        imageRenderingDecorator.setY(8);

        return imageRenderingDecorator;
      }
    }
    return null;
  }

  /**
   * Get {@link IDecorator} indicating that the {@link Parameter} have a invalid expression.
   *
   * @param param
   *          the {@link Parameter} to test
   * @param pe
   *          the {@link PictogramElement} of the tested {@link Parameter}
   * @return the {@link IDecorator} for the {@link Parameter} or <code>null</code> if the {@link Parameter} have a valid expression.
   */
  protected static IDecorator getExpressionDecorator(final Parameter param, final PictogramElement pe) {
    if (param.getExpression().evaluate().contains("Error")) {
      final ImageDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
      imageRenderingDecorator.setMessage("Problems in parameter resolution");
      imageRenderingDecorator.setX((pe.getGraphicsAlgorithm().getWidth() / 2) - 8);
      imageRenderingDecorator.setY(8);

      return imageRenderingDecorator;
    }
    return null;
  }
}
