/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.algorithm.mapper.ui.bestcost;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.preesm.algorithm.mapper.ui.BestCostPlotter;

/**
 * Editor displaying the best cost found in time.
 *
 * @author mpelcat
 */
public class BestCostEditor extends EditorPart {

  private BestCostPlotter plotter = null;

  /**
   * Instantiates a new best cost editor.
   */
  public BestCostEditor() {
    super();
  }

  @Override
  public void doSave(final IProgressMonitor monitor) {
    // nothing
  }

  @Override
  public void doSaveAs() {
    // nothing
  }

  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {

    try {
      setSite(site);
      setInput(input);
      setPartName(input.getName());

      if (input instanceof BestCostEditorInput) {
        final BestCostEditorInput implinput = (BestCostEditorInput) input;
        this.plotter = implinput.getPlotter();
      }

    } catch (final Exception e) {
      // Editor might not exist anymore if switching databases. So
      // just close it.
      getEditorSite().getPage().closeEditor(this, false);
      throw new PartInitException("File " + input.getName() + " does not exist.");
    }

  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void createPartControl(final Composite parent) {
    if (this.plotter != null) {
      this.plotter.display(parent);
    }
  }

  @Override
  public void setFocus() {
    // nothing
  }

  /**
   * Creates the editor.
   *
   * @param plotter
   *          the plotter
   */
  public static void createEditor(final BestCostPlotter plotter) {
    final IEditorInput input = new BestCostEditorInput(plotter);
    PlatformUI.getWorkbench().getDisplay().asyncExec(new BestCostEditorRunnable(input));
  }
}
