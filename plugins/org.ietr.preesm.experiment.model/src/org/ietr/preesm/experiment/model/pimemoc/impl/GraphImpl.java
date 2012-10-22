/**
 */
package org.ietr.preesm.experiment.model.pimemoc.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimemoc.AbstractVertex;
import org.ietr.preesm.experiment.model.pimemoc.Fifo;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Graph</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl#getVertices
 * <em>Vertices</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl#getFifos
 * <em>Fifos</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class GraphImpl extends AbstractVertexImpl implements Graph {
	/**
	 * The cached value of the '{@link #getVertices() <em>Vertices</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getVertices()
	 * @generated
	 * @ordered
	 */
	protected EList<AbstractVertex> vertices;

	/**
	 * The cached value of the '{@link #getFifos() <em>Fifos</em>}' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getFifos()
	 * @generated
	 * @ordered
	 */
	protected EList<Fifo> fifos;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected GraphImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PIMeMoCPackage.Literals.GRAPH;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<AbstractVertex> getVertices() {
		if (vertices == null) {
			vertices = new EObjectContainmentEList<AbstractVertex>(
					AbstractVertex.class, this, PIMeMoCPackage.GRAPH__VERTICES);
		}
		return vertices;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<Fifo> getFifos() {
		if (fifos == null) {
			fifos = new EObjectContainmentEList<Fifo>(Fifo.class, this,
					PIMeMoCPackage.GRAPH__FIFOS);
		}
		return fifos;
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
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case PIMeMoCPackage.GRAPH__VERTICES:
			return ((InternalEList<?>) getVertices()).basicRemove(otherEnd,
					msgs);
		case PIMeMoCPackage.GRAPH__FIFOS:
			return ((InternalEList<?>) getFifos()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PIMeMoCPackage.GRAPH__VERTICES:
			return getVertices();
		case PIMeMoCPackage.GRAPH__FIFOS:
			return getFifos();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case PIMeMoCPackage.GRAPH__VERTICES:
			getVertices().clear();
			getVertices().addAll(
					(Collection<? extends AbstractVertex>) newValue);
			return;
		case PIMeMoCPackage.GRAPH__FIFOS:
			getFifos().clear();
			getFifos().addAll((Collection<? extends Fifo>) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case PIMeMoCPackage.GRAPH__VERTICES:
			getVertices().clear();
			return;
		case PIMeMoCPackage.GRAPH__FIFOS:
			getFifos().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case PIMeMoCPackage.GRAPH__VERTICES:
			return vertices != null && !vertices.isEmpty();
		case PIMeMoCPackage.GRAPH__FIFOS:
			return fifos != null && !fifos.isEmpty();
		}
		return super.eIsSet(featureID);
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

} // GraphImpl
