/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class AddConfigInputPortFeature.
 */
public class AddConfigInputPortFeature extends AbstractAddActorPortFeature {

  /** The Constant CFG_INPUT_PORT_FOREGROUND. */
  public static final IColorConstant CFG_INPUT_PORT_FOREGROUND = AddActorFeature.ACTOR_FOREGROUND;

  /** The Constant CFG_INPUT_PORT_BACKGROUND. */
  public static final IColorConstant CFG_INPUT_PORT_BACKGROUND = AddParameterFeature.PARAMETER_BACKGROUND;

  /** The Constant CFG_INPUT_PORT_POSITION. */
  public static final PortPosition CFG_INPUT_PORT_POSITION = PortPosition.LEFT;

  /** The Constant CFG_INPUT_PORT_KIND. */
  public static final String CFG_INPUT_PORT_KIND = "cfg_in";

  /**
   * Default constructor.
   *
   * @param fp
   *          the feature provider
   */
  public AddConfigInputPortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Add Config. Input Port";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Add a configuration input port to the Actor";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.AbstractAddActorPortFeature#getPosition()
   */
  @Override
  public PortPosition getPosition() {
    return AddConfigInputPortFeature.CFG_INPUT_PORT_POSITION;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.AbstractAddActorPortFeature#addPortGA(org.eclipse.graphiti.mm.algorithms.
   * GraphicsAlgorithm)
   */
  @Override
  public GraphicsAlgorithm addPortGA(final GraphicsAlgorithm containerShape) {

    // Get the GaService
    final IGaService gaService = Graphiti.getGaService();
    // Create the port GraphicAlgorithm
    final int[] xy = { 0, 0, AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE,
        (AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE + 2) / 2, 0,
        AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE + 2 };
    final Polygon triangle = gaService.createPolygon(containerShape, xy);

    triangle.setForeground(manageColor(AddConfigInputPortFeature.CFG_INPUT_PORT_FOREGROUND));
    triangle.setBackground(manageColor(AddConfigInputPortFeature.CFG_INPUT_PORT_BACKGROUND));
    triangle.setLineWidth(0);
    return triangle;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.AbstractAddActorPortFeature#addPortLabel(org.eclipse.graphiti.mm.algorithms.
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
    final int portFontHeight = AbstractAddActorPortFeature.PORT_FONT_HEIGHT;
    text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
    gaService.setHeight(text, portFontHeight);

    return text;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.AbstractAddActorPortFeature#getNewPort(java.lang.String,
   * org.ietr.preesm.experiment.model.pimm.ExecutableActor)
   */
  @Override
  public Port getNewPort(final String portName, final ExecutableActor actor) {
    final ConfigInputPort newPort = PiMMUserFactory.instance.createConfigInputPort();
    newPort.setName(portName);
    actor.getConfigInputPorts().add(newPort);
    return newPort;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.AbstractAddActorPortFeature#getPortKind()
   */
  @Override
  public String getPortKind() {
    return AddConfigInputPortFeature.CFG_INPUT_PORT_KIND;
  }

}
