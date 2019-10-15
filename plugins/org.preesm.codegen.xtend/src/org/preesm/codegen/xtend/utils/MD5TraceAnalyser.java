package org.preesm.codegen.xtend.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;

/**
 *
 */
public class MD5TraceAnalyser {

  /**
   */
  static class MD5Trace {
    List<Iteration> iterations;
    BufferState     initState;

    public MD5Trace(BufferState initState, List<Iteration> iterations) {
      this.initState = initState;
      this.iterations = iterations;
    }
  }

  /**
   *
   */
  static class Iteration {
    final long                       iterationNumber;
    final List<PostActorBufferState> actorTraces = new ArrayList<>();

    public Iteration(long parseLong) {
      this.iterationNumber = parseLong;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (PostActorBufferState bs : actorTraces) {
        sb.append(bs.toString() + "\n");
      }
      return sb.toString();
    }
  }

  /**
   *
   */
  static class BufferState {
    final String              actor;
    final Map<String, String> bufferMd5Map = new LinkedHashMap<>();

    public BufferState(final String actor) {
      this.actor = actor;
    }
  }

  /**
   */
  static class PostActorBufferState extends BufferState {
    List<String>    allowedInBufs  = new ArrayList<>();
    List<String>    allowedOutBufs = new ArrayList<>();
    final Iteration containingIteration;

    public PostActorBufferState(final Iteration iteration, final String actor) {
      super(actor);
      this.containingIteration = iteration;
    }

    @Override
    public String toString() {
      return containingIteration.iterationNumber + " - " + actor + " : IN" + allowedInBufs + " OUT" + allowedOutBufs;
    }
  }

  /**
   * @throws IOException
   *
   */
  public static void main(String[] args) throws IOException {
    final String read = PreesmResourcesHelper.getInstance().read("md5", MD5TraceAnalyser.class);
    final MD5Trace extractIterations = extractIterations(read);
    for (Iteration i : extractIterations.iterations) {
      System.out.println(i);
    }

  }

  private static MD5Trace extractIterations(final String traces) {
    final BufferState initState = new BufferState("INIT");
    final List<Iteration> list = new ArrayList<>();
    Map<Long, Iteration> itMap = new LinkedHashMap<>();
    Map<Long, String> actorMap = new LinkedHashMap<>();

    final String[] split = traces.split("\n");

    BufferState prevActorObj = null;
    BufferState currentActorState = null;

    for (final String line : split) {
      final String[] mainSep = line.split(" - ");

      // get iteration
      final String iterationLiteral = mainSep[0].split(" ")[1];
      final long iterationNumber = Long.parseLong(iterationLiteral);
      if (!itMap.containsKey(iterationNumber)) {
        final Iteration newIteration = new Iteration(iterationNumber);
        itMap.put(iterationNumber, newIteration);
        list.add(newIteration);
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
          final String actor = lastPart.substring(16);
          if (actor.equals("PREESM_BUFFERINIT")) {
            currentActorState = initState;
          } else {
            if (!actorMap.containsKey(actorIdNumber)) {
              actorMap.put(actorIdNumber, actor);
            } else {
              final String string = actorMap.get(actorIdNumber);
              if (!string.equals(actor)) {
                throw new PreesmRuntimeException("actor with same id but diff name");
              }
            }
            prevActorObj = currentActorState;
            PostActorBufferState pactorObj = new PostActorBufferState(iteration, actor);
            currentActorState = pactorObj;
            iteration.actorTraces.add(pactorObj);
            final String inOutBufs = mainSep[3];
            final String[] split2 = inOutBufs.split(" OUT");

            final String inDecl = split2[0];
            String inList = inDecl.substring(3, inDecl.length() - 1);
            if (inList.endsWith(", ")) {
              inList = inList.substring(0, inList.length() - 2);
            }
            final String[] split3 = inList.split(", ");
            for (String in : split3) {
              pactorObj.allowedInBufs.add(in);
            }

            final String outDecl = split2[1];
            if (outDecl.length() > 2) {
              String outList = inDecl.substring(1, inDecl.length() - 3);
              final String[] split4 = outList.split(", ");
              for (String out : split4) {
                pactorObj.allowedOutBufs.add(out);
              }
            }
          }
        } else if (lastPart.startsWith("preesm_md5_ZZZZ --")) {
          // end actor
        } else if (lastPart.startsWith("preesm_md5_")) {
          // buffer MD5
          if (currentActorState == null) {
            throw new PreesmRuntimeException();
          } else {
            final String substring = lastPart.substring(11);
            final String[] split2 = substring.split(" : ");
            final String bufName = split2[0];
            final String bufMD5 = split2[1];
            currentActorState.bufferMd5Map.put(bufName, bufMD5);

            if (currentActorState instanceof PostActorBufferState && prevActorObj != null) {
              // check if current actor is allowed to change bufName
              PostActorBufferState actorState = (PostActorBufferState) currentActorState;
              if (prevActorObj.bufferMd5Map.containsKey(bufName)) {
                final String prevMD5 = prevActorObj.bufferMd5Map.get(bufName);
                if (prevMD5.equals(bufMD5)) {
                  // no change : skip
                } else {
                  // actor did change the buffer. Was it one if its output ?
                  if (actorState.allowedOutBufs.contains(bufName)) {
                    // ok
                  } else {
                    throw new PreesmRuntimeException(actorState + "\n did changed " + bufName);
                  }
                }
              } else {
                // TODO : error : dump didnt go well
              }
            }
          }
        } else {
          // unsupported
          throw new UnsupportedOperationException("\"unsuported line: \" + line");
        }
      } else {
        // unsupported
        throw new UnsupportedOperationException("\"unsuported line: \" + line");
      }

    }

    return new MD5Trace(initState, list);
  }
}
