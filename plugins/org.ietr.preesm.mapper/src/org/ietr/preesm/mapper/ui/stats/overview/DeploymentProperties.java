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

/**
 * Gathering the properties that will be displayed in the overview page of the
 * stat display
 * 
 * @author mpelcat
 */
public class DeploymentProperties implements IStructuredContentProvider,
		ITableLabelProvider {

	private String columnOrder;

	private StatGenerator statGen;

	private Map<ComponentInstance, Long> loads;
	private Map<ComponentInstance, Integer> memoryNeeds;

	private long repetitionPeriod;

	public void setColumnOrder(String columnOrder) {
		this.columnOrder = columnOrder;
	}

	public DeploymentProperties(StatGenerator statGen) {
		super();
		this.statGen = statGen;

		loads = new HashMap<ComponentInstance, Long>();
		memoryNeeds = new HashMap<ComponentInstance, Integer>();

		repetitionPeriod = statGen.getFinalTime();
		columnOrder = Messages.getString("Overview.properties.opColumn");

		initData();
	}

	private void initData() {
		Set<ComponentInstance> opSet = DesignTools.getOperatorInstances(statGen
				.getAbc().getArchitecture());

		for (ComponentInstance cmp : opSet) {
			loads.put(cmp, statGen.getLoad(cmp));
			memoryNeeds.put(cmp, statGen.getMem(cmp));
		}

	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<ComponentInstance> elements = new ArrayList<ComponentInstance>(
				loads.keySet());

		Comparator<ComponentInstance> comparator = null;

		if (columnOrder.equals(Messages
				.getString("Overview.properties.opColumn"))) {
			comparator = new Comparator<ComponentInstance>() {
				@Override
				public int compare(ComponentInstance o1, ComponentInstance o2) {
					return o1.getInstanceName().compareTo(o2.getInstanceName());
				}
			};
		} else if (columnOrder.equals(Messages
				.getString("Overview.properties.loadColumn"))) {
			comparator = new Comparator<ComponentInstance>() {
				@Override
				public int compare(ComponentInstance o1, ComponentInstance o2) {
					return (int) (loads.get(o1) - loads.get(o2));
				}
			};
		} else if (columnOrder.equals(Messages
				.getString("Overview.properties.memColumn"))) {
			comparator = new Comparator<ComponentInstance>() {
				@Override
				public int compare(ComponentInstance o1, ComponentInstance o2) {
					return memoryNeeds.get(o1) - memoryNeeds.get(o2);
				}
			};
		}

		Collections.sort(elements, comparator);
		return elements.toArray();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String text = "";

		if (element instanceof ComponentInstance) {
			ComponentInstance op = (ComponentInstance) element;

			if (columnIndex == 0) {
				text = op.getInstanceName();
			} else if (columnIndex == 1) {
				double d = loads.get(op);
				d = d * 10000;
				d = d / repetitionPeriod;
				d = Math.ceil(d);
				d = d / 100;

				text = String.valueOf(d);
			} else if (columnIndex == 2) {
				text = memoryNeeds.get(op).toString();
			}
		}

		return text;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	public void setRepetitionPeriod(Integer repetitionPeriod) {
		if (repetitionPeriod != 0)
			this.repetitionPeriod = repetitionPeriod;
	}

	public long getRepetitionPeriod() {
		return repetitionPeriod;
	}

}
