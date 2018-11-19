/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.preesm.algorithm.codegen.idl;

import java.util.logging.Level;
import org.jacorb.idl.ConstDecl;
import org.jacorb.idl.GlobalInputStream;
import org.jacorb.idl.IDLTreeVisitor;
import org.jacorb.idl.NameTable;
import org.jacorb.idl.TypeMap;
import org.jacorb.idl.lexer;
import org.jacorb.idl.parser;
import org.preesm.commons.logger.PreesmLogger;

// TODO: Auto-generated Javadoc
/**
 * Parsing actor function prototypes from IDL files.
 *
 * @author jpiat
 */

public class IDLParser extends parser {

  /**
   * Parses the.
   *
   * @param filePath
   *          the file path
   * @param visitor
   *          the visitor
   */
  public static void parse(final String filePath, final IDLTreeVisitor visitor) {
    try {
      parser.init();
      parser.setGenerator(visitor);
      GlobalInputStream.init();
      GlobalInputStream.setInput(filePath);

      /* reset tables everywhere */
      lexer.reset();
      NameTable.init();
      ConstDecl.init();
      TypeMap.init();

      new parser().parse();
    } catch (final Exception e) {
      PreesmLogger.getLogger().log(Level.WARNING, "IDL Parser internal exception: " + e.getMessage());
    }
  }
}
