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

package org.ietr.preesm.plugin.mapper.algo.genetic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.impl.latency.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.algo.list.InitialLists;
import org.ietr.preesm.plugin.mapper.algo.list.ListScheduler;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Chromosome representing an implementation
 * 
 * @author pmenuet
 */
public class Chromosome {

	// List of the gene constituting this chromosome
	private List<Gene> ChromoList;

	// Cost of the solution represented by this chromosome
	private long evaluateCost;

	// Reference MapperDAG of this chromosome
	private MapperDAG dag;

	// Boolean to know if the evaluateCost is up to date
	private boolean dirty;

	// Architecture on which this chromosome will be used (chromosome = mapping
	// and scheduling)
	private MultiCoreArchitecture archi;

	/**
	 * Constructor
	 */
	public Chromosome() {
		super();
		this.ChromoList = null;
		this.evaluateCost = 0;
		this.dirty = true;
	}

	/**
	 * Constructor
	 * 
	 * @param chromoList
	 * @param dag
	 * @param architecture
	 */
	public Chromosome(List<Gene> chromoList, MapperDAG dag,
			MultiCoreArchitecture architecture) {
		super();
		this.ChromoList = chromoList;
		this.evaluateCost = 0;
		this.dirty = true;
		this.dag = dag;
		this.archi = architecture;
	}

	/**
	 * Constructor working only with a DAG implemented
	 * 
	 * @param dag
	 * @param architecture
	 */
	public Chromosome(MapperDAG dag, MultiCoreArchitecture architecture) {
		Iterator<MapperDAGVertex> iterator = dag.getVertexTopologicalList().listIterator();
		MapperDAGVertex currentVertex = null;
		this.dag = dag;
		this.ChromoList = new ArrayList<Gene>();
		Gene currentGene = null;

		while (iterator.hasNext()) {
			currentVertex = iterator.next();
			currentGene = new Gene(currentVertex);
			ChromoList.add(currentGene);
		}
		this.evaluateCost = 0;
		this.dirty = true;
		this.archi = architecture;

	}

	/**
	 * updateDAG : Modify the DAG inside the chromosome to make it corresponding
	 * with the ChromoList
	 * 
	 * @param : void
	 * 
	 * @return : void
	 */
	public void updateDAG() {

		Gene currentGene;
		MapperDAGVertex currentVertex;
		Iterator<Gene> iterator = this.ChromoList.listIterator();
		while (iterator.hasNext()) {
			currentGene = iterator.next();
			currentVertex = this.dag.getMapperDAGVertex(currentGene.getVertexName());
			currentVertex
					.getImplementationVertexProperty()
					.setEffectiveComponent(
							this.archi.getComponent(ArchitectureComponentType.operator,currentGene.getOperatorId()));

		}
		this.setDirty(true);
	}

	/**
	 * evaluate : Evaluate the cost of the implementation represented by the
	 * chromosome and set the evaluateCost to this cost
	 * 
	 * @param : simulatorType
	 * 
	 * @return : void
	 */
	public void evaluate(AbcType simulatorType, EdgeSchedType edgeSchedType) {
		this.updateDAG();
		IAbc simulator = AbstractAbc
				.getInstance(simulatorType, edgeSchedType, this.dag, this.archi);
		simulator.setDAG(this.getDag());
		this.setEvaluateCost(simulator.getFinalCost());
		this.setDirty(false);
	}

	/**
	 * clone : Clone the chromosome
	 * 
	 * @param : void
	 * 
	 * @return : Chromosome
	 */
	public Chromosome clone() {
		Chromosome chromosome = new Chromosome();
		chromosome.setArchi(this.archi.clone());
		chromosome.setDag(this.dag.clone());
		chromosome.setDirty(this.dirty);
		chromosome.setEvaluateCost(this.evaluateCost);
		List<Gene> list = new ArrayList<Gene>();
		Gene gene;
		Iterator<Gene> iterator = this.ChromoList.listIterator();
		while (iterator.hasNext()) {
			gene = iterator.next();
			list.add(gene.clone());
		}
		chromosome.setChromoList(list);
		return chromosome;
	}

	/**
	 * Getters and Setters
	 */
	public MultiCoreArchitecture getArchi() {
		return archi;
	}

	public void setArchi(MultiCoreArchitecture archi) {
		this.archi = archi;
	}

	public MapperDAG getDag() {
		return dag;
	}

	public void setDag(MapperDAG dag) {
		this.dag = dag;
	}

