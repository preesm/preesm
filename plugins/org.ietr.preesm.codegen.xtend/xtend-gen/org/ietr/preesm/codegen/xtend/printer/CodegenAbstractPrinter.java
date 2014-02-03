/**
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
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
 */
package org.ietr.preesm.codegen.xtend.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialType;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.codegen.xtend.model.codegen.util.CodegenSwitch;
import org.ietr.preesm.codegen.xtend.printer.PrinterState;
import org.ietr.preesm.codegen.xtend.task.CodegenException;

/**
 * The {@link CodegenPrinterVisitor} is used to visit a {@link CodegenPackage
 * Codegen model}. To use a printer, the following function calls should be used:<br>
 * 1. Call {@link #preProcessing(List)} on a {@link List} containing all printed {@link Block blocks}.<br>
 * 2. Call {@link #doSwitch()} on each {@link Block} to print.
 * 
 * @author kdesnos
 */
@SuppressWarnings("all")
public abstract class CodegenAbstractPrinter extends CodegenSwitch<CharSequence> {
  private PrinterState state = PrinterState.IDLE;
  
  /**
   * Method used to change the current state of the printer.
   * @param newState
   * 	the new State of the printer
   */
  protected void setState(final PrinterState newState) {
    this.state = newState;
  }
  
  /**
   * True if the visitor is currently printing {@link Variable}
   * {@link CoreBlock#getDefinitions() definitions}
   */
  protected boolean printingDefinitions = false;
  
  /**
   * True if the visitor is currently printing {@link Variable}
   * {@link CoreBlock#getDeclarations() declarations}
   */
  protected boolean printingDeclarations = false;
  
  /**
   * True if the visitor is currently printing the {@link CallBlock}
   * {@link CoreBlock#getInitBlock() initBlock}
   */
  protected boolean printingInitBlock = false;
  
  /**
   * True if the visitor is currently printing the {@link LoopBlock}
   * {@link CoreBlock#getInitBlock() initBlock}
   */
  protected boolean printingLoopBlock = false;
  
  /**
   * Reference to the {@link CoreBlock} currently printed
   */
  protected CoreBlock _printedCoreBlock;
  
  /**
   * Reference to the {@link CoreBlock} currently printed
   */
  public CoreBlock getPrintedCoreBlock() {
    return this._printedCoreBlock;
  }
  
  /**
   * Reference to the {@link CoreBlock} currently printed
   */
  public void setPrintedCoreBlock(final CoreBlock printedCoreBlock) {
    this._printedCoreBlock = printedCoreBlock;
  }
  
  public CharSequence caseCommunication(final Communication communication) {
    CharSequence _printCommunication = this.printCommunication(communication);
    return _printCommunication;
  }
  
  public CharSequence caseSharedMemoryCommunication(final SharedMemoryCommunication communication) {
    CharSequence _printSharedMemoryCommunication = this.printSharedMemoryCommunication(communication);
    return _printSharedMemoryCommunication;
  }
  
