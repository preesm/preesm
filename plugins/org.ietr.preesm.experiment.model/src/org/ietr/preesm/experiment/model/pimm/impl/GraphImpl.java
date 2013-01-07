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
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.adapter.InterfaceObserver;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Graph</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getVertices <em>Vertices</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getFifos <em>Fifos</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GraphImpl extends AbstractActorImpl implements Graph {
	/**
	 * The cached value of the '{@link #getVertices() <em>Vertices</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getVertices()
	 * @generated
	 * @ordered
	 */
	protected EList<AbstractActor> vertices;

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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> parameters;

	/**
	 * The cached value of the '{@link #getDependencies() <em>Dependencies</em>}
	 * ' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getDependencies()
	 * @generated
	 * @ordered
	 */
	protected EList<Dependency> dependencies;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 */
	protected GraphImpl() {
		super();
		this.eAdapters().add(new InterfaceObserver());
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
			case PiMMPackage.GRAPH__DEPENDENCIES:
				return getDependencies();
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
			case PiMMPackage.GRAPH__DEPENDENCIES:
				return ((InternalEList<?>)getDependencies()).basicRemove(otherEnd, msgs);
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
			case PiMMPackage.GRAPH__DEPENDENCIES:
				return dependencies != null && !dependencies.isEmpty();
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
				getVertices().addAll((Collection<? extends AbstractActor>)newValue);
				return;
			case PiMMPackage.GRAPH__FIFOS:
				getFifos().clear();
				getFifos().addAll((Collection<? extends Fifo>)newValue);
				return;
			case PiMMPackage.GRAPH__PARAMETERS:
				getParameters().clear();
				getParameters().addAll((Collection<? extends Parameter>)newValue);
				return;
			case PiMMPackage.GRAPH__DEPENDENCIES:
				getDependencies().clear();
				getDependencies().addAll((Collection<? extends Dependency>)newValue);
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
			case PiMMPackage.GRAPH__DEPENDENCIES:
				getDependencies().clear();
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Parameter> getParameters() {
		if (parameters == null) {
			parameters = new EObjectContainmentEList<Parameter>(Parameter.class, this, PiMMPackage.GRAPH__PARAMETERS);
		}
		return parameters;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Dependency> getDependencies() {
		if (dependencies == null) {
			dependencies = new EObjectContainmentEList<Dependency>(Dependency.class, this, PiMMPackage.GRAPH__DEPENDENCIES);
		}
		return dependencies;
	}

	@Override
	public AbstractVertex getVertexNamed(String name) {
		for (AbstractVertex vert : vertices) {
			if (vert.getName().equals(name)) {
				return vert;
			}
		}

		for (AbstractVertex vert : parameters) {
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
	public EList<AbstractActor> getVertices() {
		if (vertices == null) {
			vertices = new EObjectContainmentEList<AbstractActor>(AbstractActor.class, this, PiMMPackage.GRAPH__VERTICES);
		}
		return vertices;
	}

	@Override
	public Set<String> getVerticesNames() {
		Set<String> names = new HashSet<String>(getVertices().size());
		for (AbstractActor vertex : this.getVertices()) {
			names.add(vertex.getName());
		}
		return names;
	}

	@Override
	public Set<String> getParametersNames() {
		Set<String> names = new HashSet<String>(getVertices().size());
		for (Parameter param : this.getParameters()) {
			names.add(param.getName());
		}
		return names;
	}

	@Override
	public Fifo getFifoIded(String id) {
		for (Fifo fifo : this.getFifos()) {
			if (fifo.getId().equals(id)) {
				return fifo;
			}
		}
		return null;
	}

} // GraphImpl
