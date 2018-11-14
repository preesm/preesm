/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2014)
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
package org.ietr.preesm.ui.pimm.features;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiMMPackage;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;

/**
 * Layout Feature for Actors.
 *
 * @author kdesnos
 */
public class LayoutActorFeature extends AbstractLayoutFeature {

  /**
   *
   */
  public static final int INITIAL_GAP = 5;
  /**
   *
   */
  public static final int BOTTOM_GAP  = 3;
  /**
   * Gap between ports in pixels
   */
  public static final int PORT_GAP    = 2;

  /**
   * Default Constructor of the {@link LayoutActorFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public LayoutActorFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ILayout#canLayout(org.eclipse.graphiti.features.context.ILayoutContext)
   */
  @Override
  public boolean canLayout(final ILayoutContext context) {
    // return true, if pictogram element is linked to an Actor
    final PictogramElement pe = context.getPictogramElement();
    if (!(pe instanceof ContainerShape)) {
      return false;
    }

    final EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
    return (businessObjects.size() == 1) && (businessObjects.get(0) instanceof ExecutableActor);
  }

  /**
   * Return the new Height of the shape. <br>
   * <br>
   *
   * <p>
   * The new height is computed so that all text are completely visible. Consequently, the method check the width of all
   * Text children shape.<br>
   * <br>
   * <b> The method does not apply the new height to the shape. </b> Use {@link LayoutActorFeature#setNewHeight} for
   * that purpose.
   * </p>
   *
   * @param childrenShapes
   *          the children shapes of the Actor
   * @param anchorShapes
   *          the anchor shapes of the actor
   * @return the new height
   */
  protected int getNewHeight(final EList<Shape> childrenShapes, final EList<Anchor> anchorShapes) {
    // RETRIEVE THE NAME HEIGHT
    int nameHeight = 0;
    {
      // Scan the children shape looking for the actor name
      for (final Shape shape : childrenShapes) {
        final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
        // The name should be the only children with type text
        if (child instanceof Text) {
          final String text = ((Text) child).getValue();
          final Font font = ((Text) child).getFont();

          // Retrieve the size of the text
          final IDimension size = GraphitiUi.getUiLayoutService().calculateTextSize(text, font);
          // Retrieve the space of the name
          // (+ port gap to add space and lighten the actor representation)
          nameHeight = size.getHeight() + LayoutActorFeature.PORT_GAP;
        }
      }
    }

    // RETRIEVE THE ANCHOR HEIGHT
    int anchorMaxHeight = 0;
    {
      int inputsHeight = 0;
      int outputsHeight = 0;
      int cfgInputsHeight = 0;
      int cfgOutputsHeight = 0;
      for (final Anchor anchor : anchorShapes) {
        // Invisible anchors added to actors in order to start
        // connections without ports do not have any GraphicAlgorithm
        // Only process anchors with a GraphicAlgorithm
        if (anchor.getGraphicsAlgorithm() != null) {
          // Retrieve the children of the invisible rectangle of the
          // anchor
          final EList<GraphicsAlgorithm> anchorChildren = anchor.getGraphicsAlgorithm().getGraphicsAlgorithmChildren();

          // Scan the children of the invisible rectangle looking for
          // the label
          for (final GraphicsAlgorithm child : anchorChildren) {
            // The Label of the anchor should be the only child with
            // type Text
            if (child instanceof Text) {
              // Retrieve the size of the text
              final String text = ((Text) child).getValue();
              final Font font = ((Text) child).getFont();
              final IDimension size = GraphitiUi.getUiLayoutService().calculateTextSize(text, font);
              // Write the port font height in
              // AbstractAddActorPortFeature. This is needed when
              // opening a saved graph because in such case
              // PORT_FONT_HEIGHT will remain equal to 0 until a
              // port
              // is added to the graph
              AbstractAddActorPortFeature.PORT_FONT_HEIGHT = size.getHeight();
              final EObject obj = (EObject) getBusinessObjectForPictogramElement(anchor);

              switch (obj.eClass().getClassifierID()) {
                case PiMMPackage.CONFIG_INPUT_PORT:
                  cfgInputsHeight += size.getHeight() + LayoutActorFeature.PORT_GAP;
                  break;
                case PiMMPackage.CONFIG_OUTPUT_PORT:
                  cfgOutputsHeight += size.getHeight() + LayoutActorFeature.PORT_GAP;
                  break;

                case PiMMPackage.DATA_INPUT_PORT:
                  inputsHeight += size.getHeight() + LayoutActorFeature.PORT_GAP;
                  break;
                case PiMMPackage.DATA_OUTPUT_PORT:
                  outputsHeight += size.getHeight() + LayoutActorFeature.PORT_GAP;
                  break;
                default:
              }
            }
          }

        }
      }
      anchorMaxHeight = Math.max(cfgInputsHeight, cfgOutputsHeight) + Math.max(inputsHeight, outputsHeight);
    }

    return anchorMaxHeight + nameHeight + LayoutActorFeature.INITIAL_GAP + LayoutActorFeature.BOTTOM_GAP;
  }

