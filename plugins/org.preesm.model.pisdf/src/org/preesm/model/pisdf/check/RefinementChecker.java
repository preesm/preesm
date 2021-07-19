/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
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
package org.preesm.model.pisdf.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;

/**
 * Class to check different properties of the Refinements of the Actors of a PiGraph. Entry point is the
 * checkRefinements method. Actors with invalid refinements are kept in several sets
 *
 * @author cguy
 *
 */
public class RefinementChecker extends AbstractPiSDFObjectChecker {

  /** All accepted header file extensions. */
  public static final String[] acceptedHeaderExtensions = { "h", "hpp", "hxx", "h++", "hh", "H" };

  /**
   * Check if the given C/C++ header file extension is supported.
   * 
   * @param extension
   *          The extension to check (as "hxx" for "MyHeader.hxx").
   * @return Whether or not the file extension is supported.
   */
  public static boolean isAsupportedHeaderFileExtension(final String extension) {
    return Arrays.asList(acceptedHeaderExtensions).stream().anyMatch(x -> x.equals(extension));
  }

  /**
   * Instantiates a new refinement checker.
   */
  public RefinementChecker() {
    super();
  }

  /**
   * Instantiates a new refinement checker.
   * 
   * @param throwExceptionLevel
   *          The maximum level of error throwing exceptions.
   * @param loggerLevel
   *          The maximum level of error generating logs.
   */
  public RefinementChecker(final CheckerErrorLevel throwExceptionLevel, final CheckerErrorLevel loggerLevel) {
    super(throwExceptionLevel, loggerLevel);
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    boolean ok = true;
    for (final AbstractActor aa : graph.getActors()) {
      if (aa instanceof Actor) {
        ok &= doSwitch(aa);
      } else if (aa instanceof PiGraph) {
        // PiGraph as direct children means that a reconnection already occurred
        // but we don't want a recursive pattern and it needs a different check
        // Indeed, the SubgraphReconnector already checked the ports so we only need to check the fifo types.
        ok &= checkReconnectedSubGraphFifoTypes((PiGraph) aa);
      }
    }
    return ok;
  }

  @Override
  public Boolean caseActor(final Actor a) {
    final Refinement refinement = a.getRefinement();
    boolean validity = false;
    if ((refinement != null) && (refinement.getFilePath() != null) && !refinement.getFilePath().isEmpty()) {
      validity = checkRefinementExtension(a) && checkRefinementValidity(a);
      if (validity) {
        final List<Pair<Port, Port>> correspondingPorts = getPiGraphRefinementCorrespondingPorts(a);
        final List<Pair<Port, FunctionArgument>> correspondingArguments = getCHeaderRefinementCorrespondingArguments(a);

        validity &= checkRefinementPorts(a, correspondingPorts, correspondingArguments);
        validity &= checkRefinementFifoTypes(a, correspondingPorts, correspondingArguments);
        // continue recursive visit if hierarchical?
      }
    } else {
      reportError(CheckerErrorLevel.WARNING, a, "Actor [%s] has no refinement set.", a.getVertexPath());
    }

    return validity;
  }

  /**
   * Check the file extension of the Refinement of an Actor
   *
   * <p>
   * Precondition: a has a non-null refinement with a non-null and non-empty filePath.
   * </p>
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @return true if the file extension of the refinement of a is a valid one, false otherwise
   */
  private boolean checkRefinementExtension(final Actor a) {
    final IPath path = new Path(a.getRefinement().getFilePath());
    final String fileExtension = path.getFileExtension();
    if (!fileExtension.equals("idl") && !fileExtension.equals("pi")
        && !isAsupportedHeaderFileExtension(fileExtension)) {
      // File pointed by the refinement of a does not have a valid extension
      reportError(CheckerErrorLevel.RECOVERABLE, a, "Actor [%s] has an unrecognized refinement file extension.",
          a.getVertexPath());
      return false;
    }
    return true;
  }

