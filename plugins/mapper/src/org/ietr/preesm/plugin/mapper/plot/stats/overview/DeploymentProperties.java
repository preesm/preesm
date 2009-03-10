/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
 
package org.ietr.preesm.plugin.mapper.plot.stats.overview;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.plugin.mapper.plot.stats.StatGenerator;

/**
 * Gathering the properties that will be displayed in the overview page of the
 * stat display
 * 
 * @author mpelcat
 */
public class DeploymentProperties implements IStructuredContentProvider,
		ITableLabelProvider {

	private StatGenerator statGen;

	private Map<Operator, Long> loads;
	private Map<Operator, Integer> memoryNeeds;
	
	private long repetitionPeriod;

	public DeploymentProperties(StatGenerator statGen) {
		super();
		this.statGen = statGen;

		loads = new HashMap<Operator, Long>();
		memoryNeeds = new HashMap<Operator, Integer>();

		repetitionPeriod = statGen.getFinalTime();
			
		initData();
	}

	private void initData() {
		Set<ArchitectureComponent> opSet = statGen.getAbc().getArchitecture().getComponents(
				ArchitectureComponentType.operator);

		for (ArchitectureComponent cmp : opSet) {
			Operator op = (Operator) cmp;
			loads.put(op, statGen.getLoad(op));
			memoryNeeds.put(op, statGen.getMem(op));
		}

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return loads.keySet().toArray();
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

		if (element instanceof Operator) {
			Operator op = (Operator) element;

			if (columnIndex == 0) {
				text = op.getName();
			} else if (columnIndex == 1) {
				double d = loads.get(op);
				d = d * 10000;
				d = d / repetitionPeriod;
				d = Math.ceil(d);
				d = d/100;

				text =String.valueOf(d);
			} else if (columnIndex == 2) {
				text = memoryNeeds.get(op).toString();
			}
		}

		return text;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void setRepetitionPeriod(Integer repetitionPeriod) {
		if(repetitionPeriod != 0)
			this.repetitionPeriod = repetitionPeriod;
	}


	public long getRepetitionPeriod() {
		return repetitionPeriod;
	}

}
