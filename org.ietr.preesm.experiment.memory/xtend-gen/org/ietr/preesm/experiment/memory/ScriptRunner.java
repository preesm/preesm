package org.ietr.preesm.experiment.memory;

import bsh.EvalError;
import bsh.Interpreter;
import com.google.common.base.Objects;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.dftools.algorithm.model.AbstractEdgePropertyType;
import net.sf.dftools.algorithm.model.AbstractGraph;
import net.sf.dftools.algorithm.model.PropertyBean;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import net.sf.dftools.algorithm.model.dag.edag.DAGForkVertex;
import net.sf.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import net.sf.dftools.algorithm.model.parameters.Argument;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.algorithm.model.sdf.SDFVertex;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import net.sf.dftools.workflow.tools.WorkflowLogger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.experiment.memory.Buffer;
import org.ietr.preesm.experiment.memory.FixedSizeList;
import org.ietr.preesm.experiment.memory.Match;

@SuppressWarnings("all")
public class ScriptRunner {
  /**
   * A {@link Map} that associates each {@link String} representing a type name
   * with a corresponding {@link DataType}.
   */
  private Map<String,DataType> _dataTypes;
  
  /**
   * A {@link Map} that associates each {@link String} representing a type name
   * with a corresponding {@link DataType}.
   */
  public Map<String,DataType> getDataTypes() {
    return this._dataTypes;
  }
  
  /**
   * A {@link Map} that associates each {@link String} representing a type name
   * with a corresponding {@link DataType}.
   */
  public void setDataTypes(final Map<String,DataType> dataTypes) {
    this._dataTypes = dataTypes;
  }
  
  /**
   * A {@link Map} that associates each {@link DAGVertex} with a
   * memory script to this memory script {@link File}.
   */
  private final HashMap<DAGVertex,File> scriptedVertices = new Function0<HashMap<DAGVertex,File>>() {
    public HashMap<DAGVertex,File> apply() {
      HashMap<DAGVertex,File> _hashMap = new HashMap<DAGVertex, File>();
      return _hashMap;
    }
  }.apply();
  
  /**
   * A {@link Map} that associates each {@link DAGVertex} from the
   * {@link #scriptedVertices} map to the result of the successful
   * execution of its script. The result is stored as a {@link Pair}
   * of {@link List} of {@link Buffer}. The first {@link List} contains
   * the input {@link Buffer buffers} and the second contains output
   * {@link Buffer buffers}.
   */
  private final HashMap<DAGVertex,Pair<List<Buffer>,List<Buffer>>> scriptResults = new Function0<HashMap<DAGVertex,Pair<List<Buffer>,List<Buffer>>>>() {
    public HashMap<DAGVertex,Pair<List<Buffer>,List<Buffer>>> apply() {
      HashMap<DAGVertex,Pair<List<Buffer>,List<Buffer>>> _hashMap = new HashMap<DAGVertex, Pair<List<Buffer>, List<Buffer>>>();
      return _hashMap;
    }
  }.apply();
  
