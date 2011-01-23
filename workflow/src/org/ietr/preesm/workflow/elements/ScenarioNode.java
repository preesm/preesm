/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.workflow.elements;

import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.ietr.preesm.workflow.implement.AbstractScenarioImplementation;
import org.ietr.preesm.workflow.tools.WorkflowLogger;

/**
 * This class provides a scenario workflow node.
 * 
 * @author mpelcat
 */
public class ScenarioNode extends AbstractWorkflowNode {

	/**
	 * The identifier of this scenario node. It is needed to retrieve the
	 * implementation of this node
	 */
	private String scenarioId = null;

	public ScenarioNode(String scenarioId) {
		super();
		this.scenarioId = scenarioId;
	}

	public String getScenarioId() {
		return scenarioId;
	}

	public AbstractScenarioImplementation getScenario() {
		return (AbstractScenarioImplementation) implementation;
	}

	@Override
	public boolean isScenarioNode() {
		return true;
	}

	@Override
	public boolean isTaskNode() {
		return false;
	}

	/**
	 * Initializes the outputs types of the scenario using information from the
	 * plugin extension.
	 * 
	 * @return True if the prototype was correctly set.
	 */
	private boolean initPrototype(AbstractScenarioImplementation scenario,
			IConfigurationElement element) {

		for (IConfigurationElement child : element.getChildren()) {
			if (child.getName().equals("outputs")) {
				for (IConfigurationElement output : child.getChildren()) {
					scenario.addOutput(output.getAttribute("id"),
							output.getAttribute("object"));
				}
			}
		}
		return true;
	}

	/**
	 * Checks if this scenario exists based on its ID.
	 * 
	 * @return True if this scenario exists, false otherwise.
	 */
	public boolean getExtensionInformation() {
		try {
			IExtensionRegistry registry = Platform.getExtensionRegistry();

			IConfigurationElement[] elements = registry
					.getConfigurationElementsFor("org.ietr.preesm.workflow.scenarios");
			for (int i = 0; i < elements.length; i++) {
				IConfigurationElement element = elements[i];
				if (element.getAttribute("id").equals(scenarioId)) {
					// Tries to create the transformation
					Object obj = element.createExecutableExtension("type");

					// and checks it actually is an ITransformation.
					if (obj instanceof AbstractScenarioImplementation) {
						implementation = (AbstractScenarioImplementation) obj;

						// Initializes the prototype of the scenario
						initPrototype(
								(AbstractScenarioImplementation) implementation,
								element);
						return true;
					}
				}
			}

			return false;
		} catch (CoreException e) {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"Failed to find the scenario from workflow");
			return false;
		}
	}
}
