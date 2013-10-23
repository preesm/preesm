/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos, Julien Heulot

[mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr

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

package org.ietr.preesm.memory.allocation;

import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

/**
 * This implementation of the MemoryAllocator mainly is an implementation
 * example.<br>
 * The allocation performed here simply consists in allocating each edge of the
 * graph into a dedicated memory space (i.e. there will be no re-use). This
 * memory allocation will always give the worst memory allocation possible. <br>
 * 
 * @author kdesnos
 * 
 */
public class BasicAllocator extends MemoryAllocator {

	/**
	 * Constructor of the MemoryAllocator
	 * 
	 * @param memEx
	 *            The exclusion graph to analyze
	 */
	public BasicAllocator(MemoryExclusionGraph memEx) {
		super(memEx);
	}

	/**
	 * Each edge of the graph is given a dedicated memory space.
	 */
	public void allocate() {
		clear();

		int offset = 0;

		if (inputExclusionGraph != null) {
			for (MemoryExclusionVertex vertex : inputExclusionGraph.vertexSet()) {
				// If a data alignment is required
				Integer typeSize = (Integer) vertex.getPropertyBean().getValue(
						MemoryExclusionVertex.TYPE_SIZE, Integer.class);
				if (alignment == 0) {
					offset += ((offset % typeSize) == 0) ? 0 : typeSize
							- (offset % typeSize);
				} else if (alignment > 0) {
					// Fixed alignment case
					int align = lcm(typeSize, alignment);

					offset += ((offset % align) == 0) ? 0 : align
							- (offset % align);

				}
				allocateMemoryObject(vertex, offset);
				offset += vertex.getWeight();
			}
		}
	}
}