  /**
   * Check the existence of the file of the Refinement of an Actor
   *
   * <p>
   * Precondition: a has a non-null refinement with a non-null and non-empty filePath.
   * </p>
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @return true if the file exists, false otherwise
   */
  private boolean checkRefinementValidity(final Actor a) {
    final IPath path = new Path(a.getRefinement().getFilePath());
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    if (!file.exists()) {
      // File pointed by the refinement does not exist
      reportError(CheckerErrorLevel.RECOVERABLE, a,
          "Actor [%s] has a refinement file missing in the file system: '%s'.", a.getVertexPath(),
          a.getRefinement().getFilePath());
      return false;
    }
    return true;
  }

  /**
   * Check if all the ports of the actor are present in the refinement, and vice versa.
   * 
   * @param a
   *          the Actor for which we want to check the Refinement
   * @param correspondingPorts
   *          The corresponding ports if PiGraph refinement (null otherwise).
   * @param correspondingArguments
   *          The corresponding arguments if PiCHeader refinement (null otherwise).
   * @return true if all ports are corresponding, false otherwise
   */
  private boolean checkRefinementPorts(final Actor a, final List<Pair<Port, Port>> correspondingPorts,
      final List<Pair<Port, FunctionArgument>> correspondingArguments) {
    boolean validity = true;

    if (a.isHierarchical()) {
      for (final Pair<Port, Port> correspondingPort : correspondingPorts) {
        final Port topPort = correspondingPort.getKey();
        final Port subPort = correspondingPort.getValue();

        if (topPort != null && subPort == null) {
          validity = false;
          reportError(CheckerErrorLevel.RECOVERABLE, a, "Port [%s:%s] is not present in refinement.", a.getName(),
              topPort.getName());
        } else if (topPort == null && subPort != null) {
          validity = false;
          reportError(CheckerErrorLevel.RECOVERABLE, a, "Port [%s:%s] is only present in refinement.", a.getName(),
              subPort.getName());
        }

      }
    } else if (a.getRefinement() instanceof CHeaderRefinement) {
      for (final Pair<Port, FunctionArgument> correspondingArgument : correspondingArguments) {
        final Port topPort = correspondingArgument.getKey();
        final FunctionArgument fa = correspondingArgument.getValue();

        if (topPort != null && fa == null) {
          validity = false;
          reportError(CheckerErrorLevel.RECOVERABLE, a, "Port [%s:%s] is not present in refinement.", a.getName(),
              topPort.getName());
        } else if (topPort == null && fa != null) {
          validity = false;
          reportError(CheckerErrorLevel.RECOVERABLE, a, "Port [%s:%s] is only present in refinement.", a.getName(),
              fa.getName());
        }
      }
    }

    return validity;
  }

  /**
   * Check if all the fifo connected to the actor and its refinement have the same type.
   * 
   * @param a
   *          the Actor for which we want to check the Refinement
   * @param correspondingPorts
   *          The corresponding ports if PiGraph refinement (null otherwise).
   * @param correspondingArguments
   *          The corresponding arguments if PiCHeader refinement (null otherwise).
   * @return true if all (present) fifo types are corresponding, false otherwise
   */
  private boolean checkRefinementFifoTypes(final Actor a, final List<Pair<Port, Port>> correspondingPorts,
      final List<Pair<Port, FunctionArgument>> correspondingArguments) {
    boolean validity = true;

    if (a.isHierarchical()) {
      for (final Pair<Port, Port> correspondingPort : correspondingPorts) {
        final Port topPort = correspondingPort.getKey();
        final Port subPort = correspondingPort.getValue();

        if (!(topPort instanceof DataPort) || !(subPort instanceof DataPort)) {
          continue;
        }

        final Fifo topFifo = ((DataPort) topPort).getFifo();
        final Fifo subFifo = ((DataPort) subPort).getFifo();

        if (topFifo != null && subFifo != null && !topFifo.getType().equals(subFifo.getType())) {
          validity = false;
          reportError(CheckerErrorLevel.WARNING, a,
              "Port [%s:%s] has a different fifo type than in its inner self [%s]: '%s' vs '%s'.", a.getName(),
              topPort.getName(), a.getSubGraph().getName(), topFifo.getType(), subFifo.getType());
        }
      }
    } else if (a.getRefinement() instanceof CHeaderRefinement) {
      for (final Pair<Port, FunctionArgument> correspondingArgument : correspondingArguments) {
        final Port topPort = correspondingArgument.getKey();
        final FunctionArgument fa = correspondingArgument.getValue();

        if (!(topPort instanceof DataPort) || fa == null) {
          continue;
        }

        final Fifo topFifo = ((DataPort) topPort).getFifo();

        if (topFifo != null && !fa.getType().equals(topFifo.getType())) {
          validity = false;
          reportError(CheckerErrorLevel.WARNING, a,
              "Port [%s:%s] has a different fifo type than in its C/C++ refinement: '%s' vs '%s'.", a.getName(),
              topPort.getName(), topFifo.getType(), fa.getType());
          // check here the container type? hls::stream for FPGA
        }
      }
    }

    return validity;
  }

