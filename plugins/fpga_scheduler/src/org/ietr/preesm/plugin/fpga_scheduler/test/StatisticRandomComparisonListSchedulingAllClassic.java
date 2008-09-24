package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingAllClassic {

	public StatisticRandomComparisonListSchedulingAllClassic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling All Classic (ordered by bottom-level) Begins! *****");
		double ccr = 0.1;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int criticalClassicTimes = 0;
		double criticalClassicRelativeScheduleLength = 0;
		int delayClassicTimes = 0;
		double delayClassicRelativeScheduleLength = 0;
		int CcCdClassicTimes = 0;
		double CcCdClassicRelativeScheduleLength = 0;

		int criticalClassicBetterClassicTimes = 0;
		int criticalClassicEqualClassicTimes = 0;
		double criticalClassicMaxAcceleration = -1;
		double criticalClassicMinAcceleration = 1;
		double criticalClassicAverageAcceleration = 0;

		int delayClassicBetterClassicTimes = 0;
		int delayClassicEqualClassicTimes = 0;
		double delayClassicMaxAcceleration = -1;
		double delayClassicMinAcceleration = 1;
		double delayClassicAverageAcceleration = 0;

		int CcCdClassicBetterClassicTimes = 0;
		int CcCdClassicEqualClassicTimes = 0;
		double CcCdClassicMaxAcceleration = -1;
		double CcCdClassicMinAcceleration = 1;
		double CcCdClassicAverageAcceleration = 0;

		double allClassicRelativeScheduleLength = 0;
		int allClassicBetterClassicTimes = 0;
		int allClassicEqualClassicTimes = 0;
		double allClassicMaxAcceleration = -1;
		double allClassicMinAcceleration = 1;
		double allClassicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingAllClassic comparison = new RandomComparisonListSchedulingAllClassic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			criticalClassicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildClassic()
					.getScheduleLength()
					/ classicScheduleLength;
			delayClassicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayClassic()
					.getScheduleLength()
					/ classicScheduleLength;
			CcCdClassicRelativeScheduleLength += comparison
					.getListSchedulingCcCdClassic().getScheduleLength()
					/ classicScheduleLength;
			minScheduleLength = minScheduleLength > comparison
					.getListSchedulingClassic().getScheduleLength() ? comparison
					.getListSchedulingClassic().getScheduleLength()
					: minScheduleLength;
			minScheduleLength = minScheduleLength > comparison
					.getListSchedulingCriticalChildClassic()
					.getScheduleLength() ? comparison
					.getListSchedulingCriticalChildClassic()
					.getScheduleLength() : minScheduleLength;
			minScheduleLength = minScheduleLength > comparison
					.getListSchedulingCommunicationDelayClassic()
					.getScheduleLength() ? comparison
					.getListSchedulingCommunicationDelayClassic()
					.getScheduleLength() : minScheduleLength;
			minScheduleLength = minScheduleLength > comparison
					.getListSchedulingCcCdClassic().getScheduleLength() ? comparison
					.getListSchedulingCcCdClassic().getScheduleLength()
					: minScheduleLength;

			if (minScheduleLength == comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildClassic()
					.getScheduleLength()) {
				criticalClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayClassic()
					.getScheduleLength()) {
				delayClassicTimes += 1;
			}
			if (minScheduleLength == comparison.getListSchedulingCcCdClassic()
					.getScheduleLength()) {
				CcCdClassicTimes += 1;
			}

			allClassicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare criticalClassic to classic
			criticalClassicMaxAcceleration = criticalClassicMaxAcceleration > 1
					- comparison.getListSchedulingCriticalChildClassic()
							.getScheduleLength() / classicScheduleLength ? criticalClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassic()
									.getScheduleLength()
							/ classicScheduleLength;
			criticalClassicMinAcceleration = criticalClassicMinAcceleration < 1
					- comparison.getListSchedulingCriticalChildClassic()
							.getScheduleLength() / classicScheduleLength ? criticalClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassic()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildClassic()
					.getScheduleLength()) {
				criticalClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildClassic()
					.getScheduleLength()) {
				criticalClassicEqualClassicTimes += 1;
			}
			// Compare delayClassic to classic
			delayClassicMaxAcceleration = delayClassicMaxAcceleration > 1
					- comparison.getListSchedulingCommunicationDelayClassic()
							.getScheduleLength() / classicScheduleLength ? delayClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassic()
									.getScheduleLength()
							/ classicScheduleLength;
			delayClassicMinAcceleration = delayClassicMinAcceleration < 1
					- comparison.getListSchedulingCommunicationDelayClassic()
							.getScheduleLength() / classicScheduleLength ? delayClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassic()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayClassic()
					.getScheduleLength()) {
				delayClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayClassic()
					.getScheduleLength()) {
				delayClassicEqualClassicTimes += 1;
			}
			// Compare CcCdClassic to classic
			CcCdClassicMaxAcceleration = CcCdClassicMaxAcceleration > 1
					- comparison.getListSchedulingCcCdClassic()
							.getScheduleLength() / classicScheduleLength ? CcCdClassicMaxAcceleration
					: 1
							- comparison.getListSchedulingCcCdClassic()
									.getScheduleLength()
							/ classicScheduleLength;
			CcCdClassicMinAcceleration = CcCdClassicMinAcceleration < 1
					- comparison.getListSchedulingCcCdClassic()
							.getScheduleLength() / classicScheduleLength ? CcCdClassicMinAcceleration
					: 1
							- comparison.getListSchedulingCcCdClassic()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdClassic().getScheduleLength()) {
				CcCdClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdClassic().getScheduleLength()) {
				CcCdClassicEqualClassicTimes += 1;
			}

			// Compare all to classic
			allClassicMaxAcceleration = allClassicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? allClassicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			allClassicMinAcceleration = allClassicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? allClassicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				allClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				allClassicEqualClassicTimes += 1;
			}
			// Compare criticalClassic child to CcCdClassic
			// if
			// (comparison.getListSchedulingCriticalChildClassic().getScheduleLength()
			// > comparison
			// .getListSchedulingCcCdClassic().getScheduleLength()) {
			// CcCdClassicBetterCriticalClassicTimes += 1;
			// } else if (comparison.getListSchedulingCriticalChildClassic()
			// .getScheduleLength() == comparison
			// .getListSchedulingCcCdClassic().getScheduleLength()) {
			// CcCdClassicEqualCriticalClassicTimes += 1;
			// } else {
			// criticalClassicBetterCcCdClassicTimes += 1;
			// }

		}
		criticalClassicRelativeScheduleLength /= statisticTimes;
		delayClassicRelativeScheduleLength /= statisticTimes;
		CcCdClassicRelativeScheduleLength /= statisticTimes;
		allClassicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t\t" + ccr);
		System.out.println("classic relative length:\t\t" + 1.0);
		System.out.println("criticalClassic relative length:\t"
				+ criticalClassicRelativeScheduleLength);
		System.out.println("CcCdClassic relative length:\t\t"
				+ CcCdClassicRelativeScheduleLength);
		System.out.println("classic optimization times:\t\t" + classicTimes
				+ "/" + statisticTimes);
		System.out.println("criticalClassic optimization times:\t"
				+ criticalClassicTimes + "/" + statisticTimes);
		System.out.println("delayClassic optimization times:\t"
				+ delayClassicTimes + "/" + statisticTimes);
		System.out.println("CcCdClassic optimization times:\t\t"
				+ CcCdClassicTimes + "/" + statisticTimes);
		System.out.println("total not worse times:\t\t\t" + betterTimes + "/"
				+ statisticTimes);

		criticalClassicAverageAcceleration = 1 - criticalClassicRelativeScheduleLength;
		delayClassicAverageAcceleration = 1 - delayClassicRelativeScheduleLength;
		CcCdClassicAverageAcceleration = 1 - CcCdClassicRelativeScheduleLength;
		allClassicAverageAcceleration = 1 - allClassicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);

		System.out.println("criticalClassic better than classic times:\t"
				+ criticalClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("criticalClassic equal to classic times:\t\t"
				+ criticalClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("criticalClassic worse than classic times:\t"
						+ (statisticTimes - criticalClassicBetterClassicTimes - criticalClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("max criticalClassic acceleration:\t\t"
				+ criticalClassicMaxAcceleration);
		System.out.println("min criticalClassic acceleration:\t\t"
				+ criticalClassicMinAcceleration);
		System.out.println("average criticalClassic acceleration:\t\t"
				+ criticalClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("delayClassic better than classic times:\t\t"
				+ delayClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("delayClassic equal to classic times:\t\t"
				+ delayClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("delayClassic worse than classic times:\t\t"
						+ (statisticTimes - delayClassicBetterClassicTimes - delayClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("max delayClassic acceleration:\t\t\t"
				+ delayClassicMaxAcceleration);
		System.out.println("min delayClassic acceleration:\t\t\t"
				+ delayClassicMinAcceleration);
		System.out.println("average delayClassic acceleration:\t\t"
				+ delayClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("CcCdClassic better than classic times:\t\t"
				+ CcCdClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("CcCdClassic equal to classic times:\t\t"
				+ CcCdClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("CcCdClassic worse than classic times:\t\t"
						+ (statisticTimes - CcCdClassicBetterClassicTimes - CcCdClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("max CcCdClassic acceleration:\t\t\t"
				+ CcCdClassicMaxAcceleration);
		System.out.println("min CcCdClassic acceleration:\t\t\t"
				+ CcCdClassicMinAcceleration);
		System.out.println("average CcCdClassic acceleration:\t\t"
				+ CcCdClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all Classic better than classic times:\t\t"
				+ allClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all Classic equal to classic times:\t\t"
				+ allClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all Classic worse than classic times:\t\t"
						+ (statisticTimes - allClassicBetterClassicTimes - allClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all Classic max acceleration:\t\t\t"
				+ allClassicMaxAcceleration);
		System.out.println("all Classic min acceleration:\t\t\t"
				+ allClassicMinAcceleration);
		System.out.println("all Classic average acceleration:\t\t"
				+ allClassicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling All Classic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingAllClassic();
	}

}
