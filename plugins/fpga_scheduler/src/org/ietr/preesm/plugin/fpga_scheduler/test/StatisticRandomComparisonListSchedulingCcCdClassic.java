package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCcCdClassic {

	public StatisticRandomComparisonListSchedulingCcCdClassic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CcCdClassic Begins! *****");
		double ccr = 1;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_CcCdClassicTimes = 0;
		double bl_c_CcCdClassicRelativeScheduleLength = 0;
		int bl_CcCdClassicTimes = 0;
		double bl_CcCdClassicRelativeScheduleLength = 0;
		int bl_in_CcCdClassicTimes = 0;
		double bl_in_CcCdClassicRelativeScheduleLength = 0;
		int bl_out_CcCdClassicTimes = 0;
		double bl_out_CcCdClassicRelativeScheduleLength = 0;
		int bl_inout_CcCdClassicTimes = 0;
		double bl_inout_CcCdClassicRelativeScheduleLength = 0;

		int bl_c_CcCdClassicBetterClassicTimes = 0;
		int bl_c_CcCdClassicEqualClassicTimes = 0;
		double bl_c_CcCdClassicMaxAcceleration = -1;
		double bl_c_CcCdClassicMinAcceleration = 1;
		double bl_c_CcCdClassicAverageAcceleration = 0;

		int bl_CcCdClassicBetterClassicTimes = 0;
		int bl_CcCdClassicEqualClassicTimes = 0;
		double bl_CcCdClassicMaxAcceleration = -1;
		double bl_CcCdClassicMinAcceleration = 1;
		double bl_CcCdClassicAverageAcceleration = 0;

		int bl_in_CcCdClassicBetterClassicTimes = 0;
		int bl_in_CcCdClassicEqualClassicTimes = 0;
		double bl_in_CcCdClassicMaxAcceleration = -1;
		double bl_in_CcCdClassicMinAcceleration = 1;
		double bl_in_CcCdClassicAverageAcceleration = 0;

		int bl_out_CcCdClassicBetterClassicTimes = 0;
		int bl_out_CcCdClassicEqualClassicTimes = 0;
		double bl_out_CcCdClassicMaxAcceleration = -1;
		double bl_out_CcCdClassicMinAcceleration = 1;
		double bl_out_CcCdClassicAverageAcceleration = 0;

		int bl_inout_CcCdClassicBetterClassicTimes = 0;
		int bl_inout_CcCdClassicEqualClassicTimes = 0;
		double bl_inout_CcCdClassicMaxAcceleration = -1;
		double bl_inout_CcCdClassicMinAcceleration = 1;
		double bl_inout_CcCdClassicAverageAcceleration = 0;

		double all_CcCdClassicRelativeScheduleLength = 0;
		int all_CcCdClassicBetterClassicTimes = 0;
		int all_CcCdClassicEqualClassicTimes = 0;
		double all_CcCdClassicMaxAcceleration = -1;
		double all_CcCdClassicMinAcceleration = 1;
		double all_CcCdClassicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCcCdClassic comparison = new RandomComparisonListSchedulingCcCdClassic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_CcCdClassicRelativeScheduleLength += comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_CcCdClassicRelativeScheduleLength += comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_CcCdClassicRelativeScheduleLength += comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_CcCdClassicRelativeScheduleLength += comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_CcCdClassicRelativeScheduleLength += comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
					.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_CcCdClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_CcCdClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_CcCdClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_CcCdClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_CcCdClassicTimes += 1;
			}

			all_CcCdClassicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_CcCdClassicMaxAcceleration = bl_c_CcCdClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_CcCdClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_CcCdClassicMinAcceleration = bl_c_CcCdClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_CcCdClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_CcCdClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_CcCdClassicEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_CcCdClassicMaxAcceleration = bl_CcCdClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_CcCdClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_CcCdClassicMinAcceleration = bl_CcCdClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_CcCdClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_CcCdClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_CcCdClassicEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_CcCdClassicMaxAcceleration = bl_in_CcCdClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_CcCdClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_CcCdClassicMinAcceleration = bl_in_CcCdClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_CcCdClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_CcCdClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_CcCdClassicEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_CcCdClassicMaxAcceleration = bl_out_CcCdClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_CcCdClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_CcCdClassicMinAcceleration = bl_out_CcCdClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_CcCdClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_CcCdClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_CcCdClassicEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_CcCdClassicMaxAcceleration = bl_inout_CcCdClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_CcCdClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_CcCdClassicMinAcceleration = bl_inout_CcCdClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_CcCdClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_CcCdClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_CcCdClassicEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_CcCdClassicMaxAcceleration = all_CcCdClassicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_CcCdClassicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_CcCdClassicMinAcceleration = all_CcCdClassicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_CcCdClassicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_CcCdClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_CcCdClassicEqualClassicTimes += 1;
			}
		}
		bl_c_CcCdClassicRelativeScheduleLength /= statisticTimes;
		bl_CcCdClassicRelativeScheduleLength /= statisticTimes;
		bl_in_CcCdClassicRelativeScheduleLength /= statisticTimes;
		bl_out_CcCdClassicRelativeScheduleLength /= statisticTimes;
		bl_inout_CcCdClassicRelativeScheduleLength /= statisticTimes;
		all_CcCdClassicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_CcCdClassicRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_CcCdClassicRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_CcCdClassicRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_CcCdClassicRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_CcCdClassicRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_CcCdClassicTimes
				+ "/" + statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_CcCdClassicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-in optimization times:\t"
				+ bl_in_CcCdClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t"
				+ bl_out_CcCdClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_CcCdClassicTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_CcCdClassicAverageAcceleration = 1 - bl_c_CcCdClassicRelativeScheduleLength;
		bl_CcCdClassicAverageAcceleration = 1 - bl_CcCdClassicRelativeScheduleLength;
		bl_in_CcCdClassicAverageAcceleration = 1 - bl_in_CcCdClassicRelativeScheduleLength;
		bl_out_CcCdClassicAverageAcceleration = 1 - bl_out_CcCdClassicRelativeScheduleLength;
		bl_inout_CcCdClassicAverageAcceleration = 1 - bl_inout_CcCdClassicRelativeScheduleLength;
		all_CcCdClassicAverageAcceleration = 1 - all_CcCdClassicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c CcCdClassic better than classic times:\t"
				+ bl_c_CcCdClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c CcCdClassic equal to classic times:\t"
				+ bl_c_CcCdClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c CcCdClassic worse than classic times:\t"
						+ (statisticTimes - bl_c_CcCdClassicBetterClassicTimes - bl_c_CcCdClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c CcCdClassic max acceleration:\t\t"
				+ bl_c_CcCdClassicMaxAcceleration);
		System.out.println("bl-c CcCdClassic min acceleration:\t\t"
				+ bl_c_CcCdClassicMinAcceleration);
		System.out.println("bl-c CcCdClassic average acceleration:\t\t"
				+ bl_c_CcCdClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl CcCdClassic better than classic times:\t"
				+ bl_CcCdClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl CcCdClassic equal to classic times:\t\t"
				+ bl_CcCdClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl CcCdClassic worse than classic times:\t"
						+ (statisticTimes - bl_CcCdClassicBetterClassicTimes - bl_CcCdClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl CcCdClassic max acceleration:\t\t"
				+ bl_CcCdClassicMaxAcceleration);
		System.out.println("bl CcCdClassic min acceleration:\t\t"
				+ bl_CcCdClassicMinAcceleration);
		System.out.println("bl CcCdClassic average acceleration:\t\t"
				+ bl_CcCdClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in CcCdClassic better than classic times:\t"
				+ bl_in_CcCdClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in CcCdClassic equal to classic times:\t"
				+ bl_in_CcCdClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in CcCdClassic worse than classic times:\t"
						+ (statisticTimes - bl_in_CcCdClassicBetterClassicTimes - bl_in_CcCdClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in CcCdClassic max acceleration:\t\t"
				+ bl_in_CcCdClassicMaxAcceleration);
		System.out.println("bl-in CcCdClassic min acceleration:\t\t"
				+ bl_in_CcCdClassicMinAcceleration);
		System.out.println("bl-in CcCdClassic average acceleration:\t\t"
				+ bl_in_CcCdClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out CcCdClassic better than classic times:\t"
				+ bl_out_CcCdClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out CcCdClassic equal to classic times:\t"
				+ bl_out_CcCdClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out CcCdClassic worse than classic times:\t"
						+ (statisticTimes
								- bl_out_CcCdClassicBetterClassicTimes - bl_out_CcCdClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out CcCdClassic max acceleration:\t\t"
				+ bl_out_CcCdClassicMaxAcceleration);
		System.out.println("bl-out CcCdClassic min acceleration:\t\t"
				+ bl_out_CcCdClassicMinAcceleration);
		System.out.println("bl-out CcCdClassic average acceleration:\t"
				+ bl_out_CcCdClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out
				.println("bl-inout CcCdClassic better than classic times:\t"
						+ bl_inout_CcCdClassicBetterClassicTimes + "/"
						+ statisticTimes);
		System.out.println("bl-inout CcCdClassic equal to classic times:\t"
				+ bl_inout_CcCdClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout CcCdClassic worse than classic times:\t"
						+ (statisticTimes
								- bl_inout_CcCdClassicBetterClassicTimes - bl_inout_CcCdClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout CcCdClassic max acceleration:\t\t"
				+ bl_inout_CcCdClassicMaxAcceleration);
		System.out.println("bl-inout CcCdClassic min acceleration:\t\t"
				+ bl_inout_CcCdClassicMinAcceleration);
		System.out.println("bl-inout CcCdClassic average acceleration:\t"
				+ bl_inout_CcCdClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all CcCdClassic better than classic times:\t"
				+ all_CcCdClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all CcCdClassic equal to classic times:\t\t"
				+ all_CcCdClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all CcCdClassic worse than classic times:\t"
						+ (statisticTimes - all_CcCdClassicBetterClassicTimes - all_CcCdClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all CcCdClassic max acceleration:\t\t"
				+ all_CcCdClassicMaxAcceleration);
		System.out.println("all CcCdClassic min acceleration:\t\t"
				+ all_CcCdClassicMinAcceleration);
		System.out.println("all CcCdClassic average acceleration:\t\t"
				+ all_CcCdClassicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CcCdClassic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCcCdClassic();
	}

}
