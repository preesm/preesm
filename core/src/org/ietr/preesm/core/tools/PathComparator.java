/**
 * 
 */
package org.ietr.preesm.core.tools;

import java.util.Comparator;
import org.ietr.preesm.core.scenario.editor.VertexWithPath;

/**
 * @author mpelcat
 *
 * Vertex comparator that helps to order vertices in path alphabetical order
 */
public class PathComparator implements Comparator<VertexWithPath>{

	@Override
	public int compare(VertexWithPath o1, VertexWithPath o2) {

		int diff = o1.getStoredVertex().getInfo().compareTo(o2.getStoredVertex().getInfo());
		if(diff == 0) diff = 1;
		
		return diff;
	}
	
}
