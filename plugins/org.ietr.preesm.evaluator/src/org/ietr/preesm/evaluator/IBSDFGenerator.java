/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * blaunay <bapt.launay@gmail.com> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.evaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.ietr.dftools.algorithm.exporter.GMLSDFExporter;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.*;
import org.ietr.dftools.algorithm.model.sdf.esdf.*;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.algorithm.importSdf3Xml.Sdf3XmlParser;

/**
 * Generator of hierarchical graphs (IBSDF) from alive SDF graphs generated
 * with <a href="https://github.com/bbodin/turbine/">Turbine</a>, the only parameter to give is the number of actors.
 * Used independently from the throughput evaluator to create instances for test
 * @author blaunay
 *
 */
public class IBSDFGenerator {

	// Parameters
	public int nbactors;
	public Random rand;
	// change the path according to where Turbine is placed, and create folders IBgen and IBSDF in this directory
	public final String path = "/home/anmorvan/git/turbine/Turbine/";

	// Set of graphs
	public ArrayList<SDFGraph> graphSet;

	public IBSDFGenerator(int n) {
		graphSet = new ArrayList<SDFGraph>();
		nbactors = n;
		rand = new Random();
	}


	/**
	 * Generates randomly a set of SDF graphs which will be used to construct an IBSDF graph
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void graphSet_gen() throws IOException, InterruptedException {
		int remaining_actors = nbactors;
		int randomNum;
		Process p;
		p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "rm -f "+path+"IBgen/*"});
		p.waitFor();
		while (remaining_actors > 0) {
			// draw a random number for the graph size (no graph with 1 actor)
		    randomNum = rand.nextInt((nbactors/3 -1) +1) +2;
		    if (remaining_actors < randomNum || remaining_actors - randomNum == 1)
		    	randomNum = remaining_actors;
		    remaining_actors -= randomNum;

		    // Generation of the graph
		    p = Runtime.getRuntime().exec(new String[]{	path+"SDFGenerator.py",
		    		Integer.toString(randomNum),Integer.toString(graphSet.size())});
		    p.waitFor();

		    // Import it in PREESM
		    File file = new File(path+"IBgen/"+String.valueOf(graphSet.size())+".sdf3");
			InputStream iStream = new FileInputStream(file);
			Sdf3XmlParser importer = new Sdf3XmlParser();
			SDFGraph G = importer.parse(iStream);
			G.setName(String.valueOf(graphSet.size()));
			graphSet.add(G);
		}
	}


	/**
	 * Picks randomly nb vertices in the graph G
	 * @param G
	 * @param nb
	 * @return list of vertices
	 */
	private ArrayList<SDFAbstractVertex> randomVertices(SDFGraph G, int nb) {
		int tmp = rand.nextInt((G.vertexSet().size()));
		ArrayList<SDFAbstractVertex> vertices = new ArrayList<SDFAbstractVertex>(G.vertexSet());
		ArrayList<SDFAbstractVertex> verticestoreturn = new ArrayList<SDFAbstractVertex>();
		for (int i=0; i<nb; i++) {
			// make sure we dont choose interface vertices
			while (vertices.get(tmp) instanceof SDFInterfaceVertex)
				tmp = rand.nextInt((G.vertexSet().size()));
			verticestoreturn.add(vertices.get(tmp));
			tmp = rand.nextInt((G.vertexSet().size()));
		}
		return verticestoreturn;
	}


