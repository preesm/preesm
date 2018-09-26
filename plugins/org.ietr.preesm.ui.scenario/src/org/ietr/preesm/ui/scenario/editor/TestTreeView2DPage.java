/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.preesm.ui.scenario.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.ui.scenario.editor.simulation.OperatorCheckStateListener;

/**
 *
 * @author anmorvan
 *
 */
public class TestTreeView2DPage extends FormPage implements IPropertyListener {

  private final PreesmScenario scenario;

  public TestTreeView2DPage(final PreesmScenario scenario, final FormEditor editor, final String id,
      final String title) {
    super(editor, id, title);
    this.scenario = scenario;
  }

  @Override
  protected void createFormContent(final IManagedForm managedForm) {

    // Creates the section
    final ScrolledForm form = managedForm.getForm();
    final FormToolkit toolkit = managedForm.getToolkit();
    form.setText(Messages.getString("Simulation.title"));
    final GridLayout layout = new GridLayout(1, true);
    layout.verticalSpacing = 10;
    form.getBody().setLayout(layout);

    final String title = "Test Tree View";
    final String desc = "Tree view for testing having hierarchical configuration of list of check boxes";

    final Section section = toolkit.createSection(form.getBody(),
        ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR 
        | Section.DESCRIPTION | ExpandableComposite.EXPANDED);
    section.setText(title);
    section.setDescription(desc);
    toolkit.createCompositeSeparator(section);
    final Composite container = toolkit.createComposite(section);

    container.setLayout(new FillLayout());
    section.setClient(container);
    section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

    createOperatorTreeSection(container, toolkit, this);

    managedForm.refresh();
    managedForm.reflow(true);
  }

  final List<String> eventNames = Arrays.asList("Event 1", "Event 2", "Event 3", "Event 4", "Event 54", "perf", "miss");

  /**
   *
   * @author anmorvan
   *
   */
  enum EventMonitorStatus {
    // do not monitor the event
    SKIP,

    // monitor that event if the processing element supports it
    MAY_MONITOR,

    // add a constraint in the scheduler/mapper for enforcing the actor to be mapped
    // on a processing element that supports the event
    MUST_MONITOR;

    EventMonitorStatus next() {
      switch (this) {
        case SKIP:
          return MAY_MONITOR;
        case MAY_MONITOR:
          return MUST_MONITOR;
        case MUST_MONITOR:
          return SKIP;
        default:
          return null;
      }
    }
  }

  /**
   *
   * @author anmorvan
   *
   */
  abstract class TreeElement {
    String                          label;
    Node                            parent;
    Map<String, EventMonitorStatus> eventMonitoringStatuses;

    TreeElement(final String label) {
      this.label = label;
      this.eventMonitoringStatuses = new LinkedHashMap<>();
    }

    @Override
    public String toString() {
      return this.label;
    }
  }

  /**
   *
   * @author anmorvan
   *
   */
  class Node extends TreeElement {
    List<TreeElement> children;

    Node(final String label, final List<? extends TreeElement> children) {
      super(label);
      this.eventMonitoringStatuses.put(TestTreeView2DPage.this.eventNames.get(0), EventMonitorStatus.MUST_MONITOR);
      this.children = new ArrayList<>();
      for (final TreeElement e : children) {
        this.children.add(e);
        e.parent = this;
      }

    }
  }

  /**
   *
   * @author anmorvan
   *
   */
  class Leaf extends TreeElement {
    Leaf(final String label) {
      super(label);
      this.eventMonitoringStatuses.put(TestTreeView2DPage.this.eventNames.get(1), EventMonitorStatus.MAY_MONITOR);

    }
  }

  Leaf leaf(final String label) {
    return new Leaf(label);
  }

  Node node(final String label, final TreeElement... children) {
    return new Node(label, Arrays.asList(children));
  }

  Node buildExample() {
    return node("root", leaf("topleaf"), node("2ndLevel", leaf("titi"), leaf("toto")));
  }

