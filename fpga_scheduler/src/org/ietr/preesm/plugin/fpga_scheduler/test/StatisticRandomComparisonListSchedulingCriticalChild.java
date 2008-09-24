package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCriticalChild {

	public StatisticRandomComparisonListSchedulingCriticalChild() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CriticalChild Begins! *****");
		double ccr = 10;
		int statisticTimes = 10000;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_criticalTimes = 0;
		double bl_c_criticalRelativeScheduleLength = 0;
		int bl_criticalTimes = 0;
		double bl_criticalRelativeScheduleLength = 0;
		int bl_in_criticalTimes = 0;
		double bl_in_criticalRelativeScheduleLength = 0;
		int bl_out_criticalTimes = 0;
		double bl_out_criticalRelativeScheduleLength = 0;
		int bl_inout_criticalTimes = 0;
		double bl_inout_criticalRelativeScheduleLength = 0;

		int bl_c_criticalBetterClassicTimes = 0;
		int bl_c_criticalEqualClassicTimes = 0;
		double bl_c_criticalMaxAcceleration = -1;
		double bl_c_criticalMinAcceleration = 1;
		double bl_c_criticalAverageAcceleration = 0;

		int bl_criticalBetterClassicTimes = 0;
		int bl_criticalEqualClassicTimes = 0;
		double bl_criticalMaxAcceleration = -1;
		double bl_criticalMinAcceleration = 1;
		double bl_criticalAverageAcceleration = 0;

		int bl_in_criticalBetterClassicTimes = 0;
		int bl_in_criticalEqualClassicTimes = 0;
		double bl_in_criticalMaxAcceleration = -1;
		double bl_in_criticalMinAcceleration = 1;
		double bl_in_criticalAverageAcceleration = 0;

		int bl_out_criticalBetterClassicTimes = 0;
		int bl_out_criticalEqualClassicTimes = 0;
		double bl_out_criticalMaxAcceleration = -1;
		double bl_out_criticalMinAcceleration = 1;
		double bl_out_criticalAverageAcceleration = 0;

		int bl_inout_criticalBetterClassicTimes = 0;
		int bl_inout_criticalEqualClassicTimes = 0;
		double bl_inout_criticalMaxAcceleration = -1;
		double bl_inout_criticalMinAcceleration = 1;
		double bl_inout_criticalAverageAcceleration = 0;

		double all_criticalRelativeScheduleLength = 0;
		int all_criticalBetterClassicTimes = 0;
		int all_criticalEqualClassicTimes = 0;
		double all_criticalMaxAcceleration = -1;
		double all_criticalMinAcceleration = 1;
		double all_criticalAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCriticalChild comparison = new RandomComparisonListSchedulingCriticalChild(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalTimes += 1;
			}

			all_criticalRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_criticalMaxAcceleration = bl_c_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_criticalMinAcceleration = bl_c_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_criticalMaxAcceleration = bl_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_criticalMinAcceleration = bl_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_criticalMaxAcceleration = bl_in_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_criticalMinAcceleration = bl_in_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_criticalMaxAcceleration = bl_out_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_criticalMinAcceleration = bl_out_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_criticalMaxAcceleration = bl_inout_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_criticalMinAcceleration = bl_inout_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_criticalMaxAcceleration = all_criticalMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_criticalMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_criticalMinAcceleration = all_criticalMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_criticalMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_criticalEqualClassicTimes += 1;
			}
		}
		bl_c_criticalRelativeScheduleLength /= statisticTimes;
		bl_criticalRelativeScheduleLength /= statisticTimes;
		bl_in_criticalRelativeScheduleLength /= statisticTimes;
		bl_out_criticalRelativeScheduleLength /= statisticTimes;
		bl_inout_criticalRelativeScheduleLength /= statisticTimes;
		all_criticalRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_criticalRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_criticalRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_criticalRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_criticalRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_criticalRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_criticalTimes
				+ "/" + statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_criticalTimes
				+ "/" + statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_criticalTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t"
				+ bl_out_criticalTimes + "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_criticalTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_criticalAverageAcceleration = 1 - bl_c_criticalRelativeScheduleLength;
		bl_criticalAverageAcceleration = 1 - bl_criticalRelativeScheduleLength;
		bl_in_criticalAverageAcceleration = 1 - bl_in_criticalRelativeScheduleLength;
		bl_out_criticalAverageAcceleration = 1 - bl_out_criticalRelativeScheduleLength;
		bl_inout_criticalAverageAcceleration = 1 - bl_inout_criticalRelativeScheduleLength;
		all_criticalAverageAcceleration = 1 - all_criticalRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c critical better than classic times:\t"
				+ bl_c_criticalBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c critical equal to classic times:\t\t"
				+ bl_c_criticalEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c critical worse than classic times:\t\t"
						+ (statisticTimes - bl_c_criticalBetterClassicTimes - bl_c_criticalEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c critical max acceleration:\t\t\t"
				+ bl_c_criticalMaxAcceleration);
		System.out.println("bl-c critical min acceleration:\t\t\t"
				+ bl_c_criticalMinAcceleration);
		System.out.println("bl-c critical average acceleration:\t\t"
				+ bl_c_criticalAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl critical better than classic times:\t\t"
				+ bl_criticalBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl critical equal to classic times:\t\t"
				+ bl_criticalEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl critical worse than classic times:\t\t"
						+ (statisticTimes - bl_criticalBetterClassicTimes - bl_criticalEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl critical max acceleration:\t\t\t"
				+ bl_criticalMaxAcceleration);
		System.out.println("bl critical min acceleration:\t\t\t"
				+ bl_criticalMinAcceleration);
		System.out.println("bl critical average acceleration:\t\t"
				+ bl_criticalAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in critical better than classic times:\t"
				+ bl_in_criticalBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in critical equal to classic times:\t\t"
				+ bl_in_criticalEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in critical worse than classic times:\t"
						+ (statisticTimes - bl_in_criticalBetterClassicTimes - bl_in_criticalEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in critical max acceleration:\t\t"
				+ bl_in_criticalMaxAcceleration);
		System.out.println("bl-in critical min acceleration:\t\t"
				+ bl_in_criticalMinAcceleration);
		System.out.println("bl-in critical average acceleration:\t\t"
				+ bl_in_criticalAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out critical better than classic times:\t"
				+ bl_out_criticalBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out critical equal to classic times:\t\t"
				+ bl_out_criticalEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out critical worse than classic times:\t"
						+ (statisticTimes - bl_out_criticalBetterClassicTimes - bl_out_criticalEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out critical max acceleration:\t\t"
				+ bl_out_criticalMaxAcceleration);
		System.out.println("bl-out critical min acceleration:\t\t"
				+ bl_out_criticalMinAcceleration);
		System.out.println("bl-out critical average acceleration:\t\t"
				+ bl_out_criticalAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout critical better than classic times:\t"
				+ bl_inout_criticalBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout critical equal to classic times:\t"
				+ bl_inout_criticalEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout critical worse than classic times:\t"
						+ (statisticTimes - bl_inout_criticalBetterClassicTimes - bl_inout_criticalEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout critical max acceleration:\t\t"
				+ bl_inout_criticalMaxAcceleration);
		System.out.println("bl-inout critical min acceleration:\t\t"
				+ bl_inout_criticalMinAcceleration);
		System.out.println("bl-inout critical average acceleration:\t\t"
				+ bl_inout_criticalAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all critical better than classic times:\t\t"
				+ all_criticalBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all critical equal to classic times:\t\t"
				+ all_criticalEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all critical worse than classic times:\t\t"
						+ (statisticTimes - all_criticalBetterClassicTimes - all_criticalEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all critical max acceleration:\t\t\t"
				+ all_criticalMaxAcceleration);
		System.out.println("all critical min acceleration:\t\t\t"
				+ all_criticalMinAcceleration);
		System.out.println("all critical average acceleration:\t\t"
				+ all_criticalAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CriticalChild Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCriticalChild();
	}

}
