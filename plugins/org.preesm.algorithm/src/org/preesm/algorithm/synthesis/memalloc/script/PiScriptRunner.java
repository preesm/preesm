/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2021 - 2022)
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
package org.preesm.algorithm.synthesis.memalloc.script;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.ParseException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.memory.script.Buffer;
import org.preesm.algorithm.memory.script.CheckPolicy;
import org.preesm.algorithm.memory.script.Match;
import org.preesm.algorithm.memory.script.MatchType;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.synthesis.memalloc.meg.PiMemoryExclusionGraph;
import org.preesm.algorithm.synthesis.memalloc.meg.PiMemoryExclusionVertex;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.files.URLHelper;
import org.preesm.commons.files.URLResolver;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PortMemoryAnnotation;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.scenario.SimulationInfo;

/**
 *
 */
public class PiScriptRunner {

  private static final Logger logger = PreesmLogger.getLogger();

  private static final String SCRIPT_FOLDER = "scripts";

  private static final String JOIN_SCRIPT        = "join.bsh";
  private static final String FORK_SCRIPT        = "fork.bsh";
  private static final String ROUNDBUFFER_SCRIPT = "roundbuffer.bsh";
  private static final String BROADCAST_SCRIPT   = "broadcast.bsh";

  // Paths to the special scripts files
  private static final String JOIN        = PiScriptRunner.SCRIPT_FOLDER + IPath.SEPARATOR + PiScriptRunner.JOIN_SCRIPT;
  private static final String FORK        = PiScriptRunner.SCRIPT_FOLDER + IPath.SEPARATOR + PiScriptRunner.FORK_SCRIPT;
  private static final String ROUNDBUFFER = PiScriptRunner.SCRIPT_FOLDER + IPath.SEPARATOR
      + PiScriptRunner.ROUNDBUFFER_SCRIPT;
  private static final String BROADCAST   = PiScriptRunner.SCRIPT_FOLDER + IPath.SEPARATOR
      + PiScriptRunner.BROADCAST_SCRIPT;

  private CheckPolicy checkPolicy = CheckPolicy.NONE;

  private SimulationInfo simulationInfo;

  /**
   * A {@link Map} that associates each {@link DAGVertex} from the {@link #scriptedVertices} map to the result of the
   * successful execution of its script. The result is stored as a {@link Pair} of {@link List} of {@link Buffer}. The
   * first {@link List} contains the input {@link Buffer buffers} and the second contains output {@link Buffer buffers}.
   */
  private final Map<AbstractActor, Pair<List<PiBuffer>, List<PiBuffer>>> scriptResults = new LinkedHashMap<>();

  /**
   * A {@link Map} that associates each {@link DAGVertex} with a memory script to this memory script {@link File}.
   */
  private final Map<AbstractActor, URL> scriptedVertices = new LinkedHashMap<>();

  /**
   * Each {@link List} of {@link Buffer} stored in this {@link List} corresponds to an independent connected
   * {@link Match} tree resulting from the execution of the memory scripts.
   */
  private final List<List<PiBuffer>> bufferGroups = new ArrayList<>();

  private CharSequence log = "";

  private final boolean printTodo;

  private boolean generateLog = true;

  private long nbBuffersBefore = 0;
  private long nbBuffersAfter  = 0;
  private long sizeBefore;
  private long sizeAfter;

  private long getSizeBeforeInBit() {
    return sizeBefore;
  }

  private long getSizeBeforeInByte() {
    return (sizeBefore + 7L) / 8L;
  }

  private long getSizeAfterInBit() {
    return sizeAfter;
  }

  private long getSizeAfterInByte() {
    return (sizeAfter + 7L) / 8L;
  }

  /**
   * This property is used to represent the alignment of buffers in memory. The same value, or a multiple should always
   * be used in the memory allocation.
   */
  private final long alignment;

  private final boolean falseSharingPreventionFlag;

  /**
   * Check the results obtained when running the {@link #run()} method. Checks are performed according to the current
   * {@link #setCheckPolicy(CheckPolicy)}. The {@link #checkResult(File,Pair)} method is used to perform the checks.
   * Vertices whose script results do not pass the checks are removed from the {@link #scriptResults} map.
   */
  public void check() {
    if (this.checkPolicy != CheckPolicy.NONE) {
      final List<AbstractActor> invalidVertices = new ArrayList<>();

      // Do the checks
      this.scriptResults.forEach((vertex, result) -> {
        final URL dingenskirchen = this.scriptedVertices.get(vertex);
        if (!checkResult(dingenskirchen, result)) {
          invalidVertices.add(vertex);
        }
      });

      // Remove invalid results
      invalidVertices.stream().forEach(this.scriptedVertices::remove);
    }
  }

  /**
   * This method perform several checks on the {@link Buffer buffers} resulting from the evaluation of a script. The
   * checked points are:
   * <ul>
   * <li>If all {@link Match matches} are reciprocal. A {@link Match} belonging to the {@link Buffer#getMatchTable()
   * matchTable} of a {@link Buffer} is reciprocal if the {@link Match#getBuffer() matched buffer} has a reciprocal
   * {@link Match} in its {@link Buffer#getMatchTable() match table}.</li>
   * <li>If there are inter-siblings matches. (i.e. inter-inputs or inter- outputs matches.)</li>
   * <li>If ranges matched multiple times are not matched with other ranges that are
   * {@link #getMultipleMatchRange(Buffer) matched multiple times}. For example, with a,b and c three {@link Buffer
   * buffers}, if a[i] is matched with b[j], b[k], and c[l] then b[j] (or b[k] or c[l]) cannot be matched with a
   * {@link Buffer} different than a[i].</li>
   * </ul>
   * If one of the checks is not valid, the method will return false and a warning will be displayed in the
   * {@link Logger log}.
   *
   * @param script
   *          the script {@link File} from which the result {@link Buffer buffers} result.
   * @param result
   *          a {@link Pair} of {@link List} of {@link Buffer buffers}. The key {@link List} contains input
   *          {@link Buffer buffers} and the value {@link List} contain output {@link Buffer buffers}.
   * @return <code>true</code> if all checks were valid, <code>false</code> otherwise.
   */
  private boolean checkResult(final URL script, final Pair<List<PiBuffer>, List<PiBuffer>> result) {
    final List<PiBuffer> allBuffers = new ArrayList<>(result.getKey());
    allBuffers.addAll(result.getValue());

    // Check that all matches are reciprocal
    // For all buffers
    final boolean res1 = allBuffers.stream().allMatch(PiBuffer::isReciprocal);

    if (!res1 && (this.checkPolicy == CheckPolicy.FAST)) {
      final String message = "Error in " + script + ":\nOne or more match is not reciprocal."
          + " Please set matches only by using Buffer.matchWith() methods.";
      PiScriptRunner.logger.log(Level.WARNING, message);
    } else if (!res1 && (this.checkPolicy == CheckPolicy.THOROUGH)) {
      allBuffers.stream().forEach(localBuffer -> {
        // for all matcheSet
        final boolean res = localBuffer.isReciprocal();
        if (!res && (this.checkPolicy == CheckPolicy.THOROUGH)) {
          final List<PiMatch> flattenList = new ArrayList<>();
          localBuffer.matchTable.values().forEach(flattenList::addAll);
          final String buffDefs = flattenList.stream().filter(match -> {
            final List<PiMatch> remoteMatches = match.getRemoteBuffer().matchTable.get(match.getRemoteIndex());
            return !((remoteMatches != null) && remoteMatches.contains(new PiMatch(match.getRemoteBuffer(),
                match.getRemoteIndex(), localBuffer, match.getLocalIndex(), match.getLength())));
          }).toList().toString();

          final String message = "Error in " + script + ":\nBuffer " + localBuffer + " has nonreciprocal matches:\n"
              + buffDefs + "\nPlease set matches only by using Buffer.matchWith() methods.";
          PiScriptRunner.logger.log(Level.WARNING, message);
        }

      });
    }

    // Find inter-inputs and inter-outputs matches
    final boolean res2 = result.getKey().stream().allMatch(buffer -> {
      final List<PiMatch> flatten = new ArrayList<>();
      buffer.matchTable.values().forEach(flatten::addAll);
      return flatten.stream().allMatch(match -> result.getValue().contains(match.getRemoteBuffer()));
    }) && result.getValue().stream().allMatch(buffer -> {
      final List<PiMatch> flatten = new ArrayList<>();
      buffer.matchTable.values().forEach(flatten::addAll);
      return flatten.stream().allMatch(match -> result.getKey().contains(match.getRemoteBuffer()));
    });

    if (!res2 && (this.checkPolicy == CheckPolicy.FAST)) {
      final String message = "Error in " + script + ":\nOne or more match links an input (or an output) to another."
          + "\nPlease set matches only between inputs and outputs.";
      PiScriptRunner.logger.log(Level.WARNING, message);
    } else if (!res2 && (this.checkPolicy == CheckPolicy.THOROUGH)) {

      result.getKey().stream().forEach(buffer -> {
        final List<PiMatch> flatten = new ArrayList<>();
        buffer.matchTable.values().forEach(flatten::addAll);
        flatten.stream().forEach(match -> {
          if (!result.getValue().contains(match.getRemoteBuffer())) {
            PiScriptRunner.logger.warning(() -> "Error in " + script + ":\nMatch " + match
                + " links an input to another." + "\nPlease set matches only between inputs and outputs.");
          }
        });
      });

      result.getValue().stream().forEach(buffer -> {
        final List<PiMatch> flatten = new ArrayList<>();
        buffer.matchTable.values().forEach(flatten::addAll);
        flatten.stream().forEach(match -> {
          if (!result.getKey().contains(match.getRemoteBuffer())) {
            PiScriptRunner.logger.warning(() -> "Error in " + script + ":\nMatch " + match
                + " links an output to another." + "\nPlease set matches only between inputs and outputs.");
          }
        });
      });
    }

    // Find ranges from input and output with multiple matches
    final List<Pair<PiBuffer, List<PiRange>>> multipleRanges = allBuffers.stream()
        .map(b -> new Pair<>(b, b.getMultipleMatchRange())).toList();

    // There can be no multiple match range in the output buffers !
    final boolean res3 = multipleRanges.stream()
        .allMatch(p -> result.getKey().contains(p.getKey()) || (p.getValue().isEmpty()));

    if (!res3 && (this.checkPolicy == CheckPolicy.FAST)) {
      final String message = "Error in " + script
          + ":\nMatching multiple times a range of an output buffer is not allowed.";
      PiScriptRunner.logger.log(Level.WARNING, message);
    } else if (!res3 && (this.checkPolicy == CheckPolicy.THOROUGH)) {
      multipleRanges.stream().forEach(p -> {
        if (!(result.getKey().contains(p.getKey()) || (p.getValue().isEmpty()))) {
          final String message = "Error in " + script + ":\nMatching multiple times output buffer " + p.getKey()
              + " is not allowed." + "\nRange matched multiple times:" + p.getValue();
          PiScriptRunner.logger.log(Level.WARNING, message);
        }
      });
    }

    return res1 && res2 && res3;
  }

