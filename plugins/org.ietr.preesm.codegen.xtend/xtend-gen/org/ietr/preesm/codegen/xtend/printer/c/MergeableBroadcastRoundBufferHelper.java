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
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.printer.c.CPrinter;

/**
 * This class is used within the {@link DynamicAllocCPrinter} to identify the
 * {@link Buffer buffers} that do not need to be allocated separately.
 */
@SuppressWarnings("all")
public class MergeableBroadcastRoundBufferHelper extends CPrinter {
  public MergeableBroadcastRoundBufferHelper() {
    super();
    this.IGNORE_USELESS_MEMCPY = true;
  }
  
  /**
   * When calling {@link #doSwitch(org.eclipse.emf.ecore.EObject)} on a
   * Broadcast or a RoundBuffer {@link SpecialCall}, this {@link Map}
   * is filled with lists of mergeable {@link Buffer buffers}.
   */
  private HashMap<Buffer,List<Buffer>> _mergeableBuffers;
  
  /**
   * When calling {@link #doSwitch(org.eclipse.emf.ecore.EObject)} on a
   * Broadcast or a RoundBuffer {@link SpecialCall}, this {@link Map}
   * is filled with lists of mergeable {@link Buffer buffers}.
   */
  public HashMap<Buffer,List<Buffer>> getMergeableBuffers() {
    return this._mergeableBuffers;
  }
  
  /**
   * When calling {@link #doSwitch(org.eclipse.emf.ecore.EObject)} on a
   * Broadcast or a RoundBuffer {@link SpecialCall}, this {@link Map}
   * is filled with lists of mergeable {@link Buffer buffers}.
   */
  public void setMergeableBuffers(final HashMap<Buffer,List<Buffer>> mergeableBuffers) {
    this._mergeableBuffers = mergeableBuffers;
  }
  
  public CharSequence printMemcpy(final Buffer output, final int outOffset, final Buffer input, final int inOffset, final int size, final String type) {
    CharSequence _xblockexpression = null;
    {
      int totalOffsetOut = outOffset;
      Buffer bOutput = output;
      boolean _while = (bOutput instanceof SubBuffer);
      while (_while) {
        {
          int _offset = ((SubBuffer) bOutput).getOffset();
          int _plus = (totalOffsetOut + _offset);
          totalOffsetOut = _plus;
          Buffer _container = ((SubBuffer) bOutput).getContainer();
          bOutput = _container;
        }
        _while = (bOutput instanceof SubBuffer);
      }
      int totalOffsetIn = inOffset;
      Buffer bInput = input;
      boolean _while_1 = (bInput instanceof SubBuffer);
      while (_while_1) {
        {
          int _offset = ((SubBuffer) bInput).getOffset();
          int _plus = (totalOffsetIn + _offset);
          totalOffsetIn = _plus;
          Buffer _container = ((SubBuffer) bInput).getContainer();
          bInput = _container;
        }
        _while_1 = (bInput instanceof SubBuffer);
      }
      boolean _and = false;
      boolean _and_1 = false;
      if (!this.IGNORE_USELESS_MEMCPY) {
        _and_1 = false;
      } else {
        boolean _equals = Objects.equal(bInput, bOutput);
        _and_1 = (this.IGNORE_USELESS_MEMCPY && _equals);
      }
      if (!_and_1) {
        _and = false;
      } else {
        boolean _equals_1 = (totalOffsetIn == totalOffsetOut);
        _and = (_and_1 && _equals_1);
      }
      if (_and) {
        List<Buffer> _elvis = null;
        HashMap<Buffer,List<Buffer>> _mergeableBuffers = this.getMergeableBuffers();
        List<Buffer> _get = _mergeableBuffers.get(input);
        if (_get != null) {
          _elvis = _get;
        } else {
          ArrayList<Buffer> _arrayList = new ArrayList<Buffer>();
          _elvis = ObjectExtensions.<List<Buffer>>operator_elvis(_get, _arrayList);
        }
        List<Buffer> list = _elvis;
        list.add(output);
        HashMap<Buffer,List<Buffer>> _mergeableBuffers_1 = this.getMergeableBuffers();
        _mergeableBuffers_1.put(input, list);
      } else {
      }
      StringConcatenation _builder = new StringConcatenation();
      _xblockexpression = (_builder);
    }
    return _xblockexpression;
  }
  
