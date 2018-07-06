/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Optional;

import java.util.function.Function;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

import org.eclipse.xtext.xbase.lib.IterableExtensions;

import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Configurable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigurableImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigurableImpl#getConfigInputPorts <em>Config Input Ports</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ConfigurableImpl extends VertexImpl implements Configurable {
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getConfigInputPorts() <em>Config Input Ports</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConfigInputPorts()
   * @generated
   * @ordered
   */
  protected EList<ConfigInputPort> configInputPorts;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ConfigurableImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CONFIGURABLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName() {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName) {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIGURABLE__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ConfigInputPort> getConfigInputPorts() {
    if (configInputPorts == null) {
      configInputPorts = new EObjectContainmentWithInverseEList<ConfigInputPort>(ConfigInputPort.class, this, PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS, PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE);
    }
    return configInputPorts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Parameter> getInputParameters() {
    final Function1<ConfigInputPort, Dependency> _function = new Function1<ConfigInputPort, Dependency>() {
      public Dependency apply(final ConfigInputPort it) {
        return it.getIncomingDependency();
      }
    };
    final Function1<Dependency, ISetter> _function_1 = new Function1<Dependency, ISetter>() {
      public ISetter apply(final Dependency it) {
        return it.getSetter();
      }
    };
    return ECollections.<Parameter>unmodifiableEList(ECollections.<Parameter>toEList(Iterables.<Parameter>filter(IterableExtensions.<Dependency, ISetter>map(IterableExtensions.<Dependency>filterNull(IterableExtensions.<ConfigInputPort, Dependency>map(IterableExtensions.<ConfigInputPort>filterNull(this.getConfigInputPorts()), _function)), _function_1), Parameter.class)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigInputPort lookupConfigInputPortConnectedWithParameter(final Parameter parameter) {
    final Function1<ConfigInputPort, Dependency> _function = new Function1<ConfigInputPort, Dependency>() {
      public Dependency apply(final ConfigInputPort it) {
        return it.getIncomingDependency();
      }
    };
    final Function1<Dependency, Boolean> _function_1 = new Function1<Dependency, Boolean>() {
      public Boolean apply(final Dependency it) {
        ISetter _setter = it.getSetter();
        return Boolean.valueOf((_setter == parameter));
      }
    };
    final Function<Dependency, ConfigInputPort> _function_2 = new Function<Dependency, ConfigInputPort>() {
      public ConfigInputPort apply(final Dependency it) {
        return it.getGetter();
      }
    };
    return Optional.<Dependency>ofNullable(IterableExtensions.<Dependency>findFirst(IterableExtensions.<Dependency>filterNull(IterableExtensions.<ConfigInputPort, Dependency>map(IterableExtensions.<ConfigInputPort>filterNull(this.getConfigInputPorts()), _function)), _function_1)).<ConfigInputPort>map(_function_2).orElse(null);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Port> getAllConfigPorts() {
    return ECollections.<Port>unmodifiableEList(this.getConfigInputPorts());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Port> getAllPorts() {
    return ECollections.<Port>unmodifiableEList(this.getAllConfigPorts());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isLocallyStatic() {
    final Function1<Parameter, Boolean> _function = new Function1<Parameter, Boolean>() {
      public Boolean apply(final Parameter it) {
        return Boolean.valueOf(it.isLocallyStatic());
      }
    };
    return IterableExtensions.<Parameter>forall(IterableExtensions.<Parameter>filterNull(this.getInputParameters()), _function);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PiGraph getContainingPiGraph() {
    Graph _containingGraph = super.getContainingGraph();
    return ((PiGraph) _containingGraph);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Port lookupPort(final String portName) {
    final Function1<Port, Boolean> _function = new Function1<Port, Boolean>() {
      public Boolean apply(final Port it) {
        return Boolean.valueOf((((it.getName() == null) && (portName == null)) || ((it.getName() != null) && it.getName().equals(portName))));
      }
    };
    return IterableExtensions.<Port>findFirst(IterableExtensions.<Port>filterNull(this.getAllPorts()), _function);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getVertexPath() {
    final String actorName = this.getName();
    final Function<PiGraph, String> _function = new Function<PiGraph, String>() {
      public String apply(final PiGraph it) {
        String _vertexPath = it.getVertexPath();
        String _plus = (_vertexPath + "/");
        return (_plus + actorName);
      }
    };
    return Optional.<PiGraph>ofNullable(this.getContainingPiGraph()).<String>map(_function).orElse(actorName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getConfigInputPorts()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        return ((InternalEList<?>)getConfigInputPorts()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.CONFIGURABLE__NAME:
        return getName();
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        return getConfigInputPorts();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case PiMMPackage.CONFIGURABLE__NAME:
        setName((String)newValue);
        return;
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        getConfigInputPorts().clear();
        getConfigInputPorts().addAll((Collection<? extends ConfigInputPort>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID) {
    switch (featureID) {
      case PiMMPackage.CONFIGURABLE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        getConfigInputPorts().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID) {
    switch (featureID) {
      case PiMMPackage.CONFIGURABLE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        return configInputPorts != null && !configInputPorts.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //ConfigurableImpl
