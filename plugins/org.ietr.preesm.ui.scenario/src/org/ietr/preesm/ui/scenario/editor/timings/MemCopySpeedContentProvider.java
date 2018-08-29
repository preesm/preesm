/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2013)
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
package org.ietr.preesm.ui.scenario.editor.timings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.scenario.MemCopySpeed;
import org.ietr.preesm.core.scenario.PreesmScenario;

// TODO: Auto-generated Javadoc
/**
 * Provides the elements contained in the memcopy speeds editor.
 *
 * @author mpelcat
 */
public class MemCopySpeedContentProvider implements IStructuredContentProvider {

  /** The element list. */
  List<MemCopySpeed> elementList = null;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {

    if (inputElement instanceof PreesmScenario) {
      final PreesmScenario inputScenario = (PreesmScenario) inputElement;

      /**
       * Memcopy speeds are added for all operator types if non present
       */
      for (final String opDefId : inputScenario.getOperatorDefinitionIds()) {
        if (!inputScenario.getTimingManager().hasMemCpySpeed(opDefId)) {
          inputScenario.getTimingManager().setDefaultMemCpySpeed(opDefId);
        }
      }

      // Retrieving the memory copy speeds in operator definition order
      this.elementList = new ArrayList<>(inputScenario.getTimingManager().getMemcpySpeeds().values());

      Collections.sort(this.elementList, (o1, o2) -> o1.getOperatorDef().compareTo(o2.getOperatorDef()));
    }
    return this.elementList.toArray();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // TODO Auto-generated method stub

  }

}
