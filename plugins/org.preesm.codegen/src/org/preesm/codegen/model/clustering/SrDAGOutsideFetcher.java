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
    DirectedAcyclicGraph dag = (DirectedAcyclicGraph) input.get("dag");
    DAGVertex dagVertex = (DAGVertex) input.get("dagVertex");
    BufferProperties subBufferProperties = null;
    if (graphPort instanceof DataInputPort) {
      Set<DAGEdge> inEdges = dag.incomingEdgesOf(dagVertex);
      for (final DAGEdge edge : inEdges) {
        final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
        for (final BufferProperties buffProperty : bufferAggregate) {
          final String portHsdfName = graphPort.getName();
          if (buffProperty.getDestInputPortID().equals(portHsdfName) && edge.getSource().getKind() != null) {
            subBufferProperties = buffProperty;
          }
        }
      }
    } else if (graphPort instanceof DataOutputPort) {
      Set<DAGEdge> outEdges = dag.outgoingEdgesOf(dagVertex);
      for (final DAGEdge edge : outEdges) {
        final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
        for (final BufferProperties buffProperty : bufferAggregate) {
          final String portHsdfName = graphPort.getName();
          if (buffProperty.getSourceOutputPortID().equals(portHsdfName) && edge.getTarget().getKind() != null) {
            subBufferProperties = buffProperty;
          }
        }
      }
    }

    // Is the buffer a DistributedBuffer ?
    @SuppressWarnings("unchecked")
    Map<BufferProperties, Buffer> srSDFEdgeBuffers = (Map<BufferProperties, Buffer>) input.get("srSDFEdgeBuffers");
    CoreBlock operatorBlock = (CoreBlock) input.get("coreBlock");
    Buffer var = srSDFEdgeBuffers.get(subBufferProperties);
    if (var instanceof DistributedBuffer) {
      DistributedBuffer twinBuffer = (DistributedBuffer) var;
      EList<Buffer> copies = twinBuffer.getDistributedCopies();
      String coreBlockName = operatorBlock.getName();
      for (Buffer bufferTwinChecker : copies) {
        SubBuffer subBufferChecker = (SubBuffer) bufferTwinChecker;
        SubBuffer twinContainer = (SubBuffer) subBufferChecker.getContainer();
        if (twinContainer.getContainer().getName().equals(coreBlockName)) {
          var = subBufferChecker;
          break;
        }
      }
    }

    return var;
  }

}
