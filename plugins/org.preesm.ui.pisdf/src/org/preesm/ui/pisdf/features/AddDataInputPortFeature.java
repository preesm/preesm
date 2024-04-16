/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2013)
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
package org.preesm.ui.pisdf.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

// TODO: Auto-generated Javadoc
/**
 * Add Feature for {@link DataInputPort}s.
 *
 * @author kdesnos
 * @author jheulot
 */
public class AddDataInputPortFeature extends AbstractAddActorPortFeature {

  /** The Constant DATA_INPUT_PORT_FOREGROUND. */
  public static final IColorConstant DATA_INPUT_PORT_FOREGROUND = AddActorFeature.ACTOR_FOREGROUND;

  /** The Constant DATA_INPUT_PORT_BACKGROUND. */
  public static final IColorConstant DATA_INPUT_PORT_BACKGROUND = new ColorConstant(182, 215, 122);

  /** The Constant DATA_INPUT_PORT_POSITION. */
  public static final PortPosition DATA_INPUT_PORT_POSITION = PortPosition.LEFT;

  /** The Constant DATA_INPUT_PORT_KIND. */
  public static final String DATA_INPUT_PORT_KIND = "input";

  /**
   * Default constructor.
   *
   * @param fp
   *          the feature provider
   */
  public AddDataInputPortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Add Data Input Port";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Add a data input port to the Actor";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.AbstractAddActorPortFeature#getPosition()
   */
  @Override
  public PortPosition getPosition() {
    return AddDataInputPortFeature.DATA_INPUT_PORT_POSITION;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.AbstractAddActorPortFeature#addPortGA(org.eclipse.graphiti.mm.algorithms.
   * GraphicsAlgorithm)
   */
  @Override
  public GraphicsAlgorithm addPortGA(final GraphicsAlgorithm containerShape) {
    // Get the GaService
    final IGaService gaService = Graphiti.getGaService();
    // Create the port GraphicAlgorithm
    final Rectangle rectangle = gaService.createPlainRectangle(containerShape);
    rectangle.setForeground(manageColor(AddDataInputPortFeature.DATA_INPUT_PORT_FOREGROUND));
    rectangle.setBackground(manageColor(AddDataInputPortFeature.DATA_INPUT_PORT_BACKGROUND));
    rectangle.setLineWidth(1);
    final int portFontHeight = AbstractAddActorPortFeature.portFontHeight;
    gaService.setSize(rectangle, AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE,
        AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE);
    gaService.setLocation(rectangle, 0, 1 + ((portFontHeight - AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE) / 2));
    return rectangle;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.AbstractAddActorPortFeature#addPortLabel(org.eclipse.graphiti.mm.algorithms.
   * GraphicsAlgorithm, java.lang.String)
   */
  @Override
  public GraphicsAlgorithm addPortLabel(final GraphicsAlgorithm containerShape, final String portName) {
    // Get the GaService
    final IGaService gaService = Graphiti.getGaService();

    // Create the text
    final Text text = gaService.createText(containerShape);
    text.setValue(portName);
    text.setFont(getPortFont());
    text.setForeground(manageColor(AbstractAddActorPortFeature.PORT_TEXT_FOREGROUND));

    // Layout the text
    final int portFontHeight = AbstractAddActorPortFeature.portFontHeight;
    text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
    gaService.setHeight(text, portFontHeight);

    return text;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.AbstractAddActorPortFeature#getNewPort(java.lang.String,
   * org.ietr.preesm.experiment.model.pimm.ExecutableActor)
   */
  @Override
  public Port getNewPort(final String portName, final AbstractActor actor) {
    final DataInputPort newPort = PiMMUserFactory.instance.createDataInputPort();
    newPort.setName(portName);
    actor.getDataInputPorts().add(newPort);
    return newPort;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.AbstractAddActorPortFeature#getPortKind()
   */
  @Override
  public String getPortKind() {
    return AddDataInputPortFeature.DATA_INPUT_PORT_KIND;
  }

}
