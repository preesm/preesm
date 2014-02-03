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
package org.ietr.preesm.codegen.xtend.printer.c.instrumented;

import com.google.common.base.Objects;
import java.util.Date;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Call;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.Direction;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.codegen.xtend.printer.c.instrumented.InstrumentedCPrinter;

@SuppressWarnings("all")
public class InstrumentedC6678CPrinter extends InstrumentedCPrinter {
  /**
   * This methods prints a call to the cache invalidate method for each
   * {@link PortDirection#INPUT input} {@link Buffer} of the given
   * {@link Call}.
   * 
   * @param call
   *            the {@link Call} whose {@link PortDirection#INPUT input}
   *            {@link Buffer buffers} must be invalidated.
   * @return the corresponding code.
   */
  public CharSequence printCacheCoherency(final Call call) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<Variable> _parameters = call.getParameters();
      int _size = _parameters.size();
      boolean _greaterThan = (_size > 0);
      if (_greaterThan) {
        {
          EList<Variable> _parameters_1 = call.getParameters();
          int _size_1 = _parameters_1.size();
          int _minus = (_size_1 - 1);
          IntegerRange _upTo = new IntegerRange(0, _minus);
          for(final Integer i : _upTo) {
            {
              EList<PortDirection> _parameterDirections = call.getParameterDirections();
              PortDirection _get = _parameterDirections.get((i).intValue());
              boolean _equals = Objects.equal(_get, PortDirection.INPUT);
              if (_equals) {
                _builder.append("cache_inv(");
                EList<Variable> _parameters_2 = call.getParameters();
                Variable _get_1 = _parameters_2.get((i).intValue());
                CharSequence _doSwitch = this.doSwitch(_get_1);
                _builder.append(_doSwitch, "");
                _builder.append(", ");
                EList<Variable> _parameters_3 = call.getParameters();
                Variable _get_2 = _parameters_3.get((i).intValue());
                int _size_2 = ((Buffer) _get_2).getSize();
                _builder.append(_size_2, "");
                _builder.append("*sizeof(");
                EList<Variable> _parameters_4 = call.getParameters();
                Variable _get_3 = _parameters_4.get((i).intValue());
                String _type = _get_3.getType();
                _builder.append(_type, "");
                _builder.append("));");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
    }
    return _builder;
  }
  
  public CharSequence printCoreBlockHeader(final CoreBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("/** ");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* @file ");
    String _name = block.getName();
    _builder.append(_name, " ");
    _builder.append(".c");
    _builder.newLineIfNotEmpty();
    _builder.append(" ");
    _builder.append("* @generated by ");
    Class<? extends InstrumentedC6678CPrinter> _class = this.getClass();
    String _simpleName = _class.getSimpleName();
    _builder.append(_simpleName, " ");
    _builder.newLineIfNotEmpty();
    _builder.append(" ");
    _builder.append("* @date ");
    Date _date = new Date();
    _builder.append(_date, " ");
    _builder.newLineIfNotEmpty();
    _builder.append(" ");
    _builder.append("*/");
    _builder.newLine();
    _builder.append(" \t\t ");
    _builder.newLine();
    _builder.newLine();
    _builder.append("#include \"cores.h\"");
    _builder.newLine();
    _builder.append("#include \"utils.h\"");
    _builder.newLine();
    _builder.append("#include \"communication.h\"");
    _builder.newLine();
    _builder.append("#include \"fifo.h\"");
    _builder.newLine();
    _builder.append("#include \"dump.h\"");
    _builder.newLine();
    _builder.append("#include \"cache.h\"");
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printBroadcast(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printBroadcast = super.printBroadcast(call);
    _builder.append(_printBroadcast, "");
    _builder.newLineIfNotEmpty();
    CharSequence _printCacheCoherency = this.printCacheCoherency(call);
    _builder.append(_printCacheCoherency, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printBufferDefinition(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("// Won\'t work if the shared memory is >= 512 MB ");
    _builder.newLine();
    _builder.append("#pragma DATA_SECTION(");
    String _name = buffer.getName();
    _builder.append(_name, "");
    _builder.append(", \".mySharedMem\")");
    _builder.newLineIfNotEmpty();
    CharSequence _printBufferDefinition = super.printBufferDefinition(buffer);
    _builder.append(_printBufferDefinition, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printBufferDeclaration(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("extern ");
    CharSequence _printBufferDefinition = super.printBufferDefinition(buffer);
    _builder.append(_printBufferDefinition, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printDeclarationsHeader(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("// Core Global Declaration");
    _builder.newLine();
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printCoreInitBlockHeader(final CallBlock callBlock) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("void ");
    EObject _eContainer = callBlock.eContainer();
    String _name = ((CoreBlock) _eContainer).getName();
    String _lowerCase = _name.toLowerCase();
    _builder.append(_lowerCase, "");
    _builder.append("(void){");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("// Initialisation(s)");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("communicationInit();");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printCoreLoopBlockHeader(final LoopBlock block2) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("\t", "");
    _builder.append("// Begin the execution loop ");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("while(1){");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("busy_barrier();");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printFifoCall(final FifoCall fifoCall) {
    StringConcatenation _builder = new StringConcatenation();
    {
      FifoOperation _operation = fifoCall.getOperation();
      boolean _equals = Objects.equal(_operation, FifoOperation.POP);
      if (_equals) {
        _builder.append("cache_inv(");
        Buffer _headBuffer = fifoCall.getHeadBuffer();
        CharSequence _doSwitch = this.doSwitch(_headBuffer);
        _builder.append(_doSwitch, "");
        _builder.append(", ");
        Buffer _headBuffer_1 = fifoCall.getHeadBuffer();
        int _size = _headBuffer_1.getSize();
        _builder.append(_size, "");
        _builder.append("*sizeof(");
        Buffer _headBuffer_2 = fifoCall.getHeadBuffer();
        String _type = _headBuffer_2.getType();
        _builder.append(_type, "");
        _builder.append("));");
        _builder.newLineIfNotEmpty();
        {
          Buffer _bodyBuffer = fifoCall.getBodyBuffer();
          boolean _notEquals = (!Objects.equal(_bodyBuffer, null));
          if (_notEquals) {
            _builder.append("cache_inv(");
            Buffer _bodyBuffer_1 = fifoCall.getBodyBuffer();
            CharSequence _doSwitch_1 = this.doSwitch(_bodyBuffer_1);
            _builder.append(_doSwitch_1, "");
            _builder.append(", ");
            Buffer _bodyBuffer_2 = fifoCall.getBodyBuffer();
            int _size_1 = _bodyBuffer_2.getSize();
            _builder.append(_size_1, "");
            _builder.append("*sizeof(");
            Buffer _bodyBuffer_3 = fifoCall.getBodyBuffer();
            String _type_1 = _bodyBuffer_3.getType();
            _builder.append(_type_1, "");
            _builder.append("));");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    CharSequence _printFifoCall = super.printFifoCall(fifoCall);
    _builder.append(_printFifoCall, "");
    _builder.newLineIfNotEmpty();
    {
      boolean _or = false;
      FifoOperation _operation_1 = fifoCall.getOperation();
      boolean _equals_1 = Objects.equal(_operation_1, FifoOperation.PUSH);
      if (_equals_1) {
        _or = true;
      } else {
        FifoOperation _operation_2 = fifoCall.getOperation();
        boolean _equals_2 = Objects.equal(_operation_2, FifoOperation.INIT);
        _or = (_equals_1 || _equals_2);
      }
      if (_or) {
        _builder.append("cache_wbInv(");
        Buffer _headBuffer_3 = fifoCall.getHeadBuffer();
        CharSequence _doSwitch_2 = this.doSwitch(_headBuffer_3);
        _builder.append(_doSwitch_2, "");
        _builder.append(", ");
        Buffer _headBuffer_4 = fifoCall.getHeadBuffer();
        int _size_2 = _headBuffer_4.getSize();
        _builder.append(_size_2, "");
        _builder.append("*sizeof(");
        Buffer _headBuffer_5 = fifoCall.getHeadBuffer();
        String _type_2 = _headBuffer_5.getType();
        _builder.append(_type_2, "");
        _builder.append("));");
        _builder.newLineIfNotEmpty();
        {
          Buffer _bodyBuffer_4 = fifoCall.getBodyBuffer();
          boolean _notEquals_1 = (!Objects.equal(_bodyBuffer_4, null));
          if (_notEquals_1) {
            _builder.append("cache_wbInv(");
            Buffer _bodyBuffer_5 = fifoCall.getBodyBuffer();
            CharSequence _doSwitch_3 = this.doSwitch(_bodyBuffer_5);
            _builder.append(_doSwitch_3, "");
            _builder.append(", ");
            Buffer _bodyBuffer_6 = fifoCall.getBodyBuffer();
            int _size_3 = _bodyBuffer_6.getSize();
            _builder.append(_size_3, "");
            _builder.append("*sizeof(");
            Buffer _bodyBuffer_7 = fifoCall.getBodyBuffer();
            String _type_3 = _bodyBuffer_7.getType();
            _builder.append(_type_3, "");
            _builder.append("));");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    CharSequence _printCacheCoherency = this.printCacheCoherency(fifoCall);
    _builder.append(_printCacheCoherency, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printFork(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printFork = super.printFork(call);
    _builder.append(_printFork, "");
    _builder.newLineIfNotEmpty();
    CharSequence _printCacheCoherency = this.printCacheCoherency(call);
    _builder.append(_printCacheCoherency, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printFunctionCall(final FunctionCall functionCall) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printFunctionCall = super.printFunctionCall(functionCall);
    _builder.append(_printFunctionCall, "");
    _builder.newLineIfNotEmpty();
    CharSequence _printCacheCoherency = this.printCacheCoherency(functionCall);
    _builder.append(_printCacheCoherency, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printJoin(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printJoin = super.printJoin(call);
    _builder.append(_printJoin, "");
    _builder.newLineIfNotEmpty();
    CharSequence _printCacheCoherency = this.printCacheCoherency(call);
    _builder.append(_printCacheCoherency, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printSharedMemoryCommunication(final SharedMemoryCommunication communication) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _and = false;
      Direction _direction = communication.getDirection();
      boolean _equals = Objects.equal(_direction, Direction.SEND);
      if (!_equals) {
        _and = false;
      } else {
        Delimiter _delimiter = communication.getDelimiter();
        boolean _equals_1 = Objects.equal(_delimiter, Delimiter.START);
        _and = (_equals && _equals_1);
      }
      if (_and) {
        _builder.append("cache_wbInv(");
        Buffer _data = communication.getData();
        CharSequence _doSwitch = this.doSwitch(_data);
        _builder.append(_doSwitch, "");
        _builder.append(", ");
        Buffer _data_1 = communication.getData();
        int _size = _data_1.getSize();
        _builder.append(_size, "");
        _builder.append("*sizeof(");
        Buffer _data_2 = communication.getData();
        String _type = _data_2.getType();
        _builder.append(_type, "");
        _builder.append("));");
        _builder.newLineIfNotEmpty();
      }
    }
    Direction _direction_1 = communication.getDirection();
    String _string = _direction_1.toString();
    String _lowerCase = _string.toLowerCase();
    _builder.append(_lowerCase, "");
    Delimiter _delimiter_1 = communication.getDelimiter();
    String _string_1 = _delimiter_1.toString();
    String _lowerCase_1 = _string_1.toLowerCase();
    String _firstUpper = StringExtensions.toFirstUpper(_lowerCase_1);
    _builder.append(_firstUpper, "");
    _builder.append("(");
    {
      boolean _or = false;
      boolean _and_1 = false;
      Direction _direction_2 = communication.getDirection();
      boolean _equals_2 = Objects.equal(_direction_2, Direction.SEND);
      if (!_equals_2) {
        _and_1 = false;
      } else {
        Delimiter _delimiter_2 = communication.getDelimiter();
        boolean _equals_3 = Objects.equal(_delimiter_2, Delimiter.START);
        _and_1 = (_equals_2 && _equals_3);
      }
      if (_and_1) {
        _or = true;
      } else {
        boolean _and_2 = false;
        Direction _direction_3 = communication.getDirection();
        boolean _equals_4 = Objects.equal(_direction_3, Direction.RECEIVE);
        if (!_equals_4) {
          _and_2 = false;
        } else {
          Delimiter _delimiter_3 = communication.getDelimiter();
          boolean _equals_5 = Objects.equal(_delimiter_3, Delimiter.END);
          _and_2 = (_equals_4 && _equals_5);
        }
        _or = (_and_1 || _and_2);
      }
      if (_or) {
        char _xblockexpression = (char) 0;
        {
          String _xifexpression = null;
          Direction _direction_4 = communication.getDirection();
          boolean _equals_6 = Objects.equal(_direction_4, Direction.SEND);
          if (_equals_6) {
            Communication _receiveStart = communication.getReceiveStart();
            CoreBlock _coreContainer = _receiveStart.getCoreContainer();
            String _name = _coreContainer.getName();
            _xifexpression = _name;
          } else {
            Communication _sendStart = communication.getSendStart();
            CoreBlock _coreContainer_1 = _sendStart.getCoreContainer();
            String _name_1 = _coreContainer_1.getName();
            _xifexpression = _name_1;
          }
          String coreName = _xifexpression;
          int _length = coreName.length();
          int _minus = (_length - 1);
          char _charAt = coreName.charAt(_minus);
          _xblockexpression = (_charAt);
        }
        _builder.append(_xblockexpression, "");
      }
    }
    _builder.append("); // ");
    Communication _sendStart = communication.getSendStart();
    CoreBlock _coreContainer = _sendStart.getCoreContainer();
    String _name = _coreContainer.getName();
    _builder.append(_name, "");
    _builder.append(" > ");
    Communication _receiveStart = communication.getReceiveStart();
    CoreBlock _coreContainer_1 = _receiveStart.getCoreContainer();
    String _name_1 = _coreContainer_1.getName();
    _builder.append(_name_1, "");
    _builder.append(": ");
    Buffer _data_3 = communication.getData();
    CharSequence _doSwitch_1 = this.doSwitch(_data_3);
    _builder.append(_doSwitch_1, "");
    _builder.append(" ");
    _builder.newLineIfNotEmpty();
    {
      boolean _and_3 = false;
      Direction _direction_4 = communication.getDirection();
      boolean _equals_6 = Objects.equal(_direction_4, Direction.RECEIVE);
      if (!_equals_6) {
        _and_3 = false;
      } else {
        Delimiter _delimiter_4 = communication.getDelimiter();
        boolean _equals_7 = Objects.equal(_delimiter_4, Delimiter.END);
        _and_3 = (_equals_6 && _equals_7);
      }
      if (_and_3) {
        _builder.append("cache_inv(");
        Buffer _data_4 = communication.getData();
        CharSequence _doSwitch_2 = this.doSwitch(_data_4);
        _builder.append(_doSwitch_2, "");
        _builder.append(", ");
        Buffer _data_5 = communication.getData();
        int _size_1 = _data_5.getSize();
        _builder.append(_size_1, "");
        _builder.append("*sizeof(");
        Buffer _data_6 = communication.getData();
        String _type_1 = _data_6.getType();
        _builder.append(_type_1, "");
        _builder.append("));");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  public CharSequence printSemaphoreDeclaration(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    return _builder;
  }
  
  public CharSequence printSemaphoreDefinition(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    return _builder;
  }
  
  public CharSequence printSemaphore(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    return _builder;
  }
  
  public void preProcessing(final List<Block> printerBlocks, final List<Block> allBlocks) {
    super.preProcessing(printerBlocks, allBlocks);
    this.dumpTimedBuffer.setType("int");
    for (final Block block : printerBlocks) {
      CallBlock _initBlock = ((CoreBlock) block).getInitBlock();
      EList<CodeElt> _codeElts = _initBlock.getCodeElts();
      CallBlock _initBlock_1 = ((CoreBlock) block).getInitBlock();
      EList<CodeElt> _codeElts_1 = _initBlock_1.getCodeElts();
      final Function1<CodeElt,Boolean> _function = new Function1<CodeElt,Boolean>() {
          public Boolean apply(final CodeElt it) {
            boolean _and = false;
            if (!(it instanceof FunctionCall)) {
              _and = false;
            } else {
              String _name = ((FunctionCall) it).getName();
              boolean _startsWith = _name.startsWith("sem_init");
              _and = ((it instanceof FunctionCall) && _startsWith);
            }
            return Boolean.valueOf(_and);
          }
        };
      Iterable<CodeElt> _filter = IterableExtensions.<CodeElt>filter(_codeElts_1, _function);
      CollectionExtensions.<CodeElt>removeAll(_codeElts, _filter);
    }
  }
  
  public CharSequence printCoreLoopBlockFooter(final LoopBlock block2) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\t\t");
    _builder.append("busy_barrier();");
    _builder.newLine();
    {
      Block _creator = this.dumpTimedBuffer.getCreator();
      EObject _eContainer = block2.eContainer();
      boolean _equals = Objects.equal(_creator, _eContainer);
      if (_equals) {
        _builder.append("\t\t");
        _builder.append("writeTime(");
        CharSequence _doSwitch = this.doSwitch(this.dumpTimedBuffer);
        _builder.append(_doSwitch, "		");
        _builder.append(",");
        Constant _xblockexpression = null;
        {
          final Constant const_ = CodegenFactory.eINSTANCE.createConstant();
          const_.setName("nbDump");
          const_.setType("int");
          int _size = this.dumpTimedBuffer.getSize();
          const_.setValue(_size);
          _xblockexpression = (const_);
        }
        CharSequence _doSwitch_1 = this.doSwitch(_xblockexpression);
        _builder.append(_doSwitch_1, "		");
        _builder.append(", ");
        CharSequence _doSwitch_2 = this.doSwitch(this.nbExec);
        _builder.append(_doSwitch_2, "		");
        _builder.append(");");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printDefinitionsFooter(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = list.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.newLine();
      }
    }
    return _builder;
  }
  
  public CharSequence printInstrumentedCall(final CodeElt elt, final CharSequence superPrint) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(superPrint, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printMemcpy(final Buffer output, final int outOffset, final Buffer input, final int inOffset, final int size, final String type) {
    String _xblockexpression = null;
    {
      CharSequence _printMemcpy = super.printMemcpy(output, outOffset, input, inOffset, size, type);
      String result = _printMemcpy.toString();
      String regex = "(memcpy\\()(.*?)[,](.*?)[,](.*?[;])";
      String _replaceAll = result.replaceAll(regex, "$1(void*)($2),(void*)($3),$4");
      result = _replaceAll;
      boolean _isEmpty = result.isEmpty();
      if (_isEmpty) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("cache_wb(");
        CharSequence _doSwitch = this.doSwitch(input);
        _builder.append(_doSwitch, "");
        _builder.append(", ");
        int _size = input.getSize();
        _builder.append(_size, "");
        _builder.append("*sizeof(");
        String _type = input.getType();
        _builder.append(_type, "");
        _builder.append("));");
        result = _builder.toString();
      }
      _xblockexpression = (result);
    }
    return _xblockexpression;
  }
  
  public CharSequence printRoundBuffer(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printRoundBuffer = super.printRoundBuffer(call);
    _builder.append(_printRoundBuffer, "");
    _builder.newLineIfNotEmpty();
    CharSequence _printCacheCoherency = this.printCacheCoherency(call);
    _builder.append(_printCacheCoherency, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
}
