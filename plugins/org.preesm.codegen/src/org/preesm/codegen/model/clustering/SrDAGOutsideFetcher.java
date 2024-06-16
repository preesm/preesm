/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
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
package org.preesm.codegen.model.clustering;

import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapper.graphtransfo.BufferAggregate;
import org.preesm.algorithm.mapper.graphtransfo.BufferProperties;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.DistributedBuffer;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;

/**
 * @author dgageot
 *
 */
public class SrDAGOutsideFetcher implements IOutsideFetcher {

  @Override
  public Buffer getOuterClusterBuffer(DataPort graphPort, Map<String, Object> input) {

    // Search corresponding port
    final BufferProperties subBufferProperties = searchBufferProperties(graphPort, input);

    // Is the buffer a DistributedBuffer ?
    @SuppressWarnings("unchecked")
    final Map<BufferProperties,
        Buffer> srSDFEdgeBuffers = (Map<BufferProperties, Buffer>) input.get("srSDFEdgeBuffers");
    final CoreBlock operatorBlock = (CoreBlock) input.get("coreBlock");
    Buffer variable = srSDFEdgeBuffers.get(subBufferProperties);
    if (variable instanceof final DistributedBuffer twinBuffer) {
      final EList<Buffer> copies = twinBuffer.getDistributedCopies();
      final String coreBlockName = operatorBlock.getName();
      for (final Buffer bufferTwinChecker : copies) {
        final SubBuffer subBufferChecker = (SubBuffer) bufferTwinChecker;
        final SubBuffer twinContainer = (SubBuffer) subBufferChecker.getContainer();
        if (twinContainer.getContainer().getName().equals(coreBlockName)) {
          variable = subBufferChecker;
          break;
        }
      }
    }

    return variable;
  }

  private BufferProperties searchBufferProperties(DataPort graphPort, Map<String, Object> input) {
    final DirectedAcyclicGraph dag = (DirectedAcyclicGraph) input.get("dag");
    final DAGVertex dagVertex = (DAGVertex) input.get("dagVertex");
    BufferProperties subBufferProperties = null;
    if (graphPort instanceof DataInputPort) {
      subBufferProperties = searchInputPort(graphPort, dag, dagVertex, subBufferProperties);
    } else if (graphPort instanceof DataOutputPort) {
      subBufferProperties = searchOutputPort(graphPort, dag, dagVertex, subBufferProperties);
    }
    return subBufferProperties;
  }

  private BufferProperties searchOutputPort(DataPort graphPort, DirectedAcyclicGraph dag, DAGVertex dagVertex,
      BufferProperties subBufferProperties) {
    final Set<DAGEdge> outEdges = dag.outgoingEdgesOf(dagVertex);
    for (final DAGEdge edge : outEdges) {
      final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.PROPERTY_BEAN_NAME);
      for (final BufferProperties buffProperty : bufferAggregate) {
        final String portHsdfName = graphPort.getName();
        if (buffProperty.getSourceOutputPortID().equals(portHsdfName) && edge.getTarget().getKind() != null) {
          subBufferProperties = buffProperty;
        }
      }
    }
    return subBufferProperties;
  }

  private BufferProperties searchInputPort(DataPort graphPort, DirectedAcyclicGraph dag, DAGVertex dagVertex,
      BufferProperties subBufferProperties) {
    final Set<DAGEdge> inEdges = dag.incomingEdgesOf(dagVertex);
    for (final DAGEdge edge : inEdges) {
      final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.PROPERTY_BEAN_NAME);
      for (final BufferProperties buffProperty : bufferAggregate) {
        final String portHsdfName = graphPort.getName();
        if (buffProperty.getDestInputPortID().equals(portHsdfName) && edge.getSource().getKind() != null) {
          subBufferProperties = buffProperty;
        }
      }
    }
    return subBufferProperties;
  }

}
