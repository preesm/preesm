/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
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
package org.ietr.preesm.experiment.ui.piscenario.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.common.IIdentifiable;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.ActorTree;
import org.ietr.preesm.experiment.core.piscenario.ParameterValue;

/**
 * This class provides the elements displayed in {@link ActorTree}. Each
 * element is a {@link ActorNode}. This tree is used in scenario editor to edit
 * parameters
 * 
 * @author jheulot
 */
public class PiParameterTableContentProvider implements IStructuredContentProvider {
	/**
	 * The corresponding {@link ActorTree}
	 */
	private ActorTree actorTree = null;

	/**
	 * Default Constructor
	 */
	public PiParameterTableContentProvider() {
		super();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		actorTree = (ActorTree) newInput;
	}
	
	private void getParams(HashSet<ParameterValue> paramValues, ActorNode node){
		paramValues.addAll(node.getParamValues());
		for(ActorNode childnode : node.getChildren()){
			getParams(paramValues, childnode);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		HashSet<ParameterValue> paramValues = new HashSet<ParameterValue>();
		if(actorTree.getRoot() != null){
			getParams(paramValues, actorTree.getRoot());
		}		
		return paramValues.toArray();
	}

}