  public CharSequence caseCoreBlock(final CoreBlock coreBlock) {
    StringConcatenation _xblockexpression = null;
    {
      StringConcatenation _stringConcatenation = new StringConcatenation();
      StringConcatenation result = _stringConcatenation;
      final CharSequence coreBlockHeader = this.printCoreBlockHeader(coreBlock);
      result.append(coreBlockHeader);
      String _xifexpression = null;
      int _length = coreBlockHeader.length();
      boolean _greaterThan = (_length > 0);
      if (_greaterThan) {
        String _lastLineIndentation = CodegenAbstractPrinter.getLastLineIndentation(result);
        _xifexpression = _lastLineIndentation;
      } else {
        _xifexpression = "";
      }
      final String indentationCoreBlock = _xifexpression;
      boolean _xifexpression_1 = false;
      int _length_1 = coreBlockHeader.length();
      boolean _greaterThan_1 = (_length_1 > 0);
      if (_greaterThan_1) {
        boolean _xblockexpression_1 = false;
        {
          StringConcatenation _trimLastEOL = CodegenAbstractPrinter.trimLastEOL(result);
          result = _trimLastEOL;
          boolean _endWithEOL = CodegenAbstractPrinter.endWithEOL(result);
          _xblockexpression_1 = (_endWithEOL);
        }
        _xifexpression_1 = _xblockexpression_1;
      } else {
        _xifexpression_1 = false;
      }
      final boolean coreBlockHasNewLine = _xifexpression_1;
      String indentation = null;
      boolean hasNewLine = false;
      {
        this.setState(PrinterState.PRINTING_DECLARATIONS);
        EList<Variable> _declarations = coreBlock.getDeclarations();
        final Function1<Variable,Boolean> _function = new Function1<Variable,Boolean>() {
            public Boolean apply(final Variable it) {
              EList<Variable> _definitions = coreBlock.getDefinitions();
              boolean _contains = _definitions.contains(it);
              boolean _not = (!_contains);
              return Boolean.valueOf(_not);
            }
          };
        Iterable<Variable> _filter = IterableExtensions.<Variable>filter(_declarations, _function);
        List<Variable> _list = IterableExtensions.<Variable>toList(_filter);
        final CharSequence declarationsHeader = this.printDeclarationsHeader(_list);
        result.append(declarationsHeader, indentationCoreBlock);
        int _length_2 = declarationsHeader.length();
        boolean _greaterThan_2 = (_length_2 > 0);
        if (_greaterThan_2) {
          String _lastLineIndentation_1 = CodegenAbstractPrinter.getLastLineIndentation(result);
          indentation = _lastLineIndentation_1;
          StringConcatenation _trimLastEOL = CodegenAbstractPrinter.trimLastEOL(result);
          result = _trimLastEOL;
          boolean _endWithEOL = CodegenAbstractPrinter.endWithEOL(result);
          hasNewLine = _endWithEOL;
        } else {
          indentation = indentationCoreBlock;
          hasNewLine = false;
        }
        EList<Variable> _declarations_1 = coreBlock.getDeclarations();
        final Function1<Variable,Boolean> _function_1 = new Function1<Variable,Boolean>() {
            public Boolean apply(final Variable it) {
              EList<Variable> _definitions = coreBlock.getDefinitions();
              boolean _contains = _definitions.contains(it);
              boolean _not = (!_contains);
              return Boolean.valueOf(_not);
            }
          };
        Iterable<Variable> _filter_1 = IterableExtensions.<Variable>filter(_declarations_1, _function_1);
        final Function1<Variable,CharSequence> _function_2 = new Function1<Variable,CharSequence>() {
            public CharSequence apply(final Variable it) {
              CharSequence _doSwitch = CodegenAbstractPrinter.this.doSwitch(it);
              return _doSwitch;
            }
          };
        Iterable<CharSequence> _map = IterableExtensions.<Variable, CharSequence>map(_filter_1, _function_2);
        String _join = IterableExtensions.join(_map, "");
        result.append(_join, indentation);
        if (hasNewLine) {
          result.newLineIfNotEmpty();
          result.append(indentationCoreBlock);
        }
        EList<Variable> _declarations_2 = coreBlock.getDeclarations();
        CharSequence _printDeclarationsFooter = this.printDeclarationsFooter(_declarations_2);
        result.append(_printDeclarationsFooter, indentationCoreBlock);
      }
      {
        this.setState(PrinterState.PRINTING_DEFINITIONS);
        EList<Variable> _definitions = coreBlock.getDefinitions();
        final CharSequence definitionsHeader = this.printDefinitionsHeader(_definitions);
        result.append(definitionsHeader, indentationCoreBlock);
        int _length_2 = definitionsHeader.length();
        boolean _greaterThan_2 = (_length_2 > 0);
        if (_greaterThan_2) {
          String _lastLineIndentation_1 = CodegenAbstractPrinter.getLastLineIndentation(result);
          indentation = _lastLineIndentation_1;
          StringConcatenation _trimLastEOL = CodegenAbstractPrinter.trimLastEOL(result);
          result = _trimLastEOL;
          boolean _endWithEOL = CodegenAbstractPrinter.endWithEOL(result);
          hasNewLine = _endWithEOL;
        } else {
          indentation = indentationCoreBlock;
          hasNewLine = false;
        }
        EList<Variable> _definitions_1 = coreBlock.getDefinitions();
        final Function1<Variable,CharSequence> _function = new Function1<Variable,CharSequence>() {
            public CharSequence apply(final Variable it) {
              CharSequence _doSwitch = CodegenAbstractPrinter.this.doSwitch(it);
              return _doSwitch;
            }
          };
        List<CharSequence> _map = ListExtensions.<Variable, CharSequence>map(_definitions_1, _function);
        String _join = IterableExtensions.join(_map, "");
        result.append(_join, indentation);
        if (hasNewLine) {
          result.newLineIfNotEmpty();
          result.append(indentationCoreBlock);
        }
        EList<Variable> _definitions_2 = coreBlock.getDefinitions();
        CharSequence _printDefinitionsFooter = this.printDefinitionsFooter(_definitions_2);
        result.append(_printDefinitionsFooter, indentationCoreBlock);
      }
      {
        this.setState(PrinterState.PRINTING_INIT_BLOCK);
        CallBlock _initBlock = coreBlock.getInitBlock();
        final CharSequence coreInitHeader = this.printCoreInitBlockHeader(_initBlock);
        result.append(coreInitHeader, indentationCoreBlock);
        int _length_2 = coreInitHeader.length();
        boolean _greaterThan_2 = (_length_2 > 0);
        if (_greaterThan_2) {
          String _lastLineIndentation_1 = CodegenAbstractPrinter.getLastLineIndentation(result);
          indentation = _lastLineIndentation_1;
          StringConcatenation _trimLastEOL = CodegenAbstractPrinter.trimLastEOL(result);
          result = _trimLastEOL;
          boolean _endWithEOL = CodegenAbstractPrinter.endWithEOL(result);
          hasNewLine = _endWithEOL;
        } else {
          indentation = indentationCoreBlock;
          hasNewLine = false;
        }
        CallBlock _initBlock_1 = coreBlock.getInitBlock();
        CharSequence _doSwitch = this.doSwitch(_initBlock_1);
        result.append(_doSwitch, indentation);
        if (hasNewLine) {
          result.newLineIfNotEmpty();
          result.append(indentationCoreBlock);
        }
        CallBlock _initBlock_2 = coreBlock.getInitBlock();
        CharSequence _printCoreInitBlockFooter = this.printCoreInitBlockFooter(_initBlock_2);
        result.append(_printCoreInitBlockFooter, indentationCoreBlock);
      }
      {
        this.setState(PrinterState.PRINTING_LOOP_BLOCK);
        LoopBlock _loopBlock = coreBlock.getLoopBlock();
        final CharSequence coreLoopHeader = this.printCoreLoopBlockHeader(_loopBlock);
        result.append(coreLoopHeader, indentationCoreBlock);
        int _length_2 = coreLoopHeader.length();
        boolean _greaterThan_2 = (_length_2 > 0);
        if (_greaterThan_2) {
          String _lastLineIndentation_1 = CodegenAbstractPrinter.getLastLineIndentation(result);
          indentation = _lastLineIndentation_1;
          StringConcatenation _trimLastEOL = CodegenAbstractPrinter.trimLastEOL(result);
          result = _trimLastEOL;
          boolean _endWithEOL = CodegenAbstractPrinter.endWithEOL(result);
          hasNewLine = _endWithEOL;
        } else {
          indentation = indentationCoreBlock;
          hasNewLine = false;
        }
        LoopBlock _loopBlock_1 = coreBlock.getLoopBlock();
        CharSequence _doSwitch = this.doSwitch(_loopBlock_1);
        result.append(_doSwitch, indentation);
        if (hasNewLine) {
          result.newLineIfNotEmpty();
          result.append(indentationCoreBlock);
        }
        LoopBlock _loopBlock_2 = coreBlock.getLoopBlock();
        CharSequence _printCoreLoopBlockFooter = this.printCoreLoopBlockFooter(_loopBlock_2);
        result.append(_printCoreLoopBlockFooter, indentationCoreBlock);
      }
      if (coreBlockHasNewLine) {
        result.newLineIfNotEmpty();
      }
      CharSequence _printCoreBlockFooter = this.printCoreBlockFooter(coreBlock);
      result.append(_printCoreBlockFooter);
      _xblockexpression = (result);
    }
    return _xblockexpression;
  }
  