  public CharSequence printRoundBuffer(final SpecialCall call) {
    CharSequence _xblockexpression = null;
    {
      EList<Buffer> _inputBuffers = call.getInputBuffers();
      EList<Buffer> _inputBuffers_1 = call.getInputBuffers();
      int _size = _inputBuffers_1.size();
      int _minus = (_size - 1);
      List<Buffer> inputs = _inputBuffers.subList(0, _minus);
      List<Buffer> mergeableInputs = null;
      int _size_1 = inputs.size();
      int _minus_1 = (_size_1 - 2);
      IntegerRange _upTo = new IntegerRange(0, _minus_1);
      for (final Integer i : _upTo) {
        {
          int totalOffsetRef = 0;
          Buffer ref = inputs.get((i).intValue());
          boolean _while = (ref instanceof SubBuffer);
          while (_while) {
            {
              int _offset = ((SubBuffer) ref).getOffset();
              int _plus = (totalOffsetRef + _offset);
              totalOffsetRef = _plus;
              Buffer _container = ((SubBuffer) ref).getContainer();
              ref = _container;
            }
            _while = (ref instanceof SubBuffer);
          }
          final int offRef = totalOffsetRef;
          final Buffer r = ref;
          final Function1<Buffer,Boolean> _function = new Function1<Buffer,Boolean>() {
              public Boolean apply(final Buffer it) {
                boolean _xblockexpression = false;
                {
                  int totalOffset = 0;
                  Buffer b = it;
                  boolean _while = (b instanceof SubBuffer);
                  while (_while) {
                    {
                      int _offset = ((SubBuffer) b).getOffset();
                      int _plus = (totalOffset + _offset);
                      totalOffset = _plus;
                      Buffer _container = ((SubBuffer) b).getContainer();
                      b = _container;
                    }
                    _while = (b instanceof SubBuffer);
                  }
                  boolean _and = false;
                  boolean _equals = (totalOffset == offRef);
                  if (!_equals) {
                    _and = false;
                  } else {
                    boolean _equals_1 = Objects.equal(b, r);
                    _and = (_equals && _equals_1);
                  }
                  _xblockexpression = (_and);
                }
                return Boolean.valueOf(_xblockexpression);
              }
            };
          Iterable<Buffer> mergeableBuffers = IterableExtensions.<Buffer>filter(inputs, _function);
          int _size_2 = IterableExtensions.size(mergeableBuffers);
          boolean _greaterThan = (_size_2 > 1);
          if (_greaterThan) {
            ArrayList<Buffer> _arrayList = new ArrayList<Buffer>();
            mergeableInputs = _arrayList;
            Iterables.<Buffer>addAll(mergeableInputs, mergeableBuffers);
          }
        }
      }
      boolean _notEquals = (!Objects.equal(mergeableInputs, null));
      if (_notEquals) {
        Buffer mergedBuffer = IterableExtensions.<Buffer>head(mergeableInputs);
        mergeableInputs.remove(0);
        HashMap<Buffer,List<Buffer>> _mergeableBuffers = this.getMergeableBuffers();
        _mergeableBuffers.put(mergedBuffer, mergeableInputs);
      }
      {
        int totalOffsetOut = 0;
        EList<Buffer> _outputBuffers = call.getOutputBuffers();
        Buffer bOutput = IterableExtensions.<Buffer>head(_outputBuffers);
        boolean _while = (bOutput instanceof SubBuffer);
        while (_while) {
          {
            int _offset = ((SubBuffer) bOutput).getOffset();
            int _plus = (totalOffsetOut + _offset);
            totalOffsetOut = _plus;
            Buffer _container = ((SubBuffer) bOutput).getContainer();
            bOutput = _container;
          }
          _while = (bOutput instanceof SubBuffer);
        }
        int totalOffsetIn = 0;
        EList<Buffer> _inputBuffers_2 = call.getInputBuffers();
        Buffer bInput = IterableExtensions.<Buffer>last(_inputBuffers_2);
        boolean _while_1 = (bInput instanceof SubBuffer);
        while (_while_1) {
          {
            int _offset = ((SubBuffer) bInput).getOffset();
            int _plus = (totalOffsetIn + _offset);
            totalOffsetIn = _plus;
            Buffer _container = ((SubBuffer) bInput).getContainer();
            bInput = _container;
          }
          _while_1 = (bInput instanceof SubBuffer);
        }
        boolean _and = false;
        boolean _equals = (totalOffsetIn == totalOffsetOut);
        if (!_equals) {
          _and = false;
        } else {
          boolean _equals_1 = Objects.equal(bInput, bOutput);
          _and = (_equals && _equals_1);
        }
        if (_and) {
          ArrayList<Buffer> _arrayList = new ArrayList<Buffer>();
          ArrayList<Buffer> out = _arrayList;
          EList<Buffer> _outputBuffers_1 = call.getOutputBuffers();
          Buffer _head = IterableExtensions.<Buffer>head(_outputBuffers_1);
          out.add(_head);
          HashMap<Buffer,List<Buffer>> _mergeableBuffers_1 = this.getMergeableBuffers();
          EList<Buffer> _inputBuffers_3 = call.getInputBuffers();
          Buffer _last = IterableExtensions.<Buffer>last(_inputBuffers_3);
          _mergeableBuffers_1.put(_last, out);
        }
      }
      StringConcatenation _builder = new StringConcatenation();
      _xblockexpression = (_builder);
    }
    return _xblockexpression;
  }
}
