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
package org.ietr.preesm.codegen.xtend.printer.c;

import com.google.common.base.Objects;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Call;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.codegen.xtend.printer.c.CPrinter;
import org.ietr.preesm.codegen.xtend.printer.c.MergeableBroadcastRoundBufferHelper;

/**
 * This printer is currently used to print C code only for X86 processor with
 * shared memory communication. It is very similary to the {@link CPrinter} except
 * that it prints calls to <code>malloc()</code> and <code>free()</code> before and
 * after each printed function call.
 * 
 * @author kdesnos
 */
@SuppressWarnings("all")
public class DynamicAllocCPrinter extends CPrinter {
  /**
   * Default constructor.
   */
  public DynamicAllocCPrinter() {
    super();
    this.IGNORE_USELESS_MEMCPY = false;
  }
  
  /**
   * Stores the merged {@link Buffer} already
   * {@link #printBufferDeclaration(Buffer) declared} for the current
   * {@link CoreBlock}. Must be emptied before printed a new {@link CoreBlock}
   */
  private Set<Buffer> printedMerged;
  
  /**
   * Associates a buffer to all its merged buffers.
   */
  private HashMap<Buffer,List<Buffer>> _mergedMalloc = new Function0<HashMap<Buffer,List<Buffer>>>() {
    public HashMap<Buffer,List<Buffer>> apply() {
      HashMap<Buffer,List<Buffer>> _hashMap = new HashMap<Buffer,List<Buffer>>();
      return _hashMap;
    }
  }.apply();
  
  /**
   * Associates a buffer to all its merged buffers.
   */
  public HashMap<Buffer,List<Buffer>> getMergedMalloc() {
    return this._mergedMalloc;
  }
  
  /**
   * Associates a buffer to all its merged buffers.
   */
  public void setMergedMalloc(final HashMap<Buffer,List<Buffer>> mergedMalloc) {
    this._mergedMalloc = mergedMalloc;
  }
  
  /**
   * Associates a {@link Buffer} to all its merged buffers. Each entry
   * corresponds to a set of Buffers that are allocated in the same place.
   * Consequently, only the first malloc and the last free calls must be
   * effective for these buffers. Extra care must be taken to ensure that only
   * one malloc is executed.
   */
  private HashMap<Buffer,List<Buffer>> _mergedFree = new Function0<HashMap<Buffer,List<Buffer>>>() {
    public HashMap<Buffer,List<Buffer>> apply() {
      HashMap<Buffer,List<Buffer>> _hashMap = new HashMap<Buffer,List<Buffer>>();
      return _hashMap;
    }
  }.apply();
  
  /**
   * Associates a {@link Buffer} to all its merged buffers. Each entry
   * corresponds to a set of Buffers that are allocated in the same place.
   * Consequently, only the first malloc and the last free calls must be
   * effective for these buffers. Extra care must be taken to ensure that only
   * one malloc is executed.
   */
  public HashMap<Buffer,List<Buffer>> getMergedFree() {
    return this._mergedFree;
  }
  
  /**
   * Associates a {@link Buffer} to all its merged buffers. Each entry
   * corresponds to a set of Buffers that are allocated in the same place.
   * Consequently, only the first malloc and the last free calls must be
   * effective for these buffers. Extra care must be taken to ensure that only
   * one malloc is executed.
   */
  public void setMergedFree(final HashMap<Buffer,List<Buffer>> mergedFree) {
    this._mergedFree = mergedFree;
  }
  
  /**
   * Associates a key from the {@link #mergedFree} map to a {@link Semaphore}
   * used to ensure that the malloc for this {@link Buffer} is only called
   * once.
   */
  private HashMap<Buffer,Semaphore> _mergedFreeSemaphore = new Function0<HashMap<Buffer,Semaphore>>() {
    public HashMap<Buffer,Semaphore> apply() {
      HashMap<Buffer,Semaphore> _hashMap = new HashMap<Buffer,Semaphore>();
      return _hashMap;
    }
  }.apply();
  
  /**
   * Associates a key from the {@link #mergedFree} map to a {@link Semaphore}
   * used to ensure that the malloc for this {@link Buffer} is only called
   * once.
   */
  public HashMap<Buffer,Semaphore> getMergedFreeSemaphore() {
    return this._mergedFreeSemaphore;
  }
  
