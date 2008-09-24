package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCcCdBasic {

	public StatisticRandomComparisonListSchedulingCcCdBasic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CcCdBasic Begins! *****");
		double ccr = 1;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_CcCdBasicTimes = 0;
		double bl_c_CcCdBasicRelativeScheduleLength = 0;
		int bl_CcCdBasicTimes = 0;
		double bl_CcCdBasicRelativeScheduleLength = 0;
		int bl_in_CcCdBasicTimes = 0;
		double bl_in_CcCdBasicRelativeScheduleLength = 0;
		int bl_out_CcCdBasicTimes = 0;
		double bl_out_CcCdBasicRelativeScheduleLength = 0;
		int bl_inout_CcCdBasicTimes = 0;
		double bl_inout_CcCdBasicRelativeScheduleLength = 0;

		int bl_c_CcCdBasicBetterClassicTimes = 0;
		int bl_c_CcCdBasicEqualClassicTimes = 0;
		double bl_c_CcCdBasicMaxAcceleration = -1;
		double bl_c_CcCdBasicMinAcceleration = 1;
		double bl_c_CcCdBasicAverageAcceleration = 0;

		int bl_CcCdBasicBetterClassicTimes = 0;
		int bl_CcCdBasicEqualClassicTimes = 0;
		double bl_CcCdBasicMaxAcceleration = -1;
		double bl_CcCdBasicMinAcceleration = 1;
		double bl_CcCdBasicAverageAcceleration = 0;

		int bl_in_CcCdBasicBetterClassicTimes = 0;
		int bl_in_CcCdBasicEqualClassicTimes = 0;
		double bl_in_CcCdBasicMaxAcceleration = -1;
		double bl_in_CcCdBasicMinAcceleration = 1;
		double bl_in_CcCdBasicAverageAcceleration = 0;

		int bl_out_CcCdBasicBetterClassicTimes = 0;
		int bl_out_CcCdBasicEqualClassicTimes = 0;
		double bl_out_CcCdBasicMaxAcceleration = -1;
		double bl_out_CcCdBasicMinAcceleration = 1;
		double bl_out_CcCdBasicAverageAcceleration = 0;

		int bl_inout_CcCdBasicBetterClassicTimes = 0;
		int bl_inout_CcCdBasicEqualClassicTimes = 0;
		double bl_inout_CcCdBasicMaxAcceleration = -1;
		double bl_inout_CcCdBasicMinAcceleration = 1;
		double bl_inout_CcCdBasicAverageAcceleration = 0;

		double all_CcCdBasicRelativeScheduleLength = 0;
		int all_CcCdBasicBetterClassicTimes = 0;
		int all_CcCdBasicEqualClassicTimes = 0;
		double all_CcCdBasicMaxAcceleration = -1;
		double all_CcCdBasicMinAcceleration = 1;
		double all_CcCdBasicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCcCdBasic comparison = new RandomComparisonListSchedulingCcCdBasic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_CcCdBasicRelativeScheduleLength += comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_CcCdBasicRelativeScheduleLength += comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_CcCdBasicRelativeScheduleLength += comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_CcCdBasicRelativeScheduleLength += comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_CcCdBasicRelativeScheduleLength += comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_CcCdBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_CcCdBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_CcCdBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_CcCdBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_CcCdBasicTimes += 1;
			}

			all_CcCdBasicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_CcCdBasicMaxAcceleration = bl_c_CcCdBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_CcCdBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_CcCdBasicMinAcceleration = bl_c_CcCdBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_CcCdBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_CcCdBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_CcCdBasicEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_CcCdBasicMaxAcceleration = bl_CcCdBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_CcCdBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_CcCdBasicMinAcceleration = bl_CcCdBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_CcCdBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_CcCdBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_CcCdBasicEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_CcCdBasicMaxAcceleration = bl_in_CcCdBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_CcCdBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_CcCdBasicMinAcceleration = bl_in_CcCdBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_CcCdBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_CcCdBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_CcCdBasicEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_CcCdBasicMaxAcceleration = bl_out_CcCdBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_CcCdBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_CcCdBasicMinAcceleration = bl_out_CcCdBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_CcCdBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_CcCdBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_CcCdBasicEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_CcCdBasicMaxAcceleration = bl_inout_CcCdBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_CcCdBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_CcCdBasicMinAcceleration = bl_inout_CcCdBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_CcCdBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_CcCdBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCcCdBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_CcCdBasicEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_CcCdBasicMaxAcceleration = all_CcCdBasicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_CcCdBasicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_CcCdBasicMinAcceleration = all_CcCdBasicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_CcCdBasicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_CcCdBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_CcCdBasicEqualClassicTimes += 1;
			}
		}
		bl_c_CcCdBasicRelativeScheduleLength /= statisticTimes;
		bl_CcCdBasicRelativeScheduleLength /= statisticTimes;
		bl_in_CcCdBasicRelativeScheduleLength /= statisticTimes;
		bl_out_CcCdBasicRelativeScheduleLength /= statisticTimes;
		bl_inout_CcCdBasicRelativeScheduleLength /= statisticTimes;
		all_CcCdBasicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_CcCdBasicRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_CcCdBasicRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_CcCdBasicRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_CcCdBasicRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_CcCdBasicRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_CcCdBasicTimes
				+ "/" + statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_CcCdBasicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_CcCdBasicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t"
				+ bl_out_CcCdBasicTimes + "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_CcCdBasicTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_CcCdBasicAverageAcceleration = 1 - bl_c_CcCdBasicRelativeScheduleLength;
		bl_CcCdBasicAverageAcceleration = 1 - bl_CcCdBasicRelativeScheduleLength;
		bl_in_CcCdBasicAverageAcceleration = 1 - bl_in_CcCdBasicRelativeScheduleLength;
		bl_out_CcCdBasicAverageAcceleration = 1 - bl_out_CcCdBasicRelativeScheduleLength;
		bl_inout_CcCdBasicAverageAcceleration = 1 - bl_inout_CcCdBasicRelativeScheduleLength;
		all_CcCdBasicAverageAcceleration = 1 - all_CcCdBasicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c CcCdBasic better than classic times:\t"
				+ bl_c_CcCdBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c CcCdBasic equal to classic times:\t\t"
				+ bl_c_CcCdBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c CcCdBasic worse than classic times:\t"
						+ (statisticTimes - bl_c_CcCdBasicBetterClassicTimes - bl_c_CcCdBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c CcCdBasic max acceleration:\t\t"
				+ bl_c_CcCdBasicMaxAcceleration);
		System.out.println("bl-c CcCdBasic min acceleration:\t\t"
				+ bl_c_CcCdBasicMinAcceleration);
		System.out.println("bl-c CcCdBasic average acceleration:\t\t"
				+ bl_c_CcCdBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl CcCdBasic better than classic times:\t\t"
				+ bl_CcCdBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl CcCdBasic equal to classic times:\t\t"
				+ bl_CcCdBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl CcCdBasic worse than classic times:\t\t"
						+ (statisticTimes - bl_CcCdBasicBetterClassicTimes - bl_CcCdBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl CcCdBasic max acceleration:\t\t\t"
				+ bl_CcCdBasicMaxAcceleration);
		System.out.println("bl CcCdBasic min acceleration:\t\t\t"
				+ bl_CcCdBasicMinAcceleration);
		System.out.println("bl CcCdBasic average acceleration:\t\t"
				+ bl_CcCdBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in CcCdBasic better than classic times:\t"
				+ bl_in_CcCdBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in CcCdBasic equal to classic times:\t\t"
				+ bl_in_CcCdBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in CcCdBasic worse than classic times:\t"
						+ (statisticTimes - bl_in_CcCdBasicBetterClassicTimes - bl_in_CcCdBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in CcCdBasic max acceleration:\t\t"
				+ bl_in_CcCdBasicMaxAcceleration);
		System.out.println("bl-in CcCdBasic min acceleration:\t\t"
				+ bl_in_CcCdBasicMinAcceleration);
		System.out.println("bl-in CcCdBasic average acceleration:\t\t"
				+ bl_in_CcCdBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out CcCdBasic better than classic times:\t"
				+ bl_out_CcCdBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out CcCdBasic equal to classic times:\t"
				+ bl_out_CcCdBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out CcCdBasic worse than classic times:\t"
						+ (statisticTimes - bl_out_CcCdBasicBetterClassicTimes - bl_out_CcCdBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out CcCdBasic max acceleration:\t\t"
				+ bl_out_CcCdBasicMaxAcceleration);
		System.out.println("bl-out CcCdBasic min acceleration:\t\t"
				+ bl_out_CcCdBasicMinAcceleration);
		System.out.println("bl-out CcCdBasic average acceleration:\t\t"
				+ bl_out_CcCdBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout CcCdBasic better than classic times:\t"
				+ bl_inout_CcCdBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout CcCdBasic equal to classic times:\t"
				+ bl_inout_CcCdBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout CcCdBasic worse than classic times:\t"
						+ (statisticTimes - bl_inout_CcCdBasicBetterClassicTimes - bl_inout_CcCdBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout CcCdBasic max acceleration:\t\t"
				+ bl_inout_CcCdBasicMaxAcceleration);
		System.out.println("bl-inout CcCdBasic min acceleration:\t\t"
				+ bl_inout_CcCdBasicMinAcceleration);
		System.out.println("bl-inout CcCdBasic average acceleration:\t"
				+ bl_inout_CcCdBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all CcCdBasic better than classic times:\t"
				+ all_CcCdBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all CcCdBasic equal to classic times:\t\t"
				+ all_CcCdBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all CcCdBasic worse than classic times:\t\t"
						+ (statisticTimes - all_CcCdBasicBetterClassicTimes - all_CcCdBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all CcCdBasic max acceleration:\t\t\t"
				+ all_CcCdBasicMaxAcceleration);
		System.out.println("all CcCdBasic min acceleration:\t\t\t"
				+ all_CcCdBasicMinAcceleration);
		System.out.println("all CcCdBasic average acceleration:\t\t"
				+ all_CcCdBasicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CcCdBasic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCcCdBasic();
	}

}
