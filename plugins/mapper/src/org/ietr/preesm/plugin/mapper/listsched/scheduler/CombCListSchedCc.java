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
package org.ietr.preesm.plugin.mapper.listsched.scheduler;

import org.ietr.preesm.plugin.mapper.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ArchitectureDescriptor;

/**
 * This class gives a classic communication contentious list scheduling method
 * with nodes sorted by bottom level.
 * 
 * @author pmu
 */
public class CombCListSchedCc {

	private String name = null;

	private AlgorithmDescriptor algorithm = null;

	private ArchitectureDescriptor architecture = null;

	private CListSchedCc bestScheduler = null;

	private CListSchedCcBlcomp scheduler1 = null;

	private CListSchedCcBl scheduler2 = null;

	private CListSchedCcBlin scheduler3 = null;

	private CListSchedCcBlout scheduler4 = null;

	private CListSchedCcBlinout scheduler5 = null;

	private int scheduleLength = Integer.MAX_VALUE;

	private int nbUsedOperators = Integer.MAX_VALUE;

	public CombCListSchedCc(AlgorithmDescriptor algorithm,
			ArchitectureDescriptor architecture) {
		// TODO Auto-generated constructor stub
		this.name = "Combined Classic List Scheduling With Critical Child";
		this.algorithm = algorithm;
		this.architecture = architecture;
	}

	public boolean schedule() {
		System.out.println("\n***** " + name + " *****");
		scheduleLength = Integer.MAX_VALUE;
		nbUsedOperators = Integer.MAX_VALUE;

		AlgorithmDescriptor algorithm1 = (AlgorithmDescriptor) algorithm
				.clone();
		ArchitectureDescriptor architecture1 = (ArchitectureDescriptor) architecture
				.clone();
		scheduler1 = new CListSchedCcBlcomp(algorithm1, architecture1);
		scheduler1.schedule();
		if (scheduler1.getScheduleLength() < scheduleLength) {
			scheduleLength = scheduler1.getScheduleLength();
			nbUsedOperators = scheduler1.getUsedOperators().size();
			bestScheduler = scheduler1;
		} else if (scheduler1.getScheduleLength() == scheduleLength) {
			if (scheduler1.getUsedOperators().size() < nbUsedOperators) {
				scheduleLength = scheduler1.getScheduleLength();
				nbUsedOperators = scheduler1.getUsedOperators().size();
				bestScheduler = scheduler1;
			}
		}

		AlgorithmDescriptor algorithm2 = (AlgorithmDescriptor) algorithm
				.clone();
		ArchitectureDescriptor architecture2 = (ArchitectureDescriptor) architecture
				.clone();
		scheduler2 = new CListSchedCcBl(algorithm2, architecture2);
		scheduler2.schedule();
		if (scheduler2.getScheduleLength() < scheduleLength) {
			scheduleLength = scheduler2.getScheduleLength();
			nbUsedOperators = scheduler2.getUsedOperators().size();
			bestScheduler = scheduler2;
		} else if (scheduler2.getScheduleLength() == scheduleLength) {
			if (scheduler2.getUsedOperators().size() < nbUsedOperators) {
				scheduleLength = scheduler2.getScheduleLength();
				nbUsedOperators = scheduler2.getUsedOperators().size();
				bestScheduler = scheduler2;
			}
		}

		AlgorithmDescriptor algorithm3 = (AlgorithmDescriptor) algorithm
				.clone();
		ArchitectureDescriptor architecture3 = (ArchitectureDescriptor) architecture
				.clone();
		scheduler3 = new CListSchedCcBlin(algorithm3, architecture3);
		scheduler3.schedule();
		if (scheduler3.getScheduleLength() < scheduleLength) {
			scheduleLength = scheduler3.getScheduleLength();
			nbUsedOperators = scheduler3.getUsedOperators().size();
			bestScheduler = scheduler3;
		} else if (scheduler3.getScheduleLength() == scheduleLength) {
			if (scheduler3.getUsedOperators().size() < nbUsedOperators) {
				scheduleLength = scheduler3.getScheduleLength();
				nbUsedOperators = scheduler3.getUsedOperators().size();
				bestScheduler = scheduler3;
			}
		}

		AlgorithmDescriptor algorithm4 = (AlgorithmDescriptor) algorithm
				.clone();
		ArchitectureDescriptor architecture4 = (ArchitectureDescriptor) architecture
				.clone();
		scheduler4 = new CListSchedCcBlout(algorithm4, architecture4);
		scheduler4.schedule();
		if (scheduler4.getScheduleLength() < scheduleLength) {
			scheduleLength = scheduler4.getScheduleLength();
			nbUsedOperators = scheduler4.getUsedOperators().size();
			bestScheduler = scheduler4;
		} else if (scheduler4.getScheduleLength() == scheduleLength) {
			if (scheduler4.getUsedOperators().size() < nbUsedOperators) {
				scheduleLength = scheduler4.getScheduleLength();
				nbUsedOperators = scheduler4.getUsedOperators().size();
				bestScheduler = scheduler4;
			}
		}

		AlgorithmDescriptor algorithm5 = (AlgorithmDescriptor) algorithm
				.clone();
		ArchitectureDescriptor architecture5 = (ArchitectureDescriptor) architecture
				.clone();
		scheduler5 = new CListSchedCcBlinout(algorithm5, architecture5);
		scheduler5.schedule();
		if (scheduler5.getScheduleLength() < scheduleLength) {
			scheduleLength = scheduler5.getScheduleLength();
			nbUsedOperators = scheduler5.getUsedOperators().size();
			bestScheduler = scheduler5;
		} else if (scheduler5.getScheduleLength() == scheduleLength) {
			if (scheduler5.getUsedOperators().size() < nbUsedOperators) {
				scheduleLength = scheduler5.getScheduleLength();
				nbUsedOperators = scheduler5.getUsedOperators().size();
				bestScheduler = scheduler5;
			}
		}

		return true;
	}

	public String getName() {
		return name;
	}

	public AbstractScheduler getBestScheduler() {
		return bestScheduler;
	}

	public AbstractScheduler getSchedulerBlcomp() {
		return scheduler1;
	}

	public AbstractScheduler getSchedulerBl() {
		return scheduler2;
	}

	public AbstractScheduler getSchedulerBlin() {
		return scheduler3;
	}

	public AbstractScheduler getSchedulerBlout() {
		return scheduler4;
	}

	public AbstractScheduler getSchedulerBlinout() {
		return scheduler5;
	}

	public int getScheduleLength() {
		return scheduleLength;
	}

	public int getNbUsedOperators() {
		return nbUsedOperators;
	}

}
