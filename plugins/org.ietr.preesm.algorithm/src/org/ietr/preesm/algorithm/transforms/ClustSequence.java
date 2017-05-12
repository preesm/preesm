package org.ietr.preesm.algorithm.transforms;

import java.util.ArrayList;
import java.util.List;

public class ClustSequence extends AbstractClust {
	
	private List <AbstractClust> seq = new ArrayList<AbstractClust>();

	public List<AbstractClust> getSeq() {
		return seq;
	}

	public void setSeq(List<AbstractClust> seq) {
		this.seq = seq;
	}
}
