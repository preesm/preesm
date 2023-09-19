package org.preesm.algorithm.node.partitioner;

import java.io.File;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

public class CodegenSimSDP {
  public CodegenSimSDP(final Scenario scenario, final PiGraph topGraph, Map<Long, String> nodeNames) {

    final String topPath = scenario.getCodegenDirectory() + File.separator;
    final String fileName = "mainSimSDP.c";

    final StringConcatenation content = buildContent(topGraph, nodeNames);
    PreesmIOHelper.getInstance().print(topPath, fileName, content);
  }

  private StringConcatenation buildContent(PiGraph topGraph, Map<Long, String> nodeNames) {
    final StringConcatenation result = new StringConcatenation();
    result.append(init());
    result.append(initNode(topGraph, nodeNames));
    result.append(buffer(topGraph));
    result.append(mpiEnv());
    result.append(exeNode(topGraph));
    return result;
  }

  private StringConcatenation exeNode(PiGraph topGraph) {
    StringConcatenation result = new StringConcatenation();
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {
        final String indexStr = node.getName().replace("sub_", "");
        final Long indexL = Long.decode(indexStr);
        result.append("if (world_rank ==" + indexL + "){ \n");
        for (final DataInputPort din : node.getDataInputPorts()) {
          result.append("MPI_Recv(" + ((AbstractActor) din.getFifo().getSource()).getName() + ","
              + din.getExpression().evaluate() + "," + din.getFifo().getType() + "," + (indexL - 1)
              + ", MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);\n");
        }
        result.append(node.getName() + "(");
        for (final ConfigInputPort cfg : node.getConfigInputPorts()) {
          result.append(cfg.getName() + ",");
        }
        for (final DataInputPort din : node.getDataInputPorts()) {
          result.append(((AbstractActor) din.getFifo().getSource()).getName() + ",");
        }
        for (final DataOutputPort dout : node.getDataOutputPorts()) {
          result.append(dout.getName() + ",");
        }
        final String temp = result.toString().substring(0, result.length() - 1);
        result = new StringConcatenation();
        result.append(temp);
        for (final DataOutputPort dout : node.getDataOutputPorts()) {
          result.append("MPI_Ssend(" + dout.getName() + "," + dout.getExpression().evaluate() + ","
              + dout.getFifo().getType() + "," + (indexL + 1) + ", MPI_COMM_WORLD);\n");
        }
        result.append("initNode" + indexL + " = 0;\n }\n");
      }
    }
    result.append("}\n");
    for (final AbstractActor node : topGraph.getOnlyActors()) {
      if (node instanceof Actor) {
        for (final DataOutputPort dout : node.getDataOutputPorts()) {
          result.append("MPI_Free_mem(" + dout.getName());

        }
      }
    }
    result.append("MPI_Finalize();\n");

    return result;
  }

  private StringConcatenation mpiEnv() {
    final StringConcatenation result = new StringConcatenation();
    result.append("// Get the name of the processor \n");
    result.append("char processor_name[MPI_MAX_PROCESSOR_NAME]; \n");
    result.append("int name_len; \n");
    result.append("int rank = 0; \n");

    result.append("MPI_Get_processor_name(processor_name, &name_len); \n");
    result.append("for(int index = 0;index < nodeset;index++){  \n");
    result.append("if(processor_name==nodeset[index]){ \n");
    result.append("rank = index; \n");
    result.append("} \n } \n");

    result.append("for(int index=0; index< MPI_LOOP_SIZE;index++) { \n");
    return result;
  }

  private StringConcatenation buffer(PiGraph topGraph) {
    final StringConcatenation result = new StringConcatenation();
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

  private StringConcatenation init() {
    final StringConcatenation result = new StringConcatenation();
    result.append("#include <stdio.h> \n");
    result.append("#include <mpi.h> \n");
    result.append("#include \"stdlib.h\" \n");
    result.append("#include \"preesm_gen.h\" \n");
    result.append("//#define MPI_LOOP_SIZE 0 \n\n");
    return result;
  }

  private StringConcatenation initNode(PiGraph topGraph, Map<Long, String> nodeNames) {
    final StringConcatenation result = new StringConcatenation();
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
