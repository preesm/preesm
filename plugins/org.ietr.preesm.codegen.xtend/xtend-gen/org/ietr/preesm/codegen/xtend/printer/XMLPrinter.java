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

import com.google.common.base.Objects;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.Direction;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.codegen.xtend.printer.DefaultPrinter;

@SuppressWarnings("all")
public class XMLPrinter extends DefaultPrinter {
  public CharSequence printBroadcast(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<CompoundCode name=\"");
    String _name = call.getName();
    _builder.append(_name, "");
    _builder.append("\">");
    EList<Buffer> _inputBuffers = call.getInputBuffers();
    Buffer input = IterableExtensions.<Buffer>head(_inputBuffers);
    int index = 0;
    _builder.newLineIfNotEmpty();
    {
      EList<Buffer> _outputBuffers = call.getOutputBuffers();
      for(final Buffer output : _outputBuffers) {
        int outputIdx = 0;
        _builder.newLineIfNotEmpty();
        {
          int _size = output.getSize();
          int _size_1 = input.getSize();
          int _divide = (_size / _size_1);
          int _plus = (_divide + 1);
          IntegerRange _upTo = new IntegerRange(0, _plus);
          for(final Integer nbIter : _upTo) {
            {
              int _size_2 = output.getSize();
              boolean _lessThan = (outputIdx < _size_2);
              if (_lessThan) {
                _builder.append("\t");
                _builder.append("<userFunctionCall comment=\"\" name=\"memcpy\">");
                _builder.newLine();
                _builder.append("\t");
                _builder.append("\t");
                _builder.append("<bufferAtIndex index=\"");
                _builder.append(outputIdx, "		");
                _builder.append("\" name=\"");
                String _name_1 = output.getName();
                _builder.append(_name_1, "		");
                _builder.append("\"/>");
                _builder.newLineIfNotEmpty();
                _builder.append("\t");
                _builder.append("\t");
                _builder.append("<bufferAtIndex index=\"");
                _builder.append(index, "		");
                _builder.append("\" name=\"");
                String _name_2 = input.getName();
                _builder.append(_name_2, "		");
                _builder.append("\"/>");
                _builder.newLineIfNotEmpty();
                _builder.append("\t");
                _builder.append("\t");
                _builder.append("<constant name=\"size\" type=\"string\" value=\"");
                int _size_3 = output.getSize();
                int _minus = (_size_3 - outputIdx);
                int _size_4 = input.getSize();
                int _minus_1 = (_size_4 - index);
                final int value = Math.min(_minus, _minus_1);
                _builder.append(value, "		");
                _builder.append("*sizeof(");
                String _type = output.getType();
                _builder.append(_type, "		");
                _builder.append(")\"/>");
                _builder.newLineIfNotEmpty();
                _builder.append("\t");
                _builder.append("</userFunctionCall>");
                String _xblockexpression = null;
                {
                  int _plus_1 = (index + value);
                  int _size_5 = input.getSize();
                  int _modulo = (_plus_1 % _size_5);
                  index = _modulo;
                  int _plus_2 = (outputIdx + value);
                  outputIdx = _plus_2;
                  _xblockexpression = ("");
                }
                _builder.append(_xblockexpression, "	");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
    }
    _builder.append("\t");
    _builder.append("</CompoundCode>");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printBuffer(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<buffer name=\"");
    String _name = buffer.getName();
    _builder.append(_name, "");
    _builder.append("\" size=\"");
    int _size = buffer.getSize();
    _builder.append(_size, "");
    _builder.append("\" type=\"");
    String _type = buffer.getType();
    _builder.append(_type, "");
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printBufferDeclaration(final Buffer buffer) {
    CharSequence _printBufferDefinition = this.printBufferDefinition(buffer);
    return _printBufferDefinition;
  }
  
  public CharSequence printBufferDefinition(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<bufferAllocation");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("comment=\"");
    Class<? extends Buffer> _class = buffer.getClass();
    String _simpleName = _class.getSimpleName();
    _builder.append(_simpleName, "	");
    _builder.append(": ");
    String _comment = buffer.getComment();
    _builder.append(_comment, "	");
    _builder.append("\"");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("name=\"");
    String _name = buffer.getName();
    _builder.append(_name, "	");
    _builder.append("\" size=\"");
    int _size = buffer.getSize();
    _builder.append(_size, "	");
    _builder.append("\" type=\"");
    String _type = buffer.getType();
    _builder.append(_type, "	");
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printConstant(final Constant constant) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<constant name=\"");
    String _name = constant.getName();
    _builder.append(_name, "");
    _builder.append("\" type=\"");
    String _type = constant.getType();
    _builder.append(_type, "");
    _builder.append("\" value=\"");
    long _value = constant.getValue();
    _builder.append(_value, "");
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printCoreBlockHeader(final CoreBlock coreBlock) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    _builder.newLine();
    _builder.append("<sourceCode xmlns=\"http://org.ietr.preesm.sourceCode\">");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("<coreType>");
    String _coreType = coreBlock.getCoreType();
    _builder.append(_coreType, "	");
    _builder.append("</coreType>");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("<coreName>");
    String _name = coreBlock.getName();
    _builder.append(_name, "	");
    _builder.append("</coreName>");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("<SourceFile>");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printCoreBlockFooter(final CoreBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\t");
    _builder.append("</SourceFile>");
    _builder.newLine();
    _builder.append("</sourceCode>");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printCoreInitBlockHeader(final CallBlock callBlock) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<threadDeclaration name=\"computationThread\">");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("<bufferContainer/>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("<linearCodeContainer comment=\"COMINIT\"/>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("<linearCodeContainer comment=\"Fifo Initialization Section\"/>");
    _builder.newLine();
    {
      EList<CodeElt> _codeElts = callBlock.getCodeElts();
      boolean _isEmpty = _codeElts.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("\t");
        _builder.append("<linearCodeContainer comment=\"Initialization phase number 0\">");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("\t");
        _builder.newLine();
      } else {
        _builder.append("\t");
        _builder.append("<linearCodeContainer comment=\"Initialization phase number 0\"/>");
        _builder.newLine();
      }
    }
    return _builder;
  }
  
  public CharSequence printCoreInitBlockFooter(final CallBlock callBlock) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<CodeElt> _codeElts = callBlock.getCodeElts();
      boolean _isEmpty = _codeElts.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("\t", "");
        _builder.append("</linearCodeContainer>");
        _builder.newLineIfNotEmpty();
      } else {
        _builder.append("\t");
        _builder.newLine();
      }
    }
    return _builder;
  }
  
  public CharSequence printCoreLoopBlockHeader(final LoopBlock loopBlock) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\t", "");
    _builder.append("<forLoop comment=\"Main loop of computation\">");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printCoreLoopBlockFooter(final LoopBlock loopBlock) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\t");
    _builder.append("</forLoop>");
    _builder.newLine();
    _builder.append("</threadDeclaration>");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printDeclarationsHeader(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<bufferContainer>");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printDefinitionsHeader(final List<Variable> list) {
    return "\t\r\n";
  }
  
  public CharSequence printDefinitionsFooter(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("</bufferContainer>");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printFunctionCall(final FunctionCall functionCall) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<userFunctionCall comment=\"");
    String _actorName = functionCall.getActorName();
    _builder.append(_actorName, "");
    _builder.append("\" name=\"");
    String _name = functionCall.getName();
    _builder.append(_name, "");
    _builder.append("\">");
    _builder.newLineIfNotEmpty();
    {
      EList<Variable> _parameters = functionCall.getParameters();
      for(final Variable param : _parameters) {
        _builder.append("\t");
        CharSequence _doSwitch = this.doSwitch(param);
        _builder.append(_doSwitch, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</userFunctionCall>");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printCommunication(final Communication communication) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    {
      Direction _direction = communication.getDirection();
      boolean _equals = Objects.equal(_direction, Direction.SEND);
      if (_equals) {
        _builder.append("send");
      } else {
        _builder.append("receive");
      }
    }
    _builder.append("Msg ID=\"");
    int _id = communication.getId();
    _builder.append(_id, "");
    _builder.append("\" comment=\"");
    String _name = communication.getName();
    _builder.append(_name, "");
    _builder.append("\"");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("phase=\"");
    Delimiter _delimiter = communication.getDelimiter();
    _builder.append(_delimiter, "	");
    _builder.append("\" ");
    {
      Direction _direction_1 = communication.getDirection();
      boolean _equals_1 = Objects.equal(_direction_1, Direction.SEND);
      if (_equals_1) {
        _builder.append("target=\"");
        Communication _receiveStart = communication.getReceiveStart();
        CoreBlock _coreContainer = _receiveStart.getCoreContainer();
        String _name_1 = _coreContainer.getName();
        _builder.append(_name_1, "	");
        _builder.append("\"");
      } else {
        _builder.append("source=\"");
        Communication _sendStart = communication.getSendStart();
        CoreBlock _coreContainer_1 = _sendStart.getCoreContainer();
        String _name_2 = _coreContainer_1.getName();
        _builder.append(_name_2, "	");
        _builder.append("\"");
      }
    }
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("<routeStep type\"msg\">");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("<sender def=\"");
    Communication _sendStart_1 = communication.getSendStart();
    CoreBlock _coreContainer_2 = _sendStart_1.getCoreContainer();
    String _coreType = _coreContainer_2.getCoreType();
    _builder.append(_coreType, "		");
    _builder.append("\" name=\"");
    Communication _sendStart_2 = communication.getSendStart();
    CoreBlock _coreContainer_3 = _sendStart_2.getCoreContainer();
    String _name_3 = _coreContainer_3.getName();
    _builder.append(_name_3, "		");
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("<receiver def=\"");
    Communication _receiveStart_1 = communication.getReceiveStart();
    CoreBlock _coreContainer_4 = _receiveStart_1.getCoreContainer();
    String _coreType_1 = _coreContainer_4.getCoreType();
    _builder.append(_coreType_1, "		");
    _builder.append("\" name=\"");
    Communication _receiveStart_2 = communication.getReceiveStart();
    CoreBlock _coreContainer_5 = _receiveStart_2.getCoreContainer();
    String _name_4 = _coreContainer_5.getName();
    _builder.append(_name_4, "		");
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    {
      EList<CommunicationNode> _nodes = communication.getNodes();
      for(final CommunicationNode node : _nodes) {
        _builder.append("\t\t");
        _builder.append("<node def=\"");
        String _type = node.getType();
        _builder.append(_type, "		");
        _builder.append("\" name=\"");
        String _name_5 = node.getName();
        _builder.append(_name_5, "		");
        _builder.append("\"/>");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t");
    _builder.append("</routeStep>");
    _builder.newLine();
    _builder.append("\t");
    Buffer _data = communication.getData();
    CharSequence _doSwitch = this.doSwitch(_data);
    _builder.append(_doSwitch, "	");
    _builder.newLineIfNotEmpty();
    _builder.append("</");
    {
      Direction _direction_2 = communication.getDirection();
      boolean _equals_2 = Objects.equal(_direction_2, Direction.SEND);
      if (_equals_2) {
        _builder.append("send");
      } else {
        _builder.append("receive");
      }
    }
    _builder.append("Msg>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printFifoCall(final FifoCall fifoCall) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<userFunctionCall comment=\"");
    String _name = fifoCall.getName();
    _builder.append(_name, "");
    _builder.append("\" name=\"");
    String _switchResult = null;
    FifoOperation _operation = fifoCall.getOperation();
    final FifoOperation _switchValue = _operation;
    boolean _matched = false;
    if (!_matched) {
      if (Objects.equal(_switchValue,FifoOperation.POP)) {
        _matched=true;
        _switchResult = "pull";
      }
    }
    if (!_matched) {
      if (Objects.equal(_switchValue,FifoOperation.INIT)) {
        _matched=true;
        _switchResult = "new_fifo";
      }
    }
    if (!_matched) {
      if (Objects.equal(_switchValue,FifoOperation.PUSH)) {
        _matched=true;
        _switchResult = "push";
      }
    }
    _builder.append(_switchResult, "");
    _builder.append("\">");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("<variable name=\"&amp;");
    Buffer _headBuffer = fifoCall.getHeadBuffer();
    String _name_1 = _headBuffer.getName();
    _builder.append(_name_1, "	");
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    {
      Buffer _bodyBuffer = fifoCall.getBodyBuffer();
      boolean _notEquals = (!Objects.equal(_bodyBuffer, null));
      if (_notEquals) {
        _builder.append("\t");
        _builder.append("<variable name=\"&amp;");
        Buffer _bodyBuffer_1 = fifoCall.getBodyBuffer();
        String _name_2 = _bodyBuffer_1.getName();
        _builder.append(_name_2, "	");
        _builder.append("\"/>");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      FifoOperation _operation_1 = fifoCall.getOperation();
      boolean _notEquals_1 = (!Objects.equal(_operation_1, FifoOperation.INIT));
      if (_notEquals_1) {
        _builder.append("\t");
        EList<Variable> _parameters = fifoCall.getParameters();
        Variable _head = IterableExtensions.<Variable>head(_parameters);
        CharSequence _doSwitch = this.doSwitch(_head);
        _builder.append(_doSwitch, "	");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("<constant name=\"nb_token\" type=\"int\" value=\"");
        EList<Variable> _parameters_1 = fifoCall.getParameters();
        Variable _head_1 = IterableExtensions.<Variable>head(_parameters_1);
        int _size = ((Buffer) _head_1).getSize();
        _builder.append(_size, "	");
        _builder.append("\"/>");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t");
    _builder.append("<constant name=\"size\" type=\"string\" value=\"sizeof(");
    Buffer _headBuffer_1 = fifoCall.getHeadBuffer();
    String _type = _headBuffer_1.getType();
    _builder.append(_type, "	");
    _builder.append(")\"/>");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    Constant _xblockexpression = null;
    {
      Constant const_ = CodegenFactory.eINSTANCE.createConstant();
      const_.setName("head_size");
      const_.setType("int");
      Buffer _headBuffer_2 = fifoCall.getHeadBuffer();
      int _size_1 = _headBuffer_2.getSize();
      const_.setValue(_size_1);
      _xblockexpression = (const_);
    }
    CharSequence _doSwitch_1 = this.doSwitch(_xblockexpression);
    _builder.append(_doSwitch_1, "	");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    Constant _xblockexpression_1 = null;
    {
      Constant const_ = CodegenFactory.eINSTANCE.createConstant();
      const_.setName("fifo_size");
      const_.setType("int");
      Buffer _headBuffer_2 = fifoCall.getHeadBuffer();
      int _size_1 = _headBuffer_2.getSize();
      int _xifexpression = (int) 0;
      Buffer _bodyBuffer_2 = fifoCall.getBodyBuffer();
      boolean _equals = Objects.equal(_bodyBuffer_2, null);
      if (_equals) {
        _xifexpression = 0;
      } else {
        Buffer _bodyBuffer_3 = fifoCall.getBodyBuffer();
        int _size_2 = _bodyBuffer_3.getSize();
        _xifexpression = _size_2;
      }
      int _plus = (_size_1 + _xifexpression);
      const_.setValue(_plus);
      _xblockexpression_1 = (const_);
    }
    CharSequence _doSwitch_2 = this.doSwitch(_xblockexpression_1);
    _builder.append(_doSwitch_2, "	");
    _builder.newLineIfNotEmpty();
    _builder.append("</userFunctionCall>");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printFork(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<CompoundCode name=\"");
    String _name = call.getName();
    _builder.append(_name, "");
    _builder.append("\">");
    EList<Buffer> _inputBuffers = call.getInputBuffers();
    Buffer input = IterableExtensions.<Buffer>head(_inputBuffers);
    int index = 0;
    _builder.newLineIfNotEmpty();
    {
      EList<Buffer> _outputBuffers = call.getOutputBuffers();
      for(final Buffer output : _outputBuffers) {
        _builder.append("\t");
        _builder.append("<userFunctionCall comment=\"\" name=\"memcpy\">");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("\t");
        CharSequence _doSwitch = this.doSwitch(output);
        _builder.append(_doSwitch, "		");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("\t");
        _builder.append("<bufferAtIndex index=\"");
        _builder.append(index, "		");
        _builder.append("\" name=\"");
        String _name_1 = input.getName();
        _builder.append(_name_1, "		");
        _builder.append("\"/>");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("\t");
        _builder.append("<constant name=\"size\" type=\"string\" value=\"");
        int _size = output.getSize();
        _builder.append(_size, "		");
        _builder.append("*sizeof(");
        String _type = output.getType();
        _builder.append(_type, "		");
        _builder.append(")\"/>");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("</userFunctionCall>");
        String _xblockexpression = null;
        {
          int _size_1 = output.getSize();
          int _plus = (index + _size_1);
          index = _plus;
          _xblockexpression = ("");
        }
        _builder.append(_xblockexpression, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</CompoundCode>\t");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printJoin(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<CompoundCode name=\"");
    String _name = call.getName();
    _builder.append(_name, "");
    _builder.append("\">");
    EList<Buffer> _outputBuffers = call.getOutputBuffers();
    Buffer output = IterableExtensions.<Buffer>head(_outputBuffers);
    int index = 0;
    _builder.newLineIfNotEmpty();
    {
      EList<Buffer> _inputBuffers = call.getInputBuffers();
      for(final Buffer input : _inputBuffers) {
        _builder.append("\t");
        _builder.append("<userFunctionCall comment=\"\" name=\"memcpy\">");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("\t");
        _builder.append("<bufferAtIndex index=\"");
        _builder.append(index, "		");
        _builder.append("\" name=\"");
        String _name_1 = output.getName();
        _builder.append(_name_1, "		");
        _builder.append("\"/>");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("\t");
        CharSequence _doSwitch = this.doSwitch(input);
        _builder.append(_doSwitch, "		");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("\t");
        _builder.append("<constant name=\"size\" type=\"string\" value=\"");
        int _size = input.getSize();
        _builder.append(_size, "		");
        _builder.append("*sizeof(");
        String _type = input.getType();
        _builder.append(_type, "		");
        _builder.append(")\"/>");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("</userFunctionCall>");
        String _xblockexpression = null;
        {
          int _size_1 = input.getSize();
          int _plus = (index + _size_1);
          index = _plus;
          _xblockexpression = ("");
        }
        _builder.append(_xblockexpression, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t\t");
    _builder.append("</CompoundCode>\t");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printRoundBuffer(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<CompoundCode name=\"");
    String _name = call.getName();
    _builder.append(_name, "");
    _builder.append("\">");
    EList<Buffer> _outputBuffers = call.getOutputBuffers();
    Buffer output = IterableExtensions.<Buffer>head(_outputBuffers);
    int index = 0;
    _builder.newLineIfNotEmpty();
    {
      EList<Buffer> _inputBuffers = call.getInputBuffers();
      for(final Buffer buffer : _inputBuffers) {
        int inputIdx = 0;
        _builder.newLineIfNotEmpty();
        {
          int _size = buffer.getSize();
          int _size_1 = output.getSize();
          int _divide = (_size / _size_1);
          int _plus = (_divide + 1);
          IntegerRange _upTo = new IntegerRange(0, _plus);
          for(final Integer nbIter : _upTo) {
            {
              int _size_2 = buffer.getSize();
              boolean _lessThan = (inputIdx < _size_2);
              if (_lessThan) {
                _builder.append("\t");
                _builder.append("<userFunctionCall comment=\"\" name=\"memcpy\">");
                _builder.newLine();
                _builder.append("\t");
                _builder.append("\t");
                _builder.append("<bufferAtIndex index=\"");
                _builder.append(index, "		");
                _builder.append("\" name=\"");
                String _name_1 = output.getName();
                _builder.append(_name_1, "		");
                _builder.append("\"/>");
                _builder.newLineIfNotEmpty();
                _builder.append("\t");
                _builder.append("\t");
                _builder.append("<bufferAtIndex index=\"");
                _builder.append(inputIdx, "		");
                _builder.append("\" name=\"");
                String _name_2 = buffer.getName();
                _builder.append(_name_2, "		");
                _builder.append("\"/>");
                _builder.newLineIfNotEmpty();
                _builder.append("\t");
                _builder.append("\t");
                _builder.append("<constant name=\"size\" type=\"string\" value=\"");
                int _size_3 = buffer.getSize();
                int _minus = (_size_3 - inputIdx);
                int _size_4 = output.getSize();
                int _minus_1 = (_size_4 - index);
                final int value = Math.min(_minus, _minus_1);
                _builder.append(value, "		");
                _builder.append("*sizeof(");
                String _type = buffer.getType();
                _builder.append(_type, "		");
                _builder.append(")\"/>");
                _builder.newLineIfNotEmpty();
                _builder.append("\t");
                _builder.append("</userFunctionCall>");
                String _xblockexpression = null;
                {
                  int _plus_1 = (index + value);
                  int _size_5 = output.getSize();
                  int _modulo = (_plus_1 % _size_5);
                  index = _modulo;
                  int _plus_2 = (inputIdx + value);
                  inputIdx = _plus_2;
                  _xblockexpression = ("");
                }
                _builder.append(_xblockexpression, "	");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
    }
    _builder.append("\t");
    _builder.append("</CompoundCode>");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printSubBuffer(final SubBuffer subBuffer) {
    CharSequence _printBuffer = this.printBuffer(subBuffer);
    return _printBuffer;
  }
  
  public CharSequence printSubBufferDeclaration(final SubBuffer buffer) {
    CharSequence _printBufferDefinition = this.printBufferDefinition(buffer);
    return _printBufferDefinition;
  }
  
  public CharSequence printSubBufferDefinition(final SubBuffer buffer) {
    CharSequence _printBufferDefinition = this.printBufferDefinition(buffer);
    return _printBufferDefinition;
  }
}
