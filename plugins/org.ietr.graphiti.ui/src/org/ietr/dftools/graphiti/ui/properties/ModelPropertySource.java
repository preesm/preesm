/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2011)
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
package org.ietr.dftools.graphiti.ui.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ietr.dftools.graphiti.GraphitiException;
import org.ietr.dftools.graphiti.model.AbstractObject;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Parameter;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.commands.ParameterChangeValueCommand;
import org.ietr.dftools.graphiti.ui.editors.GraphEditor;

/**
 * This class implements a property source for the different objects of our model.
 *
 * @author Matthieu Wipliez
 *
 */
public class ModelPropertySource implements IPropertySource, PropertyChangeListener {

  /** The descs. */
  private final IPropertyDescriptor[] descs;

  /** The do refresh. */
  private boolean doRefresh;

  /** The model. */
  private final AbstractObject model;

  /** The type. */
  private final ObjectType type;

  /**
   * Instantiates a new model property source.
   *
   * @param model
   *          the model
   */
  public ModelPropertySource(final AbstractObject model) {
    this.model = model;
    model.addPropertyChangeListener(this);
    this.type = model.getType();

    final List<IPropertyDescriptor> descriptionList = new ArrayList<>();
    for (final Parameter parameter : this.type.getParameters()) {
      if (!((parameter.getType() == List.class) || (parameter.getType() == Map.class))) {
        final String name = parameter.getName();
        final TextPropertyDescriptor desc = new TextPropertyDescriptor(name, name);
        descriptionList.add(desc);
      }
    }

    this.descs = descriptionList.toArray(new IPropertyDescriptor[0]);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
   */
  @Override
  public Object getEditableValue() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    return this.descs;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
   */
  @Override
  public Object getPropertyValue(final Object id) {
    final Object value = this.model.getValue((String) id);
    if (value == null) {
      return "";
    }
    return String.valueOf(value);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
   */
  @Override
  public boolean isPropertySet(final Object id) {
    final Object value = this.model.getValue((String) id);
    final Object defaultValue = this.model.getParameter((String) id).getDefault();
    return value != defaultValue;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    if (!this.doRefresh) {
      return;
    }

    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

    // show properties
    try {
      final IViewPart part = page.showView(IPageLayout.ID_PROP_SHEET);
      if (part instanceof PropertySheet) {
        final IPropertySheetPage propPage = (IPropertySheetPage) ((PropertySheet) part).getCurrentPage();
        if (propPage instanceof PropertySheetPage) {
          ((PropertySheetPage) propPage).refresh();
        } else if (propPage instanceof TabbedPropertySheetPage) {
          ((TabbedPropertySheetPage) propPage).refresh();
        }
      }
    } catch (final PartInitException e) {
      throw new GraphitiException("Could not change property", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
   */
  @Override
  public void resetPropertyValue(final Object id) {
    Object defaultValue = this.model.getParameter((String) id).getDefault();
    if (defaultValue == null) {
      defaultValue = "";
    }
    this.model.setValue((String) id, defaultValue);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
   */
  @Override
  public void setPropertyValue(final Object id, Object value) {
    Graph graph;
    if (this.model instanceof Vertex) {
      graph = ((Vertex) this.model).getParent();
    } else if (this.model instanceof Edge) {
      graph = ((Edge) this.model).getParent();
    } else {
      graph = (Graph) this.model;
    }

    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
    try {
      final IEditorPart part = IDE.openEditor(page, graph.getFile());
      if (part instanceof GraphEditor) {
        final String parameterName = (String) id;

        // only update value if it is different than before
        final Object oldValue = this.model.getValue(parameterName);
        final String str = (String) value;
        if (((oldValue == null) && str.isEmpty()) || String.valueOf(oldValue).equals(value)) {
          return;
        }

        final ParameterChangeValueCommand command = new ParameterChangeValueCommand(this.model, "Change value");
        final Class<?> parameterType = this.model.getParameter((String) id).getType();
        if (str.isEmpty()) {
          // get default value
          value = this.model.getParameter(parameterName).getDefault();
        } else {
          value = parsePropertyValue(value, str, parameterType);
        }
        command.setValue(parameterName, value);
        this.doRefresh = false;
        ((GraphEditor) part).executeCommand(command);
        this.doRefresh = true;
      }
    } catch (final PartInitException e) {
      throw new GraphitiException("Could not set property", e);
    }
  }

  private Object parsePropertyValue(Object value, final String str, final Class<?> parameterType) {
    try {
      if (parameterType == Integer.class) {
        value = Integer.valueOf(str);
      } else if (parameterType == Float.class) {
        value = Float.valueOf(str);
      } else if (parameterType == Boolean.class) {
        if (!"true".equals(value) && !"false".equals(value)) {
          throw new IllegalArgumentException();
        }
        value = Boolean.valueOf(str);
      }
    } catch (final RuntimeException e) {
      value = "invalid \"" + value + "\" value for " + parameterType.getSimpleName();
    }
    return value;
  }

}
