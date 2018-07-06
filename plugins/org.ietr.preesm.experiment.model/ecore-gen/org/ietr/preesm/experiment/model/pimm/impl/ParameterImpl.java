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
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

import org.eclipse.xtext.xbase.lib.IterableExtensions;

import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.ExpressionHolder;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getConfigInputPorts <em>Config Input Ports</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getOutgoingDependencies <em>Outgoing Dependencies</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getExpression <em>Expression</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParameterImpl extends VertexImpl implements Parameter {
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
   * The cached value of the '{@link #getOutgoingDependencies() <em>Outgoing Dependencies</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutgoingDependencies()
   * @generated
   * @ordered
   */
  protected EList<Dependency> outgoingDependencies;

  /**
   * The cached value of the '{@link #getExpression() <em>Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExpression()
   * @generated
   * @ordered
   */
  protected Expression expression;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ParameterImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.PARAMETER;
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
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ConfigInputPort> getConfigInputPorts() {
    if (configInputPorts == null) {
      configInputPorts = new EObjectContainmentWithInverseEList<ConfigInputPort>(ConfigInputPort.class, this, PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS, PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE);
    }
    return configInputPorts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Dependency> getOutgoingDependencies() {
    if (outgoingDependencies == null) {
      outgoingDependencies = new EObjectWithInverseResolvingEList<Dependency>(Dependency.class, this, PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES, PiMMPackage.DEPENDENCY__SETTER);
    }
    return outgoingDependencies;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getExpression() {
    return expression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetExpression(Expression newExpression, NotificationChain msgs) {
    Expression oldExpression = expression;
    expression = newExpression;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__EXPRESSION, oldExpression, newExpression);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setExpression(Expression newExpression) {
    if (newExpression != expression) {
      NotificationChain msgs = null;
      if (expression != null)
        msgs = ((InternalEObject)expression).eInverseRemove(this, PiMMPackage.EXPRESSION__HOLDER, Expression.class, msgs);
      if (newExpression != null)
        msgs = ((InternalEObject)newExpression).eInverseAdd(this, PiMMPackage.EXPRESSION__HOLDER, Expression.class, msgs);
      msgs = basicSetExpression(newExpression, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__EXPRESSION, newExpression, newExpression));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getValueExpression() {
    return this.getExpression();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isLocallyStatic() {
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
    final Function1<ISetter, Boolean> _function_2 = new Function1<ISetter, Boolean>() {
      public Boolean apply(final ISetter it) {
        return Boolean.valueOf(it.isLocallyStatic());
      }
    };
    return IterableExtensions.<ISetter>forall(XcoreEListExtensions.<Dependency, ISetter>map(XcoreEListExtensions.<ConfigInputPort, Dependency>map(this.getConfigInputPorts(), _function), _function_1), _function_2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isDependent() {
    boolean _isEmpty = this.getConfigInputPorts().isEmpty();
    return (!_isEmpty);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isConfigurationInterface() {
    return false;
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
      case PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getConfigInputPorts()).basicAdd(otherEnd, msgs);
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingDependencies()).basicAdd(otherEnd, msgs);
      case PiMMPackage.PARAMETER__EXPRESSION:
        if (expression != null)
          msgs = ((InternalEObject)expression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.PARAMETER__EXPRESSION, null, msgs);
        return basicSetExpression((Expression)otherEnd, msgs);
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
      case PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS:
        return ((InternalEList<?>)getConfigInputPorts()).basicRemove(otherEnd, msgs);
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return ((InternalEList<?>)getOutgoingDependencies()).basicRemove(otherEnd, msgs);
      case PiMMPackage.PARAMETER__EXPRESSION:
        return basicSetExpression(null, msgs);
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
      case PiMMPackage.PARAMETER__NAME:
        return getName();
      case PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS:
        return getConfigInputPorts();
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return getOutgoingDependencies();
      case PiMMPackage.PARAMETER__EXPRESSION:
        return getExpression();
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
      case PiMMPackage.PARAMETER__NAME:
        setName((String)newValue);
        return;
      case PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS:
        getConfigInputPorts().clear();
        getConfigInputPorts().addAll((Collection<? extends ConfigInputPort>)newValue);
        return;
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        getOutgoingDependencies().clear();
        getOutgoingDependencies().addAll((Collection<? extends Dependency>)newValue);
        return;
      case PiMMPackage.PARAMETER__EXPRESSION:
        setExpression((Expression)newValue);
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
      case PiMMPackage.PARAMETER__NAME:
        setName(NAME_EDEFAULT);
        return;
      case PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS:
        getConfigInputPorts().clear();
        return;
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        getOutgoingDependencies().clear();
        return;
      case PiMMPackage.PARAMETER__EXPRESSION:
        setExpression((Expression)null);
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
      case PiMMPackage.PARAMETER__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS:
        return configInputPorts != null && !configInputPorts.isEmpty();
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return outgoingDependencies != null && !outgoingDependencies.isEmpty();
      case PiMMPackage.PARAMETER__EXPRESSION:
        return expression != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
    if (baseClass == AbstractVertex.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.PARAMETER__NAME: return PiMMPackage.ABSTRACT_VERTEX__NAME;
        default: return -1;
      }
    }
    if (baseClass == Parameterizable.class) {
      switch (derivedFeatureID) {
        default: return -1;
      }
    }
    if (baseClass == Configurable.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS: return PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS;
        default: return -1;
      }
    }
    if (baseClass == ISetter.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES: return PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES;
        default: return -1;
      }
    }
    if (baseClass == ExpressionHolder.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.PARAMETER__EXPRESSION: return PiMMPackage.EXPRESSION_HOLDER__EXPRESSION;
        default: return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
    if (baseClass == AbstractVertex.class) {
      switch (baseFeatureID) {
        case PiMMPackage.ABSTRACT_VERTEX__NAME: return PiMMPackage.PARAMETER__NAME;
        default: return -1;
      }
    }
    if (baseClass == Parameterizable.class) {
      switch (baseFeatureID) {
        default: return -1;
      }
    }
    if (baseClass == Configurable.class) {
      switch (baseFeatureID) {
        case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS: return PiMMPackage.PARAMETER__CONFIG_INPUT_PORTS;
        default: return -1;
      }
    }
    if (baseClass == ISetter.class) {
      switch (baseFeatureID) {
        case PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES: return PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES;
        default: return -1;
      }
    }
    if (baseClass == ExpressionHolder.class) {
      switch (baseFeatureID) {
        case PiMMPackage.EXPRESSION_HOLDER__EXPRESSION: return PiMMPackage.PARAMETER__EXPRESSION;
        default: return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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

} //ParameterImpl
