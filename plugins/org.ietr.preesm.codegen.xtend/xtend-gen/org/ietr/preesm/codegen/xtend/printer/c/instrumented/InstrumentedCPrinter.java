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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.Direction;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.codegen.xtend.printer.PrinterState;
import org.ietr.preesm.codegen.xtend.printer.c.CPrinter;

/**
 * This printer currently prints instrumented C code for X86 cores with all
 * communications made in the shared memory.
 * 
 * @author kdesnos
 */
@SuppressWarnings("all")
public class InstrumentedCPrinter extends CPrinter {
  /**
   * Buffer storing the timing dumped by the actors
   */
  private Buffer dumpTimedBuffer;
  
  /**
   * Buffer storing the number of execution of the actors
   */
  private Buffer nbExec;
  
  /**
   * This map associates each codeElt to its ID
   */
  private HashMap<CodeElt,Integer> codeEltID = new Function0<HashMap<CodeElt,Integer>>() {
    public HashMap<CodeElt,Integer> apply() {
      HashMap<CodeElt,Integer> _hashMap = new HashMap<CodeElt,Integer>();
      return _hashMap;
    }
  }.apply();
  
  /**
   * Map associating actor names to their different IDs
   */
  private HashMap<String,List<Integer>> actorIDs = new Function0<HashMap<String,List<Integer>>>() {
    public HashMap<String,List<Integer>> apply() {
      HashMap<String,List<Integer>> _hashMap = new HashMap<String,List<Integer>>();
      return _hashMap;
    }
  }.apply();
  
