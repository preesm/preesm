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

package org.ietr.preesm.plugin.mapper.algo.fast;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.plugin.abc.taskscheduling.TaskSwitcher;
import org.ietr.preesm.plugin.mapper.algo.list.InitialLists;
import org.ietr.preesm.plugin.mapper.algo.list.KwokListScheduler;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;
import org.ietr.preesm.plugin.mapper.plot.BestCostPlotter;
import org.ietr.preesm.plugin.mapper.plot.bestcost.BestCostEditor;
import org.ietr.preesm.plugin.mapper.plot.gantt.GanttEditorInput;
import org.ietr.preesm.plugin.mapper.plot.gantt.GanttEditorRunnable;
import org.ietr.preesm.plugin.mapper.tools.RandomIterator;

/**
 * Fast Algorithm
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class FastAlgorithm extends Observable {

	/**
	 * The scheduling (total order of tasks) for the best found solution.
	 */
	private List<String> bestTotalOrder = null;

	private InitialLists initialLists = null;
	private IScenario scenario = null;

	/**
	 * Constructor
	 */
	public FastAlgorithm(InitialLists initialLists, IScenario scenario) {
		super();
		this.initialLists = initialLists;
		this.scenario = scenario;
	}

	public MapperDAG map(String threadName, AbcParameters abcParams,
			MapperDAG dag, MultiCoreArchitecture archi, int maxcount,
			int maxstep, int margin, boolean alreadyimplanted,
			boolean pfastused, boolean displaySolutions,
			IProgressMonitor monitor, AbstractTaskSched taskSched) {

		List<MapperDAGVertex> cpnDominantList = initialLists.getCpnDominant();
		List<MapperDAGVertex> blockingNodesList = initialLists
				.getBlockingNodes();
		List<MapperDAGVertex> finalcriticalpathList = initialLists
				.getCriticalpath();

		return map(threadName, abcParams, dag, archi, maxcount, maxstep,
				margin, alreadyimplanted, pfastused, displaySolutions, monitor,
				cpnDominantList, blockingNodesList, finalcriticalpathList,
				taskSched);
	}

	/**
	 * map : do the FAST algorithm by Kwok without the initialization of the
	 * list which must be done before this algorithm
	 * 
	 * @param maxcount
	 *            nb max of tested neighborhoods
	 * @param maxstep
	 *            nb max solutions tested in neighborhood
	 * @param margin
	 *            nb max better solutions found in neighborhood
	 * 
	 */
	public MapperDAG map(String threadName, AbcParameters abcParams,
			MapperDAG dag, MultiCoreArchitecture archi, int maxcount,
			int maxstep, int margin, boolean alreadyimplanted,
			boolean pfastused, boolean displaySolutions,
			IProgressMonitor monitor, List<MapperDAGVertex> cpnDominantList,
			List<MapperDAGVertex> blockingNodesList,
			List<MapperDAGVertex> finalcriticalpathList,
			AbstractTaskSched taskSched) {

		Random randomGenerator = new Random(System.nanoTime());

		Semaphore pauseSemaphore = new Semaphore(1);
		final BestCostPlotter costPlotter = new BestCostPlotter(
				"FastAlgorithm", pauseSemaphore);

		// initialing the data window if this is necessary
		if (!pfastused) {

			costPlotter.setSUBPLOT_COUNT(1);
			// demo.display();
			BestCostEditor.createEditor(costPlotter);

			this.addObserver(costPlotter);
		}

		// Variables
		IAbc simulator = AbstractAbc.getInstance(abcParams, dag, archi,
				scenario);

		// A topological task scheduler is chosen for the list scheduling.
		// It schedules the tasks in topological order and, if they are on
		// the same level, in alphabetical name order
		simulator.setTaskScheduler(taskSched);

		KwokListScheduler listscheduler = new KwokListScheduler();

		Iterator<MapperDAGVertex> vertexiter = new RandomIterator<MapperDAGVertex>(
				blockingNodesList, randomGenerator);

		RandomIterator<MapperDAGVertex> iter = new RandomIterator<MapperDAGVertex>(
				finalcriticalpathList, randomGenerator);
		MapperDAGVertex currentvertex = null;
		MapperDAGVertex fcpvertex = null;
		Operator operatortest;
		Operator operatorfcp;
		Operator operatorprec;
		Logger logger = PreesmLogger.getLogger();

		// these steps are linked to the description of the FAST algorithm to
		// understand the steps, please refer to the Kwok thesis

		// step 3
		long bestSL = Long.MAX_VALUE;
		int searchcount = 0;

		// step 1
		if (!alreadyimplanted) {
			listscheduler.schedule(dag, cpnDominantList, simulator, null, null);
		} else {
			simulator.setDAG(dag);
		}
		// display initial time after the list scheduling
		simulator.updateFinalCosts();
		long initial = simulator.getFinalCost();

		bestTotalOrder = simulator.getTotalOrder().toStringList();

		if (displaySolutions) {
			createEditor(simulator, abcParams, getBestTotalOrder(), "Cost:"
					+ initial + " List");
		}

		PreesmLogger.getLogger().log(Level.INFO,
				"Found List solution; Cost:" + initial);

		logger.log(Level.FINE, "InitialSP " + initial);

		long SL = initial;
		dag.setScheduleLatency(initial);
		if (blockingNodesList.size() < 2)
			return simulator.getDAG().clone();
		bestSL = initial;
		Long iBest;
		MapperDAG dagfinal = simulator.getDAG().clone();
		dagfinal.setScheduleLatency(bestSL);

		// A switcher task scheduler is chosen for the fast refinement
		simulator.setTaskScheduler(new TaskSwitcher());

		// step 4/17
		// FAST is to be stopped manually
		// PFAST stops after maxcount iterations
		while (!pfastused || (searchcount <= maxcount)) {

			searchcount++;

			// Notifying display
			iBest = (Long) bestSL;
			setChanged();
			notifyObservers(iBest);

			// step 5
			int searchstep = 0;
			int localCounter = 0;
			simulator.updateFinalCosts();

			// step 6 : neighborhood search
			do {
				if (!pfastused) {
					// Mode stop
					if (costPlotter.getActionType() == 1
							|| (monitor != null && monitor.isCanceled())) {

						return dagfinal.clone();
					} else if (costPlotter.getActionType() == 2) {
						// Mode Pause
						try {
							pauseSemaphore.acquire();
							pauseSemaphore.release();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				// step 7
				// Selecting random vertex with operator set of size > 1
				Set<Operator> operatorSet = null;
				int nonBlockingIndex = 0;

				do {
					nonBlockingIndex++;
					currentvertex = (MapperDAGVertex) vertexiter.next();
					operatorSet = currentvertex.getInitialVertexProperty()
							.getOperatorSet();
				} while (operatorSet.size() < 2 && nonBlockingIndex < 100);

				SL = simulator.getFinalCost();

				// step 8

				// The mapping can reaffect the same operator as before,
				// refining the edge scheduling
				int randomIndex = randomGenerator.nextInt(operatorSet.size());
				operatortest = (Operator) operatorSet.toArray()[randomIndex];

				operatorprec = (Operator) simulator
						.getEffectiveComponent(currentvertex);

				// step 9
				simulator.implant(currentvertex, operatortest, false);

				// step 10
				simulator.updateFinalCosts();
				long newSL = simulator.getFinalCost();

				if (newSL >= SL) {
					simulator.implant(currentvertex, operatorprec, false);
					simulator.updateFinalCosts();
					localCounter++;
				} else {
					localCounter = 0;
					SL = newSL;
				}

				searchstep++;
				// step 11
			} while (searchstep < maxstep && localCounter < margin);

			// step 12
			simulator.updateFinalCosts();

			if (bestSL > simulator.getFinalCost()) {

				// step 13
				dagfinal = simulator.getDAG().clone();
				// step 14

				bestSL = simulator.getFinalCost();

				bestTotalOrder = simulator.getTotalOrder().toStringList();

				if (displaySolutions) {
					createEditor(simulator, abcParams, getBestTotalOrder(),
							"Cost:" + bestSL + " Fast");
				}

				PreesmLogger.getLogger().log(Level.INFO,
						"Found Fast solution; Cost:" + bestSL);

				dagfinal.setScheduleLatency(bestSL);
			}

			// step 15
			simulator.resetDAG();

			// step 16
			// Choosing a vertex in critical path with an operator set of more
			// than 1 element
			Set<Operator> operatorSet = null;
			int nonBlockingIndex = 0;

			do {
				nonBlockingIndex++;
				fcpvertex = (MapperDAGVertex) iter.next();
				operatorSet = fcpvertex.getInitialVertexProperty()
						.getOperatorSet();
			} while (operatorSet.size() < 2 && nonBlockingIndex < 100);

			// Choosing an operator different from the current vertex operator
			Operator currentOp = dagfinal.getMapperDAGVertex(
					fcpvertex.getName()).getImplementationVertexProperty()
					.getEffectiveOperator();

			do {
				int randomIndex = randomGenerator.nextInt(operatorSet.size());
				operatorfcp = (Operator) operatorSet.toArray()[randomIndex];
			} while (operatorfcp.equals(currentOp));

			// Reschedule the whole dag
			listscheduler.schedule(dag, cpnDominantList, simulator,
					operatorfcp, fcpvertex);

		}

		return dagfinal;
	}

	public List<String> getBestTotalOrder() {
		return bestTotalOrder;
	}

	public void createEditor(IAbc abc, AbcParameters abcParams,
			List<String> bestTotalOrder, String name) {

		MapperDAG dag = abc.getDAG().clone();
		IAbc newAbc = AbstractAbc.getInstance(abcParams, dag, abc
				.getArchitecture(), abc.getScenario());
		newAbc.setDAG(dag);
		newAbc.reschedule(bestTotalOrder);
		newAbc.updateFinalCosts();

		IEditorInput input = new GanttEditorInput(newAbc, name);

		PlatformUI.getWorkbench().getDisplay().asyncExec(
				new GanttEditorRunnable(input));

	}

}
