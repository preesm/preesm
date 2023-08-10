package org.preesm.codegen.model.clustering;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.MainSimsdpBlock;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.slam.Design;

public class CodegenModelGeneratorSimSDP {
  final Design              archi;
  final PiGraph             topgraph;
  protected MainSimsdpBlock mainSimsdpBlock;

  public CodegenModelGeneratorSimSDP(Design archi, PiGraph topgraph) {
    this.archi = archi;
    this.topgraph = topgraph;
    this.mainSimsdpBlock = CodegenModelUserFactory.eINSTANCE.createMainSimsdpBlock();
  }

  public List<Block> generate(Map<Long, String> nodeNames) {

    final String coreType = this.archi.getComponentHolder().getComponents().get(0).getVlnv().getName();
    mainSimsdpBlock.setCoreType(coreType);
    // init node names
    for (final Entry<Long, String> n : nodeNames.entrySet()) {
      mainSimsdpBlock.getNodeID().add(n.getKey().intValue(), n.getValue());
    }
    // init buffer
    for (final Fifo f : topgraph.getAllFifos()) {
      final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();

      buffer.setName(((AbstractActor) f.getSource()).getName() + "_" + f.getSourcePort().getName() + "__"
          + ((AbstractActor) f.getTarget()).getName() + "_" + f.getSourcePort().getName());
      buffer.setNbToken(f.getSourcePort().getExpression().evaluate());
      buffer.setType(f.getType());

      mainSimsdpBlock.getBuffers().add(buffer);
    }
    // init sub func
    for (final AbstractActor a : topgraph.getActors()) {
      if (a instanceof Actor) {
        final FunctionCall fct = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
        fct.setActorName(a.getName());
        fct.setName(a.getName());
        for (final ConfigInputPort cfg : a.getConfigInputPorts()) {
          final Constant argCfg = CodegenModelUserFactory.eINSTANCE.createConstant();
          argCfg.setName(cfg.getName());
          argCfg.setType("int");
          // param2arg(argCfg, a);
          argCfg.setComment(
              String.valueOf(((ExpressionHolder) cfg.getIncomingDependency().getSetter()).getExpression().evaluate())); // name
          fct.addParameter(argCfg, PortDirection.NONE);

          argCfg.setValue(0L);
        }
        for (final DataInputPort in : a.getDataInputPorts()) {
          final Constant argIn = CodegenModelUserFactory.eINSTANCE.createConstant();
          argIn.setName(in.getFifo().getSourcePort().getName());
          argIn.setType(in.getFifo().getType());
          // Constant i =0;
          argIn.setComment(buffer2arg(argIn, (AbstractActor) in.getFifo().getSource())); // name
          fct.addParameter(argIn, PortDirection.INPUT);
          argIn.setValue(1L);
        }
        for (final DataOutputPort out : a.getDataOutputPorts()) {
          final Constant argOut = CodegenModelUserFactory.eINSTANCE.createConstant();
          argOut.setName(out.getName());
          argOut.setType(out.getFifo().getType());

          argOut.setComment(buffer2arg(argOut, a)); // name
          fct.addParameter(argOut, PortDirection.OUTPUT);
          argOut.setValue(2L);
        }

        mainSimsdpBlock.getSubFunc().add(fct);
      }
    }

    // output
    final List<Block> resultList = new LinkedList<>();
    resultList.add(mainSimsdpBlock);

    return resultList;

  }

  private void param2arg(Constant argCfg, AbstractActor a) {
    // TODO Auto-generated method stub

  }

  private String buffer2arg(Constant arg, AbstractActor a) {
    final String result = "";
    // mainSimsdpBlock.getBuffers().stream().filter(x ->
    // x.getName().contains(a.getName())).filter(x->x.getName().contains(arg.getName())).forEach(x-> result="1");
    for (final Buffer bf : mainSimsdpBlock.getBuffers()) {
      if (bf.getName().contains(arg.getName()) && bf.getName().contains(a.getName())) {
        return bf.getName();
      }
    }
    return "42";

  }
}