  /**
   * Add instrumentation code to the {@link Block blocks}.<br>
   * In the current version, the instrumentation consists of:<br>
   * - A shared {@link Buffer} that stores all measured durations.<br>
   * - Calls to <code>dumpTime(ID, Buffer)</code> between all actors.<br>
   * 
   * @param blocks
   * 			List of the blocks printed by the printer. (will be
   * 			modified)
   */
  public void preProcessing(final List<Block> blocks) {
    super.preProcessing(blocks);
    Buffer _createBuffer = CodegenFactory.eINSTANCE.createBuffer();
    this.dumpTimedBuffer = _createBuffer;
    this.dumpTimedBuffer.setName("dumpedTimes");
    this.dumpTimedBuffer.setType("long");
    Buffer _createBuffer_1 = CodegenFactory.eINSTANCE.createBuffer();
    this.nbExec = _createBuffer_1;
    this.nbExec.setName("nbExec");
    this.nbExec.setType("int");
    int globalID = 0;
    HashMap<Integer,String> _hashMap = new HashMap<Integer,String>();
    HashMap<Integer,String> globalFunctionID = _hashMap;
    for (final Block block : blocks) {
      {
        Block _creator = this.dumpTimedBuffer.getCreator();
        boolean _equals = Objects.equal(_creator, null);
        if (_equals) {
          this.dumpTimedBuffer.setCreator(block);
          this.nbExec.setCreator(block);
        }
        EList<Block> _users = this.dumpTimedBuffer.getUsers();
        _users.add(block);
        EList<Block> _users_1 = this.nbExec.getUsers();
        _users_1.add(block);
        LoopBlock coreLoop = ((CoreBlock) block).getLoopBlock();
        {
          FunctionCall dumpCall = CodegenFactory.eINSTANCE.createFunctionCall();
          dumpCall.setName("dumpTime");
          Constant _xblockexpression = null;
          {
            Constant const_ = CodegenFactory.eINSTANCE.createConstant();
            const_.setName("globalID");
            const_.setType("int");
            const_.setValue(globalID);
            _xblockexpression = (const_);
          }
          dumpCall.addParameter(_xblockexpression);
          int _plus = (globalID + 1);
          globalID = _plus;
          dumpCall.addParameter(this.dumpTimedBuffer);
          EList<CodeElt> _codeElts = coreLoop.getCodeElts();
          _codeElts.add(0, dumpCall);
        }
        int i = 1;
        EList<CodeElt> _codeElts = coreLoop.getCodeElts();
        int _size = _codeElts.size();
        boolean _lessThan = (i < _size);
        boolean _while = _lessThan;
        while (_while) {
          {
            final FunctionCall dumpCall = CodegenFactory.eINSTANCE.createFunctionCall();
            dumpCall.setName("dumpTime");
            Constant _xblockexpression = null;
            {
              final Constant const_ = CodegenFactory.eINSTANCE.createConstant();
              const_.setName("globalID");
              const_.setType("int");
              const_.setValue(globalID);
              _xblockexpression = (const_);
            }
            dumpCall.addParameter(_xblockexpression);
            dumpCall.addParameter(this.dumpTimedBuffer);
            EList<CodeElt> _codeElts_1 = coreLoop.getCodeElts();
            int _plus = (i + 1);
            _codeElts_1.add(_plus, dumpCall);
            EList<CodeElt> _codeElts_2 = coreLoop.getCodeElts();
            final CodeElt elt = _codeElts_2.get(i);
            String _switchResult = null;
            boolean _matched = false;
            if (!_matched) {
              if (elt instanceof FunctionCall) {
                final FunctionCall _functionCall = (FunctionCall)elt;
                if (true) {
                  _matched=true;
                  String _name = _functionCall.getName();
                  _switchResult = _name;
                }
              }
            }
            if (!_matched) {
              if (elt instanceof SpecialCall) {
                final SpecialCall _specialCall = (SpecialCall)elt;
                if (true) {
                  _matched=true;
                  String _name = _specialCall.getName();
                  _switchResult = _name;
                }
              }
            }
            if (!_matched) {
              if (elt instanceof SharedMemoryCommunication) {
                final SharedMemoryCommunication _sharedMemoryCommunication = (SharedMemoryCommunication)elt;
                if (true) {
                  _matched=true;
                  Direction _direction = _sharedMemoryCommunication.getDirection();
                  String _string = _direction.toString();
                  String _lowerCase = _string.toLowerCase();
                  Delimiter _delimiter = _sharedMemoryCommunication.getDelimiter();
                  String _string_1 = _delimiter.toString();
                  String _lowerCase_1 = _string_1.toLowerCase();
                  String _firstUpper = StringExtensions.toFirstUpper(_lowerCase_1);
                  String _plus_1 = (_lowerCase + _firstUpper);
                  Buffer _data = _sharedMemoryCommunication.getData();
                  String _name = _data.getName();
                  String _plus_2 = (_plus_1 + _name);
                  _switchResult = _plus_2;
                }
              }
            }
            if (!_matched) {
              if (elt instanceof FifoCall) {
                final FifoCall _fifoCall = (FifoCall)elt;
                if (true) {
                  _matched=true;
                  String _name = _fifoCall.getName();
                  _switchResult = _name;
                }
              }
            }
            if (!_matched) {
              _switchResult = "undefined";
            }
            final String functionID = _switchResult;
            globalFunctionID.put(Integer.valueOf(globalID), functionID);
            List<Integer> _elvis = null;
            List<Integer> _get = this.actorIDs.get(functionID);
            if (_get != null) {
              _elvis = _get;
            } else {
              List<Integer> _xblockexpression_1 = null;
              {
                ArrayList<Integer> _arrayList = new ArrayList<Integer>();
                this.actorIDs.put(functionID, _arrayList);
                List<Integer> _get_1 = this.actorIDs.get(functionID);
                _xblockexpression_1 = (_get_1);
              }
              _elvis = ObjectExtensions.<List<Integer>>operator_elvis(_get, _xblockexpression_1);
            }
            List<Integer> actorID = _elvis;
            actorID.add(Integer.valueOf(globalID));
            this.codeEltID.put(elt, Integer.valueOf(globalID));
            int _plus_1 = (globalID + 1);
            globalID = _plus_1;
            int _plus_2 = (i + 2);
            i = _plus_2;
          }
          EList<CodeElt> _codeElts_1 = coreLoop.getCodeElts();
          int _size_1 = _codeElts_1.size();
          boolean _lessThan_1 = (i < _size_1);
          _while = _lessThan_1;
        }
      }
    }
    this.dumpTimedBuffer.setSize(globalID);
    this.nbExec.setSize(globalID);
    FunctionCall initCall = CodegenFactory.eINSTANCE.createFunctionCall();
    initCall.setName("initNbExec");
    initCall.addParameter(this.nbExec);
    Constant _xblockexpression = null;
    {
      Constant const_ = CodegenFactory.eINSTANCE.createConstant();
      const_.setName("nbDump");
      const_.setType("int");
      const_.setValue(globalID);
      _xblockexpression = (const_);
    }
    initCall.addParameter(_xblockexpression);
    Block _head = IterableExtensions.<Block>head(blocks);
    CallBlock _initBlock = ((CoreBlock) _head).getInitBlock();
    EList<CodeElt> _codeElts = _initBlock.getCodeElts();
    _codeElts.add(initCall);
  }
  