  /**
   * Return the new width of the shape. <br>
   * <br>
   *
   * <p>
   * The new width is computed so that all text are completely visible. Consequently, the method check the width of all
   * Text children shape. <br>
   * <br>
   * <b> The method does not apply the new width to the shape. </b> Use {@link LayoutActorFeature#setNewWidth} for that
   * purpose.
   * </p>
   *
   * @param childrenShapes
   *          the children shapes of the Actor
   * @param anchorShapes
   *          the anchor shapes of the actor
   * @return the new width
   */
  protected int getNewWidth(final EList<Shape> childrenShapes, final EList<Anchor> anchorShapes) {
    // RETRIEVE THE NAME WIDTH
    int nameWidth = 0;
    {
      // Scan the children shape looking for the actor name
      for (final Shape shape : childrenShapes) {
        final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
        // The name should be the only children with type text
        if (child instanceof Text) {
          final String text = ((Text) child).getValue();
          final Font font = ((Text) child).getFont();

          // Retrieve the size of the text
          final IDimension size = GraphitiUi.getUiLayoutService().calculateTextSize(text, font);

          // Retrieve the space of the name
          // (+30 to add space and lighten the actor representation)
          // And allow the addition of a decorator
          nameWidth = size.getWidth() + 30;

        }
      }
    }

    // RETRIEVE THE ANCHOR WIDTH
    int anchorWidth = 0;
    {
      int inputMaxWidth = 0;
      int outputMaxWidth = 0;

      // Retrieve a few constants
      final int gaSize = AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE;
      final int labelGASpace = AbstractAddActorPortFeature.PORT_LABEL_GA_SPACE;

      for (final Anchor anchor : anchorShapes) {
        // Invisible anchors added to actors in order to start
        // connections without ports do not have any GraphicAlgorithm
        // Only process anchors with a GraphicAlgorithm
        if (anchor.getGraphicsAlgorithm() != null) {
          // Retrieve the children of the invisible rectangle of the
          // anchor
          final EList<GraphicsAlgorithm> anchorChildren = anchor.getGraphicsAlgorithm().getGraphicsAlgorithmChildren();

          // Scan the children of the invisible rectangle looking for
          // the label
          for (final GraphicsAlgorithm child : anchorChildren) {
            // The Label of the anchor should be the only child with
            // type Text
            if (child instanceof Text) {
              // Retrieve the size of the text
              final String text = ((Text) child).getValue();
              final Font font = ((Text) child).getFont();
              final IDimension size = GraphitiUi.getUiLayoutService().calculateTextSize(text, font);

              if (((BoxRelativeAnchor) anchor).getRelativeWidth() == 0.0) {
                // This is an input port
                inputMaxWidth = Math.max(size.getWidth() + gaSize + labelGASpace, inputMaxWidth);
              } else {
                // This is an output port
                outputMaxWidth = Math.max(size.getWidth() + gaSize + labelGASpace, outputMaxWidth);
              }
            }
          }
        }
      }
      anchorWidth = inputMaxWidth + outputMaxWidth;
      // We add an extra space of 8 between inputs and output ports to
      // lighten the actor pictogram
      anchorWidth += 8;
    }

    return Math.max(anchorWidth, nameWidth);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ILayout#layout(org.eclipse.graphiti.features.context.ILayoutContext)
   */
  @Override
  public boolean layout(final ILayoutContext context) {
    boolean anythingChanged = false;

    // Retrieve the shape and the graphic algorithm
    final ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
    final GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();

    // Retrieve all contained shapes
    final EList<Shape> childrenShapes = containerShape.getChildren();
    final EList<Anchor> anchorShapes = containerShape.getAnchors();

    // Get the new Width of the shape
    final int newWidth = getNewWidth(childrenShapes, anchorShapes);

    // Apply change if newWidth is different from the current
    if (newWidth != containerGa.getWidth()) {
      setNewWidth(newWidth, childrenShapes);
      containerGa.setWidth(newWidth);
      anythingChanged = true;
    }

    // Get the new Height of the shape
    final int newHeight = getNewHeight(childrenShapes, anchorShapes);
    // Apply change (always since it will organize ports even if the shape
    // height did not change)
    setNewHeight(newHeight, childrenShapes, anchorShapes);
    containerGa.setHeight(newHeight);

    final EObject bo = containerShape.getLink().getBusinessObjects().get(0);
    if (bo instanceof Actor) {
      final Actor actor = (Actor) bo;
      layoutActor(actor, childrenShapes, containerGa);
    } else if (bo instanceof ExecutableActor) {
      layoutSpecialActor((ExecutableActor) bo, childrenShapes, containerGa);
    }

    // If Anything changed, call the move feature to layout connections
    if (anythingChanged) {
      final MoveAbstractActorFeature moveFeature = new MoveAbstractActorFeature(getFeatureProvider());
      final MoveShapeContext moveCtxt = new MoveShapeContext(containerShape);
      moveCtxt.setDeltaX(0);
      moveCtxt.setDeltaY(0);
      final ILocation csLoc = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(containerShape);
      moveCtxt.setLocation(csLoc.getX(), csLoc.getY());
      moveFeature.moveShape(moveCtxt);
    }

    return anythingChanged;
  }

  /**
   * Layout special actor.
   *
   * @param ea
   *          the ea
   * @param childrenShapes
   *          the children shapes
   * @param containerGa
   *          the container ga
   */
  private void layoutSpecialActor(final ExecutableActor ea, final EList<Shape> childrenShapes,
      final GraphicsAlgorithm containerGa) {
    final IColorConstant backgroundColor;
    final IColorConstant foregroundColor;
    if (ea instanceof BroadcastActor) {
      backgroundColor = AddBroadcastActorFeature.BROADCAST_ACTOR_BACKGROUND;
      foregroundColor = AddBroadcastActorFeature.BROADCAST_ACTOR_FOREGROUND;
    } else if (ea instanceof JoinActor) {
      backgroundColor = AddJoinActorFeature.JOIN_ACTOR_BACKGROUND;
      foregroundColor = AddJoinActorFeature.JOIN_ACTOR_FOREGROUND;
    } else if (ea instanceof ForkActor) {
      backgroundColor = AddForkActorFeature.FORK_ACTOR_BACKGROUND;
      foregroundColor = AddForkActorFeature.FORK_ACTOR_FOREGROUND;
    } else if (ea instanceof RoundBufferActor) {
      backgroundColor = AddRoundBufferActorFeature.ROUND_BUFFER_ACTOR_BACKGROUND;
      foregroundColor = AddRoundBufferActorFeature.ROUND_BUFFER_ACTOR_FOREGROUND;
    } else {
      backgroundColor = IColorConstant.WHITE;
      foregroundColor = IColorConstant.BLACK;
    }
    for (final Shape shape : childrenShapes) {
      final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
      final IGaService gaService = Graphiti.getGaService();
      if (child instanceof Text) {
        ((Text) child).setFont(gaService.manageDefaultFont(getDiagram(), false, true));
      }
    }
    final RoundedRectangle roundedRectangle = (RoundedRectangle) containerGa;
    roundedRectangle.setBackground(manageColor(backgroundColor));
    roundedRectangle.setForeground(manageColor(foregroundColor));
    roundedRectangle.setLineWidth(2);
  }

  /**
   * Layout actor.
   *
   * @param actor
   *          the actor
   * @param childrenShapes
   *          the children shapes
   * @param containerGa
   *          the container ga
   */
  private void layoutActor(final Actor actor, final EList<Shape> childrenShapes, final GraphicsAlgorithm containerGa) {
    final Refinement refinement = actor.getRefinement();
    final boolean isHactor = (refinement != null) && (refinement.getFilePath() != null)
        && refinement.getFilePath().getFileExtension().equals("pi");

    final IColorConstant bgColor = isHactor ? AddActorFeature.HIERARCHICAL_ACTOR_BACKGROUND
        : AddActorFeature.ACTOR_BACKGROUND;
    final IColorConstant fgColor = isHactor ? AddActorFeature.HIERARCHICAL_ACTOR_FOREGROUND
        : AddActorFeature.ACTOR_FOREGROUND;

    for (final Shape shape : childrenShapes) {
      final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
      final IGaService gaService = Graphiti.getGaService();
      if (child instanceof Text) {
        ((Text) child).setFont(gaService.manageDefaultFont(getDiagram(), isHactor, true));
      }
    }
    final RoundedRectangle roundedRectangle = (RoundedRectangle) containerGa;
    roundedRectangle.setBackground(manageColor(bgColor));
    roundedRectangle.setForeground(manageColor(fgColor));
    roundedRectangle.setLineWidth(2);
  }

  /**
   * Apply the new height of the shape.
   *
   * @param newHeigt
   *          the new height to apply
   * @param childrenShapes
   *          the children shape (contain the name)
   * @param anchorShapes
   *          the anchors of the shape
   * @return true if something was changed
   */
  protected boolean setNewHeight(final int newHeigt, final EList<Shape> childrenShapes,
      final EList<Anchor> anchorShapes) {
    boolean anythingChanged = false;

    // No need to change the height of the name

    // Retrieve and separate the inputs and outputs ports
    final List<BoxRelativeAnchor> inputs = new ArrayList<>();
    final List<BoxRelativeAnchor> outputs = new ArrayList<>();
    int nbConfigInput = 0;
    int nbConfigOutput = 0;
    for (final Anchor anchor : anchorShapes) {

      if (getBusinessObjectForPictogramElement(anchor) instanceof ConfigInputPort) {
        inputs.add(nbConfigInput, (BoxRelativeAnchor) anchor);
        nbConfigInput++;
      } else if (getBusinessObjectForPictogramElement(anchor) instanceof DataInputPort) {
        inputs.add((BoxRelativeAnchor) anchor);
      }

      if (getBusinessObjectForPictogramElement(anchor) instanceof ConfigOutputPort) {
        outputs.add(nbConfigOutput, (BoxRelativeAnchor) anchor);
        nbConfigOutput++;
      } else if (getBusinessObjectForPictogramElement(anchor) instanceof DataOutputPort) {
        // The else is important because a ConfigOutputPort IS an OutputPort
        outputs.add((BoxRelativeAnchor) anchor);
      }
    }
    final int maxNbConfigPort = Math.max(nbConfigInput, nbConfigOutput);

    // Place the inputs
    final int portFontHeight = AbstractAddActorPortFeature.PORT_FONT_HEIGHT;
    // The first port is placed below the name
    int y = portFontHeight + LayoutActorFeature.PORT_GAP + LayoutActorFeature.INITIAL_GAP;
    for (int i = 0; i < inputs.size(); i++) {
      final int configSpace = (i < nbConfigInput) ? 0 : maxNbConfigPort - nbConfigInput;
      final double relativeHeight = (y + ((i + configSpace) * (portFontHeight + LayoutActorFeature.PORT_GAP)))
          / (double) newHeigt;
      if (inputs.get(i).getRelativeHeight() != relativeHeight) {
        anythingChanged = true;
        inputs.get(i).setRelativeHeight(relativeHeight);
      }

    }

    // Place the outputs
    y = portFontHeight + LayoutActorFeature.PORT_GAP + LayoutActorFeature.INITIAL_GAP;
    for (int i = 0; i < outputs.size(); i++) {
      final int configSpace = (i < nbConfigOutput) ? 0 : maxNbConfigPort - nbConfigOutput;
      final double relativeHeight = (y + ((i + configSpace) * (portFontHeight + LayoutActorFeature.PORT_GAP)))
          / (double) newHeigt;
      if (outputs.get(i).getRelativeHeight() != relativeHeight) {
        anythingChanged = true;
        outputs.get(i).setRelativeHeight(relativeHeight);
      }
    }

    return anythingChanged;
  }

  /**
   * Apply the new width of the shape.
   *
   * @param newWidth
   *          the new width of the actor
   * @param childrenShapes
   *          the children shapes to resize
   */
  protected void setNewWidth(final int newWidth, final EList<Shape> childrenShapes) {
    // Scan the children shapes
    for (final Shape shape : childrenShapes) {
      final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
      if (child instanceof Text) {
        final Orientation align = ((Text) child).getHorizontalAlignment();

        // If the text is the name of the object
        if (align == Orientation.ALIGNMENT_CENTER) {
          // The name is centered and has the same width as the actor
          // rounded rectangle
          child.setWidth(newWidth);
        }
      }
    }

    // No need to layout the ports width (for now)
  }
}
