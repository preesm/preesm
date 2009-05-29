/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
package org.ietr.preesm.plugin.mapper.listsched;

import java.util.HashMap;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComponentType;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.OperatorDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.SwitchDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.parser.ArchitectureParser;
import org.ietr.preesm.plugin.mapper.listsched.parser.ParameterParser;
import org.ietr.preesm.plugin.mapper.listsched.plotter.GanttPlotter;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.AbstractScheduler;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CombCDListSched;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CombCDListSchedCc;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CombCDListSchedCcCd;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CombCDListSchedCd;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CombCSListSched;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CombCSListSchedCc;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CombCSListSchedCcCd;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CombCSListSchedCd;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.jfree.ui.RefineryUtilities;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * The CombListSched combines different list scheduling methods to obtain the
 * best schedule result
 * 
 * @author pmu
 * 
 */
public class CombListSched {

	/**
	 * @param args
	 * @throws InvalidExpressionException
	 */
	public static void main(String[] args) throws InvalidExpressionException {
		// TODO Auto-generated method stub
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\listsched\\architecture.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\listsched\\parameter.xml";

		CombListSched test = new CombListSched(parameterFileName,
				architectureFileName);
		test.schedule();
	}

	/**
	 * The file name of an architecture
	 */
	private String architectureFileName = "D:\\Projets\\PreesmSourceForge\\trunk\\plugins\\mapper\\src\\org\\ietr\\preesm\\plugin\\mapper\\listsched\\architecture.xml";

	/**
	 * The file name of parameters
	 */
	private String parameterFileName = "D:\\Projets\\PreesmSourceForge\\trunk\\plugins\\mapper\\src\\org\\ietr\\preesm\\plugin\\mapper\\listsched\\parameter.xml";

	/**
	 * The AlgorithmTransformer
	 */
	private AlgorithmTransformer algoTransformer = null;

	/**
	 * The ArchitectureTransformer
	 */
	private ArchitectureTransformer archiTransformer = null;

	/**
	 * The ScenarioTransformer
	 */
	private ScenarioTransformer scenaTransformer = null;

	/**
	 * The SDFGraph
	 */
	private SDFGraph sdf = null;

	/**
	 * The MultiCoreArchitecture
	 */
	private MultiCoreArchitecture architecture = null;

	/**
	 * The Scenario
	 */
	private IScenario scenario = null;

	/**
	 * The MapperDAG
	 */
	private MapperDAG dag = null;

	/**
	 * The AlgorithmDescriptor
	 */
	private AlgorithmDescriptor algo = null;

	/**
	 * The ArchitectureDescriptor
	 */
	private ArchitectureDescriptor archi = null;

	/**
	 * The vertices' weights of random SDFGraph
	 */
	private HashMap<String, Integer> computationWeights = null;

	/**
	 * The scheduler that gives the best result
	 */
	private AbstractScheduler bestScheduler = null;

	/**
	 * The best schedule length
	 */
	private int bestScheduleLength = Integer.MAX_VALUE;

	/**
	 * Construct the CombListSched using the given SDFGraph,
	 * MultiCoreArchitecture and Scenario
	 * 
	 * @param sdf
	 *            An SDFGraph
	 * @param architecture
	 *            A MultiCoreArchitecture
	 * @param scenario
	 *            A Scenario
	 */
	public CombListSched(SDFGraph sdf, MultiCoreArchitecture architecture,
			IScenario scenario) {
		algoTransformer = new AlgorithmTransformer();
		archiTransformer = new ArchitectureTransformer();
		scenaTransformer = new ScenarioTransformer();
		this.sdf = sdf;
		this.architecture = architecture;
		this.scenario = scenario;
		computationWeights = algoTransformer.generateRandomNodeWeight(sdf, 500,
				1000);
	}

	/**
	 * Construct the CombListSched using the given parameter file and
	 * architecture file, the algorithm is a random SDFGraph
	 * 
	 * @param parameterFileName
	 *            File name of parameters
	 * @param architectureFileName
	 *            File name of architecture
	 */
	public CombListSched(String parameterFileName, String architectureFileName) {
		this.architectureFileName = architectureFileName;
		this.parameterFileName = parameterFileName;
		algoTransformer = new AlgorithmTransformer();

		// Generating random DAG-like SDF
		int nbVertex = 100, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		sdf = algoTransformer.randomSDF(nbVertex, minInDegree, maxInDegree,
				minOutDegree, maxOutDegree, 500, 1000);
		computationWeights = algoTransformer.generateRandomNodeWeight(sdf, 500,
				1000);
	}