  /**
   * Associates a key from the {@link #mergedFree} map to a {@link Semaphore}
   * used to ensure that the malloc for this {@link Buffer} is only called
   * once.
   */
  public void setMergedFreeSemaphore(final HashMap<Buffer,Semaphore> mergedFreeSemaphore) {
    this._mergedFreeSemaphore = mergedFreeSemaphore;
  }
  
  /**
   * Associates merged buffers to their allocated buffer.
   */
  private HashMap<Buffer,Buffer> _mergedBuffers = new Function0<HashMap<Buffer,Buffer>>() {
    public HashMap<Buffer,Buffer> apply() {
      HashMap<Buffer,Buffer> _hashMap = new HashMap<Buffer,Buffer>();
      return _hashMap;
    }
  }.apply();
  
  /**
   * Associates merged buffers to their allocated buffer.
   */
  public HashMap<Buffer,Buffer> getMergedBuffers() {
    return this._mergedBuffers;
  }
  
  /**
   * Associates merged buffers to their allocated buffer.
   */
  public void setMergedBuffers(final HashMap<Buffer,Buffer> mergedBuffers) {
    this._mergedBuffers = mergedBuffers;
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
    _builder.append("#include <memory.h>");
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    return _builder;
  }
  
  /**
   * In this Printer, the preprocessing method searches for mergeable broadcasts
   * and replaces their classic malloc method with a malloc with multiple free.
   */
  public void preProcessing(final List<Block> printerBlocks, final List<Block> allBlocks) {
    super.preProcessing(printerBlocks, allBlocks);
    if (this.IGNORE_USELESS_MEMCPY) {
      MergeableBroadcastRoundBufferHelper _mergeableBroadcastRoundBufferHelper = new MergeableBroadcastRoundBufferHelper();
      MergeableBroadcastRoundBufferHelper helper = _mergeableBroadcastRoundBufferHelper;
      for (final Block block : printerBlocks) {
        LoopBlock _loopBlock = ((CoreBlock) block).getLoopBlock();
        EList<CodeElt> _codeElts = _loopBlock.getCodeElts();
        for (final CodeElt codeElt : _codeElts) {
          {
            boolean _and = false;
            if (!(codeElt instanceof SpecialCall)) {
              _and = false;
            } else {
              boolean _isBroadcast = ((SpecialCall) codeElt).isBroadcast();
              _and = ((codeElt instanceof SpecialCall) && _isBroadcast);
            }
            if (_and) {
              HashMap<Buffer,List<Buffer>> _hashMap = new HashMap<Buffer,List<Buffer>>();
              helper.setMergeableBuffers(_hashMap);
              helper.doSwitch(codeElt);
              HashMap<Buffer,List<Buffer>> _mergeableBuffers = helper.getMergeableBuffers();
              int _size = _mergeableBuffers.size();
              boolean _notEquals = (_size != 0);
              if (_notEquals) {
                HashMap<Buffer,List<Buffer>> _mergeableBuffers_1 = helper.getMergeableBuffers();
                Set<Entry<Buffer,List<Buffer>>> _entrySet = _mergeableBuffers_1.entrySet();
                for (final Entry<Buffer,List<Buffer>> malloc : _entrySet) {
                  {
                    Buffer _xifexpression = null;
                    HashMap<Buffer,Buffer> _mergedBuffers = this.getMergedBuffers();
                    Buffer _key = malloc.getKey();
                    boolean _containsKey = _mergedBuffers.containsKey(_key);
                    if (_containsKey) {
                      Buffer _xblockexpression = null;
                      {
                        HashMap<Buffer,Buffer> _mergedBuffers_1 = this.getMergedBuffers();
                        Buffer _key_1 = malloc.getKey();
                        Buffer buffer = _mergedBuffers_1.get(_key_1);
                        HashMap<Buffer,List<Buffer>> _mergedMalloc = this.getMergedMalloc();
                        List<Buffer> _get = _mergedMalloc.get(buffer);
                        List<Buffer> _value = malloc.getValue();
                        _get.addAll(_value);
                        _xblockexpression = (buffer);
                      }
                      _xifexpression = _xblockexpression;
                    } else {
                      Buffer _xblockexpression_1 = null;
                      {
                        HashMap<Buffer,List<Buffer>> _mergedMalloc = this.getMergedMalloc();
                        Buffer _key_1 = malloc.getKey();
                        List<Buffer> _value = malloc.getValue();
                        _mergedMalloc.put(_key_1, _value);
                        Buffer _key_2 = malloc.getKey();
                        _xblockexpression_1 = (_key_2);
                      }
                      _xifexpression = _xblockexpression_1;
                    }
                    final Buffer mergedInBuffer = _xifexpression;
                    List<Buffer> _value = malloc.getValue();
                    final Procedure1<Buffer> _function = new Procedure1<Buffer>() {
                        public void apply(final Buffer it) {
                          HashMap<Buffer,Buffer> _mergedBuffers = DynamicAllocCPrinter.this.getMergedBuffers();
                          _mergedBuffers.put(it, mergedInBuffer);
                        }
                      };
                    IterableExtensions.<Buffer>forEach(_value, _function);
                  }
                }
              }
            }
            boolean _and_1 = false;
            if (!(codeElt instanceof SpecialCall)) {
              _and_1 = false;
            } else {
              boolean _isRoundBuffer = ((SpecialCall) codeElt).isRoundBuffer();
              _and_1 = ((codeElt instanceof SpecialCall) && _isRoundBuffer);
            }
            if (_and_1) {
              HashMap<Buffer,List<Buffer>> _hashMap_1 = new HashMap<Buffer,List<Buffer>>();
              helper.setMergeableBuffers(_hashMap_1);
              helper.doSwitch(codeElt);
              HashMap<Buffer,List<Buffer>> _mergeableBuffers_2 = helper.getMergeableBuffers();
              int _size_1 = _mergeableBuffers_2.size();
              boolean _notEquals_1 = (_size_1 != 0);
              if (_notEquals_1) {
                HashMap<Buffer,List<Buffer>> _mergeableBuffers_3 = helper.getMergeableBuffers();
                Set<Entry<Buffer,List<Buffer>>> _entrySet_1 = _mergeableBuffers_3.entrySet();
                for (final Entry<Buffer,List<Buffer>> free : _entrySet_1) {
                  {
                    List<Buffer> _value = free.getValue();
                    final Function2<Boolean,Buffer,Boolean> _function = new Function2<Boolean,Buffer,Boolean>() {
                        public Boolean apply(final Boolean res, final Buffer buffer) {
                          boolean _and = false;
                          if (!(res).booleanValue()) {
                            _and = false;
                          } else {
                            EList<Buffer> _inputBuffers = ((SpecialCall) codeElt).getInputBuffers();
                            boolean _contains = _inputBuffers.contains(buffer);
                            _and = ((res).booleanValue() && _contains);
                          }
                          return Boolean.valueOf(_and);
                        }
                      };
                    Boolean _fold = IterableExtensions.<Buffer, Boolean>fold(_value, Boolean.valueOf(true), _function);
                    if ((_fold).booleanValue()) {
                      HashMap<Buffer,List<Buffer>> _mergedFree = this.getMergedFree();
                      Buffer _key = free.getKey();
                      List<Buffer> _value_1 = free.getValue();
                      _mergedFree.put(_key, _value_1);
                      final Semaphore sem = CodegenFactory.eINSTANCE.createSemaphore();
                      HashMap<Buffer,Semaphore> _mergedFreeSemaphore = this.getMergedFreeSemaphore();
                      int _size_2 = _mergedFreeSemaphore.size();
                      String _plus = ("sem_multipleAlloc_" + Integer.valueOf(_size_2));
                      sem.setName(_plus);
                      sem.setCreator(block);
                      FunctionCall initSem = CodegenFactory.eINSTANCE.createFunctionCall();
                      initSem.addParameter(sem, PortDirection.NONE);
                      Constant cstShared = CodegenFactory.eINSTANCE.createConstant();
                      cstShared.setType("int");
                      cstShared.setValue(0);
                      initSem.addParameter(cstShared, PortDirection.NONE);
                      cstShared.setCreator(block);
                      Constant cstInitVal = CodegenFactory.eINSTANCE.createConstant();
                      cstInitVal.setType("int");
                      cstInitVal.setValue(1);
                      cstInitVal.setName("init_val");
                      initSem.addParameter(cstInitVal, PortDirection.NONE);
                      cstInitVal.setCreator(block);
                      initSem.setName("sem_init");
                      CallBlock _initBlock = ((CoreBlock) block).getInitBlock();
                      EList<CodeElt> _codeElts_1 = _initBlock.getCodeElts();
                      _codeElts_1.add(initSem);
                      Buffer _key_1 = free.getKey();
                      EList<Block> _users = _key_1.getUsers();
                      final Procedure1<Block> _function_1 = new Procedure1<Block>() {
                          public void apply(final Block it) {
                            EList<Block> _users = sem.getUsers();
                            _users.add(it);
                          }
                        };
                      IterableExtensions.<Block>forEach(_users, _function_1);
                      List<Buffer> _value_2 = free.getValue();
                      final Procedure1<Buffer> _function_2 = new Procedure1<Buffer>() {
                          public void apply(final Buffer it) {
                            EList<Block> _users = sem.getUsers();
                            Block _creator = it.getCreator();
                            _users.add(_creator);
                            EList<Block> _users_1 = it.getUsers();
                            final Procedure1<Block> _function = new Procedure1<Block>() {
                                public void apply(final Block it) {
                                  EList<Block> _users = sem.getUsers();
                                  _users.add(it);
                                }
                              };
                            IterableExtensions.<Block>forEach(_users_1, _function);
                          }
                        };
                      IterableExtensions.<Buffer>forEach(_value_2, _function_2);
                      HashMap<Buffer,Semaphore> _mergedFreeSemaphore_1 = this.getMergedFreeSemaphore();
                      Buffer _key_2 = free.getKey();
                      _mergedFreeSemaphore_1.put(_key_2, sem);
                    } else {
                      HashMap<Buffer,List<Buffer>> _mergedMalloc = this.getMergedMalloc();
                      Buffer _key_3 = free.getKey();
                      List<Buffer> _value_3 = free.getValue();
                      _mergedMalloc.put(_key_3, _value_3);
                    }
                    List<Buffer> _value_4 = free.getValue();
                    final Procedure1<Buffer> _function_3 = new Procedure1<Buffer>() {
                        public void apply(final Buffer it) {
                          HashMap<Buffer,Buffer> _mergedBuffers = DynamicAllocCPrinter.this.getMergedBuffers();
                          Buffer _key = free.getKey();
                          _mergedBuffers.put(it, _key);
                        }
                      };
                    IterableExtensions.<Buffer>forEach(_value_4, _function_3);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  public CharSequence printBufferDefinition(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    {
      HashMap<Buffer,Buffer> _mergedBuffers = this.getMergedBuffers();
      boolean _containsKey = _mergedBuffers.containsKey(buffer);
      boolean _not = (!_containsKey);
      if (_not) {
        String _type = buffer.getType();
        _builder.append(_type, "");
        _builder.append(" *");
        String _name = buffer.getName();
        _builder.append(_name, "");
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
  
  public CharSequence printBufferDeclaration(final Buffer buffer) {
    CharSequence _xifexpression = null;
    boolean _and = false;
    HashMap<Buffer,Buffer> _mergedBuffers = this.getMergedBuffers();
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
        HashMap<Buffer,List<Buffer>> _mergedMalloc = this.getMergedMalloc();
        boolean _containsKey_1 = _mergedMalloc.containsKey(buffer);
        if (_containsKey_1) {
          this.printedMerged.add(buffer);
        }
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("extern ");
        String _type = buffer.getType();
        _builder.append(_type, "");
        _builder.append(" *");
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
      HashMap<Buffer,Buffer> _mergedBuffers_1 = this.getMergedBuffers();
      boolean _containsKey_1 = _mergedBuffers_1.containsKey(buffer);
      if (_containsKey_1) {
        HashMap<Buffer,Buffer> _mergedBuffers_2 = this.getMergedBuffers();
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
  
  public CharSequence printSubBufferDefinition(final SubBuffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printBufferDefinition = this.printBufferDefinition(buffer);
    _builder.append(_printBufferDefinition, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printSubBufferDeclaration(final SubBuffer buffer) {
    CharSequence _printBufferDeclaration = this.printBufferDeclaration(buffer);
    return _printBufferDeclaration;
  }
  
  public CharSequence printBuffer(final Buffer buffer) {
    CharSequence _xifexpression = null;
    HashMap<Buffer,Buffer> _mergedBuffers = this.getMergedBuffers();
    boolean _containsKey = _mergedBuffers.containsKey(buffer);
    boolean _not = (!_containsKey);
    if (_not) {
      StringConcatenation _builder = new StringConcatenation();
      String _name = buffer.getName();
      _builder.append(_name, "");
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      HashMap<Buffer,Buffer> _mergedBuffers_1 = this.getMergedBuffers();
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
    HashMap<Buffer,Buffer> _mergedBuffers = this.getMergedBuffers();
    boolean _containsKey = _mergedBuffers.containsKey(buffer);
    boolean _not = (!_containsKey);
    if (!_not) {
      _and_1 = false;
    } else {
      HashMap<Buffer,List<Buffer>> _mergedMalloc = this.getMergedMalloc();
      boolean _containsKey_1 = _mergedMalloc.containsKey(buffer);
      boolean _not_1 = (!_containsKey_1);
      _and_1 = (_not && _not_1);
    }
    if (!_and_1) {
      _and = false;
    } else {
      HashMap<Buffer,List<Buffer>> _mergedFree = this.getMergedFree();
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
      _builder.append("*) malloc(");
      int _size = buffer.getSize();
      _builder.append(_size, "");
      _builder.append("*sizeof(");
      String _type_1 = buffer.getType();
      _builder.append(_type_1, "");
      _builder.append("));");
      _xifexpression = _builder;
    } else {
      CharSequence _xifexpression_1 = null;
      HashMap<Buffer,List<Buffer>> _mergedMalloc_1 = this.getMergedMalloc();
      boolean _containsKey_3 = _mergedMalloc_1.containsKey(buffer);
      if (_containsKey_3) {
        StringConcatenation _builder_1 = new StringConcatenation();
        CharSequence _doSwitch_1 = this.doSwitch(buffer);
        _builder_1.append(_doSwitch_1, "");
        _builder_1.append(" = (");
        String _type_2 = buffer.getType();
        _builder_1.append(_type_2, "");
        _builder_1.append("*) merged_malloc(");
        int _size_1 = buffer.getSize();
        _builder_1.append(_size_1, "");
        _builder_1.append("*sizeof(");
        String _type_3 = buffer.getType();
        _builder_1.append(_type_3, "");
        _builder_1.append("),");
        HashMap<Buffer,List<Buffer>> _mergedMalloc_2 = this.getMergedMalloc();
        List<Buffer> _get = _mergedMalloc_2.get(buffer);
        int _size_2 = _get.size();
        int _plus = (_size_2 + 1);
        _builder_1.append(_plus, "");
        _builder_1.append("/*nbOfFree*/);");
        _xifexpression_1 = _builder_1;
      } else {
        CharSequence _xifexpression_2 = null;
        boolean _or = false;
        HashMap<Buffer,List<Buffer>> _mergedFree_1 = this.getMergedFree();
        boolean _containsKey_4 = _mergedFree_1.containsKey(buffer);
        if (_containsKey_4) {
          _or = true;
        } else {
          boolean _and_2 = false;
          HashMap<Buffer,Buffer> _mergedBuffers_1 = this.getMergedBuffers();
          boolean _containsKey_5 = _mergedBuffers_1.containsKey(buffer);
          if (!_containsKey_5) {
            _and_2 = false;
          } else {
            HashMap<Buffer,List<Buffer>> _mergedFree_2 = this.getMergedFree();
            HashMap<Buffer,Buffer> _mergedBuffers_2 = this.getMergedBuffers();
            Buffer _get_1 = _mergedBuffers_2.get(buffer);
            boolean _containsKey_6 = _mergedFree_2.containsKey(_get_1);
            _and_2 = (_containsKey_5 && _containsKey_6);
          }
          _or = (_containsKey_4 || _and_2);
        }
        if (_or) {
          CharSequence _xblockexpression = null;
          {
            Buffer _xifexpression_3 = null;
            HashMap<Buffer,List<Buffer>> _mergedFree_3 = this.getMergedFree();
            boolean _containsKey_7 = _mergedFree_3.containsKey(buffer);
            if (_containsKey_7) {
              _xifexpression_3 = buffer;
            } else {
              HashMap<Buffer,Buffer> _mergedBuffers_3 = this.getMergedBuffers();
              Buffer _get_2 = _mergedBuffers_3.get(buffer);
              _xifexpression_3 = _get_2;
            }
            Buffer mergedBuffer = _xifexpression_3;
            StringConcatenation _builder_2 = new StringConcatenation();
            CharSequence _doSwitch_2 = this.doSwitch(mergedBuffer);
            _builder_2.append(_doSwitch_2, "");
            _builder_2.append(" = (");
            String _type_4 = mergedBuffer.getType();
            _builder_2.append(_type_4, "");
            _builder_2.append("*) multiple_malloc(&");
            CharSequence _doSwitch_3 = this.doSwitch(mergedBuffer);
            _builder_2.append(_doSwitch_3, "");
            _builder_2.append(", ");
            int _size_3 = mergedBuffer.getSize();
            _builder_2.append(_size_3, "");
            _builder_2.append("*sizeof(");
            String _type_5 = mergedBuffer.getType();
            _builder_2.append(_type_5, "");
            _builder_2.append("),");
            HashMap<Buffer,List<Buffer>> _mergedFree_4 = this.getMergedFree();
            List<Buffer> _get_3 = _mergedFree_4.get(mergedBuffer);
            int _size_4 = _get_3.size();
            int _plus_1 = (_size_4 + 1);
            _builder_2.append(_plus_1, "");
            _builder_2.append("/*nbOfFree*/, ");
            HashMap<Buffer,Semaphore> _mergedFreeSemaphore = this.getMergedFreeSemaphore();
            Semaphore _get_4 = _mergedFreeSemaphore.get(mergedBuffer);
            CharSequence _doSwitch_4 = this.doSwitch(_get_4);
            _builder_2.append(_doSwitch_4, "");
            _builder_2.append(");");
            _xblockexpression = (_builder_2);
          }
          _xifexpression_2 = _xblockexpression;
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
    HashMap<Buffer,Buffer> _mergedBuffers = this.getMergedBuffers();
    boolean _containsKey = _mergedBuffers.containsKey(buffer);
    boolean _not = (!_containsKey);
    if (!_not) {
      _and_1 = false;
    } else {
      HashMap<Buffer,List<Buffer>> _mergedMalloc = this.getMergedMalloc();
      boolean _containsKey_1 = _mergedMalloc.containsKey(buffer);
      boolean _not_1 = (!_containsKey_1);
      _and_1 = (_not && _not_1);
    }
    if (!_and_1) {
      _and = false;
    } else {
      HashMap<Buffer,List<Buffer>> _mergedFree = this.getMergedFree();
      boolean _containsKey_2 = _mergedFree.containsKey(buffer);
      boolean _not_2 = (!_containsKey_2);
      _and = (_and_1 && _not_2);
    }
    if (_and) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("free(");
      CharSequence _doSwitch = this.doSwitch(buffer);
      _builder.append(_doSwitch, "");
      _builder.append(");");
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      CharSequence _doSwitch_1 = this.doSwitch(buffer);
      _builder_1.append(_doSwitch_1, "");
      _builder_1.append(" = (");
      String _type = buffer.getType();
      _builder_1.append(_type, "");
      _builder_1.append("*) merged_free(");
      CharSequence _doSwitch_2 = this.doSwitch(buffer);
      _builder_1.append(_doSwitch_2, "");
      _builder_1.append(", ");
      int _size = buffer.getSize();
      _builder_1.append(_size, "");
      _builder_1.append("*sizeof(");
      String _type_1 = buffer.getType();
      _builder_1.append(_type_1, "");
      _builder_1.append("));");
      _xifexpression = _builder_1;
    }
    return _xifexpression;
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
      } else {
        _builder.append("cache_inv(&");
        Buffer _headBuffer_1 = fifoCall.getHeadBuffer();
        CharSequence _doSwitch = this.doSwitch(_headBuffer_1);
        _builder.append(_doSwitch, "");
        _builder.append(",1);");
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
        Buffer _headBuffer_2 = fifoCall.getHeadBuffer();
        CharSequence _printFree = this.printFree(_headBuffer_2);
        _builder.append(_printFree, "");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  public CharSequence printBroadcast(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printBroadcast = super.printBroadcast(call);
    CharSequence _printCallWithMallocFree = this.printCallWithMallocFree(call, _printBroadcast);
    _builder.append(_printCallWithMallocFree, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printRoundBuffer(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printRoundBuffer = super.printRoundBuffer(call);
    CharSequence _printCallWithMallocFree = this.printCallWithMallocFree(call, _printRoundBuffer);
    _builder.append(_printCallWithMallocFree, "");
    _builder.newLineIfNotEmpty();
    return _builder;
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
}
