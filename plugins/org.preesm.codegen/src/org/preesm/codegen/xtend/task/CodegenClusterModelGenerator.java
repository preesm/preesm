package org.preesm.codegen.xtend.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.codegen.model.Block;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;

/**
 * @author dgageot
 *
 */
public class CodegenClusterModelGenerator extends AbstractCodegenModelGenerator {

  final Map<AbstractActor, Schedule> mapper;

  final PiGraph piAlgo;

  /**
   * @param archi
   *          architecture
   * @param algo
   *          PiGraph algorithm
   * @param scenario
   *          scenario
   * @param workflow
   *          workflow
   * @param mapper
   *          schedule associated with cluster
   */
  public CodegenClusterModelGenerator(final Design archi, final PiGraph algo, final Scenario scenario,
      final Workflow workflow, final Map<AbstractActor, Schedule> mapper) {
    super(archi, new MapperDAG(algo), null, scenario, workflow);
    this.mapper = mapper;
    this.piAlgo = algo;
  }

  @Override
  public List<Block> generate() {

    List<Block> blockList = new LinkedList<>();

    // // For every clustered actors of the map that contains a Schedule
    // for (Entry<AbstractActor, Schedule> e : mapper.entrySet()) {
    // // Get every FIFO and build a FIFO Buffer Map
    // Map<Fifo, Buffer> bufferMap = generateFifoBuffer(e.getValue());
    //
    // }

    return blockList;
  }

  // private Map<Fifo, Buffer> generateFifoBuffer(Schedule schedule) {
  // Map<Fifo, Buffer> bufferMap = new LinkedHashMap<>();
  //
  // // Retrieve Fifo involve in the schedule
  // Set<Fifo> fifoSet = new LinkedHashSet<>();
  // for (AbstractActor a : schedule.getActors()) {
  // a.getDataInputPorts().stream().forEach(x -> fifoSet.add(x.getIncomingFifo()));
  // a.getDataOutputPorts().stream().forEach(x -> fifoSet.add(x.getOutgoingFifo()));
  // }
  //
  // // Build Buffer block for each Fifo
  // Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(this.piAlgo, BRVMethod.LCM);
  // for (Fifo fifo : fifoSet) {long
  // Buffer buffer = CodegenFactory.eINSTANCE.createBuffer();
  // DataOutputPort dop = fifo.getSourcePort();
  // DataInputPort dip = fifo.getTargetPort();
  // // Retrieve the fifo outside the cluster if not included
  // if (dop.getContainingActor() instanceof DataInputInterface) {
  // // Case of a DataInputInterface
  // DataInputInterface dii = (DataInputInterface) dop.getContainingActor();
  // DataInputPort outsideDip = (DataInputPort) dii.getGraphPort();
  // DataOutputPort outsideDop = outsideDip.getIncomingFifo().getSourcePort();
  // buffer.setSize(outsideDip.getExpression().evaluate() * repetitionVector.get(outsideDip.getContainingActor()));
  // buffer.setName(outsideDop.getContainingActor().getName() + dip.getContainingActor().getName());
  // } else if (dip.getContainingActor() instanceof DataOutputInterface) {
  // // Case of a DataOutputInterface
  // DataOutputInterface doi = (DataOutputInterface) dip.getContainingActor();
  // DataOutputPort outsideDop = (DataOutputPort) doi.getGraphPort();
  // DataInputPort outsideDip = outsideDop.getOutgoingFifo().getTargetPort();
  // buffer.setSize(outsideDop.getExpression().evaluate() * repetitionVector.get(outsideDop.getContainingActor()));
  // buffer.setName(dop.getContainingActor().getName() + outsideDip.getContainingActor().getName());
  // } else {
  // // Fifo inside the cluster
  // buffer.setSize(dop.getExpression().evaluate() * repetitionVector.get(dop.getContainingActor()));
  // buffer.setType(fifo.getType());
  // buffer.setName(dop.getContainingActor().getName() + dip.getContainingActor().getName());
  // }
  // buffer.setTypeSize(scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));
  // bufferMap.put(fifo, buffer);
  // }
  //
  // return bufferMap;
  //
  // }

  // private AbstractActor retrieveNextAbstractActor(Fifo inputFifo) {
  //
  // DataOutputPort dop = inputFifo.getSourcePort();
  // DataInputPort dip = inputFifo.getTargetPort();
  // while ((dip instanceof InterfaceActor) || (dop instanceof InterfaceActor)) {
  // if (dop.getContainingActor() instanceof DataInputInterface) {
  // // Case of a DataInputInterface
  // } else if (dip.getContainingActor() instanceof DataOutputInterface) {
  // // Case of a DataOutputInterface
  // }
  //
  // }
  //
  // return Fifo;
  // }

}
