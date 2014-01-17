package org.ietr.preesm.experiment.memory;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.ietr.preesm.experiment.memory.FixedSizeList;
import org.ietr.preesm.experiment.memory.Match;

@SuppressWarnings("all")
public class Buffer {
  private final String _name;
  
  public String getName() {
    return this._name;
  }
  
  private final int _nbTokens;
  
  public int getNbTokens() {
    return this._nbTokens;
  }
  
  private final int _tokenSize;
  
  public int getTokenSize() {
    return this._tokenSize;
  }
  
  private final FixedSizeList<Set<Match>> _matchTable;
  
  public FixedSizeList<Set<Match>> getMatchTable() {
    return this._matchTable;
  }
  
  /**
   * Constructor for the {@link Buffer}.
   * @param name
   * 	A {@link String} corresponding to the final name of the buffer.
   * @param nbTokens
   * 	The number of tokens stored in this buffer.
   * @param tokenSize
   * 	The size of one token of the buffer.
   */
  public Buffer(final String name, final int nbTokens, final int tokenSize) {
    this._name = name;
    this._nbTokens = nbTokens;
    this._tokenSize = tokenSize;
    FixedSizeList<Set<Match>> _fixedSizeList = new FixedSizeList<Set<Match>>((nbTokens * tokenSize));
    this._matchTable = _fixedSizeList;
  }
  
  public boolean matchWith(final int localIdx, final Buffer buffer, final int remoteIdx) {
    boolean _xblockexpression = false;
    {
      FixedSizeList<Set<Match>> _matchTable = this.getMatchTable();
      Set<Match> _elvis = null;
      FixedSizeList<Set<Match>> _matchTable_1 = this.getMatchTable();
      Set<Match> _get = _matchTable_1.get(localIdx);
      if (_get != null) {
        _elvis = _get;
      } else {
        HashSet<Match> _newHashSet = CollectionLiterals.<Match>newHashSet();
        _elvis = ObjectExtensions.<Set<Match>>operator_elvis(_get, _newHashSet);
      }
      _matchTable.set(localIdx, _elvis);
      FixedSizeList<Set<Match>> _matchTable_2 = this.getMatchTable();
      Set<Match> _get_1 = _matchTable_2.get(localIdx);
      Match _match = new Match(buffer, remoteIdx);
      _get_1.add(_match);
      FixedSizeList<Set<Match>> _matchTable_3 = buffer.getMatchTable();
      Set<Match> _elvis_1 = null;
      FixedSizeList<Set<Match>> _matchTable_4 = buffer.getMatchTable();
      Set<Match> _get_2 = _matchTable_4.get(remoteIdx);
      if (_get_2 != null) {
        _elvis_1 = _get_2;
      } else {
        HashSet<Match> _newHashSet_1 = CollectionLiterals.<Match>newHashSet();
        _elvis_1 = ObjectExtensions.<Set<Match>>operator_elvis(_get_2, _newHashSet_1);
      }
      _matchTable_3.set(remoteIdx, _elvis_1);
      FixedSizeList<Set<Match>> _matchTable_5 = buffer.getMatchTable();
      Set<Match> _get_3 = _matchTable_5.get(remoteIdx);
      Match _match_1 = new Match(this, localIdx);
      boolean _add = _get_3.add(_match_1);
      _xblockexpression = (_add);
    }
    return _xblockexpression;
  }
  
  public void matchWith(final int localIdx, final Buffer buffer, final int remoteIdx, final int size) {
    IntegerRange _upTo = new IntegerRange(0, (size - 1));
    for (final Integer i : _upTo) {
      this.matchWith((localIdx + (i).intValue()), buffer, (remoteIdx + (i).intValue()));
    }
  }
}