  /**
   * This method finds the memory scripts associated to the {@link DAGVertex vertices} of the input
   * {@link DirectedAcyclicGraph}. When a script path is set in the property of the {@link SDFVertex} associated to a
   * {@link DAGVertex} of the graph, scripts are either found in a path relative to the original {@link SDFGraph} file,
   * or in the plugin project "scripts" directory. If an invalid script path is set, a warning message will be written
   * in the log.
   *
   * @param dag
   *          the {@link DirectedAcyclicGraph} whose vertices memory scripts are retrieved.
   */
  protected int findScripts(final PiGraph dag) {

    // TODO : extract script lookup and initialization

    // Create temporary containers for special scripts files
    // and extract special script files and fill the map with it

    // Special scripts files
    final Map<String, URL> specialScriptFiles = new LinkedHashMap<>();

    // Script files already found
    final Map<String, URL> scriptFiles = new LinkedHashMap<>();

    PiScriptRunner.putSpecialScriptFile(specialScriptFiles, PiScriptRunner.JOIN);
    PiScriptRunner.putSpecialScriptFile(specialScriptFiles, PiScriptRunner.FORK);
    PiScriptRunner.putSpecialScriptFile(specialScriptFiles, PiScriptRunner.ROUNDBUFFER);
    PiScriptRunner.putSpecialScriptFile(specialScriptFiles, PiScriptRunner.BROADCAST);

    for (final AbstractActor dagVertex : dag.getAllActors()) {

      if (dagVertex instanceof ForkActor) {
        associateScriptToSpecialVertex(dagVertex, "fork", specialScriptFiles.get(PiScriptRunner.FORK));
      } else if (dagVertex instanceof JoinActor) {
        associateScriptToSpecialVertex(dagVertex, "join", specialScriptFiles.get(PiScriptRunner.JOIN));
      } else if (dagVertex instanceof RoundBufferActor) {
        associateScriptToSpecialVertex(dagVertex, "roundbuffer", specialScriptFiles.get(PiScriptRunner.ROUNDBUFFER));
      } else if (dagVertex instanceof BroadcastActor) {
        associateScriptToSpecialVertex(dagVertex, "broadcast", specialScriptFiles.get(PiScriptRunner.BROADCAST));
      } else if (dagVertex instanceof final Actor actor) {
        final String pathString = actor.getMemoryScriptPath();
        if (pathString != null) {
          // Retrieve the script path as a relative path to the graph
          URL scriptFile = scriptFiles.get(pathString);
          if (scriptFile == null) {
            scriptFile = URLResolver.findFirst(pathString);
          }
          if (scriptFile != null) {
            scriptFiles.put(pathString, scriptFile);
            this.scriptedVertices.put(dagVertex, scriptFile);
          } else {
            final String message = "Memory script of vertex " + dagVertex.getName() + " is invalid: \"" + pathString
                + "\". Change it in the graphml editor.";
            PiScriptRunner.logger.log(Level.WARNING, message);
          }
        }
      } else {
        // nothing
      }
    }

    return this.scriptedVertices.size();
  }

  /**
   * Associate a script file to a special DAGVertex if this script file have been extracted, display an error otherwise
   */
  private void associateScriptToSpecialVertex(final AbstractActor dagVertex, final String vertexName,
      final URL scriptFile) {
    if (scriptFile == null) {
      final String message = "Memory script [" + scriptFile + "] of [" + vertexName
          + "] vertices not found. Please contact Preesm developers.";
      throw new IllegalStateException(message);
    }
    this.scriptedVertices.put(dagVertex, scriptFile);
  }

  /**
   * Get the special script file at the right path and put it into the map
   */
  private static void putSpecialScriptFile(final Map<String, URL> specialScriptFiles, final String filePath) {
    final URL url = PreesmResourcesHelper.getInstance().resolve(filePath, PiScriptRunner.class);
    if (url != null) {
      specialScriptFiles.put(filePath, url);
    }
  }

  /**
   * This method process the {@link #scriptResults} in order to simplify them with {@link #simplifyResult(List,List)}.
   * Then, it extracts mergeable buffers. This method must be called after {@link #run()} and {@link #check()} have been
   * successfully called.
   */
  public void process() {

    // Simplify results
    this.scriptResults.entrySet().stream()
        .forEach(e -> PiScriptRunner.simplifyResult(e.getValue().getKey(), e.getValue().getValue()));

    // Identify divisible buffers
    this.scriptResults.entrySet().stream().forEach(e -> PiScriptRunner.identifyDivisibleBuffers(e.getValue()));

    // Update output buffers for alignment
    if (this.falseSharingPreventionFlag && this.alignment > 0) {
      this.scriptResults.entrySet().stream().forEach(e -> e.getValue().getValue().stream().filter(it -> {
        // All outputs except the mergeable one linked only to read_only
        // inputs within their actor must be enlarged.
        // In other terms, only buffers that will never be written by their
        // producer actor or consumer actor are not enlarged since these
        // buffer will only be used to divide data written by other actors.
        final List<PiMatch> flatten = new ArrayList<>();
        it.matchTable.values().stream().forEach(flatten::addAll);
        return !(it.originallyMergeable && flatten.stream().allMatch(m -> m.getRemoteBuffer().originallyMergeable));
        // Enlarge the buffer
        // New range mergeability is automatically handled by
        // the setMinIndex(int) function
      }).forEach(buffer -> PiScriptRunner.enlargeForAlignment(buffer, this.alignment, this.printTodo)));

    }

    // Identify matches types
    this.scriptResults.entrySet().stream().forEach(e -> PiScriptRunner.identifyMatchesType(e.getValue()));

    // Identify match that may cause a inter-output merge (not inter-input since
    // at this point, multiple matches of output range is forbidden)

    this.scriptResults.entrySet().stream().forEach(
        e -> PiScriptRunner.identifyConflictingMatchCandidates(e.getValue().getKey(), e.getValue().getValue()));

    // Identify groups of chained buffers from the scripts and dag
    final List<List<AbstractActor>> groups = groupVertices();

    // Update input buffers on the group border for alignment
    if (this.falseSharingPreventionFlag && this.alignment > 0) {

      // For each group
      groups.stream()
          .forEach(group -> group.stream().forEach(dagVertex -> dagVertex.getDataInputPorts().stream().forEach(port -> {
            final Fifo edge = port.getFifo();
            // If the edge producer is not part of the group
            if (!group.contains(edge.getSource())) {
              // Retrieve the corresponding buffer.
              this.scriptResults.get(dagVertex).getKey().stream()
                  .filter(buffer -> buffer.getLoggingEdgeName().getSourcePort().getContainingActor().getName()
                      .equals(edge.getSourcePort().getContainingActor().getName()))
                  .forEach(it -> PiScriptRunner.enlargeForAlignment(it, this.alignment, this.printTodo));
            }
          })));
    }

    // Process the groups one by one
    this.sizeBefore = this.sizeAfter = this.nbBuffersBefore = this.nbBuffersAfter = 0L;
    groups.stream().forEach(this::processGroup);

    if (isGenerateLog()) {
      this.log = "# Memory scripts summary" + '\n' + "- Independent match trees : *" + groups.size() + "*" + '\n'
          + "- Total number of buffers in these trees: From " + this.nbBuffersBefore + " to " + this.nbBuffersAfter
          + " buffers." + "\n" + "- Total size of these buffers: From " + this.getSizeBeforeInByte() + " to "
          + this.getSizeAfterInByte() + " bytes ("
          + ((100.0 * (this.getSizeBeforeInBit() - this.getSizeAfterInBit())) / this.getSizeBeforeInBit()) + "%)."
          + "\n\n" + "# Match tree optimization log" + '\n' + this.log;
    }
  }

  private static void enlargeForAlignment(final PiBuffer buffer, final long alignment, final boolean printTodo) {
    if (printTodo) {
      PiScriptRunner.logger.log(Level.FINEST,
          "Alignment minus one is probably sufficient + Only enlarge [0-Alignment,Max+alignment];");
      // TODO description :
      // This method is called only for output buffers.
      // Since only "real" tokens of the output buffers are written back
      // from cache (in non-coherent architectures), alignment is here
      // only to ensure that these "real" tokens are not cached in the
      // same cache line as other real tokens.
      // Consequently, enlarging buffers as follows is sufficient to
      // prevent cache-line alignment issues:
      // minIdx = min(0 - (alignment -1), minIdx)
      // maxIdx = max(maxIdx + (alignment -1), maxIdx)
      //
    }
    final long oldMinIndex = buffer.minIndex;
    if ((oldMinIndex == 0) || (((oldMinIndex) % alignment) != 0)) {
      buffer.minIndex = ((oldMinIndex / alignment) - 1) * alignment;

      // New range is indivisible with end of buffer
      PiRange.lazyUnion(buffer.indivisibleRanges, new PiRange(buffer.minIndex, oldMinIndex + 1));
    }

    final long oldMaxIndex = buffer.maxIndex;
    if ((oldMaxIndex == (buffer.getNbTokens() * buffer.getTokenSize())) || (((oldMaxIndex) % alignment) != 0)) {
      buffer.maxIndex = ((oldMaxIndex / alignment) + 1) * alignment;

      // New range is indivisible with end of buffer
      PiRange.lazyUnion(buffer.indivisibleRanges, new PiRange(oldMaxIndex - 1, buffer.maxIndex));
    }

    // Update matches of the buffer
    // Since the updateMatches update the remote buffer matchTable of a match
    // except the given match, we create a fake match with the current
    // buffer as a remote buffer
    final PiMatch fakeMatch = new PiMatch(null, 0, buffer, 0, 0);
    PiBuffer.updateMatches(fakeMatch);

    final List<PiMatch> flatten = new ArrayList<>();
    buffer.matchTable.values().forEach(flatten::addAll);
    PiBuffer.updateConflictingMatches(flatten);
  }