	/**
	 * Compare a scheduler to the best one, the best scheduler is replaced if
	 * the new one is better
	 * 
	 * @param scheduler
	 *            A scheduler to be compared with the best one
	 */
	private void compareScheduler(AbstractScheduler scheduler) {
		if (bestScheduleLength > scheduler.getScheduleLength()) {
			bestScheduler = scheduler;
			bestScheduleLength = scheduler.getScheduleLength();
		} else if (bestScheduleLength == scheduler.getScheduleLength()) {
			if (bestScheduler.getUsedOperators().size() > scheduler
					.getUsedOperators().size()) {
				bestScheduler = scheduler;
				bestScheduleLength = scheduler.getScheduleLength();
			}
		}
	}

	/**
	 * Get the best schedule length
	 * 
	 * @return The best schedule length
	 */
	public int getBestScheduleLength() {
		return bestScheduleLength;
	}

	/**
	 * Get the best scheduler
	 * 
	 * @return The best scheduler
	 */
	public AbstractScheduler getBestScheduler() {
		return bestScheduler;
	}

	/**
	 * Parse the algorithm, architecture and scenario
	 * 
	 * @throws InvalidExpressionException
	 */
	private void parse() throws InvalidExpressionException {
		if (dag != null) {
			System.out.println("Transform DAG to algorithm...");
			algo = algoTransformer.dag2Algorithm(dag);
		} else {
			System.out.println("Transform SDF to algorithm...");
			algo = algoTransformer.sdf2Algorithm(sdf, scenario);
		}

		if (architecture != null) {
			System.out.println("Transform architecture...");
			archi = archiTransformer.architecture2Descriptor(architecture);
		} else {
			System.out.println("Parse architecture...");
			archi = new ArchitectureDescriptor();
			// Parse the design architecture document
			new ArchitectureParser(architectureFileName, archi).parse();
		}

		if (scenario != null) {
			System.out.println("Parse scenario...");
			scenaTransformer.parseScenario(scenario, algo, archi);
		} else {
			// set computation time with default weights
			for (SDFAbstractVertex indexVertex : sdf.vertexSet()) {
				algo.getComputation(indexVertex.getName()).setTime(
						computationWeights.get(indexVertex.getName()));
			}
			// Parse the design parameter document
			System.out.println("Parse parameters...");
			new ParameterParser(parameterFileName, archi, algo).parse();
			OperatorDescriptor defaultOperator = null;
			SwitchDescriptor defaultSwitch = null;
			LinkDescriptor defaultLink = null;
			for (ComponentDescriptor indexComponent : archi.getComponents()
					.values()) {
				if ((indexComponent.getType() == ComponentType.Ip || indexComponent
						.getType() == ComponentType.Processor)
						&& indexComponent.getId().equalsIgnoreCase(
								indexComponent.getName())) {
					defaultOperator = (OperatorDescriptor) indexComponent;
				} else if (indexComponent.getType() == ComponentType.Switch
						&& indexComponent.getId().equalsIgnoreCase(
								indexComponent.getName())) {
					defaultSwitch = (SwitchDescriptor) indexComponent;
				} else if ((indexComponent.getType() == ComponentType.Bus || indexComponent
						.getType() == ComponentType.Processor)
						&& indexComponent.getId().equalsIgnoreCase(
								indexComponent.getName())) {
					defaultLink = (LinkDescriptor) indexComponent;
				}
			}
			System.out.println(" default operator: Id="
					+ defaultOperator.getId() + "; Name="
					+ defaultOperator.getName());
			System.out.println(" default switch: Id=" + defaultSwitch.getId()
					+ "; Name=" + defaultSwitch.getName());
			System.out.println(" default link: Id=" + defaultLink.getId()
					+ "; Name=" + defaultLink.getName());

			System.out.println("Computations in the algorithm:");
			for (ComputationDescriptor indexComputation : algo
					.getComputations().values()) {
				// Allow a computation to be executed on each operator
				for (OperatorDescriptor indexOperator : archi.getAllOperators()
						.values()) {
					indexComputation.addOperator(indexOperator);
				}
				if (!indexComputation.getComputationDurations().containsKey(
						defaultOperator)) {
					indexComputation.addComputationDuration(defaultOperator,
							indexComputation.getTime());
					System.out.println(" Name="
							+ indexComputation.getName()
							+ "; default computationDuration="
							+ indexComputation
									.getComputationDuration(defaultOperator
											.getId()) + "; nbTotalRepeate="
							+ indexComputation.getNbTotalRepeat());
				}
			}
			System.out.println("Communications in the algorithm:");
			for (CommunicationDescriptor indexCommunication : algo
					.getCommunications().values()) {
				if (!indexCommunication.getCommunicationDurations()
						.containsKey(defaultSwitch)) {
					indexCommunication.addCommunicationDuration(defaultSwitch,
							indexCommunication.getWeight());
					System.out.println(" Name="
							+ indexCommunication.getName()
							+ "; default communicationDuration="
							+ indexCommunication
									.getCommunicationDuration(defaultSwitch));
				}
				if (!indexCommunication.getCommunicationDurations()
						.containsKey(defaultLink)) {
					indexCommunication.addCommunicationDuration(defaultLink,
							indexCommunication.getWeight());
					System.out.println(" Name="
							+ indexCommunication.getName()
							+ "; default communicationDuration="
							+ indexCommunication
									.getCommunicationDuration(defaultLink));
				}
			}
		}

		System.out.println("Operators in the architecture:");
		for (OperatorDescriptor indexOperator : archi.getAllOperators()
				.values()) {
			System.out.println(" Id=" + indexOperator.getId() + "; Name="
					+ indexOperator.getName());
		}
		System.out.println("Switches in the architecture:");
		for (SwitchDescriptor indexSwitch : archi.getAllSwitches().values()) {
			System.out.println(" Id=" + indexSwitch.getId() + "; Name="
					+ indexSwitch.getName());
		}
		System.out.println("Media(Buses) in the architecture:");
		for (LinkDescriptor indexLink : archi.getAllLinks().values()) {
			System.out.println(" Id=" + indexLink.getId() + "; Name="
					+ indexLink.getName());
		}
	}

