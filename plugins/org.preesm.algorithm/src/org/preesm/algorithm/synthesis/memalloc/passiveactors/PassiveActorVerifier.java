package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveInputPort;
import org.preesm.model.pisdf.PassiveOutputPort;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PortKind;
import org.preesm.model.pisdf.PortMemoryAnnotation;
import org.preesm.model.pisdf.SimplePassiveActor;

public class PassiveActorVerifier {

  private PassiveActorVerifier() {
  }

  /**
   * Verify spacing between begin and end pointers. If spacing is not good for at least one instance of one port, it
   * will throw an error.
   *
   * @param pa
   *          the passive actor
   */
  public static void verifyPassiveActorConditions(PassiveActor pa, PassiveScriptRunner psr) {

    actorCondition1(pa, psr);

  }

  /**
   * Verify the write spacing between begin and end pointers of all port of an actor. If one is not good, it will raise
   * an exception, saying the passive script must be reworked.
   *
   * @param pa
   *          the passive actor
   */
  private static void actorCondition1(PassiveActor pa, PassiveScriptRunner psr) {
    for (final PassivePort pp : pa.getAllPassivePorts()) {
      final Pair<Long, Long> globalBeginEnd = psr.getBeginEnd(pp);
      long rate = pp.getExpression().evaluateAsLong();
      final long bufferSize = pp.getSubBufferSize();

      String scriptName = "";
      try {
        final URI scriptURI = new URI(pp.getScript());
        final URL scriptURL = scriptURI.toURL();
        final String path = scriptURL.getPath();
        scriptName = path.substring(path.lastIndexOf('/') + 1);
      } catch (URISyntaxException | MalformedURLException e) {
        e.printStackTrace();
      }

      boolean pointersValuesAreCorrect = verifyPointerValue(globalBeginEnd.getKey(), bufferSize)
          && verifyPointerValue(globalBeginEnd.getValue(), bufferSize);

      if (!verifyPointersSpacing(globalBeginEnd.getKey(), globalBeginEnd.getValue(), rate, bufferSize)
          && pointersValuesAreCorrect) {

        throw new PreesmRuntimeException("port " + pp.getName() + " of actor " + pa.getName()
            + " has not the right spacing between its begin and end pointers. "
            + "Consider reworking the following passive script : " + scriptName);
      }

      int i = 0;
      for (final Pair<Long, Long> beginEnd : psr.getBeginEndAllInstances(pp)) {

        rate = pp.getOppositePort().getExpression().evaluateAsLong();

        pointersValuesAreCorrect = verifyPointerValue(beginEnd.getKey(), bufferSize)
            && verifyPointerValue(beginEnd.getValue(), bufferSize);

        if (!verifyPointersSpacing(beginEnd.getKey(), beginEnd.getValue(), rate, bufferSize)
            && pointersValuesAreCorrect) {

          throw new PreesmRuntimeException("port " + pp.getName() + " of actor " + pa.getName()
              + " has not the right spacing between its begin and end pointers on at least instance " + i + ". "
              + "Consider reworking the following passive script : " + scriptName);
        }
        i++;
      }
    }
  }

  /**
   * verify the passive port conditions
   *
   * @param pp
   *          the passive port of a complex passive actor
   * @param brv
   *          the basic repetition vector of the parent graph of the current graph, because if pp is linked to an
   *          interface, the method will go look seek actors in the parent graph.
   * @return true if the conditions are valid, false otherwise.
   */
  public static boolean verifyPassivePortConditions(PassivePort pp, Map<AbstractVertex, Long> brv) {
    final boolean condition1 = portCondition1(pp, brv);
    final boolean condition2 = portCondition2(pp);
    return condition1 && condition2;

  }

  private static DataPort getNextPort(DataPort pp) {
    DataPort nextPort = pp.getOppositePort();
    final AbstractActor nextActor = nextPort.getContainingActor();
    if (nextActor instanceof final DataInterface dataInterface) {
      nextPort = dataInterface.getGraphPort();
      return getNextPort(nextPort);
    }
    if (nextActor instanceof final SimplePassiveActor spa) {
      nextPort = nextPort.getKind() == PortKind.DATA_INPUT ? spa.getOutputPort() : spa.getInputPort();
      return getNextPort(nextPort);
    }
    return nextPort;
  }

