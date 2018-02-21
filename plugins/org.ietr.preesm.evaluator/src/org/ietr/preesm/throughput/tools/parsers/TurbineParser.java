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
package org.ietr.preesm.throughput.tools.parsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;

/**
 * @author hderoui
 *
 */
public abstract class TurbineParser {

  public static final double InterfaceDuration_default = 0;

  /**
   * @param path
   *          IBSDF file
   * @return IBSDF graph
   */
  public static SDFGraph importIBSDFGraph(String path, PreesmScenario scenario) {
    // TODO: add actors duration to the scenario
    Stopwatch timer = new Stopwatch();
    timer.start();

    // initialize the actors id table
    Hashtable<String, String> actorsId = new Hashtable<>();

    // Open the file
    FileInputStream fstream;
    try {
      fstream = new FileInputStream(path);
      BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
      // .tur file structure
      // line 1 to 2: graph's name (line 2)
      // line 3 to 4: number of actors and edges (line 4)
      // line 5 and 6 to N: description of the N actors, one actor per
      // line
      // line 6+N and 7+N to 7+N+M: description of the M edges

      // print the name of the graph
      jumpToLine(br, 1);
      String graphName = br.readLine().split(" ")[0];
      // System.out.println("Graph's name: " + graphName);

      // create new graph
      SDFGraph g = new SDFGraph();
      g.setName(graphName);
      int nbActors = 0;
      int nbEdges = 0;

      // reading the number of actor and edges
      String[] line = jumpToLine(br, 2).split(" ");
      nbActors = Integer.parseInt(line[0]);
      nbEdges = Integer.parseInt(line[1]);

      // list of actors
      Hashtable<String, SDFAbstractVertex> listActors = new Hashtable<>();

      // reading actors description
      jumpToLine(br, 2);
      for (int i = 0; i < nbActors; i++) {
        line = br.readLine().split(" ");
        // line structure: Id repetitionFactor phaseDuration
        String _actorId = line[0];
        Integer _actorRF = Integer.parseInt(line[1]);
        double _actorDuration = Double.parseDouble(line[2]);

        // String newActorId = Identifier.generateActorId();
        String newActorId = _actorId;
        actorsId.put(_actorId, newActorId);

        // add the actor to the graph
        SDFAbstractVertex actor = GraphStructureHelper.addActor(g, newActorId, null, _actorRF, _actorDuration, null, null);
        // System.out.println("actor : " + newActorId + " " + _actorRF + " " + _actorDuration);
        listActors.put(newActorId, actor);
      }

      // reading edges description
      jumpToLine(br, 2);
      for (int i = 0; i < nbEdges; i++) {
        line = br.readLine().split(" ");
        // line structure: (source,target) initial_marking
        // production_vector consumption_vector
        String[] source_target = line[0].split(",");
        String srcActorId = actorsId.get(source_target[0].replace("(", ""));
        String trgActorId = actorsId.get(source_target[1].replace(")", ""));
        Double _initialMarking = Double.parseDouble(line[1]);
        Double _prod = Double.parseDouble(line[2]);
        Double _cons = Double.parseDouble(line[3]);

        // add the edge to the graph
        GraphStructureHelper.addEdge(g, srcActorId, null, trgActorId, null, _prod.intValue(), _cons.intValue(), _initialMarking.intValue(), null);
        // System.out.println("edge : (" + srcActorId + "," + trgActorId + " ) prod= " + _prod.intValue() + " cons= " + _cons.intValue() + " d= " +
        // _initialMarking.intValue());
      }

      // GraphStructureHelper.printSDF(g);

      // subgraphs
      jumpToLine(br, 2);
      line = br.readLine().split(" ");
      int nbSubGraphs = Integer.parseInt(line[0]);

      for (int i = 0; i < nbSubGraphs; i++) {
        // parent actor (hierarchical actor)
        SDFAbstractVertex hierarchicalActor;
        jumpToLine(br, 2);
        line = br.readLine().split(" ");
        String _parentActorId = line[0];
        hierarchicalActor = listActors.get(actorsId.get(_parentActorId));

        // get the parent graph of the hierarchical actor
        // SDFGraph topGraph = listActors.get(actorsId.get(_parentActorId)).ParentGraph;
        // get the hierarchical actor
        // HierarchicalActor HActor = topGraph.hierarchise(listActors.get(actorsId.get(_parentActorId)));

        // construct the subgraph
        // print the name of the graph
        jumpToLine(br, 1);
        String subGraphName = br.readLine().split(" ")[0];
        // System.out.println("Graph's name: " + graphName);

        // create new graph
        SDFGraph subGraph = new SDFGraph();
        subGraph.setName(subGraphName);

        // reading the number of actor and edges
        line = jumpToLine(br, 2).split(" ");
        nbActors = Integer.parseInt(line[0]);
        nbEdges = Integer.parseInt(line[1]);
        int nbInI = 0;
        nbInI = Integer.parseInt(line[2]);
        int nbOutI = 0;
        nbOutI = Integer.parseInt(line[3]);

        // reading actors description
        jumpToLine(br, 2);
        for (int j = 0; j < nbActors; j++) {
          line = br.readLine().split(" ");
          // line structure: Id repetitionFactor phaseDuration
          String _actorId = line[0];
          Integer _actorRF = Integer.parseInt(line[1]);
          Double _actorDuration = Double.parseDouble(line[2]);

          // String newActorId = Identifier.generateActorId();
          String newActorId = _actorId;
          actorsId.put(_actorId, newActorId);

          // add the actor to the graph
          SDFAbstractVertex actor = GraphStructureHelper.addActor(subGraph, newActorId, null, _actorRF, _actorDuration, null, null);
          // System.out.println("actor : " + newActorId + " " + _actorRF + " " + _actorDuration);
          listActors.put(newActorId, actor);
        }

        // reading edges description
        jumpToLine(br, 2);
        for (int j = 0; j < nbEdges; j++) {
          line = br.readLine().split(" ");
          // line structure: (source,target) initial_marking
          // production_vector consumption_vector
          String[] source_target = line[0].split(",");
          String srcActorId = actorsId.get(source_target[0].replace("(", ""));
          String trgActorId = actorsId.get(source_target[1].replace(")", ""));
          Double _initialMarking = Double.parseDouble(line[1]);
          Double _prod = Double.parseDouble(line[2]);
          Double _cons = Double.parseDouble(line[3]);

          // add the edge to the graph
          GraphStructureHelper.addEdge(subGraph, srcActorId, null, trgActorId, null, _prod.intValue(), _cons.intValue(), _initialMarking.intValue(), null);
          // System.out.println(
          // "edge : (" + srcActorId + "," + trgActorId + " ) prod= " + _prod.intValue() + " cons= " + _cons.intValue() + " d= " + _initialMarking.intValue());
        }

        // reading Input interfaces description
        jumpToLine(br, 2);
        for (int j = 0; j < nbInI; j++) {
          line = br.readLine().split(" ");
          // line structure: (source,target) consumption_vector
          String[] source_target = line[0].split(",");
          String srcActorId = actorsId.get(source_target[0].replace("(", ""));
          Double _cons = 0.;

          String inputPort = null;
          // get the name of the input interface
          for (SDFInterfaceVertex input : hierarchicalActor.getSources()) {
            if (hierarchicalActor.getAssociatedEdge(input).getSource().getName().equals(srcActorId)) {
              inputPort = input.getName();
              _cons = (double) hierarchicalActor.getAssociatedEdge(input).getCons().intValue();
              break;
            }
          }

          String trgActorId = actorsId.get(source_target[1].replace(")", ""));
          Double _prod = Double.parseDouble(line[1]);

          GraphStructureHelper.addInputInterface(subGraph, inputPort, null, InterfaceDuration_default, null, null);
          // System.out.println("inputInterface : " + inputPort);
          GraphStructureHelper.addEdge(subGraph, inputPort, null, trgActorId, null, _prod.intValue(), _cons.intValue(), 0, null);
          // System.out.println("edge : (" + inputPort + "," + trgActorId + " ) prod= " + _prod.intValue() + " cons= " + _cons.intValue());
        }

        // reading Output interfaces description
        jumpToLine(br, 2);
        for (int j = 0; j < nbOutI; j++) {
          line = br.readLine().split(" ");
          // line structure: (source,target) production_vector
          String[] source_target = line[0].split(",");
          String trgActorId = actorsId.get(source_target[1].replace(")", ""));
          Double _prod = 0.;

          String outputPort = null;
          // get the name of the output interface
          for (SDFInterfaceVertex output : hierarchicalActor.getSinks()) {
            if (hierarchicalActor.getAssociatedEdge(output).getTarget().getName().equals(trgActorId)) {
              outputPort = output.getName();
              _prod = (double) hierarchicalActor.getAssociatedEdge(output).getProd().intValue();
              break;
            }
          }

          String srcActorId = actorsId.get(source_target[0].replace("(", ""));
          Double _cons = Double.parseDouble(line[1]);

          GraphStructureHelper.addOutputInterface(subGraph, outputPort, null, InterfaceDuration_default, null, null);
          // System.out.println("outputInterface : " + outputPort);
          GraphStructureHelper.addEdge(subGraph, srcActorId, null, outputPort, null, _prod.intValue(), _cons.intValue(), 0, null);
          // System.out.println("edge : (" + srcActorId + "," + outputPort + " ) prod= " + _prod.intValue() + " cons= " + _cons.intValue());
        }

        // add the subgraph description to the hierarchical actor
        hierarchicalActor.setGraphDescription(subGraph);
        // GraphStructureHelper.printSDF(subGraph);
      }

      // Close the input stream
      br.close();

      timer.stop();
      System.out.println("IBSDF Graph Imported in " + timer.toString());

      // return the imported graph
      return g;

    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
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
  private static String jumpToLine(BufferedReader br, int n) {
    try {
      for (int i = 1; i < n; i++) {
        br.readLine();
      }
      return br.readLine();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }
}
