/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clement Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
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
package org.ietr.preesm.ui.pimm.util;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * This class contains methods that can be usefull when manipulating
 * {@link Diagram} together with associated {@link PiGraph}.
 * 
 * @author kdesnos
 *
 */
public class DiagramPiGraphLinkHelper {

	/**
	 * Clear all the bendpoints of the {@link Fifo} and {@link Dependency} in
	 * the diagram passed as a parameter.
	 * 
	 * @param diagram
	 */
	public static void clearBendpoints(Diagram diagram) {
		for (Connection connection : diagram.getConnections()) {
			((FreeFormConnection) connection).getBendpoints().clear();
		}
	}

	/**
	 * Retrieve the {@link PictogramElement} of the {@link Diagram}
	 * corresponding to the given {@link AbstractActor}.
	 * 
	 * @param diagram
	 *            the {@link Diagram} containing the {@link PictogramElement}
	 * @param actor
	 *            the {@link AbstractActor} whose {@link PictogramElement} is
	 *            searched.
	 * @return the {@link PictogramElement} of the {@link AbstractActor}.
	 * @throws RuntimeException
	 *             if no {@link PictogramElement} could be found in this
	 *             {@link Diagram} for this {@link AbstractActor}.
	 */
	public static PictogramElement getActorPE(Diagram diagram, AbstractActor actor)
			throws RuntimeException {
		// Get the PE
		List<PictogramElement> pes = Graphiti.getLinkService()
				.getPictogramElements(diagram, actor);
		PictogramElement actorPE = null;
		for (PictogramElement pe : pes) {
			if (pe instanceof ContainerShape) {
				actorPE = pe;
				break;
			}
		}
	
		if (actorPE == null) {
			throw new RuntimeException("No PE was found for actor :"
					+ actor.getName());
		}
		return actorPE;
	}

	/**
	 * Retrieve the {@link PictogramElement} of the {@link Diagram}
	 * corresponding to the given {@link Fifo}.
	 * 
	 * @param diagram
	 *            the {@link Diagram} containing the {@link PictogramElement}
	 * @param fifo
	 *            the {@link Fifo} whose {@link PictogramElement} is searched.
	 * @return the {@link PictogramElement} of the {@link Fifo}.
	 * @throws RuntimeException
	 *             if no {@link PictogramElement} could be found in this
	 *             {@link Diagram} for this {@link Fifo}.
	 */
	public static ContainerShape getDelayPE(Diagram diagram, Fifo fifo)
			throws RuntimeException {
		// Get all delays with identical attributes (may not be the
		// right delay is several delays have the same properties.)
		List<PictogramElement> pes = Graphiti.getLinkService()
				.getPictogramElements(diagram, fifo.getDelay());
		PictogramElement pe = null;
		for (PictogramElement p : pes) {
			if (p instanceof ContainerShape
					&& Graphiti.getLinkService()
							.getBusinessObjectForLinkedPictogramElement(p) == fifo
							.getDelay()) {
				pe = p;
			}
		}
		// if PE is still null.. something is deeply wrong with this
		// graph !
		if (pe == null) {
			throw new RuntimeException(
					"Pictogram element associated to delay of Fifo "
							+ fifo.getId() + " could not be found.");
		}
		return (ContainerShape) pe;
	}

	/**
	 * Get the {@link FreeFormConnection} associated to an edge of the
	 * {@link Diagram}. The Edge can either be a {@link Fifo} or a
	 * {@link Dependency}.
	 * 
	 * @param diagram
	 *            the {@link Diagram} containing the edge.
	 * @param edge
	 *            the {@link Fifo} or the {@link Dependency} whose
	 *            {@link FreeFormConnection} is searched.
	 * @return the searched {@link FreeFormConnection}.
	 * @throws RuntimeException
	 *             if not {@link FreeFormConnection} could be found, a
	 *             {@link RuntimeException} is thrown
	 */
	public static FreeFormConnection getFreeFormConnectionOfEdge(
			Diagram diagram, EObject edge) throws RuntimeException {
		List<PictogramElement> pes = Graphiti.getLinkService()
				.getPictogramElements(diagram, edge);
		FreeFormConnection ffc = null;
		for (PictogramElement pe : pes) {
			if (Graphiti.getLinkService()
					.getBusinessObjectForLinkedPictogramElement(pe) == edge
					&& pe instanceof FreeFormConnection) {
				ffc = (FreeFormConnection) pe;
			}
		}
	
		// if PE is still null.. something is deeply wrong with this
		// graph !
		if (ffc == null) {
			throw new RuntimeException("Pictogram element associated Edge "
					+ edge + " could not be found.");
		}
		return ffc;
	}

}
