package org.ietr.preesm.experiment.memory;

import bsh.Interpreter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.ietr.preesm.experiment.memory.Buffer;

@SuppressWarnings("all")
public class ScriptRunner {
  public void run() {
    try {
      Interpreter _interpreter = new Interpreter();
      final Interpreter interpreter = _interpreter;
      String _name = Buffer.class.getName();
      String _plus = ("import " + _name);
      String _plus_1 = (_plus + ";");
      interpreter.eval(_plus_1);
      Pair<String,Integer> _mappedTo = Pair.<String, Integer>of("NbSplit", Integer.valueOf(3));
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
      Integer _get_4 = parameters.get("NbSplit");
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
        Object _source = interpreter.source("D:/SVN-ParcoursRecherche/Preesm/script.bsh");
        _xtrycatchfinallyexpression = _source;
      } catch (final Throwable _t) {
        if (_t instanceof IOException) {
          final IOException e = (IOException)_t;
          Object _xblockexpression = null;
          {
            e.printStackTrace();
            _xblockexpression = (null);
          }
          _xtrycatchfinallyexpression = _xblockexpression;
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      Object o = _xtrycatchfinallyexpression;
      Buffer _get_7 = inputs.get(0);
      System.out.println(_get_7);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
