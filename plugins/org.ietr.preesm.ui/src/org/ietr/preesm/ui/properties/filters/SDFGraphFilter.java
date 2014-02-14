package org.ietr.preesm.ui.properties.filters;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.IFilter;
import org.ietr.graphiti.model.Graph;

/**
 * This class filters SDF graphs to enable the correct property tabs
 * 
 * @author mpelcat
 * 
 */
public class SDFGraphFilter implements IFilter {

	@Override
	public boolean select(Object toTest) {
		if (toTest instanceof EditPart) {
			Object model = ((EditPart) toTest).getModel();
			if (model instanceof Graph) {
				Graph graph = (Graph) model;
				return graph.getType().getName().equals("Dataflow Graph");
			}
		}
		return false;
	}

}
