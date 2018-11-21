/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.preesm.algorithm.mapper.ui.stats;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.preesm.algorithm.mapper.ui.stats.overview.OverviewPage;

/**
 * The statistic editor displays statistics on the generated implementation.
 *
 * @author mpelcat
 */
public class StatEditor extends SharedHeaderFormEditor implements IPropertyListener {

  /** The stat gen. */
  private StatGenerator statGen = null;

  /**
   * Instantiates a new stat editor.
   */
  public StatEditor() {
    super();
  }

  /**
   * Loading the scenario file.
   *
   * @param site
   *          the site
   * @param input
   *          the input
   * @throws PartInitException
   *           the part init exception
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {

    setSite(site);
    setInput(input);
    setPartName(input.getName());

    if (input instanceof StatEditorInput) {
      final StatEditorInput statinput = (StatEditorInput) input;
      this.statGen = new StatGenerator(statinput.getAbc(), statinput.getScenario(), statinput.getParams());
    }
  }

  /**
   * Adding the editor pages.
   */
  @Override
  protected void addPages() {
    final IFormPage ganttPage = new GanttPage(this.statGen, this, "Gantt", "Gantt");
    final IFormPage overviewPage = new OverviewPage(this.statGen, this, "Loads", "Loads");
    overviewPage.addPropertyListener(this);
    final PerformancePage performancePage = new PerformancePage(this.statGen, this, "Performance",
        "Work, Span and Achieved Speedup");
    performancePage.addPropertyListener(this);

    try {
      addPage(ganttPage);
      addPage(overviewPage);
      addPage(performancePage);
    } catch (final PartInitException e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#isDirty()
   */
  @Override
  public boolean isDirty() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  @Override
  public void doSaveAs() {
    // actually not meant to be edited, so no save action
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void doSave(final IProgressMonitor monitor) {
    // actually not meant to be edited, so no save action
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object, int)
   */
  @Override
  public void propertyChanged(final Object source, final int propId) {
    // actually not meant to be edited, so no save action
  }
}
