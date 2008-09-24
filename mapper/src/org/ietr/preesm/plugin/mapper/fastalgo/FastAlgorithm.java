/**
 * 
 */
package org.ietr.preesm.plugin.mapper.fastalgo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.constraints.Scenario;
import org.ietr.preesm.core.constraints.Timing;
import org.ietr.preesm.core.constraints.TimingManager;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.plot.PlotBestLatency;
import org.ietr.preesm.plugin.mapper.test.BenchmarkWriter;
import org.ietr.preesm.plugin.mapper.tools.OperatorIterator;
import org.ietr.preesm.plugin.mapper.tools.RandomIterator;
import org.jfree.ui.RefineryUtilities;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Fast Algorithm
 * 
 * @author pmenuet
 */
public class FastAlgorithm extends Observable {

	/**
	 * Main for tests
	 */
	public static void main(String[] args) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		// PreesmLogger.getLogger().setLevel(Level.FINER);
		DAGCreator dagCreator = new DAGCreator();

		// Generating archi
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		// Generating random sdf dag
		int nbVertex = 50, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 100,true);

		// Generating constraints
		IScenario scenario = new Scenario();

		TimingManager tmgr = scenario.getTimingManager();

		for (int i = 1; i <= nbVertex; i++) {
			String name = String.format("Vertex %d", i);
			Timing newt = new Timing(archi.getOperatorDefinition("c64x"), graph
					.getVertex(name), 50);
			tmgr.addTiming(newt);
		}

		// Converting sdf dag in mapper dag
		// MapperDAG dag = dagCreator.sdf2dag(graph, archi, constraints);
		MapperDAG dag = dagCreator.dagexample2(archi);

		IAbc simu = new InfiniteHomogeneousAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");

		InitialLists initial = new InitialLists();

		logger.log(Level.FINEST, "Evaluating constructInitialList ");
		initial.constructInitialLists(dag, simu);

		logger.log(Level.FINEST, "Displaying Cpndominantlist ");
		initial.orderlistdisplay(initial.getCpnDominantList());

		logger.log(Level.FINEST, "Displaying blockingNodes ");
		initial.orderlistdisplay(initial.getBlockingNodesList());

		logger.log(Level.FINEST, "Displaying fcp ");
		initial.orderlistdisplay(initial.getFinalcriticalpathList());

		ListScheduler scheduler = new ListScheduler();

		simu.resetDAG();

		logger.log(Level.FINEST, "Evaluating fast algo");
		FastAlgorithm algorithm = new FastAlgorithm();

		dag = algorithm.map("test", ArchitectureSimulatorType.LooselyTimed,
				dag, archi, initial.getCpnDominantList(), initial
						.getBlockingNodesList(), initial
						.getFinalcriticalpathList(), 50, 50, 5, false, true, null);

		IAbc simu2 = AbstractAbc
				.getInstance(ArchitectureSimulatorType.LooselyTimed, dag, archi);

		simu2.setDAG(dag);
		logger.log(Level.FINER, "Displaying dag implanted 2");
		scheduler.dagimplanteddisplay(dag, simu2);

		logger.log(Level.FINER, "SPlength " + dag.getScheduleLatency());

		simu2.plotImplementation();

		logger.log(Level.FINER, "Test finished");
	}

	/**
	 * Constructor
	 */
	public FastAlgorithm() {
		super();
	}

	/**
	 * map : do the FAST algorithm by Kwok without the initialization of the
	 * list which must be done before this algorithm
	 * 
	 * @param threadName
	 * @param simulatorType
	 * @param dag
	 * @param archi
	 * @param CpnDominantList
	 * @param BlockingNodesList
	 * @param FinalcriticalpathList
	 * @param MAXCOUNT
	 * @param MAXSTEP
	 * @param MARGIN
	 * @param alreadyimplanted
	 *            (implementation already set)
	 * @param pfastused
	 * 
	 * @return : MapperDAG
	 */

	public MapperDAG map(String threadName,
			ArchitectureSimulatorType simulatorType, MapperDAG dag,
			IArchitecture archi, List<MapperDAGVertex> CpnDominantList,
			List<MapperDAGVertex> BlockingNodesList,
			List<MapperDAGVertex> FinalcriticalpathList, int MAXCOUNT,
			int MAXSTEP, int MARGIN, boolean alreadyimplanted, boolean pfastused, 
			BenchmarkWriter writer) {

		final PlotBestLatency demo = new PlotBestLatency("FastAlgorithm");

		// initialing the data window if this is necessary
		if (!pfastused) {

			demo.setSUBPLOT_COUNT(1);
			demo.pack();
			RefineryUtilities.centerFrameOnScreen(demo);
			demo.setVisible(true);

			this.addObserver(demo);
		}

		// Variables
		IAbc simulator = AbstractAbc
				.getInstance(simulatorType, dag, archi);
		ListScheduler scheduler = new ListScheduler();
		Iterator<Operator> prociter;
		Iterator<MapperDAGVertex> vertexiter = new RandomIterator<MapperDAGVertex>(
				BlockingNodesList, new Random());
		RandomIterator<MapperDAGVertex> iter = new RandomIterator<MapperDAGVertex>(
				FinalcriticalpathList, new Random());
		MapperDAGVertex currentvertex = null;
		MapperDAGVertex fcpvertex = null;
		Operator operatortest;
		Operator operatorfcp;
		Operator operatorprec;
		List<Operator> operatorlist = new ArrayList<Operator>();
		Logger logger = PreesmLogger.getLogger();

		// these steps are linked to the description of the FAST algorithm to
		// understand the steps, please refer to the Kwok thesis

		// step 3
		int bestSL = Integer.MAX_VALUE;
		int searchcount = 0;

		// step 1
		if (!alreadyimplanted) {

			scheduler.schedule(dag, CpnDominantList, BlockingNodesList,
					FinalcriticalpathList, simulator, null, null);
		} else {
			simulator.setDAG(dag);
		}
		// display initial time after the list scheduling
		int initial = simulator.getFinalTime();
		
		logger.log(Level.FINE, "InitialSP " + initial);
		
		// The writer allows textual logs
		if(writer != null){
			writer.printLatency(initial);
		}
		
		int SL = initial;
		dag.setScheduleLatency(initial);
		if (BlockingNodesList.size() < 2)
			return simulator.getDAG().clone();
		bestSL = initial;
		Integer iBest;
		MapperDAG dagfinal2 = simulator.getDAG().clone();
		dagfinal2.setScheduleLatency(bestSL);

		// step 4/17
		while (searchcount++ <= MAXCOUNT) {

			iBest = (Integer) bestSL;
			setChanged();
			notifyObservers(iBest);

			if(writer != null){
				writer.printLatency(iBest);
			}
			
			if (!pfastused) {
				// Mode Pause
				while (demo.getActionType() == 2)
					;

				// Mode stop
				if (demo.getActionType() == 1) {
					logger
							.log(
									Level.FINE,
									"Gain "
											+ ((((double) initial - (double) bestSL) / (double) initial) * 100)
											+ " %");
					return dagfinal2.clone();
				}
			}

			// step 5
			int searchstep = 0;
			int counter = 0;

			// step 6 : neighborhood search
			do {
				// step 7
				currentvertex = (MapperDAGVertex) vertexiter.next();
				SL = simulator.getFinalTime();

				// step 8
				OperatorIterator iteratorop = new OperatorIterator(
						currentvertex, simulator.getArchitecture());
				operatorlist = iteratorop.getOperatorList();

				prociter = new RandomIterator<Operator>(operatorlist,
						new Random());
				operatortest = prociter.next();
				if (operatortest.equals(dagfinal2.getMapperDAGVertex(
						currentvertex.getName()).getImplementationVertexProperty()
						.getEffectiveOperator())) {
					operatortest = prociter.next();
				}
				operatorprec = (Operator) simulator
						.getEffectiveComponent(currentvertex);

				// step 9
				simulator.implant(currentvertex, operatortest, false);

				// step 10
				int newSL = simulator.getFinalTime();
				if (newSL >= SL) {

					simulator.implant(currentvertex, operatorprec, false);
					counter++;
				} else {
					logger.log(Level.FINEST, threadName + ", SL " + SL
							+ "FinalTime " + newSL);
					
					counter = 0;
					SL = newSL;

				}

				// step 11
			} while (searchstep++ < MAXSTEP && counter < MARGIN);

			// step 12
			if (bestSL > simulator.getFinalTime()) {

				// step 13
				dagfinal2 = simulator.getDAG().clone();
				// step 14

				bestSL = simulator.getFinalTime();
				dagfinal2.setScheduleLatency(bestSL);
				logger.log(Level.FINER, threadName + ", bestSL " + bestSL);

			}

			// step 15
			simulator.resetDAG();

			// step 16
			fcpvertex = (MapperDAGVertex) iter.next();
			OperatorIterator iteratorop = new OperatorIterator(fcpvertex,
					simulator.getArchitecture());
			operatorlist = iteratorop.getOperatorList();

			prociter = new RandomIterator<Operator>(operatorlist, new Random());

			operatorfcp = prociter.next();
			if (operatorfcp.equals(dagfinal2.getMapperDAGVertex(fcpvertex.getName())
					.getImplementationVertexProperty().getEffectiveOperator())) {

				operatorfcp = prociter.next();
			}
			scheduler.schedule(dag, CpnDominantList, BlockingNodesList,
					FinalcriticalpathList, simulator, operatorfcp, fcpvertex);

		}

		logger
				.log(
						Level.FINE,
						threadName
								+ " : Gain "
								+ ((((double) initial - (double) bestSL) / (double) initial) * 100)
								+ " %");

		return dagfinal2;
	}
}
