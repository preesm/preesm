/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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
package org.ietr.dftools.graphiti.ui.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ietr.dftools.graphiti.model.AbstractObject;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.editpolicies.DependencyEditPolicy;
import org.ietr.dftools.graphiti.ui.editpolicies.DependencyEndPointEditPolicy;
import org.ietr.dftools.graphiti.ui.figure.EdgeFigure;
import org.ietr.dftools.graphiti.ui.properties.ModelPropertySource;
import org.ietr.dftools.graphiti.ui.properties.PropertiesConstants;

/**
 * The EditPart associated to the Dependency gives methods to refresh the view when a property has changed.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 *
 */
public class EdgeEditPart extends AbstractConnectionEditPart
    implements PropertyChangeListener, ITabbedPropertySheetPageContributor {

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
   */
  @Override
  public void activate() {
    super.activate();
    ((Edge) getModel()).addPropertyChangeListener(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
   */
  @Override
  protected void createEditPolicies() {
    installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new DependencyEndPointEditPolicy());
    installEditPolicy(EditPolicy.CONNECTION_ROLE, new DependencyEditPolicy());
  }

  /**
   * Creates a new DependencyGef figure.
   *
   * @return the i figure
   */
  @Override
  protected IFigure createFigure() {
    final Edge edge = (Edge) getModel();
    return new EdgeFigure(edge);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
   */
  @Override
  public void deactivate() {
    super.deactivate();
    ((Edge) getModel()).removePropertyChangeListener(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#getAdapter(java.lang.Class)
   */
  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(final Class adapter) {
    if (adapter == IPropertySource.class) {
      return new ModelPropertySource((AbstractObject) getModel());
    }
    if (adapter == IPropertySheetPage.class) {
      return new TabbedPropertySheetPage(this);
    }
    return super.getAdapter(adapter);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
   */
  @Override
  public String getContributorId() {
    return PropertiesConstants.CONTRIBUTOR_ID;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    final String propertyName = evt.getPropertyName();
    if (propertyName.equals(ObjectType.PARAMETER_SOURCE_PORT)) {
      final VertexEditPart vertexEP = (VertexEditPart) getSource();
      vertexEP.propertyChange(new PropertyChangeEvent(this, Vertex.PROPERTY_SRC_VERTEX, null, null));
      // update anchors
      refresh();
    } else if (propertyName.equals(ObjectType.PARAMETER_TARGET_PORT)) {
      final VertexEditPart vertexEP = (VertexEditPart) getTarget();
      vertexEP.propertyChange(new PropertyChangeEvent(this, Vertex.PROPERTY_DST_VERTEX, null, null));
      // update anchors
      refresh();
    }

    // any parameter
    // (including ports in case they are also displayed on the edge)
    final EdgeFigure figure = (EdgeFigure) getFigure();
    figure.refresh(evt.getPropertyName(), evt.getNewValue());
  }
}