  /**
   * Returns <code>True</code> if the {@link StringConcatenation} ends with
   * an empty line. (i.e. it ends with a \n)
   * @param concatenation
   * 		the {@link StringConcatenation} to test
   * @return <code>True</code> if the {@link StringConcatenation} ends with
   * an empty line. (i.e. it ends with a \n)
   */
  public static boolean endWithEOL(final StringConcatenation concatenation) {
    boolean _xblockexpression = false;
    {
      final char n = '\n';
      int _length = concatenation.length();
      int _minus = (_length - 1);
      char _charAt = concatenation.charAt(_minus);
      boolean _equals = (_charAt == n);
      _xblockexpression = (_equals);
    }
    return _xblockexpression;
  }
  
  /**
   * Returns a copy of the input {@link StringConcatenation}. If the final
   * line of the {@link StringConcatenation} is empty, the last "\r\n" (or
   * "\n") are removed from the returned {@link StringConcatenation}, else the
   * input {@link StringConcatenation} is returned as is
   * 
   * @param sequence
   *            the {@link StringConcatenation} to process
   * @return the input {@link StringConcatenation} as is or without its final
   *         "\r\n" or "\n"
   */
  public static StringConcatenation trimLastEOL(final StringConcatenation sequence) {
    StringConcatenation _xblockexpression = null;
    {
      String result = sequence.toString();
      final char newLine = '\n';
      final char r = '\r';
      boolean _and = false;
      int _length = result.length();
      boolean _greaterThan = (_length > 0);
      if (!_greaterThan) {
        _and = false;
      } else {
        int _length_1 = result.length();
        int _minus = (_length_1 - 1);
        char _charAt = result.charAt(_minus);
        boolean _equals = (_charAt == newLine);
        _and = (_greaterThan && _equals);
      }
      if (_and) {
        int _length_2 = result.length();
        int _minus_1 = (_length_2 - 1);
        CharSequence _subSequence = result.subSequence(0, _minus_1);
        String _string = _subSequence.toString();
        result = _string;
        boolean _and_1 = false;
        int _length_3 = result.length();
        boolean _greaterThan_1 = (_length_3 > 0);
        if (!_greaterThan_1) {
          _and_1 = false;
        } else {
          int _length_4 = result.length();
          int _minus_2 = (_length_4 - 1);
          char _charAt_1 = result.charAt(_minus_2);
          boolean _equals_1 = (_charAt_1 == r);
          _and_1 = (_greaterThan_1 && _equals_1);
        }
        if (_and_1) {
          int _length_5 = result.length();
          int _minus_3 = (_length_5 - 1);
          CharSequence _subSequence_1 = result.subSequence(0, _minus_3);
          String _string_1 = _subSequence_1.toString();
          result = _string_1;
        }
      }
      StringConcatenation _stringConcatenation = new StringConcatenation();
      StringConcatenation res = _stringConcatenation;
      res.append(result);
      _xblockexpression = (res);
    }
    return _xblockexpression;
  }
  
