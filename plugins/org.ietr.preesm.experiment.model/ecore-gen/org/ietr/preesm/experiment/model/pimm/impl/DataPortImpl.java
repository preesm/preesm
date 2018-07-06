/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Optional;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

import org.eclipse.xtext.xbase.lib.IterableExtensions;

import org.ietr.preesm.experiment.model.PiGraphException;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.ExpressionHolder;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PortKind;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl#getExpression <em>Expression</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl#getAnnotation <em>Annotation</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class DataPortImpl extends MinimalEObjectImpl.Container implements DataPort {
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
   * The cached value of the '{@link #getExpression() <em>Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExpression()
   * @generated
   * @ordered
   */
  protected Expression expression;

  /**
   * The default value of the '{@link #getAnnotation() <em>Annotation</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAnnotation()
   * @generated
   * @ordered
   */
  protected static final PortMemoryAnnotation ANNOTATION_EDEFAULT = PortMemoryAnnotation.NONE;

  /**
   * The cached value of the '{@link #getAnnotation() <em>Annotation</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAnnotation()
   * @generated
   * @ordered
   */
  protected PortMemoryAnnotation annotation = ANNOTATION_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DataPortImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.DATA_PORT;
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
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_PORT__NAME, oldName, name));
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_PORT__EXPRESSION, oldExpression, newExpression);
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
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_PORT__EXPRESSION, newExpression, newExpression));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PortMemoryAnnotation getAnnotation() {
    return annotation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAnnotation(PortMemoryAnnotation newAnnotation) {
    PortMemoryAnnotation oldAnnotation = annotation;
    annotation = newAnnotation == null ? ANNOTATION_EDEFAULT : newAnnotation;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_PORT__ANNOTATION, oldAnnotation, annotation));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getPortRateExpression() {
    return this.getExpression();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbstractActor getContainingActor() {
    EObject _eContainer = this.eContainer();
    if ((_eContainer instanceof AbstractActor)) {
      EObject _eContainer_1 = this.eContainer();
      return ((AbstractActor) _eContainer_1);
    }
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Parameter> getInputParameters() {
    return ECollections.<Parameter>unmodifiableEList(this.getContainingActor().getInputParameters());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getId() {
    try {
      final Function<AbstractActor, String> _function = new Function<AbstractActor, String>() {
        public String apply(final AbstractActor it) {
          return it.getName();
        }
      };
      final Supplier<PiGraphException> _function_1 = new Supplier<PiGraphException>() {
        public PiGraphException get() {
          return new PiGraphException((("Data port " + DataPortImpl.this) + " is not contained in an AbstracytActor."));
        }
      };
      final String actorName = Optional.<AbstractActor>ofNullable(this.getContainingActor()).<String>map(_function).<PiGraphException>orElseThrow(_function_1);
      final Predicate<String> _function_2 = new Predicate<String>() {
        public boolean test(final String it) {
          boolean _isEmpty = it.isEmpty();
          return (!_isEmpty);
        }
      };
      final Function<String, String> _function_3 = new Function<String, String>() {
        public String apply(final String it) {
          return ("." + it);
        }
      };
      final String portName = Optional.<String>ofNullable(this.getName()).filter(_function_2).<String>map(_function_3).orElse("");
      return (actorName + portName);
    }
    catch (Throwable _e) {
      throw org.eclipse.xtext.xbase.lib.Exceptions.sneakyThrow(_e);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Fifo getFifo() {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
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
  public PortKind getKind() {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.DATA_PORT__EXPRESSION:
        if (expression != null)
          msgs = ((InternalEObject)expression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.DATA_PORT__EXPRESSION, null, msgs);
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
      case PiMMPackage.DATA_PORT__EXPRESSION:
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
      case PiMMPackage.DATA_PORT__NAME:
        return getName();
      case PiMMPackage.DATA_PORT__EXPRESSION:
        return getExpression();
      case PiMMPackage.DATA_PORT__ANNOTATION:
        return getAnnotation();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case PiMMPackage.DATA_PORT__NAME:
        setName((String)newValue);
        return;
      case PiMMPackage.DATA_PORT__EXPRESSION:
        setExpression((Expression)newValue);
        return;
      case PiMMPackage.DATA_PORT__ANNOTATION:
        setAnnotation((PortMemoryAnnotation)newValue);
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
      case PiMMPackage.DATA_PORT__NAME:
        setName(NAME_EDEFAULT);
        return;
      case PiMMPackage.DATA_PORT__EXPRESSION:
        setExpression((Expression)null);
        return;
      case PiMMPackage.DATA_PORT__ANNOTATION:
        setAnnotation(ANNOTATION_EDEFAULT);
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
      case PiMMPackage.DATA_PORT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case PiMMPackage.DATA_PORT__EXPRESSION:
        return expression != null;
      case PiMMPackage.DATA_PORT__ANNOTATION:
        return annotation != ANNOTATION_EDEFAULT;
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
    if (baseClass == Parameterizable.class) {
      switch (derivedFeatureID) {
        default: return -1;
      }
    }
    if (baseClass == ExpressionHolder.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.DATA_PORT__EXPRESSION: return PiMMPackage.EXPRESSION_HOLDER__EXPRESSION;
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
    if (baseClass == Parameterizable.class) {
      switch (baseFeatureID) {
        default: return -1;
      }
    }
    if (baseClass == ExpressionHolder.class) {
      switch (baseFeatureID) {
        case PiMMPackage.EXPRESSION_HOLDER__EXPRESSION: return PiMMPackage.DATA_PORT__EXPRESSION;
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
    result.append(", annotation: ");
    result.append(annotation);
    result.append(')');
    return result.toString();
  }

} //DataPortImpl
