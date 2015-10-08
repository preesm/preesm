package org.ietr.preesm.ui.pimm.decorators;

import java.util.Map
import org.eclipse.emf.common.notify.Notification
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.util.EContentAdapter
import org.eclipse.graphiti.dt.IDiagramTypeProvider
import org.eclipse.graphiti.mm.pictograms.PictogramElement
import org.eclipse.graphiti.tb.IDecorator
import org.eclipse.xtend.lib.annotations.Accessors

public class PiMMDecoratorAdapter extends EContentAdapter {

	/**
	 * DiagramTypeProvider is used to retrieve the PictogramElement associated 
	 * to the elements of the PiGraph.
	 */
	IDiagramTypeProvider diagramTypeProvider

	@Accessors
	Map<PictogramElement, IDecorator[]> pesAndDecorators

	new(IDiagramTypeProvider diagramTypeProvider) {
		this.diagramTypeProvider = diagramTypeProvider;
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

	def checkGraphChanges(Notification notification) {
		pesAndDecorators.clear
	// TODO If computations become slow, a switch could be used to 
	// activate / deactivate decorator computations depending on the 
	// notification notifier.
	}
}