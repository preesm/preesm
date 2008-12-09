/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.plugin.mapper.fastalgo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;

/**
 * Population for genetic algorithm generated by FAST algorithm
 * 
 * @author pmenuet
 */
public class FastPopulation {

	// Number of individual in the population
	private int populationNum;

	// List of mapperDAG constituting the population
	private List<MapperDAG> population;

	// Simulator used to make this population
	private AbcType simulatorType;

	// architecture used to make this population
	private MultiCoreArchitecture archi;

	/**
	 * Constructors
	 */

	public FastPopulation() {
		super();

	}

	/**
	 * @param populationNum
	 * @param simulatorType
	 * @param archi
	 */
	public FastPopulation(int populationNum,
			AbcType simulatorType, MultiCoreArchitecture archi) {
		super();
		this.populationNum = populationNum;
		this.simulatorType = simulatorType;
		this.archi = archi;
		this.population = new ArrayList<MapperDAG>();
	}

	/**
	 * @param populationNum
	 * @param population
	 * @param simulatorType
	 * @param archi
	 */
	public FastPopulation(int populationNum, List<MapperDAG> population,
			AbcType simulatorType, MultiCoreArchitecture archi) {
		super();
		this.populationNum = populationNum;
		this.population = population;
		this.simulatorType = simulatorType;
		this.archi = archi;
	}

	/**
	 * Getters and setters
	 */

	public AbcType getSimulatorType() {
		return simulatorType;
	}

	public void setSimulatorType(AbcType simulatorType) {
		this.simulatorType = simulatorType;
	}

	public MultiCoreArchitecture getArchi() {
		return archi;
	}

	public void setArchi(MultiCoreArchitecture archi) {
		this.archi = archi;
	}

	public int getPopulationNum() {
		return populationNum;
	}

	public void setPopulationNum(int populationNum) {
		this.populationNum = populationNum;
	}

	public List<MapperDAG> getPopulation() {
		return population;
	}

	public void setPopulation(List<MapperDAG> population) {
		this.population = population;
	}

	/**
	 * constructPopulation = run the fast algorithm as many times it is
	 * necessary to make the population
	 * 
	 * @param dag
	 * @param MAXCOUNT
	 * @param MAXSTEP
	 * @param MARGIN
	 */
	public void constructPopulation(MapperDAG dag, int MAXCOUNT, int MAXSTEP,
			int MARGIN) {

		// create the population
		List<MapperDAG> temp = new ArrayList<MapperDAG>();

		// PopulationNum times
		for (int i = 0; i < this.getPopulationNum(); i++) {

			MapperDAG tempdag = null;
			tempdag = dag.clone();

			// perform the initialization
			IAbc simu = new InfiniteHomogeneousAbc(EdgeSchedType.none, 
					tempdag, this.getArchi());
			InitialLists initial = new InitialLists();
			initial.constructInitialLists(tempdag, simu);
			simu.resetDAG();

			// perform the fast algo
			FastAlgorithm algorithm = new FastAlgorithm();
			tempdag = algorithm.map("population", this.simulatorType, tempdag,
					this.archi, initial.getCpnDominantList(),
					initial.getBlockingNodesList(),
					initial.getFinalcriticalpathList(), MAXCOUNT, MAXSTEP,
					MARGIN, false, true, null).clone();
			temp.add(tempdag.clone());

		}
		this.population.addAll(temp);
	}

	/**
	 * Display the population found with the preceding function
	 */
	public void populationDisplay() {

		// variables
		//Logger logger = PreesmLogger.getLogger();
		//logger.setLevel(Level.FINEST);
		ListScheduler scheduler = new ListScheduler();
		Iterator<MapperDAG> iterator = this.getPopulation().iterator();
		MapperDAG temp;

		// implant the DAG in a simulator and then display it
		while (iterator.hasNext()) {
			temp = iterator.next().clone();
			IAbc simu2 = AbstractAbc
					.getInstance(this.getSimulatorType(), EdgeSchedType.none, temp, this.getArchi());

			scheduler.dagimplanteddisplay(temp, simu2);
			simu2.setDAG(temp);
			simu2.plotImplementation(false);
			simu2.resetDAG();

		}

	}

	/**
	 * main for test
	 */
	public static void main(String[] args) {

		DAGCreator dagCreator = new DAGCreator();
		MultiCoreArchitecture archi = Examples.get2C64Archi();
		MapperDAG dag = dagCreator.dagexample2(archi);

		FastPopulation population = new FastPopulation(5,
				AbcType.LooselyTimed, archi);
		population.constructPopulation(dag, 20, 10, 3);
		population.populationDisplay();

	}
}