  /**
   * Get a {@link String} that corresponds to the the number of tabulations
   * that form the last line of the input {@link CharSequence}. If the
   * {@link CharSequence} ends with an '\n' or with a '\r\n' {@link String},
   * the penultimate line is processed instead. If the last line contains
   * something else than tabulations, an empty {@link String} is returned<br>
   * <br>
   * Examples:<br>
   * "<code>I'm a line of
   * <br>		code</code>" <br>
   * returns ''<br>
   * <br>
   * "<code>I'm another line of code
   * <br>\t\t</code>" <br>
   * returns '\t\t' <br>
   * <br>
   * "<code>I'm a last line of code
   * <br>\t\t<br>
   * </code>" <br>
   * returns '\t\t'
   * 
   * @param input
   *            the processed {@link CharSequence}
   * @return the {@link String} containing only '\t' or nothing
   */
  public static String getLastLineIndentation(final CharSequence input) {
    final char newLine = '\n';
    final char tab = '\t';
    final char r = '\r';
    int _length = input.length();
    int lastLineBeginning = (_length - 1);
    int lastLineIndentationEnd = 0;
    boolean exitLoop = false;
    boolean _and = false;
    boolean _greaterEqualsThan = (lastLineBeginning >= 0);
    if (!_greaterEqualsThan) {
      _and = false;
    } else {
      char _charAt = input.charAt(lastLineBeginning);
      boolean _equals = (_charAt == newLine);
      _and = (_greaterEqualsThan && _equals);
    }
    if (_and) {
      int _minus = (lastLineBeginning - 1);
      lastLineBeginning = _minus;
      boolean _and_1 = false;
      boolean _greaterEqualsThan_1 = (lastLineBeginning >= 0);
      if (!_greaterEqualsThan_1) {
        _and_1 = false;
      } else {
        char _charAt_1 = input.charAt(lastLineBeginning);
        boolean _equals_1 = (_charAt_1 == r);
        _and_1 = (_greaterEqualsThan_1 && _equals_1);
      }
      if (_and_1) {
        int _minus_1 = (lastLineBeginning - 1);
        lastLineBeginning = _minus_1;
      }
    }
    int _plus = (lastLineBeginning + 1);
    lastLineIndentationEnd = _plus;
    boolean _and_2 = false;
    boolean _greaterEqualsThan_2 = (lastLineBeginning >= 0);
    if (!_greaterEqualsThan_2) {
      _and_2 = false;
    } else {
      boolean _not = (!exitLoop);
      _and_2 = (_greaterEqualsThan_2 && _not);
    }
    boolean _while = _and_2;
    while (_while) {
      {
        char currentChar = input.charAt(lastLineBeginning);
        boolean _equals_2 = (currentChar == newLine);
        if (_equals_2) {
          int _plus_1 = (lastLineBeginning + 1);
          lastLineBeginning = _plus_1;
          exitLoop = true;
        } else {
          boolean _notEquals = (currentChar != tab);
          if (_notEquals) {
            return "";
          }
          int _minus_2 = (lastLineBeginning - 1);
          lastLineBeginning = _minus_2;
        }
      }
      boolean _and_3 = false;
      boolean _greaterEqualsThan_3 = (lastLineBeginning >= 0);
      if (!_greaterEqualsThan_3) {
        _and_3 = false;
      } else {
        boolean _not_1 = (!exitLoop);
        _and_3 = (_greaterEqualsThan_3 && _not_1);
      }
      _while = _and_3;
    }
    CharSequence _subSequence = input.subSequence(lastLineBeginning, lastLineIndentationEnd);
    return _subSequence.toString();
  }
  
  public CharSequence caseBuffer(final Buffer buffer) {
    boolean _equals = this.state.equals(PrinterState.PRINTING_DEFINITIONS);
    if (_equals) {
      return this.printBufferDefinition(buffer);
    }
    boolean _equals_1 = this.state.equals(PrinterState.PRINTING_DECLARATIONS);
    if (_equals_1) {
      return this.printBufferDeclaration(buffer);
    }
    return this.printBuffer(buffer);
  }
  
