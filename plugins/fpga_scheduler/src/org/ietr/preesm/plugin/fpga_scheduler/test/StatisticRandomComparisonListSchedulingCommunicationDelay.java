package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCommunicationDelay {

	public StatisticRandomComparisonListSchedulingCommunicationDelay() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CommunicationDelay Begins! *****");
		double ccr = 10;
		int statisticTimes = 100;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int bl_c_delayTimes = 0;
		double bl_c_delayRelativeScheduleLength = 0;
		int bl_delayTimes = 0;
		double bl_delayRelativeScheduleLength = 0;
		int bl_in_delayTimes = 0;
		double bl_in_delayRelativeScheduleLength = 0;
		int bl_out_delayTimes = 0;
		double bl_out_delayRelativeScheduleLength = 0;
		int bl_inout_delayTimes = 0;
		double bl_inout_delayRelativeScheduleLength = 0;

		int bl_c_delayBetterClassicTimes = 0;
		int bl_c_delayEqualClassicTimes = 0;
		double bl_c_delayMaxAcceleration = -1;
		double bl_c_delayMinAcceleration = 1;
		double bl_c_delayAverageAcceleration = 0;

		int bl_delayBetterClassicTimes = 0;
		int bl_delayEqualClassicTimes = 0;
		double bl_delayMaxAcceleration = -1;
		double bl_delayMinAcceleration = 1;
		double bl_delayAverageAcceleration = 0;

		int bl_in_delayBetterClassicTimes = 0;
		int bl_in_delayEqualClassicTimes = 0;
		double bl_in_delayMaxAcceleration = -1;
		double bl_in_delayMinAcceleration = 1;
		double bl_in_delayAverageAcceleration = 0;

		int bl_out_delayBetterClassicTimes = 0;
		int bl_out_delayEqualClassicTimes = 0;
		double bl_out_delayMaxAcceleration = -1;
		double bl_out_delayMinAcceleration = 1;
		double bl_out_delayAverageAcceleration = 0;

		int bl_inout_delayBetterClassicTimes = 0;
		int bl_inout_delayEqualClassicTimes = 0;
		double bl_inout_delayMaxAcceleration = -1;
		double bl_inout_delayMinAcceleration = 1;
		double bl_inout_delayAverageAcceleration = 0;

		double all_delayRelativeScheduleLength = 0;
		int all_delayBetterClassicTimes = 0;
		int all_delayEqualClassicTimes = 0;
		double all_delayMaxAcceleration = -1;
		double all_delayMinAcceleration = 1;
		double all_delayAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCommunicationDelay comparison = new RandomComparisonListSchedulingCommunicationDelay(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			bl_c_delayRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_delayRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_delayRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_delayRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_delayRelativeScheduleLength += comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLength = Math
					.min(
							minScheduleLength,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			if (minScheduleLength >= comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayTimes += 1;
			}

			all_delayRelativeScheduleLength += minScheduleLength
					/ classicScheduleLength;

			// Compare bl_c to classic
			bl_c_delayMaxAcceleration = bl_c_delayMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_delayMinAcceleration = bl_c_delayMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_delayEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_delayMaxAcceleration = bl_delayMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_delayMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_delayMinAcceleration = bl_delayMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_delayMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_delayEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_delayMaxAcceleration = bl_in_delayMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_delayMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_delayMinAcceleration = bl_in_delayMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_delayMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_delayEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_delayMaxAcceleration = bl_out_delayMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_delayMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_delayMinAcceleration = bl_out_delayMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_delayMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_delayEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_delayMaxAcceleration = bl_inout_delayMaxAcceleration > 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_delayMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_delayMinAcceleration = bl_inout_delayMinAcceleration < 1
					- comparison
							.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_delayMinAcceleration
					: 1
							- comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_delayEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_delayMaxAcceleration = all_delayMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_delayMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_delayMinAcceleration = all_delayMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_delayMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_delayBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_delayEqualClassicTimes += 1;
			}
		}
		bl_c_delayRelativeScheduleLength /= statisticTimes;
		bl_delayRelativeScheduleLength /= statisticTimes;
		bl_in_delayRelativeScheduleLength /= statisticTimes;
		bl_out_delayRelativeScheduleLength /= statisticTimes;
		bl_inout_delayRelativeScheduleLength /= statisticTimes;
		all_delayRelativeScheduleLength /= statisticTimes;

		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative bl-c length:\t\t"
				+ bl_c_delayRelativeScheduleLength);
		System.out.println("relative bl length:\t\t"
				+ bl_delayRelativeScheduleLength);
		System.out.println("relative bl-in length:\t\t"
				+ bl_in_delayRelativeScheduleLength);
		System.out.println("relative bl-out length:\t\t"
				+ bl_out_delayRelativeScheduleLength);
		System.out.println("relative bl-inout length:\t"
				+ bl_inout_delayRelativeScheduleLength);

		System.out.println("classic optimization times:\t" + classicTimes + "/"
				+ statisticTimes);
		System.out.println("bl-c optimization times:\t" + bl_c_delayTimes + "/"
				+ statisticTimes);
		System.out.println("bl optimization times:\t\t" + bl_delayTimes + "/"
				+ statisticTimes);
		System.out.println("bl-in optimization times:\t" + bl_in_delayTimes
				+ "/" + statisticTimes);
		System.out.println("bl-out optimization times:\t" + bl_out_delayTimes
				+ "/" + statisticTimes);
		System.out.println("bl-inout optimization times:\t"
				+ bl_inout_delayTimes + "/" + statisticTimes);

		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		bl_c_delayAverageAcceleration = 1 - bl_c_delayRelativeScheduleLength;
		bl_delayAverageAcceleration = 1 - bl_delayRelativeScheduleLength;
		bl_in_delayAverageAcceleration = 1 - bl_in_delayRelativeScheduleLength;
		bl_out_delayAverageAcceleration = 1 - bl_out_delayRelativeScheduleLength;
		bl_inout_delayAverageAcceleration = 1 - bl_inout_delayRelativeScheduleLength;
		all_delayAverageAcceleration = 1 - all_delayRelativeScheduleLength;

		System.out.println("********************");
		System.out.println("communication/computation:\t\t\t" + ccr);
		System.out.println("--------------------");
		System.out.println("bl-c delay better than classic times:\t\t"
				+ bl_c_delayBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-c delay equal to classic times:\t\t"
				+ bl_c_delayEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-c delay worse than classic times:\t\t"
						+ (statisticTimes - bl_c_delayBetterClassicTimes - bl_c_delayEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-c delay max acceleration:\t\t\t"
				+ bl_c_delayMaxAcceleration);
		System.out.println("bl-c delay min acceleration:\t\t\t"
				+ bl_c_delayMinAcceleration);
		System.out.println("bl-c delay average acceleration:\t\t"
				+ bl_c_delayAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl delay better than classic times:\t\t"
				+ bl_delayBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl delay equal to classic times:\t\t"
				+ bl_delayEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl delay worse than classic times:\t\t"
						+ (statisticTimes - bl_delayBetterClassicTimes - bl_delayEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl delay max acceleration:\t\t\t"
				+ bl_delayMaxAcceleration);
		System.out.println("bl delay min acceleration:\t\t\t"
				+ bl_delayMinAcceleration);
		System.out.println("bl delay average acceleration:\t\t\t"
				+ bl_delayAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-in delay better than classic times:\t\t"
				+ bl_in_delayBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-in delay equal to classic times:\t\t"
				+ bl_in_delayEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-in delay worse than classic times:\t\t"
						+ (statisticTimes - bl_in_delayBetterClassicTimes - bl_in_delayEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-in delay max acceleration:\t\t\t"
				+ bl_in_delayMaxAcceleration);
		System.out.println("bl-in delay min acceleration:\t\t\t"
				+ bl_in_delayMinAcceleration);
		System.out.println("bl-in delay average acceleration:\t\t"
				+ bl_in_delayAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-out delay better than classic times:\t\t"
				+ bl_out_delayBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-out delay equal to classic times:\t\t"
				+ bl_out_delayEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-out delay worse than classic times:\t\t"
						+ (statisticTimes - bl_out_delayBetterClassicTimes - bl_out_delayEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-out delay max acceleration:\t\t\t"
				+ bl_out_delayMaxAcceleration);
		System.out.println("bl-out delay min acceleration:\t\t\t"
				+ bl_out_delayMinAcceleration);
		System.out.println("bl-out delay average acceleration:\t\t"
				+ bl_out_delayAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("bl-inout delay better than classic times:\t"
				+ bl_inout_delayBetterClassicTimes + "/" + statisticTimes);
		System.out.println("bl-inout delay equal to classic times:\t\t"
				+ bl_inout_delayEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("bl-inout delay worse than classic times:\t"
						+ (statisticTimes - bl_inout_delayBetterClassicTimes - bl_inout_delayEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("bl-inout delay max acceleration:\t\t"
				+ bl_inout_delayMaxAcceleration);
		System.out.println("bl-inout delay min acceleration:\t\t"
				+ bl_inout_delayMinAcceleration);
		System.out.println("bl-inout delay average acceleration:\t\t"
				+ bl_inout_delayAverageAcceleration);
		System.out.println("--------------------");
		System.out.println("all delay better than classic times:\t\t"
				+ all_delayBetterClassicTimes + "/" + statisticTimes);
		System.out.println("all delay equal to classic times:\t\t"
				+ all_delayEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("all delay worse than classic times:\t\t"
						+ (statisticTimes - all_delayBetterClassicTimes - all_delayEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("all delay max acceleration:\t\t\t"
				+ all_delayMaxAcceleration);
		System.out.println("all delay min acceleration:\t\t\t"
				+ all_delayMinAcceleration);
		System.out.println("all delay average acceleration:\t\t\t"
				+ all_delayAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling CommunicationDelay Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCommunicationDelay();
	}

}
