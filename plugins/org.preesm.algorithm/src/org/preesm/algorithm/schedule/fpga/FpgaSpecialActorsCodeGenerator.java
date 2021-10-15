package org.preesm.algorithm.schedule.fpga;

import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

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
    // only broadcast are supported for now
    for (final AbstractActor aa : flatGraph.getActors()) {
      if (aa instanceof BroadcastActor) {
        // generate the definition
        sb.append(generateBroadcastDefinition(aa));
        // map the call
        actorCalls.put(aa, generateBroadcastCall(aa));
      }
    }

    return sb.toString();
  }

  protected static String generateBroadcastDefinition(final AbstractActor aa) {
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
    // body
    def.append(typeElt + " tmp = " + inputName + ".read();\n");
    for (final DataPort dp : aa.getDataOutputPorts()) {
      def.append(dp.getName() + ".write(tmp);\n");
    }
    def.append("}\n\n");

    return def.toString();
  }

  protected static String generateBroadcastCall(final AbstractActor aa) {
    final String name = aa.getName();
    final Fifo inputFifo = aa.getDataInputPorts().get(0).getFifo();
    final StringBuilder call = new StringBuilder(name + "(" + FpgaCodeGenerator.getFifoStreamName(inputFifo));
    for (final DataPort dp : aa.getDataOutputPorts()) {
      call.append(", " + FpgaCodeGenerator.getFifoStreamName(dp.getFifo()));
    }
    call.append(");\n");
    return call.toString();
  }

}