  public CharSequence caseCallBlock(final CallBlock callBlock) {
    String _xblockexpression = null;
    {
      ArrayList<CharSequence> _arrayList = new ArrayList<CharSequence>();
      ArrayList<CharSequence> result = _arrayList;
      this.printCallBlockHeader(callBlock);
      EList<CodeElt> _codeElts = callBlock.getCodeElts();
      final Function1<CodeElt,CharSequence> _function = new Function1<CodeElt,CharSequence>() {
          public CharSequence apply(final CodeElt it) {
            CharSequence _doSwitch = CodegenAbstractPrinter.this.doSwitch(it);
            return _doSwitch;
          }
        };
      List<CharSequence> _map = ListExtensions.<CodeElt, CharSequence>map(_codeElts, _function);
      result.addAll(_map);
      this.printCallBlockFooter(callBlock);
      String _join = IterableExtensions.join(result, "");
      _xblockexpression = (_join);
    }
    return _xblockexpression;
  }
  
  public CharSequence caseConstant(final Constant constant) {
    boolean _equals = this.state.equals(PrinterState.PRINTING_DEFINITIONS);
    if (_equals) {
      return this.printConstantDefinition(constant);
    }
    boolean _equals_1 = this.state.equals(PrinterState.PRINTING_DECLARATIONS);
    if (_equals_1) {
      return this.printConstantDeclaration(constant);
    }
    return this.printConstant(constant);
  }
  
  public CharSequence caseConstantString(final ConstantString constant) {
    boolean _equals = this.state.equals(PrinterState.PRINTING_DEFINITIONS);
    if (_equals) {
      return this.printConstantStringDefinition(constant);
    }
    boolean _equals_1 = this.state.equals(PrinterState.PRINTING_DECLARATIONS);
    if (_equals_1) {
      return this.printConstantStringDeclaration(constant);
    }
    return this.printConstantString(constant);
  }
  