  /**
   * This method finds the memory scripts associated to the {@link DAGVertex
   * vertices} of the input {@link DirectedAcyclicGraph}. When a script path
   * is set in the property of the {@link SDFVertex} associated to a
   * {@link DAGVertex} of the graph, scripts are either found in a path
   * relative to the original {@link SDFGraph} file, or in the plugin project
   * "scripts" directory. If an invalid script path is set, a warning message
   * will be written in the log.
   * 
   * @param dag
   *            the {@link DirectedAcyclicGraph} whose vertices memory scripts
   *            are retrieved.
   */
  protected void findScripts(final DirectedAcyclicGraph dag) {
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    PropertyBean _propertyBean = dag.getPropertyBean();
    Object _value = _propertyBean.getValue(DirectedAcyclicGraph.PATH, String.class);
    final String sdfPath = ((String) _value);
    IWorkspaceRoot _root = workspace.getRoot();
    Path _path = new Path(sdfPath);
    IFile sdfFile = _root.getFileForLocation(_path);
    final WorkflowLogger logger = WorkflowLogger.getLogger();
    Set<DAGVertex> _vertexSet = dag.vertexSet();
    for (final DAGVertex dagVertex : _vertexSet) {
      {
        PropertyBean _propertyBean_1 = dagVertex.getPropertyBean();
        Object _value_1 = _propertyBean_1.getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex.class);
        final SDFAbstractVertex sdfVertex = ((SDFAbstractVertex) _value_1);
        String _kind = dagVertex.getKind();
        boolean _notEquals = (!Objects.equal(_kind, null));
        if (_notEquals) {
          String _kind_1 = dagVertex.getKind();
          final String _switchValue = _kind_1;
          boolean _matched = false;
          if (!_matched) {
            if (Objects.equal(_switchValue,DAGVertex.DAG_VERTEX)) {
              _matched=true;
              PropertyBean _propertyBean_2 = sdfVertex.getPropertyBean();
              Object _value_2 = _propertyBean_2.getValue(SDFVertex.MEMORY_SCRIPT, String.class);
              final String pathString = ((String) _value_2);
              boolean _notEquals_1 = (!Objects.equal(pathString, null));
              if (_notEquals_1) {
                IContainer _parent = sdfFile.getParent();
                Path _path_1 = new Path(pathString);
                IFile _file = _parent.getFile(_path_1);
                IPath _rawLocation = _file.getRawLocation();
                IPath _makeAbsolute = _rawLocation.makeAbsolute();
                File scriptFile = _makeAbsolute.toFile();
                boolean _exists = scriptFile.exists();
                if (_exists) {
                  this.scriptedVertices.put(dagVertex, scriptFile);
                } else {
                  final String classpathString = ("/../scripts/" + pathString);
                  URI sourceStream = null;
                  try {
                    Class<? extends ScriptRunner> _class = this.getClass();
                    URL _resource = _class.getResource(classpathString);
                    URI _uRI = _resource.toURI();
                    sourceStream = _uRI;
                    URL _uRL = sourceStream.toURL();
                    URL _resolve = FileLocator.resolve(_uRL);
                    String _file_1 = _resolve.getFile();
                    File _file_2 = new File(_file_1);
                    scriptFile = _file_2;
                    boolean _exists_1 = scriptFile.exists();
                    if (_exists_1) {
                      this.scriptedVertices.put(dagVertex, scriptFile);
                    }
                  } catch (final Throwable _t) {
                    if (_t instanceof Exception) {
                      final Exception e = (Exception)_t;
                    } else {
                      throw Exceptions.sneakyThrow(_t);
                    }
                  }
                  boolean _or = false;
                  boolean _equals = Objects.equal(scriptFile, null);
                  if (_equals) {
                    _or = true;
                  } else {
                    boolean _exists_2 = scriptFile.exists();
                    boolean _not = (!_exists_2);
                    _or = (_equals || _not);
                  }
                  if (_or) {
                    String _name = sdfVertex.getName();
                    String _plus = ("Memory script of vertex " + _name);
                    String _plus_1 = (_plus + " is invalid: \"");
                    String _plus_2 = (_plus_1 + pathString);
                    String _plus_3 = (_plus_2 + "\". Change it in the graphml editor.");
                    logger.log(Level.WARNING, _plus_3);
                  }
                }
              }
            }
          }
          if (!_matched) {
            if (Objects.equal(_switchValue,DAGForkVertex.DAG_FORK_VERTEX)) {
              _matched=true;
              final String classpathString_1 = "/../scripts/fork.bsh";
              try {
                Class<? extends ScriptRunner> _class_1 = this.getClass();
                URL _resource_1 = _class_1.getResource(classpathString_1);
                final URI sourceStream_1 = _resource_1.toURI();
                URL _uRL_1 = sourceStream_1.toURL();
                URL _resolve_1 = FileLocator.resolve(_uRL_1);
                String _file_3 = _resolve_1.getFile();
                File _file_4 = new File(_file_3);
                final File scriptFile_1 = _file_4;
                boolean _exists_3 = scriptFile_1.exists();
                if (_exists_3) {
                  this.scriptedVertices.put(dagVertex, scriptFile_1);
                }
              } catch (final Throwable _t_1) {
                if (_t_1 instanceof Exception) {
                  final Exception e_1 = (Exception)_t_1;
                  logger.log(Level.SEVERE, 
                    "Memory script of fork vertices not found. Please contact Preesm developers.");
                } else {
                  throw Exceptions.sneakyThrow(_t_1);
                }
              }
            }
          }
          if (!_matched) {
            if (Objects.equal(_switchValue,DAGJoinVertex.DAG_JOIN_VERTEX)) {
              _matched=true;
              final String classpathString_2 = "/../scripts/join.bsh";
              try {
                Class<? extends ScriptRunner> _class_2 = this.getClass();
                URL _resource_2 = _class_2.getResource(classpathString_2);
                final URI sourceStream_2 = _resource_2.toURI();
                URL _uRL_2 = sourceStream_2.toURL();
                URL _resolve_2 = FileLocator.resolve(_uRL_2);
                String _file_5 = _resolve_2.getFile();
                File _file_6 = new File(_file_5);
                final File scriptFile_2 = _file_6;
                boolean _exists_4 = scriptFile_2.exists();
                if (_exists_4) {
                  this.scriptedVertices.put(dagVertex, scriptFile_2);
                }
              } catch (final Throwable _t_2) {
                if (_t_2 instanceof Exception) {
                  final Exception e_2 = (Exception)_t_2;
                  logger.log(Level.SEVERE, 
                    "Memory script of join vertices not found. Please contact Preesm developers.");
                } else {
                  throw Exceptions.sneakyThrow(_t_2);
                }
              }
            }
          }
          if (!_matched) {
            if (Objects.equal(_switchValue,DAGBroadcastVertex.DAG_BROADCAST_VERTEX)) {
              _matched=true;
              String _xifexpression = null;
              if ((sdfVertex instanceof SDFRoundBufferVertex)) {
                _xifexpression = "/../scripts/roundbuffer.bsh";
              } else {
                _xifexpression = "/../scripts/broadcast.bsh";
              }
              String classpathString_3 = _xifexpression;
              try {
                Class<? extends ScriptRunner> _class_3 = this.getClass();
                URL _resource_3 = _class_3.getResource(classpathString_3);
                final URI sourceStream_3 = _resource_3.toURI();
                URL _uRL_3 = sourceStream_3.toURL();
                URL _resolve_3 = FileLocator.resolve(_uRL_3);
                String _file_7 = _resolve_3.getFile();
                File _file_8 = new File(_file_7);
                final File scriptFile_3 = _file_8;
                boolean _exists_5 = scriptFile_3.exists();
                if (_exists_5) {
                  this.scriptedVertices.put(dagVertex, scriptFile_3);
                }
              } catch (final Throwable _t_3) {
                if (_t_3 instanceof Exception) {
                  final Exception e_3 = (Exception)_t_3;
                  logger.log(Level.SEVERE, 
                    "Memory script of broadcast/roundbuffer vertices not found. Please contact Preesm developers.");
                } else {
                  throw Exceptions.sneakyThrow(_t_3);
                }
              }
            }
          }
        }
      }
    }
  }
  
  public boolean check(final File script, final Pair<List<Buffer>,List<Buffer>> result) {
    boolean _xblockexpression = false;
    {
      ArrayList<Buffer> _arrayList = new ArrayList<Buffer>();
      final ArrayList<Buffer> allBuffers = _arrayList;
      List<Buffer> _key = result.getKey();
      allBuffers.addAll(_key);
      List<Buffer> _value = result.getValue();
      allBuffers.addAll(_value);
      boolean _and = false;
      List<Buffer> _key_1 = result.getKey();
      final Function1<Buffer,Boolean> _function = new Function1<Buffer,Boolean>() {
        public Boolean apply(final Buffer it) {
          FixedSizeList<Set<Match>> _matchTable = it.getMatchTable();
          final Function1<Set<Match>,Boolean> _function = new Function1<Set<Match>,Boolean>() {
            public Boolean apply(final Set<Match> it) {
              boolean _xifexpression = false;
              boolean _notEquals = (!Objects.equal(it, null));
              if (_notEquals) {
                final Function1<Match,Boolean> _function = new Function1<Match,Boolean>() {
                  public Boolean apply(final Match it) {
                    List<Buffer> _value = result.getValue();
                    Buffer _buffer = it.getBuffer();
                    boolean _contains = _value.contains(_buffer);
                    return Boolean.valueOf(_contains);
                  }
                };
                boolean _forall = IterableExtensions.<Match>forall(it, _function);
                _xifexpression = _forall;
              } else {
                _xifexpression = true;
              }
              return Boolean.valueOf(_xifexpression);
            }
          };
          boolean _forall = IterableExtensions.<Set<Match>>forall(_matchTable, _function);
          return Boolean.valueOf(_forall);
        }
      };
      boolean _forall = IterableExtensions.<Buffer>forall(_key_1, _function);
      if (!_forall) {
        _and = false;
      } else {
        List<Buffer> _value_1 = result.getValue();
        final Function1<Buffer,Boolean> _function_1 = new Function1<Buffer,Boolean>() {
          public Boolean apply(final Buffer it) {
            FixedSizeList<Set<Match>> _matchTable = it.getMatchTable();
            final Function1<Set<Match>,Boolean> _function = new Function1<Set<Match>,Boolean>() {
              public Boolean apply(final Set<Match> it) {
                boolean _xifexpression = false;
                boolean _notEquals = (!Objects.equal(it, null));
                if (_notEquals) {
                  final Function1<Match,Boolean> _function = new Function1<Match,Boolean>() {
                    public Boolean apply(final Match it) {
                      List<Buffer> _key = result.getKey();
                      Buffer _buffer = it.getBuffer();
                      boolean _contains = _key.contains(_buffer);
                      return Boolean.valueOf(_contains);
                    }
                  };
                  boolean _forall = IterableExtensions.<Match>forall(it, _function);
                  _xifexpression = _forall;
                } else {
                  _xifexpression = true;
                }
                return Boolean.valueOf(_xifexpression);
              }
            };
            boolean _forall = IterableExtensions.<Set<Match>>forall(_matchTable, _function);
            return Boolean.valueOf(_forall);
          }
        };
        boolean _forall_1 = IterableExtensions.<Buffer>forall(_value_1, _function_1);
        _and = (_forall && _forall_1);
      }
      final boolean res1 = _and;
      if ((!res1)) {
        final WorkflowLogger logger = WorkflowLogger.getLogger();
        logger.log(Level.WARNING, 
          (("Error in " + script) + 
            ": an input was directly matched with another input, or an output with another output."));
      }
      final Function1<Buffer,Boolean> _function_2 = new Function1<Buffer,Boolean>() {
        public Boolean apply(final Buffer it) {
          FixedSizeList<Set<Match>> _matchTable = it.getMatchTable();
          final Function1<Set<Match>,Boolean> _function = new Function1<Set<Match>,Boolean>() {
            public Boolean apply(final Set<Match> it) {
              boolean _xifexpression = false;
              boolean _and = false;
              boolean _notEquals = (!Objects.equal(it, null));
              if (!_notEquals) {
                _and = false;
              } else {
                int _size = it.size();
                boolean _greaterThan = (_size > 1);
                _and = (_notEquals && _greaterThan);
              }
              if (_and) {
                final Function1<Match,Boolean> _function = new Function1<Match,Boolean>() {
                  public Boolean apply(final Match it) {
                    Buffer _buffer = it.getBuffer();
                    FixedSizeList<Set<Match>> _matchTable = _buffer.getMatchTable();
                    int _index = it.getIndex();
                    Set<Match> _get = _matchTable.get(_index);
                    int _size = _get.size();
                    boolean _equals = (_size == 1);
                    return Boolean.valueOf(_equals);
                  }
                };
                boolean _forall = IterableExtensions.<Match>forall(it, _function);
                _xifexpression = _forall;
              } else {
                _xifexpression = true;
              }
              return Boolean.valueOf(_xifexpression);
            }
          };
          boolean _forall = IterableExtensions.<Set<Match>>forall(_matchTable, _function);
          return Boolean.valueOf(_forall);
        }
      };
      final boolean res2 = IterableExtensions.<Buffer>forall(allBuffers, _function_2);
      if ((!res2)) {
        final WorkflowLogger logger_1 = WorkflowLogger.getLogger();
        logger_1.log(Level.WARNING, 
          (("Error in " + script) + 
            ": A buffer element matched multiple times cannot be matched with an element that is itself matched multiple times."));
      }
      boolean _and_1 = false;
      if (!res1) {
        _and_1 = false;
      } else {
        _and_1 = (res1 && res2);
      }
      _xblockexpression = (_and_1);
    }
    return _xblockexpression;
  }
  
  /**
   * This method run the scripts that were found during the call to
   * {@link #findScripts()}. As a result, the {@link #scriptResults} is
   * filled.<br>
   * <br>
   * 
   * If the execution of a script fails, the {@link Interpreter} error message
   * will be printed in the {@link Logger log} as a warning.<br>
   * <br>
   * The {@link #check(List,List)} method is also used after each script
   * execution to verify the validity of the script results. If the results
   * are not valid, they will not be stored in the {@link #scriptResults}
   * {@link Map}, and a warning will be printed in the {@link Logger log}.
   */
  public void run() {
    try {
      Set<Map.Entry<DAGVertex,File>> _entrySet = this.scriptedVertices.entrySet();
      for (final Map.Entry<DAGVertex,File> e : _entrySet) {
        {
          final DAGVertex dagVertex = e.getKey();
          final File script = e.getValue();
          Interpreter _interpreter = new Interpreter();
          final Interpreter interpreter = _interpreter;
          PropertyBean _propertyBean = dagVertex.getPropertyBean();
          Object _value = _propertyBean.getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex.class);
          final SDFAbstractVertex sdfVertex = ((SDFAbstractVertex) _value);
          final Map<String,Integer> parameters = CollectionLiterals.<String, Integer>newHashMap();
          {
            PropertyBean _propertyBean_1 = sdfVertex.getPropertyBean();
            Object _value_1 = _propertyBean_1.getValue(SDFAbstractVertex.ARGUMENTS);
            final HashMap<String,Argument> arguments = ((HashMap<String,Argument>) _value_1);
            boolean _notEquals = (!Objects.equal(arguments, null));
            if (_notEquals) {
              Set<Map.Entry<String,Argument>> _entrySet_1 = arguments.entrySet();
              final Procedure1<Map.Entry<String,Argument>> _function = new Procedure1<Map.Entry<String,Argument>>() {
                public void apply(final Map.Entry<String,Argument> it) {
                  try {
                    String _key = it.getKey();
                    Argument _value = it.getValue();
                    int _intValue = _value.intValue();
                    parameters.put(_key, Integer.valueOf(_intValue));
                  } catch (Throwable _e) {
                    throw Exceptions.sneakyThrow(_e);
                  }
                }
              };
              IterableExtensions.<Map.Entry<String,Argument>>forEach(_entrySet_1, _function);
            }
          }
          Set<SDFEdge> _incomingEdges = ScriptRunner.incomingEdges(sdfVertex);
          final Function1<SDFEdge,Buffer> _function = new Function1<SDFEdge,Buffer>() {
            public Buffer apply(final SDFEdge it) {
              try {
                String _targetLabel = it.getTargetLabel();
                AbstractEdgePropertyType<? extends Object> _cons = it.getCons();
                int _intValue = _cons.intValue();
                Map<String,DataType> _dataTypes = ScriptRunner.this.getDataTypes();
                AbstractEdgePropertyType<? extends Object> _dataType = it.getDataType();
                String _string = _dataType.toString();
                DataType _get = _dataTypes.get(_string);
                Integer _size = _get.getSize();
                Buffer _buffer = new Buffer(_targetLabel, _intValue, (_size).intValue());
                return _buffer;
              } catch (Throwable _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            }
          };
          Iterable<Buffer> _map = IterableExtensions.<SDFEdge, Buffer>map(_incomingEdges, _function);
          final List<Buffer> inputs = IterableExtensions.<Buffer>toList(_map);
          Set<SDFEdge> _outgoingEdges = ScriptRunner.outgoingEdges(sdfVertex);
          final Function1<SDFEdge,Buffer> _function_1 = new Function1<SDFEdge,Buffer>() {
            public Buffer apply(final SDFEdge it) {
              try {
                String _sourceLabel = it.getSourceLabel();
                AbstractEdgePropertyType<? extends Object> _prod = it.getProd();
                int _intValue = _prod.intValue();
                Map<String,DataType> _dataTypes = ScriptRunner.this.getDataTypes();
                AbstractEdgePropertyType<? extends Object> _dataType = it.getDataType();
                String _string = _dataType.toString();
                DataType _get = _dataTypes.get(_string);
                Integer _size = _get.getSize();
                Buffer _buffer = new Buffer(_sourceLabel, _intValue, (_size).intValue());
                return _buffer;
              } catch (Throwable _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            }
          };
          Iterable<Buffer> _map_1 = IterableExtensions.<SDFEdge, Buffer>map(_outgoingEdges, _function_1);
          final List<Buffer> outputs = IterableExtensions.<Buffer>toList(_map_1);
          String _name = Buffer.class.getName();
          String _plus = ("import " + _name);
          String _plus_1 = (_plus + ";");
          interpreter.eval(_plus_1);
          String _name_1 = List.class.getName();
          String _plus_2 = ("import " + _name_1);
          String _plus_3 = (_plus_2 + ";");
          interpreter.eval(_plus_3);
          final Procedure2<String,Integer> _function_2 = new Procedure2<String,Integer>() {
            public void apply(final String name, final Integer value) {
              try {
                interpreter.set(name, value);
              } catch (Throwable _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            }
          };
          MapExtensions.<String, Integer>forEach(parameters, _function_2);
          final Procedure1<Buffer> _function_3 = new Procedure1<Buffer>() {
            public void apply(final Buffer it) {
              try {
                String _name = it.getName();
                String _plus = ("i_" + _name);
                interpreter.set(_plus, it);
              } catch (Throwable _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            }
          };
          IterableExtensions.<Buffer>forEach(inputs, _function_3);
          final Procedure1<Buffer> _function_4 = new Procedure1<Buffer>() {
            public void apply(final Buffer it) {
              try {
                String _name = it.getName();
                String _plus = ("o_" + _name);
                interpreter.set(_plus, it);
              } catch (Throwable _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            }
          };
          IterableExtensions.<Buffer>forEach(outputs, _function_4);
          Object _get = interpreter.get("parameters");
          boolean _equals = Objects.equal(_get, null);
          if (_equals) {
            interpreter.set("parameters", parameters);
          }
          Object _get_1 = interpreter.get("inputs");
          boolean _equals_1 = Objects.equal(_get_1, null);
          if (_equals_1) {
            interpreter.set("inputs", inputs);
          }
          Object _get_2 = interpreter.get("outputs");
          boolean _equals_2 = Objects.equal(_get_2, null);
          if (_equals_2) {
            interpreter.set("outputs", outputs);
          }
          try {
            String _absolutePath = script.getAbsolutePath();
            interpreter.source(_absolutePath);
            String _name_2 = sdfVertex.getName();
            String _plus_4 = (_name_2 + ": ");
            System.out.print(_plus_4);
            Pair<List<Buffer>,List<Buffer>> _mappedTo = Pair.<List<Buffer>, List<Buffer>>of(inputs, outputs);
            boolean _check = this.check(script, _mappedTo);
            System.out.println(_check);
            Pair<List<Buffer>,List<Buffer>> _mappedTo_1 = Pair.<List<Buffer>, List<Buffer>>of(inputs, outputs);
            this.scriptResults.put(dagVertex, _mappedTo_1);
          } catch (final Throwable _t) {
            if (_t instanceof EvalError) {
              final EvalError error = (EvalError)_t;
              final WorkflowLogger logger = WorkflowLogger.getLogger();
              String _name_3 = sdfVertex.getName();
              String _plus_5 = ("Evaluation error in " + _name_3);
              String _plus_6 = (_plus_5 + " memory script:\n[Line ");
              int _errorLineNumber = error.getErrorLineNumber();
              String _plus_7 = (_plus_6 + Integer.valueOf(_errorLineNumber));
              String _plus_8 = (_plus_7 + "] ");
              String _rawMessage = error.getRawMessage();
              String _plus_9 = (_plus_8 + _rawMessage);
              logger.log(Level.WARNING, _plus_9);
            } else if (_t instanceof IOException) {
              final IOException exception = (IOException)_t;
              exception.printStackTrace();
            } else {
              throw Exceptions.sneakyThrow(_t);
            }
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static Set<SDFEdge> incomingEdges(final SDFAbstractVertex vertex) {
    AbstractGraph _base = vertex.getBase();
    Set _incomingEdgesOf = _base.incomingEdgesOf(vertex);
    return _incomingEdgesOf;
  }
  
  public static Set<SDFEdge> outgoingEdges(final SDFAbstractVertex vertex) {
    AbstractGraph _base = vertex.getBase();
    Set _outgoingEdgesOf = _base.outgoingEdgesOf(vertex);
    return _outgoingEdgesOf;
  }
  
  public Object runTest() {
    try {
      Object _xblockexpression = null;
      {
        Interpreter _interpreter = new Interpreter();
        final Interpreter interpreter = _interpreter;
        String _name = Buffer.class.getName();
        String _plus = ("import " + _name);
        String _plus_1 = (_plus + ";");
        interpreter.eval(_plus_1);
        Pair<String,Integer> _mappedTo = Pair.<String, Integer>of("NbSlice", Integer.valueOf(3));
        Pair<String,Integer> _mappedTo_1 = Pair.<String, Integer>of("Overlap", Integer.valueOf(1));
        Pair<String,Integer> _mappedTo_2 = Pair.<String, Integer>of("Height", Integer.valueOf(3));
        Pair<String,Integer> _mappedTo_3 = Pair.<String, Integer>of("Width", Integer.valueOf(2));
        HashMap<String,Integer> parameters = CollectionLiterals.<String, Integer>newHashMap(_mappedTo, _mappedTo_1, _mappedTo_2, _mappedTo_3);
        final Procedure2<String,Integer> _function = new Procedure2<String,Integer>() {
          public void apply(final String name, final Integer value) {
            try {
              interpreter.set(name, value);
            } catch (Throwable _e) {
              throw Exceptions.sneakyThrow(_e);
            }
          }
        };
        MapExtensions.<String, Integer>forEach(parameters, _function);
        Integer _get = parameters.get("Height");
        Integer _get_1 = parameters.get("Width");
        int _multiply = ((_get).intValue() * (_get_1).intValue());
        Buffer _buffer = new Buffer("input", _multiply, 1);
        ArrayList<Buffer> inputs = CollectionLiterals.<Buffer>newArrayList(_buffer);
        final Procedure1<Buffer> _function_1 = new Procedure1<Buffer>() {
          public void apply(final Buffer it) {
            try {
              String _name = it.getName();
              String _plus = ("i_" + _name);
              interpreter.set(_plus, it);
            } catch (Throwable _e) {
              throw Exceptions.sneakyThrow(_e);
            }
          }
        };
        IterableExtensions.<Buffer>forEach(inputs, _function_1);
        Integer _get_2 = parameters.get("Height");
        Integer _get_3 = parameters.get("Width");
        int _multiply_1 = ((_get_2).intValue() * (_get_3).intValue());
        Integer _get_4 = parameters.get("NbSlice");
        Integer _get_5 = parameters.get("Overlap");
        int _multiply_2 = ((_get_4).intValue() * (_get_5).intValue());
        int _multiply_3 = (_multiply_2 * 2);
        Integer _get_6 = parameters.get("Width");
        int _multiply_4 = (_multiply_3 * (_get_6).intValue());
        int _plus_2 = (_multiply_1 + _multiply_4);
        Buffer _buffer_1 = new Buffer("output", _plus_2, 1);
        ArrayList<Buffer> outputs = CollectionLiterals.<Buffer>newArrayList(_buffer_1);
        final Procedure1<Buffer> _function_2 = new Procedure1<Buffer>() {
          public void apply(final Buffer it) {
            try {
              String _name = it.getName();
              String _plus = ("o_" + _name);
              interpreter.set(_plus, it);
            } catch (Throwable _e) {
              throw Exceptions.sneakyThrow(_e);
            }
          }
        };
        IterableExtensions.<Buffer>forEach(outputs, _function_2);
        Object _xtrycatchfinallyexpression = null;
        try {
          Object _xblockexpression_1 = null;
          {
            Class<? extends ScriptRunner> _class = this.getClass();
            URL _resource = _class.getResource("/../scripts/split.bsh");
            final URI sourceStream = _resource.toURI();
            URL _uRL = sourceStream.toURL();
            URL _resolve = FileLocator.resolve(_uRL);
            String _file = _resolve.getFile();
            File _file_1 = new File(_file);
            final File scriptFile = _file_1;
            Object _xifexpression = null;
            boolean _exists = scriptFile.exists();
            if (_exists) {
              String _absolutePath = scriptFile.getAbsolutePath();
              Object _source = interpreter.source(_absolutePath);
              _xifexpression = _source;
            }
            _xblockexpression_1 = (_xifexpression);
          }
          _xtrycatchfinallyexpression = _xblockexpression_1;
        } catch (final Throwable _t) {
          if (_t instanceof IOException) {
            final IOException e = (IOException)_t;
            e.printStackTrace();
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
        _xblockexpression = (_xtrycatchfinallyexpression);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
