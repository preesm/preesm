/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2013)
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

package org.ietr.preesm.algorithm.exportDif;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.NoIntegerValueException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;

/**
 * This class is a visitor for SDF graphs whose purpose is to export the visited
 * graph into the DIF format. The visitor should be used only on Single-rate
 * SDF. Each actor of the graph can be given a parameter "nb_node" which will be
 * used in the exported format.
 * 
 * All edges with delays are removed from the exported graph.
 * 
 * All actors of the graph should have an execution time for cores of type x86.
 * 
 * This class is a quick solution to convert very specific graph to the DIF. It
 * is not well coded and should be replaced with a better solution in the
 * future. (Or simply removed if not used anymore)
 * 
 * @see http://www.ece.umd.edu/DSPCAD/dif/index.htm
 * @author kdesnos
 * 
 */
public class DIFExporterVisitor implements
		IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {

	protected PreesmScenario scenario; // The scenario used to retrieve
										// execution times
	protected String moc; // MoC of the Graph
	protected String graphName; // Name of the Graph

	// Map associating an actor to its Map of DIF attributes
	// The Map of attributes associates the name of the attribute to
	// the attributes Objects.
	protected HashMap<String, HashMap<String, Object>> actorAttributes;

	// Map associating an edge to its Map of DIF attributes
	// The Map of attributes associates the name of the attribute to
	// the attributes Objects. (eg. production, consumption rates)
	protected HashMap<String, HashMap<String, Object>> edgeAttributes;

	public DIFExporterVisitor(PreesmScenario scenario) {
		// Initialize the attributes maps
		actorAttributes = new HashMap<String, HashMap<String, Object>>();
		edgeAttributes = new HashMap<String, HashMap<String, Object>>();
		this.scenario = scenario;
		// Currently, all graph will have the sdf moc
		moc = "sdf";
		graphName = "anonymous";
	}

	/**
	 * Default constructor of the visitor. Made private to forbid its use
	 */
	@SuppressWarnings("unused")
	private DIFExporterVisitor() {

	}

	@Override
	public void visit(SDFEdge edge) {

		// Skip edges with delays
		try {
			if (edge.getDelay().intValue() > 0) {
				return;
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}

		// Add the edge entry to the actorAttributes Map
		// With an empty attributes map
		HashMap<String, Object> attributesMap = new HashMap<String, Object>();
		// Create the edge name := <source_name>__<target_name>
		String edgeName = edge.getSource().getName() + "__"
				+ edge.getTarget().getName();

		// If there was already an edge between those two vertices, it will
		// be replaced
		edgeAttributes.put(edgeName, attributesMap);

		// Fill the map with the edge attributes
		attributesMap.put("source", edge.getSource().getName());
		attributesMap.put("target", edge.getTarget().getName());

		String comm_cost = edge.getPropertyStringValue("comm_cost");
		if (comm_cost != null) {
			attributesMap.put("comm_cost", Integer.decode(comm_cost));
		}

	}

	@Override
	public void visit(SDFGraph sdf) throws SDF4JException {

		// Retrieve the name of the graph
		String name = sdf.getName();
		if (name != null) {
			graphName = name;
		}

		// visit vertices
		for (SDFAbstractVertex vertex : sdf.vertexSet()) {
			vertex.accept(this);
		}

		// visit edges
		for (SDFEdge edge : sdf.edgeSet()) {
			edge.accept(this);
		}

	}

	@Override
	public void visit(SDFAbstractVertex vertex) throws SDF4JException {
		// Add the vertex entry to the actorAttributes Map
		// With an empty attributes map
		HashMap<String, Object> attributesMap = new HashMap<String, Object>();
		actorAttributes.put(vertex.getName(), attributesMap);

		// Fill the map with the vertex attributes

		// Retrieve execution time
		// Only the execution runtime for cores with type x86 will be taken
		// into account
		Timing time = scenario.getTimingManager().getTimingOrDefault(
				vertex.getId(), "x86");
		attributesMap.put("exec_time", time.getStringValue());
		attributesMap.put("hierarchical", false);

		// If the actor has a refinement
		if (vertex.getRefinement() != null) {
			// Check if the actor is hierarchical
			String hierarchical = vertex.getRefinement().getClass().toString();

			if (hierarchical.endsWith("SDFGraph")) {
				attributesMap.put("hierarchical", true);

				// Overwrite the execution time for hierarchical actors
				attributesMap.remove("exec_time");
				time = scenario.getTimingManager()
						.generateVertexTimingFromHierarchy(vertex, "x86");
				// If the previous method returns an unexpected result
				// it might be because actor with default timing (100) are
				// not taken into account when computing the exec_time of
				// a hierarchical actor.
				attributesMap.put("exec_time", time.getStringValue());
			}
		}

		// Retrieve the number of cores associated to the actor.
		try {
			Argument nbCoreArg = vertex.getArgument("nb_cores");
			if (nbCoreArg != null) {
				int nbCores = nbCoreArg.intValue();
				attributesMap.put("nb_cores", nbCores);
			} else {
				attributesMap.put("nb_cores", 1);
			}
		} catch (InvalidExpressionException e) {

			// If the number of core is not specified, 1 is the default
			attributesMap.put("nb_cores", 1);
		} catch (NoIntegerValueException e) {
			// If the number of core is not specified, 1 is the default
			attributesMap.put("nb_cores", 1);
		}

	}

	/**
	 * This method is called after a graph has been visited by the visitor and
	 * write the resulting DIF graph into a text file.
	 * 
	 * @param file
	 *            the file where to write the DIF graph
	 */
	public void write(File file) {
		try {
			FileWriter writer = new FileWriter(file);

			// Main block
			writer.write(moc + ' ');
			writer.write(graphName + " {" + '\n');

			writeTopologyBlock(writer);

			writeActorBlocks(writer);

			writeEdgeBlocks(writer);

			writer.write("}");

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write the topology block of the files
	 * 
	 * @param writer
	 *            the writer where to write the DIF graph
	 */
	public void writeTopologyBlock(FileWriter writer) {
		try {
			// Topology block
			writer.write('\t' + "topology {" + '\n');

			// Nodes
			writeTopologyNodes(writer);

			// Edges
			writeTopologyEdges(writer);

			writer.write('\t' + "}" + "\n\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Methods to write the node section of the topology block
	 * 
	 * @param writer
	 *            the writer to use
	 * @throws IOException
	 */
	protected void writeTopologyNodes(FileWriter writer) throws IOException {
		writer.write("\t\t" + "nodes=");
		boolean first = true;
		int i = 0;
		for (String nodeName : actorAttributes.keySet()) {
			if (!first) {
				writer.write(", ");
				if (i == 0) {
					writer.write('\n' + "\t\t\t");
				}
			}
			writer.write(nodeName);
			first = false;
			i = (i + 1) % 5;
		}
		writer.write(";" + '\n');
	}

	/**
	 * Methods to write the edge section of the topology block
	 * 
	 * @param writer
	 *            the writer to use
	 * @throws IOException
	 */
	protected void writeTopologyEdges(FileWriter writer) throws IOException {
		writer.write("\t\t" + "edges=");
		boolean first = true;
		for (String edgeName : edgeAttributes.keySet()) {
			if (!first) {
				writer.write(", ");
				writer.write('\n' + "\t\t\t");
			}
			writer.write(edgeName);
			writer.write("(" + edgeAttributes.get(edgeName).get("source"));
			writer.write(", " + edgeAttributes.get(edgeName).get("target")
					+ ")");
			first = false;
		}
		writer.write(";" + '\n');
	}

	/**
	 * Write the Actor blocks of the files
	 * 
	 * @param writer
	 *            the writer where to write the DIF graph
	 */
	public void writeActorBlocks(FileWriter writer) {
		try {
			// Print a block for each actor
			for (String actorName : actorAttributes.keySet()) {
				// Actor block
				writer.write('\t' + "actor " + actorName + " {\n");
				// Exec time
				writer.write("\t\tExecutionTime = "
						+ actorAttributes.get(actorName).get("exec_time"));
				writer.write(";\n");

				// Hierarchical node ?
				writer.write("\t\thierarchical = "
						+ actorAttributes.get(actorName).get("hierarchical"));
				writer.write(";\n");

				// NB cores
				writer.write("\t\tProcessorNumber = "
						+ actorAttributes.get(actorName).get("nb_cores"));
				writer.write(";\n");

				writer.write("\t}\n\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeEdgeBlocks(FileWriter writer) {
		try {
			// Print a block for each edge
			for (String edgeName : edgeAttributes.keySet()) {
				// Print the block only if the attribute map is not empty
				if (!edgeAttributes.get(edgeName).isEmpty()) {
					// Edge block
					writer.write('\t' + "edge " + edgeName + " {\n");

					// Exec time
					if (edgeAttributes.get(edgeName).get("comm_cost") != null) {
						writer.write("\t\tExecutionTime = "
								+ edgeAttributes.get(edgeName).get("comm_cost"));
						writer.write(";\n");
					}

					writer.write("\t}\n\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
