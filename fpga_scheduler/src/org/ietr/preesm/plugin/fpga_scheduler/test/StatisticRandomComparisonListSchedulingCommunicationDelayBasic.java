package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCommunicationDelayBasic {

	public StatisticRandomComparisonListSchedulingCommunicationDelayBasic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CommunicationDelayBasic Begins! *****");
		double ccr = 1;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_delayBasicTimes = 0;
		double bl_c_delayBasicRelativeScheduleLength = 0;
		int bl_delayBasicTimes = 0;
		double bl_delayBasicRelativeScheduleLength = 0;
		int bl_in_delayBasicTimes = 0;
		double bl_in_delayBasicRelativeScheduleLength = 0;
		int bl_out_delayBasicTimes = 0;
		double bl_out_delayBasicRelativeScheduleLength = 0;
		int bl_inout_delayBasicTimes = 0;
		double bl_inout_delayBasicRelativeScheduleLength = 0;

		int bl_c_delayBasicBetterClassicTimes = 0;
		int bl_c_delayBasicEqualClassicTimes = 0;
		double bl_c_delayBasicMaxAcceleration = -1;
		double bl_c_delayBasicMinAcceleration = 1;
		double bl_c_delayBasicAverageAcceleration = 0;

		int bl_delayBasicBetterClassicTimes = 0;
		int bl_delayBasicEqualClassicTimes = 0;
		double bl_delayBasicMaxAcceleration = -1;
		double bl_delayBasicMinAcceleration = 1;
		double bl_delayBasicAverageAcceleration = 0;

		int bl_in_delayBasicBetterClassicTimes = 0;
		int bl_in_delayBasicEqualClassicTimes = 0;
		double bl_in_delayBasicMaxAcceleration = -1;
		double bl_in_delayBasicMinAcceleration = 1;
		double bl_in_delayBasicAverageAcceleration = 0;

		int bl_out_delayBasicBetterClassicTimes = 0;
		int bl_out_delayBasicEqualClassicTimes = 0;
		double bl_out_delayBasicMaxAcceleration = -1;
		double bl_out_delayBasicMinAcceleration = 1;
		double bl_out_delayBasicAverageAcceleration = 0;

		int bl_inout_delayBasicBetterClassicTimes = 0;
		int bl_inout_delayBasicEqualClassicTimes = 0;
		double bl_inout_delayBasicMaxAcceleration = -1;
		double bl_inout_delayBasicMinAcceleration = 1;
		double bl_inout_delayBasicAverageAcceleration = 0;

		double all_delayBasicRelativeScheduleLength = 0;
		int all_delayBasicBetterClassicTimes = 0;
		int all_delayBasicEqualClassicTimes = 0;
		double all_delayBasicMaxAcceleration = -1;
		double all_delayBasicMinAcceleration = 1;
		double all_delayBasicAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCommunicationDelayBasic comparison = new RandomComparisonListSchedulingCommunicationDelayBasic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_delayBasicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_delayBasicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_delayBasicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_delayBasicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_delayBasicRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayBasicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayBasicTimes += 1;
			}

			all_delayBasicRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_delayBasicMaxAcceleration = bl_c_delayBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_delayBasicMinAcceleration = bl_c_delayBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayBasicEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_delayBasicMaxAcceleration = bl_delayBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_delayBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_delayBasicMinAcceleration = bl_delayBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_delayBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayBasicEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_delayBasicMaxAcceleration = bl_in_delayBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_delayBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_delayBasicMinAcceleration = bl_in_delayBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayBasicEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_delayBasicMaxAcceleration = bl_out_delayBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_delayBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_delayBasicMinAcceleration = bl_out_delayBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_delayBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayBasicEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_delayBasicMaxAcceleration = bl_inout_delayBasicMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_delayBasicMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_delayBasicMinAcceleration = bl_inout_delayBasicMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_delayBasicMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayBasicEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_delayBasicMaxAcceleration = all_delayBasicMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_delayBasicMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_delayBasicMinAcceleration = all_delayBasicMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_delayBasicMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_delayBasicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_delayBasicEqualClassicTimes += 1;
			}
		}
		bl_c_delayBasicRelativeScheduleLength /= statisticTimes;
		bl_delayBasicRelativeScheduleLength /= statisticTimes;
		bl_in_delayBasicRelativeScheduleLength /= statisticTimes;
		bl_out_delayBasicRelativeScheduleLength /= statisticTimes;
		bl_inout_delayBasicRelativeScheduleLength /= statisticTimes;
		all_delayBasicRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_delayBasicRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_delayBasicRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_delayBasicRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_delayBasicRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_delayBasicRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_delayBasicTimes
				+ "/" + statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_delayBasicTimes
				+ "/" + statisticTimes);
		System.out.println("bl-in optimization times:\t"
				+ bl_in_delayBasicTimes + "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t"
				+ bl_out_delayBasicTimes + "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_delayBasicTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_delayBasicAverageAcceleration = 1 - bl_c_delayBasicRelativeScheduleLength;
		bl_delayBasicAverageAcceleration = 1 - bl_delayBasicRelativeScheduleLength;
		bl_in_delayBasicAverageAcceleration = 1 - bl_in_delayBasicRelativeScheduleLength;
		bl_out_delayBasicAverageAcceleration = 1 - bl_out_delayBasicRelativeScheduleLength;
		bl_inout_delayBasicAverageAcceleration = 1 - bl_inout_delayBasicRelativeScheduleLength;
		all_delayBasicAverageAcceleration = 1 - all_delayBasicRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c delayBasic better than classic times:\t"
				+ bl_c_delayBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c delayBasic equal to classic times:\t\t"
				+ bl_c_delayBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c delayBasic worse than classic times:\t"
						+ (statisticTimes - bl_c_delayBasicBetterClassicTimes - bl_c_delayBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c delayBasic max acceleration:\t\t"
				+ bl_c_delayBasicMaxAcceleration);
		System.out.println("bl-c delayBasic min acceleration:\t\t"
				+ bl_c_delayBasicMinAcceleration);
		System.out.println("bl-c delayBasic average acceleration:\t\t"
				+ bl_c_delayBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl delayBasic better than classic times:\t"
				+ bl_delayBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl delayBasic equal to classic times:\t\t"
				+ bl_delayBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl delayBasic worse than classic times:\t\t"
						+ (statisticTimes - bl_delayBasicBetterClassicTimes - bl_delayBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl delayBasic max acceleration:\t\t\t"
				+ bl_delayBasicMaxAcceleration);
		System.out.println("bl delayBasic min acceleration:\t\t\t"
				+ bl_delayBasicMinAcceleration);
		System.out.println("bl delayBasic average acceleration:\t\t"
				+ bl_delayBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in delayBasic better than classic times:\t"
				+ bl_in_delayBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in delayBasic equal to classic times:\t"
				+ bl_in_delayBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in delayBasic worse than classic times:\t"
						+ (statisticTimes - bl_in_delayBasicBetterClassicTimes - bl_in_delayBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in delayBasic max acceleration:\t\t"
				+ bl_in_delayBasicMaxAcceleration);
		System.out.println("bl-in delayBasic min acceleration:\t\t"
				+ bl_in_delayBasicMinAcceleration);
		System.out.println("bl-in delayBasic average acceleration:\t\t"
				+ bl_in_delayBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out delayBasic better than classic times:\t"
				+ bl_out_delayBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out delayBasic equal to classic times:\t"
				+ bl_out_delayBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out delayBasic worse than classic times:\t"
						+ (statisticTimes - bl_out_delayBasicBetterClassicTimes - bl_out_delayBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out delayBasic max acceleration:\t\t"
				+ bl_out_delayBasicMaxAcceleration);
		System.out.println("bl-out delayBasic min acceleration:\t\t"
				+ bl_out_delayBasicMinAcceleration);
		System.out.println("bl-out delayBasic average acceleration:\t\t"
				+ bl_out_delayBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout delayBasic better than classic times:\t"
				+ bl_inout_delayBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout delayBasic equal to classic times:\t\t"
				+ bl_inout_delayBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout delayBasic worse than classic times:\t"
						+ (statisticTimes
								- bl_inout_delayBasicBetterClassicTimes - bl_inout_delayBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout delayBasic max acceleration:\t\t"
				+ bl_inout_delayBasicMaxAcceleration);
		System.out.println("bl-inout delayBasic min acceleration:\t\t"
				+ bl_inout_delayBasicMinAcceleration);
		System.out.println("bl-inout delayBasic average acceleration:\t"
				+ bl_inout_delayBasicAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all delayBasic better than classic times:\t"
				+ all_delayBasicBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all delayBasic equal to classic times:\t\t"
				+ all_delayBasicEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all delayBasic worse than classic times:\t"
						+ (statisticTimes - all_delayBasicBetterClassicTimes - all_delayBasicEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all delayBasic max acceleration:\t\t"
				+ all_delayBasicMaxAcceleration);
		System.out.println("all delayBasic min acceleration:\t\t"
				+ all_delayBasicMinAcceleration);
		System.out.println("all delayBasic average acceleration:\t\t"
				+ all_delayBasicAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CommunicationDelayBasic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCommunicationDelayBasic();
	}

}
