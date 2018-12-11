/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2009 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.mapper.model.special;

import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.types.LongVertexPropertyType;
import org.preesm.model.scenario.types.ImplementationPropertyNames;

// TODO: Auto-generated Javadoc
/**
 * Vertex corresponding to receiving a data. This vertex is mapped on the receiver of the corresponding route step.
 *
 * @author mpelcat
 */
public class ReceiveVertex extends TransferVertex {

  static {
    {
      AbstractVertex.public_properties.add(ImplementationPropertyNames.Receive_receiverGraphName);
    }
  }

  /**
   * Instantiates a new receive vertex.
   *
   * @param id
   *          the id
   * @param base
   *          the base
   * @param source
   *          the source
   * @param target
   *          the target
   * @param routeStepIndex
   *          the route step index
   * @param nodeIndex
   *          the node index
   */
  public ReceiveVertex(final String id, final MapperDAG base, final MapperDAGVertex source,
      final MapperDAGVertex target, final int routeStepIndex, final int nodeIndex) {
    super(id, base, source, target, routeStepIndex, nodeIndex);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#getNbRepeat()
   */
  @Override
  public LongVertexPropertyType getNbRepeat() {
    return new LongVertexPropertyType(1);
  }

}
