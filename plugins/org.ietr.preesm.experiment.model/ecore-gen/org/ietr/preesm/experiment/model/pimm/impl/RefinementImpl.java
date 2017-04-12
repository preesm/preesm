/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013 - 2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Refinement</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl#getFileName <em>File Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RefinementImpl extends EObjectImpl implements Refinement {
  /**
   * The default value of the '{@link #getFileName() <em>File Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getFileName()
   * @generated
   * @ordered
   */
  protected static final String FILE_NAME_EDEFAULT = "\"\"";

  /**
   * The cached value of the '{@link #getFileName() <em>File Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getFileName()
   * @generated
   * @ordered
   */
  protected String fileName = RefinementImpl.FILE_NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected RefinementImpl() {
    super();
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
      case PiMMPackage.REFINEMENT__FILE_NAME:
        return getFileName();
    }
    return super.eGet(featureID, resolve, coreType);
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
      case PiMMPackage.REFINEMENT__FILE_NAME:
        return RefinementImpl.FILE_NAME_EDEFAULT == null ? this.fileName != null : !RefinementImpl.FILE_NAME_EDEFAULT.equals(this.fileName);
    }
    return super.eIsSet(featureID);
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
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.REFINEMENT__FILE_NAME:
        setFileName((String) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.REFINEMENT;
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
      case PiMMPackage.REFINEMENT__FILE_NAME:
        setFileName(RefinementImpl.FILE_NAME_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the abstract actor
   */
  @Override
  public AbstractActor getAbstractActor() {

    if ((getFilePath() != null) && this.filePath.getFileExtension().equals("pi")) {
      final URI refinementURI = URI.createPlatformResourceURI(getFilePath().makeRelative().toString(), true);

      // Check if the file exists
      if (refinementURI != null) {
        final ResourceSet rSet = new ResourceSetImpl();
        Resource resourceRefinement;
        try {
          resourceRefinement = rSet.getResource(refinementURI, true);
          if (resourceRefinement != null) {
            // does resource contain a graph as root object?
            final EList<EObject> contents = resourceRefinement.getContents();
            for (final EObject object : contents) {
              if (object instanceof PiGraph) {
                final AbstractActor actor = (AbstractActor) object;
                actor.setName(((Actor) this.eContainer).getName());
                return actor;
              }
            }
          }
        } catch (final WrappedException e) {
          e.printStackTrace();
        }
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the file name
   * @generated
   */
  @Override
  public String getFileName() {
    return this.fileName;
  }

  /** The file path. */
  IPath filePath;

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.Refinement#getFilePath()
   */
  @Override
  public IPath getFilePath() {
    return this.filePath;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.Refinement#setFilePath(org.eclipse.core.runtime.IPath)
   */
  @Override
  public void setFilePath(final IPath path) {
    this.filePath = path;
    if (path != null) {
      setFileName(path.lastSegment());
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newFileName
   *          the new file name
   * @generated
   */
  @Override
  public void setFileName(final String newFileName) {
    final String oldFileName = this.fileName;
    this.fileName = newFileName;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.REFINEMENT__FILE_NAME, oldFileName, this.fileName));
    }
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
    result.append(" (fileName: ");
    result.append(this.fileName);
    result.append(')');
    return result.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitable#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitRefinement(this);
  }

} // RefinementImpl
