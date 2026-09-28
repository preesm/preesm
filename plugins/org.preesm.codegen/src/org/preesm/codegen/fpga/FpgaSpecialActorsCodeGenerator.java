/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Mickael Dardaillon [mickael.dardaillon@insa-rennes.fr] (2023 - 2024)
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

package org.preesm.codegen.fpga;

import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;

/**
 * This class generates the code for PiSDF special actors.
 *
 * @author ahonorat
 */
public class FpgaSpecialActorsCodeGenerator {

  private FpgaSpecialActorsCodeGenerator() {
    // forbid instantiation
  }

  /**
   * Generate the function definition of special actor, and give the call prototype.
   *
   * @param flatGraph
   *          Graph to consider.
   * @param actorCalls
   *          Map of actors to their call prototype, will be set for special actors (only broadcast for now).
   * @return All the new function definitions.
   */
  public static String generateSpecialActorDefinitions(final PiGraph flatGraph,
      final Map<AbstractActor, String> actorCalls) {
    final StringBuilder sb = new StringBuilder("\n");
    final var brv = PiBRV.compute(flatGraph, BRVMethod.LCM);
    // only broadcast are supported for now
    for (final AbstractActor aa : flatGraph.getActors()) {
      if (aa instanceof BroadcastActor) {
        // generate the definition
        sb.append(generateBroadcastDefinition(aa, brv));
        // map the call
        actorCalls.put(aa, generateBroadcastCall(aa, brv.get(aa)));
      }
    }

    return sb.toString();
  }

  protected static String generateBroadcastDefinition(final AbstractActor aa, Map<AbstractVertex, Long> brv) {
    final String name = aa.getName();
    final DataPort dip = aa.getDataInputPorts().get(0);
    final String inputName = dip.getName();
    final String typeElt = dip.getFifo().getType();
    final String typeFifo = "hls::stream<" + typeElt + ">";
    // signature
    final StringBuilder def = new StringBuilder("static void " + name + "(" + typeFifo + " &" + inputName);
    for (final DataPort dp : aa.getDataOutputPorts()) {
      def.append(", " + typeFifo + " &" + dp.getName());
    }
    def.append(") {\n");
    def.append("#ifdef LIGHTNINGSIM \n for(int i=0 ; i<" + brv.get(aa) + " ; i++) {\n #endif\n");
    def.append("#pragma HLS PIPELINE II=1 style=flp\n");
    // body
    def.append(typeElt + " tmp = " + inputName + ".read();\n");
    for (final DataPort dp : aa.getDataOutputPorts()) {
      def.append(dp.getName() + ".write(tmp);\n");
    }
    def.append("#ifdef LIGHTNINGSIM \n } \n #endif\n");
    def.append("}\n\n");

    return def.toString();
  }

  protected static String generateBroadcastCall(final AbstractActor aa, long rc) {
    final String name = aa.getName();
    final Fifo inputFifo = aa.getDataInputPorts().get(0).getFifo();

    final StringBuilder wrapperOutArgs = new StringBuilder();
    for (final DataPort dp : aa.getDataOutputPorts()) {
      // call.append(", " + FpgaCodeGenerator.getFifoStreamName(dp.getFifo()));
      wrapperOutArgs.append(", " + FpgaCodeGenerator.getFifoStreamName(dp.getFifo()));
    }

    final String taskCall = "hls_thread_local hls::task " + name + "_task(" + name + ", "
        + FpgaCodeGenerator.getFifoStreamName(inputFifo) + wrapperOutArgs + ");";

    final String funcCall = name + "(" + FpgaCodeGenerator.getFifoStreamName(inputFifo) + wrapperOutArgs + ");"
        + "  \n";
    // final String forLoopCall = " for(int i = 0; i < " + rc + "; i++) {\n " + name + "("
    // + FpgaCodeGenerator.getFifoStreamName(inputFifo) + wrapperOutArgs + ");" + " \n}\n";

    final String call = "#ifdef LIGHTNINGSIM\n " + funcCall + "#else\n " + taskCall + "\n#endif\n\n";

    return call;
  }

}