  public CharSequence defaultCase(final EObject object) {
    try {
      String _plus = ("Object " + object);
      String _plus_1 = (_plus + " is not supported by the printer");
      String _plus_2 = (_plus_1 + this);
      String _plus_3 = (_plus_2 + "in its current state. ");
      CodegenException _codegenException = new CodegenException(_plus_3);
      throw _codegenException;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Get the current {@link PrinterState} of the printer
   */
  public PrinterState getState() {
    return this.state;
  }
  
  public CharSequence caseFifoCall(final FifoCall fifoCall) {
    CharSequence _printFifoCall = this.printFifoCall(fifoCall);
    return _printFifoCall;
  }
  
  public CharSequence caseFunctionCall(final FunctionCall functionCall) {
    CharSequence _printFunctionCall = this.printFunctionCall(functionCall);
    return _printFunctionCall;
  }
  
  public CharSequence caseLoopBlock(final LoopBlock loopBlock) {
    String _xblockexpression = null;
    {
      ArrayList<CharSequence> _arrayList = new ArrayList<CharSequence>();
      ArrayList<CharSequence> result = _arrayList;
      CharSequence _printLoopBlockHeader = this.printLoopBlockHeader(loopBlock);
      result.add(_printLoopBlockHeader);
      EList<CodeElt> _codeElts = loopBlock.getCodeElts();
      final Function1<CodeElt,CharSequence> _function = new Function1<CodeElt,CharSequence>() {
          public CharSequence apply(final CodeElt it) {
            CharSequence _doSwitch = CodegenAbstractPrinter.this.doSwitch(it);
            return _doSwitch;
          }
        };
      List<CharSequence> _map = ListExtensions.<CodeElt, CharSequence>map(_codeElts, _function);
      result.addAll(_map);
      CharSequence _printLoopBlockFooter = this.printLoopBlockFooter(loopBlock);
      result.add(_printLoopBlockFooter);
      String _join = IterableExtensions.join(result, "");
      _xblockexpression = (_join);
    }
    return _xblockexpression;
  }
  
  public CharSequence caseSemaphore(final Semaphore semaphore) {
    boolean _equals = this.state.equals(PrinterState.PRINTING_DEFINITIONS);
    if (_equals) {
      return this.printSemaphoreDefinition(semaphore);
    }
    boolean _equals_1 = this.state.equals(PrinterState.PRINTING_DECLARATIONS);
    if (_equals_1) {
      return this.printSemaphoreDeclaration(semaphore);
    }
    return this.printSemaphore(semaphore);
  }
  
  public CharSequence caseSpecialCall(final SpecialCall specialCall) {
    CharSequence _xblockexpression = null;
    {
      CharSequence _switchResult = null;
      boolean _matched = false;
      if (!_matched) {
        boolean _isFork = specialCall.isFork();
        if (_isFork) {
          _matched=true;
          CharSequence _printFork = this.printFork(specialCall);
          _switchResult = _printFork;
        }
      }
      if (!_matched) {
        boolean _isJoin = specialCall.isJoin();
        if (_isJoin) {
          _matched=true;
          CharSequence _printJoin = this.printJoin(specialCall);
          _switchResult = _printJoin;
        }
      }
      if (!_matched) {
        boolean _isBroadcast = specialCall.isBroadcast();
        if (_isBroadcast) {
          _matched=true;
          CharSequence _printBroadcast = this.printBroadcast(specialCall);
          _switchResult = _printBroadcast;
        }
      }
      if (!_matched) {
        boolean _isRoundBuffer = specialCall.isRoundBuffer();
        if (_isRoundBuffer) {
          _matched=true;
          CharSequence _printRoundBuffer = this.printRoundBuffer(specialCall);
          _switchResult = _printRoundBuffer;
        }
      }
      CharSequence result = _switchResult;
      CharSequence _elvis = null;
      if (result != null) {
        _elvis = result;
      } else {
        CharSequence _printSpecialCall = this.printSpecialCall(specialCall);
        _elvis = ObjectExtensions.<CharSequence>operator_elvis(result, _printSpecialCall);
      }
      _xblockexpression = (_elvis);
    }
    return _xblockexpression;
  }
  
  public CharSequence caseSubBuffer(final SubBuffer subBuffer) {
    boolean _equals = this.state.equals(PrinterState.PRINTING_DEFINITIONS);
    if (_equals) {
      return this.printSubBufferDefinition(subBuffer);
    }
    boolean _equals_1 = this.state.equals(PrinterState.PRINTING_DECLARATIONS);
    if (_equals_1) {
      return this.printSubBufferDeclaration(subBuffer);
    }
    return this.printSubBuffer(subBuffer);
  }
  
  /**
   * Method called before printing a set of {@link Block blocks}. This method
   * can perform some printer specific modification on the blocks passed as
   * parameters. For example, it can be used to insert instrumentation
   * primitives in the code. This method will NOT print the code of the
   * {@link Block blocks}, use {@link #doSwitch()} on each {@link Block} to
   * print after the pre-processing to do so.
   * 
   * @param printerBlocks
   * 				The list of {@link Block blocks} that will be printer by the
   * 				printer
   * @param allBlocks
   * 				The list of all {@link Block blocks} printed during a workflow execution.
   * 				This list includes all printerBlocks
   */
  public abstract void preProcessing(final List<Block> printerBlocks, final List<Block> allBlocks);
  
  /**
   * This method is called after all the {@link Block blocks} have been
   * printed by the printer to give the opportunity to print secondary
   * files. (eg. project files, main files, ...).<br>
   * This method returns a {@link Map} where each {@link Entry} associates
   * a {@link String} to a {@link CharSequence} respectively corresponding
   * to a file name (including the extension) and the its content.
   * 
   * @param printerBlocks
   *   	The list of {@link Block blocks} that were printed by the
   * 		printer
   * @param allBlocks
   * 		The list of all {@link Block blocks} printed during a workflow execution.
   * 		This list includes all printerBlocks
   */
  public abstract Map<String,CharSequence> createSecondaryFiles(final List<Block> printerBlocks, final List<Block> allBlocks);
  
  /**
   * Method called to print a {@link SpecialCall} with
   * {@link SpecialCall#getType() type} {@link SpecialType#BROADCAST}. If this
   * method returns <code>null</code>, the result of
   * {@link #printSpecialCall(SpecialCall) } will be used
   * instead (in case the method is called through doSwitch).
   * 
   * @param specialCall
   *            the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBroadcast(final SpecialCall call);
  
  /**
   * Method called to print a {@link Buffer} outside the
   * {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a
   * {@link CoreBlock}
   * 
   * @param buffer
   *            the {@link Buffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBuffer(final Buffer buffer);
  
  /**
   * Method called to print a {@link Buffer} within the
   * {@link CoreBlock#getDeclarations() declaration} of a
   * {@link CoreBlock}. If a {@link Buffer} was defined in
   * the current block, it will not be declared.
   * 
   * @param buffer
   *            the {@link Buffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBufferDeclaration(final Buffer buffer);
  
  /**
   * Method called to print a {@link Buffer} within the
   * {@link CoreBlock#getDefinitions() definition} {@link LoopBlock} of a
   * {@link CoreBlock}
   * 
   * @param buffer
   *            the {@link Buffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBufferDefinition(final Buffer buffer);
  
  /**
   * Method called after printing all {@link CodeElement} belonging
   * to a {@link CallBlock}.
   * 
   * @param callBlock
   *            the {@link CallBlock} whose {@link CodeElement} were
   * 			  printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCallBlockFooter(final CallBlock callBlock);
  
  /**
   * Method called before printing all {@link CodeElement} belonging
   * to a {@link CallBlock}.
   * 
   * @param callBlock
   *            the {@link CallBlock} whose {@link CodeElement} will be
   * 			  printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCallBlockHeader(final CallBlock block);
  
  /**
   * Method called to print a {@link Communication}.
   * 
   * @param communication
   *             the printed {@link Communication}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCommunication(final Communication communication);
  
  /**
   * Method called to print a {@link Constant} outside the
   * {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a
   * {@link CoreBlock}
   * 
   * @param constant
   *            the {@link Constant} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstant(final Constant constant);
  
  /**
   * Method called to print a {@link Constant} within the
   * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
   * {@link CoreBlock}
   * 
   * @param constant
   *            the {@link Constant} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantDeclaration(final Constant constant);
  
  /**
   * Method called to print a {@link Constant} within the
   * {@link CoreBlock#getDefinitions() definition} {@link LoopBlock} of a
   * {@link CoreBlock}
   * 
   * @param constant
   *            the {@link Constant} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantDefinition(final Constant constant);
  
  /**
   * Method called to print a {@link ConstantString} outside the
   * {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a
   * {@link CoreBlock}
   * 
   * @param constant
   *            the {@link ConstantString} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantString(final ConstantString constant);
  
  /**
   * Method called to print a {@link ConstantString} within the
   * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
   * {@link CoreBlock}
   * 
   * @param constant
   *            the {@link ConstantString} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantStringDeclaration(final ConstantString constant);
  
  /**
   * Method called to print a {@link ConstantString} within the
   * {@link CoreBlock#getDefinitions() definition} {@link LoopBlock} of a
   * {@link CoreBlock}
   * 
   * @param constant
   *            the {@link ConstantString} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantStringDefinition(final ConstantString constant);
  
  /**
   * Method called after printing all code belonging
   * to a {@link CoreBlock}.
   * 
   * @param coreBlock
   *            the {@link CoreBlock} whose code was
   * 			  printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreBlockFooter(final CoreBlock coreBlock);
  
  /**
   * Method called before printing all code belonging
   * to a {@link CoreBlock}.
   * 
   * @param coreBlock
   *            the {@link CoreBlock} whose code will be
   * 			  printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreBlockHeader(final CoreBlock coreBlock);
  
  /**
   * Method called after printing all {@link CodeElement} belonging
   * to the {@link CoreBlock#getInitBlock() initBlock} {@link CallBlock} of
   * a {@link CoreBlock}.
   * 
   * @param callBlock
   *            the {@link CallBlock} whose {@link CodeElement} were
   * 			  printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreInitBlockFooter(final CallBlock callBlock);
  
  /**
   * Method called before printing all {@link CodeElement} belonging
   * to the {@link CoreBlock#getInitBlock() initBlock} {@link CallBlock} of
   * a {@link CoreBlock}.
   * 
   * @param callBlock
   *            the {@link CallBlock} whose {@link CodeElement} will be
   * 			  printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreInitBlockHeader(final CallBlock callBlock);
  
  /**
   * Method called after printing all {@link CodeElement} belonging
   * to the {@link CoreBlock#getLoopBlock() loopBlock} {@link CallBlock} of
   * a {@link CoreBlock}.
   * 
   * @param loopBlock
   *            the {@link LoopBlock} whose {@link CodeElement} were
   * 			  printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreLoopBlockFooter(final LoopBlock loopBlock);
  
  /**
   * Method called before printing all {@link CodeElement} belonging
   * to the {@link CoreBlock#getLoopBlock() loopBlock} {@link CallBlock} of
   * a {@link CoreBlock}.
   * 
   * @param loopBlock
   *            the {@link LoopBlock} whose {@link CodeElement} will be
   * 			  printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreLoopBlockHeader(final LoopBlock loopBlock);
  
  /**
   * Method called after printing all {@link Variable} belonging
   * to the {@link CoreBlock#getDeclarations() declarations} of
   * a {@link CoreBlock}.
   * 
   * @param variableList
   *            the {@link List} of {@link Variable} that were
   * 			  printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDeclarationsFooter(final List<Variable> variableList);
  
  /**
   * Method called before printing all {@link Variable} belonging
   * to the {@link CoreBlock#getDeclarations() declarations} of
   * a {@link CoreBlock}.
   * 
   * @param variableList
   *            the {@link List} of {@link Variable} that will be
   * 			  printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDeclarationsHeader(final List<Variable> variableList);
  
  /**
   * Method called after printing all {@link Variable} belonging
   * to the {@link CoreBlock#getDefinitions() definitions} of
   * a {@link CoreBlock}.
   * 
   * @param variableList
   *            the {@link List} of {@link Variable} that were
   * 			  printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDefinitionsFooter(final List<Variable> variableList);
  
  /**
   * Method called before printing all {@link Variable} belonging
   * to the {@link CoreBlock#getDefinitions() definitions} of
   * a {@link CoreBlock}.
   * 
   * @param variableList
   *            the {@link List} of {@link Variable} that will be
   * 			  printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDefinitionsHeader(final List<Variable> vaeiableList);
  
  /**
   * This method should be called when printing a "printXXXHeader" method
   * when the desired behavior is to print nothing but indent the
   * "content" of the Block (i.e. what will be printed between the header
   *  and the corresponding footer")
   * 
   * @param n
   * 		the number of indentation desired for the block content
   * 
   * @return a {@link CharSequence}
   */
  public static CharSequence printEmptyHeaderWithNIndentation(final int n) {
    final char tab = '\t';
    char[] indent = new char[n];
    int _length = indent.length;
    boolean _greaterThan = (_length > 0);
    if (_greaterThan) {
      int _length_1 = indent.length;
      int _minus = (_length_1 - 1);
      IntegerRange _upTo = new IntegerRange(0, _minus);
      for (final Integer i : _upTo) {
        indent[(i).intValue()] = tab;
      }
    }
    final char[] _converted_indent = (char[])indent;
    String _join = IterableExtensions.join(((Iterable<? extends Object>)Conversions.doWrapArray(_converted_indent)));
    String _plus = ("\r\n" + _join);
    return (_plus + "\r\n");
  }
  
  /**
   * Method called to print a {@link FifoCall}.
   * 
   * @param communication
   *             the printed {@link FifoCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFifoCall(final FifoCall fifoCall);
  
  /**
   * Method called to print a {@link SpecialCall} with
   * {@link SpecialCall#getType() type} {@link SpecialType#FORK}. If this
   * method returns <code>null</code>, the result of
   * {@link #printSpecialCall(SpecialCall) } will be used
   * instead (in case the method is called through doSwitch).
   * 
   * @param specialCall
   *            the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFork(final SpecialCall call);
  
  /**
   * Method called to print a {@link FunctionCall}.
   * 
   * @param functionCall
   *             the printed {@link FunctionCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFunctionCall(final FunctionCall functionCall);
  
  /**
   * Method called to print a {@link SpecialCall} with
   * {@link SpecialCall#getType() type} {@link SpecialType#JOIN}. If this
   * method returns <code>null</code>, the result of
   * {@link #printSpecialCall(SpecialCall) } will be used
   * instead (in case the method is called through doSwitch).
   * 
   * @param specialCall
   *            the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printJoin(final SpecialCall call);
  
  /**
   * Method called after printing all {@link CodeElement} belonging
   * to a {@link LoopBlock}.
   * 
   * @param loopBlock
   *            the {@link LoopBlock} whose {@link CodeElement} were
   * 			  printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printLoopBlockFooter(final LoopBlock loopBlock);
  
  /**
   * Method called before printing all {@link CodeElement} belonging
   * to a {@link LoopBlock}.
   * 
   * @param loopBlock
   *            the {@link LoopBlock} whose {@link CodeElement} will be
   * 			  printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printLoopBlockHeader(final LoopBlock block);
  
  /**
   * Method called to print a {@link SpecialCall} with
   * {@link SpecialCall#getType() type} {@link SpecialType#ROUND_BUFFER}. If this
   * method returns <code>null</code>, the result of
   * {@link #printSpecialCall(SpecialCall) } will be used
   * instead (in case the method is called through doSwitch).
   * 
   * @param specialCall
   *            the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printRoundBuffer(final SpecialCall call);
  
  /**
   * Method called to print a {@link Semaphore} outside the
   * {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a
   * {@link CoreBlock}
   * 
   * @param semaphore
   *            the {@link Semaphore} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSemaphore(final Semaphore semaphore);
  
  /**
   * Method called to print a {@link Semaphore} within the
   * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
   * {@link CoreBlock}
   * 
   * @param semaphore
   *            the {@link Semaphore} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSemaphoreDeclaration(final Semaphore semaphore);
  
  /**
   * Method called to print a {@link Semaphore} within the
   * {@link CoreBlock#getDefinitions() definition} {@link CallBlock} of a
   * {@link CoreBlock}
   * 
   * @param semaphore
   *            the {@link Semaphore} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSemaphoreDefinition(final Semaphore semaphore);
  
  /**
   * ethod called to print a {@link SharedMemoryCommunication}.
   * 
   * @param communication
   *             the printed {@link SharedMemoryCommunication}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSharedMemoryCommunication(final SharedMemoryCommunication communication);
  
  /**
   * Method called to print a {@link SpecialCall}.
   * 
   * @param specialCall
   *             the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSpecialCall(final SpecialCall specialCall);
  
  /**
   * Method called to print a {@link SubBuffer} outside the
   * {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a
   * {@link CoreBlock}
   * 
   * @param subBuffer
   *            the {@link SubBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSubBuffer(final SubBuffer subBuffer);
  
  /**
   * Method called to print a {@link SubBuffer} within the
   * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
   * {@link CoreBlock}
   * 
   * @param subBuffer
   *            the {@link SubBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSubBufferDeclaration(final SubBuffer subBuffer);
  
  /**
   * Method called to print a {@link SubBuffer} within the
   * {@link CoreBlock#getDefinitions() definition} {@link CallBlock} of a
   * {@link CoreBlock}
   * 
   * @param subBuffer
   *            the {@link SubBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSubBufferDefinition(final SubBuffer subBuffer);
}
