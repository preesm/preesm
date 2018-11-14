/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.preesm.mapper.ui.gantt;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.ietr.preesm.mapper.activator.Activator;
import org.ietr.preesm.mapper.gantt.GanttData;
import org.preesm.commons.logger.PreesmLogger;

/**
 * Input of the simple editor of implementation gantt chart.
 *
 * @author mpelcat
 */
public class GanttEditorInput implements IEditorInput {

  /** The gantt data. */
  private GanttData ganttData = null;

  /** The name. */
  private String name = null;

  /**
   * Instantiates a new gantt editor input.
   *
   * @param ganttData
   *          the gantt data
   * @param name
   *          the name
   */
  public GanttEditorInput(final GanttData ganttData, final String name) {
    super();
    this.ganttData = ganttData;
    this.name = name;
  }

  /**
   * Gets the gantt data.
   *
   * @return the gantt data
   */
  public GanttData getGanttData() {
    return this.ganttData;
  }

  /**
   * Sets the gantt data.
   *
   * @param ganttData
   *          the new gantt data
   */
  public void setGanttData(final GanttData ganttData) {
    this.ganttData = ganttData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#exists()
   */
  @Override
  public boolean exists() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    return Activator.getImageDescriptor("icons/preesm3mini.PNG");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getName()
   */
  @Override
  public String getName() {
    return this.name + " " + PreesmLogger.getFormattedTime();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getPersistable()
   */
  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getToolTipText()
   */
  @Override
  public String getToolTipText() {
    return this.name;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Object getAdapter(final Class adapter) {
    return null;
  }

}
