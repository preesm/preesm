/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.mapper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;

/**
 * Centralizing constraints of related vertices
 * 
 * @author mpelcat
 */
public class RelativeConstraint {

	private List<MapperDAGVertex> vertices;

	public RelativeConstraint() {
		super();
		this.vertices = new ArrayList<MapperDAGVertex>();
	}

	public List<MapperDAGVertex> getVertices() {
		return vertices;
	}

	public void addVertex(MapperDAGVertex vertex) {
		this.vertices.add(vertex);
	}

	public void merge(RelativeConstraint constraints) {
		for (MapperDAGVertex v : constraints.getVertices()) {
			if (!vertices.contains(v)) {
				this.addVertex(v);
			}
		}
	}

	public List<ComponentInstance> getOperatorsIntersection() {

		List<ComponentInstance> operators = new ArrayList<ComponentInstance>();

		if (vertices.isEmpty()) {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"Relative constraint with no vertex.");

			return operators;
		} else {
			MapperDAGVertex firstVertex = vertices.get(0);
			ComponentInstance op = firstVertex
					.getMapping().getEffectiveOperator();
			if (op != null && vertices.size() > 1) {
				// Forcing the mapper to put together related vertices
				operators.add(op);
			} else {
				operators.addAll(firstVertex.getInit()
						.getInitialOperatorList());
			}
		}

		for (int i = 1; i < vertices.size(); i++) {
			MapperDAGVertex vertex = vertices.get(i);
			ComponentInstance op = vertex.getMapping()
					.getEffectiveOperator();
			if (op != null) {
				if (DesignTools.contains(operators, op)) {
					operators.clear();
					operators.add(op);
				} else {
					operators.clear();
				}
			} else {
				DesignTools.retainAll(operators, vertex
						.getInit().getInitialOperatorList());
			}
		}

		return operators;
	}
}