  private boolean checkReconnectedSubGraphFifoTypes(final PiGraph graph) {
    boolean validity = true;
    for (final AbstractActor aa : graph.getActors()) {
      if (aa instanceof DataInputInterface | aa instanceof DataOutputInterface) {
        final InterfaceActor ia = (InterfaceActor) aa;
        final DataPort iaPort = ia.getDataPort();
        final DataPort graphPort = ia.getGraphPort();
        if (iaPort == null || graphPort == null) {
          // already reported elsewhere
          continue;
        }
        final Fifo iaFifo = iaPort.getFifo(); // fifo sub graph
        final Fifo graphFifo = graphPort.getFifo(); // fifo top graph
        if (iaFifo != null && graphFifo != null && !iaFifo.getType().equals(graphFifo.getType())) {
          validity = false;
          reportError(CheckerErrorLevel.WARNING, graph,
              "Port [%s:%s] has a different fifo type than in its inner Interface: '%s' vs '%s'.", graph.getName(),
              graphPort.getName(), graphFifo.getType(), iaFifo.getType());
        }

      }
    }

    return validity;
  }

  private static List<Pair<Port, FunctionArgument>> getCHeaderRefinementCorrespondingArguments(final Actor a) {
    if (!(a.getRefinement() instanceof CHeaderRefinement)) {
      return null;
    }
    final CHeaderRefinement ref = (CHeaderRefinement) a.getRefinement();
    final FunctionPrototype fp = ref.getLoopPrototype();
    if (fp == null) {
      // there might be no given loop prototype
      return null;
    }

    final List<FunctionArgument> noRefCorrespondingFoundYet = new ArrayList<>(fp.getArguments());
    final List<Pair<Port, FunctionArgument>> result = new ArrayList<>();

    for (final Port p1 : a.getAllPorts()) {
      FunctionArgument correspondingFA = null;
      for (final FunctionArgument fa : fp.getArguments()) {
        if (p1.getName().equals(fa.getName())) {
          correspondingFA = fa;
          break;
        }
      }
      noRefCorrespondingFoundYet.remove(correspondingFA);
      result.add(new Pair<>(p1, correspondingFA));
    }

    // Function arguments in refinement without top corresponding port
    for (final FunctionArgument fa : noRefCorrespondingFoundYet) {
      result.add(new Pair<>(null, fa));
    }

    return result;
  }

  private static List<Pair<Port, Port>> getPiGraphRefinementCorrespondingPorts(final Actor a) {
    if (!a.isHierarchical()) {
      return null;
    }

    final PiGraph subGraph = a.getSubGraph();
    // if it is reconnected, there is no point to do this, this case should not happen however
    if (subGraph.getContainingPiGraph() != null) {
      return new ArrayList<>();
    }

    // Then we try to perform a sort of reconnection ...
    // but with the subgraph interface actors instead of the subgraph ports (except for Config Input ports).
    // TODO

    return new ArrayList<>();
  }

}
