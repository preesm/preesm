/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2011)
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
package org.preesm.ui.scenario.editor.papify;

import java.util.Map;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.preesm.model.slam.Component;
import org.preesm.ui.scenario.editor.papify.PapifyListTreeElement.PAPIStatus;

/**
 * Provides the elements contained in the papify editor.
 *
 * @author dmadronal
 */
/**
 *
 * @author anmorvan
 *
 */
class PapifyComponentListContentProvider2DMatrixES extends EditingSupport {

  Component                                  peType;
  CellEditor                                 editor = new CheckboxCellEditor();
  PapifyComponentListContentProvider2DMatrix contentProvider;

  public PapifyComponentListContentProvider2DMatrixES(final ColumnViewer viewer, final Component name,
      PapifyComponentListContentProvider2DMatrix contentProvider) {
    super(viewer);
    this.peType = name;
    this.contentProvider = contentProvider;
  }

  @Override
  protected CellEditor getCellEditor(final Object element) {
    return this.editor;
  }

  @Override
  protected boolean canEdit(final Object element) {
    return true;
  }

  @Override
  protected Object getValue(final Object element) {
    // we do not need to read the value: it will rotate
    return true;
  }

  @Override
  protected void setValue(final Object element, final Object value) {
    if (element instanceof final PapifyListTreeElement listTreeElement) {
      final String elementName = listTreeElement.label;
      final Map<Component, PAPIStatus> statuses = listTreeElement.papiStatuses;

      final PAPIStatus componentStatus = statuses.get(this.peType);
      if (componentStatus.next().equals(PAPIStatus.NO)) {
        this.contentProvider.removePEfromComp(this.peType, elementName);

      } else {
        this.contentProvider.cleanPE(this.peType);
        this.contentProvider.addPEtoComp(this.peType, elementName);
      }
    }
    this.contentProvider.selectionUpdated();
    this.contentProvider.updateView();
  }

}
