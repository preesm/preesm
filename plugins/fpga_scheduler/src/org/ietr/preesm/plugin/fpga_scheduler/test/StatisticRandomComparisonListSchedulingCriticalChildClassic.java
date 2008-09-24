package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCriticalChildClassic {

	public StatisticRandomComparisonListSchedulingCriticalChildClassic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CriticalChildClassic Begins! *****");
		double ccr = 1;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_criticalClassicTimes = 0;
		double bl_c_criticalClassicRelativeScheduleLength = 0;
		int bl_criticalClassicTimes = 0;
		double bl_criticalClassicRelativeScheduleLength = 0;
		int bl_in_criticalClassicTimes = 0;
		double bl_in_criticalClassicRelativeScheduleLength = 0;
		int bl_out_criticalClassicTimes = 0;
		double bl_out_criticalClassicRelativeScheduleLength = 0;
		int bl_inout_criticalClassicTimes = 0;
		double bl_inout_criticalClassicRelativeScheduleLength = 0;

		int bl_c_criticalClassicBetterClassicTimes = 0;
		int bl_c_criticalClassicEqualClassicTimes = 0;
		double bl_c_criticalClassicMaxAcceleration = -1;
		double bl_c_criticalClassicMinAcceleration = 1;
		double bl_c_criticalClassicAverageAcceleration = 0;

		int bl_criticalClassicBetterClassicTimes = 0;
		int bl_criticalClassicEqualClassicTimes = 0;
		double bl_criticalClassicMaxAcceleration = -1;
		double bl_criticalClassicMinAcceleration = 1;
		double bl_criticalClassicAverageAcceleration = 0;

		int bl_in_criticalClassicBetterClassicTimes = 0;
		int bl_in_criticalClassicEqualClassicTimes = 0;
		double bl_in_criticalClassicMaxAcceleration = -1;
		double bl_in_criticalClassicMinAcceleration = 1;
		double bl_in_criticalClassicAverageAcceleration = 0;

		int bl_out_criticalClassicBetterClassicTimes = 0;
		int bl_out_criticalClassicEqualClassicTimes = 0;
		double bl_out_criticalClassicMaxAcceleration = -1;
		double bl_out_criticalClassicMinAcceleration = 1;
		double bl_out_criticalClassicAverageAcceleration = 0;

		int bl_inout_criticalClassicBetterClassicTimes = 0;
		int bl_inout_criticalClassicEqualClassicTimes = 0;
		double bl_inout_criticalClassicMaxAcceleration = -1;
		double bl_inout_criticalClassicMinAcceleration = 1;
		double bl_inout_criticalClassicAverageAcceleration = 0;

		double all_criticalClassicRelativeScheduleLength = 0;
		int all_criticalClassicBetterClassicTimes = 0;
		int all_criticalClassicEqualClassicTimes = 0;
		double all_criticalClassicMaxAcceleration = -1;
		double all_criticalClassicMinAcceleration = 1;
		double all_criticalClassicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCriticalChildClassic comparison = new RandomComparisonListSchedulingCriticalChildClassic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_criticalClassicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_criticalClassicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_criticalClassicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_criticalClassicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_criticalClassicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalClassicTimes += 1;
			}

			all_criticalClassicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_criticalClassicMaxAcceleration = bl_c_criticalClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_criticalClassicMinAcceleration = bl_c_criticalClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalClassicEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_criticalClassicMaxAcceleration = bl_criticalClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_criticalClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_criticalClassicMinAcceleration = bl_criticalClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_criticalClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalClassicEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_criticalClassicMaxAcceleration = bl_in_criticalClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_criticalClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_criticalClassicMinAcceleration = bl_in_criticalClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalClassicEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_criticalClassicMaxAcceleration = bl_out_criticalClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_criticalClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_criticalClassicMinAcceleration = bl_out_criticalClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_criticalClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalClassicEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_criticalClassicMaxAcceleration = bl_inout_criticalClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_criticalClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_criticalClassicMinAcceleration = bl_inout_criticalClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_criticalClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalClassicEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_criticalClassicMaxAcceleration = all_criticalClassicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_criticalClassicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_criticalClassicMinAcceleration = all_criticalClassicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_criticalClassicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_criticalClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_criticalClassicEqualClassicTimes += 1;
			}
		}
		bl_c_criticalClassicRelativeScheduleLength /= statisticTimes;
		bl_criticalClassicRelativeScheduleLength /= statisticTimes;
		bl_in_criticalClassicRelativeScheduleLength /= statisticTimes;
		bl_out_criticalClassicRelativeScheduleLength /= statisticTimes;
		bl_inout_criticalClassicRelativeScheduleLength /= statisticTimes;
		all_criticalClassicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_criticalClassicRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_criticalClassicRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_criticalClassicRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_criticalClassicRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_criticalClassicRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_criticalClassicTimes
				+ "/" + statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_criticalClassicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_criticalClassicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t"
				+ bl_out_criticalClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_criticalClassicTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_criticalClassicAverageAcceleration = 1 - bl_c_criticalClassicRelativeScheduleLength;
		bl_criticalClassicAverageAcceleration = 1 - bl_criticalClassicRelativeScheduleLength;
		bl_in_criticalClassicAverageAcceleration = 1 - bl_in_criticalClassicRelativeScheduleLength;
		bl_out_criticalClassicAverageAcceleration = 1 - bl_out_criticalClassicRelativeScheduleLength;
		bl_inout_criticalClassicAverageAcceleration = 1 - bl_inout_criticalClassicRelativeScheduleLength;
		all_criticalClassicAverageAcceleration = 1 - all_criticalClassicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c criticalClassic better than classic times:\t\t"
				+ bl_c_criticalClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c criticalClassic equal to classic times:\t\t"
				+ bl_c_criticalClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c criticalClassic worse than classic times:\t\t"
						+ (statisticTimes - bl_c_criticalClassicBetterClassicTimes - bl_c_criticalClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c criticalClassic max acceleration:\t\t\t"
				+ bl_c_criticalClassicMaxAcceleration);
		System.out.println("bl-c criticalClassic min acceleration:\t\t\t"
				+ bl_c_criticalClassicMinAcceleration);
		System.out.println("bl-c criticalClassic average acceleration:\t\t"
				+ bl_c_criticalClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl criticalClassic better than classic times:\t\t"
				+ bl_criticalClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl criticalClassic equal to classic times:\t\t"
				+ bl_criticalClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl criticalClassic worse than classic times:\t\t"
						+ (statisticTimes - bl_criticalClassicBetterClassicTimes - bl_criticalClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl criticalClassic max acceleration:\t\t\t"
				+ bl_criticalClassicMaxAcceleration);
		System.out.println("bl criticalClassic min acceleration:\t\t\t"
				+ bl_criticalClassicMinAcceleration);
		System.out.println("bl criticalClassic average acceleration:\t\t"
				+ bl_criticalClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in criticalClassic better than classic times:\t"
				+ bl_in_criticalClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in criticalClassic equal to classic times:\t\t"
				+ bl_in_criticalClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in criticalClassic worse than classic times:\t\t"
						+ (statisticTimes - bl_in_criticalClassicBetterClassicTimes - bl_in_criticalClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in criticalClassic max acceleration:\t\t\t"
				+ bl_in_criticalClassicMaxAcceleration);
		System.out.println("bl-in criticalClassic min acceleration:\t\t\t"
				+ bl_in_criticalClassicMinAcceleration);
		System.out.println("bl-in criticalClassic average acceleration:\t\t"
				+ bl_in_criticalClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out criticalClassic better than classic times:\t"
				+ bl_out_criticalClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out criticalClassic equal to classic times:\t\t"
				+ bl_out_criticalClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out criticalClassic worse than classic times:\t"
						+ (statisticTimes - bl_out_criticalClassicBetterClassicTimes - bl_out_criticalClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out criticalClassic max acceleration:\t\t"
				+ bl_out_criticalClassicMaxAcceleration);
		System.out.println("bl-out criticalClassic min acceleration:\t\t"
				+ bl_out_criticalClassicMinAcceleration);
		System.out.println("bl-out criticalClassic average acceleration:\t\t"
				+ bl_out_criticalClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout criticalClassic better than classic times:\t"
				+ bl_inout_criticalClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout criticalClassic equal to classic times:\t"
				+ bl_inout_criticalClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout criticalClassic worse than classic times:\t"
						+ (statisticTimes - bl_inout_criticalClassicBetterClassicTimes - bl_inout_criticalClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout criticalClassic max acceleration:\t\t"
				+ bl_inout_criticalClassicMaxAcceleration);
		System.out.println("bl-inout criticalClassic min acceleration:\t\t"
				+ bl_inout_criticalClassicMinAcceleration);
		System.out.println("bl-inout criticalClassic average acceleration:\t\t"
				+ bl_inout_criticalClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all criticalClassic better than classic times:\t\t"
				+ all_criticalClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all criticalClassic equal to classic times:\t\t"
				+ all_criticalClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all criticalClassic worse than classic times:\t\t"
						+ (statisticTimes - all_criticalClassicBetterClassicTimes - all_criticalClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all criticalClassic max acceleration:\t\t\t"
				+ all_criticalClassicMaxAcceleration);
		System.out.println("all criticalClassic min acceleration:\t\t\t"
				+ all_criticalClassicMinAcceleration);
		System.out.println("all criticalClassic average acceleration:\t\t"
				+ all_criticalClassicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CriticalChildClassic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCriticalChildClassic();
	}

}
