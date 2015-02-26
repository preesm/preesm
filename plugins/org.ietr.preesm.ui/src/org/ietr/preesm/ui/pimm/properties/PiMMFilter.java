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
package org.ietr.preesm.ui.pimm.properties;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;

public class PiMMFilter extends AbstractPropertySectionFilter{

	/**
	 * Check the given pictogram element for acceptance.
	 * Returns true, if pictogram element is accepted, otherwise false.
	 */
	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);

		/**
		 * Parameter, SourceInterface, SinkInterface and Delay are EObject has associated properties window.
		 */

		//System.out.println(eObject.toString()+" Container: "+eObject.eContainer());
		
		// Parameter and ConfigInputInterface.
		if(eObject instanceof Parameter){
			if(((Parameter) eObject).isConfigurationInterface())	// is an ConfigInputInterface
				return false;
			return true;
		}
	
		// ConfigOutputPort contained in the Actor.
		if(eObject instanceof ConfigOutputPort)
			return false;
		
		// OutputPort contained in the SourceInterface and Actor
		if(eObject instanceof DataOutputPort){
			if(eObject.eContainer() instanceof DataInputInterface)
				return true;
			if(eObject.eContainer() instanceof ExecutableActor)
				return true;
		}
		
		// InputPort contained in the SinkInterface and Actor
		if(eObject instanceof DataInputPort){
			if(eObject.eContainer() instanceof DataOutputInterface)
				return true;
			if(eObject.eContainer() instanceof ExecutableActor)
				return true;
		}
		
		if(eObject instanceof DataOutputInterface)
			return true;
		
		if(eObject instanceof DataInputInterface)
			return true;
		
		if(eObject instanceof Delay)
			return true;

		
		return false;
	}

}
