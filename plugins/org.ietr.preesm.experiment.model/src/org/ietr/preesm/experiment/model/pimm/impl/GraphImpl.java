/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceVertex;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Graph</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getVertices <em>Vertices</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getFifos <em>Fifos</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GraphImpl extends AbstractVertexImpl implements Graph {
	/**
	 * The cached value of the '{@link #getVertices() <em>Vertices</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getVertices()
	 * @generated
	 * @ordered
	 */
	protected EList<AbstractVertex> vertices;

	/**
	 * The cached value of the '{@link #getFifos() <em>Fifos</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getFifos()
	 * @generated
	 * @ordered
	 */
	protected EList<Fifo> fifos;

	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> parameters;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected GraphImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 */
	public boolean addInterfaceVertex(InterfaceVertex interfaceVertex) {
		Port port;
		switch (interfaceVertex.getKind()) {
		case "src":
			port = PiMMFactory.eINSTANCE.createInputPort();
			this.getInputPorts().add((InputPort) port);
			break;
		case "snk":
			port = PiMMFactory.eINSTANCE.createOutputPort();
			this.getOutputPorts().add((OutputPort) port);
			break;
		default:
			return false;
		}

		// Set the sourceInterface properties
		port.setName(interfaceVertex.getName());
		interfaceVertex.setGraphPort(port);

		// Add the actor to the parsed graph
		this.getVertices().add(interfaceVertex);
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.GRAPH__VERTICES:
				return getVertices();
			case PiMMPackage.GRAPH__FIFOS:
				return getFifos();
			case PiMMPackage.GRAPH__PARAMETERS:
				return getParameters();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.GRAPH__VERTICES:
				return ((InternalEList<?>)getVertices()).basicRemove(otherEnd, msgs);
			case PiMMPackage.GRAPH__FIFOS:
				return ((InternalEList<?>)getFifos()).basicRemove(otherEnd, msgs);
			case PiMMPackage.GRAPH__PARAMETERS:
				return ((InternalEList<?>)getParameters()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PiMMPackage.GRAPH__VERTICES:
				return vertices != null && !vertices.isEmpty();
			case PiMMPackage.GRAPH__FIFOS:
				return fifos != null && !fifos.isEmpty();
			case PiMMPackage.GRAPH__PARAMETERS:
				return parameters != null && !parameters.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PiMMPackage.GRAPH__VERTICES:
				getVertices().clear();
				getVertices().addAll((Collection<? extends AbstractVertex>)newValue);
				return;
			case PiMMPackage.GRAPH__FIFOS:
				getFifos().clear();
				getFifos().addAll((Collection<? extends Fifo>)newValue);
				return;
			case PiMMPackage.GRAPH__PARAMETERS:
				getParameters().clear();
				getParameters().addAll((Collection<? extends Parameter>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.GRAPH;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PiMMPackage.GRAPH__VERTICES:
				getVertices().clear();
				return;
			case PiMMPackage.GRAPH__FIFOS:
				getFifos().clear();
				return;
			case PiMMPackage.GRAPH__PARAMETERS:
				getParameters().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Fifo> getFifos() {
		if (fifos == null) {
			fifos = new EObjectContainmentEList<Fifo>(Fifo.class, this, PiMMPackage.GRAPH__FIFOS);
		}
		return fifos;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Parameter> getParameters() {
		if (parameters == null) {
			parameters = new EObjectContainmentEList<Parameter>(Parameter.class, this, PiMMPackage.GRAPH__PARAMETERS);
		}
		return parameters;
	}

	@Override
	public AbstractVertex getVertexNamed(String name) {
		for (AbstractVertex vert : vertices) {
			if (vert.getName().equals(name)) {
				return vert;
			}
		}

		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<AbstractVertex> getVertices() {
		if (vertices == null) {
			vertices = new EObjectContainmentEList<AbstractVertex>(AbstractVertex.class, this, PiMMPackage.GRAPH__VERTICES);
		}
		return vertices;
	}

	@Override
	public Set<String> getVerticesNames() {
		Set<String> names = new HashSet<String>(getVertices().size());
		for (AbstractVertex vertex : vertices) {
			names.add(vertex.getName());
		}
		return names;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	public boolean removeInterfaceVertex(InterfaceVertex interfaceVertex) {
		this.getVertices().remove(interfaceVertex);
		this.getInputPorts().remove(interfaceVertex.getGraphPort());
		this.getOutputPorts().remove(interfaceVertex.getGraphPort());
		return true;

	}

} // GraphImpl
