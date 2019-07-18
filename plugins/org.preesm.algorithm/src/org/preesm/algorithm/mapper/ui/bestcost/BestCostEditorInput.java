/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.preesm.algorithm.PreesmAlgorithmPlugin;
import org.preesm.algorithm.mapper.ui.BestCostPlotter;

/**
 * Input of the editor displaying the best cost found in time.
 *
 * @author mpelcat
 */
public class BestCostEditorInput implements IEditorInput {

  private BestCostPlotter plotter = null;

  /**
   * Instantiates a new best cost editor input.
   *
   * @param plotter
   *          the plotter
   */
  public BestCostEditorInput(final BestCostPlotter plotter) {
    super();
    this.plotter = plotter;
  }

  public BestCostPlotter getPlotter() {
    return this.plotter;
  }

  @Override
  public boolean exists() {
    return false;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return PreesmAlgorithmPlugin.getInstance().getImageDescriptor("icons/preesm2mini.PNG");
  }

  @Override
  public String getName() {
    return "Best found cost";
  }

  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "Best Latency";
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Object getAdapter(final Class adapter) {
    return null;
  }

}
