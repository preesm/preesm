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
 
package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

import java.util.HashMap;
import java.util.Vector;

public class TGVertexDescriptor extends ComponentDescriptor {

	protected Vector<LinkDescriptor> outputLinks;

	protected Vector<LinkDescriptor> inputLinks;

	public TGVertexDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		outputLinks = new Vector<LinkDescriptor>();
		inputLinks = new Vector<LinkDescriptor>();
	}

	public TGVertexDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		outputLinks = new Vector<LinkDescriptor>();
		inputLinks = new Vector<LinkDescriptor>();
	}

	public void addOutputLink(LinkDescriptor link) {
		outputLinks.add(link);
	}

	public LinkDescriptor getOutputLink(int index) {
		return outputLinks.get(index);
	}

	public Vector<LinkDescriptor> getOutputLinks() {
		return outputLinks;
	}

	public void setOutputLinks(Vector<LinkDescriptor> outputLinks) {
		this.outputLinks = outputLinks;
	}

	public void addInputLink(LinkDescriptor link) {
		inputLinks.add(link);
	}

	public LinkDescriptor getInputLink(int index) {
		return inputLinks.get(index);
	}

	public Vector<LinkDescriptor> getInputLinks() {
		return inputLinks;
	}

	public void setInputLinks(Vector<LinkDescriptor> inputLinks) {
		this.inputLinks = inputLinks;
	}

}