  /**
   * "All passiveActor's neighbors actors have a repetition value that is a multiple of the passiveActor's repetition
   * value."
   *
   * @param pa
   *          the passive actor
   * @return true if condition is respected, false otherwise.
   */
  private static boolean portCondition1(PassivePort pp, Map<AbstractVertex, Long> brv) {

    final long paRepValue = brv.get(pp.getContainingActor());

    final DataPort oppositePort = getNextPort(pp);
    final AbstractActor opposite = oppositePort.getContainingActor();
    if (brv.get(opposite) == null) {
      throw new PreesmRuntimeException(
          "Actor " + opposite.getName() + " in graph " + opposite.getContainingPiGraph().getName()
              + " is not present in brv. If you are a developer, be sure to pass the brv of the parent graph, "
              + "and not of the current graph. Otherwise, please contact PREESM developers.");
    }

    // Computing the repetition value of the opposite actor
    final long neighborRepValue = opposite instanceof DataInterface ? 1 : brv.get(opposite);

    // Even if pp.getContainingActor() and opposite are not in the same graph and the the cluster is repeated more than
    // one time, It will be taken into account in the brv.
    return neighborRepValue >= paRepValue && neighborRepValue % paRepValue == 0;

  }

  /**
   * 2 conditions in 1 : <br>
   * 1. The ports linked to input ports are either marked with a write only or unused annotation, or are not writing the
   * same tokens.<br>
   * 2. The ports linked to output ports are either marked with a read only annotation, or are not reading the same
   * tokens
   *
   * @param pp
   *          the passive port
   * @return true if the condition is respected, false otherwise.
   */
  private static boolean portCondition2(PassivePort pp) {
    boolean result = false;
    if (pp instanceof final PassiveInputPort pip) {
      final PortMemoryAnnotation portAnnotation = pip.getOppositePort().getAnnotation();
      result = portAnnotation == PortMemoryAnnotation.WRITE_ONLY || portAnnotation == PortMemoryAnnotation.UNUSED;

    } else if (pp instanceof final PassiveOutputPort pop) {
      result = pop.getOppositePort().getAnnotation() == PortMemoryAnnotation.READ_ONLY;
    }

    // If input port doesn't have a an annotation, it is not necessary a problem.
    // If every instances (composed of a begin and a end pointer) are not recovering another instance AND if every other
    // ports are not recovering the tokens , then there will be no
    // writing/reading concurrency since no token will be written/read 2 times.
    if (!result) {
      result = true;

      // Inner verification
      final List<Pair<Long, Long>> allInstances = pp.getBeginiEndi();
      for (int i = 0; i < allInstances.size(); i++) {

        final long begin1 = allInstances.get(i).getKey();
        final long end1 = allInstances.get(i).getValue();

        result &= verifyOverlooping(pp);
        if (!result) {
          break;
        }

        for (int x = 1; x < allInstances.size() - 1; x++) {
          if (x == i) {
            continue;
          }

          final long begin2 = allInstances.get(x).getKey();
          final long end2 = allInstances.get(x).getValue();

          if (!result) {
            break;
          }

          boolean resultTmp = false;

          resultTmp |= end2 <= begin1 && begin2 < begin1;
          resultTmp |= end2 > begin1 && begin2 >= end1;
          resultTmp |= end2 <= begin1 && begin2 >= end1;

          result &= resultTmp;
        }
      }

      // Outer verification
      final Pair<Long, Long> beginEnd = pp.getBeginjEndj();
      final long begin = beginEnd.getKey();
      final long end = beginEnd.getValue();

      final PassiveActor parent = (PassiveActor) pp.getContainingActor();
      for (final PassivePort otherPort : parent.getAllPassivePorts()) {
        if (otherPort == pp) {
          continue;
        }
        final Pair<Long, Long> otherBeginEnd = otherPort.getBeginjEndj();
        final long otherBegin = otherBeginEnd.getKey();
        final long otherEnd = otherBeginEnd.getValue();

        boolean resultTmp = false;

        resultTmp |= otherEnd <= begin && otherBegin < begin;
        resultTmp |= otherEnd > begin && otherBegin >= end;
        resultTmp |= otherEnd <= begin && otherBegin >= end;

        result &= resultTmp;
      }
    }
    return result;
  }

  private static boolean verifyOverlooping(PassivePort pp) {
    final long rate = pp.getOppositePort().getExpression().evaluateAsLong();
    final long bufferSize = pp.getSubBufferSize();

    return rate <= bufferSize;

  }

  private static boolean verifyPointersSpacing(long begin, long end, long rate, long bufferSize) {
    boolean result;

    // Number of time rate is bigger than bufferSize
    // For instance, if rate is 6 and bufferSize is 4, then ratio will be 4 x ⌊6/4⌋ = 4
    final long ratio = bufferSize * (int) (rate / bufferSize);

    if (begin > end) {
      result = rate == bufferSize - begin + end + ratio;
    } else {
      result = rate == end - begin + ratio;
    }
    return result;
  }

  private static boolean verifyPointerValue(long pointer, long bufferSize) {
    return pointer >= 0 && pointer < bufferSize;
  }

}
