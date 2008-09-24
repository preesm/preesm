package org.ietr.preesm.plugin.fpga_scheduler.descriptor;

import java.util.Vector;

public class RouteDescriptor {

	private Vector<LinkDescriptor> linkList;

	public RouteDescriptor() {
		linkList = new Vector<LinkDescriptor>();
	}

	public void addLink(LinkDescriptor link) {
		linkList.add(link);
	}

	public void addLink(int index, LinkDescriptor link) {
		linkList.add(index, link);
	}

	public LinkDescriptor getLink(int index) {
		return linkList.get(index);
	}

	public Vector<LinkDescriptor> getLinkList() {
		return linkList;
	}

	public LinkDescriptor getFirstLink() {
		return linkList.firstElement();
	}

	public LinkDescriptor getLastLink() {
		return linkList.lastElement();
	}

}
