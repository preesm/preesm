/**
 * *****************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 *
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
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
 * ****************************************************************************
 */

package org.ietr.preesm.experiment.pimm.cppgenerator.scala.visitor

import org.ietr.preesm.experiment.model.pimm.PiGraph
import org.ietr.preesm.experiment.pimm.cppgenerator.scala.utils.CppCodeGenerationNameGenerator

class CPPCodeGenerationLauncher extends CppCodeGenerationNameGenerator {

  val stringBuilder = new StringBuilder()
  //Shortcut to stringBuilder.append
  private def append(a: Any) = stringBuilder.append(a)
  
  /**
   * Main method, launching the generation for the whole PiGraph pg,
   * including license, includes, constants and top method generation
   */
  def generateCPPCode(pg: PiGraph): String = {    
    val preprocessor = new CPPCodeGenerationPreProcessVisitor
    val codeGenerator = new CPPCodeGenerationVisitor(pg, stringBuilder, preprocessor)

    ///Generate the header (license, includes and constants)
    generateHeader
    append("\n")
    //Generate the top method from which the C++ graph building is launch
    generateTopMehod(pg)
    //Preprocess PiGraph pg in order to collect information on sources and targets of Fifos and Dependecies
    preprocessor.visit(pg)
    //Generate C++ code for the whole PiGraph
    codeGenerator.visit(pg)
    //Returns the final C++ code
    stringBuilder.toString
  }

  /**
   * Generate the header of the final C++ file (license, includes and constants)
   */
  private def generateHeader() = {
    append(license)
    append("\n")
    generateIncludes()
    append("\n")
    generateConstants()
  }

  /**
   * Generate the needed includes for the final C++ file
   */
  private def generateIncludes() = {
    append("\n#include <string.h>")
    append("\n#include <graphs/PiSDF/PiSDFGraph.h.h>")
  }

  /**
   * Generate the needed constants for the final C++ file
   */
  private def generateConstants() = {
    append("\n\nstatic PiSDFGraph graphs[MAX_NB_PiSDF_SUB_GRAPHS];")
    append("\nstatic UINT8 nb_graphs = 0;")
  }

  /**
   * Generate the top method, responsible for building the whole C++ PiGraph corresponding to pg
   */
  private def generateTopMehod(pg: PiGraph): Unit = {
    append("\n/**")
    append("\n * This is the method you need to call to build a complete PiSDF graph.")
    append("\n */")
    //The method does not return anything and is named top
    append("\nvoid top")
    //The method accept as parameter a pointer to the PiSDFGraph graph it will build
    append("(PiSDFGraph* graph)")
    append("{")

    val sgName = getSubraphName(pg)
    val vxName = getVertexName(pg)

    //Get the pointer to the subgraph
    append("\n\tPiSDFGraph *")
    append(sgName)
    append(" = &graphs[nb_graphs];")
    //Increment the graph counter
    append("\n\tnb_graphs++;")
    //Call the building method of sg with the pointer
    append("\n\t")
    append(getMethodName(pg))
    append("(")
    //Pass the pointer to the subgraph
    append(sgName)
    append(",")
    //Pass the parent vertex
    append(vxName)
    append(");")
    append("\n\t")
    //Set the subgraph as subgraph of the vertex
    append(vxName)
    append("->setSubGraph(")
    append(sgName)
    append(");")

    append("\n}\n")
  }

  /**
   * License for PREESM
   */
  private val license: String = "/**\n" +
    " * *****************************************************************************\n" +
    " * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,\n" +
    " * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas\n" +
    " *\n" +
    " * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr\n" +
    " *\n" +
    " * This software is a computer program whose purpose is to prototype\n" +
    " * parallel applications.\n" +
    " *\n" +
    " * This software is governed by the CeCILL-C license under French law and\n" +
    " * abiding by the rules of distribution of free software.  You can  use,\n" +
    " * modify and/ or redistribute the software under the terms of the CeCILL-C\n" +
    " * license as circulated by CEA, CNRS and INRIA at the following URL\n" +
    " * \"http://www.cecill.info\".\n" +
    " *\n" +
    " * As a counterpart to the access to the source code and  rights to copy,\n" +
    " * modify and redistribute granted by the license, users are provided only\n" +
    " * with a limited warranty  and the software's author,  the holder of the\n" +
    " * economic rights,  and the successive licensors  have only  limited\n" +
    " * liability.\n" +
    " *\n" +
    " * In this respect, the user's attention is drawn to the risks associated\n" +
    " * with loading,  using,  modifying and/or developing or reproducing the\n" +
    " * software by the user in light of its specific status of free software,\n" +
    " * that may mean  that it is complicated to manipulate,  and  that  also\n" +
    " * therefore means  that it is reserved for developers  and  experienced\n" +
    " * professionals having in-depth computer knowledge. Users are therefore\n" +
    " * encouraged to load and test the software's suitability as regards their\n" +
    " * requirements in conditions enabling the security of their systems and/or\n" +
    " * data to be ensured and,  more generally, to use and operate it in the\n" +
    " * same conditions as regards security.\n" +
    " *\n" +
    " * The fact that you are presently reading this means that you have had\n" +
    " * knowledge of the CeCILL-C license and that you accept its terms.\n" +
    " * ****************************************************************************\n" +
    " */"

}