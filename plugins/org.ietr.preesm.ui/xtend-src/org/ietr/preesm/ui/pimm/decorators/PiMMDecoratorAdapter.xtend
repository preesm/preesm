/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.ui.pimm.decorators;

import java.util.Map
import org.eclipse.emf.common.notify.Notification
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.util.EContentAdapter
import org.eclipse.graphiti.dt.IDiagramTypeProvider
import org.eclipse.graphiti.mm.pictograms.PictogramElement
import org.eclipse.graphiti.tb.IDecorator
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * This class manages the list of IDecorator associated to the 
 * PictogramElement of a graph.</br>
 * The purpose of this class is to listen for changes of the PiGraph targeted
 * by the current Editor. Each time something is changed in the graph, the
 * PiMMDecoratorAdapter clears the map of decorators associated to the 
 * PictogramElement of the graph in order to force their update. Otherwise,
 * pre-computed IDecorator stored in the Map are used when required by the
 * editor.
 * 
 * @author kdesnos
 */
@SuppressWarnings("unchecked")
public class PiMMDecoratorAdapter extends EContentAdapter {

	@Accessors
	Map<PictogramElement, IDecorator[]> pesAndDecorators

	new(IDiagramTypeProvider diagramTypeProvider) {
		pesAndDecorators = newHashMap
	}

	/** 
	 * Is called each time a change is made in the PiGraph
	 */
	override notifyChanged(Notification notification) {

		switch (notification.getFeatureID(typeof(Resource))) {
			case Notification.SET,
			case Notification.ADD,
			case Notification.REMOVE,
			case Notification.UNSET,
			case 0: { // Notification.CREATE: { // equivalent, but without deprecation warning
				checkGraphChanges(notification)
			}
		}
		super.notifyChanged(notification)
	}

	def void checkGraphChanges(Notification notification) {
		pesAndDecorators.clear
	// TODO If computations become slow, a switch could be used to 
	// activate / deactivate decorator computations depending on the 
	// notification notifier.
	}
}