  /**
   * For each {@link Buffer} passed as a parameter, this method scan the {@link Match} in the
   * {@link Buffer#getMatchTable() matchTable} and set. their {@link Match#getType() type}. Matches whose
   * {@link Match#getLocalBuffer() localBuffer} and {@link Match#getRemoteBuffer() remoteBuffer} belong to the same
   * {@link List} of {@link Match} will cause the method to throw a {@link RuntimeException}. Other {@link Match} are
   * marked as <code>FORWARD</code> or <code>BACKWARD</code>.
   *
   * @param result
   *          {@link Pair} of {@link List} of {@link Buffer}. The {@link Pair} key and value respectively contain input
   *          and output {@link Buffer} of an actor.
   */
  private static void identifyMatchesType(final Pair<List<PiBuffer>, List<PiBuffer>> result) {

    result.getKey().stream().forEach(it -> {
      final List<PiMatch> flatten = new ArrayList<>();
      it.matchTable.values().forEach(flatten::addAll);
      flatten.stream().forEach(match -> {
        if (result.getKey().contains(match.getRemoteBuffer())) {
          throw new PreesmRuntimeException("Inter-sibling matches are no longer allowed.");
        }
        match.setType(MatchType.FORWARD);
      });
    });

    result.getValue().stream().forEach(it -> {
      final List<PiMatch> flatten = new ArrayList<>();
      it.matchTable.values().forEach(flatten::addAll);
      flatten.stream().forEach(match -> {
        if (result.getValue().contains(match.getRemoteBuffer())) {
          throw new PreesmRuntimeException("Inter-sibling matches are no longer allowed.");
        }
        match.setType(MatchType.BACKWARD);
      });
    });

  }

  /**
   * Also fill the {@link Buffer#getDivisibilityRequiredMatches() divisibilityRequiredMatches} {@link List}.
   */
  private static void identifyDivisibleBuffers(final Pair<List<PiBuffer>, List<PiBuffer>> result) {
    final List<PiBuffer> allBuffers = new ArrayList<>(result.getKey());
    allBuffers.addAll(result.getValue());

    // A buffer is potentially divisible
    // If it has several matches (that were not merged by the
    // simplifyResult). (Because if the buffer only has one
    // contiguous match, a divided buffer is not possible, cf
    // Buffer.simplifyMatches() comments.)

    // if it is totally matched, so that all parts of the divided
    // buffer can still be accessed in an immediately logical way.
    // With the successive merges, unmatched ranges might become
    // part of an indivisible range with a matched range. However
    // since this kind of behavior is not intuitive, we set not
    // completely matched buffers as indivisible from the start so
    // that the developer knows where tokens are only by looking at
    // its actor script.

    // Note that at this point, virtual tokens are always matched
    // so this constraint ensure that future virtual tokens are
    // always attached to real token by an overlapping
    // indivisible range !
    final List<PiBuffer> divisibleCandidates = allBuffers.stream()
        .filter(buffer -> (buffer.matchTable.size() > 1) && buffer.isCompletelyMatched()).toList();

    divisibleCandidates.stream().forEach(buffer -> {
      // All are divisible BUT it will not be possible to match divided
      // buffers together (checked later)
      final List<PiMatch> drMatches = new ArrayList<>();
      buffer.divisibilityRequiredMatches.add(drMatches);

      final List<PiMatch> flatten = new ArrayList<>();
      buffer.matchTable.values().stream().forEach(flatten::addAll);
      flatten.stream().forEach(it -> {
        final PiRange r = it.getLocalRange();
        PiRange.lazyUnion(buffer.indivisibleRanges, r);
        drMatches.add(it);
      });
    });

    // All other buffers are not divisible
    allBuffers.removeAll(divisibleCandidates);
    allBuffers.stream().forEach(it -> it.indivisibleRanges.add(new PiRange(it.minIndex, it.maxIndex)));
  }

  /**
   * This method fills the {@link Match#getConflictCandidates() conflictCandidates} {@link Match} {@link List} of all
   * the {@link Match matches} contained in the {@link Buffer#getMatchTable() matchTable} of the {@link Buffer} passed
   * as parameter. Two {@link Match} are potentially conflicting if:
   * <ul>
   * <li>They have the same {@link Match#getRemoteBuffer()}</li>
   * </ul>
   *
   * @param inputs
   *          {@link List} of input {@link Buffer} of an actor.
   * @param outputs
   *          {@link List} of output {@link Buffer} of an actor.
   */
  private static void identifyConflictingMatchCandidates(final List<PiBuffer> inputs, final List<PiBuffer> outputs) {

    final List<PiBuffer> allBuffers = new ArrayList<>(inputs.size() + outputs.size());
    allBuffers.addAll(inputs);
    allBuffers.addAll(outputs);

    // Identify potentially conflicting matches
    // For each Buffer
    for (final PiBuffer buffer : allBuffers) {
      final List<PiMatch> flatten = new ArrayList<>();
      buffer.matchTable.values().stream().forEach(flatten::addAll);

      // Get the matches
      final List<PiMatch> matches = new ArrayList<>(flatten);

      // Update the potential conflict list of all matches
      matches.stream().forEach(match -> match.getReciprocate().getConflictCandidates()
          .addAll(matches.stream().filter(it -> it != match).map(PiMatch::getReciprocate).toList()));

    }

    // Identify the already conflicting matches
    for (final PiBuffer buffer : allBuffers) {
      // for Each match
      final List<PiMatch> matchList = new ArrayList<>();
      buffer.matchTable.values().stream().forEach(matchList::addAll);
      PiBuffer.updateConflictingMatches(matchList);
    }
  }

  /**
   * Process the groups generated by the groupVertices method.
   *
   * @return the total amount of memory saved
   */
  private void processGroup(final List<AbstractActor> vertices) {

    // Get all the buffers
    final List<PiBuffer> buffers = new ArrayList<>();
    vertices.stream().forEach(it -> {
      final Pair<List<PiBuffer>, List<PiBuffer>> pair = this.scriptResults.get(it);
      // Buffer that were already merged are not processed
      buffers.addAll(pair.getKey().stream().filter(buf -> buf.appliedMatches.size() == 0).toList());
      buffers.addAll(pair.getValue().stream().filter(buf -> buf.appliedMatches.size() == 0).toList());
    });

    // copy the buffer list for later use in MEG update
    final List<PiBuffer> bufferList = new ArrayList<>(buffers);
    getBufferGroups().add(bufferList);
    this.nbBuffersBefore = this.nbBuffersBefore + buffers.size();

    final long before = buffers.stream().map(PiBuffer::getBufferSize).reduce((l1, l2) -> l1 + l2).orElse(0L);

    this.sizeBefore = this.sizeBefore + before;
    if (isGenerateLog()) {
      this.log = this.log + "## Tree of " + buffers.size() + " buffers" + '\n' + "### Original buffer list:" + '\n'
          + "> " + buffers + "" + "\n\n" + "### Match application log: " + '\n';
    }

    // Iterate the merging algorithm until no buffers are merged
    int step = 0;
    boolean stop = false;
    do {

      // Sort the buffers in alphabetical order to enforce similarities
      // between successive run
      Collections.sort(buffers, (a, b) -> {
        final int nameRes = a.getVertexName().compareTo(b.getVertexName());
        return (nameRes != 0) ? nameRes : a.name.compareTo(b.name);
      });

      final List<PiBuffer> matchedBuffers = switch (step) {
        // First step: Merge non-conflicting buffer with a unique match
        case 0 -> processGroupStep0(buffers);
        // Second step: Merge divisible buffers with multiple matches and no conflict
        case 1 -> processGroupStep1(buffers);
        // Third step: Same as step 0, but test forward matches of buffers only
        case 2 -> processGroupStep2(buffers);
        // Fourth step: Like case 1 but considering forward only or backward only matches
        case 3 -> processGroupStep3(buffers);
        // Fifth step: Mergeable buffers with a unique backward match that have conflict(s)
        case 4 -> processGroupStep4(buffers);
        case 5 -> processGroupStep5(buffers);
        case 6 -> processGroupStep6(buffers);
        case 7 -> processGroupStep7(buffers);
        default -> throw new PreesmRuntimeException("Unsupported step number " + step);
      };

      if (!matchedBuffers.isEmpty()) {
        step = 0;
      } else {
        step = step + 1;
      }

      // Stop if only buffers with no match remains
      stop = buffers.stream().allMatch(it -> it.matchTable.isEmpty());

    } while ((step < 8) && !stop);

    final long after = buffers.stream().map(PiBuffer::getBufferSize).reduce((l1, l2) -> l1 + l2).orElse(0L);

    if (isGenerateLog()) {
      this.log = this.log + "\n" + "### Tree summary:" + '\n';
      this.log = this.log + "- From " + bufferList.size() + " buffers to " + buffers.size() + " buffers." + "\n";
      this.log = this.log + "- From " + (before + 7L) / 8L + " bytes to " + (after + 7L) / 8L + " bytes ("
          + ((100.0 * (before - after)) / before) + "%)" + "\n\n";
    }

    // Log unapplied matches (if any)
    if (isGenerateLog()) {
      this.log = this.log + "### Unapplied matches:" + "\n>";
      final List<PiMatch> logged = new ArrayList<>();
      for (final PiBuffer buffer : bufferList) {
        final List<PiMatch> flatten = new ArrayList<>();
        buffer.matchTable.values().stream().forEach(flatten::addAll);

        for (final PiMatch match : flatten.stream().filter(it -> !it.isApplied()).toList()) {
          if (!logged.contains(match.getReciprocate())) {
            this.log = this.log + match.getOriginalMatch().toString() + ", ";
            logged.add(match);
          }
        }
      }
      this.log = this.log + "\n";
    }

    this.nbBuffersAfter = this.nbBuffersAfter + buffers.size();
    this.sizeAfter = this.sizeAfter + after;
  }

