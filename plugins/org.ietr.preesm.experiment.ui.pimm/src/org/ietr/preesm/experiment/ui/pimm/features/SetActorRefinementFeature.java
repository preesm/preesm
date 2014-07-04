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
package org.ietr.preesm.experiment.ui.pimm.features;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.header.parser.cdt.ASTAndActorComparisonVisitor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMUtil;

/**
 * Custom Feature to set a new {@link Refinement} of an {@link Actor}
 * 
 * @author kdesnos
 * 
 */
public class SetActorRefinementFeature extends AbstractCustomFeature {

	protected boolean hasDoneChanges = false;

	/**
	 * Default Constructor of {@link SetActorRefinementFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public SetActorRefinementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Set Refinement";
	}

	@Override
	public String getDescription() {
		return "Set/Change the Refinement of an Actor";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// Allow setting if exactly one pictogram element
		// representing an Actor is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Actor) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {

		// Re-check if only one element is selected
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Actor) {
				Actor actor = (Actor) bo;

				String question = "Please select a valid file\n(.idl, .h or .pi)";
				String dialogTitle = "Select a refinement file";
				askRefinement(actor, question, dialogTitle);

				// Call the layout feature
				layoutPictogramElement(pes[0]);
			}
		}
	}

	private void askRefinement(Actor actor, String question, String dialogTitle) {
		// Ask user for Actor name until a valid name is entered.
		IPath newFilePath = PiMMUtil.askRefinement(dialogTitle, question, null);

		Refinement refinement = actor.getRefinement();
		if (newFilePath != null) {
			if (!newFilePath.equals(refinement.getFilePath())) {
				this.hasDoneChanges = true;
				// If the file is a .h header
				if (newFilePath.getFileExtension().equals("h")) {
					// We get it
					IFile file = ResourcesPlugin.getWorkspace().getRoot()
							.getFile(newFilePath);
					Set<FunctionPrototype> prototypes = getPrototypes(file,
							actor);
					if (prototypes.isEmpty()) {
						String message = "The .h file you selected does not contain any prototype corresponding to actor "
								+ actor.getName()
								+ ".\nPlease select another valid file.";
						this.askRefinement(actor, message, dialogTitle);
					} else {
						String title = "Loop Function Selection";
						String message = "Select a loop function\n(* = any string, ? = any char):";
						FunctionPrototype[] protoArray = prototypes
								.toArray(new FunctionPrototype[prototypes
										.size()]);
						FunctionPrototype loopProto = PiMMUtil.selectFunction(
								protoArray, title, message, true);

						title = "Init Function Selection";
						message = "Select an init function\n(* = any string, ? = any char):";
						FunctionPrototype initProto = PiMMUtil.selectFunction(
								protoArray, title, message, false);

						HRefinement newRefinement = PiMMFactory.eINSTANCE
								.createHRefinement();
						newRefinement.setLoopPrototype(loopProto);
						newRefinement.setInitPrototype(initProto);
						newRefinement.setFilePath(newFilePath);
						actor.setRefinement(newRefinement);
					}

				} else {
					refinement.setFilePath(newFilePath);
				}
			}
		}
	}

	private Set<FunctionPrototype> getPrototypes(IFile file, Actor actor) {
		Set<FunctionPrototype> result = new HashSet<FunctionPrototype>();

		if (file != null) {
			ICElement element = CoreModel.getDefault().create(file);
			ITranslationUnit tu = (ITranslationUnit) element;
			try {
				// Parse it
				IASTTranslationUnit ast = tu.getAST();
				ASTAndActorComparisonVisitor visitor = new ASTAndActorComparisonVisitor();
				ast.accept(visitor);
				// And extract from it the functions
				// compatible with the current actor
				result = visitor.filterPrototypesFor(actor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}
