package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingAdvanced {

	public StatisticRandomComparisonListSchedulingAdvanced() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Advanced Begins! *****");
		double ccr = 10;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_advancedTimes = 0;
		double bl_c_advancedRelativeScheduleLength = 0;
		int bl_advancedTimes = 0;
		double bl_advancedRelativeScheduleLength = 0;
		int bl_in_advancedTimes = 0;
		double bl_in_advancedRelativeScheduleLength = 0;
		int bl_out_advancedTimes = 0;
		double bl_out_advancedRelativeScheduleLength = 0;
		int bl_inout_advancedTimes = 0;
		double bl_inout_advancedRelativeScheduleLength = 0;

		int bl_c_advancedBetterClassicTimes = 0;
		int bl_c_advancedEqualClassicTimes = 0;
		double bl_c_advancedMaxAcceleration = -1;
		double bl_c_advancedMinAcceleration = 1;
		double bl_c_advancedAverageAcceleration = 0;

		int bl_advancedBetterClassicTimes = 0;
		int bl_advancedEqualClassicTimes = 0;
		double bl_advancedMaxAcceleration = -1;
		double bl_advancedMinAcceleration = 1;
		double bl_advancedAverageAcceleration = 0;

		int bl_in_advancedBetterClassicTimes = 0;
		int bl_in_advancedEqualClassicTimes = 0;
		double bl_in_advancedMaxAcceleration = -1;
		double bl_in_advancedMinAcceleration = 1;
		double bl_in_advancedAverageAcceleration = 0;

		int bl_out_advancedBetterClassicTimes = 0;
		int bl_out_advancedEqualClassicTimes = 0;
		double bl_out_advancedMaxAcceleration = -1;
		double bl_out_advancedMinAcceleration = 1;
		double bl_out_advancedAverageAcceleration = 0;

		int bl_inout_advancedBetterClassicTimes = 0;
		int bl_inout_advancedEqualClassicTimes = 0;
		double bl_inout_advancedMaxAcceleration = -1;
		double bl_inout_advancedMinAcceleration = 1;
		double bl_inout_advancedAverageAcceleration = 0;

		double all_advancedRelativeScheduleLength = 0;
		int all_advancedBetterClassicTimes = 0;
		int all_advancedEqualClassicTimes = 0;
		double all_advancedMaxAcceleration = -1;
		double all_advancedMinAcceleration = 1;
		double all_advancedAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingAdvanced comparison = new RandomComparisonListSchedulingAdvanced(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
					.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_advancedTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_advancedTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_advancedTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_advancedTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_advancedTimes += 1;
			}

			all_advancedRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_advancedMaxAcceleration = bl_c_advancedMaxAcceleration > 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_advancedMaxAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_advancedMinAcceleration = bl_c_advancedMinAcceleration < 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_advancedMinAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_advancedBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_advancedEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_advancedMaxAcceleration = bl_advancedMaxAcceleration > 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_advancedMaxAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_advancedMinAcceleration = bl_advancedMinAcceleration < 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_advancedMinAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_advancedBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_advancedEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_advancedMaxAcceleration = bl_in_advancedMaxAcceleration > 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_advancedMaxAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_advancedMinAcceleration = bl_in_advancedMinAcceleration < 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_advancedMinAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_advancedBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_advancedEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_advancedMaxAcceleration = bl_out_advancedMaxAcceleration > 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_advancedMaxAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_advancedMinAcceleration = bl_out_advancedMinAcceleration < 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_advancedMinAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_advancedBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_advancedEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_advancedMaxAcceleration = bl_inout_advancedMaxAcceleration > 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_advancedMaxAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_advancedMinAcceleration = bl_inout_advancedMinAcceleration < 1
					- comparison
							.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_advancedMinAcceleration
					: 1
							- comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_advancedBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_advancedEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_advancedMaxAcceleration = all_advancedMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_advancedMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_advancedMinAcceleration = all_advancedMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_advancedMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_advancedBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_advancedEqualClassicTimes += 1;
			}
		}
		bl_c_advancedRelativeScheduleLength /= statisticTimes;
		bl_advancedRelativeScheduleLength /= statisticTimes;
		bl_in_advancedRelativeScheduleLength /= statisticTimes;
		bl_out_advancedRelativeScheduleLength /= statisticTimes;
		bl_inout_advancedRelativeScheduleLength /= statisticTimes;
		all_advancedRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_advancedRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_advancedRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_advancedRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_advancedRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_advancedRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_advancedTimes
				+ "/" + statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_advancedTimes
				+ "/" + statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_advancedTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t"
				+ bl_out_advancedTimes + "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_advancedTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_advancedAverageAcceleration = 1 - bl_c_advancedRelativeScheduleLength;
		bl_advancedAverageAcceleration = 1 - bl_advancedRelativeScheduleLength;
		bl_in_advancedAverageAcceleration = 1 - bl_in_advancedRelativeScheduleLength;
		bl_out_advancedAverageAcceleration = 1 - bl_out_advancedRelativeScheduleLength;
		bl_inout_advancedAverageAcceleration = 1 - bl_inout_advancedRelativeScheduleLength;
		all_advancedAverageAcceleration = 1 - all_advancedRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c advanced better than classic times:\t"
				+ bl_c_advancedBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c advanced equal to classic times:\t\t"
				+ bl_c_advancedEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c advanced worse than classic times:\t\t"
						+ (statisticTimes - bl_c_advancedBetterClassicTimes - bl_c_advancedEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c advanced max acceleration:\t\t\t"
				+ bl_c_advancedMaxAcceleration);
		System.out.println("bl-c advanced min acceleration:\t\t\t"
				+ bl_c_advancedMinAcceleration);
		System.out.println("bl-c advanced average acceleration:\t\t"
				+ bl_c_advancedAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl advanced better than classic times:\t\t"
				+ bl_advancedBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl advanced equal to classic times:\t\t"
				+ bl_advancedEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl advanced worse than classic times:\t\t"
						+ (statisticTimes - bl_advancedBetterClassicTimes - bl_advancedEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl advanced max acceleration:\t\t\t"
				+ bl_advancedMaxAcceleration);
		System.out.println("bl advanced min acceleration:\t\t\t"
				+ bl_advancedMinAcceleration);
		System.out.println("bl advanced average acceleration:\t\t"
				+ bl_advancedAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in advanced better than classic times:\t"
				+ bl_in_advancedBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in advanced equal to classic times:\t\t"
				+ bl_in_advancedEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in advanced worse than classic times:\t"
						+ (statisticTimes - bl_in_advancedBetterClassicTimes - bl_in_advancedEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in advanced max acceleration:\t\t"
				+ bl_in_advancedMaxAcceleration);
		System.out.println("bl-in advanced min acceleration:\t\t"
				+ bl_in_advancedMinAcceleration);
		System.out.println("bl-in advanced average acceleration:\t\t"
				+ bl_in_advancedAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out advanced better than classic times:\t"
				+ bl_out_advancedBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out advanced equal to classic times:\t\t"
				+ bl_out_advancedEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out advanced worse than classic times:\t"
						+ (statisticTimes - bl_out_advancedBetterClassicTimes - bl_out_advancedEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out advanced max acceleration:\t\t"
				+ bl_out_advancedMaxAcceleration);
		System.out.println("bl-out advanced min acceleration:\t\t"
				+ bl_out_advancedMinAcceleration);
		System.out.println("bl-out advanced average acceleration:\t\t"
				+ bl_out_advancedAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout advanced better than classic times:\t"
				+ bl_inout_advancedBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout advanced equal to classic times:\t"
				+ bl_inout_advancedEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout advanced worse than classic times:\t"
						+ (statisticTimes - bl_inout_advancedBetterClassicTimes - bl_inout_advancedEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout advanced max acceleration:\t\t"
				+ bl_inout_advancedMaxAcceleration);
		System.out.println("bl-inout advanced min acceleration:\t\t"
				+ bl_inout_advancedMinAcceleration);
		System.out.println("bl-inout advanced average acceleration:\t\t"
				+ bl_inout_advancedAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all advanced better than classic times:\t\t"
				+ all_advancedBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all advanced equal to classic times:\t\t"
				+ all_advancedEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all advanced worse than classic times:\t\t"
						+ (statisticTimes - all_advancedBetterClassicTimes - all_advancedEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all advanced max acceleration:\t\t\t"
				+ all_advancedMaxAcceleration);
		System.out.println("all advanced min acceleration:\t\t\t"
				+ all_advancedMinAcceleration);
		System.out.println("all advanced average acceleration:\t\t"
				+ all_advancedAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Advanced Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingAdvanced();
	}

}
