/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
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
package org.preesm.codegen.xtend.spider2.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;

/**
 * Pre-processed information of actor prototype for the spider 2 codegen.
 * 
 * @author farresti
 *
 */
public class Spider2CodegenPrototype {
  /** The CHeaderRefinement */
  private final CHeaderRefinement refinement;

  /** Formated list of loop arguments */
  private final List<String> formatedLoopArgs = new ArrayList<>();

  private static final String START_COMMENT = "/* = ";
  private static final String END_COMMENT   = " = */ ";

  /**
   * Constructor of the class;
   * 
   * @param refinement
   *          the CHeaderRefinement associated with the prototype
   */
  public Spider2CodegenPrototype(final CHeaderRefinement refinement) {
    this.refinement = refinement;
    buildLoopProperties(this.refinement.getLoopPrototype());
  }

  private void buildLoopProperties(final FunctionPrototype prototype) {
    if (prototype == null) {
      return;
    }
    /** Init with empty string for out of order insertions */
    final List<FunctionArgument> arguments = new ArrayList<>(prototype.getArguments());
    arguments.forEach(x -> this.formatedLoopArgs.add(""));
    final int maxLength = arguments.stream().map(FunctionArgument::getName).mapToInt(String::length).max().orElse(0);

    /* Insert input config parameters */
    final List<FunctionArgument> inputConfigParams = prototype.getInputConfigParameters();
    for (final FunctionArgument param : inputConfigParams) {
      StringBuilder formatParam = new StringBuilder(START_COMMENT);
      formatParam.append(StringUtils.rightPad(param.getName(), maxLength, " "));
      formatParam.append(END_COMMENT);
      if (param.getType().equals("int64_t")) {
        formatParam.append("inputParams[" + inputConfigParams.indexOf(param) + "]");
      } else {
        formatParam.append(
            "static_cast<" + param.getType().trim() + ">(inputParams[" + inputConfigParams.indexOf(param) + "])");
      }
      this.formatedLoopArgs.set(param.getPosition(), formatParam.toString());
    }

    /* Insert output config parameters */
    final List<FunctionArgument> outputConfigParams = prototype.getOutputConfigParameters();
    for (final FunctionArgument param : outputConfigParams) {
      StringBuilder formatParam = new StringBuilder(START_COMMENT);
      formatParam.append(StringUtils.rightPad(param.getName(), maxLength));
      formatParam.append(END_COMMENT);
      if (param.getType().equals("int64_t")) {
        formatParam.append("&outputParams[" + outputConfigParams.indexOf(param) + "]");
      } else {
        formatParam.append(
            "static_cast<" + param.getType().trim() + " *>(&outputParams[" + outputConfigParams.indexOf(param) + "])");
      }
      this.formatedLoopArgs.set(param.getPosition(), formatParam.toString());
    }

    /* Insert inputs */
    final List<FunctionArgument> inputs = prototype.getInputArguments();
    for (final FunctionArgument param : inputs) {
      StringBuilder formatParam = new StringBuilder(START_COMMENT);
      formatParam.append(StringUtils.rightPad(param.getName(), maxLength));
      formatParam.append(END_COMMENT);
      formatParam.append("reinterpret_cast<" + param.getType().trim() + " *>(inputs[" + inputs.indexOf(param) + "])");
      this.formatedLoopArgs.set(param.getPosition(), formatParam.toString());
    }

    /* Insert outputs */
    final List<FunctionArgument> outputs = prototype.getOutputArguments();
    for (final FunctionArgument param : outputs) {
      StringBuilder formatParam = new StringBuilder(START_COMMENT);
      formatParam.append(StringUtils.rightPad(param.getName(), maxLength));
      formatParam.append(END_COMMENT);
      formatParam.append("reinterpret_cast<" + param.getType().trim() + " *>(outputs[" + outputs.indexOf(param) + "])");
      this.formatedLoopArgs.set(param.getPosition(), formatParam.toString());
    }
  }

  /**
   * 
   * @return init function
   */
  public FunctionPrototype getInit() {
    return this.refinement.getInitPrototype();
  }

  /**
   * 
   * @return loop function
   */
  public FunctionPrototype getLoop() {
    return this.refinement.getLoopPrototype();
  }

  /**
   * 
   * @return list of formatted argument of the loop function
   */
  public List<String> getFormatedLoopArgList() {
    return this.formatedLoopArgs;
  }

  /**
   * 
   * @return list of formatted argument of the loop function without last one
   */
  public List<String> getFormatedLoopArgListButLast() {
    if (this.formatedLoopArgs.isEmpty()) {
      return new ArrayList<>();
    }
    return this.formatedLoopArgs.subList(0, this.formatedLoopArgs.size() - 1);
  }

  /**
   * 
   * @return last formatted argument of the loop function
   */
  public String getLastFormatedLoopArg() {
    if (this.formatedLoopArgs.isEmpty()) {
      return "";
    }
    return this.formatedLoopArgs.get(this.formatedLoopArgs.size() - 1);
  }
}
