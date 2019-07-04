package org.preesm.codegen.xtend.task;

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
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.TwinBuffer;
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
    DAGEdge dagEdge = null;
    BufferProperties subBufferProperties = null;
    if (graphPort instanceof DataInputPort) {
      Set<DAGEdge> inEdges = dag.incomingEdgesOf(dagVertex);
      for (final DAGEdge edge : inEdges) {
        final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
        for (final BufferProperties buffProperty : bufferAggregate) {
          final String portHsdfName = graphPort.getName();
          if (buffProperty.getDestInputPortID().equals(portHsdfName) && edge.getSource().getKind() != null) {
            dagEdge = edge;
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
            dagEdge = edge;
            subBufferProperties = buffProperty;
          }
        }
      }
    }

    // Is the buffer a TwinBuffer ?
    @SuppressWarnings("unchecked")
    Map<BufferProperties, Buffer> srSDFEdgeBuffers = (Map<BufferProperties, Buffer>) input.get("srSDFEdgeBuffers");
    CoreBlock operatorBlock = (CoreBlock) input.get("coreBlock");
    Buffer var = srSDFEdgeBuffers.get(subBufferProperties);
    if (var instanceof TwinBuffer) {
      TwinBuffer twinBuffer = (TwinBuffer) var;
      SubBuffer original = (SubBuffer) twinBuffer.getOriginal();
      EList<Buffer> twins = twinBuffer.getTwins();
      SubBuffer originalContainer = (SubBuffer) original.getContainer();
      String coreBlockName = operatorBlock.getName();
      if (originalContainer.getContainer().getName().equals(coreBlockName)) {
        var = original;
      } else {
        for (Buffer bufferTwinChecker : twins) {
          SubBuffer subBufferChecker = (SubBuffer) bufferTwinChecker;
          SubBuffer twinContainer = (SubBuffer) subBufferChecker.getContainer();
          if (twinContainer.getContainer().getName().equals(coreBlockName)) {
            var = subBufferChecker;
            break;
          }
        }
      }
    }

    return var;
  }

}
