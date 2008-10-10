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
