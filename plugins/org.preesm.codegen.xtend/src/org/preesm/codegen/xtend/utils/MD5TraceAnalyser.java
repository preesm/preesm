package org.preesm.codegen.xtend.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.commons.files.PreesmResourcesHelper;

/**
 *
 */
public class MD5TraceAnalyser {

  /**
   *
   */
  static class Iteration {
    final long             iterationNumber;
    final List<ActorTrace> actorTraces = new ArrayList<>();

    public Iteration(long parseLong) {
      this.iterationNumber = parseLong;
    }

  }

  /**
   *
   */
  static class ActorTrace {
    final Iteration iteration;

    public ActorTrace(final Iteration iteration) {
      this.iteration = iteration;
    }
  }

  /**
   * @throws IOException
   *
   */
  public static void main(String[] args) throws IOException {
    final String read = PreesmResourcesHelper.getInstance().read("md5_basic", MD5TraceAnalyser.class);
    extractFirstIterations(read);

  }

  private static List<Iteration> extractFirstIterations(final String traces) {
    final List<Iteration> res = new ArrayList<>();
    Map<Long, Iteration> itMap = new LinkedHashMap<Long, MD5TraceAnalyser.Iteration>();
    final String[] split = traces.split("\n");

    long prevActorId = -1;

    for (final String line : split) {
      final String[] mainSep = line.split(" - ");

      // get iteration
      final String iterationLiteral = mainSep[0].split(" ")[1];
      final long iterationNumber = Long.parseLong(iterationLiteral);
      if (!itMap.containsKey(iterationNumber)) {
        itMap.put(iterationNumber, new Iteration(iterationNumber));
      }
      final Iteration iteration = itMap.get(iterationNumber);

      // get actor ID
      final String actorIdLiteral = mainSep[1].split(" ")[1];
      final long actorIdNumber = Long.parseLong(actorIdLiteral);

      // get last part
      if (mainSep.length > 2) {
        final String lastPart = mainSep[2];
        if (lastPart.startsWith("preesm_md5_0000")) {
          // new actor
          System.out.println("start : " + lastPart);
        } else if (lastPart.startsWith("preesm_md5_ZZZZ --")) {
          // end actor
          System.out.println("end : " + lastPart);
        } else if (lastPart.startsWith("preesm_md5_")) {
          // fifo MD5
          // System.out.println("fifo : " + lastPart);
        } else {
          // unsupported
          throw new UnsupportedOperationException("\"unsuported line: \" + line");
        }
      } else {
        // unsupported
        throw new UnsupportedOperationException("\"unsuported line: \" + line");
      }

    }
    System.out.println(itMap.size());
    return res;
  }
}
