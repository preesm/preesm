/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.architecture.writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.IOperator;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.codegen.DataType;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * Writes an architecture as an IP-XACT XML file
 * 
 * @author mpelcat
 */
public class DesignWriter {

	/**
	 * Current document
	 */
	private Document dom;

	/**
	 * Current scenario
	 */
	private MultiCoreArchitecture archi;

	public DesignWriter(MultiCoreArchitecture archi) {
		super();

		this.archi = archi;

		try {
			DOMImplementation impl;
			impl = DOMImplementationRegistry.newInstance()
					.getDOMImplementation("Core 3.0 XML 3.0 LS");
			dom = impl.createDocument("http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4", "spirit:design", null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public Document generateArchitectureDOM() {

		Element root = dom.getDocumentElement();

		addID(root);
		addComponentInstances(root);

		return dom;
	}

	public void writeDom(String fileName) {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		Path relativePath = new Path(fileName);
		
		IFile file = workspace.getRoot().getFile(relativePath);
		
		if(file != null){
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

			if(file.exists()){
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true,false,
					 new NullProgressMonitor());
			}
			else{

				file.create(new ByteArrayInputStream(out.toByteArray()), true,
						 new NullProgressMonitor());
			}
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		else{
			PreesmLogger.getLogger().log(Level.SEVERE,"Architecture exporter has an invalid file path.");
		}
	}

	private void addID(Element parent) {

		Element id = dom.createElement("spirit:id");
		parent.appendChild(id);
		id.setTextContent(archi.getId());

		Element name = dom.createElement("spirit:name");
		parent.appendChild(name);
		name.setTextContent(archi.getName());
	}
	
	private void addComponentInstances(Element parent) {

		Element cmpsElt = dom.createElement("spirit:componentInstances");
		parent.appendChild(cmpsElt);
		
		for(ArchitectureComponent cmp : archi.getComponents()){
			addComponentInstance(cmpsElt,cmp);
		}
	}
	
	private void addComponentInstance(Element parent, ArchitectureComponent cmp) {

		Element cmpElt = dom.createElement("spirit:componentInstance");
		parent.appendChild(cmpElt);

		Element nameElt = dom.createElement("spirit:instanceName");
		cmpElt.appendChild(nameElt);
		nameElt.setTextContent(cmp.getName());
		
		addVLNV(cmpElt,cmp);
		
		addConfigurableElementValues(cmpElt,cmp);
	}
	
	private void addVLNV(Element parent, ArchitectureComponent cmp) {

		Element vlnvElt = dom.createElement("spirit:componentRef");
		parent.appendChild(vlnvElt);
		vlnvElt.setAttribute("spirit:library", cmp.getDefinition().getVlnv().getLibrary());
		vlnvElt.setAttribute("spirit:name", cmp.getDefinition().getVlnv().getName());
		vlnvElt.setAttribute("spirit:vendor", cmp.getDefinition().getVlnv().getVendor());
		vlnvElt.setAttribute("spirit:version", cmp.getDefinition().getVlnv().getVersion());
		
	}
	
	private void addConfigurableElementValues(Element parent, ArchitectureComponent cmp) {

		Element confsElt = dom.createElement("spirit:configurableElementValues");
		parent.appendChild(confsElt);
		
		Element typeElt = dom.createElement("spirit:configurableElementValue");
		confsElt.appendChild(typeElt);

		typeElt.setAttribute("spirit:referenceId", "componentType");
		typeElt.setTextContent(cmp.getType().getName());

		Element refElt = dom.createElement("spirit:configurableElementValue");
		confsElt.appendChild(refElt);
		
		refElt.setAttribute("spirit:referenceId", "refinement");
		refElt.setTextContent(cmp.getRefinementName());
		
	}

/*

	private void addSimuParams(Element parent) {

		Element params = dom.createElement("simuParams");
		parent.appendChild(params);

		Element core = dom.createElement("mainCore");
		params.appendChild(core);
		core.setTextContent(scenario.getSimulationManager()
				.getMainOperatorName());

		Element medium = dom.createElement("mainMedium");
		params.appendChild(medium);
		medium.setTextContent(scenario.getSimulationManager()
				.getMainMediumName());
		
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

		for (ArchitectureComponent cmp : scenario.getSimulationManager().getSpecialVertexOperators()) {
			addSpecialVertexOperator(sVOperators,cmp);
		}
	}

	private void addDataType(Element parent, DataType dataType) {

		Element dataTypeElt = dom.createElement("dataType");
		parent.appendChild(dataTypeElt);
		dataTypeElt.setAttribute("name", dataType.getTypeName());
		dataTypeElt.setAttribute("size", Integer.toString(dataType.getSize()));
	}

	private void addSpecialVertexOperator(Element parent, ArchitectureComponent cmp) {

		Element dataTypeElt = dom.createElement("specialVertexOperator");
		parent.appendChild(dataTypeElt);
		dataTypeElt.setAttribute("path", cmp.getInfo());
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

		Element timings = dom.createElement("timingfile");
		files.appendChild(timings);
		timings.setAttribute("url", scenario.getTimingManager()
				.getTimingFileURL());

		Element codeGenDir = dom.createElement("codegenDirectory");
		files.appendChild(codeGenDir);
		codeGenDir.setAttribute("url", scenario.getCodegenManager()
				.getCodegenDirectory());

	}

	private void addConstraints(Element parent) {

		Element constraints = dom.createElement("constraints");
		parent.appendChild(constraints);

		for (ConstraintGroup cst : scenario.getConstraintGroupManager()
				.getConstraintGroups()) {
			addConstraint(constraints, cst);
		}
	}

	private void addConstraint(Element parent, ConstraintGroup cst) {

		Element constraintGroupElt = dom.createElement("constraintGroup");
		parent.appendChild(constraintGroupElt);

		for (IOperator opdef : cst.getOperators()) {
			if (((ArchitectureComponent) opdef).getType() == ArchitectureComponentType.operator) {
				Element opdefelt = dom.createElement("operator");
				constraintGroupElt.appendChild(opdefelt);
				opdefelt.setAttribute("name", ((ArchitectureComponent) opdef)
						.getName());
			} else if (((ArchitectureComponent) opdef).getType() == ArchitectureComponentType.processor) {
				Element opdefelt = dom.createElement("processor");
				constraintGroupElt.appendChild(opdefelt);
				opdefelt.setAttribute("name", ((ArchitectureComponent) opdef)
						.getName());
			} else if (((ArchitectureComponent) opdef).getType() == ArchitectureComponentType.ipCoprocessor) {
				Element opdefelt = dom.createElement("ipCoprocessor");
				constraintGroupElt.appendChild(opdefelt);
				opdefelt.setAttribute("name", ((ArchitectureComponent) opdef)
						.getName());
			}
		}

		for (SDFAbstractVertex vtx : cst.getVertices()) {
			Element vtxelt = dom.createElement("task");
			constraintGroupElt.appendChild(vtxelt);
			vtxelt.setAttribute("name", vtx.getInfo());
		}
	}

	private void addTimings(Element parent) {

		Element timings = dom.createElement("timings");
		parent.appendChild(timings);

		for (Timing timing : scenario.getTimingManager().getTimings()) {
			addTiming(timings, timing);
		}
	}

	private void addTiming(Element parent, Timing timing) {

		Element timingelt = dom.createElement("timing");
		parent.appendChild(timingelt);
		timingelt.setAttribute("vertexname", timing.getVertex().getName());
		timingelt
				.setAttribute("opname", timing.getOperatorDefinition().getId());
		timingelt.setAttribute("time", Integer.toString(timing.getTime()));
	}*/
}
