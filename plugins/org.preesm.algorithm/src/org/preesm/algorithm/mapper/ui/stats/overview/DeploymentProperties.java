/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.preesm.algorithm.mapper.ui.stats.overview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.preesm.algorithm.mapper.ui.Messages;
import org.preesm.algorithm.mapper.ui.stats.IStatGenerator;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.utils.LexicographicComponentInstanceComparator;

/**
 * Gathering the properties that will be displayed in the overview page of the stat display.
 *
 * @author mpelcat
 */
public class DeploymentProperties implements IStructuredContentProvider, ITableLabelProvider {

  private String                             columnOrder;
  private final IStatGenerator               statGen;
  private final Map<ComponentInstance, Long> loads;
  private final Map<ComponentInstance, Long> memoryNeeds;
  private final long                         repetitionPeriod;

  public void setColumnOrder(final String columnOrder) {
    this.columnOrder = columnOrder;
  }

  /**
   */
  public DeploymentProperties(final IStatGenerator statGen) {
    super();
    this.statGen = statGen;

    this.loads = new LinkedHashMap<>();
    this.memoryNeeds = new LinkedHashMap<>();

    this.repetitionPeriod = statGen.getFinalTime();
    this.columnOrder = Messages.getString("Overview.properties.opColumn");

    initData();
  }

  /**
   * Inits the data.
   */
  private void initData() {
    final Design architecture = this.statGen.getDesign();
    final List<ComponentInstance> opSet = architecture.getOperatorComponentInstances();

    for (final ComponentInstance cmp : opSet) {
      this.loads.put(cmp, this.statGen.getLoad(cmp));
      this.memoryNeeds.put(cmp, this.statGen.getMem(cmp));
    }

  }

  @Override
  public Object[] getElements(final Object inputElement) {
    final List<ComponentInstance> elements = new ArrayList<>(this.loads.keySet());

    Comparator<ComponentInstance> comparator = null;

    if (this.columnOrder.equals(Messages.getString("Overview.properties.opColumn"))) {
      comparator = new LexicographicComponentInstanceComparator();
    } else if (this.columnOrder.equals(Messages.getString("Overview.properties.loadColumn"))) {
      comparator = (o1, o2) -> Long.compare(loads.get(o1), loads.get(o2));
    } else if (this.columnOrder.equals(Messages.getString("Overview.properties.memColumn"))) {
      comparator = (o1, o2) -> Long.compare(memoryNeeds.get(o1), memoryNeeds.get(o2));
    }

    Collections.sort(elements, comparator);
    return elements.toArray();
  }

  @Override
  public void dispose() {
    // nothing
  }

  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // nothing
  }

  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    return null;
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String text = "";

    if (element instanceof final ComponentInstance op) {

      if (columnIndex == 0) {
        text = op.getInstanceName();
      } else if (columnIndex == 1) {
        double d = this.loads.get(op);
        d = d * 100;
        d = d / this.repetitionPeriod;

        text = String.valueOf(Math.ceil(d));
      } else if (columnIndex == 2) {
        text = this.memoryNeeds.get(op).toString();
      }
    }

    return text;
  }

  @Override
  public void addListener(final ILabelProviderListener listener) {
    // nothing
  }

  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // nothing
  }

}
