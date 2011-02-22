package org.ietr.preesm.ui.properties.filters;

import net.sf.graphiti.model.Graph;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.IFilter;

public class SDFGraphFilter implements IFilter {

	@Override
	public boolean select(Object toTest) {
		if (toTest instanceof EditPart) {
			Object model = ((EditPart) toTest).getModel();
			if (model instanceof Graph) {
				Graph graph = (Graph) model;
				return graph.getType().getName().equals("SDF Graph");
			}
		}
		return false;
	}

}
