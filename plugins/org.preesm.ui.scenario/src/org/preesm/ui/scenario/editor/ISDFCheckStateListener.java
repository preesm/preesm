/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2009)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2011)
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
package org.preesm.ui.scenario.editor;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;

// TODO: Auto-generated Javadoc
/**
 * Interface allowing code generation page and constraint page to share the same class for sdf tree edition.
 *
 * @author mpelcat
 */
public interface ISDFCheckStateListener extends SelectionListener, ICheckStateListener, PaintListener {

  /**
   * Adds the combo box selector.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  public void addComboBoxSelector(Composite parent, FormToolkit toolkit);

  /**
   * Sets the tree viewer.
   *
   * @param treeViewer
   *          the tree viewer
   * @param contentProvider
   *          the content provider
   * @param propertyListener
   *          the property listener
   */
  public void setTreeViewer(CheckboxTreeViewer treeViewer, PreesmAlgorithmTreeContentProvider contentProvider,
      IPropertyListener propertyListener);
}
