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
 
package org.ietr.preesm.plugin.mapper.commcontenlistsched;

import java.util.HashMap;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ComponentType;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.OperatorDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.SwitchDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.parser.ArchitectureParser;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.parser.ParameterParser;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.plotter.GanttPlotter;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.scheduler.AbstractScheduler;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.scheduler.CombCListSched;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.scheduler.CombCListSchedCc;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.scheduler.CombCListSchedCcCd;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.scheduler.CombCListSchedCd;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.jfree.ui.RefineryUtilities;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

public class CombListSched {

	private String architectureFileName = "D:\\Projets\\PreesmSourceForge\\trunk\\plugins\\mapper\\src\\org\\ietr\\preesm\\plugin\\mapper\\commcontenlistsched\\architecture.xml";

	private String parameterFileName = "D:\\Projets\\PreesmSourceForge\\trunk\\plugins\\mapper\\src\\org\\ietr\\preesm\\plugin\\mapper\\commcontenlistsched\\parameter.xml";

	private AlgorithmTransformer algoTransformer = null;

	private ArchitectureTransformer archiTransformer = null;

	private ScenarioTransformer scenaTransformer = null;

	private SDFGraph sdf = null;
	private MultiCoreArchitecture architecture = null;
	private IScenario scenario = null;

	private MapperDAG dag = null;

	private AlgorithmDescriptor algo = null;
	private ArchitectureDescriptor archi = null;
	private HashMap<String, Integer> computationWeights = null;

	private AbstractScheduler bestScheduler = null;

	private int bestScheduleLength = Integer.MAX_VALUE;

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

	private void parse() {
		if (dag != null) {
			System.out.println("Transform DAG to algorithm...");
			algo = algoTransformer.dag2Algorithm(dag);
		} else {
			System.out.println("Transform SDF to algorithm...");
			algo = algoTransformer.sdf2Algorithm(sdf);
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

	public void schedule() {
		System.out
				.println("\n***** Combined List Scheduling With Static Order Begins! *****");
		if ((architecture != null) && (scenario != null)) {
			dag = SdfToDagConverter.convert(sdf, architecture, scenario, false);
		}
		parse();

		CombCListSched scheduler1 = new CombCListSched(algo.clone(), archi
				.clone());
		scheduler1.schedule();

		CombCListSchedCc scheduler2 = new CombCListSchedCc(algo.clone(), archi
				.clone());
		scheduler2.schedule();

		CombCListSchedCd scheduler3 = new CombCListSchedCd(algo.clone(), archi
				.clone());
		scheduler3.schedule();

		CombCListSchedCcCd scheduler4 = new CombCListSchedCcCd(algo.clone(),
				archi.clone());
		scheduler4.schedule();

		chooseBestScheduler(scheduler1.getBestScheduler());
		chooseBestScheduler(scheduler2.getBestScheduler());
		chooseBestScheduler(scheduler3.getBestScheduler());
		chooseBestScheduler(scheduler4.getBestScheduler());

		System.out.println("***Compared Results***");

		System.out
				.print("No.\tScheduling Method\t\t\t\t\t\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

		System.out.print("\n1\t" + scheduler1.getName() + "\t\t\t\t\t\t"
				+ scheduler1.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ scheduler1.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler1
				.getBestScheduler().getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n2\t" + scheduler2.getName() + "\t\t\t\t"
				+ scheduler2.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ scheduler2.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler2
				.getBestScheduler().getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n3\t" + scheduler3.getName() + "\t\t\t"
				+ scheduler3.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ scheduler3.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler3
				.getBestScheduler().getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n4\t" + scheduler4.getName() + "\t"
				+ scheduler4.getBestScheduler().getScheduleLength() + "\t\t\t"
				+ scheduler4.getBestScheduler().getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler4
				.getBestScheduler().getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n\nBest Scheduler:\t\t" + bestScheduler.getName()
				+ "\nSchedule Length:\t" + bestScheduler.getScheduleLength()
				+ "\nUsed Operators:\t\t"
				+ bestScheduler.getUsedOperators().size());

		plot(bestScheduler);
		System.out
				.println("\n\n***** Combined List Scheduling With Static Order Finishes!*****");
	}

	private void chooseBestScheduler(AbstractScheduler scheduler) {
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

	public AbstractScheduler getBestScheduler() {
		return bestScheduler;
	}

	public int getBestScheduleLength() {
		return bestScheduleLength;
	}

	private void plot(AbstractScheduler scheduler) {
		GanttPlotter plot = new GanttPlotter(scheduler.getName()
				+ " -> Schedule Length=" + scheduler.getScheduleLength(),
				scheduler);
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\commcontenlistsched\\architecture.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\commcontenlistsched\\parameter.xml";

		CombListSched test = new CombListSched(parameterFileName,
				architectureFileName);
		test.schedule();
	}
}