  /**
   *
   * @author anmorvan
   * 
   */
  public void createOperatorTreeSection(final Composite container, final FormToolkit toolkit,
      final IPropertyListener listener) {

    container.setLayout(new GridLayout());

    final OperatorCheckStateListener checkStateListener =
        new OperatorCheckStateListener((Section) container.getParent(), this.scenario);

    // Creating the tree view
    final CheckboxTreeViewer treeviewer = new CheckboxTreeViewer(toolkit.createTree(container, SWT.CHECK)) {

    };

    // The content provider fills the tree
    final ITreeContentProvider contentProvider = new ITreeContentProvider() {

      @Override
      public boolean hasChildren(final Object element) {
        if (element instanceof Node) {
          return !(((Node) element).children.isEmpty());
        }
        return false;
      }

      @Override
      public Object getParent(final Object element) {
        if (element instanceof TreeElement) {
          return ((TreeElement) element).parent;
        }
        return null;
      }

      @Override
      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof Leaf) {
          return new String[] { ((TreeElement) inputElement).label };
        } else if (inputElement instanceof Node) {
          return getChildren(inputElement);
        }
        return new String[0];
      }

      @Override
      public Object[] getChildren(final Object parentElement) {
        if (parentElement instanceof Node) {
          final List<TreeElement> children = ((Node) parentElement).children;
          return children.toArray(new TreeElement[children.size()]);
        }
        return new String[0];
      }

    };

    treeviewer.setContentProvider(contentProvider);

    // The check state listener modifies the check status of elements
    checkStateListener.setTreeViewer(treeviewer, listener);
    treeviewer.setLabelProvider(new LabelProvider() {

    });
    treeviewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

    treeviewer.addCheckStateListener(checkStateListener);

    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    gd.heightHint = 400;
    gd.widthHint = 250;
    treeviewer.getTree().setLayoutData(gd);

    treeviewer.setUseHashlookup(true);

    treeviewer.getTree().setLinesVisible(true);
    treeviewer.getTree().setHeaderVisible(true);

    final TreeViewerColumn actorViewerColumn = new TreeViewerColumn(treeviewer, SWT.CENTER | SWT.CHECK);
    final TreeColumn actorColumn = actorViewerColumn.getColumn();
    actorColumn.setText("Actor Name");
    actorColumn.setWidth(200);
    actorViewerColumn.setLabelProvider(new ColumnLabelProvider());

    int columnIndex = 1;
    for (final String columnLabel : this.eventNames) {

      final TreeViewerColumn viewerColumn = new TreeViewerColumn(treeviewer, SWT.CENTER | SWT.CHECK);
      final TreeColumn column = viewerColumn.getColumn();

      column.setText(columnLabel);
      column.setMoveable(true);
      column.setWidth(150);

      viewerColumn.setLabelProvider(new TestCLP(this.eventNames.get(columnIndex - 1)));
      viewerColumn.setEditingSupport(new TestES(treeviewer, this.eventNames.get(columnIndex - 1)));
      columnIndex++;
    }

    treeviewer.setInput(buildExample());

    toolkit.paintBordersFor(container);

    // Tree is refreshed in case of algorithm modifications
    container.addPaintListener(checkStateListener);
  }

  /**
   *
   * @author anmorvan
   *
   */
  class TestES extends EditingSupport {

    public TestES(final ColumnViewer viewer, final String name) {
      super(viewer);
      this.eventName = name;
    }

    String     eventName;
    CellEditor editor = new CheckboxCellEditor();

    @Override
    protected CellEditor getCellEditor(final Object element) {
      return this.editor;
    }

    @Override
    protected boolean canEdit(final Object element) {
      return true;
    }

    @Override
    protected Object getValue(final Object element) {
      // we do not need to read the value: it will rotate
      return true;
    }

    @Override
    protected void setValue(final Object element, final Object value) {
      if (element instanceof TreeElement) {

        final EventMonitorStatus eventMonitorStatus = 
            ((TreeElement) element).eventMonitoringStatuses.get(this.eventName);
        ((TreeElement) element).eventMonitoringStatuses.put(this.eventName, eventMonitorStatus.next());
      }
      getViewer().update(element, null);
    }

  }

  /**
   *
   * @author anmorvan
   *
   */
  class TestCLP extends ColumnLabelProvider {

    public TestCLP(final String name) {
      this.eventName = name;
    }

    String eventName;

    @Override
    public String getText(final Object element) {
      if (element instanceof TreeElement) {
        final TreeElement treeElement = (TreeElement) element;
        final Map<String, EventMonitorStatus> statuses = treeElement.eventMonitoringStatuses;
        if (!statuses.containsKey(this.eventName)) {
          statuses.put(this.eventName, EventMonitorStatus.SKIP);
        }
        return statuses.get(this.eventName).toString();
      } else {
        return "ERROR";
      }
    }

    @Override
    public Image getImage(final Object element) {
      return null;
    }
  }

  @Override
  public void propertyChanged(final Object source, final int propId) {
    if (propId == IEditorPart.PROP_DIRTY) {
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
  }

}
