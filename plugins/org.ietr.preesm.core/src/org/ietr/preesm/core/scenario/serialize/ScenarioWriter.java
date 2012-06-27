/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.scenario.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

import net.sf.dftools.algorithm.model.parameters.Variable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.RelativeConstraintManager;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.types.DataType;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * Writes a scenario as an XML
 * 
 * @author mpelcat
 */
public class ScenarioWriter {

	/**
	 * Current document
	 */
	private Document dom;

	/**
	 * Current scenario
	 */
	private PreesmScenario scenario;

	public ScenarioWriter(PreesmScenario scenario) {
		super();

		this.scenario = scenario;

		try {
			DOMImplementation impl;
			impl = DOMImplementationRegistry.newInstance()
					.getDOMImplementation("Core 3.0 XML 3.0 LS");
			dom = impl.createDocument("", "scenario", null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Document generateScenarioDOM() {

		Element root = dom.getDocumentElement();

		addFiles(root);
		addConstraints(root);
		addRelativeConstraints(root);
		addTimings(root);
		addSimuParams(root);
		addVariables(root);

		return dom;
	}

	private void addVariables(Element parent) {

		Element variables = dom.createElement("variables");
		parent.appendChild(variables);

		variables.setAttribute("excelUrl", scenario.getVariablesManager()
				.getExcelFileURL());

		Collection<Variable> variablesSet = scenario.getVariablesManager()
				.getVariables().values();
		for (Variable variable : variablesSet) {
			addVariable(variables, variable.getName(), variable.getValue());
		}
	}

	private void addVariable(Element parent, String variableName,
			String variableValue) {

		Element variableelt = dom.createElement("variable");
		parent.appendChild(variableelt);
		variableelt.setAttribute("name", variableName);
		variableelt.setAttribute("value", variableValue);
	}

	private void addSimuParams(Element parent) {

		Element params = dom.createElement("simuParams");
		parent.appendChild(params);

		Element core = dom.createElement("mainCore");
		params.appendChild(core);
		core.setTextContent(scenario.getSimulationManager()
				.getMainOperatorName());

		Element medium = dom.createElement("mainComNode");
		params.appendChild(medium);
		medium.setTextContent(scenario.getSimulationManager()
				.getMainComNodeName());

		Element dataSize = dom.createElement("averageDataSize");
		params.appendChild(dataSize);
		dataSize.setTextContent(String.valueOf(scenario.getSimulationManager()
				.getAverageDataSize()));

		Element dataTypes = dom.createElement("dataTypes");
		params.appendChild(dataTypes);

		for (DataType dataType : scenario.getSimulationManager().getDataTypes()
				.values()) {
			addDataType(dataTypes, dataType);
		}

		Element sVOperators = dom.createElement("specialVertexOperators");
		params.appendChild(sVOperators);

		for (String opId : scenario.getSimulationManager()
				.getSpecialVertexOperatorIds()) {
			addSpecialVertexOperator(sVOperators, opId);
		}
	}

	private void addDataType(Element parent, DataType dataType) {

		Element dataTypeElt = dom.createElement("dataType");
		parent.appendChild(dataTypeElt);
		dataTypeElt.setAttribute("name", dataType.getTypeName());
		dataTypeElt.setAttribute("size", Integer.toString(dataType.getSize()));
	}

	private void addSpecialVertexOperator(Element parent, String opId) {

		Element dataTypeElt = dom.createElement("specialVertexOperator");
		parent.appendChild(dataTypeElt);
		dataTypeElt.setAttribute("path", opId);
	}

	public void writeDom(IFile file) {

		try {
			// Gets the DOM implementation of document
			DOMImplementation impl = dom.getImplementation();
			DOMImplementationLS implLS = (DOMImplementationLS) impl;

			LSOutput output = implLS.createLSOutput();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			output.setByteStream(out);

			LSSerializer serializer = implLS.createLSSerializer();
			serializer.getDomConfig().setParameter("format-pretty-print", true);
			serializer.write(dom, output);

			file.setContents(new ByteArrayInputStream(out.toByteArray()), true,
					false, new NullProgressMonitor());
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addFiles(Element parent) {

		Element files = dom.createElement("files");
		parent.appendChild(files);

		Element algo = dom.createElement("algorithm");
		files.appendChild(algo);
		algo.setAttribute("url", scenario.getAlgorithmURL());

		Element archi = dom.createElement("architecture");
		files.appendChild(archi);
		archi.setAttribute("url", scenario.getArchitectureURL());

		Element codeGenDir = dom.createElement("codegenDirectory");
		files.appendChild(codeGenDir);
		codeGenDir.setAttribute("url", scenario.getCodegenManager()
				.getCodegenDirectory());

	}

	private void addConstraints(Element parent) {

		Element constraints = dom.createElement("constraints");
		parent.appendChild(constraints);

		constraints.setAttribute("excelUrl", scenario
				.getConstraintGroupManager().getExcelFileURL());

		for (ConstraintGroup cst : scenario.getConstraintGroupManager()
				.getConstraintGroups()) {
			addConstraint(constraints, cst);
		}
	}

	private void addConstraint(Element parent, ConstraintGroup cst) {

		Element constraintGroupElt = dom.createElement("constraintGroup");
		parent.appendChild(constraintGroupElt);

		for (String opId : cst.getOperatorIds()) {
			Element opdefelt = dom.createElement("operator");
			constraintGroupElt.appendChild(opdefelt);
			opdefelt.setAttribute("name", opId);
		}

		for (String vtxId : cst.getVertexPaths()) {
			Element vtxelt = dom.createElement("task");
			constraintGroupElt.appendChild(vtxelt);
			vtxelt.setAttribute("name", vtxId);
		}
	}
	

	private void addRelativeConstraints(Element parent) {

		RelativeConstraintManager manager = scenario.getRelativeconstraintManager();
		Element timings = dom.createElement("relativeconstraints");
		parent.appendChild(timings);

		timings.setAttribute("excelUrl", manager
				.getExcelFileURL());

		for (String id : manager.getExplicitConstraintIds()) {
			addRelativeConstraint(timings, id, manager.getConstraintOrDefault(id));
		}
	}

	private void addRelativeConstraint(Element parent, String id, int group) {

		Element timingelt = dom.createElement("relativeconstraint");
		parent.appendChild(timingelt);
		timingelt.setAttribute("vertexname", id);
		timingelt.setAttribute("group", Integer.toString(group));
	}

	private void addTimings(Element parent) {

		Element timings = dom.createElement("timings");
		parent.appendChild(timings);

		timings.setAttribute("excelUrl", scenario.getTimingManager()
				.getExcelFileURL());

		for (Timing timing : scenario.getTimingManager().getTimings()) {
			addTiming(timings, timing);
		}
	}

	private void addTiming(Element parent, Timing timing) {

		Element timingelt = dom.createElement("timing");
		parent.appendChild(timingelt);
		timingelt.setAttribute("vertexname", timing.getSdfVertexId());
		timingelt.setAttribute("opname", timing.getOperatorDefinitionId());
		timingelt.setAttribute("time", Integer.toString(timing.getTime()));
	}
}