	public long getEvaluateCost() {
		return evaluateCost;
	}

	public void setEvaluateCost(long evaluateCost) {
		this.evaluateCost = evaluateCost;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public List<Gene> getChromoList() {
		return ChromoList;
	}

	public void setChromoList(List<Gene> chromoList) {
		ChromoList = chromoList;
	}

	/**
	 * toString
	 * 
	 * @param : void
	 * 
	 * @return : String
	 */
	@Override
	public String toString() {

		return this.ChromoList.toString();
	}

	/**
	 * Main for test
	 */
	public static void main(String[] args) {

		// Create and set the context
		DAGCreator dagCreator = new DAGCreator();
		MultiCoreArchitecture archi = Examples.get2C64Archi();
		MapperDAG dag = dagCreator.dagexample2(archi);

		ListScheduler scheduler = new ListScheduler();
		IAbc simu = new InfiniteHomogeneousAbc(EdgeSchedType.Simple, 
				dag, archi);
		InitialLists initialLists = new InitialLists();
		initialLists.constructInitialLists(dag, simu);
		simu.resetDAG();
		AbcType abcType = AbcType.LooselyTimed;
		IAbc archisimu = new LooselyTimedAbc(EdgeSchedType.Simple, 
				dag, archi, abcType);
		scheduler.schedule(dag, initialLists.getCpnDominant(), archisimu, null, null);

		// test constructor
		Chromosome chromosome = new Chromosome(dag, archi);

		// test Evaluate
		chromosome.evaluate(AbcType.LooselyTimed, EdgeSchedType.Simple);

		IAbc simu3 = AbstractAbc
				.getInstance(AbcType.LooselyTimed, EdgeSchedType.Simple, chromosome
						.getDag(), archi);
		simu3.setDAG(chromosome.getDag());
		simu3.plotImplementation(false);

		// test clone
		Chromosome chromosome2 = chromosome.clone();

		IAbc simu4 = AbstractAbc
				.getInstance(AbcType.LooselyTimed, EdgeSchedType.Simple,
						chromosome2.getDag(), archi);
		simu4.setDAG(chromosome2.getDag());
		simu4.plotImplementation(false);

		// test MutationOperator
		MutationOperator mutationOperator = new MutationOperator();
		chromosome = mutationOperator.transform(chromosome,
				AbcType.LooselyTimed, EdgeSchedType.Simple);

		IAbc simu5 = AbstractAbc
				.getInstance(AbcType.LooselyTimed, EdgeSchedType.Simple, chromosome
						.getDag(), archi);
		simu5.setDAG(chromosome.getDag());
		simu5.plotImplementation(false);

		chromosome = mutationOperator.transform(chromosome,
				AbcType.LooselyTimed, EdgeSchedType.Simple);

		IAbc simu6 = AbstractAbc
				.getInstance(AbcType.LooselyTimed, EdgeSchedType.Simple, chromosome
						.getDag(), archi);
		simu6.setDAG(chromosome.getDag());
		simu6.plotImplementation(false);

		chromosome = mutationOperator.transform(chromosome,
				AbcType.LooselyTimed, EdgeSchedType.Simple);

		IAbc simu7 = AbstractAbc
				.getInstance(AbcType.LooselyTimed, EdgeSchedType.Simple, chromosome
						.getDag(), archi);
		simu7.setDAG(chromosome.getDag());
		simu7.plotImplementation(false);

		chromosome = mutationOperator.transform(chromosome,
				AbcType.LooselyTimed, EdgeSchedType.Simple);

		IAbc simu1 = AbstractAbc
				.getInstance(AbcType.LooselyTimed, EdgeSchedType.Simple, chromosome
						.getDag(), archi);
		simu1.setDAG(chromosome.getDag());
		simu1.plotImplementation(false);

		// test crossOverOperator
		CrossOverOperator crossOverOperator = new CrossOverOperator();
		Chromosome chromosome3 = crossOverOperator.transform(chromosome2,
				chromosome, AbcType.LooselyTimed, EdgeSchedType.Simple);
		chromosome3.evaluate(AbcType.LooselyTimed, EdgeSchedType.Simple);

		IAbc simu2 = AbstractAbc
				.getInstance(AbcType.LooselyTimed, EdgeSchedType.Simple,
						chromosome3.getDag(), archi);
		simu2.setDAG(chromosome3.getDag());
		simu2.plotImplementation(false);

	}

}
