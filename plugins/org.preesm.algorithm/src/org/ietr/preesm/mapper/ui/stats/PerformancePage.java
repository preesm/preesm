/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.mapper.ui.stats;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
import org.preesm.workflow.WorkflowException;

// TODO: Auto-generated Javadoc
/**
 * This page displays the quality of the current implementation compared to the theoretic achievable time.
 *
 * @author mpelcat
 */
public class PerformancePage extends FormPage {

  /** The class generating the performance data. */
  private StatGenerator statGen = null;

  /** The class plotting the performance data. */
  PerformancePlotter plotter = null;

  /**
   * Instantiates a new performance page.
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
  public PerformancePage(final StatGenerator statGen, final FormEditor editor, final String id, final String title) {
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
    form.setText(Messages.getString("Performance.title"));
    final GridLayout layout = new GridLayout();
    form.getBody().setLayout(layout);

    this.plotter = new PerformancePlotter("Comparing the obtained speedup to ideal speedups");

    // Explanation on how to read the chart
    /*
     * createExplanationSection(managedForm, Messages.getString("Performance.Explanation.title"),
     * Messages.getString("Performance.Explanation.description"));
     */
    try {
      createChartSection(managedForm, Messages.getString("Performance.Chart.title"),
          Messages.getString("Performance.Chart.description"));
    } catch (final WorkflowException e) {
      e.printStackTrace();
    }

    managedForm.refresh();
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
  public Composite createSection(final IManagedForm mform, final String title, final String desc, final int numColumns,
      final GridData gridData) {

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
   * Creates a section to explain the performances.
   *
   * @param mform
   *          form containing the section
   * @param title
   *          section title
   * @param desc
   *          description of the section
   * @throws WorkflowException
   *           the workflow exception
   */
  /*
   * private void createExplanationSection(IManagedForm mform, String title, String desc) {
   *
   * GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING); gridData.heightHint
   * = 500;
   *
   * Composite client = createSection(mform, title, desc, 1, gridData);
   *
   * FormToolkit toolkit = mform.getToolkit(); toolkit.paintBordersFor(client); }
   */

  /**
   * Creates a section for the chart
   *
   * @param mform
   *          form containing the section
   * @param title
   *          section title
   * @param desc
   *          description of the section
   */
  private void createChartSection(final IManagedForm mform, final String title, final String desc)
      throws WorkflowException {

    final long workLength = this.statGen.getDAGWorkLength();
    final long spanLength = this.statGen.getDAGSpanLength();
    final long resultTime = this.statGen.getResultTime();
    final int resultNbCores = this.statGen.getNbUsedOperators();
    final int resultNbMainCores = this.statGen.getNbMainTypeOperators();

    final String currentValuesDisplay = String.format(
        "work length: %d, span length: %d, implementation length: %d, "
            + "implementation number of main type operators: %d.",
        workLength, spanLength, resultTime, resultNbMainCores);

    // GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
    final GridData gridData = new GridData();
    gridData.widthHint = 800;
    gridData.heightHint = 500;

    // mform.getForm().setLayout(new FillLayout());

    final Composite client = createSection(mform, title, desc + currentValuesDisplay, 1, gridData);

    final FormToolkit toolkit = mform.getToolkit();

    if ((workLength > 0) && (spanLength > 0) && (resultTime > 0) && (resultNbCores > 0)) {
      this.plotter.setData(workLength, spanLength, resultTime, resultNbCores, resultNbMainCores);
      this.plotter.display(client);
    }

    toolkit.paintBordersFor(client);
  }

  /**
   * Gets the stat gen.
   *
   * @return the stat gen
   */
  public StatGenerator getStatGen() {
    return this.statGen;
  }

}
