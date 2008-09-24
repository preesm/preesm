package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingClassic {

	public StatisticRandomComparisonListSchedulingClassic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Classic Begins! *****");
		double ccr = 0.1;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_classicTimes = 0;
		double bl_c_classicRelativeScheduleLength = 0;
		int bl_classicTimes = 0;
		double bl_classicRelativeScheduleLength = 0;
		int bl_in_classicTimes = 0;
		double bl_in_classicRelativeScheduleLength = 0;
		int bl_out_classicTimes = 0;
		double bl_out_classicRelativeScheduleLength = 0;
		int bl_inout_classicTimes = 0;
		double bl_inout_classicRelativeScheduleLength = 0;

		int bl_c_classicBetterClassicTimes = 0;
		int bl_c_classicEqualClassicTimes = 0;
		double bl_c_classicMaxAcceleration = 0;
		double bl_c_classicMinAcceleration = 0;
		double bl_c_classicAverageAcceleration = 0;

		int bl_classicBetterClassicTimes = 0;
		int bl_classicEqualClassicTimes = 0;
		double bl_classicMaxAcceleration = 0;
		double bl_classicMinAcceleration = 0;
		double bl_classicAverageAcceleration = 0;

		int bl_in_classicBetterClassicTimes = 0;
		int bl_in_classicEqualClassicTimes = 0;
		double bl_in_classicMaxAcceleration = 0;
		double bl_in_classicMinAcceleration = 0;
		double bl_in_classicAverageAcceleration = 0;

		int bl_out_classicBetterClassicTimes = 0;
		int bl_out_classicEqualClassicTimes = 0;
		double bl_out_classicMaxAcceleration = 0;
		double bl_out_classicMinAcceleration = 0;
		double bl_out_classicAverageAcceleration = 0;

		int bl_inout_classicBetterClassicTimes = 0;
		int bl_inout_classicEqualClassicTimes = 0;
		double bl_inout_classicMaxAcceleration = 0;
		double bl_inout_classicMinAcceleration = 0;
		double bl_inout_classicAverageAcceleration = 0;

		double all_classicRelativeScheduleLength = 0;
		int all_classicBetterClassicTimes = 0;
		int all_classicEqualClassicTimes = 0;
		double all_classicMaxAcceleration = 0;
		double all_classicMinAcceleration = 0;
		double all_classicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingClassic comparison = new RandomComparisonListSchedulingClassic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_classicRelativeScheduleLength += comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_classicRelativeScheduleLength += comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_classicRelativeScheduleLength += comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_classicRelativeScheduleLength += comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_classicRelativeScheduleLength += comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevel()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength());
			minScheduleLength = Math.min(minScheduleLength, comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_classicTimes += 1;
			}

			all_classicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_classicMaxAcceleration = bl_c_classicMaxAcceleration > 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_classicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_classicMinAcceleration = bl_c_classicMinAcceleration < 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_classicMinAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_classicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_classicEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_classicMaxAcceleration = bl_classicMaxAcceleration > 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_classicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_classicMinAcceleration = bl_classicMinAcceleration < 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_classicMinAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_classicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_classicEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_classicMaxAcceleration = bl_in_classicMaxAcceleration > 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_classicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_classicMinAcceleration = bl_in_classicMinAcceleration < 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_classicMinAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_classicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_classicEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_classicMaxAcceleration = bl_out_classicMaxAcceleration > 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_classicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_classicMinAcceleration = bl_out_classicMinAcceleration < 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_classicMinAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_classicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_classicEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_classicMaxAcceleration = bl_inout_classicMaxAcceleration > 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_classicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_classicMinAcceleration = bl_inout_classicMinAcceleration < 1
					- comparison
							.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_classicMinAcceleration
					: 1
							- comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_classicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_classicEqualClassicTimes += 1;
			}

			// Compare all to classic
			// all_classicMaxAcceleration = all_classicMaxAcceleration >
			// bl_c_classicMaxAcceleration ? all_classicMaxAcceleration
			// : bl_c_classicMaxAcceleration;
			// all_classicMaxAcceleration = all_classicMaxAcceleration >
			// bl_classicMaxAcceleration ? all_classicMaxAcceleration
			// : bl_classicMaxAcceleration;
			// all_classicMaxAcceleration = all_classicMaxAcceleration >
			// bl_in_classicMaxAcceleration ? all_classicMaxAcceleration
			// : bl_in_classicMaxAcceleration;
			// all_classicMaxAcceleration = all_classicMaxAcceleration >
			// bl_out_classicMaxAcceleration ? all_classicMaxAcceleration
			// : bl_out_classicMaxAcceleration;
			// all_classicMaxAcceleration = all_classicMaxAcceleration >
			// bl_inout_classicMaxAcceleration ? all_classicMaxAcceleration
			// : bl_inout_classicMaxAcceleration;
			//
			// double tmp1 = Math.max(bl_c_classicMinAcceleration,
			// bl_classicMinAcceleration);
			// tmp1 = Math.max(tmp1, bl_in_classicMinAcceleration);
			// tmp1 = Math.max(tmp1, bl_out_classicMinAcceleration);
			// tmp1 = Math.max(tmp1, bl_inout_classicMinAcceleration);
			// all_classicMinAcceleration = all_classicMinAcceleration < tmp1 ?
			// all_classicMinAcceleration
			// : tmp1;
			all_classicMaxAcceleration = all_classicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_classicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_classicMinAcceleration = all_classicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_classicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_classicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_classicEqualClassicTimes += 1;
			}
		}
		bl_c_classicRelativeScheduleLength /= statisticTimes;
		bl_classicRelativeScheduleLength /= statisticTimes;
		bl_in_classicRelativeScheduleLength /= statisticTimes;
		bl_out_classicRelativeScheduleLength /= statisticTimes;
		bl_inout_classicRelativeScheduleLength /= statisticTimes;
		all_classicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_classicRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_classicRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_classicRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_classicRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_classicRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_classicTimes
				+ "/" + statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_classicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t" + bl_out_classicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_classicTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_classicAverageAcceleration = 1 - bl_c_classicRelativeScheduleLength;
		bl_classicAverageAcceleration = 1 - bl_classicRelativeScheduleLength;
		bl_in_classicAverageAcceleration = 1 - bl_in_classicRelativeScheduleLength;
		bl_out_classicAverageAcceleration = 1 - bl_out_classicRelativeScheduleLength;
		bl_inout_classicAverageAcceleration = 1 - bl_inout_classicRelativeScheduleLength;
		all_classicAverageAcceleration = 1 - all_classicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c classic better than classic times:\t\t"
				+ bl_c_classicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c classic equal to classic times:\t\t"
				+ bl_c_classicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c classic worse than classic times:\t\t"
						+ (statisticTimes - bl_c_classicBetterClassicTimes - bl_c_classicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c classic max acceleration:\t\t\t"
				+ bl_c_classicMaxAcceleration);
		System.out.println("bl-c classic min acceleration:\t\t\t"
				+ bl_c_classicMinAcceleration);
		System.out.println("bl-c classic average acceleration:\t\t"
				+ bl_c_classicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl classic better than classic times:\t\t"
				+ bl_classicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl classic equal to classic times:\t\t"
				+ bl_classicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl classic worse than classic times:\t\t"
						+ (statisticTimes - bl_classicBetterClassicTimes - bl_classicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl classic max acceleration:\t\t\t"
				+ bl_classicMaxAcceleration);
		System.out.println("bl classic min acceleration:\t\t\t"
				+ bl_classicMinAcceleration);
		System.out.println("bl classic average acceleration:\t\t"
				+ bl_classicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in classic better than classic times:\t"
				+ bl_in_classicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in classic equal to classic times:\t\t"
				+ bl_in_classicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in classic worse than classic times:\t\t"
						+ (statisticTimes - bl_in_classicBetterClassicTimes - bl_in_classicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in classic max acceleration:\t\t\t"
				+ bl_in_classicMaxAcceleration);
		System.out.println("bl-in classic min acceleration:\t\t\t"
				+ bl_in_classicMinAcceleration);
		System.out.println("bl-in classic average acceleration:\t\t"
				+ bl_in_classicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out classic better than classic times:\t"
				+ bl_out_classicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out classic equal to classic times:\t\t"
				+ bl_out_classicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out classic worse than classic times:\t"
						+ (statisticTimes - bl_out_classicBetterClassicTimes - bl_out_classicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out classic max acceleration:\t\t"
				+ bl_out_classicMaxAcceleration);
		System.out.println("bl-out classic min acceleration:\t\t"
				+ bl_out_classicMinAcceleration);
		System.out.println("bl-out classic average acceleration:\t\t"
				+ bl_out_classicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout classic better than classic times:\t"
				+ bl_inout_classicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout classic equal to classic times:\t"
				+ bl_inout_classicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout classic worse than classic times:\t"
						+ (statisticTimes - bl_inout_classicBetterClassicTimes - bl_inout_classicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout classic max acceleration:\t\t"
				+ bl_inout_classicMaxAcceleration);
		System.out.println("bl-inout classic min acceleration:\t\t"
				+ bl_inout_classicMinAcceleration);
		System.out.println("bl-inout classic average acceleration:\t\t"
				+ bl_inout_classicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all classic better than classic times:\t\t"
				+ all_classicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all classic equal to classic times:\t\t"
				+ all_classicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all classic worse than classic times:\t\t"
						+ (statisticTimes - all_classicBetterClassicTimes - all_classicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all classic max acceleration:\t\t\t"
				+ all_classicMaxAcceleration);
		System.out.println("all classic min acceleration:\t\t\t"
				+ all_classicMinAcceleration);
		System.out.println("all classic average acceleration:\t\t"
				+ all_classicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Classic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingClassic();
	}

}
