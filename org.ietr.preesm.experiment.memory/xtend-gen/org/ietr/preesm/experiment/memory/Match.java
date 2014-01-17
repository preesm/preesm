package org.ietr.preesm.experiment.memory;

import org.eclipse.xtend.lib.Data;
import org.eclipse.xtext.xbase.lib.util.ToStringHelper;
import org.ietr.preesm.experiment.memory.Buffer;

@Data
@SuppressWarnings("all")
public class Match {
  private final Buffer _buffer;
  
  public Buffer getBuffer() {
    return this._buffer;
  }
  
  private final int _index;
  
  public int getIndex() {
    return this._index;
  }
  
  public Match(final Buffer buffer, final int index) {
    super();
    this._buffer = buffer;
    this._index = index;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_buffer== null) ? 0 : _buffer.hashCode());
    result = prime * result + _index;
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Match other = (Match) obj;
    if (_buffer == null) {
      if (other._buffer != null)
        return false;
    } else if (!_buffer.equals(other._buffer))
      return false;
    if (other._index != _index)
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    String result = new ToStringHelper().toString(this);
    return result;
  }
}
