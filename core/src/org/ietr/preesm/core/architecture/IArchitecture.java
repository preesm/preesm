/**
 * 
 */
package org.ietr.preesm.core.architecture;

import java.util.Set;

/**
 * This interface provides methods to manipulate an architecture.
 * 
 * @author Matthieu Wipliez
 */
public interface IArchitecture {

	public IArchitecture clone();

	/**
	 * Returns the main medium definition. This definition is used by kwok
	 * homogeneous architecture algorithm to pre-study the implementation
	 */
	public Medium getMainMedium();

	/**
	 * Returns the main operator. This definition is used by kwok homogeneous
	 * architecture algorithm to pre-study the implementation
	 */
	public Operator getMainOperator();

	/**
	 * Returns all the media
	 * 
	 * @return
	 */
	public Set<Medium> getMedia();

	/**
	 * Returns the operator of the given id
	 * 
	 * @param op1
	 * @param op2
	 * @return
	 */
	public Set<Medium> getMedia(Operator op1, Operator op2);

	public Medium getMedium(String name);

	public MediumDefinition getMediumDefinition(String id);

	/**
	 * Returns the number of operators.
	 * 
	 * @return The number of operators.
	 */
	public int getNumberOfOperators();

	/**
	 * Returns the operator of the given id
	 * 
	 * @param id
	 * @return
	 */
	public Operator getOperator(String id);

	/**
	 * Returns the operator definition with the given id
	 * 
	 * @param id
	 * @return
	 */
	public OperatorDefinition getOperatorDefinition(String id);

	/**
	 * Returns all the operators
	 * 
	 * @return
	 */
	public Set<Operator> getOperators();

	/**
	 * Returns the operators of the given type
	 * 
	 * @param def
	 * @return
	 */
	public Set<Operator> getOperators(OperatorDefinition def);
}
