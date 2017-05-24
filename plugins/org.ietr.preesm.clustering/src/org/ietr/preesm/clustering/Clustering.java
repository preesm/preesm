package org.ietr.preesm.clustering;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 */
public class Clustering extends AbstractTaskImplementation {

  Logger logger = WorkflowLogger.getLogger();

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {
    final Map<String, Object> outputs = new HashMap<>();
    final SDFGraph inputSdf = (SDFGraph) inputs.get("SDF");
    final SDFGraph outputSdf = inputSdf.clone();
    computeHierarchizedGraph(outputSdf);
    p("Clustering computeHierarchizedGraph Done !" + inputs.toString());
    outputs.put("SDF", outputSdf);
    p("Clustering Done !" + inputs.toString());
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> defaultParams = new HashMap<>();
    defaultParams.put("factor", "1");
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "Execute Clustering.";
  }

  private SDFGraph computeHierarchizedGraph(final SDFGraph sdfGraph) {

    // go through all vertexes
    for (final SDFAbstractVertex v : sdfGraph.vertexSet()) {
      try {
        final int nbRepeat = v.getNbRepeatAsInteger();

        final Object refinement = v.getPropertyBean().getValue(AbstractVertex.REFINEMENT);

        /* Hierarchical graph */
        if (refinement instanceof AbstractGraph) {
          p("Hierarchical graph of name " + v.getName() + " rep " + nbRepeat);

          final SDFGraph h = (SDFGraph) v.getGraphDescription();
          for (final SDFEdge e : h.edgeSet()) {
            p("SDFEdge source " + e.getSourceLabel() + " target " + e.getTargetLabel());
          }
          for (final SDFAbstractVertex vi : h.vertexSet()) {
            p("SDFAbstractVertex " + vi.getName() + " repeated " + vi.getNbRepeatAsInteger());
          }
        } else if (v instanceof SDFInterfaceVertex) {
          /* Interface */
          p("SDFInterfaceVertex of name " + v.getName() + " rep " + nbRepeat);
        } else if (v instanceof SDFVertex) {
          /* Vextex */
          p("SDFVertex of name " + v.getName() + " rep " + nbRepeat);

          // if vertex and vertex.nb_repeat > 1
          if (v.getNbRepeatAsInteger() > 1) {
            p("Auto Transform " + v.getName() + " rep " + nbRepeat);
            final SDFAbstractVertex vGraph = v.clone(); // new SDFVertex();
            // vGraph.getPropertyBean().setValue(AbstractVertex.REFINEMENT, new SDFGraph());
            vGraph.setName(new String("Hierarchical_" + v.getName()));
            // sdfGraph.addVertex(vGraph);
            vGraph.setNbRepeat(1);

            for (final SDFInterfaceVertex iv : v.getSources()) {
              p("SDFInterfaceVertex sources " + iv.getName());
            }
            for (final SDFInterfaceVertex iv : v.getSinks()) {
              p("SDFInterfaceVertex sinks " + iv.getName());
            }
          }
        } else if (v instanceof SDFAbstractVertex) {
          p("SDFAbstractVertex of name " + v.getName() + " rep " + nbRepeat);
        } else {
          p("Unkown vertex of name " + v.getName() + " rep " + nbRepeat);
        }

        // if(nbRepeat > 1){
        // /* update source cons */
        // for(int i=0;i<hVertex.getSources().size();i++){
        // SDFEdge e = hVertex.getAssociatedEdge(hVertex.getSources().get(i));
        // p("update edge source cons " + e.getSourceLabel() + " " + e.getTargetLabel() +
        // " from " + e.getCons().intValue() + " to " + e.getCons().intValue()*nbRepeat);
        // e.setCons(new SDFDoubleEdgePropertyType(nbRepeat*e.getCons().intValue()));
        // }
        // /* update target prod */
        // for(int i=0;i<hVertex.getSinks().size();i++){
        // SDFEdge e = hVertex.getAssociatedEdge(hVertex.getSinks().get(i));
        // p("update edge target prod " + e.getSourceLabel() + " " + e.getTargetLabel() +
        // " from " + e.getProd().intValue() + " to " + e.getProd().intValue()*nbRepeat);
        // e.setProd(new SDFDoubleEdgePropertyType(nbRepeat*e.getProd().intValue()));
        // }
        //
        // SDFAbstractVertex v = hVertex.clone(); // v is the actor which is going to be repeated (for loop)
        // hVertex.setNbRepeat(1); // hierarchical actor triggered one time
        // SDFGraph ng = new SDFGraph(); // related sdf graph to the hierarchical actor
        // hVertex.setGraphDescription(ng);
        // ng.addVertex(v);
        // for(int i=0; i<hVertex.getGraphDescription().edgeSet().size();i++ ){
        // p(hVertex.getGraphDescription().edgeSet().toArray()[i].toString());
        // }
        // hVertex.getGraphDescription().
        // }
        // p("vertice " + hVertex.getName() + " rep " + hVertex.getNbRepeatAsInteger());
      } catch (final InvalidExpressionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    /*
     * p("Start printing"); for(SDFAbstractVertex iv : sdfGraph.vertexSet()){ p(iv.getName()); } p("End printing");
     */

    /*
     * for(List<SDFAbstractVertex> sv : sdfGraph.getAllSubGraphs()){ for(SDFAbstractVertex v : sv){ if(v.getGraphDescription() != null &&
     * v.getGraphDescription() instanceof SDFGraph){ p("subgrapf vertice " + v.getName()); } } } for(SDFEdge e : sdfGraph.edgeSet()){ if(e != null){ try {
     * p("edge " + e.getSourceLabel() + " " + e.getCons().intValue() + " " + e.getTargetLabel() + " " + e.getProd().intValue()); } catch
     * (InvalidExpressionException e1) { // TODO Auto-generated catch block e1.printStackTrace(); } } }
     */
    return sdfGraph;
  }

  private void p(final String s) {
    this.logger.log(Level.INFO, s);
  }

}
