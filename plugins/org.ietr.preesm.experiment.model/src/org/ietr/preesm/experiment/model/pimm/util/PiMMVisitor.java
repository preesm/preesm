/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.util;

import org.ietr.preesm.experiment.model.pimm.*;

public abstract class PiMMVisitor {
	public void visit(PiMMVisitable v) {
		v.accept(this);
	}

	public abstract void visitAbstractActor(AbstractActor aa);

	public abstract void visitAbstractVertex(AbstractVertex av);

	public abstract void visitActor(Actor a);

	public abstract void visitConfigInputInterface(ConfigInputInterface cii);

	public abstract void visitConfigInputPort(ConfigInputPort cip);
	
	public abstract void visitConfigOutputInterface(ConfigOutputInterface coi);

	public abstract void visitConfigOutputPort(ConfigOutputPort cop);

	public abstract void visitDataInputInterface(DataInputInterface dii);

	public abstract void visitDataInputPort(DataInputPort dip);

	public abstract void visitDataOutputInterface(DataOutputInterface doi);
	
	public abstract void visitDataOutputPort(DataOutputPort dop);
	
	public abstract void visitDelay(Delay d);
	
	public abstract void visitDependency(Dependency d);
	
	public abstract void visitExpression(Expression e);
	
	public abstract void visitFifo(Fifo f);
	
	public abstract void visitInterfaceActor(InterfaceActor ia);
	
	public abstract void visitISetter(ISetter is);
	
	public abstract void visitParameter(Parameter p);
	
	public abstract void visitParameterizable(Parameterizable p);
	
	public abstract void visitPiGraph(PiGraph pg);
	
	public abstract void visitPort(Port p);
	
	public abstract void visitRefinement(Refinement r);
}
