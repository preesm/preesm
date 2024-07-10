/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
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
package org.preesm.ui.pisdf.diagram;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.ui.pisdf.decorators.ActorDecorators;
import org.preesm.ui.pisdf.decorators.DelayDecorators;
import org.preesm.ui.pisdf.decorators.ParameterDecorators;
import org.preesm.ui.pisdf.decorators.PiMMDecoratorAdapter;
import org.preesm.ui.pisdf.decorators.PortDecorators;
import org.preesm.ui.pisdf.features.ExchangePortCategory;
import org.preesm.ui.pisdf.features.ExchangePortDirection;
import org.preesm.ui.pisdf.features.MoveDownActorPortFeature;
import org.preesm.ui.pisdf.features.MoveUpActorPortFeature;
import org.preesm.ui.pisdf.features.OpenRefinementFeature;
import org.preesm.ui.pisdf.features.RenameAbstractVertexFeature;
import org.preesm.ui.pisdf.features.RenameActorPortFeature;
import org.preesm.ui.pisdf.layout.AutoLayoutFeature;

/**
 * {@link IToolBehaviorProvider} for the {@link Diagram} with type {@link PiMMDiagramTypeProvider}.
 *
 * @author kdesnos
 * @author jheulot
 *
 */
public class PiMMToolBehaviorProvider extends DefaultToolBehaviorProvider {

  /**
   * Store the message to display when a ga is under the mouse.
   */
  public final Map<GraphicsAlgorithm, String> toolTips;

  /** The decorator adapter. */
  public final PiMMDecoratorAdapter decoratorAdapter;

  /**
   * The default constructor of {@link PiMMToolBehaviorProvider}.
   *
   * @param diagramTypeProvider
   *          the {@link DiagramTypeWizardPage}
   */
  public PiMMToolBehaviorProvider(final IDiagramTypeProvider diagramTypeProvider) {
    super(diagramTypeProvider);
    this.toolTips = new LinkedHashMap<>();
    this.decoratorAdapter = new PiMMDecoratorAdapter();

    final IFeatureProvider featureProvider = getFeatureProvider();
    final Diagram diagram = diagramTypeProvider.getDiagram();

    checkVersion(diagramTypeProvider);

    final PiGraph piGraph = (PiGraph) featureProvider.getBusinessObjectForPictogramElement(diagram);
    if (!piGraph.eAdapters().contains(this.decoratorAdapter)) {
      piGraph.eAdapters().add(this.decoratorAdapter);
    }
  }

