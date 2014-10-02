package org.ietr.preesm.experiment.header.parser.cdt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.ui.FunctionPrototypeSummary;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;

public class ASTAndActorComparisonVisitor extends ASTVisitor {

	private Set<FunctionPrototype> prototypes;

	public ASTAndActorComparisonVisitor() {
		super(true);
		this.prototypes = new HashSet<FunctionPrototype>();
	}

	// The IASTTranslationUnit is the object representing the whole file (it
	// contains all the declarations)
	@Override
	public int visit(IASTTranslationUnit tu) {
		return PROCESS_CONTINUE;
	}

	// IASTSimpleDeclaration are functions declarations
	@Override
	public int visit(IASTDeclaration declaration) {
		if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) declaration;

			prototypes.add(createFunctionPrototypeFrom(simpleDecl));
		}

		return PROCESS_CONTINUE;
	}

	private FunctionPrototype createFunctionPrototypeFrom(
			IASTSimpleDeclaration simpleDecl) {
		FunctionPrototype proto = PiMMFactory.eINSTANCE
				.createFunctionPrototype();

		// FunctionPrototypeSummary get informations such as type, name and
		// arguments of a function
		FunctionPrototypeSummary summary = new FunctionPrototypeSummary(
				simpleDecl.getRawSignature());

		// Get the name of the function (we ignore its return type, since
		// it's an actor function)
		proto.setName(summary.getName());

		String argumentsString = summary.getArguments();
		// Arguments are separated by commas
		for (String argument : argumentsString.split(",")) {
			proto.getParameters().add(createFunctionParameterFrom(argument));
		}

		return proto;
	}

	private FunctionParameter createFunctionParameterFrom(String argument) {
		FunctionParameter parameter = PiMMFactory.eINSTANCE
				.createFunctionParameter();
		// Get the different segments of one argument, separated by
		// whitespaces
		String[] segments = argument.split("\\s+");
		// The name of the argument is the last segment (others are the
		// type)
		String argumentName = segments[segments.length - 1];
		if (argumentName.startsWith("*")) argumentName = argumentName.replace("*", "");
		parameter.setName(argumentName);
		// Type of the argument is the whole string minus the name
		parameter.setType(argument.replace(argumentName, ""));

		return parameter;
	}

	/**
	 * Filters the prototypes obtained from the parsed file to keep only the
	 * ones corresponding to the actor signature (ports)
	 * 
	 * @param actor
	 *            the AbstractActor which ports we use to filter prototypes
	 * @return the set of FunctionPrototypes corresponding to actor
	 */
	public Set<FunctionPrototype> filterPrototypesFor(AbstractActor actor) {
		Set<FunctionPrototype> result = new HashSet<FunctionPrototype>();

		// For each function prototype proto
		for (FunctionPrototype proto : this.prototypes) {
			// proto matches the signature of actor if:
			boolean matches = true;
			// each of its parameter matches one of the ports of actor
			for (FunctionParameter param : proto.getParameters()) {
				if (hasCorrespondingPort(param, actor.getDataInputPorts())) {
					param.setDirection(Direction.IN);
					param.setIsConfigurationParameter(false);
				} else if (hasCorrespondingPort(param,
						actor.getDataOutputPorts())) {
					param.setDirection(Direction.OUT);
					param.setIsConfigurationParameter(false);
				} else if (hasCorrespondingPort(param,
						actor.getConfigInputPorts())) {
					param.setDirection(Direction.IN);
					param.setIsConfigurationParameter(true);
				} else if (hasCorrespondingPort(param,
						actor.getConfigOutputPorts())) {
					param.setDirection(Direction.OUT);
					param.setIsConfigurationParameter(true);
				}else {
					matches = false;
					break;
				}
			}
			if (matches) {
				result.add(proto);
			}
		}

		return result;
	}

	private boolean hasCorrespondingPort(FunctionParameter f,
			List<? extends Port> ports) {
		for (Port p : ports) {
			if (p.getName().equals(f.getName())) {
				return true;
			}
		}
		return false;
	}
}