	/**
	 * Plot the Gantt Chart for the given scheduler
	 * 
	 * @param scheduler
	 *            A scheduler to be used for the Gantt Chart
	 */
	private void plot(AbstractScheduler scheduler) {
		GanttPlotter plot = new GanttPlotter(scheduler.getName()
				+ " -> Schedule Length=" + scheduler.getScheduleLength(),
				scheduler);
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);
	}

	/**
	 * Use different list scheduling methods to schedule
	 * 
	 * @throws InvalidExpressionException
	 */
	public void schedule() throws InvalidExpressionException {
		System.out
				.println("\n***** Combined List Scheduling With Static Order Begins! *****");
		if ((architecture != null) && (scenario != null)) {
			dag = SdfToDagConverter.convert(sdf, architecture, scenario, false);
		}
		parse();

		/* Static */
		AlgorithmDescriptor algoCloned = algo.clone();
		ArchitectureDescriptor archiCloned = archi.clone();
//		archiCloned.setArchi(null);
		CombCSListSched scheduler1 = new CombCSListSched(algoCloned,
				archiCloned);
		scheduler1.schedule();
		// CombCSListSched scheduler1 = new CombCSListSched(algo.clone(), archi
		// .clone());
		// scheduler1.schedule();

		algoCloned = algo.clone();
		archiCloned = archi.clone();
//		archiCloned.setArchi(null);
		CombCSListSchedCc scheduler2 = new CombCSListSchedCc(algoCloned,
				archiCloned);
		scheduler2.schedule();

		algoCloned = algo.clone();
		archiCloned = archi.clone();
//		archiCloned.setArchi(null);
		CombCSListSchedCd scheduler3 = new CombCSListSchedCd(algoCloned,
				archiCloned);
		scheduler3.schedule();

		algoCloned = algo.clone();
		archiCloned = archi.clone();
//		archiCloned.setArchi(null);
		CombCSListSchedCcCd scheduler4 = new CombCSListSchedCcCd(algoCloned,
				archiCloned);
		scheduler4.schedule();

		compareScheduler(scheduler1.getBestScheduler());
		compareScheduler(scheduler2.getBestScheduler());
		compareScheduler(scheduler3.getBestScheduler());
		compareScheduler(scheduler4.getBestScheduler());

		/* Dynamic */
		algoCloned = algo.clone();
		archiCloned = archi.clone();
//		archiCloned.setArchi(null);
		CombCDListSched schedulerD1 = new CombCDListSched(algoCloned,
				archiCloned);
		schedulerD1.schedule();

		algoCloned = algo.clone();
		archiCloned = archi.clone();
//		archiCloned.setArchi(null);
		CombCDListSchedCc schedulerD2 = new CombCDListSchedCc(algoCloned,
				archiCloned);
		schedulerD2.schedule();

		algoCloned = algo.clone();
		archiCloned = archi.clone();
//		archiCloned.setArchi(null);
		CombCDListSchedCd schedulerD3 = new CombCDListSchedCd(algoCloned,
				archiCloned);
		schedulerD3.schedule();

		algoCloned = algo.clone();
		archiCloned = archi.clone();
//		archiCloned.setArchi(null);
		CombCDListSchedCcCd schedulerD4 = new CombCDListSchedCcCd(algoCloned,
				archiCloned);
		schedulerD4.schedule();

		compareScheduler(schedulerD1.getBestScheduler());
		compareScheduler(schedulerD2.getBestScheduler());
		compareScheduler(schedulerD3.getBestScheduler());
		compareScheduler(schedulerD4.getBestScheduler());

		System.out.println("***Compared Results***");

		System.out
				.print("No.\tScheduling Method\t\t\t\t\t\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

		/* Static */
		System.out.print("\n1\t" + scheduler1.getName() + "\t\t\t\t\t\t"
				+ scheduler1.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ scheduler1.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler1
				.getBestScheduler().getStaticOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		System.out.println("\n\tBlcomp:"
				+ scheduler1.getSchedulerBlcomp().getScheduleLength()
				+ "\n\tBl:" + scheduler1.getSchedulerBl().getScheduleLength()
				+ "\n\tBlin:"
				+ scheduler1.getSchedulerBlin().getScheduleLength()
				+ "\n\tBlout:"
				+ scheduler1.getSchedulerBlout().getScheduleLength()
				+ "\n\tBlinout:"
				+ scheduler1.getSchedulerBlinout().getScheduleLength());

		System.out.print("\n2\t" + scheduler2.getName() + "\t\t\t\t"
				+ scheduler2.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ scheduler2.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler2
				.getBestScheduler().getStaticOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		System.out.println("\n\tBlcomp:"
				+ scheduler2.getSchedulerBlcomp().getScheduleLength()
				+ "\n\tBl:" + scheduler2.getSchedulerBl().getScheduleLength()
				+ "\n\tBlin:"
				+ scheduler2.getSchedulerBlin().getScheduleLength()
				+ "\n\tBlout:"
				+ scheduler2.getSchedulerBlout().getScheduleLength()
				+ "\n\tBlinout:"
				+ scheduler2.getSchedulerBlinout().getScheduleLength());

		System.out.print("\n3\t" + scheduler3.getName() + "\t\t\t"
				+ scheduler3.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ scheduler3.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler3
				.getBestScheduler().getStaticOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		System.out.println("\n\tBlcomp:"
				+ scheduler3.getSchedulerBlcomp().getScheduleLength()
				+ "\n\tBl:" + scheduler3.getSchedulerBl().getScheduleLength()
				+ "\n\tBlin:"
				+ scheduler3.getSchedulerBlin().getScheduleLength()
				+ "\n\tBlout:"
				+ scheduler3.getSchedulerBlout().getScheduleLength()
				+ "\n\tBlinout:"
				+ scheduler3.getSchedulerBlinout().getScheduleLength());

		System.out.print("\n4\t" + scheduler4.getName() + "\t"
				+ scheduler4.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ scheduler4.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler4
				.getBestScheduler().getStaticOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		System.out.println("\n\tBlcomp:"
				+ scheduler4.getSchedulerBlcomp().getScheduleLength()
				+ "\n\tBl:" + scheduler4.getSchedulerBl().getScheduleLength()
				+ "\n\tBlin:"
				+ scheduler4.getSchedulerBlin().getScheduleLength()
				+ "\n\tBlout:"
				+ scheduler4.getSchedulerBlout().getScheduleLength()
				+ "\n\tBlinout:"
				+ scheduler4.getSchedulerBlinout().getScheduleLength());

		/* Dynamic */
		System.out.print("\nD1\t" + schedulerD1.getName() + "\t\t\t\t\t\t"
				+ schedulerD1.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ schedulerD1.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerD1
				.getBestScheduler().getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		System.out.println("\n\tBlcomp:"
				+ schedulerD1.getSchedulerBlcomp().getScheduleLength()
				+ "\n\tBl:" + schedulerD1.getSchedulerBl().getScheduleLength()
				+ "\n\tBlin:"
				+ schedulerD1.getSchedulerBlin().getScheduleLength()
				+ "\n\tBlout:"
				+ schedulerD1.getSchedulerBlout().getScheduleLength()
				+ "\n\tBlinout:"
				+ schedulerD1.getSchedulerBlinout().getScheduleLength());

		System.out.print("\nD2\t" + schedulerD2.getName() + "\t\t\t\t"
				+ schedulerD2.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ schedulerD2.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerD2
				.getBestScheduler().getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		System.out.println("\n\tBlcomp:"
				+ schedulerD2.getSchedulerBlcomp().getScheduleLength()
				+ "\n\tBl:" + schedulerD2.getSchedulerBl().getScheduleLength()
				+ "\n\tBlin:"
				+ schedulerD2.getSchedulerBlin().getScheduleLength()
				+ "\n\tBlout:"
				+ schedulerD2.getSchedulerBlout().getScheduleLength()
				+ "\n\tBlinout:"
				+ schedulerD2.getSchedulerBlinout().getScheduleLength());

		System.out.print("\nD3\t" + schedulerD3.getName() + "\t\t\t"
				+ schedulerD3.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ schedulerD3.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerD3
				.getBestScheduler().getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		System.out.println("\n\tBlcomp:"
				+ schedulerD3.getSchedulerBlcomp().getScheduleLength()
				+ "\n\tBl:" + schedulerD3.getSchedulerBl().getScheduleLength()
				+ "\n\tBlin:"
				+ schedulerD3.getSchedulerBlin().getScheduleLength()
				+ "\n\tBlout:"
				+ schedulerD3.getSchedulerBlout().getScheduleLength()
				+ "\n\tBlinout:"
				+ schedulerD3.getSchedulerBlinout().getScheduleLength());

		System.out.print("\nD4\t" + schedulerD4.getName() + "\t"
				+ schedulerD4.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ schedulerD4.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerD4
				.getBestScheduler().getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		System.out.println("\n\tBlcomp:"
				+ schedulerD4.getSchedulerBlcomp().getScheduleLength()
				+ "\n\tBl:" + schedulerD4.getSchedulerBl().getScheduleLength()
				+ "\n\tBlin:"
				+ schedulerD4.getSchedulerBlin().getScheduleLength()
				+ "\n\tBlout:"
				+ schedulerD4.getSchedulerBlout().getScheduleLength()
				+ "\n\tBlinout:"
				+ schedulerD4.getSchedulerBlinout().getScheduleLength());

		/* Best */
		System.out.print("\n\nBest Scheduler:\t\t" + bestScheduler.getName()
				+ "\nSchedule Length:\t" + bestScheduler.getScheduleLength()
				+ "\nUsed Operators:\t\t"
				+ bestScheduler.getUsedOperators().size());

		plot(bestScheduler);
		System.out
				.println("\n\n***** Combined List Scheduling With Static Order Finishes!*****");
		/* Test */
//		plot(scheduler1.getBestScheduler());
//		plot(scheduler2.getBestScheduler());
//		plot(scheduler3.getBestScheduler());
//		plot(scheduler4.getBestScheduler());
//		plot(schedulerD1.getBestScheduler());
//		plot(schedulerD2.getBestScheduler());
//		plot(schedulerD3.getBestScheduler());
//		plot(schedulerD4.getBestScheduler());
	}
}
