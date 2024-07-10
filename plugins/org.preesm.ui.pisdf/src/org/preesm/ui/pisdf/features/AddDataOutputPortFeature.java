/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.ui.pisdf.util.PiMMUtil;

// TODO: Auto-generated Javadoc
/**
 * Add Feature for {@link DataOutputPort}s.
 *
 * @author kdesnos
 * @author jheulot
 */
public class AddDataOutputPortFeature extends AbstractAddActorPortFeature {

  /** The Constant DATA_OUTPUT_PORT_FOREGROUND. */
  public static final IColorConstant DATA_OUTPUT_PORT_FOREGROUND = AddActorFeature.ACTOR_FOREGROUND;

  /** The Constant DATA_OUTPUT_PORT_BACKGROUND. */
  public static final IColorConstant DATA_OUTPUT_PORT_BACKGROUND = new ColorConstant(234, 153, 153);

  /** The Constant DATA_OUTPUT_PORT_POSITION. */
  public static final PortPosition DATA_OUTPUT_PORT_POSITION = PortPosition.RIGHT;

  /** The Constant DATA_OUTPUT_PORT_KIND. */
  public static final String DATA_OUTPUT_PORT_KIND = "output";

  private static final String JOIN_INPUT_ERR_MESSAGE = "A Join Actor can have only 1 data output port.";

  private static final String RB_INPUT_ERR_MESSAGE = "A Round Buffer Actor can have only 1 data output port.";

  /**
   * Default constructor.
   *
   * @param fp
   *          the feature provider
   */
  public AddDataOutputPortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Add Data Output Port";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Add a data output port to the Actor";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.AbstractAddActorPortFeature#getPosition()
   */
  @Override
  public PortPosition getPosition() {
    return AddDataOutputPortFeature.DATA_OUTPUT_PORT_POSITION;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    // allow if exactly one pictogram element representing an Actor is selected
    boolean ret = false;
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof final JoinActor joinActor && !joinActor.getDataOutputPorts().isEmpty()) {
        PiMMUtil.setToolTip(getFeatureProvider(), pes[0].getGraphicsAlgorithm(), getDiagramBehavior(),
            JOIN_INPUT_ERR_MESSAGE);
      } else if (bo instanceof final RoundBufferActor rbActor && !rbActor.getDataOutputPorts().isEmpty()) {
        PiMMUtil.setToolTip(getFeatureProvider(), pes[0].getGraphicsAlgorithm(), getDiagramBehavior(),
            RB_INPUT_ERR_MESSAGE);
      } else if (bo instanceof ExecutableActor) {
        ret = true;
      }
    }
    return ret;
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
    // Create the port GraphicAlcorithm
    final Rectangle rectangle = gaService.createPlainRectangle(containerShape);
    rectangle.setForeground(manageColor(AddDataOutputPortFeature.DATA_OUTPUT_PORT_FOREGROUND));
    rectangle.setBackground(manageColor(AddDataOutputPortFeature.DATA_OUTPUT_PORT_BACKGROUND));
    rectangle.setLineWidth(1);
    gaService.setSize(rectangle, AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE,
        AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE);
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

    final Text text = (Text) super.addPortLabel(containerShape, portName);
    text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);

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
    final DataOutputPort newPort = PiMMUserFactory.instance.createDataOutputPort();
    newPort.setName(portName);
    actor.getDataOutputPorts().add(newPort);
    return newPort;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.ui.pisdf.features.AbstractAddActorPortFeature#getPortKind()
   */
  @Override
  public String getPortKind() {
    return AddDataOutputPortFeature.DATA_OUTPUT_PORT_KIND;
  }
}
