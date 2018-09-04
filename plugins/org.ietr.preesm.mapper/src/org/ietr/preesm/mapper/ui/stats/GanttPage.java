/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.ui.GanttPlotter;

/**
 * This page contains the gantt display.
 *
 * @author mpelcat
 */
public class GanttPage extends FormPage {

  /** The stat gen. */
  private StatGenerator statGen = null;

  /**
   * Instantiates a new gantt page.
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
  public GanttPage(final StatGenerator statGen, final FormEditor editor, final String id, final String title) {
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
    final ColumnLayout layout = new ColumnLayout();
    layout.topMargin = 0;
    layout.bottomMargin = 5;
    layout.leftMargin = 10;
    layout.rightMargin = 10;
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 10;
    layout.maxNumColumns = 4;
    layout.minNumColumns = 1;
    form.getBody().setLayout(layout);

    final LatencyAbc abc = this.statGen.getAbc();

    GanttPlotter.plotDeployment(abc.getGanttData(), form.getBody());

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
