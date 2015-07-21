package org.ietr.preesm.evaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.ietr.dftools.algorithm.model.sdf.*;
import org.ietr.dftools.algorithm.model.sdf.esdf.*;
import org.ietr.preesm.algorithm.importSdf3Xml.Sdf3XmlParser;

public class IBSDFGenerator {
	
	// Parameters
	public int nbactors;
	
	// Set of graphs
	public ArrayList<SDFGraph> graphSet;
	
	public IBSDFGenerator(int n, int d) {
		graphSet = new ArrayList<SDFGraph>();
		nbactors = n;
	}
	
	private void graphSet_gen() throws IOException, InterruptedException {
		int remaining_actors = nbactors;
		int randomNum;
		Random rand = new Random();
		Process p;
		p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "rm -f /home/blaunay/Bureau/turbine-master/turbine/IBgen/*"});
		p.waitFor();
		while (remaining_actors > 0) {
			// draw a random number for the graph size
		    randomNum = rand.nextInt((nbactors/2 -1) +1) +1;
		    if (remaining_actors < randomNum)
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
			graphSet.add(G);
		}
	}
	
	
	// Insert g in the actor v, by adding interfaces and make sure we got no problem
	// of liveness
	private void insert(SDFGraph g, SDFAbstractVertex v) {
		v.setGraphDescription(g);
		// Add the necessary interfaces
		for (int i=0; i<v.getSources().size(); i++) {
			SDFSourceInterfaceVertex s = new SDFSourceInterfaceVertex();
			s.setName(v.getSources().get(i).getName());
			s.setId(v.getSources().get(i).getId());
			g.addVertex(s);
		}
		for (int i=0; i<v.getSinks().size(); i++) {
			SDFSinkInterfaceVertex s = new SDFSinkInterfaceVertex();
			s.setName(v.getSinks().get(i).getName());
			s.setId(v.getSinks().get(i).getId());
			g.addVertex(s);
		}
		//TODO Add edges between graph and the interfaces with appropriate rates
		//TODO pour chaque interface d'entrée
		// bellman ford retournant longueur et edges du + court chemin
		// pour les interfaces de sortie tq longeur < 0
			// add -longueur tokens sur les arcs du +court chemin de façon random
	}
	
	//rand.nextInt((max - min) + 1) + min
	
	private void hierarchize() {
		Random rand = new Random();
		int nbsublevels;
		int remaining_graphs = graphSet.size()-1;
		// First graph in the list : top graph
		// select a subset to be part of the level -1
		while (remaining_graphs > 0) {
			nbsublevels = rand.nextInt(graphSet.size()-1) +1;
			remaining_graphs -= nbsublevels;
			
			for (int i=graphSet.size()-remaining_graphs; i<graphSet.size(); i++) {
				
			}
			// choisir un sommet au pif
			// compter nb in et out
			// choisir le graphe qui sera son sous niveau
		}
	}
	
	
	public static void main(String [] args) throws IOException, InterruptedException
	{
		IBSDFGenerator x = new IBSDFGenerator(300, 2);
		x.graphSet_gen();
		x.hierarchize();
	}
}