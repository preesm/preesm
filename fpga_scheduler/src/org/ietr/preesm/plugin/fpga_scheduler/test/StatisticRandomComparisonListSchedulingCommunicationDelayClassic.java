package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCommunicationDelayClassic {

	public StatisticRandomComparisonListSchedulingCommunicationDelayClassic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CommunicationDelayClassic Begins! *****");
		double ccr = 10;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_delayClassicTimes = 0;
		double bl_c_delayClassicRelativeScheduleLength = 0;
		int bl_delayClassicTimes = 0;
		double bl_delayClassicRelativeScheduleLength = 0;
		int bl_in_delayClassicTimes = 0;
		double bl_in_delayClassicRelativeScheduleLength = 0;
		int bl_out_delayClassicTimes = 0;
		double bl_out_delayClassicRelativeScheduleLength = 0;
		int bl_inout_delayClassicTimes = 0;
		double bl_inout_delayClassicRelativeScheduleLength = 0;

		int bl_c_delayClassicBetterClassicTimes = 0;
		int bl_c_delayClassicEqualClassicTimes = 0;
		double bl_c_delayClassicMaxAcceleration = -1;
		double bl_c_delayClassicMinAcceleration = 1;
		double bl_c_delayClassicAverageAcceleration = 0;

		int bl_delayClassicBetterClassicTimes = 0;
		int bl_delayClassicEqualClassicTimes = 0;
		double bl_delayClassicMaxAcceleration = -1;
		double bl_delayClassicMinAcceleration = 1;
		double bl_delayClassicAverageAcceleration = 0;

		int bl_in_delayClassicBetterClassicTimes = 0;
		int bl_in_delayClassicEqualClassicTimes = 0;
		double bl_in_delayClassicMaxAcceleration = -1;
		double bl_in_delayClassicMinAcceleration = 1;
		double bl_in_delayClassicAverageAcceleration = 0;

		int bl_out_delayClassicBetterClassicTimes = 0;
		int bl_out_delayClassicEqualClassicTimes = 0;
		double bl_out_delayClassicMaxAcceleration = -1;
		double bl_out_delayClassicMinAcceleration = 1;
		double bl_out_delayClassicAverageAcceleration = 0;

		int bl_inout_delayClassicBetterClassicTimes = 0;
		int bl_inout_delayClassicEqualClassicTimes = 0;
		double bl_inout_delayClassicMaxAcceleration = -1;
		double bl_inout_delayClassicMinAcceleration = 1;
		double bl_inout_delayClassicAverageAcceleration = 0;

		double all_delayClassicRelativeScheduleLength = 0;
		int all_delayClassicBetterClassicTimes = 0;
		int all_delayClassicEqualClassicTimes = 0;
		double all_delayClassicMaxAcceleration = -1;
		double all_delayClassicMinAcceleration = 1;
		double all_delayClassicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCommunicationDelayClassic comparison = new RandomComparisonListSchedulingCommunicationDelayClassic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_delayClassicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_delayClassicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_delayClassicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_delayClassicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_delayClassicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayClassicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayClassicTimes += 1;
			}

			all_delayClassicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_delayClassicMaxAcceleration = bl_c_delayClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_delayClassicMinAcceleration = bl_c_delayClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayClassicEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_delayClassicMaxAcceleration = bl_delayClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_delayClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_delayClassicMinAcceleration = bl_delayClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_delayClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayClassicEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_delayClassicMaxAcceleration = bl_in_delayClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_delayClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_delayClassicMinAcceleration = bl_in_delayClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayClassicEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_delayClassicMaxAcceleration = bl_out_delayClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_delayClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_delayClassicMinAcceleration = bl_out_delayClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_delayClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayClassicEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_delayClassicMaxAcceleration = bl_inout_delayClassicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_delayClassicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_delayClassicMinAcceleration = bl_inout_delayClassicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_delayClassicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayClassicEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_delayClassicMaxAcceleration = all_delayClassicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_delayClassicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_delayClassicMinAcceleration = all_delayClassicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_delayClassicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_delayClassicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_delayClassicEqualClassicTimes += 1;
			}
		}
		bl_c_delayClassicRelativeScheduleLength /= statisticTimes;
		bl_delayClassicRelativeScheduleLength /= statisticTimes;
		bl_in_delayClassicRelativeScheduleLength /= statisticTimes;
		bl_out_delayClassicRelativeScheduleLength /= statisticTimes;
		bl_inout_delayClassicRelativeScheduleLength /= statisticTimes;
		all_delayClassicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_delayClassicRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_delayClassicRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_delayClassicRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_delayClassicRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_delayClassicRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_delayClassicTimes + "/"
				+ statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_delayClassicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_delayClassicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t" + bl_out_delayClassicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_delayClassicTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_delayClassicAverageAcceleration = 1 - bl_c_delayClassicRelativeScheduleLength;
		bl_delayClassicAverageAcceleration = 1 - bl_delayClassicRelativeScheduleLength;
		bl_in_delayClassicAverageAcceleration = 1 - bl_in_delayClassicRelativeScheduleLength;
		bl_out_delayClassicAverageAcceleration = 1 - bl_out_delayClassicRelativeScheduleLength;
		bl_inout_delayClassicAverageAcceleration = 1 - bl_inout_delayClassicRelativeScheduleLength;
		all_delayClassicAverageAcceleration = 1 - all_delayClassicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c delayClassic better than classic times:\t\t"
				+ bl_c_delayClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c delayClassic equal to classic times:\t\t"
				+ bl_c_delayClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c delayClassic worse than classic times:\t\t"
						+ (statisticTimes - bl_c_delayClassicBetterClassicTimes - bl_c_delayClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c delayClassic max acceleration:\t\t\t"
				+ bl_c_delayClassicMaxAcceleration);
		System.out.println("bl-c delayClassic min acceleration:\t\t\t"
				+ bl_c_delayClassicMinAcceleration);
		System.out.println("bl-c delayClassic average acceleration:\t\t\t"
				+ bl_c_delayClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl delayClassic better than classic times:\t\t"
				+ bl_delayClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl delayClassic equal to classic times:\t\t\t"
				+ bl_delayClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl delayClassic worse than classic times:\t\t"
						+ (statisticTimes - bl_delayClassicBetterClassicTimes - bl_delayClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl delayClassic max acceleration:\t\t\t"
				+ bl_delayClassicMaxAcceleration);
		System.out.println("bl delayClassic min acceleration:\t\t\t"
				+ bl_delayClassicMinAcceleration);
		System.out.println("bl delayClassic average acceleration:\t\t\t"
				+ bl_delayClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in delayClassic better than classic times:\t\t"
				+ bl_in_delayClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in delayClassic equal to classic times:\t\t"
				+ bl_in_delayClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in delayClassic worse than classic times:\t\t"
						+ (statisticTimes - bl_in_delayClassicBetterClassicTimes - bl_in_delayClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in delayClassic max acceleration:\t\t\t"
				+ bl_in_delayClassicMaxAcceleration);
		System.out.println("bl-in delayClassic min acceleration:\t\t\t"
				+ bl_in_delayClassicMinAcceleration);
		System.out.println("bl-in delayClassic average acceleration:\t\t"
				+ bl_in_delayClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out delayClassic better than classic times:\t\t"
				+ bl_out_delayClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out delayClassic equal to classic times:\t\t"
				+ bl_out_delayClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out delayClassic worse than classic times:\t\t"
						+ (statisticTimes - bl_out_delayClassicBetterClassicTimes - bl_out_delayClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out delayClassic max acceleration:\t\t\t"
				+ bl_out_delayClassicMaxAcceleration);
		System.out.println("bl-out delayClassic min acceleration:\t\t\t"
				+ bl_out_delayClassicMinAcceleration);
		System.out.println("bl-out delayClassic average acceleration:\t\t"
				+ bl_out_delayClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout delayClassic better than classic times:\t"
				+ bl_inout_delayClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout delayClassic equal to classic times:\t\t"
				+ bl_inout_delayClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout delayClassic worse than classic times:\t\t"
						+ (statisticTimes - bl_inout_delayClassicBetterClassicTimes - bl_inout_delayClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout delayClassic max acceleration:\t\t\t"
				+ bl_inout_delayClassicMaxAcceleration);
		System.out.println("bl-inout delayClassic min acceleration:\t\t\t"
				+ bl_inout_delayClassicMinAcceleration);
		System.out.println("bl-inout delayClassic average acceleration:\t\t"
				+ bl_inout_delayClassicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all delayClassic better than classic times:\t\t"
				+ all_delayClassicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all delayClassic equal to classic times:\t\t"
				+ all_delayClassicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all delayClassic worse than classic times:\t\t"
						+ (statisticTimes - all_delayClassicBetterClassicTimes - all_delayClassicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all delayClassic max acceleration:\t\t\t"
				+ all_delayClassicMaxAcceleration);
		System.out.println("all delayClassic min acceleration:\t\t\t"
				+ all_delayClassicMinAcceleration);
		System.out.println("all delayClassic average acceleration:\t\t\t"
				+ all_delayClassicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CommunicationDelayClassic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCommunicationDelayClassic();
	}

}
