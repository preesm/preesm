package org.ietr.preesm.pimm.header.parser.cdt;

import java.util.ArrayList;
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
			if(!argument.isEmpty()){
				proto.getParameters().add(createFunctionParameterFrom(argument));
			}
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
		// Separate parameters from data 
		if (argumentName.startsWith("*") || (segments.length > 1 &&  segments[segments.length - 2].endsWith("*"))) {
			// Following lines is useless if * is stuck to the end of the data 
			// type as in "char* param" (but not as in char *param)
			argumentName = argumentName.replace("*", ""); 
		} else {
			parameter.setIsConfigurationParameter(true);
		}
		parameter.setName(argumentName);
		// Type of the argument is the whole string minus the name
		parameter.setType(argument.replace(argumentName, ""));
		
		// If the argument declaration contains a direction, use it !
		for(String s:segments){				
			if(s.equals("IN")){
				parameter.setDirection(Direction.IN);
				break;
			}
			if(s.equals("OUT")){
				parameter.setDirection(Direction.OUT);
			}
		}

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
	public Set<FunctionPrototype> filterLoopPrototypesFor(AbstractActor actor) {
		Set<FunctionPrototype> result = new HashSet<FunctionPrototype>();

		// For each function prototype proto
		for (FunctionPrototype proto : this.prototypes) {
			// proto matches the signature of actor if:
			// -it does not have more parameters than the actors ports
			Set<FunctionParameter> params = new HashSet<FunctionParameter>(
					proto.getParameters());
			boolean matches = params.size() <= (actor.getDataInputPorts()
					.size()
					+ actor.getDataOutputPorts().size()
					+ actor.getConfigInputPorts().size() + actor
					.getConfigOutputPorts().size());

			// Check that all proto parameters can be matched with a port
			List<Port> allPorts = new ArrayList<Port>();
			allPorts.addAll(actor.getDataInputPorts());
			allPorts.addAll(actor.getDataOutputPorts());
			allPorts.addAll(actor.getConfigInputPorts());
			allPorts.addAll(actor.getConfigOutputPorts());
			for(FunctionParameter param : proto.getParameters()){
				matches &= hasCorrespondingPort(param, allPorts);
			}
			
			// -each of the data input and output ports of the actor matches one
			// of the parameters of proto
			if (matches) {
				for (Port p : actor.getDataInputPorts()) {
					FunctionParameter param = getCorrespondingFunctionParameter(
							p, params);
					if (param != null) {
						param.setDirection(Direction.IN);
						param.setIsConfigurationParameter(false);
						params.remove(param);
					} else {
						matches = false;
						break;
					}
				}
			}
			if (matches) {
				for (Port p : actor.getDataOutputPorts()) {
					FunctionParameter param = getCorrespondingFunctionParameter(
							p, params);
					if (param != null) {
						param.setDirection(Direction.OUT);
						param.setIsConfigurationParameter(false);
						params.remove(param);
					} else {
						matches = false;
						break;
					}
				}
			}
			// -each of the configuration output ports of the actor matches one
			// of the parameters of proto
			if (matches) {
				for (Port p : actor.getConfigOutputPorts()) {
					FunctionParameter param = getCorrespondingFunctionParameter(
							p, params);
					if (param != null) {
						param.setDirection(Direction.OUT);
						param.setIsConfigurationParameter(true);
						params.remove(param);
					} else {
						matches = false;
						break;
					}
				}
			}
			// -all other function parameters of proto match a configuration
			// input port of the actor
			if (matches) {
				for (FunctionParameter param : params) {
					if (hasCorrespondingPort(param, actor.getConfigInputPorts())) {
						param.setDirection(Direction.IN);
						param.setIsConfigurationParameter(true);
					}
				}
			}
			if (matches) {
				result.add(proto);
			}
		}

		return result;
	}

	/**
	 * Filters the prototypes obtained from the parsed file to keep only the
	 * ones corresponding to the actor possible initialization
	 * 
	 * @param actor
	 *            the AbstractActor which ports we use to filter prototypes
	 * @return the set of FunctionPrototypes corresponding to actor
	 *         initialization
	 */
	public Set<FunctionPrototype> filterInitPrototypesFor(AbstractActor actor) {
		Set<FunctionPrototype> result = new HashSet<FunctionPrototype>();

		// For each function prototype proto
		for (FunctionPrototype proto : this.prototypes) {
			// proto matches the initialization of actor if:
			// -it does not have more parameters than the actors configuration
			// input ports
			Set<FunctionParameter> params = new HashSet<FunctionParameter>(
					proto.getParameters());
			boolean matches = params.size() <= actor.getConfigInputPorts()
					.size();
			// -all function parameters of proto match a configuration input
			// port of the actor (initialization function cannot read or write
			// in fifo nor write on configuration output ports)
			if (matches) {
				for (FunctionParameter param : params) {
					if (hasCorrespondingPort(param, actor.getConfigInputPorts())) {
						param.setDirection(Direction.IN);
						param.setIsConfigurationParameter(true);
					} else {
						matches = false;
						break;
					}
				}
			}

			if (matches) {
				result.add(proto);
			}
		}

		return result;
	}

	/**
	 * Filters the prototypes obtained from the parsed file to keep only the
	 * ones corresponding to possible initializations.
	 * 
	 * @return the set of FunctionPrototypes corresponding to initialization
	 */
	public Set<FunctionPrototype> filterInitPrototypes() {
		Set<FunctionPrototype> result = new HashSet<FunctionPrototype>();

		// For each function prototype proto check that the prototype has no
		// input or output buffers (i.e. parameters with a pointer type)
		for (FunctionPrototype proto : this.prototypes) {
			Set<FunctionParameter> params = new HashSet<FunctionParameter>(
					proto.getParameters());
			boolean allParams = true;
			for (FunctionParameter param : params) {
				if (!param.getType().contains("*")) {
					param.setDirection(Direction.IN);
					param.setIsConfigurationParameter(true);
				} else {
					allParams = false;
					break;
				}
			}

			if (allParams) {
				result.add(proto);
			}
		}

		return result;
	}

	private FunctionParameter getCorrespondingFunctionParameter(Port p,
			Set<FunctionParameter> params) {
		for (FunctionParameter param : params) {
			if (p.getName().equals(param.getName()))
				return param;
		}
		return null;
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

	/**
	 * Returns an unfiltered list of the {@link FunctionPrototype} found by the
	 * {@link ASTAndActorComparisonVisitor}.
	 * 
	 * @return The parsed {@link FunctionPrototype}.
	 */
	public Set<FunctionPrototype> getPrototypes() {
		return new HashSet<FunctionPrototype>(this.prototypes);
	}
}
