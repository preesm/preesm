package org.ietr.preesm.codegen.xtend.printer.c;

import com.google.common.base.Objects;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Call;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.Direction;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.codegen.xtend.printer.c.C6678CPrinter;
import org.ietr.preesm.codegen.xtend.printer.c.DynamicAllocCPrinter;

@SuppressWarnings("all")
public class DynamicAllocC6678CPrinter extends C6678CPrinter {
  private DynamicAllocCPrinter helperPrinter = new Function0<DynamicAllocCPrinter>() {
    public DynamicAllocCPrinter apply() {
      DynamicAllocCPrinter _dynamicAllocCPrinter = new DynamicAllocCPrinter();
      return _dynamicAllocCPrinter;
    }
  }.apply();
  
  /**
   * Stores the merged {@link Buffer} already
   * {@link #printBufferDeclaration(Buffer) declared} for the current
   * {@link CoreBlock}. Must be emptied before printed a new {@link CoreBlock}
   */
  private Set<Buffer> printedMerged;
  
  public DynamicAllocC6678CPrinter() {
    super();
    this.IGNORE_USELESS_MEMCPY = true;
  }
  
  public CharSequence printCoreBlockHeader(final CoreBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _xblockexpression = null;
    {
      HashSet<Buffer> _hashSet = new HashSet<Buffer>();
      this.printedMerged = _hashSet;
      CharSequence _printCoreBlockHeader = super.printCoreBlockHeader(block);
      _xblockexpression = (_printCoreBlockHeader);
    }
    _builder.append(_xblockexpression, "");
    _builder.newLineIfNotEmpty();
    _builder.append("#include <stdlib.h>");
    _builder.newLine();
    _builder.append("#include <ti/ipc/SharedRegion.h>");
    _builder.newLine();
    _builder.append("#include <ti/ipc/HeapMemMP.h>");
    _builder.newLine();
    _builder.newLine();
    _builder.append("#include <memory.h>");
    _builder.newLine();
    _builder.append("#include <semaphore6678.h>");
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    return _builder;
  }
  