	/**
	 * Inserts the graph g in the vertex v by adding corresponding interfaces
	 * and ensuring the liveness of the whole graph
	 * @param g
	 * @param v
	 */
	private void insert(SDFGraph g, SDFAbstractVertex v) {
		v.setGraphDescription(g);
		int nbsources = v.getSources().size();
		int nbsinks = v.getSinks().size();
		// we need nbsources+nbsinks vertices to connect to the interfaces
		ArrayList<SDFAbstractVertex> toconnect = randomVertices(g, nbsources+nbsinks);
		AbstractEdgePropertyType<?> E_in;

		// Add the necessary interfaces and edges connecting them to the graph
		for (int i=0; i<nbsources; i++) {
			g.addVertex(v.getSources().get(i));

			// output of the interface
			SDFSinkInterfaceVertex interface_output = new SDFSinkInterfaceVertex();
			interface_output.setName(v.getSources().get(i).getName());
			v.getSources().get(i).addSink(interface_output);

			SDFSourceInterfaceVertex inPort = new SDFSourceInterfaceVertex();
			inPort.setName("from"+v.getSources().get(i).getName());
			toconnect.get(i).addSource(inPort);

			SDFEdge newEdge = g.addEdge(v.getSources().get(i), toconnect.get(i));
			newEdge.setSourceInterface(interface_output);
			newEdge.setTargetInterface(inPort);
			E_in = v.getAssociatedEdge(v.getSources().get(i)).getCons();
			newEdge.setProd(E_in);
			newEdge.setCons(E_in);
		}
		// Add the sink interfaces
		for (int i=0; i<nbsinks; i++) {
			g.addVertex(v.getSinks().get(i));

			SDFSourceInterfaceVertex interface_input = new SDFSourceInterfaceVertex();
			interface_input.setName(v.getSinks().get(i).getName());
			v.getSinks().get(i).addSource(interface_input);

			SDFSinkInterfaceVertex outPort = new SDFSinkInterfaceVertex();
			outPort.setName("to"+v.getSinks().get(i).getName());
			toconnect.get(i+nbsources).addSink(outPort);

			SDFEdge newEdge = g.addEdge(toconnect.get(i+nbsources),v.getSinks().get(i));
			newEdge.setSourceInterface(outPort);
			newEdge.setTargetInterface(interface_input);
			E_in = v.getAssociatedEdge(v.getSinks().get(i)).getProd();
			newEdge.setProd(E_in);
			newEdge.setCons(E_in);
		}
	}


	/**
	 * Counts the number of actors of a graph g (without the possible interfaces)
	 */
	private int nbActors(SDFGraph g) {
		int cpt = 0;
		for (SDFAbstractVertex v : g.vertexSet())
			if (!(v instanceof SDFInterfaceVertex))
				cpt++;
		return cpt;
	}


	/**
	 * Constructs randomly a hierarchical graph from the SDF graphs in the set.
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SDF4JException
	 * @throws InvalidExpressionException
	 */
	public boolean hierarchize() throws IOException, InterruptedException, SDF4JException, InvalidExpressionException {
		int remaining_graphs, current, r;
		// index of current graph
		current = 0;
		remaining_graphs = graphSet.size()-1;
		ArrayList<SDFAbstractVertex> selectedVertices = new ArrayList<SDFAbstractVertex>();
		SDFAbstractVertex tmp;

		while (remaining_graphs > 0) {
			// pick r < remaining_graphs and r < nbacteurs
			r = rand.nextInt(Math.min(remaining_graphs,nbActors(graphSet.get(current)))+1);
			// choose r vertices in the current graph
			while (selectedVertices.size() < r) {
				tmp = randomVertices(graphSet.get(current), 1).get(0);
				while (selectedVertices.contains(tmp))
					tmp = randomVertices(graphSet.get(current), 1).get(0);
				selectedVertices.add(tmp);
			}
			// insert the r graph in the r vertices
			for (int i=1; i<=r; i++) {
				insert(graphSet.get(current+i),selectedVertices.get(i-1));
			}
			selectedVertices.clear();

			remaining_graphs -= r;
			// new current graph is the next graph not yet considered
			current += r;
		}

		// Export the graph
		GMLSDFExporter exporter = new GMLSDFExporter();
		Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "rm -f "+path+"IBSDF/*"});
		p.waitFor();
		exporter.export(graphSet.get(0), path+"IBSDF/top.graphml");

		// Check that there is no problem if we normalize and evaluate the liveness
		IBSDFThroughputEvaluator eval = new IBSDFThroughputEvaluator();
		NormalizeVisitor normalize = new NormalizeVisitor();
		graphSet.get(0).accept(normalize);
		return eval.is_alive((SDFGraph) normalize.getOutput()) != null;

	}

}