  /**
   * Match {@link Buffer buffers} with a unique {@link Match} in their {@link Buffer#getMatchTable() matchTable} if:
   * <ul>
   * <li>The unique match covers the whole real token range of the buffer</li>
   * <li>The match is not {@link Match#getConflictingMatches() conflicting} with any other match</li>
   * <li>The match and its {@link Match#getReciprocate() reciprocate} are applicable.</li>
   * </ul>
   *
   * @param buffers
   *          {@link List} of {@link Buffer} of the processed group. Matched buffers will be removed from this list by
   *          the method.
   * @return a {@link List} of merged {@link Buffer}.
   */
  private List<PiBuffer> processGroupStep0(final List<PiBuffer> buffers) {
    final List<PiBuffer> candidates = new ArrayList<>();

    for (final PiBuffer candidate : buffers) {
      final Entry<Long, List<PiMatch>> entry = candidate.matchTable.entrySet().iterator().next();

      // Returns true if:
      // There is a unique match
      boolean test = (candidate.matchTable.size() == 1) && (entry.getValue().size() == 1);

      // that covers at index 0 (or less)
      test = test && (entry.getValue().get(0).getLocalIndivisibleRange().getStart() <= 0);

      // and ends at the end of the buffer (or more)
      test = test && (entry.getValue().get(0).getLocalIndivisibleRange().getEnd() >= (candidate.getBufferSizeInBit()));

      // entry.key + entry.value.head.length >= candidate.nbTokens * candidate.tokenSize
      // and is not involved in any conflicting range
      final PiMatch match = entry.getValue().get(0);
      test = test && (match.getConflictingMatches().isEmpty()) && match.isApplicable()
          && match.getReciprocate().isApplicable();

      // and remote buffer is not already in the candidates list
      test = test && !candidates.contains(entry.getValue().get(0).getRemoteBuffer());

      if (test) {
        candidates.add(candidate);
      }
    }

    // If there are candidates, apply the matches
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "- __Step 0 - " + candidates.size() + " matches__" + "\n>";
    }
    for (final PiBuffer candidate : candidates) {
      final List<PiMatch> value = candidate.matchTable.entrySet().iterator().next().getValue();
      if (isGenerateLog()) {
        this.log = this.log + "" + value.get(0) + " ";
      }
      candidate.applyMatches(value);
    }
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "\n";
    }

    buffers.removeAll(candidates);

    // Return the matched buffers
    return candidates;
  }

  /**
   * Match {@link Buffer buffers} that are divisible if:
   * <ul>
   * <li>The buffer is {@link Buffer#isDivisible() divisible}.</li>
   * <li>Its matches cover the whole real token range of the buffer</li>
   * <li>Its matches are not {@link Match#getConflictingMatches() conflicting} with any other match.</li>
   * <li>The buffer has no {@link Buffer#getMultipleMatchRange(Buffer) multipleMatchRange}.</li>
   * </ul>
   *
   * @param buffers
   *          {@link List} of {@link Buffer} of the processed group. Matched buffers will be removed from this list by
   *          the method.
   * @return a {@link List} of merged {@link Buffer}.
   */
  private List<PiBuffer> processGroupStep1(final List<PiBuffer> buffers) {

    final List<PiBuffer> candidates = new ArrayList<>();

    for (final PiBuffer candidate : buffers) {

      // Find all divisible buffers with multiple match and no
      // conflict that are not matched in another divisible buffer
      // (if any)
      // Has a non-empty matchTable
      boolean test = candidate.matchTable.size() != 0;

      // is divisible
      test = test && candidate.isDivisible();

      final List<PiMatch> flatten = new ArrayList<>();
      candidate.matchTable.values().stream().forEach(flatten::addAll);

      test = test && flatten.stream().allMatch(it ->
      // Is not involved in any conflicting range
      (it.getConflictingMatches().isEmpty()) && it.isApplicable() && it.getReciprocate().isApplicable());

      // Has no multiple match Range.
      test = test && (candidate.getMultipleMatchRange().isEmpty());

      // No need to check the divisibilityRequiredMatches since
      // the only matches of the Buffer are the one
      // responsible for the division
      // and remote buffer(s) are not already in the candidates list
      test = test && flatten.stream().map(PiMatch::getRemoteBuffer).allMatch(it -> !candidates.contains(it));

      if (test) {
        candidates.add(candidate);
      }
    }

    // If there are candidates, apply the matches
    if (isGenerateLog() && !candidates.isEmpty()) {

      final int fold = candidates.stream().map(buf -> {
        final List<PiMatch> flatten = new ArrayList<>();
        buf.matchTable.values().stream().forEach(flatten::addAll);
        return flatten.size();
      }).reduce((i1, i2) -> i1 + i2).orElse(0);
      this.log = this.log + "- __Step 1 - " + fold + " matches__ " + "\n>";
    }
    for (final PiBuffer candidate : candidates) {
      final List<PiMatch> flatten = new ArrayList<>();
      candidate.matchTable.values().stream().forEach(flatten::addAll);

      if (isGenerateLog()) {
        this.log = this.log + flatten.stream().map(Object::toString).collect(Collectors.joining(", "));
      }
      PiScriptRunner.applyDivisionMatch(candidate, flatten);
    }
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "\n";
    }

    buffers.removeAll(candidates);

    // Return the matched buffers
    return candidates;
  }

  /**
   * Match {@link Buffer buffers} with a unique <code>FORWARD</code> {@link Match} (or a unique <code>BACKWARD</code>
   * {@link Match}). in their {@link Buffer#getMatchTable() matchTable} if:
   * <ul>
   * <li>The unique match covers the whole real token range of the buffer</li>
   * <li>The match is not {@link Match#getConflictingMatches() conflicting} with any other match</li>
   * <li>The match and its {@link Match#getReciprocate() reciprocate} are applicable.</li>
   * </ul>
   *
   * @param buffers
   *          {@link List} of {@link Buffer} of the processed group. Matched buffers will be removed from this list by
   *          the method.
   * @return a {@link List} of merged {@link Buffer}.
   */
  private List<PiBuffer> processGroupStep2(final List<PiBuffer> buffers) {
    final Map<PiBuffer, MatchType> candidates = new LinkedHashMap<>();
    final List<PiBuffer> involved = new ArrayList<>();

    for (final PiBuffer candidate : buffers) {
      final Iterator<
          MatchType> iterType = new ArrayList<>(Arrays.asList(MatchType.FORWARD, MatchType.BACKWARD)).iterator();
      boolean test = false;
      while (iterType.hasNext() && !test) {
        final MatchType currentType = iterType.next();
        final List<PiMatch> flatten = new ArrayList<>();
        candidate.matchTable.values().stream().forEach(flatten::addAll);

        final List<PiMatch> matches = flatten.stream().filter(it -> it.getType() == currentType).toList();

        // Returns true if:
        // There is a unique forward match
        test = matches.size() == 1;

        // that begins at index 0 (or less)
        test = test && (matches.get(0).getLocalIndivisibleRange().getStart() <= 0);

        // and ends at the end of the buffer (or more)
        test = test && (matches.get(0).getLocalIndivisibleRange().getEnd() >= (candidate.getBufferSizeInBit()));

        // and is not involved in any conflicting match
        test = test && (matches.get(0).getConflictingMatches().isEmpty());

        // and is both backward and forward applicable
        test = test && matches.get(0).isApplicable() && matches.get(0).getReciprocate().isApplicable();

        // and remote buffer is not already involved in a match
        test = test && !involved.contains(matches.get(0).getRemoteBuffer());
        test = test && !involved.contains(candidate);

        if (test) {
          candidates.put(candidate, currentType);
          involved.add(matches.get(0).getRemoteBuffer());
          involved.add(candidate);
        }
      }
    }

    // If there are candidates, apply the matches
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "- __Step 2 - " + candidates.size() + " matches__" + "\n>";
    }
    for (final Entry<PiBuffer, MatchType> candidate : candidates.entrySet()) {
      final List<PiMatch> flatten = new ArrayList<>();
      candidate.getKey().matchTable.values().stream().forEach(flatten::addAll);

      if (isGenerateLog()) {
        this.log = this.log + ""
            + flatten.stream().filter(it -> it.getType() == candidate.getValue()).findFirst().orElse(null) + " ";
      }
      candidate.getKey().applyMatches(
          Arrays.asList(flatten.stream().filter(it -> it.getType() == candidate.getValue()).findFirst().orElse(null)));
    }
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "\n";
    }

    buffers.removeAll(candidates.keySet());

    // Return the matched buffers
    return new ArrayList<>(candidates.keySet());
  }

  /**
   * Match {@link Buffer buffers} that are divisible with their <code>FORWARD
   * </code> {@link Match matches} only (or a their <code>BACKWARD</code> {@link Match matches} only) if:
   * <ul>
   * <li>The buffer is {@link Buffer#isDivisible() divisible}.</li>
   * <li>Its matches cover the whole real token range of the buffer</li>
   * <li>Its matches are not {@link Match#getConflictingMatches() conflicting} with any other match.</li>
   * <li>The buffer has no {@link Buffer#getMultipleMatchRange(Buffer) multipleMatchRange}.</li>
   * <li>The buffer verify the {@link Buffer#doesCompleteRequiredMatches(Iterable)} condition.</li>
   * </ul>
   *
   * @param buffers
   *          {@link List} of {@link Buffer} of the processed group. Matched buffers will be removed from this list by
   *          the method.
   * @return a {@link List} of merged {@link Buffer}.
   */
  private List<PiBuffer> processGroupStep3(final List<PiBuffer> buffers) {
    final Map<PiBuffer, MatchType> candidates = new LinkedHashMap<>();

    for (final PiBuffer candidate : buffers) {
      final Iterator<MatchType> iterType = Arrays.asList(MatchType.FORWARD, MatchType.BACKWARD).iterator();
      boolean test = false;
      while (iterType.hasNext() && !test) {
        final MatchType currentType = iterType.next();

        final List<PiMatch> flatten = new ArrayList<>();
        candidate.matchTable.values().stream().forEach(flatten::addAll);

        final List<PiMatch> matches = flatten.stream().filter(it -> it.getType() == currentType).toList();

        // Returns true if:
        // Has a several matches
        test = !matches.isEmpty();

        // is divisible
        test = test && candidate.isDivisible();

        // and is not involved in any conflicting match
        test = test && matches.stream().allMatch(
            it -> (it.getConflictingMatches().isEmpty()) && it.isApplicable() && it.getReciprocate().isApplicable());

        // Matches have no multiple match Range.
        test = test && (PiBuffer.getOverlappingRanges(matches).isEmpty());

        // Check divisibilityRequiredMatches
        test = test && candidate.doesCompleteRequiredMatches(matches);

        // and remote buffer(s) are not already in the candidates list
        test = test && matches.stream().allMatch(it -> !candidates.keySet().contains(it.getRemoteBuffer()));

        if (test) {
          candidates.put(candidate, currentType);
        }
      }
    }

    // If there are candidates, apply the matches
    if (isGenerateLog() && !candidates.isEmpty()) {
      final long fold = candidates.entrySet().stream().map(e -> {
        final List<PiMatch> flatten = new ArrayList<>();
        e.getKey().matchTable.values().stream().forEach(flatten::addAll);
        return flatten.stream().filter(it -> it.getType() == e.getValue()).count();
      }).reduce((l1, l2) -> l1 + l2).orElse(0L);

      this.log = this.log + "- __Step 3 - " + fold + " matches__" + "\n>";
    }
    for (final Entry<PiBuffer, MatchType> candidate : candidates.entrySet()) {

      final List<PiMatch> flatten = new ArrayList<>();
      candidate.getKey().matchTable.values().stream().forEach(flatten::addAll);

      if (isGenerateLog()) {
        this.log = this.log + flatten.stream().filter(it -> it.getType() == candidate.getValue()).map(Object::toString)
            .collect(Collectors.joining(", "));
      }

      PiScriptRunner.applyDivisionMatch(candidate.getKey(),
          flatten.stream().filter(it -> it.getType() == candidate.getValue()).collect(Collectors.toList()));
    }
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "\n";
    }

    buffers.removeAll(candidates.keySet());

    // Return the matched buffers
    return new ArrayList<>(candidates.keySet());
  }

  /**
   * Match {@link Buffer buffers} with a unique {@link Match} in their {@link Buffer#getMatchTable() matchTable} if:
   * <ul>
   * <li>The unique match covers the whole real token range of the buffer</li>
   * <li>The match is {@link Match#getConflictingMatches() conflicting} with other match(es)</li>
   * <li>The buffer is mergeable</li>
   * <li>The match and its {@link Match#getReciprocate() reciprocate} are applicable.</li>
   * </ul>
   *
   * @param buffers
   *          {@link List} of {@link Buffer} of the processed group. Matched buffers will be removed from this list by
   *          the method.
   * @return a {@link List} of merged {@link Buffer}.
   */
  private List<PiBuffer> processGroupStep4(final List<PiBuffer> buffers) {
    final List<PiBuffer> candidates = new ArrayList<>();
    final List<PiBuffer> involved = new ArrayList<>();

    for (final PiBuffer candidate : buffers) {

      final Entry<Long, List<PiMatch>> entry = candidate.matchTable.entrySet().iterator().next();

      // Returns true if:
      // There is a unique match
      boolean test = (candidate.matchTable.size() == 1) && (entry.getValue().size() == 1);

      // Is backward
      test = test && (entry.getValue().get(0).getType() == MatchType.BACKWARD);

      // that begins at index 0 (or less)
      test = test && (entry.getValue().get(0).getLocalIndivisibleRange().getStart() <= 0);

      // and ends at the end of the buffer (or more)
      test = test && (entry.getValue().get(0).getLocalIndivisibleRange().getEnd() >= (candidate.getBufferSizeInBit()));

      // and is involved in any conflicting range
      final PiMatch match = entry.getValue().get(0);
      test = test && (!match.getConflictingMatches().isEmpty()) && match.isApplicable()
          && match.getReciprocate().isApplicable();

      // buffer is fully mergeable
      test = test && (candidate.mergeableRanges.size() == 1)
          && (candidate.mergeableRanges.get(0).getStart() == candidate.minIndex)
          && (candidate.mergeableRanges.get(0).getEnd() == candidate.maxIndex);

      // and remote and local buffer are not already in the candidates list
      test = test && !involved.contains(entry.getValue().get(0).getRemoteBuffer());
      test = test && !involved.contains(candidate);

      if (test) {
        candidates.add(candidate);
        involved.add(entry.getValue().get(0).getRemoteBuffer());
        involved.add(candidate);
      }
    }

    // If there are candidates, apply the matches
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "- __Step 4 - " + candidates.size() + " matches__" + "\n>";
    }
    for (final PiBuffer candidate : candidates) {
      if (isGenerateLog()) {
        this.log = this.log + "" + candidate.matchTable.entrySet().iterator().next().getValue().get(0) + " ";
      }
      candidate.applyMatches(candidate.matchTable.entrySet().iterator().next().getValue());
    }
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "\n";
    }

    buffers.removeAll(candidates);

    // Return the matched buffers
    return candidates;
  }

  /**
   * Match {@link Buffer buffers} that are divisible with their <code>BACKWARD
   * </code> {@link Match matches} only if:
   * <ul>
   * <li>The buffer is {@link Buffer#isDivisible() divisible}.</li>
   * <li>Its matches cover the whole real token range of the buffer</li>
   * <li>Its matches are {@link Match#getConflictingMatches() conflicting} with other match(s) but are applicable.</li>
   * <li>The buffer is fully mergeable</li>
   * <li>The matches are not overlapping with each other.</li>
   * <li>The buffer verify the {@link Buffer#doesCompleteRequiredMatches(Iterable)} condition.</li>
   * </ul>
   *
   * @param buffers
   *          {@link List} of {@link Buffer} of the processed group. Matched buffers will be removed from this list by
   *          the method.
   * @return a {@link List} of merged {@link Buffer}.
   */
  private List<PiBuffer> processGroupStep5(final List<PiBuffer> buffers) {
    final List<PiBuffer> candidates = new ArrayList<>();

    for (final PiBuffer candidate : buffers) {
      final List<PiMatch> flatten = new ArrayList<>();
      candidate.matchTable.values().stream().forEach(flatten::addAll);
      final List<PiMatch> matches = flatten.stream().filter(it -> it.getType() == MatchType.BACKWARD).toList();

      // Returns true if:
      // Has a several matches
      boolean test = !matches.isEmpty();

      // is divisible
      test = test && candidate.isDivisible();

      // and is involved in conflicting match(es)
      test = test && !matches.stream().allMatch(it -> it.getConflictingMatches().isEmpty());

      // All matches are applicable
      test = test && matches.stream().allMatch(it -> it.isApplicable() && it.getReciprocate().isApplicable());

      // buffer is fully mergeable (Since buffer is fully mergeable
      // even if division matches are conflicting with each other
      // this will not be a problem since they are mergeable)
      test = test && (candidate.mergeableRanges.size() == 1)
          && (candidate.mergeableRanges.get(0).getStart() == candidate.minIndex)
          && (candidate.mergeableRanges.get(0).getEnd() == candidate.maxIndex);

      // Matches have no multiple match Range (on the local buffer side).
      test = test && (PiBuffer.getOverlappingRanges(matches).isEmpty());

      // Check divisibilityRequiredMatches
      test = test && candidate.doesCompleteRequiredMatches(matches);

      // and remote buffer(s) are not already in the candidates list
      test = test && matches.stream().allMatch(it -> !candidates.contains(it.getRemoteBuffer()));

      if (test) {
        candidates.add(candidate);

      }
    }

    // If there are candidates, apply the matches
    if (isGenerateLog() && !candidates.isEmpty()) {
      final long fold = candidates.stream().map(c -> {
        final List<PiMatch> flatten = new ArrayList<>();
        c.matchTable.values().stream().forEach(flatten::addAll);
        return (long) flatten.size();
      }).reduce((l1, l2) -> l1 + l2).orElse(0L);
      this.log = this.log + "- __Step 5 - " + fold + " matches__" + "\n>";
    }
    for (final PiBuffer candidate : candidates) {
      final List<PiMatch> flatten = new ArrayList<>();
      candidate.matchTable.values().stream().forEach(flatten::addAll);

      if (isGenerateLog()) {
        this.log = this.log + flatten.stream().map(Object::toString).collect(Collectors.joining(", "));
      }
      PiScriptRunner.applyDivisionMatch(candidate,
          flatten.stream().filter(it -> it.getType() == MatchType.BACKWARD).toList());
    }
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "\n";
    }

    buffers.removeAll(candidates);

    // Return the matched buffers
    return candidates;
  }

  /**
   * Match {@link Buffer buffers} with a unique {@link Match} in their {@link Buffer#getMatchTable() matchTable} if:
   * <ul>
   * <li>The unique match covers the whole real token range of the buffer</li>
   * <li>The match is {@link Match#getConflictingMatches() conflicting} with other match(es) but is applicable</li>
   * <li>The buffer is partially or not mergeable</li>
   * <li>The match and its {@link Match#getReciprocate() reciprocate} are applicable.</li>
   * </ul>
   *
   * @param buffers
   *          {@link List} of {@link Buffer} of the processed group. Matched buffers will be removed from this list by
   *          the method.
   * @return a {@link List} of merged {@link Buffer}.
   */
  private List<PiBuffer> processGroupStep6(final List<PiBuffer> buffers) {

    final Map<PiBuffer, MatchType> candidates = new LinkedHashMap<>();
    final List<PiBuffer> involved = new ArrayList<>();

    // Largest buffers first for this step.
    Collections.sort(buffers, (a, b) -> {
      // Largest buffer first
      final int cmp = Long.compare(a.getBufferSize(), b.getBufferSize());
      // Alphabetical order for buffers of equal size
      if (cmp != 0) {
        return cmp;
      }
      final int nameRes = a.getVertexName().compareTo(b.getVertexName());
      return (nameRes != 0) ? nameRes : a.name.compareTo(b.name);
    });

    for (final PiBuffer candidate : buffers) {
      final Iterator<MatchType> iterType = Arrays.asList(MatchType.FORWARD, MatchType.BACKWARD).iterator();
      boolean test = false;
      while (iterType.hasNext() && !test) {
        final MatchType currentType = iterType.next();

        final List<PiMatch> flatten = new ArrayList<>();
        candidate.matchTable.values().stream().forEach(flatten::addAll);
        final List<PiMatch> matches = flatten.stream().filter(it -> it.getType() == currentType).toList();

        // Returns true if:
        // There is a unique forward match
        test = matches.size() == 1;

        // that begins at index 0 (or less)
        test = test && (matches.get(0).getLocalIndivisibleRange().getStart() <= 0);

        // and ends at the end of the buffer (or more)
        test = test && (matches.get(0).getLocalIndivisibleRange().getEnd() >= (candidate.getBufferSizeInBit()));

        if (test) {
          // and is involved in conflicting range
          final PiMatch match = matches.get(0);
          test = test && (!match.getConflictingMatches().isEmpty()) && match.isApplicable()
              && match.getReciprocate().isApplicable();

          // buffer not fully mergeable, no test needed,
          // such a buffer it would have been matched in step 4
          // Conflicting matches of the match are not already in the candidate list
          test = test && match.getConflictingMatches().stream()
              .allMatch(it -> !candidates.keySet().contains(it.getLocalBuffer()));

          // and buffers are not already in the candidates list
          test = test && !involved.contains(matches.get(0).getRemoteBuffer());
          test = test && !involved.contains(candidate);
        }
        if (test) {
          candidates.put(candidate, currentType);
          involved.add(matches.get(0).getRemoteBuffer());
          involved.add(candidate);
        }
      }
    }

    // If there are candidates, apply the matches
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "- __Step 6 - " + candidates.size() + " matches__" + "\n>";
    }

    for (final Entry<PiBuffer, MatchType> candidate : candidates.entrySet()) {

      final List<PiMatch> flatten = new ArrayList<>();
      candidate.getKey().matchTable.values().stream().forEach(flatten::addAll);

      if (isGenerateLog()) {
        this.log = this.log + flatten.stream().filter(it -> it.getType() == candidate.getValue()).findFirst()
            .map(Object::toString).orElse(null);
      }

      candidate.getKey().applyMatches(
          Arrays.asList(flatten.stream().filter(it -> it.getType() == candidate.getValue()).findFirst().orElse(null)));
    }

    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "\n";
    }

    buffers.removeAll(candidates.keySet());

    // Return the matched buffers
    return new ArrayList<>(candidates.keySet());
  }

  /**
   * Match {@link Buffer buffers} that are divisible with their <code>FORWARD
   * </code> {@link Match matches} only (or a their <code>BACKWARD</code> {@link Match matches} only) if:
   * <ul>
   * <li>The buffer is {@link Buffer#isDivisible() divisible}.</li>
   * <li>Its matches cover the whole real token range of the buffer</li>
   * <li>Its matches are {@link Match#getConflictingMatches() conflicting} with other matches but are applicable.</li>
   * <li>The matches are not overlapping.</li>
   * <li>The buffer verify the {@link Buffer#doesCompleteRequiredMatches(Iterable)} condition.</li>
   * </ul>
   *
   * @param buffers
   *          {@link List} of {@link Buffer} of the processed group. Matched buffers will be removed from this list by
   *          the method.
   * @return a {@link List} of merged {@link Buffer}.
   */
  private List<PiBuffer> processGroupStep7(final List<PiBuffer> buffers) {
    final Map<PiBuffer, MatchType> candidates = new LinkedHashMap<>();

    for (final PiBuffer candidate : buffers) {
      final Iterator<MatchType> iterType = Arrays.asList(MatchType.FORWARD, MatchType.BACKWARD).iterator();
      boolean test = false;
      while (iterType.hasNext() && !test) {
        final MatchType currentType = iterType.next();

        final List<PiMatch> flatten = new ArrayList<>();
        candidate.matchTable.values().stream().forEach(flatten::addAll);
        final List<PiMatch> matches = flatten.stream().filter(it -> it.getType() == currentType).toList();

        // Returns true if:
        // Has a several matches
        test = !matches.isEmpty();

        // is divisible
        test = test && candidate.isDivisible();

        // and is involved in conflicting match
        test = test && matches.stream().allMatch(
            it -> (!it.getConflictingMatches().isEmpty()) && it.isApplicable() && it.getReciprocate().isApplicable());

        // Unless the matches are backward AND the buffer is mergeable
        // the matches are backward AND the buffer is mergeable
        test = test
            // the matches must not be conflicting with each other
            && (((currentType == MatchType.BACKWARD) && (candidate.mergeableRanges.size() == 1)
                && (candidate.mergeableRanges.get(0).getStart() == candidate.minIndex)
                && (candidate.mergeableRanges.get(0).getEnd() == candidate.maxIndex))
                // the matches must not be conflicting with each other
                || matches.stream()
                    .allMatch(it -> it.getConflictingMatches().stream().allMatch(m -> !matches.contains(m))));

        // Matches have no multiple match Range (on the local buffer side).
        test = test && (PiBuffer.getOverlappingRanges(matches).isEmpty());

        // Check divisibilityRequiredMatches
        test = test && candidate.doesCompleteRequiredMatches(matches);

        // Conflicting matches of the matches are not already in the candidate list
        test = test && matches.stream().allMatch(
            it -> it.getConflictingMatches().stream().allMatch(m -> !candidates.keySet().contains(m.getLocalBuffer())));

        // and remote buffer(s) are not already in the candidates list
        test = test && matches.stream().allMatch(it -> !candidates.keySet().contains(it.getRemoteBuffer()));

        if (test) {
          candidates.put(candidate, currentType);
        }
      }
    }

    // If there are candidates, apply the matches
    if (isGenerateLog() && !candidates.isEmpty()) {
      final long fold = candidates.entrySet().stream().map(e -> {
        final List<PiMatch> flatten = new ArrayList<>();
        e.getKey().matchTable.values().stream().forEach(flatten::addAll);
        return flatten.stream().filter(it -> it.getType() == e.getValue()).count();
      }).reduce((l1, l2) -> l1 + l2).orElse(0L);

      this.log = this.log + "- __Step 7 - " + fold + " matches__ " + "\n>";
    }
    for (final Entry<PiBuffer, MatchType> candidate : candidates.entrySet()) {
      final List<PiMatch> flatten = new ArrayList<>();
      candidate.getKey().matchTable.values().stream().forEach(flatten::addAll);

      if (isGenerateLog()) {
        this.log = this.log + flatten.stream().filter(it -> it.getType() == candidate.getValue()).map(Object::toString)
            .collect(Collectors.joining(", "));
      }
      PiScriptRunner.applyDivisionMatch(candidate.getKey(),
          flatten.stream().filter(it -> it.getType() == candidate.getValue()).collect(Collectors.toList()));
    }
    if (isGenerateLog() && !candidates.isEmpty()) {
      this.log = this.log + "\n";
    }

    buffers.removeAll(candidates.keySet());

    // Return the matched buffers
    return new ArrayList<>(candidates.keySet());
  }

  /**
  *
  */
  public PiScriptRunner(final boolean falseSharingPreventionFlag, final long alignment) {
    // kdesnos: Data alignment is supposed to be equivalent
    // to no alignment from the script POV. (not 100% sure of this)
    this.alignment = (alignment <= 0) ? -1 : alignment;
    this.printTodo = false;

    this.falseSharingPreventionFlag = falseSharingPreventionFlag;
  }

  /**
   * Called only for divisible buffers with multiple match and no conflict that are not matched in another divisible
   * buffer
   */
  private static void applyDivisionMatch(final PiBuffer buffer, final List<PiMatch> matches) {

    // In the current version, the buffer only contains
    // the matches necessary and sufficient for the division (i.e. no multiple matched ranges)
    // To process this special case in the future, some matches will have
    // to be changes: e.g. siblings will become forward or things like that
    // . For a simpler version, simply remove those other matches.
    // The match table will be modified by the applyMatch method, so we need a copy of it to iterate !
    // Remove the matches from each other conflict candidates
    matches.stream().forEach(it -> {
      it.getConflictCandidates().removeAll(matches);
      it.getReciprocate().getConflictCandidates().removeAll(matches.stream().map(PiMatch::getReciprocate).toList());
      it.getConflictingMatches().removeAll(matches);
      it.getReciprocate().getConflictingMatches().removeAll(matches.stream().map(PiMatch::getReciprocate).toList());
    });

    // apply the matches of the buffer one by one
    buffer.applyMatches(matches);
  }

  /**
   * The purpose of this method is to create groups of {@link DAGVertex} which satisfy the following conditions:
   * <ul>
   * <li>{@link DAGVertex Vertices} are associated to a memory script</li>
   * <li>{@link DAGVertex Vertices} of the same group are strongly connected via {@link DAGEdge FIFOs}</li>
   * </ul>
   * The {@link #scriptResults} attribute of the calling {@link PiScriptRunner} are updated by this method. In
   * particular, a {@link Buffer#matchWith(long,Buffer,long,long) match} is added between buffers of different actors
   * that correspond to the same SDFEdges. This method must be called after
   * {@link PiScriptRunner#identifyDivisibleBuffer()} as it set to indivisible the buffers that are on the border of
   * groups.
   *
   * @return a {@link List} of groups. Each group is itself a {@link List} of {@link DAGVertex}.
   */
  private List<List<AbstractActor>> groupVertices() {

    // Each dag vertex can be involved in at most one group
    final List<List<AbstractActor>> groups = new ArrayList<>();
    final List<AbstractActor> dagVertices = new ArrayList<>(this.scriptResults.keySet());
    while (!dagVertices.isEmpty()) {

      // Get the first dagVertex
      final AbstractActor dagSeedVertex = dagVertices.remove(0);

      // Create a new group
      final List<AbstractActor> group = new ArrayList<>();
      group.add(dagSeedVertex);

      // Identify other vertices that can be put into the group
      List<AbstractActor> newVertices = new ArrayList<>();
      newVertices.add(dagSeedVertex);

      final List<PiBuffer> intraGroupBuffer = new ArrayList<>();
      while (!newVertices.isEmpty()) {

        // Initialize the group size
        final int groupSize = group.size();

        // For all vertices from the newVertices list
        // check if a successors/predecessor can be added to the group
        for (final AbstractActor dagVertex : newVertices) {
          final List<AbstractActor> candidates = new ArrayList<>();
          final List<Fifo> inEdges = dagVertex.getDataInputPorts().stream().map(DataPort::getFifo).toList();
          final List<Fifo> outEdges = dagVertex.getDataOutputPorts().stream().map(DataPort::getFifo).toList();

          inEdges.stream().forEach(it -> candidates.add(it.getSourcePort().getContainingActor()));
          outEdges.stream().forEach(it -> candidates.add(it.getTargetPort().getContainingActor()));

          List<AbstractActor> addedVertices = group.subList(groupSize, group.size());
          for (final AbstractActor candidate : candidates) {
            if (addedVertices.contains(candidate) || newVertices.contains(candidate)
                || dagVertices.contains(candidate)) {

              // Match the buffers corresponding to the edge
              // between vertices "dagVertex" and "candidate"
              // Get the sdfEdges
              EList<Fifo> lookupFifos = dagVertex.getContainingPiGraph().lookupFifos(dagVertex, candidate);
              if (lookupFifos.isEmpty()) {
                lookupFifos = dagVertex.getContainingPiGraph().lookupFifos(candidate, dagVertex);
              }

              // For edges between newVertices, only process if the dagVertex
              // is the source (to avoid matching the pair of buffer twice)
              boolean validBuffers = false;
              final boolean isBetweenNewVertices = newVertices.contains(candidate);
              if (!isBetweenNewVertices || (lookupFifos.stream().anyMatch(fifo -> fifo.getSource() == dagVertex))) {

                // Add match between the two buffers that
                // correspond to the sdf edge(s) between vertex
                // and it
                final List<PiBuffer> bufferCandidates = new ArrayList<>();

                for (final AbstractActor v : Arrays.asList(dagVertex, candidate)) {
                  final Pair<List<PiBuffer>, List<PiBuffer>> pair = this.scriptResults.get(v);
                  bufferCandidates.addAll(pair.getKey());
                  bufferCandidates.addAll(pair.getValue());
                }
                for (final Fifo fifo : lookupFifos) {

                  // Find the 2 buffers corresponding to this sdfEdge
                  final List<PiBuffer> buffers = bufferCandidates.stream().filter(it -> it.getLoggingEdgeName() == fifo)
                      .toList();
                  if (buffers.size() == 2) {
                    validBuffers = true;

                    // Match them together
                    final PiMatch match = buffers.get(0).matchWith(0, buffers.get(1), 0, buffers.get(0).getNbTokens());
                    final PiMatch forwardMatch;
                    if (buffers.get(0).getVertexName().equals(fifo.getSourcePort().getContainingActor().getName())) {
                      match.setType(MatchType.FORWARD);
                      match.getReciprocate().setType(MatchType.BACKWARD);
                      forwardMatch = match;
                    } else {
                      match.setType(MatchType.BACKWARD);
                      match.getReciprocate().setType(MatchType.FORWARD);
                      forwardMatch = match.getReciprocate();
                    }

                    // Apply the forward match immediately
                    // (we always apply the forward match to enforce
                    // reproducibility of the processing)
                    forwardMatch.getLocalBuffer().applyMatches(Arrays.asList(forwardMatch));

                    // Save matched buffer
                    intraGroupBuffer.add(match.getLocalBuffer());
                    intraGroupBuffer.add(match.getRemoteBuffer());

                  }
                }
              }

              // Add the vertex to the group (if not already in
              // it) and if there was valid buffers)
              if (!group.contains(candidate) && validBuffers) {
                group.add(candidate);
                dagVertices.remove(candidate);
                addedVertices = group.subList(groupSize, group.size());
              }
            }
          }
        }

        // Update the newVertices list (we do not use sublists here because it causes
        // a ConcurrentModificationException
        newVertices = new ArrayList<>(group.subList(groupSize, group.size()));
      }

      // Set as indivisible all buffers that are on the edge of the group.
      group.stream().forEach(it -> {
        final Pair<List<PiBuffer>, List<PiBuffer>> results = this.scriptResults.get(it);
        final List<PiBuffer> flatten = new ArrayList<>(results.getKey());
        flatten.addAll(results.getValue());

        flatten.stream().filter(buf -> !intraGroupBuffer.contains(buf))
            .forEach(buf -> PiRange.lazyUnion(buf.indivisibleRanges, new PiRange(buf.minIndex, buf.maxIndex)));
      });

      // The group is completed, save it
      groups.add(group);
    }
    return groups;
  }

  /**
   * This method run the scripts that were found during the call to {@link #findScripts()}. As a result, the
   * {@link #scriptResults} is filled.<br>
   * <br>
   *
   * If the execution of a script fails, the {@link Interpreter} error message will be printed in the {@link Logger log}
   * as a warning.<br>
   * <br>
   * The {@link #check(List,List)} method is also used after each script execution to verify the validity of the script
   * results. If the results are not valid, they will not be stored in the {@link #scriptResults} {@link Map}, and a
   * warning will be printed in the {@link Logger log}.
   */
  public void run() throws EvalError {
    // For each vertex with a script
    for (final Entry<AbstractActor, URL> e : this.scriptedVertices.entrySet()) {
      final AbstractActor key = e.getKey();
      final URL value = e.getValue();
      runScript(key, value);
    }
  }

  /**
  *
  */
  private void runScript(final AbstractActor dagVertex, final URL script) throws EvalError {
    final Interpreter interpreter = new Interpreter();

    // TODO : isolate Interpreter initializatino
    final BshClassManager classManager = interpreter.getClassManager();
    classManager.cacheClassInfo("Buffer", Buffer.class);

    // Retrieve the corresponding sdf vertex
    // val sdfVertex = dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex)

    // Create the vertex parameter list
    final Map<String, Long> parameters = new LinkedHashMap<>();

    for (final ConfigInputPort p : dagVertex.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof final Parameter param) {
        // TODO: check if parameter expression needs to be check as long or double
        parameters.put(p.getName(), param.getExpression().evaluateAsLong());
      }
    }
    parameters.put("alignment", this.alignment);

    // Create the input/output lists
    // @farresti: I use toSet instead of toList as it retrieves the unique reference of the edge
    final List<Fifo> incomingEdges = dagVertex.getDataInputPorts().stream().map(DataPort::getFifo).toList();

    final List<PiBuffer> inputs = incomingEdges.stream().map(it -> {
      final String dataType = it.getType();
      // An input buffer is backward mergeable if it is read_only OR if it is unused
      final PortMemoryAnnotation annotation = it.getTargetPort().getAnnotation();
      final boolean isMergeable = PortMemoryAnnotation.READ_ONLY.equals(annotation)
          || PortMemoryAnnotation.UNUSED.equals(annotation);

      final long dataSize = this.simulationInfo.getDataTypeSizeInBit(dataType);

      // Weight is already dataSize * (Cons || prod)
      final long nbTokens = it.getTargetPort().getPortRateExpression().evaluateAsLong(); // / dataSize
      try {
        return new PiBuffer(it, dagVertex.getName(), it.getTargetPort().getName(), nbTokens, dataSize, isMergeable);
      } catch (final NullPointerException exc) {
        throw new PreesmRuntimeException("SDFEdge " + it.getSourcePort().getId() + "->" + it.getTargetPort().getId()
            + " has unknows type " + dataType + ". Add the corresponding data type to the scenario.", exc);
      }
    }).collect(Collectors.toList());

    // outgoing edges
    final List<Fifo> outgoingEdges = dagVertex.getDataOutputPorts().stream().map(DataPort::getFifo).toList();

    final List<PiBuffer> outputs = outgoingEdges.stream().map(it -> {
      final String dataType = it.getType();
      // An output buffer is forward mergeable if its target port is unused OR if its target port is read_only
      final PortMemoryAnnotation annotation = it.getTargetPort().getAnnotation();
      final boolean isMergeable = PortMemoryAnnotation.READ_ONLY.equals(annotation)
          || PortMemoryAnnotation.UNUSED.equals(annotation);

      final long dataSize = this.simulationInfo.getDataTypeSizeInBit(dataType);
      // Weight is already dataSize * (Cons || prod)
      final long nbTokens = it.getTargetPort().getPortRateExpression().evaluateAsLong(); // / dataSize
      try {
        return new PiBuffer(it, dagVertex.getName(), it.getSourcePort().getName(), nbTokens, dataSize, isMergeable);
      } catch (final NullPointerException exc) {
        throw new PreesmRuntimeException("SDFEdge " + it.getSourcePort().getId() + "->" + it.getTargetPort().getId()
            + " has unknows type " + dataType + ". Add the corresponding data type to the scenario.", exc);
      }
    }).collect(Collectors.toList());

    // Import the necessary libraries
    interpreter.eval("import " + Buffer.class.getName() + ";");
    interpreter.eval("import " + List.class.getName() + ";");

    // Feed the parameters/inputs/outputs to the interpreter
    for (final Entry<String, Long> e : parameters.entrySet()) {
      interpreter.set(e.getKey(), e.getValue());
    }
    for (final PiBuffer i : inputs) {
      interpreter.set("i_" + i.name, i);
    }
    for (final PiBuffer o : outputs) {
      interpreter.set("o_" + o.name, o);
    }
    if (interpreter.get("parameters") == null) {
      interpreter.set("parameters", parameters);
    }
    if (interpreter.get("inputs") == null) {
      interpreter.set("inputs", inputs);
    }
    if (interpreter.get("outputs") == null) {
      interpreter.set("outputs", outputs);
    }

    try {

      // Run the script
      final String readURL = URLHelper.read(script);
      interpreter.eval(readURL);

      // Store the result if the execution was successful
      this.scriptResults.put(dagVertex, new Pair<>(inputs, outputs));
    } catch (final ParseException error) {

      // Logger is used to display messages in the console
      final String message = error.getMessage() + "\n" + error.getCause();
      PiScriptRunner.logger.log(Level.WARNING, "Parse error in " + dagVertex.getName() + " memory script:\n" + message,
          error);
    } catch (final EvalError error) {

      // Logger is used to display messages in the console
      final String message = error.getMessage() + "\n" + error.getCause();
      PiScriptRunner.logger.log(Level.WARNING, "Evaluation error in " + dagVertex.getName() + " memory script:\n[Line "
          + error.getErrorLineNumber() + "] " + message, error);
    } catch (final IOException exception) {
      PiScriptRunner.logger.log(Level.WARNING, exception.getMessage(), exception);
    }
  }

  /**
  *
  */
  public void updateMEG(final PiMemoryExclusionGraph meg) {

    // Create a new property in the MEG to store the merged memory objects
    final Map<PiMemoryExclusionVertex, Set<PiMemoryExclusionVertex>> mergedMObjects = new LinkedHashMap<>();
    meg.getPropertyBean().setValue(PiMemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY, mergedMObjects);

    // For each buffer, get the corresponding MObject
    final Map<PiBuffer, PiMemoryExclusionVertex> bufferAndMObjectMap = new LinkedHashMap<>();
    for (final List<PiBuffer> buffers : getBufferGroups()) {
      for (final PiBuffer buffer : buffers) {

        // Get the Mobj
        final PiMemoryExclusionVertex mObjCopy = new PiMemoryExclusionVertex(buffer.getLoggingEdgeName(),
            meg.getScenario());

        final PiMemoryExclusionVertex mObj = meg.getVertex(mObjCopy);
        if (mObj == null) {
          throw new PreesmRuntimeException(
              "Cannot find " + mObjCopy + " in the given MEG. Contact developers for more information.");
        }

        if (mObj.getWeight() != (buffer.getBufferSizeInBit())) {

          // Karol's Note:
          // To process the aggregated dag edges, we will need to
          // split them in the MEG. Doing so, we still need to make
          // sure that all related information remains correct:
          // - Exclusions
          // - Scheduling order
          // - Predecessors
          // - The two Mobj must have different source and sink names.
          // or otherwise they will be considered equals() even with
          // different sizes.
          //
          // Also we will need to make sure that the code generation
          // printerS are still functional
          throw new PreesmRuntimeException(
              "Aggregated DAG Edge " + mObj + " not yet supported. Contact Preesm developers for more information.");
        }
        bufferAndMObjectMap.put(buffer, mObj);
      }
    }

    // Backup neighbors of each buffer before changing anything in the meg
    for (final List<PiBuffer> buffers : getBufferGroups()) {
      for (final PiBuffer buffer : buffers) {
        final PiMemoryExclusionVertex mObj = bufferAndMObjectMap.get(buffer);
        final List<PiMemoryExclusionVertex> neighbors = new ArrayList<>(meg.getAdjacentVertexOf(mObj));
        mObj.setPropertyValue(PiMemoryExclusionVertex.ADJACENT_VERTICES_BACKUP, neighbors);
      }
    }

    // Process each group of buffers separately
    for (final List<PiBuffer> buffers : getBufferGroups()) {

      // For each unmatched buffer that received matched buffers
      for (final PiBuffer buffer : buffers.stream().filter(it -> (it.matched == null) && it.host).toList()) {

        // Enlarge the corresponding mObject to the required size
        final PiMemoryExclusionVertex mObj = bufferAndMObjectMap.get(buffer);
        final long minIndex;
        if (!this.falseSharingPreventionFlag && ((buffer.minIndex == 0) || (this.alignment <= 8))) {
          minIndex = buffer.minIndex;
        } else {

          // Make sure that index aligned in the buffer are in
          // fact aligned
          // NB: at this point, the minIndex of the buffer is
          // either 0 or a negative number (if buffer were
          // matched before the range of real tokens of the
          // host). This division is here to make sure that
          // index 0 of the host buffer is still aligned !
          minIndex = ((buffer.minIndex / this.alignment) - 1) * this.alignment;
        }
        mObj.setWeight(buffer.maxIndex - minIndex);

        // Add the mobj to the meg host list
        mergedMObjects.put(mObj, new LinkedHashSet<>());

        // Save the real token range in the Mobj properties
        final PiRange realTokenRange = new PiRange(0, buffer.getBufferSizeInBit());
        final PiRange actualRealTokenRange = new PiRange(-minIndex, (buffer.getBufferSizeInBit()) - minIndex);
        final List<Pair<PiMemoryExclusionVertex, Pair<PiRange, PiRange>>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(mObj, new Pair<>(realTokenRange, actualRealTokenRange)));
        mObj.setPropertyValue(PiMemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, ranges);
      }

      // For each matched buffers
      for (final PiBuffer buffer : buffers.stream().filter(it -> it.matched != null).toList()) {

        // find the root buffer(s)
        // there might be several roots if the buffer was divided
        // the map associates:
        // a localRange of the buffer to
        // a pair of a root buffer and its range for the buffer
        final Map<PiRange, Pair<PiBuffer, PiRange>> rootBuffers = new LinkedHashMap<>();
        for (final PiMatch match : buffer.matched) {
          rootBuffers.putAll(match.getRoot());
        }

        final PiMemoryExclusionVertex mObj = bufferAndMObjectMap.get(buffer);

        // For buffer receiving a part of the current buffer
        for (final PiBuffer rootBuffer : rootBuffers.values().stream().map(Pair::getKey).toList()) {
          final PiMemoryExclusionVertex rootMObj = bufferAndMObjectMap.get(rootBuffer);

          // Update the meg hostList property
          mergedMObjects.get(rootMObj).add(mObj);

          // Add exclusions between the rootMobj and all adjacent
          // memory objects of MObj
          final Set<PiMemoryExclusionVertex> adjacentVertexOfMObj = meg.getAdjacentVertexOf(mObj);
          final Set<PiMemoryExclusionVertex> adjacentVertexOfRootMObj = meg.getAdjacentVertexOf(rootMObj);

          adjacentVertexOfMObj.stream()
              .filter(exMObj -> !(rootMObj.equals(exMObj)) && !adjacentVertexOfRootMObj.contains(exMObj))
              .forEach(exMObj -> meg.addEdge(rootMObj, exMObj));

        }
        meg.removeVertex(mObj);

        // Fill the mobj properties (i.e. save the matched buffer info)
        final List<Pair<PiMemoryExclusionVertex, Pair<PiRange, PiRange>>> mObjRoots = new ArrayList<>();
        mObj.setPropertyValue(PiMemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, mObjRoots);
        final PiRange realTokenRange = new PiRange(0, buffer.getBufferSizeInBit());

        // For each subrange of real tokens, save the corresponding remote buffer
        // and range.
        rootBuffers.entrySet().stream().forEach(entry -> {
          final PiMemoryExclusionVertex rootMObj = bufferAndMObjectMap.get(entry.getValue().getKey());
          final PiRange localRange = entry.getKey().intersection(realTokenRange);
          final PiRange translatedLocalRange = localRange.copy();
          translatedLocalRange.translate(entry.getValue().getValue().getStart() - entry.getKey().getStart());
          final PiRange remoteRange = entry.getValue().getValue().intersection(translatedLocalRange);
          if (!remoteRange.equals(translatedLocalRange)) {
            // Should never be the case
            throw new PreesmRuntimeException("Unexpected error !");
          }
          mObjRoots.add(new Pair<>(rootMObj, new Pair<>(localRange, remoteRange)));
        });

        // If the mObj is a divided buffer
        if (rootBuffers.size() > 1) {
          // Identify and all source and destination buffers in which
          // parts of the divided buffer are merged and store this
          // information in the mObject properties.
          // => This information will be used when allocating a
          // mObject in distributed memory to make sure that the
          // divided buffer remains accessible everywhere it is
          // needed, and otherwise forbid its division.
          final List<PiBuffer> sourceAndDestBuffers = new ArrayList<>(
              rootBuffers.values().stream().map(Pair::getKey).toList());

          // buffers mapped in the divided buffer
          final Stream<PiBuffer> stream1 = buffers.stream().filter(piBuff -> (piBuff.appliedMatches.values().stream()
              .map(Pair::getKey).anyMatch(buf -> buf.equals(buffer))));
          sourceAndDestBuffers.addAll(stream1.toList());

          // Find corresponding mObjects
          final List<PiMemoryExclusionVertex> srcAndDestMObj = sourceAndDestBuffers.stream()
              .map(bufferAndMObjectMap::get).toList();

          // Save this list in the attributes of the divided buffer
          mObj.setPropertyValue(PiMemoryExclusionVertex.DIVIDED_PARTS_HOSTS, srcAndDestMObj);
        }

        // Sort mObjRoots in order of contiguous ranges
        Collections.sort(mObjRoots,
            (m1, m2) -> Long.compare(m1.getValue().getKey().getStart(), m2.getValue().getKey().getStart()));
      }
    }
    //
    // List of the unused and pureout memobjects
    final List<PiMemoryExclusionVertex> unusedMObjects = new ArrayList<>(meg.vertexSet().stream().filter(mObj -> {
      if (mObj.getEdge() != null) {

        // Find unused write_only edges
        final Fifo aggregate = mObj.getEdge();
        final boolean b1 = PortMemoryAnnotation.WRITE_ONLY.equals(aggregate.getSourcePort().getAnnotation());
        final boolean b2 = PortMemoryAnnotation.UNUSED.equals(aggregate.getTargetPort().getAnnotation());
        return b1 && b2;
      }
      return false;
    }).filter(mObj -> {
      final List<PiBuffer> flatten = new ArrayList<>();
      getBufferGroups().stream().forEach(flatten::addAll);

      // keep only those that are not host. (matched ones have already been removed from the MEG)
      final PiBuffer correspondingBuffer = flatten.stream()
          .filter(buf -> (mObj.getEdge().equals(buf.getLoggingEdgeName()))).findFirst().orElse(null);
      if (correspondingBuffer != null) {
        return !correspondingBuffer.host;
      }
      return true;
    }).toList());

    // Remove all exclusions between unused buffers
    unusedMObjects.stream().forEach(mObj -> {
      final List<PiMemoryExclusionVertex> unusedNeighbors = meg.getAdjacentVertexOf(mObj).stream()
          .filter(unusedMObjects::contains).toList();
      unusedNeighbors.stream().forEach(it -> meg.removeEdge(mObj, it));
    });
  }

  /**
   * This method calls {@link Buffer#simplifyMatches()} for each {@link Buffer} of the {@link #scriptResults}. If a
   * {@link Buffer} has an empty {@link Buffer#getMatchTable() matchTable} after the simplification process, it is
   * removed from the {@link #scriptResults}.
   */
  private static void simplifyResult(final List<PiBuffer> inputs, final List<PiBuffer> outputs) {
    final List<PiBuffer> allBuffers = new ArrayList<>(inputs);
    allBuffers.addAll(outputs);

    // Matches whose reciprocate has been processed
    // no need to test them again
    final List<PiMatch> processedMatch = new ArrayList<>();

    // Iterate over all buffers
    allBuffers.stream().forEach(it -> it.simplifyMatches(processedMatch));

    // If a buffer has an empty matchTable, remove it from its list
    final List<PiBuffer> unmatchedBuffer = allBuffers.stream().filter(it -> it.matchTable.isEmpty()).toList();
    inputs.removeAll(unmatchedBuffer);
    outputs.removeAll(unmatchedBuffer);
  }

  public void setSimulationInfo(final SimulationInfo simulationInfo) {
    this.simulationInfo = simulationInfo;
  }

  public CheckPolicy getCheckPolicy() {
    return this.checkPolicy;
  }

  public void setCheckPolicy(final CheckPolicy checkPolicy) {
    this.checkPolicy = checkPolicy;
  }

  public CharSequence getLog() {
    return this.log;
  }

  public void setLog(final CharSequence log) {
    this.log = log;
  }

  public boolean isGenerateLog() {
    return this.generateLog;
  }

  public void setGenerateLog(final boolean generateLog) {
    this.generateLog = generateLog;
  }

  public List<List<PiBuffer>> getBufferGroups() {
    return this.bufferGroups;
  }
}
