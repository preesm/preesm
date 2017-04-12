/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
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

package org.ietr.preesm.mapper.ui.stats.overview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.mapper.ui.Messages;
import org.ietr.preesm.mapper.ui.stats.StatGenerator;

// TODO: Auto-generated Javadoc
/**
 * Gathering the properties that will be displayed in the overview page of the stat display.
 *
 * @author mpelcat
 */
public class DeploymentProperties implements IStructuredContentProvider, ITableLabelProvider {

  /** The column order. */
  private String columnOrder;

  /** The stat gen. */
  private final StatGenerator statGen;

  /** The loads. */
  private final Map<ComponentInstance, Long> loads;

  /** The memory needs. */
  private final Map<ComponentInstance, Integer> memoryNeeds;

  /** The repetition period. */
  private long repetitionPeriod;

  /**
   * Sets the column order.
   *
   * @param columnOrder
   *          the new column order
   */
  public void setColumnOrder(final String columnOrder) {
    this.columnOrder = columnOrder;
  }

  /**
   * Instantiates a new deployment properties.
   *
   * @param statGen
   *          the stat gen
   */
  public DeploymentProperties(final StatGenerator statGen) {
    super();
    this.statGen = statGen;

    this.loads = new HashMap<>();
    this.memoryNeeds = new HashMap<>();

    this.repetitionPeriod = statGen.getFinalTime();
    this.columnOrder = Messages.getString("Overview.properties.opColumn");

    initData();
  }

  /**
   * Inits the data.
   */
  private void initData() {
    final Set<ComponentInstance> opSet = DesignTools
        .getOperatorInstances(this.statGen.getAbc().getArchitecture());

    for (final ComponentInstance cmp : opSet) {
      this.loads.put(cmp, this.statGen.getLoad(cmp));
      this.memoryNeeds.put(cmp, this.statGen.getMem(cmp));
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    final List<ComponentInstance> elements = new ArrayList<>(this.loads.keySet());

    Comparator<ComponentInstance> comparator = null;

    if (this.columnOrder.equals(Messages.getString("Overview.properties.opColumn"))) {
      comparator = (o1, o2) -> o1.getInstanceName().compareTo(o2.getInstanceName());
    } else if (this.columnOrder.equals(Messages.getString("Overview.properties.loadColumn"))) {
      comparator = (o1, o2) -> (int) (DeploymentProperties.this.loads.get(o1)
          - DeploymentProperties.this.loads.get(o2));
    } else if (this.columnOrder.equals(Messages.getString("Overview.properties.memColumn"))) {
      comparator = (o1, o2) -> DeploymentProperties.this.memoryNeeds.get(o1)
          - DeploymentProperties.this.memoryNeeds.get(o2);
    }

    Collections.sort(elements, comparator);
    return elements.toArray();
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
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String text = "";

    if (element instanceof ComponentInstance) {
      final ComponentInstance op = (ComponentInstance) element;

      if (columnIndex == 0) {
        text = op.getInstanceName();
      } else if (columnIndex == 1) {
        double d = this.loads.get(op);
        d = d * 10000;
        d = d / this.repetitionPeriod;
        d = Math.ceil(d);
        d = d / 100;

        text = String.valueOf(d);
      } else if (columnIndex == 2) {
        text = this.memoryNeeds.get(op).toString();
      }
    }

    return text;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.
   * ILabelProviderListener)
   */
  @Override
  public void addListener(final ILabelProviderListener listener) {

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
   * java.lang.String)
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.
   * ILabelProviderListener)
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {

  }

  /**
   * Sets the repetition period.
   *
   * @param repetitionPeriod
   *          the new repetition period
   */
  public void setRepetitionPeriod(final Integer repetitionPeriod) {
    if (repetitionPeriod != 0) {
      this.repetitionPeriod = repetitionPeriod;
    }
  }

  /**
   * Gets the repetition period.
   *
   * @return the repetition period
   */
  public long getRepetitionPeriod() {
    return this.repetitionPeriod;
  }

}
