/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
/**
 *
 */
package org.ietr.dftools.architecture.slam.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.ietr.dftools.architecture.slam.Design;

// TODO: Auto-generated Javadoc
/**
 * Resource implementation used to (de)serialize the System-Level Architecture Model into IP-XACT.
 *
 * @author mpelcat
 */
public class IPXACTResourceImpl extends ResourceImpl {

  /**
   * Constructor for XMIResourceImpl.
   *
   * @param uri
   *          the uri
   */
  public IPXACTResourceImpl(final URI uri) {
    super(uri);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#doSave(java.io.OutputStream, java.util.Map)
   */
  @Override
  public void doSave(final OutputStream outputStream, final Map<?, ?> options) throws IOException {

    final IPXACTDesignWriter designWriter = new IPXACTDesignWriter();

    final Design design = (Design) getContents().get(0);

    designWriter.write(design, outputStream);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#doLoad(java.io.InputStream, java.util.Map)
   */
  @Override
  protected void doLoad(final InputStream inputStream, final Map<?, ?> options) throws IOException {

    final IPXACTDesignParser designParser = new IPXACTDesignParser(this.uri);

    final Design design = designParser.parse(inputStream, null, null);
    if ((design != null) && !getContents().contains(design)) {
      getContents().add(design);
    }
  }
}
