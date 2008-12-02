/**
 * 
 */
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
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Gathering the properties that will be displayed in the overview page of the
 * stat display
 * 
 * @author mpelcat
 */
public class DeploymentProperties implements IStructuredContentProvider,
		ITableLabelProvider {

	private StatGenerator statGen;

	private Map<Operator, Integer> loads;
	private Map<Operator, Integer> memoryNeeds;
	
	private int repetitionPeriod;

	public DeploymentProperties(StatGenerator statGen) {
		super();
		this.statGen = statGen;

		loads = new HashMap<Operator, Integer>();
		memoryNeeds = new HashMap<Operator, Integer>();

		repetitionPeriod = statGen.getFinalTime();
			
		initData();
	}

	private void initData() {
		Set<ArchitectureComponent> opSet = statGen.getArchi().getComponents(
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


	public int getRepetitionPeriod() {
		return repetitionPeriod;
	}

}
