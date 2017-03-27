package xtend.writer;

import com.google.common.base.Objects;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;

@SuppressWarnings("all")
public class CWriter {
  public CharSequence writeHierarchyLevel(final String levelName, final EList<AbstractActor> vertices) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("void ");
    _builder.append(levelName, "");
    _builder.append(" (PiSDFGraph* graph, BaseVertex* parentVertex){");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("// Creating vertices.");
    _builder.newLine();
    {
      for(final AbstractActor vertex : vertices) {
        {
          Class<? extends AbstractActor> _class = vertex.getClass();
          boolean _equals = Objects.equal(_class, DataInputInterface.class);
          if (_equals) {
            _builder.append("\t  \t");
            _builder.append("PiSDFIfVertex* ");
            String _name = vertex.getName();
            _builder.append(_name, "	  	");
            _builder.append(" = (PiSDFIfVertex*)graph->addVertex(");
            String _name_1 = vertex.getName();
            _builder.append(_name_1, "	  	");
            _builder.append(", input_vertex);");
            _builder.newLineIfNotEmpty();
          }
        }
        {
          Class<? extends AbstractActor> _class_1 = vertex.getClass();
          boolean _equals_1 = Objects.equal(_class_1, DataOutputInterface.class);
          if (_equals_1) {
            _builder.append("\t  \t");
            _builder.append("PiSDFIfVertex* ");
            String _name_2 = vertex.getName();
            _builder.append(_name_2, "	  	");
            _builder.append(" = (PiSDFIfVertex*)graph->addVertex(");
            String _name_3 = vertex.getName();
            _builder.append(_name_3, "	  	");
            _builder.append(", output_vertex);");
            _builder.newLineIfNotEmpty();
          }
        }
        {
          Class<? extends AbstractActor> _class_2 = vertex.getClass();
          boolean _equals_2 = Objects.equal(_class_2, ConfigOutputInterface.class);
          if (_equals_2) {
            _builder.append("\t  \t");
            _builder.append("PiSDFConfigVertex* ");
            String _name_4 = vertex.getName();
            _builder.append(_name_4, "	  	");
            _builder.append(" = (PiSDFConfigVertex*)graph->addVertex(");
            String _name_5 = vertex.getName();
            _builder.append(_name_5, "	  	");
            _builder.append(", config_vertex);");
            _builder.newLineIfNotEmpty();
          }
        }
        {
          Class<? extends AbstractActor> _class_3 = vertex.getClass();
          boolean _equals_3 = Objects.equal(_class_3, Actor.class);
          if (_equals_3) {
            _builder.append("\t  \t");
            _builder.append("PiSDFVertex* ");
            String _name_6 = vertex.getName();
            _builder.append(_name_6, "	  	");
            _builder.append(" = (PiSDFVertex*)graph->addVertex(");
            String _name_7 = vertex.getName();
            _builder.append(_name_7, "	  	");
            _builder.append(", pisdf_vertex);");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    _builder.append("}");
    return _builder;
  }
}
