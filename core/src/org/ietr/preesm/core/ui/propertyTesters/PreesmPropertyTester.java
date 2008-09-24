package org.ietr.preesm.core.ui.propertyTesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * This class tests the hasNature and isWorkflow property.
 * 
 * @author Matthieu Wipliez
 */
public class PreesmPropertyTester extends PropertyTester {

	public PreesmPropertyTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (property.equals("hasNature")) {
			if (receiver instanceof IFile && expectedValue instanceof String) {
				IFile file = (IFile) receiver;
				String nature = (String) expectedValue;
				try {
					if (file.getProject().getNature(nature) != null) {
						return true;
					}
				} catch (CoreException e) {
					e.printStackTrace();
					return false;
				}
			}
		} else if (property.equals("isWorkflow")) {
			if (receiver instanceof IFile) {
				IFile file = (IFile) receiver;
				return (file.getFileExtension().equals("workflow"));
			}
		}
		return false;
	}

}
