/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
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
package org.ietr.preesm.ui.pimm.diagram;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.MarkerHelper;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.ui.util.EditUIMarkerHelper;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.internal.T;
import org.eclipse.swt.widgets.Display;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiResourceImpl;
import org.ietr.preesm.pimm.algorithm.checker.PiMMAlgorithmChecker;
import org.ietr.preesm.ui.PreesmUIPlugin;

/**
 * Class inheriting from the {@link DefaultMarkerBehavior}. This class was created to define a custom {@link DefaultMarkerBehavior} that does not reset problems
 * related to graphs on startup of the editor.
 *
 * @author kdesnos
 *
 */
public class PiMMMarkerBehavior extends DefaultMarkerBehavior {

  /**
   * Map to store the diagnostic associated with a resource.
   */
  protected Map<Resource, Diagnostic> resourceToDiagnosticMap = new LinkedHashMap<>();

  /**
   * The marker helper instance is responsible for creating workspace resource markers presented in Eclipse's Problems View.
   */
  private final MarkerHelper markerHelper = new PiMMMarkerHelper();

  /**
   * Controls whether the problem indication should be updated.
   */
  protected boolean updateProblemIndication = true;

  /**
   * Default constructor.
   *
   * @param diagramBehavior
   *          the diagram behavior
   */
  public PiMMMarkerBehavior(final DiagramBehavior diagramBehavior) {
    super(diagramBehavior);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior#initialize()
   */
  @Override
  public void initialize() {
    this.diagramBehavior.getResourceSet().eAdapters().add(this.pimmAdapter);
    super.initialize();
    super.disableProblemIndicationUpdate();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior#enableProblemIndicationUpdate()
   */
  @Override
  public void enableProblemIndicationUpdate() {
    this.updateProblemIndication = true;
    super.enableProblemIndicationUpdate();
    refreshProblemIndication();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior#disableProblemIndicationUpdate()
   */
  @Override
  public void disableProblemIndicationUpdate() {
    this.updateProblemIndication = false;
    super.disableProblemIndicationUpdate();
  }

  /**
   * Check pi resource problems.
   *
   * @param resource
   *          the resource
   * @return the diagnostic
   */
  public Diagnostic checkPiResourceProblems(final Resource resource) {
    // Check for errors before saving
    final PiMMAlgorithmChecker checker = new PiMMAlgorithmChecker();

    // Get the PiGraph resource
    if (resource instanceof PiResourceImpl) {
      final BasicDiagnostic result = new BasicDiagnostic();
      try {
        final Diagram diagram = this.diagramBehavior.getDiagramContainer().getDiagramTypeProvider().getDiagram();
        if (!resource.getContents().isEmpty() && !checker.checkGraph((PiGraph) resource.getContents().get(0))) {
          // Warnings
          for (final Entry<String, EObject> msgs : checker.getWarningMsgs().entrySet()) {
            final String msg = msgs.getKey();
            final List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram, msgs.getValue());
            final PictogramElement pictogramElement = pes.get(0);
            final String uriFragment = pictogramElement.eResource().getURIFragment(pictogramElement);
            final BasicDiagnostic d = new BasicDiagnostic(org.eclipse.emf.common.util.Diagnostic.WARNING, PreesmUIPlugin.PLUGIN_ID, 0, msg,
                new Object[] { pictogramElement, uriFragment });

            result.add(d);
          }

          // Errors
          for (final Entry<String, EObject> msgs : checker.getErrorMsgs().entrySet()) {
            final String msg = msgs.getKey();
            final List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram, msgs.getValue());
            final PictogramElement pictogramElement = pes.get(0);
            final String uriFragment = pictogramElement.eResource().getURIFragment(pictogramElement);
            final BasicDiagnostic d = new BasicDiagnostic(org.eclipse.emf.common.util.Diagnostic.ERROR, PreesmUIPlugin.PLUGIN_ID, 0, msg,
                new Object[] { pictogramElement, uriFragment });

            result.add(d);
          }
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
      return result;
    }

    return Diagnostic.OK_INSTANCE;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior#dispose()
   */
  @Override
  public void dispose() {
    super.dispose();
    this.resourceToDiagnosticMap.clear();
    this.resourceToDiagnosticMap = null;
  }

  /**
   * Updates the problems indication markers in the editor. The default implementation used an EMF {@link BasicDiagnostic} to do the checks and
   * {@link EditUIMarkerHelper} to check and set markers for {@link EObject}s.
   *
   * <p>
   * Method copied from {@link DefaultMarkerBehavior} (because it is a private method)
   * </p>
   */
  void refreshProblemIndication() {
    if (this.diagramBehavior == null) {
      // Already disposed
      return;
    }
    final TransactionalEditingDomain editingDomain = this.diagramBehavior.getEditingDomain();
    if (this.updateProblemIndication && (editingDomain != null)) {
      final ResourceSet resourceSet = editingDomain.getResourceSet();
      final BasicDiagnostic diagnostic = new BasicDiagnostic(Diagnostic.OK, PreesmUIPlugin.PLUGIN_ID, 0, null, new Object[] { resourceSet });
      for (final Diagnostic childDiagnostic : this.resourceToDiagnosticMap.values()) {
        if (childDiagnostic.getSeverity() != Diagnostic.OK) {
          diagnostic.add(childDiagnostic);
        }
      }
      if (this.markerHelper.hasMarkers(resourceSet)) {
        this.markerHelper.deleteMarkers(resourceSet);
      }
      if (diagnostic.getSeverity() != Diagnostic.OK) {
        try {
          this.markerHelper.createMarkers(diagnostic);
          T.racer().info(diagnostic.toString());
        } catch (final CoreException exception) {
          T.racer().error(exception.getMessage(), exception);
        }
      }
    }
  }

  /**
   * Adapter used to update the problem indication when resources are demanded loaded.
   *
   * <p>
   * Class adapted from {@link DefaultMarkerBehavior} (because it is a private class)
   * </p>
   */
  protected EContentAdapter pimmAdapter = new EContentAdapter() {
    @Override
    public void notifyChanged(final Notification notification) {
      if (notification.getNotifier() instanceof PiResourceImpl) {
        switch (notification.getFeatureID(Resource.class)) {
          case Resource.RESOURCE__IS_LOADED:
          case Resource.RESOURCE__IS_MODIFIED:
            notify(notification);
            break;
          default:
            // nothing
        }
      } else {
        super.notifyChanged(notification);
      }
    }

    private void notify(final Notification notification) {
      final Resource resource = (Resource) notification.getNotifier();
      final Diagnostic diagnostic = checkPiResourceProblems(resource);
      if (diagnostic.getSeverity() != Diagnostic.OK) {
        PiMMMarkerBehavior.this.resourceToDiagnosticMap.put(resource, diagnostic);
      } else {
        PiMMMarkerBehavior.this.resourceToDiagnosticMap.remove(resource);
      }

      if (PiMMMarkerBehavior.this.updateProblemIndication) {
        // Display.getDefault().asyncExec(() -> refreshProblemIndication())
        Display.getDefault().asyncExec(PiMMMarkerBehavior.this::refreshProblemIndication);
      }
    }

    @Override
    protected void setTarget(final Resource target) {
      basicSetTarget(target);
    }

    @Override
    protected void unsetTarget(final Resource target) {
      basicUnsetTarget(target);
    }

  };
}
