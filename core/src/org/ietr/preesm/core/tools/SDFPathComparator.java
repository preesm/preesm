/**
 * 
 */
package org.ietr.preesm.core.tools;

import java.util.Comparator;

import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * @author mpelcat
 *
 * Vertex comparator that helps to order vertices in path alphabetical order
 */
public class SDFPathComparator implements Comparator<SDFAbstractVertex>{

	@Override
	public int compare(SDFAbstractVertex o1, SDFAbstractVertex o2) {

		int diff = o1.getInfo().compareTo(o2.getInfo());
		if(diff == 0) diff = 1;
		
		return diff;
	}
	
}
