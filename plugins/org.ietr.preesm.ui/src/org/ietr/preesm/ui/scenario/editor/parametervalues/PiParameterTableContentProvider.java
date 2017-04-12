/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.ui.scenario.editor.parametervalues;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.scenario.PreesmScenario;

// TODO: Auto-generated Javadoc
/**
 * This class provides the elements displayed in {@link ActorTree}. Each element is a {@link ActorNode}. This tree is used in scenario editor to edit parameters
 *
 * @author jheulot
 */
public class PiParameterTableContentProvider implements IStructuredContentProvider {

  /** The scenario. */
  private PreesmScenario scenario = null;

  /**
   * Default Constructor.
   */
  public PiParameterTableContentProvider() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    this.scenario = (PreesmScenario) newInput;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    return this.scenario.getParameterValueManager().getSortedParameterValues().toArray();
  }

}
