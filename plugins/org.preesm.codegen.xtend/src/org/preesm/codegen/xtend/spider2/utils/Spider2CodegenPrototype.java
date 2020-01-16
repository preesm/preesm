package org.preesm.codegen.xtend.spider2.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;

public class Spider2CodegenPrototype {
  /** The CHeaderRefinement */
  private final CHeaderRefinement refinement;

  /** Formated list of loop arguments */
  private final List<String> formatedLoopArgs = new ArrayList<>();

  /** Formated list of init arguments */
  private final List<String> formatedInitArgs = new ArrayList<>();

  private static final String START_COMMENT = "/* = ";
  private static final String END_COMMENT   = " = */ ";

  public Spider2CodegenPrototype(final CHeaderRefinement refinement) {
    this.refinement = refinement;
    buildInitProperties(this.refinement.getInitPrototype());
    buildLoopProperties(this.refinement.getLoopPrototype());
  }

  private void buildInitProperties(final FunctionPrototype prototype) {
    if (prototype == null) {
      return;
    }
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

  public FunctionPrototype getInit() {
    return this.refinement.getInitPrototype();
  }

  public String getLastFormatedInitArg() {
    if (this.formatedInitArgs.isEmpty()) {
      return "";
    }
    return this.formatedInitArgs.get(this.formatedInitArgs.size() - 1);
  }

  public List<String> getFormatedInitArgList() {
    return this.formatedInitArgs;
  }

  public List<String> getFormatedInitArgListButLast() {
    if (this.formatedInitArgs.isEmpty()) {
      return new ArrayList<>();
    }
    return this.formatedInitArgs.subList(0, this.formatedInitArgs.size() - 1);
  }

  public FunctionPrototype getLoop() {
    return this.refinement.getLoopPrototype();
  }

  public List<String> getFormatedLoopArgList() {
    return this.formatedLoopArgs;
  }

  public List<String> getFormatedLoopArgListButLast() {
    if (this.formatedLoopArgs.isEmpty()) {
      return new ArrayList<>();
    }
    return this.formatedLoopArgs.subList(0, this.formatedLoopArgs.size() - 1);
  }

  public String getLastFormatedLoopArg() {
    if (this.formatedLoopArgs.isEmpty()) {
      return "";
    }
    return this.formatedLoopArgs.get(this.formatedLoopArgs.size() - 1);
  }
}
