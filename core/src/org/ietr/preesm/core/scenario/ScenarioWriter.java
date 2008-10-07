/**
 * 
 */
package org.ietr.preesm.core.scenario;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mpelcat
 *
 */
public class ScenarioWriter {

	/**
	 * Current document
	 */
	private Document dom;
	
	/**
	 * Current scenario
	 */
	private Scenario scenario;
	
	public ScenarioWriter(Scenario scenario) {
		super();

		this.scenario = scenario;

        try {
			//Create instance of DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//Get the DocumentBuilder
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			//Create blank DOM Document
			dom = docBuilder.newDocument();

			//create the root element
			Element root = dom.createElement("scenario");
			
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void generateScenarioDOM() {

		Element root = dom.getDocumentElement();
		
		addFiles(root);
		addConstraints(root);
		addTimings(root);
	}
	
	public void addFiles(Element parent) {

		Element files= dom.createElement("files");
		parent.appendChild(files);
		
		Element algo = dom.createElement("algorithm");
		parent.appendChild(algo);
		algo.setAttribute("url", scenario.getAlgorithmURL());
		
		Element archi = dom.createElement("architecture");
		parent.appendChild(archi);
		archi.setAttribute("url", scenario.getArchitectureURL());
		
		
	}

	public void addConstraints(Element parent) {

		Element constraints = dom.createElement("constraints");
		parent.appendChild(constraints);
		
		for(ConstraintGroup cst:scenario.getConstraintGroupManager().getConstraintGroups()){
			addConstraint(constraints, cst);
		}
	}

	public void addConstraint(Element parent, ConstraintGroup cst) {

		Element constraint = dom.createElement("constraint");
		parent.appendChild(constraint);
		
		for(OperatorDefinition opdef:cst.getOperatorDefinitions()){
			Element opdefelt = dom.createElement("operator");
			constraint.appendChild(opdefelt);
			opdefelt.setAttribute("name", opdef.getId());
		}
		
		for(SDFAbstractVertex vtx:cst.getVertices()){
			Element vtxelt = dom.createElement("task");
			constraint.appendChild(vtxelt);
			vtxelt.setAttribute("name", vtx.getName());
		}
	}

	public void addTimings(Element parent) {

		Element timings = dom.createElement("timings");
		parent.appendChild(timings);
		
		for(Timing timing:scenario.getTimingManager().getTimings()){
			addTiming(timings, timing);
		}
	}

	public void addTiming(Element parent, Timing timing) {

		Element timingelt = dom.createElement("timing");
		parent.appendChild(timingelt);
		timingelt.setAttribute("vertexname", timing.getVertex().getName());
		timingelt.setAttribute("opname", timing.getOperatorDefinition().getId());
		timingelt.setAttribute("time", Integer.toString(timing.getTime()));
	}
}
