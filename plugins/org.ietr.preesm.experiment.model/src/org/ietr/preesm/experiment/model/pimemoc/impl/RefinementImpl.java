/**
 */
package org.ietr.preesm.experiment.model.pimemoc.impl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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

import org.ietr.preesm.experiment.model.pimemoc.AbstractVertex;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage;
import org.ietr.preesm.experiment.model.pimemoc.Refinement;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Refinement</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.impl.RefinementImpl#getFileName
 * <em>File Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RefinementImpl extends EObjectImpl implements Refinement {
	/**
	 * The default value of the '{@link #getFileName() <em>File Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getFileName()
	 * @generated
	 * @ordered
	 */
	protected static final String FILE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFileName() <em>File Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getFileName()
	 * @generated
	 * @ordered
	 */
	protected String fileName = FILE_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected RefinementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PIMeMoCPackage.Literals.REFINEMENT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setFileName(String newFileName) {
		String oldFileName = fileName;
		fileName = newFileName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PIMeMoCPackage.REFINEMENT__FILE_NAME, oldFileName, fileName));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 */
	public AbstractVertex getAbstractVertex() {
		// If the fileName is null, return nothing
		if (this.fileName == null) {
			return null;
		}
		Resource resource = this.eResource();
		URI uri = resource.getURI();
		URI uriTrimmed = uri.trimFragment();
		if (uriTrimmed.isPlatformResource()) {

			// Removing the file name from the URI
			URI folder = uriTrimmed.trimSegments(1);
			URI refinementFile = folder.appendSegment(this.fileName);

			IResource fileResource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(refinementFile.toPlatformString(true));

			// Check if the file exists
			if (fileResource != null) {
				final ResourceSet rSet = new ResourceSetImpl();
				Resource resourceRefinement;
				try {
					resourceRefinement = rSet.getResource(refinementFile, true);
					if (resourceRefinement != null) {
						// does resource contain a graph as root object?
						final EList<EObject> contents = resourceRefinement.getContents();
						for (final EObject object : contents) {
							if (object instanceof Graph) {
								return (AbstractVertex) object;								
							}
						}
					}
				} catch (final WrappedException e) {
					e.printStackTrace();
				}

	
				int i = 3;
			}

		}
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PIMeMoCPackage.REFINEMENT__FILE_NAME:
			return getFileName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case PIMeMoCPackage.REFINEMENT__FILE_NAME:
			setFileName((String) newValue);
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
		case PIMeMoCPackage.REFINEMENT__FILE_NAME:
			setFileName(FILE_NAME_EDEFAULT);
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
		case PIMeMoCPackage.REFINEMENT__FILE_NAME:
			return FILE_NAME_EDEFAULT == null ? fileName != null
					: !FILE_NAME_EDEFAULT.equals(fileName);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (fileName: ");
		result.append(fileName);
		result.append(')');
		return result.toString();
	}

} // RefinementImpl
