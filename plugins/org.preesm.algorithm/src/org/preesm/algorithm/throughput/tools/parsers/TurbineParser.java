/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.throughput.tools.parsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.throughput.tools.helpers.GraphStructureHelper;
import org.preesm.algorithm.throughput.tools.helpers.Stopwatch;
import org.preesm.commons.exceptions.PreesmException;

/**
 * @author hderoui
 *
 */
public interface TurbineParser {

  public static final double INTERFACE_DURATION_DEFAULT = 0;

  /**
   * @param path
   *          IBSDF file
   * @return IBSDF graph
   */
  public static SDFGraph importIBSDFGraph(final String path) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // initialize the actors id table
    final Map<String, String> actorsId = new LinkedHashMap<>();

    // Open the file
    FileInputStream fstream;
    try {
      fstream = new FileInputStream(path);
      final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
      // .tur file structure
      // line 1 to 2: graph's name (line 2)
      // line 3 to 4: number of actors and edges (line 4)
      // line 5 and 6 to N: description of the N actors, one actor per
      // line
      // line 6+N and 7+N to 7+N+M: description of the M edges

      // print the name of the graph
      TurbineParser.jumpToLine(br, 1);
      final String graphName = br.readLine().split(" ")[0];

      // create new graph
      final SDFGraph g = new SDFGraph();
      g.setName(graphName);
      int nbActors = 0;
      int nbEdges = 0;

      // reading the number of actor and edges
      String[] line = TurbineParser.jumpToLine(br, 2).split(" ");
      nbActors = Integer.parseInt(line[0]);
      nbEdges = Integer.parseInt(line[1]);

      // list of actors
      final Map<String, SDFAbstractVertex> listActors = new LinkedHashMap<>();

      // reading actors description
      TurbineParser.jumpToLine(br, 2);
      for (int i = 0; i < nbActors; i++) {
        line = br.readLine().split(" ");
        // line structure: Id repetitionFactor phaseDuration
        final String _actorId = line[0];
        final Long _actorRF = Long.parseLong(line[1]);
        final double _actorDuration = Double.parseDouble(line[2]);

        final String newActorId = _actorId;
        actorsId.put(_actorId, newActorId);

        // add the actor to the graph
        final SDFAbstractVertex actor = GraphStructureHelper.addActor(g, newActorId, null, _actorRF, _actorDuration, 0,
            null);
        listActors.put(newActorId, actor);
      }

      // reading edges description
      TurbineParser.jumpToLine(br, 2);
      for (int i = 0; i < nbEdges; i++) {
        line = br.readLine().split(" ");
        // line structure: (source,target) initial_marking
        // production_vector consumption_vector
        final String[] sourceTarget = line[0].split(",");
        final String srcActorId = actorsId.get(sourceTarget[0].replace("(", ""));
        final String trgActorId = actorsId.get(sourceTarget[1].replace(")", ""));
        final Double _initialMarking = Double.parseDouble(line[1]);
        final Double _prod = Double.parseDouble(line[2]);
        final Double _cons = Double.parseDouble(line[3]);

        // add the edge to the graph
        GraphStructureHelper.addEdge(g, srcActorId, null, trgActorId, null, _prod.intValue(), _cons.intValue(),
            _initialMarking.intValue(), null);
      }

      // subgraphs
      TurbineParser.jumpToLine(br, 2);
      line = br.readLine().split(" ");
      final int nbSubGraphs = Integer.parseInt(line[0]);

      for (int i = 0; i < nbSubGraphs; i++) {
        // parent actor (hierarchical actor)
        SDFAbstractVertex hierarchicalActor;
        TurbineParser.jumpToLine(br, 2);
        line = br.readLine().split(" ");
        final String _parentActorId = line[0];
        hierarchicalActor = listActors.get(actorsId.get(_parentActorId));

        // construct the subgraph
        // print the name of the graph
        TurbineParser.jumpToLine(br, 1);
        final String subGraphName = br.readLine().split(" ")[0];

        // create new graph
        final SDFGraph subGraph = new SDFGraph();
        subGraph.setName(subGraphName);

        // reading the number of actor and edges
        line = TurbineParser.jumpToLine(br, 2).split(" ");
        nbActors = Integer.parseInt(line[0]);
        nbEdges = Integer.parseInt(line[1]);
        int nbInI = 0;
        nbInI = Integer.parseInt(line[2]);
        int nbOutI = 0;
        nbOutI = Integer.parseInt(line[3]);

        // reading actors description
        TurbineParser.jumpToLine(br, 2);
        for (int j = 0; j < nbActors; j++) {
          line = br.readLine().split(" ");
          // line structure: Id repetitionFactor phaseDuration
          final String _actorId = line[0];
          final Long _actorRF = Long.parseLong(line[1]);
          final Double _actorDuration = Double.parseDouble(line[2]);

          final String newActorId = _actorId;
          actorsId.put(_actorId, newActorId);

          // add the actor to the graph
          final SDFAbstractVertex actor = GraphStructureHelper.addActor(subGraph, newActorId, null, _actorRF,
              _actorDuration, 0, null);
          listActors.put(newActorId, actor);
        }

        // reading edges description
        TurbineParser.jumpToLine(br, 2);
        for (int j = 0; j < nbEdges; j++) {
          line = br.readLine().split(" ");
          // line structure: (source,target) initial_marking
          // production_vector consumption_vector
          final String[] sourceTarget = line[0].split(",");
          final String srcActorId = actorsId.get(sourceTarget[0].replace("(", ""));
          final String trgActorId = actorsId.get(sourceTarget[1].replace(")", ""));
          final Double _initialMarking = Double.parseDouble(line[1]);
          final Double _prod = Double.parseDouble(line[2]);
          final Double _cons = Double.parseDouble(line[3]);

          // add the edge to the graph
          GraphStructureHelper.addEdge(subGraph, srcActorId, null, trgActorId, null, _prod.intValue(), _cons.intValue(),
              _initialMarking.intValue(), null);
        }

        // reading Input interfaces description
        TurbineParser.jumpToLine(br, 2);
        for (int j = 0; j < nbInI; j++) {
          line = br.readLine().split(" ");
          // line structure: (source,target) consumption_vector
          final String[] sourceTarget = line[0].split(",");
          final String srcActorId = actorsId.get(sourceTarget[0].replace("(", ""));
          double cons = 0.;

          String inputPort = null;
          // get the name of the input interface
          for (final SDFInterfaceVertex input : hierarchicalActor.getSources()) {
            if (hierarchicalActor.getAssociatedEdge(input).getSource().getName().equals(srcActorId)) {
              inputPort = input.getName();
              cons = (double) hierarchicalActor.getAssociatedEdge(input).getCons().longValue();
              break;
            }
          }

          final String trgActorId = actorsId.get(sourceTarget[1].replace(")", ""));
          final Double _prod = Double.parseDouble(line[1]);

          GraphStructureHelper.addInputInterface(subGraph, inputPort, 0L, TurbineParser.INTERFACE_DURATION_DEFAULT, 0,
              null);
          GraphStructureHelper.addEdge(subGraph, inputPort, null, trgActorId, null, _prod.intValue(), (long) cons, 0,
              null);
        }

        // reading Output interfaces description
        TurbineParser.jumpToLine(br, 2);
        for (int j = 0; j < nbOutI; j++) {
          line = br.readLine().split(" ");
          // line structure: (source,target) production_vector
          final String[] sourceTarget = line[0].split(",");
          final String trgActorId = actorsId.get(sourceTarget[1].replace(")", ""));
          double prod = 0.;

          String outputPort = null;
          // get the name of the output interface
          for (final SDFInterfaceVertex output : hierarchicalActor.getSinks()) {
            if (hierarchicalActor.getAssociatedEdge(output).getTarget().getName().equals(trgActorId)) {
              outputPort = output.getName();
              prod = (double) hierarchicalActor.getAssociatedEdge(output).getProd().longValue();
              break;
            }
          }

          final String srcActorId = actorsId.get(sourceTarget[0].replace("(", ""));
          final Double _cons = Double.parseDouble(line[1]);

          GraphStructureHelper.addOutputInterface(subGraph, outputPort, 0, TurbineParser.INTERFACE_DURATION_DEFAULT, 0,
              null);
          GraphStructureHelper.addEdge(subGraph, srcActorId, null, outputPort, null, (long) prod, _cons.intValue(), 0,
              null);
        }

        // add the subgraph description to the hierarchical actor
        hierarchicalActor.setGraphDescription(subGraph);
      }

      // Close the input stream
      br.close();

      timer.stop();

      // return the imported graph
      return g;

    } catch (final IOException e) {
      throw new PreesmException("Could not import IBSDF", e);
    }
  }

  /**
   * used to skip n lines
   *
   * @param br
   *          BufferedReader
   * @param n
   *          number of lines to skip
   * @return Line
   */
  static String jumpToLine(final BufferedReader br, final int n) {
    try {
      for (int i = 1; i < n; i++) {
        br.readLine();
      }
      return br.readLine();

    } catch (final IOException e) {
      throw new PreesmException("Could not read line " + n, e);
    }
  }
}
