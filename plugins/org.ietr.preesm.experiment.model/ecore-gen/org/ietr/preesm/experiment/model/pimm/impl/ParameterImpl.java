/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
 * Romina Racca <romina.racca@gmail.com> (2013)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.DependencyCycleDetector;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Parameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getOutgoingDependencies <em>Outgoing Dependencies</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#isConfigurationInterface <em>Configuration Interface</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getGraphPort <em>Graph Port</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getExpression <em>Expression</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParameterImpl extends AbstractVertexImpl implements Parameter {
  /**
   * The cached value of the '{@link #getOutgoingDependencies() <em>Outgoing Dependencies</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getOutgoingDependencies()
   * @generated
   * @ordered
   */
  protected EList<Dependency> outgoingDependencies;

  /** The Constant LOCALLY_STATIC_EDEFAULT. */
  protected static final boolean LOCALLY_STATIC_EDEFAULT = false;

  /**
   * The default value of the '{@link #isConfigurationInterface() <em>Configuration Interface</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isConfigurationInterface()
   * @generated
   * @ordered
   */
  protected static final boolean CONFIGURATION_INTERFACE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isConfigurationInterface() <em>Configuration Interface</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isConfigurationInterface()
   * @generated
   * @ordered
   */
  protected boolean configurationInterface = ParameterImpl.CONFIGURATION_INTERFACE_EDEFAULT;

  /**
   * The cached value of the '{@link #getGraphPort() <em>Graph Port</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getGraphPort()
   * @generated
   * @ordered
   */
  protected ConfigInputPort graphPort;

  /**
   * The cached value of the '{@link #getExpression() <em>Expression</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getExpression()
   * @generated
   * @ordered
   */
  protected Expression expression;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   */
  protected ParameterImpl() {
    super();
    setExpression(PiMMFactory.eINSTANCE.createExpression());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.PARAMETER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the outgoing dependencies
   * @generated
   */
  @Override
  public EList<Dependency> getOutgoingDependencies() {
    if (this.outgoingDependencies == null) {
      this.outgoingDependencies = new EObjectWithInverseResolvingEList<>(Dependency.class, this, PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES,
          PiMMPackage.DEPENDENCY__SETTER);
    }
    return this.outgoingDependencies;
  }

  /**
   * <!-- begin-user-doc --> Check whether the {@link Parameter} is a locally static {@link Parameter} or a dynamically configurable {@link Parameter}.<br>
   * <br>
   * A {@link Parameter} is locally static if its value only depends on locally static {@link Parameter}s. If the value of a {@link Parameter} depends on a
   * {@link Actor#isConfigurationActor() configuration actor} or a configurable {@link Parameter}, the {@link Parameter} becomes a configurable
   * {@link Parameter}. <br>
   * <br>
   * <b>This method should only be called on an acyclic {@link Dependency} tree otherwise the call will result in an infinite loop. Use
   * {@link DependencyCycleDetector} to check that the {@link Dependency} tree is acyclic.</b>
   *
   * @return <code>true</code> if the {@link Parameter} is locally static, <code>false</code> if the {@link Parameter} is configurable. <!-- end-user-doc -->
   *
   */
  @Override
  public boolean isLocallyStatic() {

    // Retrieve all incoming dependencies
    final List<ConfigInputPort> ports = getConfigInputPorts();
    for (final ConfigInputPort port : ports) {
      if (port.getIncomingDependency() != null) {
        // For each dependency, check if the setter is configurable or
        // an actor
        final ISetter setter = port.getIncomingDependency().getSetter();
        if (setter instanceof ConfigOutputPort) {
          // The setter is an actor, the parameter is configurable
          return false;
        }

        if ((setter instanceof Parameter) && !((Parameter) setter).isLocallyStatic()) {
          // The setter is a configurable parameter
          return false;
        }

        if (!(setter instanceof ConfigOutputPort) && !(setter instanceof Parameter)) {
          return false;
        }
      }
    }

    // If this code is reached, the parameter is locally static
    return true;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is dependent
   * @generated NOT
   */
  @Override
  public boolean isDependent() {
    return !getConfigInputPorts().isEmpty();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is configuration interface
   * @generated
   */
  @Override
  public boolean isConfigurationInterface() {
    return this.configurationInterface;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newConfigurationInterface
   *          the new configuration interface
   * @generated
   */
  @Override
  public void setConfigurationInterface(final boolean newConfigurationInterface) {
    final boolean oldConfigurationInterface = this.configurationInterface;
    this.configurationInterface = newConfigurationInterface;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE, oldConfigurationInterface,
          this.configurationInterface));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the graph port
   * @generated
   */
  @Override
  public ConfigInputPort getGraphPort() {
    if ((this.graphPort != null) && this.graphPort.eIsProxy()) {
      final InternalEObject oldGraphPort = (InternalEObject) this.graphPort;
      this.graphPort = (ConfigInputPort) eResolveProxy(oldGraphPort);
      if (this.graphPort != oldGraphPort) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.PARAMETER__GRAPH_PORT, oldGraphPort, this.graphPort));
        }
      }
    }
    return this.graphPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the config input port
   * @generated
   */
  public ConfigInputPort basicGetGraphPort() {
    return this.graphPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newGraphPort
   *          the new graph port
   * @generated
   */
  @Override
  public void setGraphPort(final ConfigInputPort newGraphPort) {
    final ConfigInputPort oldGraphPort = this.graphPort;
    this.graphPort = newGraphPort;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__GRAPH_PORT, oldGraphPort, this.graphPort));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the expression
   * @generated
   */
  @Override
  public Expression getExpression() {
    return this.expression;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newExpression
   *          the new expression
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetExpression(final Expression newExpression, NotificationChain msgs) {
    final Expression oldExpression = this.expression;
    this.expression = newExpression;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__EXPRESSION, oldExpression, newExpression);
      if (msgs == null) {
        msgs = notification;
      } else {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newExpression
   *          the new expression
   * @generated
   */
  @Override
  public void setExpression(final Expression newExpression) {
    if (newExpression != this.expression) {
      NotificationChain msgs = null;
      if (this.expression != null) {
        msgs = ((InternalEObject) this.expression).eInverseRemove(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.PARAMETER__EXPRESSION, null, msgs);
      }
      if (newExpression != null) {
        msgs = ((InternalEObject) newExpression).eInverseAdd(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.PARAMETER__EXPRESSION, null, msgs);
      }
      msgs = basicSetExpression(newExpression, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__EXPRESSION, newExpression, newExpression));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param otherEnd
   *          the other end
   * @param featureID
   *          the feature ID
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getOutgoingDependencies()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param otherEnd
   *          the other end
   * @param featureID
   *          the feature ID
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return ((InternalEList<?>) getOutgoingDependencies()).basicRemove(otherEnd, msgs);
      case PiMMPackage.PARAMETER__EXPRESSION:
        return basicSetExpression(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param resolve
   *          the resolve
   * @param coreType
   *          the core type
   * @return the object
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return getOutgoingDependencies();
      case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
        return isConfigurationInterface();
      case PiMMPackage.PARAMETER__GRAPH_PORT:
        if (resolve) {
          return getGraphPort();
        }
        return basicGetGraphPort();
      case PiMMPackage.PARAMETER__EXPRESSION:
        return getExpression();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param newValue
   *          the new value
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        getOutgoingDependencies().clear();
        getOutgoingDependencies().addAll((Collection<? extends Dependency>) newValue);
        return;
      case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
        setConfigurationInterface((Boolean) newValue);
        return;
      case PiMMPackage.PARAMETER__GRAPH_PORT:
        setGraphPort((ConfigInputPort) newValue);
        return;
      case PiMMPackage.PARAMETER__EXPRESSION:
        setExpression((Expression) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        getOutgoingDependencies().clear();
        return;
      case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
        setConfigurationInterface(ParameterImpl.CONFIGURATION_INTERFACE_EDEFAULT);
        return;
      case PiMMPackage.PARAMETER__GRAPH_PORT:
        setGraphPort((ConfigInputPort) null);
        return;
      case PiMMPackage.PARAMETER__EXPRESSION:
        setExpression((Expression) null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @return true, if successful
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return (this.outgoingDependencies != null) && !this.outgoingDependencies.isEmpty();
      case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
        return this.configurationInterface != ParameterImpl.CONFIGURATION_INTERFACE_EDEFAULT;
      case PiMMPackage.PARAMETER__GRAPH_PORT:
        return this.graphPort != null;
      case PiMMPackage.PARAMETER__EXPRESSION:
        return this.expression != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param derivedFeatureID
   *          the derived feature ID
   * @param baseClass
   *          the base class
   * @return the int
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(final int derivedFeatureID, final Class<?> baseClass) {
    if (baseClass == ISetter.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
          return PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES;
        default:
          return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl#setName(java.lang.String)
   */
  @Override
  public void setName(final String newName) {
    super.setName(newName);
    if (isConfigurationInterface() && (getGraphPort() != null)) {
      getGraphPort().setName(newName);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param baseFeatureID
   *          the base feature ID
   * @param baseClass
   *          the base class
   * @return the int
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(final int baseFeatureID, final Class<?> baseClass) {
    if (baseClass == ISetter.class) {
      switch (baseFeatureID) {
        case PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES:
          return PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES;
        default:
          return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the string
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) {
      return super.toString();
    }

    final StringBuffer result = new StringBuffer(super.toString());
    result.append(" (configurationInterface: ");
    result.append(this.configurationInterface);
    result.append(')');
    return result.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitParameter(this);
  }

} // ParameterImpl