  public void preProcessing(final List<Block> printerBlocks, final List<Block> allBlocks) {
    super.preProcessing(printerBlocks, allBlocks);
    this.helperPrinter.IGNORE_USELESS_MEMCPY = true;
    this.helperPrinter.preProcessing(printerBlocks, allBlocks);
    for (final Block block : printerBlocks) {
      CallBlock _initBlock = ((CoreBlock) block).getInitBlock();
      EList<CodeElt> _codeElts = _initBlock.getCodeElts();
      for (final CodeElt codeElt : _codeElts) {
        if ((codeElt instanceof FunctionCall)) {
          String _name = ((FunctionCall) codeElt).getName();
          boolean _equals = Objects.equal(_name, "sem_init");
          if (_equals) {
            EList<Variable> _parameters = ((FunctionCall) codeElt).getParameters();
            Variable semaphore = IterableExtensions.<Variable>head(_parameters);
            ConstantString string = CodegenFactory.eINSTANCE.createConstantString();
            string.setCreator(block);
            String _name_1 = semaphore.getName();
            string.setValue(_name_1);
            ((FunctionCall) codeElt).addParameter(string, PortDirection.NONE);
          }
        }
      }
    }
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
    _builder.append("HeapMemMP_Handle sharedHeap;");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("// Initialisation(s)");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("communicationInit();");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("sharedHeap = SharedRegion_getHeap(1);");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence printBufferDefinition(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    {
      HashMap<Buffer,Buffer> _mergedBuffers = this.helperPrinter.getMergedBuffers();
      boolean _containsKey = _mergedBuffers.containsKey(buffer);
      boolean _not = (!_containsKey);
      if (_not) {
        _builder.append("#pragma DATA_SECTION(");
        String _name = buffer.getName();
        _builder.append(_name, "");
        _builder.append(",\".MSMCSRAM\");");
        _builder.newLineIfNotEmpty();
        _builder.append("#pragma DATA_ALIGN(");
        String _name_1 = buffer.getName();
        _builder.append(_name_1, "");
        _builder.append(", CACHE_LINE_SIZE);");
        _builder.newLineIfNotEmpty();
        String _type = buffer.getType();
        _builder.append(_type, "");
        _builder.append(" far *");
        String _name_2 = buffer.getName();
        _builder.append(_name_2, "");
        _builder.append(" = 0; // ");
        String _comment = buffer.getComment();
        _builder.append(_comment, "");
        _builder.append(" size:= ");
        int _size = buffer.getSize();
        _builder.append(_size, "");
        _builder.append("*");
        String _type_1 = buffer.getType();
        _builder.append(_type_1, "");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  public CharSequence printSubBufferDefinition(final SubBuffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printBufferDefinition = this.printBufferDefinition(buffer);
    _builder.append(_printBufferDefinition, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printBufferDeclaration(final Buffer buffer) {
    CharSequence _xifexpression = null;
    boolean _and = false;
    HashMap<Buffer,Buffer> _mergedBuffers = this.helperPrinter.getMergedBuffers();
    boolean _containsKey = _mergedBuffers.containsKey(buffer);
    boolean _not = (!_containsKey);
    if (!_not) {
      _and = false;
    } else {
      boolean _contains = this.printedMerged.contains(buffer);
      boolean _not_1 = (!_contains);
      _and = (_not && _not_1);
    }
    if (_and) {
      CharSequence _xblockexpression = null;
      {
        HashMap<Buffer,List<Buffer>> _mergedMalloc = this.helperPrinter.getMergedMalloc();
        boolean _containsKey_1 = _mergedMalloc.containsKey(buffer);
        if (_containsKey_1) {
          this.printedMerged.add(buffer);
        }
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("extern ");
        String _type = buffer.getType();
        _builder.append(_type, "");
        _builder.append(" far *");
        String _name = buffer.getName();
        _builder.append(_name, "");
        _builder.append("; // ");
        String _comment = buffer.getComment();
        _builder.append(_comment, "");
        _builder.append(" size:= ");
        int _size = buffer.getSize();
        _builder.append(_size, "");
        _builder.append("*");
        String _type_1 = buffer.getType();
        _builder.append(_type_1, "");
        _builder.newLineIfNotEmpty();
        _xblockexpression = (_builder);
      }
      _xifexpression = _xblockexpression;
    } else {
      CharSequence _xifexpression_1 = null;
      HashMap<Buffer,Buffer> _mergedBuffers_1 = this.helperPrinter.getMergedBuffers();
      boolean _containsKey_1 = _mergedBuffers_1.containsKey(buffer);
      if (_containsKey_1) {
        HashMap<Buffer,Buffer> _mergedBuffers_2 = this.helperPrinter.getMergedBuffers();
        Buffer _get = _mergedBuffers_2.get(buffer);
        CharSequence _printBufferDeclaration = this.printBufferDeclaration(_get);
        _xifexpression_1 = _printBufferDeclaration;
      } else {
        StringConcatenation _builder = new StringConcatenation();
        _xifexpression_1 = _builder;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
  
  public CharSequence printSubBufferDeclaration(final SubBuffer buffer) {
    CharSequence _printBufferDeclaration = this.printBufferDeclaration(buffer);
    return _printBufferDeclaration;
  }
  
  public CharSequence printBuffer(final Buffer buffer) {
    CharSequence _xifexpression = null;
    HashMap<Buffer,Buffer> _mergedBuffers = this.helperPrinter.getMergedBuffers();
    boolean _containsKey = _mergedBuffers.containsKey(buffer);
    boolean _not = (!_containsKey);
    if (_not) {
      StringConcatenation _builder = new StringConcatenation();
      String _name = buffer.getName();
      _builder.append(_name, "");
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      HashMap<Buffer,Buffer> _mergedBuffers_1 = this.helperPrinter.getMergedBuffers();
      Buffer _get = _mergedBuffers_1.get(buffer);
      String _name_1 = _get.getName();
      _builder_1.append(_name_1, "");
      _xifexpression = _builder_1;
    }
    return _xifexpression;
  }
  
  public CharSequence printFunctionCall(final FunctionCall functionCall) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printFunctionCall = super.printFunctionCall(functionCall);
    CharSequence _printCallWithMallocFree = this.printCallWithMallocFree(functionCall, _printFunctionCall);
    _builder.append(_printCallWithMallocFree, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  /**
   * Add the necessary <code>malloc()</code> and <code>free()</code> calls to
   * a call passed as a parameter. Before the {@link Call}, a
   * <code>malloc()</code> for all the output {@link Buffer} of the
   * {@link Call} is printed. After the {@link Call}, a <code>free()</code> is
   * printed for each output {@link Buffer} of the {@link Call}.
   * 
   * @param call
   *            the {@link Call} to print.
   * @param sequence
   *            the normal printed {@link Call} (by the {@link CPrinter}.
   * @return the {@link Call} printed with <code>malloc()</code> and
   */
  public CharSequence printCallWithMallocFree(final Call call, final CharSequence sequence) {
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
              boolean _equals = Objects.equal(_get, PortDirection.OUTPUT);
              if (_equals) {
                EList<Variable> _parameters_2 = call.getParameters();
                Variable _get_1 = _parameters_2.get((i).intValue());
                CharSequence _printMalloc = this.printMalloc(((Buffer) _get_1));
                _builder.append(_printMalloc, "");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
    }
    _builder.append(sequence, "");
    _builder.newLineIfNotEmpty();
    {
      EList<Variable> _parameters_3 = call.getParameters();
      int _size_2 = _parameters_3.size();
      boolean _greaterThan_1 = (_size_2 > 0);
      if (_greaterThan_1) {
        {
          EList<Variable> _parameters_4 = call.getParameters();
          int _size_3 = _parameters_4.size();
          int _minus_1 = (_size_3 - 1);
          IntegerRange _upTo_1 = new IntegerRange(0, _minus_1);
          for(final Integer i_1 : _upTo_1) {
            {
              EList<PortDirection> _parameterDirections_1 = call.getParameterDirections();
              PortDirection _get_2 = _parameterDirections_1.get((i_1).intValue());
              boolean _equals_1 = Objects.equal(_get_2, PortDirection.INPUT);
              if (_equals_1) {
                EList<Variable> _parameters_5 = call.getParameters();
                Variable _get_3 = _parameters_5.get((i_1).intValue());
                CharSequence _printFree = this.printFree(((Buffer) _get_3));
                _builder.append(_printFree, "");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
    }
    return _builder;
  }
  
  public CharSequence printFifoCall(final FifoCall fifoCall) {
    StringConcatenation _builder = new StringConcatenation();
    {
      FifoOperation _operation = fifoCall.getOperation();
      boolean _equals = Objects.equal(_operation, FifoOperation.INIT);
      if (_equals) {
        {
          Buffer _bodyBuffer = fifoCall.getBodyBuffer();
          boolean _notEquals = (!Objects.equal(_bodyBuffer, null));
          if (_notEquals) {
            Buffer _bodyBuffer_1 = fifoCall.getBodyBuffer();
            CharSequence _printMalloc = this.printMalloc(_bodyBuffer_1);
            _builder.append(_printMalloc, "");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
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
        Buffer _headBuffer = fifoCall.getHeadBuffer();
        CharSequence _printMalloc_1 = this.printMalloc(_headBuffer);
        _builder.append(_printMalloc_1, "");
        _builder.newLineIfNotEmpty();
      }
    }
    CharSequence _printFifoCall = super.printFifoCall(fifoCall);
    CharSequence _printCallWithMallocFree = this.printCallWithMallocFree(fifoCall, _printFifoCall);
    _builder.append(_printCallWithMallocFree, "");
    _builder.newLineIfNotEmpty();
    {
      FifoOperation _operation_3 = fifoCall.getOperation();
      boolean _equals_3 = Objects.equal(_operation_3, FifoOperation.POP);
      if (_equals_3) {
        Buffer _headBuffer_1 = fifoCall.getHeadBuffer();
        CharSequence _printFree = this.printFree(_headBuffer_1);
        _builder.append(_printFree, "");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  public CharSequence printBroadcast(final SpecialCall call) {
    CharSequence _printBroadcast = super.printBroadcast(call);
    CharSequence _printCallWithMallocFree = this.printCallWithMallocFree(call, _printBroadcast);
    return _printCallWithMallocFree;
  }
  
  public CharSequence printRoundBuffer(final SpecialCall call) {
    CharSequence _printRoundBuffer = super.printRoundBuffer(call);
    CharSequence _printCallWithMallocFree = this.printCallWithMallocFree(call, _printRoundBuffer);
    return _printCallWithMallocFree;
  }
  
  public CharSequence printFork(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printFork = super.printFork(call);
    CharSequence _printCallWithMallocFree = this.printCallWithMallocFree(call, _printFork);
    _builder.append(_printCallWithMallocFree, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printJoin(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printJoin = super.printJoin(call);
    CharSequence _printCallWithMallocFree = this.printCallWithMallocFree(call, _printJoin);
    _builder.append(_printCallWithMallocFree, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  /**
   * Methods used to print a Malloc.
   * @param buffer
   * 			the {@link Buffer} that is allocated
   * @return the printed code as a {@link CharSequence}.
   */
  public CharSequence printMalloc(final Buffer buffer) {
    CharSequence _xifexpression = null;
    boolean _and = false;
    boolean _and_1 = false;
    HashMap<Buffer,Buffer> _mergedBuffers = this.helperPrinter.getMergedBuffers();
    boolean _containsKey = _mergedBuffers.containsKey(buffer);
    boolean _not = (!_containsKey);
    if (!_not) {
      _and_1 = false;
    } else {
      HashMap<Buffer,List<Buffer>> _mergedMalloc = this.helperPrinter.getMergedMalloc();
      boolean _containsKey_1 = _mergedMalloc.containsKey(buffer);
      boolean _not_1 = (!_containsKey_1);
      _and_1 = (_not && _not_1);
    }
    if (!_and_1) {
      _and = false;
    } else {
      HashMap<Buffer,List<Buffer>> _mergedFree = this.helperPrinter.getMergedFree();
      boolean _containsKey_2 = _mergedFree.containsKey(buffer);
      boolean _not_2 = (!_containsKey_2);
      _and = (_and_1 && _not_2);
    }
    if (_and) {
      StringConcatenation _builder = new StringConcatenation();
      CharSequence _doSwitch = this.doSwitch(buffer);
      _builder.append(_doSwitch, "");
      _builder.append(" = (");
      String _type = buffer.getType();
      _builder.append(_type, "");
      _builder.append("*) HeapMemMP_alloc(sharedHeap,");
      int _size = buffer.getSize();
      _builder.append(_size, "");
      _builder.append("*sizeof(");
      String _type_1 = buffer.getType();
      _builder.append(_type_1, "");
      _builder.append("), CACHE_LINE_SIZE);");
      _builder.newLineIfNotEmpty();
      _builder.append("cache_wb(&");
      CharSequence _doSwitch_1 = this.doSwitch(buffer);
      _builder.append(_doSwitch_1, "");
      _builder.append(", 1);");
      _builder.newLineIfNotEmpty();
      _xifexpression = _builder;
    } else {
      CharSequence _xifexpression_1 = null;
      HashMap<Buffer,List<Buffer>> _mergedMalloc_1 = this.helperPrinter.getMergedMalloc();
      boolean _containsKey_3 = _mergedMalloc_1.containsKey(buffer);
      if (_containsKey_3) {
        CharSequence _xblockexpression = null;
        {
          ConstantString string = CodegenFactory.eINSTANCE.createConstantString();
          Block _creator = buffer.getCreator();
          string.setCreator(_creator);
          String _name = buffer.getName();
          string.setValue(_name);
          StringConcatenation _builder_1 = new StringConcatenation();
          CharSequence _doSwitch_2 = this.doSwitch(buffer);
          _builder_1.append(_doSwitch_2, "");
          _builder_1.append(" = (");
          String _type_2 = buffer.getType();
          _builder_1.append(_type_2, "");
          _builder_1.append("*) merged_malloc(sharedHeap, (void**)&");
          CharSequence _doSwitch_3 = this.doSwitch(buffer);
          _builder_1.append(_doSwitch_3, "");
          _builder_1.append(",");
          int _size_1 = buffer.getSize();
          _builder_1.append(_size_1, "");
          _builder_1.append("*sizeof(");
          String _type_3 = buffer.getType();
          _builder_1.append(_type_3, "");
          _builder_1.append("), ");
          HashMap<Buffer,List<Buffer>> _mergedMalloc_2 = this.helperPrinter.getMergedMalloc();
          List<Buffer> _get = _mergedMalloc_2.get(buffer);
          int _size_2 = _get.size();
          int _plus = (_size_2 + 1);
          _builder_1.append(_plus, "");
          _builder_1.append("/*nbOfFree*/, ");
          CharSequence _printConstantString = this.printConstantString(string);
          _builder_1.append(_printConstantString, "");
          _builder_1.append(",CACHE_LINE_SIZE);");
          _builder_1.newLineIfNotEmpty();
          _builder_1.append("cache_wb(&");
          CharSequence _doSwitch_4 = this.doSwitch(buffer);
          _builder_1.append(_doSwitch_4, "");
          _builder_1.append(", 1);");
          _builder_1.newLineIfNotEmpty();
          _xblockexpression = (_builder_1);
        }
        _xifexpression_1 = _xblockexpression;
      } else {
        CharSequence _xifexpression_2 = null;
        boolean _or = false;
        HashMap<Buffer,List<Buffer>> _mergedFree_1 = this.helperPrinter.getMergedFree();
        boolean _containsKey_4 = _mergedFree_1.containsKey(buffer);
        if (_containsKey_4) {
          _or = true;
        } else {
          boolean _and_2 = false;
          HashMap<Buffer,Buffer> _mergedBuffers_1 = this.helperPrinter.getMergedBuffers();
          boolean _containsKey_5 = _mergedBuffers_1.containsKey(buffer);
          if (!_containsKey_5) {
            _and_2 = false;
          } else {
            HashMap<Buffer,List<Buffer>> _mergedFree_2 = this.helperPrinter.getMergedFree();
            HashMap<Buffer,Buffer> _mergedBuffers_2 = this.helperPrinter.getMergedBuffers();
            Buffer _get = _mergedBuffers_2.get(buffer);
            boolean _containsKey_6 = _mergedFree_2.containsKey(_get);
            _and_2 = (_containsKey_5 && _containsKey_6);
          }
          _or = (_containsKey_4 || _and_2);
        }
        if (_or) {
          CharSequence _xblockexpression_1 = null;
          {
            Buffer _xifexpression_3 = null;
            HashMap<Buffer,List<Buffer>> _mergedFree_3 = this.helperPrinter.getMergedFree();
            boolean _containsKey_7 = _mergedFree_3.containsKey(buffer);
            if (_containsKey_7) {
              _xifexpression_3 = buffer;
            } else {
              HashMap<Buffer,Buffer> _mergedBuffers_3 = this.helperPrinter.getMergedBuffers();
              Buffer _get_1 = _mergedBuffers_3.get(buffer);
              _xifexpression_3 = _get_1;
            }
            Buffer mergedBuffer = _xifexpression_3;
            ConstantString string = CodegenFactory.eINSTANCE.createConstantString();
            Block _creator = buffer.getCreator();
            string.setCreator(_creator);
            String _name = mergedBuffer.getName();
            string.setValue(_name);
            StringConcatenation _builder_1 = new StringConcatenation();
            CharSequence _doSwitch_2 = this.doSwitch(mergedBuffer);
            _builder_1.append(_doSwitch_2, "");
            _builder_1.append(" = (");
            String _type_2 = mergedBuffer.getType();
            _builder_1.append(_type_2, "");
            _builder_1.append("*) multiple_malloc(sharedHeap, (void**)&");
            CharSequence _doSwitch_3 = this.doSwitch(mergedBuffer);
            _builder_1.append(_doSwitch_3, "");
            _builder_1.append(",");
            int _size_1 = mergedBuffer.getSize();
            _builder_1.append(_size_1, "");
            _builder_1.append("*sizeof(");
            String _type_3 = mergedBuffer.getType();
            _builder_1.append(_type_3, "");
            _builder_1.append("), ");
            HashMap<Buffer,List<Buffer>> _mergedFree_4 = this.helperPrinter.getMergedFree();
            List<Buffer> _get_2 = _mergedFree_4.get(mergedBuffer);
            int _size_2 = _get_2.size();
            int _plus = (_size_2 + 1);
            _builder_1.append(_plus, "");
            _builder_1.append("/*nbOfFree*/, ");
            HashMap<Buffer,Semaphore> _mergedFreeSemaphore = this.helperPrinter.getMergedFreeSemaphore();
            Semaphore _get_3 = _mergedFreeSemaphore.get(mergedBuffer);
            CharSequence _doSwitch_4 = this.doSwitch(_get_3);
            _builder_1.append(_doSwitch_4, "");
            _builder_1.append(", ");
            CharSequence _printConstantString = this.printConstantString(string);
            _builder_1.append(_printConstantString, "");
            _builder_1.append(", CACHE_LINE_SIZE);");
            _builder_1.newLineIfNotEmpty();
            _builder_1.append("cache_wb(&");
            CharSequence _doSwitch_5 = this.doSwitch(buffer);
            _builder_1.append(_doSwitch_5, "");
            _builder_1.append(", 1);");
            _builder_1.newLineIfNotEmpty();
            _xblockexpression_1 = (_builder_1);
          }
          _xifexpression_2 = _xblockexpression_1;
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
  
  /**
   * Methods used to print a Free.
   * @param buffer
   * 			the {@link Buffer} that freed
   * @return the printed code as a {@link CharSequence}.
   */
  public CharSequence printFree(final Buffer buffer) {
    CharSequence _xifexpression = null;
    boolean _and = false;
    boolean _and_1 = false;
    HashMap<Buffer,Buffer> _mergedBuffers = this.helperPrinter.getMergedBuffers();
    boolean _containsKey = _mergedBuffers.containsKey(buffer);
    boolean _not = (!_containsKey);
    if (!_not) {
      _and_1 = false;
    } else {
      HashMap<Buffer,List<Buffer>> _mergedMalloc = this.helperPrinter.getMergedMalloc();
      boolean _containsKey_1 = _mergedMalloc.containsKey(buffer);
      boolean _not_1 = (!_containsKey_1);
      _and_1 = (_not && _not_1);
    }
    if (!_and_1) {
      _and = false;
    } else {
      HashMap<Buffer,List<Buffer>> _mergedFree = this.helperPrinter.getMergedFree();
      boolean _containsKey_2 = _mergedFree.containsKey(buffer);
      boolean _not_2 = (!_containsKey_2);
      _and = (_and_1 && _not_2);
    }
    if (_and) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("HeapMemMP_free(sharedHeap, ");
      CharSequence _doSwitch = this.doSwitch(buffer);
      _builder.append(_doSwitch, "");
      _builder.append(", ");
      int _size = buffer.getSize();
      _builder.append(_size, "");
      _builder.append("*sizeof(");
      String _type = buffer.getType();
      _builder.append(_type, "");
      _builder.append("));");
      _builder.newLineIfNotEmpty();
      _builder.append("cache_inv(&");
      CharSequence _doSwitch_1 = this.doSwitch(buffer);
      _builder.append(_doSwitch_1, "");
      _builder.append(", 1);");
      _builder.newLineIfNotEmpty();
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      CharSequence _doSwitch_2 = this.doSwitch(buffer);
      _builder_1.append(_doSwitch_2, "");
      _builder_1.append(" = (");
      String _type_1 = buffer.getType();
      _builder_1.append(_type_1, "");
      _builder_1.append("*) merged_free(sharedHeap, ");
      CharSequence _doSwitch_3 = this.doSwitch(buffer);
      _builder_1.append(_doSwitch_3, "");
      _builder_1.append(", ");
      int _size_1 = buffer.getSize();
      _builder_1.append(_size_1, "");
      _builder_1.append("*sizeof(");
      String _type_2 = buffer.getType();
      _builder_1.append(_type_2, "");
      _builder_1.append("));");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("cache_wbInv(&");
      CharSequence _doSwitch_4 = this.doSwitch(buffer);
      _builder_1.append(_doSwitch_4, "");
      _builder_1.append(", 1);");
      _builder_1.newLineIfNotEmpty();
      _xifexpression = _builder_1;
    }
    return _xifexpression;
  }
  
  public CharSequence printSemaphoreDefinition(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("#pragma DATA_SECTION(");
    String _name = semaphore.getName();
    _builder.append(_name, "");
    _builder.append(",\".MSMCSRAM\");");
    _builder.newLineIfNotEmpty();
    _builder.append("#pragma DATA_ALIGN(");
    String _name_1 = semaphore.getName();
    _builder.append(_name_1, "");
    _builder.append(", CACHE_LINE_SIZE);");
    _builder.newLineIfNotEmpty();
    _builder.append("sem_t far ");
    String _name_2 = semaphore.getName();
    _builder.append(_name_2, "");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printSemaphoreDeclaration(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("extern sem_t far ");
    String _name = semaphore.getName();
    _builder.append(_name, "");
    _builder.append("; ");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printSemaphore(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("&");
    String _name = semaphore.getName();
    _builder.append(_name, "");
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
        {
          Buffer _data = communication.getData();
          EList<SubBuffer> _childrens = ((Buffer) _data).getChildrens();
          for(final SubBuffer subbuffer : _childrens) {
            _builder.append("cache_wbInv(");
            CharSequence _doSwitch = this.doSwitch(subbuffer);
            _builder.append(_doSwitch, "");
            _builder.append(", ");
            int _size = subbuffer.getSize();
            _builder.append(_size, "");
            _builder.append("*sizeof(");
            String _type = subbuffer.getType();
            _builder.append(_type, "");
            _builder.append("));");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    CharSequence _printSharedMemoryCommunication = super.printSharedMemoryCommunication(communication);
    String _string = _printSharedMemoryCommunication.toString();
    String _replaceAll = _string.replaceAll("(cache_.*?;)", "// $1");
    _builder.append(_replaceAll, "");
    _builder.newLineIfNotEmpty();
    {
      boolean _and_1 = false;
      Direction _direction_1 = communication.getDirection();
      boolean _equals_2 = Objects.equal(_direction_1, Direction.RECEIVE);
      if (!_equals_2) {
        _and_1 = false;
      } else {
        Delimiter _delimiter_1 = communication.getDelimiter();
        boolean _equals_3 = Objects.equal(_delimiter_1, Delimiter.END);
        _and_1 = (_equals_2 && _equals_3);
      }
      if (_and_1) {
        {
          Buffer _data_1 = communication.getData();
          EList<SubBuffer> _childrens_1 = ((Buffer) _data_1).getChildrens();
          for(final SubBuffer subbuffer_1 : _childrens_1) {
            {
              boolean _or = false;
              boolean _or_1 = false;
              HashMap<Buffer,Buffer> _mergedBuffers = this.helperPrinter.getMergedBuffers();
              boolean _containsKey = _mergedBuffers.containsKey(subbuffer_1);
              if (_containsKey) {
                _or_1 = true;
              } else {
                HashMap<Buffer,List<Buffer>> _mergedFree = this.helperPrinter.getMergedFree();
                boolean _containsKey_1 = _mergedFree.containsKey(subbuffer_1);
                _or_1 = (_containsKey || _containsKey_1);
              }
              if (_or_1) {
                _or = true;
              } else {
                HashMap<Buffer,List<Buffer>> _mergedMalloc = this.helperPrinter.getMergedMalloc();
                boolean _containsKey_2 = _mergedMalloc.containsKey(subbuffer_1);
                _or = (_or_1 || _containsKey_2);
              }
              if (_or) {
                _builder.append("cache_inv(&");
                CharSequence _doSwitch_1 = this.doSwitch(subbuffer_1);
                _builder.append(_doSwitch_1, "");
                _builder.append(", 1);");
                _builder.newLineIfNotEmpty();
              }
            }
            _builder.append("cache_inv(");
            CharSequence _doSwitch_2 = this.doSwitch(subbuffer_1);
            _builder.append(_doSwitch_2, "");
            _builder.append(", ");
            int _size_1 = subbuffer_1.getSize();
            _builder.append(_size_1, "");
            _builder.append("*sizeof(");
            String _type_1 = subbuffer_1.getType();
            _builder.append(_type_1, "");
            _builder.append("));");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder;
  }
}
