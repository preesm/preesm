package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingBasic {

	public StatisticRandomComparisonListSchedulingBasic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Basic Begins! *****");
		double ccr = 10;
		int statisticTimes = 10000;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_basicTimes = 0;
		double bl_c_basicRelativeScheduleLength = 0;
		int bl_basicTimes = 0;
		double bl_basicRelativeScheduleLength = 0;
		int bl_in_basicTimes = 0;
		double bl_in_basicRelativeScheduleLength = 0;
		int bl_out_basicTimes = 0;
		double bl_out_basicRelativeScheduleLength = 0;
		int bl_inout_basicTimes = 0;
		double bl_inout_basicRelativeScheduleLength = 0;

		int bl_c_basicBetterClassicTimes = 0;
		int bl_c_basicEqualClassicTimes = 0;
		double bl_c_basicMaxAcceleration = -1;
		double bl_c_basicMinAcceleration = 1;
		double bl_c_basicAverageAcceleration = 0;

		int bl_basicBetterClassicTimes = 0;
		int bl_basicEqualClassicTimes = 0;
		double bl_basicMaxAcceleration = -1;
		double bl_basicMinAcceleration = 1;
		double bl_basicAverageAcceleration = 0;

		int bl_in_basicBetterClassicTimes = 0;
		int bl_in_basicEqualClassicTimes = 0;
		double bl_in_basicMaxAcceleration = -1;
		double bl_in_basicMinAcceleration = 1;
		double bl_in_basicAverageAcceleration = 0;

		int bl_out_basicBetterClassicTimes = 0;
		int bl_out_basicEqualClassicTimes = 0;
		double bl_out_basicMaxAcceleration = -1;
		double bl_out_basicMinAcceleration = 1;
		double bl_out_basicAverageAcceleration = 0;

		int bl_inout_basicBetterClassicTimes = 0;
		int bl_inout_basicEqualClassicTimes = 0;
		double bl_inout_basicMaxAcceleration = -1;
		double bl_inout_basicMinAcceleration = 1;
		double bl_inout_basicAverageAcceleration = 0;

		double all_basicRelativeScheduleLength = 0;
		int all_basicBetterClassicTimes = 0;
		int all_basicEqualClassicTimes = 0;
		double all_basicMaxAcceleration = -1;
		double all_basicMinAcceleration = 1;
		double all_basicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingBasic comparison = new RandomComparisonListSchedulingBasic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_basicRelativeScheduleLength += comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_basicRelativeScheduleLength += comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_basicRelativeScheduleLength += comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_basicRelativeScheduleLength += comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_basicRelativeScheduleLength += comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevel()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_basicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_basicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_basicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_basicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_basicTimes += 1;
			}

			all_basicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_basicMaxAcceleration = bl_c_basicMaxAcceleration > 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_basicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_basicMinAcceleration = bl_c_basicMinAcceleration < 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_basicMinAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_basicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_basicEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_basicMaxAcceleration = bl_basicMaxAcceleration > 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_basicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_basicMinAcceleration = bl_basicMinAcceleration < 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_basicMinAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_basicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_basicEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_basicMaxAcceleration = bl_in_basicMaxAcceleration > 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_basicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_basicMinAcceleration = bl_in_basicMinAcceleration < 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_basicMinAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_basicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_basicEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_basicMaxAcceleration = bl_out_basicMaxAcceleration > 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_basicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_basicMinAcceleration = bl_out_basicMinAcceleration < 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_basicMinAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_basicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_basicEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_basicMaxAcceleration = bl_inout_basicMaxAcceleration > 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_basicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_basicMinAcceleration = bl_inout_basicMinAcceleration < 1
					- comparison
							.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_basicMinAcceleration
					: 1
							- comparison
									.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_basicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_basicEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_basicMaxAcceleration = all_basicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_basicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_basicMinAcceleration = all_basicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_basicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_basicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_basicEqualClassicTimes += 1;
			}
		}
		bl_c_basicRelativeScheduleLength /= statisticTimes;
		bl_basicRelativeScheduleLength /= statisticTimes;
		bl_in_basicRelativeScheduleLength /= statisticTimes;
		bl_out_basicRelativeScheduleLength /= statisticTimes;
		bl_inout_basicRelativeScheduleLength /= statisticTimes;
		all_basicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_basicRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_basicRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_basicRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_basicRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_basicRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_basicTimes + "/"
				+ statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_basicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_basicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t" + bl_out_basicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_basicTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_basicAverageAcceleration = 1 - bl_c_basicRelativeScheduleLength;
		bl_basicAverageAcceleration = 1 - bl_basicRelativeScheduleLength;
		bl_in_basicAverageAcceleration = 1 - bl_in_basicRelativeScheduleLength;
		bl_out_basicAverageAcceleration = 1 - bl_out_basicRelativeScheduleLength;
		bl_inout_basicAverageAcceleration = 1 - bl_inout_basicRelativeScheduleLength;
		all_basicAverageAcceleration = 1 - all_basicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c basic better than classic times:\t\t"
				+ bl_c_basicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c basic equal to classic times:\t\t"
				+ bl_c_basicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c basic worse than classic times:\t\t"
						+ (statisticTimes - bl_c_basicBetterClassicTimes - bl_c_basicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c basic max acceleration:\t\t\t"
				+ bl_c_basicMaxAcceleration);
		System.out.println("bl-c basic min acceleration:\t\t\t"
				+ bl_c_basicMinAcceleration);
		System.out.println("bl-c basic average acceleration:\t\t"
				+ bl_c_basicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl basic better than classic times:\t\t"
				+ bl_basicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl basic equal to classic times:\t\t"
				+ bl_basicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl basic worse than classic times:\t\t"
						+ (statisticTimes - bl_basicBetterClassicTimes - bl_basicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl basic max acceleration:\t\t\t"
				+ bl_basicMaxAcceleration);
		System.out.println("bl basic min acceleration:\t\t\t"
				+ bl_basicMinAcceleration);
		System.out.println("bl basic average acceleration:\t\t\t"
				+ bl_basicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in basic better than classic times:\t\t"
				+ bl_in_basicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in basic equal to classic times:\t\t"
				+ bl_in_basicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in basic worse than classic times:\t\t"
						+ (statisticTimes - bl_in_basicBetterClassicTimes - bl_in_basicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in basic max acceleration:\t\t\t"
				+ bl_in_basicMaxAcceleration);
		System.out.println("bl-in basic min acceleration:\t\t\t"
				+ bl_in_basicMinAcceleration);
		System.out.println("bl-in basic average acceleration:\t\t"
				+ bl_in_basicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out basic better than classic times:\t\t"
				+ bl_out_basicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out basic equal to classic times:\t\t"
				+ bl_out_basicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out basic worse than classic times:\t\t"
						+ (statisticTimes - bl_out_basicBetterClassicTimes - bl_out_basicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out basic max acceleration:\t\t\t"
				+ bl_out_basicMaxAcceleration);
		System.out.println("bl-out basic min acceleration:\t\t\t"
				+ bl_out_basicMinAcceleration);
		System.out.println("bl-out basic average acceleration:\t\t"
				+ bl_out_basicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout basic better than classic times:\t"
				+ bl_inout_basicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout basic equal to classic times:\t\t"
				+ bl_inout_basicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout basic worse than classic times:\t"
						+ (statisticTimes - bl_inout_basicBetterClassicTimes - bl_inout_basicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout basic max acceleration:\t\t"
				+ bl_inout_basicMaxAcceleration);
		System.out.println("bl-inout basic min acceleration:\t\t"
				+ bl_inout_basicMinAcceleration);
		System.out.println("bl-inout basic average acceleration:\t\t"
				+ bl_inout_basicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all basic better than classic times:\t\t"
				+ all_basicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all basic equal to classic times:\t\t"
				+ all_basicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all basic worse than classic times:\t\t"
						+ (statisticTimes - all_basicBetterClassicTimes - all_basicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all basic max acceleration:\t\t\t"
				+ all_basicMaxAcceleration);
		System.out.println("all basic min acceleration:\t\t\t"
				+ all_basicMinAcceleration);
		System.out.println("all basic average acceleration:\t\t\t"
				+ all_basicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Basic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingBasic();
	}

}
