package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingAdvancedToClassic {

	public StatisticRandomComparisonListSchedulingAdvancedToClassic() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Advanced To Classic (ordered by bottom-level) Begins! *****");
		double ccr = 10;
		int statisticTimes = 10;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicOptimalTimes = 0;
		double classicScheduleLength = 0;
		int criticalOptimalTimes = 0;
		double relativeCriticalScheduleLength = 0;
		int advancedOptimalTimes = 0;
		double relativeAdvancedScheduleLength = 0;

		int criticalBetterClassicTimes = 0;
		int criticalEqualClassicTimes = 0;
		double maxAccelerationCritical = 0;
		double minAccelerationCritical = 0;
		double averageAccelerationCritical = 0;

		int advancedBetterClassicTimes = 0;
		int advancedEqualClassicTimes = 0;
		double maxAccelerationAdvanced = 0;
		double minAccelerationAdvanced = 0;
		double averageAccelerationAdvanced = 0;

		int advancedBetterCriticalTimes = 0;
		int advancedEqualCriticalTimes = 0;
		int criticalBetterAdvancedTimes = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingAdvancedToClassic comparison = new RandomComparisonListSchedulingAdvancedToClassic(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLength = Integer.MAX_VALUE;

			classicScheduleLength = comparison.getListSchedulingClassic()
					.getScheduleLength();
			relativeCriticalScheduleLength += comparison
					.getListSchedulingCriticalChild().getScheduleLength()
					/ classicScheduleLength;
			relativeAdvancedScheduleLength += comparison
					.getListSchedulingAdvanced().getScheduleLength()
					/ classicScheduleLength;
			minScheduleLength = minScheduleLength > comparison
					.getListSchedulingClassic().getScheduleLength() ? comparison
					.getListSchedulingClassic().getScheduleLength()
					: minScheduleLength;
			minScheduleLength = minScheduleLength > comparison
					.getListSchedulingCriticalChild().getScheduleLength() ? comparison
					.getListSchedulingCriticalChild().getScheduleLength()
					: minScheduleLength;
			minScheduleLength = minScheduleLength > comparison
					.getListSchedulingAdvanced().getScheduleLength() ? comparison
					.getListSchedulingAdvanced().getScheduleLength()
					: minScheduleLength;

			if (minScheduleLength == comparison.getListSchedulingClassic()
					.getScheduleLength()) {
				classicOptimalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChild().getScheduleLength()) {
				criticalOptimalTimes += 1;
			}
			if (minScheduleLength == comparison.getListSchedulingAdvanced()
					.getScheduleLength()) {
				advancedOptimalTimes += 1;
			}

			// Compare critical child to classic
			maxAccelerationCritical = maxAccelerationCritical > 1
					- comparison.getListSchedulingCriticalChild()
							.getScheduleLength() / classicScheduleLength ? maxAccelerationCritical
					: 1
							- comparison.getListSchedulingCriticalChild()
									.getScheduleLength()
							/ classicScheduleLength;
			minAccelerationCritical = minAccelerationCritical < 1
					- comparison.getListSchedulingCriticalChild()
							.getScheduleLength() / classicScheduleLength ? minAccelerationCritical
					: 1
							- comparison.getListSchedulingCriticalChild()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChild().getScheduleLength()) {
				criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChild().getScheduleLength()) {
				criticalEqualClassicTimes += 1;
			}
			// Compare advanced to classic
			maxAccelerationAdvanced = maxAccelerationAdvanced > 1
					- comparison.getListSchedulingAdvanced()
							.getScheduleLength() / classicScheduleLength ? maxAccelerationAdvanced
					: 1
							- comparison.getListSchedulingAdvanced()
									.getScheduleLength()
							/ classicScheduleLength;
			minAccelerationAdvanced = minAccelerationAdvanced < 1
					- comparison.getListSchedulingAdvanced()
							.getScheduleLength() / classicScheduleLength ? minAccelerationAdvanced
					: 1
							- comparison.getListSchedulingAdvanced()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingAdvanced().getScheduleLength()) {
				advancedBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingAdvanced().getScheduleLength()) {
				advancedEqualClassicTimes += 1;
			}
			// Compare critical child to advanced
			if (comparison.getListSchedulingCriticalChild().getScheduleLength() > comparison
					.getListSchedulingAdvanced().getScheduleLength()) {
				advancedBetterCriticalTimes += 1;
			} else if (comparison.getListSchedulingCriticalChild()
					.getScheduleLength() == comparison
					.getListSchedulingAdvanced().getScheduleLength()) {
				advancedEqualCriticalTimes += 1;
			} else {
				criticalBetterAdvancedTimes += 1;
			}

		}
		relativeCriticalScheduleLength /= statisticTimes;
		relativeAdvancedScheduleLength /= statisticTimes;
		System.out.println("communication/computation:\t" + ccr);
		System.out.println("relative classic length:\t" + 1.0);
		System.out.println("relative critical length:\t"
				+ relativeCriticalScheduleLength);
		System.out.println("relative advanced length:\t"
				+ relativeAdvancedScheduleLength);
		System.out.println("classic optimization times:\t"
				+ classicOptimalTimes + "/" + statisticTimes);
		System.out.println("critical optimization times:\t"
				+ criticalOptimalTimes + "/" + statisticTimes);
		System.out.println("advanced optimization times:\t"
				+ advancedOptimalTimes + "/" + statisticTimes);
		System.out.println("total not worse times:\t\t" + betterTimes + "/"
				+ statisticTimes);

		averageAccelerationCritical = 1 - relativeCriticalScheduleLength;
		averageAccelerationAdvanced = 1 - relativeAdvancedScheduleLength;
		System.out.println("********************");
		System.out.println("communication/computation:\t\t" + ccr);

		System.out.println("critical better than classic times:\t"
				+ criticalBetterClassicTimes + "/" + statisticTimes);
		System.out.println("critical equal to classic times:\t"
				+ criticalEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("critical worse than classic times:\t"
						+ (statisticTimes - criticalBetterClassicTimes - criticalEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("max critical acceleration:\t\t"
				+ maxAccelerationCritical);
		System.out.println("min critical acceleration:\t\t"
				+ minAccelerationCritical);
		System.out.println("average critical acceleration:\t\t"
				+ averageAccelerationCritical);
		System.out.println("--------------------");
		System.out.println("advanced better than classic times:\t"
				+ advancedBetterClassicTimes + "/" + statisticTimes);
		System.out.println("advanced equal to classic times:\t"
				+ advancedEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("advanced worse than classic times:\t"
						+ (statisticTimes - advancedBetterClassicTimes - advancedEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("max advanced acceleration:\t\t"
				+ maxAccelerationAdvanced);
		System.out.println("min advanced acceleration:\t\t"
				+ minAccelerationAdvanced);
		System.out.println("average advanced acceleration:\t\t"
				+ averageAccelerationAdvanced);
		System.out.println("--------------------");
		System.out.println("advanced better than critical times:\t"
				+ advancedBetterCriticalTimes + "/" + statisticTimes);
		System.out.println("advanced equal to critical times:\t"
				+ advancedEqualCriticalTimes + "/" + statisticTimes);
		System.out.println("critical better than advanced times:\t"
				+ criticalBetterAdvancedTimes + "/" + statisticTimes);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Advanced to Classic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingAdvancedToClassic();
	}

}
