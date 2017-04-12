/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
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

package org.ietr.preesm.mapper.ui.stats.overview;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.mapper.ui.Messages;
import org.ietr.preesm.mapper.ui.stats.StatGenerator;

// TODO: Auto-generated Javadoc
/**
 * This page contains general informations of the scenario including current algorithm and current
 * architecture.
 *
 * @author mpelcat
 */
public class OverviewPage extends FormPage {

  /** The stat gen. */
  private StatGenerator statGen = null;

  /**
   * Instantiates a new overview page.
   *
   * @param statGen
   *          the stat gen
   * @param editor
   *          the editor
   * @param id
   *          the id
   * @param title
   *          the title
   */
  public OverviewPage(final StatGenerator statGen, final FormEditor editor, final String id,
      final String title) {
    super(editor, id, title);

    this.statGen = statGen;
  }

  /**
   * Creation of the sections and their initialization.
   *
   * @param managedForm
   *          the managed form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {

    final ScrolledForm form = managedForm.getForm();
    // FormToolkit toolkit = managedForm.getToolkit();
    form.setText(Messages.getString("Overview.title"));

    final GridLayout layout = new GridLayout();
    form.getBody().setLayout(layout);

    CreatePropSection(managedForm, Messages.getString("Overview.properties.title"),
        Messages.getString("Overview.properties.description"));
  }

  /**
   * Creates a generic section.
   *
   * @param mform
   *          the mform
   * @param title
   *          the title
   * @param desc
   *          the desc
   * @param numColumns
   *          the num columns
   * @param gridData
   *          the grid data
   * @return the composite
   */
  public Composite createSection(final IManagedForm mform, final String title, final String desc,
      final int numColumns, final GridData gridData) {

    final ScrolledForm form = mform.getForm();
    final FormToolkit toolkit = mform.getToolkit();
    final Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TWISTIE
        | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.EXPANDED);
    section.setText(title);
    section.setDescription(desc);
    toolkit.createCompositeSeparator(section);
    final Composite client = toolkit.createComposite(section);
    final GridLayout layout = new GridLayout();
    layout.marginWidth = layout.marginHeight = 0;
    layout.numColumns = numColumns;
    client.setLayout(layout);
    section.setClient(client);
    section.addExpansionListener(new ExpansionAdapter() {
      @Override
      public void expansionStateChanged(final ExpansionEvent e) {
        form.reflow(false);
      }
    });
    section.setLayoutData(gridData);
    return client;
  }

  /**
   * Creates the section editing timings.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void CreatePropSection(final IManagedForm managedForm, final String title,
      final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final Composite container = createSection(managedForm, title, desc, 1,
        new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
    final FormToolkit toolkit = managedForm.getToolkit();

    final DeploymentProperties props = new DeploymentProperties(this.statGen);
    final Text text = addPaceEditor(container, toolkit, props);
    addTable(container, toolkit, text, props);

  }

  /**
   * Adds the pace editor.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   * @param props
   *          the props
   * @return the text
   */
  private Text addPaceEditor(final Composite parent, final FormToolkit toolkit,
      final DeploymentProperties props) {

    toolkit.createLabel(parent, Messages.getString("Overview.properties.paceEditor.label"));

    final Text text = toolkit.createText(parent, String.valueOf(props.getRepetitionPeriod()),
        SWT.SINGLE);

    final GridData gd = new GridData();
    gd.widthHint = 400;
    text.setLayoutData(gd);

    return text;
  }

  /**
   * Gets the stat gen.
   *
   * @return the stat gen
   */
  public StatGenerator getStatGen() {
    return this.statGen;
  }

  /**
   * Adds a table to edit timings.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   * @param text
   *          the text
   * @param props
   *          the props
   */
  protected void addTable(final Composite parent, final FormToolkit toolkit, final Text text,
      final DeploymentProperties props) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    final TableViewer tableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
    final Table table = tableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    tableViewer.setContentProvider(props);
    tableViewer.setLabelProvider(props);

    // Create columns
    final TableColumn column1 = new TableColumn(table, SWT.NONE, 0);
    column1.setText(Messages.getString("Overview.properties.opColumn"));

    final TableColumn column2 = new TableColumn(table, SWT.NONE, 1);
    column2.setText(Messages.getString("Overview.properties.loadColumn"));

    final TableColumn column3 = new TableColumn(table, SWT.NONE, 2);
    column3.setText(Messages.getString("Overview.properties.memColumn"));

    final Listener charSortListener = e -> {
      final TableColumn column = (TableColumn) e.widget;
      props.setColumnOrder(column.getText());
      tableViewer.refresh();
    };

    column1.addListener(SWT.Selection, charSortListener);
    column2.addListener(SWT.Selection, charSortListener);
    column3.addListener(SWT.Selection, charSortListener);

    final Table tref = table;
    final Composite comp = tablecps;

    // Setting the column width
    tablecps.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(final ControlEvent e) {
        final Rectangle area = comp.getClientArea();
        final Point size = tref.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        final ScrollBar vBar = tref.getVerticalBar();
        int width = area.width - tref.computeTrim(0, 0, 0, 0).width - 2;
        if (size.y > (area.height + tref.getHeaderHeight())) {
          final Point vBarSize = vBar.getSize();
          width -= vBarSize.x;
        }
        final Point oldSize = tref.getSize();
        if (oldSize.x > area.width) {
          column1.setWidth((width / 3) - 1);
          column2.setWidth((width / 3) - 1);
          column3.setWidth((width / 3) - 1);
          tref.setSize(area.width, area.height);
        } else {
          tref.setSize(area.width, area.height);
          column1.setWidth((width / 3) - 1);
          column2.setWidth((width / 3) - 1);
          column3.setWidth((width / 3) - 1);
        }
      }
    });

    tableViewer.setInput(props);
    tablecps.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

    text.addModifyListener(e -> {
      final Text text1 = (Text) e.getSource();
      props.setRepetitionPeriod(Integer.valueOf(text1.getText()));
      tableViewer.refresh();
    });
  }
}
