package org.ietr.preesm.evaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.ietr.dftools.algorithm.exporter.GMLSDFExporter;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.*;
import org.ietr.dftools.algorithm.model.sdf.esdf.*;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.algorithm.importSdf3Xml.Sdf3XmlParser;

public class IBSDFGenerator {
	
	// Parameters
	public int nbactors;
	public Random rand;
	
	// Set of graphs
	public ArrayList<SDFGraph> graphSet;
	
	public IBSDFGenerator(int n) {
		graphSet = new ArrayList<SDFGraph>();
		nbactors = n;
		rand = new Random(42);
	}
	
	
	/**
	 * Generates randomly a set of SDF graphs (with Turbine) which will be used to construct an IBSDF graph
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void graphSet_gen() throws IOException, InterruptedException {
		int remaining_actors = nbactors;
		int randomNum;
		Process p;
		p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "rm -f /home/blaunay/Bureau/turbine-master/turbine/IBgen/*"});
		p.waitFor();
		while (remaining_actors > 0) {
			// draw a random number for the graph size (no graph with 1 actor)
		    randomNum = rand.nextInt((nbactors/3 -1) +1) +2;
		    if (remaining_actors < randomNum || remaining_actors - randomNum == 1)
		    	randomNum = remaining_actors;
		    remaining_actors -= randomNum;
		    
		    // Generation of the graph
		    p = Runtime.getRuntime().exec(new String[]{
		    		"/home/blaunay/Bureau/turbine-master/turbine/test.py",
		    		Integer.toString(randomNum),Integer.toString(graphSet.size())});
		    p.waitFor();
			
		    // Import it in PREESM
		    File file = new File("/home/blaunay/Bureau/turbine-master/turbine/IBgen/"+String.valueOf(graphSet.size())+".sdf3");
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
		
		//TODO pour chaque interface d'entrée
		// bellman ford retournant longueur et edges du + court chemin
		// pour les interfaces de sortie tq longeur < 0
			// add -longueur tokens sur les arcs du +court chemin de façon random
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
	
	
	private void hierarchize() throws IOException, InterruptedException, SDF4JException {
		int remaining_graphs, current, r, chosengraph;
		// index of current graph
		current = 0;
		remaining_graphs = graphSet.size()-1;
		ArrayList<SDFAbstractVertex> selectedVertices = new ArrayList<SDFAbstractVertex>();
		SDFAbstractVertex tmp;
		
		while (remaining_graphs > 0) {
			// pick r < remaining_graphs and r < nbacteurs
			r = rand.nextInt(Math.min(remaining_graphs,nbActors(graphSet.get(current))))+1;
			// insert the r next graphs in random vertices of the current graph
			while (selectedVertices.size() < r) {
				// chose randomly a graph among those already treated
				chosengraph = rand.nextInt(current+1);
				tmp = randomVertices(graphSet.get(chosengraph), 1).get(0);
				while (selectedVertices.contains(tmp)) {
					chosengraph = rand.nextInt(current+1);
					tmp = randomVertices(graphSet.get(chosengraph), 1).get(0);
				}
				selectedVertices.add(tmp);
			}
			for (int i=1; i<=r; i++) {
				insert(graphSet.get(current+i),selectedVertices.get(i-1));
			}
			selectedVertices.clear();
			
			remaining_graphs -= r;
			// new current graph is the next graph not yet considered
			current += r; 
		}
		
		//TODO remove
		for (int i=0; i<graphSet.size(); i++) {
			System.out.println(i);
			//for (SDFEdge e : graphSet.get(i).edgeSet())
				//System.out.println(e.getSourceInterface() +" - "+e.getTargetInterface());
			//System.out.println(graphSet.get(i).vertexSet());
			if (graphSet.get(i).getParentVertex() != null)
				System.out.println(graphSet.get(i).getName()+" - "+graphSet.get(i).getParentVertex().getBase().getName()+"("+graphSet.get(i).getParentVertex().getName()+")");
		}
		System.out.println("---------------");
		// Export the graph
		GMLSDFExporter exporter = new GMLSDFExporter();
		Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "rm -f /home/blaunay/Bureau/turbine-master/turbine/IBSDF/*"});
		p.waitFor();
		exporter.export(graphSet.get(0), "/home/blaunay/Bureau/turbine-master/turbine/IBSDF/top.graphml");
		
		IBSDFThroughputEvaluator liveness = new IBSDFThroughputEvaluator();
		NormalizeVisitor normalize = new NormalizeVisitor();
		graphSet.get(0).accept(normalize);
		System.out.println(liveness.is_alive((SDFGraph) normalize.getOutput()) != null);
		
	}
	
	
	public static void main(String [] args) throws IOException, InterruptedException, SDF4JException
	{
		IBSDFGenerator x = new IBSDFGenerator(4);
		x.graphSet_gen();
		x.hierarchize();
	}
}