  private void checkVersion(final IDiagramTypeProvider diagramTypeProvider) {

    final Diagram diagram = diagramTypeProvider.getDiagram();
    final TreeIterator<EObject> eAllContents = diagram.eAllContents();
    while (eAllContents.hasNext()) {
      final EObject child = eAllContents.next();
      if (child instanceof final PictogramElement node) {
        final PictogramLink link = node.getLink();
        if (link != null) {
          final EList<EObject> businessObjects = link.getBusinessObjects();
          for (final EObject bo : businessObjects) {
            final boolean eIsProxy = bo.eIsProxy();
            if (eIsProxy) {
              final String title = "Warning: the diagram is linked to an old version of the PiSDF meta-model";
              final StringBuilder sb = new StringBuilder();
              sb.append("We found business objects in the diagram that are no longer part of the PiSDF format. ");
              sb.append("That means the links from the diagram file to the PiSDF file are obsolete. ");
              sb.append("This can cause issues when exploring or manipulating the application specification. ");
              sb.append("\n\n");
              sb.append("For this reason, editing this graph is disabled, and display might even fail.");
              sb.append("\n\n");
              sb.append("This can be fixed by generating a new diagram file from the pi file (note that the layout "
                  + "will be recomputed). ");
              sb.append("To do so, right click on the pi file, then on \"Preem / Generate .diagram\".");

              MessageDialog.openWarning(null, title, sb.toString());

              final IFeatureProvider featureProvider = diagramTypeProvider.getFeatureProvider();
              if (featureProvider instanceof final PiMMFeatureProvider pfp) {
                pfp.setEditable(false);
              }
              return;
            }
          }
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.tb.DefaultToolBehaviorProvider#getDecorators(org.eclipse.graphiti.mm.pictograms.
   * PictogramElement)
   */
  @Override
  public IDecorator[] getDecorators(final PictogramElement pe) {
    final IFeatureProvider featureProvider = getFeatureProvider();
    final IDecorator[] existingDecorators = this.decoratorAdapter.getPesAndDecorators().get(pe);
    if (existingDecorators != null) {
      return existingDecorators;
    }
    final Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
    IDecorator[] result = null;
    if (bo instanceof ExecutableActor) {
      result = decorateActor(pe, bo);
    }

    if (bo instanceof final Parameter param && !param.isConfigurationInterface()) {
      result = ParameterDecorators.getDecorators(param, pe);
      this.decoratorAdapter.getPesAndDecorators().put(pe, result);
    }

    if (bo instanceof final Delay delayPo) {
      result = DelayDecorators.getDecorators(delayPo, pe);
      this.decoratorAdapter.getPesAndDecorators().put(pe, result);
    }

    if (result == null) {
      result = super.getDecorators(pe);
    }

    this.decoratorAdapter.getPesAndDecorators().put(pe, result);
    return result;
  }

  private IDecorator[] decorateActor(final PictogramElement pe, final Object bo) {

    // Add decorators for each ports of the actor
    final List<IDecorator> decorators = new ArrayList<>();
    for (final Anchor a : ((ContainerShape) pe).getAnchors()) {
      for (final Object pbo : a.getLink().getBusinessObjects()) {
        if (pbo instanceof final Port port) {
          for (final IDecorator d : PortDecorators.getDecorators(port, a)) {
            decorators.add(d);
          }
        }
      }
    }

    if (bo instanceof final Actor actorBo) {
      // Add decorators to the actor itself
      for (final IDecorator d : ActorDecorators.getDecorators(actorBo, pe)) {
        decorators.add(d);
      }
    }

    final IDecorator[] result = new IDecorator[decorators.size()];
    decorators.toArray(result);
    this.decoratorAdapter.getPesAndDecorators().put(pe, result);
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.tb.DefaultToolBehaviorProvider#getDoubleClickFeature(org.eclipse.graphiti.features.context.
   * IDoubleClickContext)
   */
  @Override
  public ICustomFeature getDoubleClickFeature(final IDoubleClickContext context) {
    final ICustomFeature customFeature = new OpenRefinementFeature(getFeatureProvider());

    // canExecute() tests especially if the context contains a Actor with a
    // valid refinement
    if (customFeature.canExecute(context)) {
      return customFeature;
    }

    return super.getDoubleClickFeature(context);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.tb.DefaultToolBehaviorProvider#getToolTip(org.eclipse.graphiti.mm.algorithms.
   * GraphicsAlgorithm)
   */
  @Override
  public String getToolTip(final GraphicsAlgorithm ga) {
    return this.toolTips.get(ga);
  }

  /**
   * Set the tooltip message for a given {@link GraphicsAlgorithm}.
   *
   * @param ga
   *          the {@link GraphicsAlgorithm}
   * @param toolTip
   *          the tooltip message to display
   */
  public void setToolTip(final GraphicsAlgorithm ga, final String toolTip) {
    this.toolTips.put(ga, toolTip);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.tb.DefaultToolBehaviorProvider#getCommandFeature(org.eclipse.graphiti.features.context.impl.
   * CustomContext, java.lang.String)
   */
  @Override
  public ICustomFeature getCommandFeature(final CustomContext context, final String hint) {
    final PictogramElement[] pes = context.getPictogramElements();
    if (pes.length > 0) {
      switch (hint) {
        case ExchangePortCategory.HINT:
          return new ExchangePortCategory(getFeatureProvider());
        case ExchangePortDirection.HINT:
          return new ExchangePortDirection(getFeatureProvider());
        case MoveUpActorPortFeature.HINT:
          return new MoveUpActorPortFeature(getFeatureProvider());
        case MoveDownActorPortFeature.HINT:
          return new MoveDownActorPortFeature(getFeatureProvider());
        case AutoLayoutFeature.HINT:
          return new AutoLayoutFeature(getFeatureProvider());
        case RenameActorPortFeature.HINT:
          final Object obj = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pes[0]);
          if (obj instanceof Port) {
            return new RenameActorPortFeature(getFeatureProvider());
          }
          if (obj instanceof AbstractVertex) {
            return new RenameAbstractVertexFeature(getFeatureProvider());
          }
          break;
        default:
      }
    }
    return super.getCommandFeature(context, hint);
  }

  @Override
  public boolean equalsBusinessObjects(final Object o1, final Object o2) {
    boolean equalsBusinessObjects = super.equalsBusinessObjects(o1, o2);
    if ((o1 instanceof final ConfigInputPort cip1) && (o2 instanceof final ConfigInputPort cip2)) {
      equalsBusinessObjects &= super.equalsBusinessObjects(cip1.eContainer(), cip2.eContainer());
    }
    return equalsBusinessObjects;
  }
}
