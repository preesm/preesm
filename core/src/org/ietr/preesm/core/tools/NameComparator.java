/**
 * 
 */
package org.ietr.preesm.core.tools;

import java.util.Comparator;

import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * @author mpelcat
 *
 * Vertex comparator that helps to order vertices in name alphabetical order
 */
public class NameComparator implements Comparator<SDFAbstractVertex>{

	@Override
	public int compare(SDFAbstractVertex o1, SDFAbstractVertex o2) {

		int diff = o1.getName().compareTo(o2.getName());
		//if(diff == 0) diff = 1;
		
		return diff;
	}
	
}
