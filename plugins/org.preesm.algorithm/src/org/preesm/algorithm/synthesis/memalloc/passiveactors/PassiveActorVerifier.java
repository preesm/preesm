package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveInputPort;
import org.preesm.model.pisdf.PassiveOutputPort;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PortMemoryAnnotation;

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

  public static long getRealBufferSize(PassiveActor pa, PassiveScriptRunner psr, Map<PassivePort, Boolean> portMask) {

    long realBufferSize = 0;

    final long bufferSize = pa.getBufferSize();

    for (final PassivePort pp : pa.getAllPassivePorts()) {
      if (!portMask.containsKey(pp)) {
        throw new PreesmRuntimeException(
            "port mask doesn't contain passive port " + pp.getName() + " of actor " + pa.getName());
      }
      if (Boolean.FALSE.equals(portMask.get(pp))) {
        continue;
      }
      final long ratei = pp.getOppositePort().getExpression().evaluateAsLong();

      for (final Pair<Long, Long> beginjiEndji : psr.getBeginEndAllInstances(pp)) {
        final long beginji = beginjiEndji.getKey();
        realBufferSize = Math.max(realBufferSize, Math.max(beginji + ratei, bufferSize));
      }
    }
    PreesmLogger.getLogger().info("[PASSIVE ACTOR] actor + " + pa.getName() + " has a buffer size = "
        + pa.getBufferSize() + " and will have a real size = " + realBufferSize);
    return realBufferSize;
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

  public static boolean verifyPassivePortConditions(PassivePort pp, Map<AbstractVertex, Long> brv) {
    return portCondition1(pp, brv) && portCondition2(pp);
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

    final long neighborRepValue = brv.get(pp.getOppositePort().getContainingActor());
    return neighborRepValue >= paRepValue && neighborRepValue % paRepValue == 0;

  }

  /**
   * 2 conditions in 1 : (1) "The input ports are either marked with a write only or unused annotation, or are not
   * writing the same tokens" (2) "All output ports are marked with a read only annotation, or are not reading the same
   * tokens"
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