  public CharSequence printDefinitionsFooter(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("int idx;");
    _builder.newLine();
    CharSequence _printDefinitionsFooter = super.printDefinitionsFooter(list);
    _builder.append(_printDefinitionsFooter, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printCoreLoopBlockFooter(final LoopBlock block2) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\t\t");
    _builder.append("pthread_barrier_wait(&iter_barrier);");
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
    CharSequence _printCoreLoopBlockFooter = super.printCoreLoopBlockFooter(block2);
    _builder.append(_printCoreLoopBlockFooter, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printInstrumentedCall(final CodeElt elt, final CharSequence superPrint) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _and = false;
      PrinterState _state = this.getState();
      boolean _equals = Objects.equal(_state, PrinterState.PRINTING_LOOP_BLOCK);
      if (!_equals) {
        _and = false;
      } else {
        Integer _get = this.codeEltID.get(elt);
        boolean _notEquals = (!Objects.equal(_get, null));
        _and = (_equals && _notEquals);
      }
      if (_and) {
        _builder.append("for(idx=0; idx<*(");
        CharSequence _doSwitch = this.doSwitch(this.nbExec);
        _builder.append(_doSwitch, "");
        _builder.append("+");
        Integer _get_1 = this.codeEltID.get(elt);
        _builder.append(_get_1, "");
        _builder.append("); idx++){");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append(superPrint, "	");
        _builder.newLineIfNotEmpty();
        _builder.append("}");
        _builder.newLine();
      } else {
        _builder.append(superPrint, "");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  /**
   * We do not instrument fifo call since this would mess up with the semaphores
   */
  public CharSequence printSharedMemoryCommunication(final SharedMemoryCommunication communication) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _and = false;
      PrinterState _state = this.getState();
      boolean _equals = Objects.equal(_state, PrinterState.PRINTING_LOOP_BLOCK);
      if (!_equals) {
        _and = false;
      } else {
        Integer _get = this.codeEltID.get(communication);
        boolean _notEquals = (!Objects.equal(_get, null));
        _and = (_equals && _notEquals);
      }
      if (_and) {
        _builder.append("*(");
        CharSequence _doSwitch = this.doSwitch(this.nbExec);
        _builder.append(_doSwitch, "");
        _builder.append("+");
        Integer _get_1 = this.codeEltID.get(communication);
        _builder.append(_get_1, "");
        _builder.append(") = 0;");
      }
    }
    _builder.newLineIfNotEmpty();
    CharSequence _printSharedMemoryCommunication = super.printSharedMemoryCommunication(communication);
    _builder.append(_printSharedMemoryCommunication, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printFunctionCall(final FunctionCall functionCall) {
    CharSequence _printFunctionCall = super.printFunctionCall(functionCall);
    CharSequence _printInstrumentedCall = this.printInstrumentedCall(functionCall, _printFunctionCall);
    return _printInstrumentedCall;
  }
  
  /**
   * Special call englobes printFork, Join, Broadcast, RoundBuffer
   */
  public CharSequence caseSpecialCall(final SpecialCall specialCall) {
    CharSequence _caseSpecialCall = super.caseSpecialCall(specialCall);
    CharSequence _printInstrumentedCall = this.printInstrumentedCall(specialCall, _caseSpecialCall);
    return _printInstrumentedCall;
  }
  
  /**
   * We do not instrument fifo call since this would mess up with the memory
   */
  public CharSequence printFifoCall(final FifoCall fifoCall) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _and = false;
      PrinterState _state = this.getState();
      boolean _equals = Objects.equal(_state, PrinterState.PRINTING_LOOP_BLOCK);
      if (!_equals) {
        _and = false;
      } else {
        Integer _get = this.codeEltID.get(fifoCall);
        boolean _notEquals = (!Objects.equal(_get, null));
        _and = (_equals && _notEquals);
      }
      if (_and) {
        _builder.append("*(");
        CharSequence _doSwitch = this.doSwitch(this.nbExec);
        _builder.append(_doSwitch, "");
        _builder.append("+");
        Integer _get_1 = this.codeEltID.get(fifoCall);
        _builder.append(_get_1, "");
        _builder.append(") = 0;");
      }
    }
    _builder.newLineIfNotEmpty();
    CharSequence _printFifoCall = super.printFifoCall(fifoCall);
    _builder.append(_printFifoCall, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public Map<String,CharSequence> createSecondaryFiles(final List<Block> blocks) {
    HashMap<String,CharSequence> _xblockexpression = null;
    {
      HashMap<String,CharSequence> _hashMap = new HashMap<String,CharSequence>();
      HashMap<String,CharSequence> result = _hashMap;
      CharSequence _printAnalysisCsvFile = this.printAnalysisCsvFile();
      result.put("analysis.csv", _printAnalysisCsvFile);
      _xblockexpression = (result);
    }
    return _xblockexpression;
  }
  
  public CharSequence printAnalysisCsvFile() {
    StringConcatenation _builder = new StringConcatenation();
    {
      Set<Entry<String,List<Integer>>> _entrySet = this.actorIDs.entrySet();
      for(final Entry<String,List<Integer>> entry : _entrySet) {
        String _key = entry.getKey();
        _builder.append(_key, "");
        _builder.append(";\"=AVERAGE(");
        {
          List<Integer> _value = entry.getValue();
          boolean _hasElements = false;
          for(final Integer id : _value) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(";", "");
            }
            String _intToColumn = this.intToColumn((id).intValue());
            _builder.append(_intToColumn, "");
            int _size = this.actorIDs.size();
            int _plus = (_size + 3);
            _builder.append(_plus, "");
            _builder.append(":");
            String _intToColumn_1 = this.intToColumn((id).intValue());
            _builder.append(_intToColumn_1, "");
            _builder.append("65536");
          }
        }
        _builder.append(")\"");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  public String intToColumn(final int i) {
    String _xblockexpression = null;
    {
      final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      String result = "";
      int digit = 0;
      int rest = i;
      boolean _dowhile = false;
      do {
        {
          int _minus = (rest - 1);
          int _modulo = (_minus % 26);
          digit = _modulo;
          int _minus_1 = (rest - digit);
          int _divide = (_minus_1 / 26);
          rest = _divide;
          char _charAt = alphabet.charAt(digit);
          String _plus = (Character.valueOf(_charAt) + result);
          result = _plus;
        }
        boolean _greaterThan = (rest > 0);
        _dowhile = _greaterThan;
      } while(_dowhile);
      _xblockexpression = (result);
    }
    return _xblockexpression;
  }
}
