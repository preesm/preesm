package org.preesm.algorithm.node.partitioner;

import java.io.File;
import java.util.Map;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

public class CodegenSimSDP {

  private static final String INIT = """
      #include <stdio.h>
      #include <mpi.h>
      #include "stdlib.h"
      #include "preesm_gen.h"
      //#define MPI_LOOP_SIZE 0


        """;

  private static final String MPI_ENV = """
      // Get the name of the processor
      char processor_name[MPI_MAX_PROCESSOR_NAME];
      int name_len;
      int rank = 0;
      MPI_Get_processor_name(processor_name, &name_len);
      for(int index = 0;index < nodeset;index++){
        if(processor_name==nodeset[index]){
          rank = index;
        }
      }
      for(int index=0; index< MPI_LOOP_SIZE;index++) {
          """;

  public CodegenSimSDP(final Scenario scenario, final PiGraph topGraph, Map<Long, String> nodeNames) {

    final String topPath = scenario.getCodegenDirectory() + File.separator;
    final String fileName = "mainSimSDP.c";

    final StringBuilder content = buildContent(topGraph, nodeNames);
    PreesmIOHelper.getInstance().print(topPath, fileName, content);
    PreesmLogger.getLogger().info(() -> "Interface file print in : " + topPath);
  }

  private StringBuilder buildContent(PiGraph topGraph, Map<Long, String> nodeNames) {
    final StringBuilder result = new StringBuilder();
    result.append(INIT);
    result.append(initNode(nodeNames));
    result.append(buffer(topGraph));
    result.append(MPI_ENV);
    result.append(exeNode(topGraph));
    return result;
  }

  private StringBuilder exeNode(PiGraph topGraph) {
    final StringBuilder result = new StringBuilder();
    for (final AbstractActor node : topGraph.getOnlyActors()) {

      if (!(node instanceof Actor)) {
        continue;
      }

      final String indexStr = node.getName().replace("sub", "");
      final Long indexL = Long.decode(indexStr);
      result.append("if (world_rank ==" + indexL + "){ \n");

      for (final DataInputPort din : node.getDataInputPorts()) {
        result.append("MPI_Recv(" + ((AbstractActor) din.getFifo().getSource()).getName() + ","
            + din.getExpression().evaluate() + "," + din.getFifo().getType() + "," + (indexL - 1)
            + ", MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);\n");
      }

      result.append(node.getName() + "(");

      node.getConfigInputPorts().stream().forEach(cfg -> result.append(cfg.getName() + ","));

      node.getDataInputPorts().stream()
          .forEach(din -> result.append(din.getFifo().getSourcePort().getContainingActor().getName() + ","));

      node.getDataOutputPorts().stream().forEach(dout -> result.append(dout.getName() + ","));

      // Remove last ,
      if (result.charAt(result.length() - 1) == ',') {
        result.deleteCharAt(result.length() - 1);
      }

      node.getDataOutputPorts().stream()
          .forEach(dout -> result.append("MPI_Ssend(" + dout.getName() + "," + dout.getExpression().evaluate() + ","
              + dout.getFifo().getType() + "," + (indexL + 1) + ", MPI_COMM_WORLD);\n"));

      result.append("initNode" + indexL + " = 0;\n }\n");

    }
    result.append("}\n");

    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {
        for (final DataOutputPort dout : node.getDataOutputPorts()) {

          result.append("MPI_Free_mem(" + dout.getName() + ");\n");
        }
      }
    }

    result.append("MPI_Finalize();\n }");

    return result;
  }

  private StringBuilder buffer(PiGraph topGraph) {
    final StringBuilder result = new StringBuilder();
    result.append("//Allocate buffers in distributed memory \n");
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {
        for (final DataOutputPort dout : node.getDataOutputPorts()) {
          result.append(dout.getFifo().getType() + " *" + dout.getName() + ";\n");
          result.append("MPI_Alloc_mem(" + dout.getExpression().evaluate() + " * sizeof(" + dout.getFifo().getType()
              + "), MPI_INFO_NULL, &" + dout.getName() + "); \n");
        }
      }
    }
    return result;
  }

  private StringBuilder initNode(Map<Long, String> nodeNames) {
    final StringBuilder result = new StringBuilder();
    result.append("int MPIStopNode = 0;\n");

    for (int i = 0; i < nodeNames.size(); i++) {
      result.append("int initNode" + i + " = 1;\n");
    }

    result.append("char nodeset[" + nodeNames.size() + "] = {");

    for (Long i = 0L; i < nodeNames.size(); i++) {
      result.append("\"" + nodeNames.get(i) + "\"");
      if (i < nodeNames.size()) {
        result.append(",");
      }
    }

    result.append("}; \n\n");
    result.append("int main(int argc, char **argv) { \n");
    result.append("// Initialize the MPI environment \n");
    result.append("MPI_Init(NULL, NULL); \n");
    return result;
  }
}
