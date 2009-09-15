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
import org.ietr.preesm.core.architecture.HierarchyPort;
import org.ietr.preesm.core.architecture.Interconnection;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNode;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Dma;
import org.ietr.preesm.core.architecture.simplemodel.DmaDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNode;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Ram;
import org.ietr.preesm.core.architecture.simplemodel.RamDefinition;
import org.ietr.preesm.core.tools.PreesmLogger;
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
			dom = impl.createDocument(
					"http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4",
					"spirit:design", null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Document generateArchitectureDOM() {

		Element root = dom.getDocumentElement();

		addID(root);
		addComponentInstances(root);
		addInterconnections(root);
		if (!archi.getHierarchyPorts().isEmpty()) {
			addHierarchicalPorts(root);
		}

		return dom;
	}

	public void writeDom(String fileName) {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		Path relativePath = new Path(fileName);

		IFile file = workspace.getRoot().getFile(relativePath);

		if (file != null) {
			try {
				// Gets the DOM implementation of document
				DOMImplementation impl = dom.getImplementation();
				DOMImplementationLS implLS = (DOMImplementationLS) impl;

				LSOutput output = implLS.createLSOutput();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				output.setByteStream(out);

				LSSerializer serializer = implLS.createLSSerializer();
				serializer.getDomConfig().setParameter("format-pretty-print",
						true);
				serializer.write(dom, output);

				if (file.exists()) {
					file.setContents(
							new ByteArrayInputStream(out.toByteArray()), true,
							false, new NullProgressMonitor());
				} else {

					file.create(new ByteArrayInputStream(out.toByteArray()),
							true, new NullProgressMonitor());
				}
				out.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"Architecture exporter has an invalid file path.");
		}
	}

	private void addInterconnections(Element parent) {

		Element intsElt = dom.createElement("spirit:interconnections");
		parent.appendChild(intsElt);

		for (Interconnection intc : archi.getInterconnections()) {
			addInterconnection(intsElt, intc);
		}
	}

	private void addInterconnection(Element parent, Interconnection intc) {

		Element intElt = dom.createElement("spirit:interconnection");
		parent.appendChild(intElt);

		Element nameElt = dom.createElement("spirit:name");
		intElt.appendChild(nameElt);

		// Writing the setup times on the setup edges
		if (intc.isSetup()) {

			// The display named is used to characterized setup edges
			Element displayName = dom.createElement("spirit:displayName");
			intElt.appendChild(displayName);
			displayName.setTextContent("setup");

			Operator o = (Operator) intc.getSource();
			Element descElt = dom.createElement("spirit:description");
			intElt.appendChild(descElt);

			if (intc.getTarget() instanceof Dma) {
				Dma d = (Dma) intc.getTarget();
				DmaDefinition dd = (DmaDefinition) d.getDefinition();
				descElt.setTextContent(Long.toString(dd.getSetupTime(o)));
			} else if (intc.getTarget() instanceof Ram) {
				Ram d = (Ram) intc.getTarget();
				RamDefinition dd = (RamDefinition) d.getDefinition();
				descElt.setTextContent(Long.toString(dd.getSetupTime(o)));
			}
		}

		// Writing the interfaces
		Element intf1Elt = dom.createElement("spirit:activeInterface");
		intElt.appendChild(intf1Elt);
		intf1Elt.setAttribute("spirit:busRef", intc.getSrcIf()
				.getBusReference().getId());
		intf1Elt
				.setAttribute("spirit:componentRef", intc.getSource().getName());

		Element intf2Elt = dom.createElement("spirit:activeInterface");
		intElt.appendChild(intf2Elt);
		intf2Elt.setAttribute("spirit:busRef", intc.getTgtIf()
				.getBusReference().getId());
		intf2Elt
				.setAttribute("spirit:componentRef", intc.getTarget().getName());

	}

	private void addHierarchicalPorts(Element parent) {

		Element hiersElt = dom.createElement("spirit:hierConnections");
		parent.appendChild(hiersElt);

		for (HierarchyPort p : archi.getHierarchyPorts()) {
			addHierarchicalPort(hiersElt, p);
		}
	}

	private void addHierarchicalPort(Element parent, HierarchyPort p) {

		Element hierElt = dom.createElement("spirit:hierConnection");
		parent.appendChild(hierElt);
		
		hierElt.setAttribute("spirit:interfaceRef", p.getName());

		Element intfElt = dom.createElement("spirit:activeInterface");
		hierElt.appendChild(intfElt);
		
		intfElt.setAttribute("spirit:busRef", p.getBusReference());
		intfElt.setAttribute("spirit:componentRef", p.getConnectedOperator());
	}

	private void addID(Element parent) {

		if (archi.getId() != null) {
			Element id = dom.createElement("spirit:id");
			parent.appendChild(id);
			id.setTextContent(archi.getId());
		}

		Element name = dom.createElement("spirit:name");
		parent.appendChild(name);
		name.setTextContent(archi.getName());
	}

	private void addComponentInstances(Element parent) {

		Element cmpsElt = dom.createElement("spirit:componentInstances");
		parent.appendChild(cmpsElt);

		for (ArchitectureComponent cmp : archi.getComponents()) {
			addComponentInstance(cmpsElt, cmp);
		}
	}

	private void addComponentInstance(Element parent, ArchitectureComponent cmp) {

		Element cmpElt = dom.createElement("spirit:componentInstance");
		parent.appendChild(cmpElt);

		Element nameElt = dom.createElement("spirit:instanceName");
		cmpElt.appendChild(nameElt);
		nameElt.setTextContent(cmp.getName());

		addVLNV(cmpElt, cmp);

		addConfigurableElementValues(cmpElt, cmp);
	}

	private void addVLNV(Element parent, ArchitectureComponent cmp) {

		Element vlnvElt = dom.createElement("spirit:componentRef");
		parent.appendChild(vlnvElt);
		vlnvElt.setAttribute("spirit:library", cmp.getDefinition().getVlnv()
				.getLibrary());
		vlnvElt.setAttribute("spirit:name", cmp.getDefinition().getVlnv()
				.getName());
		vlnvElt.setAttribute("spirit:vendor", cmp.getDefinition().getVlnv()
				.getVendor());
		vlnvElt.setAttribute("spirit:version", cmp.getDefinition().getVlnv()
				.getVersion());

	}

	private void addConfigurableElementValues(Element parent,
			ArchitectureComponent cmp) {

		Element confsElt = dom
				.createElement("spirit:configurableElementValues");
		parent.appendChild(confsElt);

		addParameter(confsElt, "componentType", cmp.getType().getName());
		addParameter(confsElt, "refinement", cmp.getRefinementName());

		// Writing component parameters depending on their type
		if (cmp instanceof ContentionNode) {
			ContentionNodeDefinition def = (ContentionNodeDefinition) ((ContentionNode) cmp)
					.getDefinition();
			addParameter(confsElt, "node_dataRate", Float.toString(def
					.getDataRate()));
		} else if (cmp instanceof ParallelNode) {
			ParallelNodeDefinition def = (ParallelNodeDefinition) ((ParallelNode) cmp)
					.getDefinition();
			addParameter(confsElt, "node_dataRate", Float.toString(def
					.getDataRate()));
		} else if (cmp instanceof Medium) {
			MediumDefinition def = (MediumDefinition) ((Medium) cmp)
					.getDefinition();
			addParameter(confsElt, "medium_dataRate", Float.toString(def
					.getDataRate()));
			addParameter(confsElt, "medium_overhead", Integer.toString(def
					.getOverheadTime()));
		} else if (cmp instanceof Operator) {
			OperatorDefinition def = (OperatorDefinition) ((Operator) cmp)
					.getDefinition();
			addParameter(confsElt, "dataCopySpeed", Float.toString(def
					.getDataCopySpeed()));
		}
	}

	private void addParameter(Element parent, String name, String value) {

		Element paramElt = dom.createElement("spirit:configurableElementValue");
		parent.appendChild(paramElt);
		paramElt.setAttribute("spirit:referenceId", name);
		paramElt.setTextContent(value);
	}

}
