package org.ietr.preesm.plugin.mapper.commcontenlistsched.scheduler;

import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ArchitectureDescriptor;

/**
 * This class gives a classic communication contentious list scheduling method
 * with nodes sorted by bottom level.
 * 
 * @author pmu
 */
public class CombCListSchedCd {

	private String name = null;

	private AlgorithmDescriptor algorithm = null;

	private ArchitectureDescriptor architecture = null;

	private CListSchedCd bestScheduler = null;

	private int scheduleLength = Integer.MAX_VALUE;

	private int nbUsedOperators = Integer.MAX_VALUE;

	public CombCListSchedCd(AlgorithmDescriptor algorithm,
			ArchitectureDescriptor architecture) {
		// TODO Auto-generated constructor stub
		this.name = "Combined Classic List Scheduling With Communication Delay";
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
		CListSchedCdBlcomp scheduler1 = new CListSchedCdBlcomp(algorithm1,
				architecture1);
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
		CListSchedCdBl scheduler2 = new CListSchedCdBl(algorithm2, architecture2);
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
		CListSchedCdBlin scheduler3 = new CListSchedCdBlin(algorithm3,
				architecture3);
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
		CListSchedCdBlout scheduler4 = new CListSchedCdBlout(algorithm4,
				architecture4);
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
		CListSchedCdBlinout scheduler5 = new CListSchedCdBlinout(algorithm5,
				architecture5);
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

	public int getScheduleLength() {
		return scheduleLength;
	}

	public int getNbUsedOperators() {
		return nbUsedOperators;
	}

}
