package org.ietr.preesm.core.architecture;

/**
 * The operator definition specifies the operator characteristics
 *         
 * @author mpelcat
 */
public class OperatorDefinition extends ArchitectureComponentDefinition {

	public OperatorDefinition(String id) {
		super(id, "operator");
	}

	public OperatorDefinition clone(IArchitecture archi) {

		// A new OperatorDefinition is created with same id
		OperatorDefinition newdef = new OperatorDefinition(this.getId());

		return newdef;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OperatorDefinition) {
			OperatorDefinition opdef = (OperatorDefinition) obj;
			return this.getId().compareToIgnoreCase(opdef.getId()) == 0;
		}

		return false;
	}
}
