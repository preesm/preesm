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
 
package org.ietr.preesm.core.scenario.editor;

import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Class used as a scenario editor tree content to distinguish two vertices with
 * the same name but different paths (stored in "info" property)
 * 
 * @author mpelcat
 */
public class VertexWithPath {


	private SDFAbstractVertex storedVertex;

	public VertexWithPath(SDFAbstractVertex storedVertex) {
		super();
		this.storedVertex = storedVertex;
	}

	public SDFAbstractVertex getStoredVertex() {
		return storedVertex;
	}

	public String getName() {
		return storedVertex.getName();
	}

	/**
	 * Checking equality between vertices but also between their paths
	 */
	public boolean equals(Object e) {
		if (e instanceof VertexWithPath) {
			VertexWithPath v = ((VertexWithPath) e);
			SDFAbstractVertex vStored = v.getStoredVertex();
			SDFAbstractVertex thisStored = this.getStoredVertex();

			boolean equals = vStored.equals(thisStored);

			if (equals) {
				if (!(vStored.getInfo() == null || thisStored.getInfo() == null)) {
					if ((!(vStored.getInfo().isEmpty()) || thisStored.getInfo()
							.isEmpty())) {
						equals = vStored.getInfo()
								.equals(thisStored.getInfo());
					}
				}
			}
			return equals;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return storedVertex.getInfo();
	}
}
