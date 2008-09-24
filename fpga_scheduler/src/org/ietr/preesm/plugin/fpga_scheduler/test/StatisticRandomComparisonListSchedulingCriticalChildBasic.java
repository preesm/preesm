package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCriticalChildBasic {

	public StatisticRandomComparisonListSchedulingCriticalChildBasic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CriticalChildBasic Begins! *****");
		double ccr = 1;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_criticalBasicTimes = 0;
		double bl_c_criticalBasicRelativeScheduleLength = 0;
		int bl_criticalBasicTimes = 0;
		double bl_criticalBasicRelativeScheduleLength = 0;
		int bl_in_criticalBasicTimes = 0;
		double bl_in_criticalBasicRelativeScheduleLength = 0;
		int bl_out_criticalBasicTimes = 0;
		double bl_out_criticalBasicRelativeScheduleLength = 0;
		int bl_inout_criticalBasicTimes = 0;
		double bl_inout_criticalBasicRelativeScheduleLength = 0;

		int bl_c_criticalBasicBetterClassicTimes = 0;
		int bl_c_criticalBasicEqualClassicTimes = 0;
		double bl_c_criticalBasicMaxAcceleration = -1;
		double bl_c_criticalBasicMinAcceleration = 1;
		double bl_c_criticalBasicAverageAcceleration = 0;

		int bl_criticalBasicBetterClassicTimes = 0;
		int bl_criticalBasicEqualClassicTimes = 0;
		double bl_criticalBasicMaxAcceleration = -1;
		double bl_criticalBasicMinAcceleration = 1;
		double bl_criticalBasicAverageAcceleration = 0;

		int bl_in_criticalBasicBetterClassicTimes = 0;
		int bl_in_criticalBasicEqualClassicTimes = 0;
		double bl_in_criticalBasicMaxAcceleration = -1;
		double bl_in_criticalBasicMinAcceleration = 1;
		double bl_in_criticalBasicAverageAcceleration = 0;

		int bl_out_criticalBasicBetterClassicTimes = 0;
		int bl_out_criticalBasicEqualClassicTimes = 0;
		double bl_out_criticalBasicMaxAcceleration = -1;
		double bl_out_criticalBasicMinAcceleration = 1;
		double bl_out_criticalBasicAverageAcceleration = 0;

		int bl_inout_criticalBasicBetterClassicTimes = 0;
		int bl_inout_criticalBasicEqualClassicTimes = 0;
		double bl_inout_criticalBasicMaxAcceleration = -1;
		double bl_inout_criticalBasicMinAcceleration = 1;
		double bl_inout_criticalBasicAverageAcceleration = 0;

		double all_criticalBasicRelativeScheduleLength = 0;
		int all_criticalBasicBetterClassicTimes = 0;
		int all_criticalBasicEqualClassicTimes = 0;
		double all_criticalBasicMaxAcceleration = -1;
		double all_criticalBasicMinAcceleration = 1;
		double all_criticalBasicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCriticalChildBasic comparison = new RandomComparisonListSchedulingCriticalChildBasic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_criticalBasicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_criticalBasicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_criticalBasicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_criticalBasicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_criticalBasicRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalBasicTimes += 1;
			}

			all_criticalBasicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_criticalBasicMaxAcceleration = bl_c_criticalBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_criticalBasicMinAcceleration = bl_c_criticalBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalBasicEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_criticalBasicMaxAcceleration = bl_criticalBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_criticalBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_criticalBasicMinAcceleration = bl_criticalBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_criticalBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalBasicEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_criticalBasicMaxAcceleration = bl_in_criticalBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_criticalBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_criticalBasicMinAcceleration = bl_in_criticalBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalBasicEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_criticalBasicMaxAcceleration = bl_out_criticalBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_criticalBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_criticalBasicMinAcceleration = bl_out_criticalBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_criticalBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalBasicEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_criticalBasicMaxAcceleration = bl_inout_criticalBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_criticalBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_criticalBasicMinAcceleration = bl_inout_criticalBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_criticalBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalBasicEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_criticalBasicMaxAcceleration = all_criticalBasicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_criticalBasicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_criticalBasicMinAcceleration = all_criticalBasicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_criticalBasicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_criticalBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_criticalBasicEqualClassicTimes += 1;
			}
		}
		bl_c_criticalBasicRelativeScheduleLength /= statisticTimes;
		bl_criticalBasicRelativeScheduleLength /= statisticTimes;
		bl_in_criticalBasicRelativeScheduleLength /= statisticTimes;
		bl_out_criticalBasicRelativeScheduleLength /= statisticTimes;
		bl_inout_criticalBasicRelativeScheduleLength /= statisticTimes;
		all_criticalBasicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_criticalBasicRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_criticalBasicRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_criticalBasicRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_criticalBasicRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_criticalBasicRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_criticalBasicTimes
				+ "/" + statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_criticalBasicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_criticalBasicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t"
				+ bl_out_criticalBasicTimes + "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_criticalBasicTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_criticalBasicAverageAcceleration = 1 - bl_c_criticalBasicRelativeScheduleLength;
		bl_criticalBasicAverageAcceleration = 1 - bl_criticalBasicRelativeScheduleLength;
		bl_in_criticalBasicAverageAcceleration = 1 - bl_in_criticalBasicRelativeScheduleLength;
		bl_out_criticalBasicAverageAcceleration = 1 - bl_out_criticalBasicRelativeScheduleLength;
		bl_inout_criticalBasicAverageAcceleration = 1 - bl_inout_criticalBasicRelativeScheduleLength;
		all_criticalBasicAverageAcceleration = 1 - all_criticalBasicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c criticalBasic better than classic times:\t\t"
				+ bl_c_criticalBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c criticalBasic equal to classic times:\t\t"
				+ bl_c_criticalBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c criticalBasic worse than classic times:\t\t"
						+ (statisticTimes - bl_c_criticalBasicBetterClassicTimes - bl_c_criticalBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c criticalBasic max acceleration:\t\t\t"
				+ bl_c_criticalBasicMaxAcceleration);
		System.out.println("bl-c criticalBasic min acceleration:\t\t\t"
				+ bl_c_criticalBasicMinAcceleration);
		System.out.println("bl-c criticalBasic average acceleration:\t\t"
				+ bl_c_criticalBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl criticalBasic better than classic times:\t\t"
				+ bl_criticalBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl criticalBasic equal to classic times:\t\t"
				+ bl_criticalBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl criticalBasic worse than classic times:\t\t"
						+ (statisticTimes - bl_criticalBasicBetterClassicTimes - bl_criticalBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl criticalBasic max acceleration:\t\t\t"
				+ bl_criticalBasicMaxAcceleration);
		System.out.println("bl criticalBasic min acceleration:\t\t\t"
				+ bl_criticalBasicMinAcceleration);
		System.out.println("bl criticalBasic average acceleration:\t\t\t"
				+ bl_criticalBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in criticalBasic better than classic times:\t\t"
				+ bl_in_criticalBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in criticalBasic equal to classic times:\t\t"
				+ bl_in_criticalBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in criticalBasic worse than classic times:\t\t"
						+ (statisticTimes - bl_in_criticalBasicBetterClassicTimes - bl_in_criticalBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in criticalBasic max acceleration:\t\t\t"
				+ bl_in_criticalBasicMaxAcceleration);
		System.out.println("bl-in criticalBasic min acceleration:\t\t\t"
				+ bl_in_criticalBasicMinAcceleration);
		System.out.println("bl-in criticalBasic average acceleration:\t\t"
				+ bl_in_criticalBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out criticalBasic better than classic times:\t\t"
				+ bl_out_criticalBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out criticalBasic equal to classic times:\t\t"
				+ bl_out_criticalBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out criticalBasic worse than classic times:\t\t"
						+ (statisticTimes - bl_out_criticalBasicBetterClassicTimes - bl_out_criticalBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out criticalBasic max acceleration:\t\t\t"
				+ bl_out_criticalBasicMaxAcceleration);
		System.out.println("bl-out criticalBasic min acceleration:\t\t\t"
				+ bl_out_criticalBasicMinAcceleration);
		System.out.println("bl-out criticalBasic average acceleration:\t\t"
				+ bl_out_criticalBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout criticalBasic better than classic times:\t"
				+ bl_inout_criticalBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout criticalBasic equal to classic times:\t\t"
				+ bl_inout_criticalBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout criticalBasic worse than classic times:\t"
						+ (statisticTimes - bl_inout_criticalBasicBetterClassicTimes - bl_inout_criticalBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout criticalBasic max acceleration:\t\t"
				+ bl_inout_criticalBasicMaxAcceleration);
		System.out.println("bl-inout criticalBasic min acceleration:\t\t"
				+ bl_inout_criticalBasicMinAcceleration);
		System.out.println("bl-inout criticalBasic average acceleration:\t\t"
				+ bl_inout_criticalBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all criticalBasic better than classic times:\t\t"
				+ all_criticalBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all criticalBasic equal to classic times:\t\t"
				+ all_criticalBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all criticalBasic worse than classic times:\t\t"
						+ (statisticTimes - all_criticalBasicBetterClassicTimes - all_criticalBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all criticalBasic max acceleration:\t\t\t"
				+ all_criticalBasicMaxAcceleration);
		System.out.println("all criticalBasic min acceleration:\t\t\t"
				+ all_criticalBasicMinAcceleration);
		System.out.println("all criticalBasic average acceleration:\t\t\t"
				+ all_criticalBasicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CriticalChildBasic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCriticalChildBasic();
	}

}
