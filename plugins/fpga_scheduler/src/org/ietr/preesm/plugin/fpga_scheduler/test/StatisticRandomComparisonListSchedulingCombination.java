package org.ietr.preesm.plugin.fpga_scheduler.test;

public class StatisticRandomComparisonListSchedulingCombination {

	public StatisticRandomComparisonListSchedulingCombination() {
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Combination Begins! *****");
		double ccr = 1;
		int statisticTimes = 100;
		int betterTimes = 0;
		int minScheduleLength = 0;
		int classicTimes = 0;
		double classicScheduleLength = 0;

		int minScheduleLengthClassic = 0;
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

		int minScheduleLengthCritical = 0;
		int bl_c_criticalTimes = 0;
		double bl_c_criticalRelativeScheduleLength = 0;
		int bl_criticalTimes = 0;
		double bl_criticalRelativeScheduleLength = 0;
		int bl_in_criticalTimes = 0;
		double bl_in_criticalRelativeScheduleLength = 0;
		int bl_out_criticalTimes = 0;
		double bl_out_criticalRelativeScheduleLength = 0;
		int bl_inout_criticalTimes = 0;
		double bl_inout_criticalRelativeScheduleLength = 0;

		int bl_c_criticalBetterClassicTimes = 0;
		int bl_c_criticalEqualClassicTimes = 0;
		double bl_c_criticalMaxAcceleration = -1;
		double bl_c_criticalMinAcceleration = 1;
		double bl_c_criticalAverageAcceleration = 0;

		int bl_criticalBetterClassicTimes = 0;
		int bl_criticalEqualClassicTimes = 0;
		double bl_criticalMaxAcceleration = -1;
		double bl_criticalMinAcceleration = 1;
		double bl_criticalAverageAcceleration = 0;

		int bl_in_criticalBetterClassicTimes = 0;
		int bl_in_criticalEqualClassicTimes = 0;
		double bl_in_criticalMaxAcceleration = -1;
		double bl_in_criticalMinAcceleration = 1;
		double bl_in_criticalAverageAcceleration = 0;

		int bl_out_criticalBetterClassicTimes = 0;
		int bl_out_criticalEqualClassicTimes = 0;
		double bl_out_criticalMaxAcceleration = -1;
		double bl_out_criticalMinAcceleration = 1;
		double bl_out_criticalAverageAcceleration = 0;

		int bl_inout_criticalBetterClassicTimes = 0;
		int bl_inout_criticalEqualClassicTimes = 0;
		double bl_inout_criticalMaxAcceleration = -1;
		double bl_inout_criticalMinAcceleration = 1;
		double bl_inout_criticalAverageAcceleration = 0;

		double all_criticalRelativeScheduleLength = 0;
		int all_criticalBetterClassicTimes = 0;
		int all_criticalEqualClassicTimes = 0;
		double all_criticalMaxAcceleration = -1;
		double all_criticalMinAcceleration = 1;
		double all_criticalAverageAcceleration = 0;

		int minScheduleLengthDelay = 0;
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

		int minScheduleLengthAdvanced = 0;
		int bl_c_advancedTimes = 0;
		double bl_c_advancedRelativeScheduleLength = 0;
		int bl_advancedTimes = 0;
		double bl_advancedRelativeScheduleLength = 0;
		int bl_in_advancedTimes = 0;
		double bl_in_advancedRelativeScheduleLength = 0;
		int bl_out_advancedTimes = 0;
		double bl_out_advancedRelativeScheduleLength = 0;
		int bl_inout_advancedTimes = 0;
		double bl_inout_advancedRelativeScheduleLength = 0;

		int bl_c_advancedBetterClassicTimes = 0;
		int bl_c_advancedEqualClassicTimes = 0;
		double bl_c_advancedMaxAcceleration = -1;
		double bl_c_advancedMinAcceleration = 1;
		double bl_c_advancedAverageAcceleration = 0;

		int bl_advancedBetterClassicTimes = 0;
		int bl_advancedEqualClassicTimes = 0;
		double bl_advancedMaxAcceleration = -1;
		double bl_advancedMinAcceleration = 1;
		double bl_advancedAverageAcceleration = 0;

		int bl_in_advancedBetterClassicTimes = 0;
		int bl_in_advancedEqualClassicTimes = 0;
		double bl_in_advancedMaxAcceleration = -1;
		double bl_in_advancedMinAcceleration = 1;
		double bl_in_advancedAverageAcceleration = 0;

		int bl_out_advancedBetterClassicTimes = 0;
		int bl_out_advancedEqualClassicTimes = 0;
		double bl_out_advancedMaxAcceleration = -1;
		double bl_out_advancedMinAcceleration = 1;
		double bl_out_advancedAverageAcceleration = 0;

		int bl_inout_advancedBetterClassicTimes = 0;
		int bl_inout_advancedEqualClassicTimes = 0;
		double bl_inout_advancedMaxAcceleration = -1;
		double bl_inout_advancedMinAcceleration = 1;
		double bl_inout_advancedAverageAcceleration = 0;

		double all_advancedRelativeScheduleLength = 0;
		int all_advancedBetterClassicTimes = 0;
		int all_advancedEqualClassicTimes = 0;
		double all_advancedMaxAcceleration = -1;
		double all_advancedMinAcceleration = 1;
		double all_advancedAverageAcceleration = 0;

		double combinationRelativeScheduleLength = 0;
		int combinationBetterClassicTimes = 0;
		int combinationEqualClassicTimes = 0;
		double combinationMaxAcceleration = 0;
		double combinationMinAcceleration = 0;
		double combinationAverageAcceleration = 0;

		for (int i = 0; i < statisticTimes; i++) {
			RandomComparisonListSchedulingCombination comparison = new RandomComparisonListSchedulingCombination(
					ccr);
			betterTimes += comparison.compare();

			minScheduleLengthClassic = Integer.MAX_VALUE;
			minScheduleLengthCritical = Integer.MAX_VALUE;
			minScheduleLengthDelay = Integer.MAX_VALUE;
			minScheduleLengthAdvanced = Integer.MAX_VALUE;
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

			bl_c_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_criticalRelativeScheduleLength += comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

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

			bl_c_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_in_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_out_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
					.getScheduleLength()
					/ classicScheduleLength;
			bl_inout_advancedRelativeScheduleLength += comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()
					/ classicScheduleLength;

			minScheduleLengthClassic = Math
					.min(
							minScheduleLengthClassic,
							comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLengthClassic = Math
					.min(
							minScheduleLengthClassic,
							comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLengthClassic = Math
					.min(
							minScheduleLengthClassic,
							comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLengthClassic = Math
					.min(
							minScheduleLengthClassic,
							comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLengthClassic = Math
					.min(
							minScheduleLengthClassic,
							comparison
									.getListSchedulingClassicWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			minScheduleLengthCritical = Math
					.min(
							minScheduleLengthCritical,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLengthCritical = Math
					.min(
							minScheduleLengthCritical,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLengthCritical = Math
					.min(
							minScheduleLengthCritical,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLengthCritical = Math
					.min(
							minScheduleLengthCritical,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLengthCritical = Math
					.min(
							minScheduleLengthCritical,
							comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			minScheduleLengthDelay = Math
					.min(
							minScheduleLengthDelay,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLengthDelay = Math
					.min(
							minScheduleLengthDelay,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLengthDelay = Math
					.min(
							minScheduleLengthDelay,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLengthDelay = Math
					.min(
							minScheduleLengthDelay,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLengthDelay = Math
					.min(
							minScheduleLengthDelay,
							comparison
									.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			minScheduleLengthAdvanced = Math
					.min(
							minScheduleLengthAdvanced,
							comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
									.getScheduleLength());
			minScheduleLengthAdvanced = Math
					.min(
							minScheduleLengthAdvanced,
							comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
									.getScheduleLength());
			minScheduleLengthAdvanced = Math
					.min(
							minScheduleLengthAdvanced,
							comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
									.getScheduleLength());
			minScheduleLengthAdvanced = Math
					.min(
							minScheduleLengthAdvanced,
							comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
									.getScheduleLength());
			minScheduleLengthAdvanced = Math
					.min(
							minScheduleLengthAdvanced,
							comparison
									.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
									.getScheduleLength());

			all_classicRelativeScheduleLength += minScheduleLengthClassic
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
			all_classicMaxAcceleration = all_classicMaxAcceleration > 1
					- minScheduleLengthClassic / classicScheduleLength ? all_classicMaxAcceleration
					: 1 - minScheduleLengthClassic / classicScheduleLength;
			all_classicMinAcceleration = all_classicMinAcceleration < 1
					- minScheduleLengthClassic / classicScheduleLength ? all_classicMinAcceleration
					: 1 - minScheduleLengthClassic / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLengthClassic) {
				all_classicBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLengthClassic) {
				all_classicEqualClassicTimes += 1;
			}

			all_criticalRelativeScheduleLength += minScheduleLengthCritical
					/ classicScheduleLength;
			// Compare bl_c to classic
			bl_c_criticalMaxAcceleration = bl_c_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_c_criticalMinAcceleration = bl_c_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalEqualClassicTimes += 1;
			}

			// Compare bl to classic
			bl_criticalMaxAcceleration = bl_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_criticalMinAcceleration = bl_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
							.getScheduleLength() / classicScheduleLength ? bl_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalEqualClassicTimes += 1;
			}

			// Compare bl_in to classic
			bl_in_criticalMaxAcceleration = bl_in_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_in_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_in_criticalMinAcceleration = bl_in_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
							.getScheduleLength() / classicScheduleLength ? bl_c_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalEqualClassicTimes += 1;
			}

			// Compare bl_out to classic
			bl_out_criticalMaxAcceleration = bl_out_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_out_criticalMinAcceleration = bl_out_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
							.getScheduleLength() / classicScheduleLength ? bl_out_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalEqualClassicTimes += 1;
			}

			// Compare bl_inout to classic
			bl_inout_criticalMaxAcceleration = bl_inout_criticalMaxAcceleration > 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_criticalMaxAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			bl_inout_criticalMinAcceleration = bl_inout_criticalMinAcceleration < 1
					- comparison
							.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
							.getScheduleLength() / classicScheduleLength ? bl_inout_criticalMinAcceleration
					: 1
							- comparison
									.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
									.getScheduleLength()
							/ classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalEqualClassicTimes += 1;
			}

			// Compare all to classic
			all_criticalMaxAcceleration = all_criticalMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? all_criticalMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			all_criticalMinAcceleration = all_criticalMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? all_criticalMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				all_criticalBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				all_criticalEqualClassicTimes += 1;
			}

			minScheduleLength = Math.min(minScheduleLength,
					minScheduleLengthClassic);
			minScheduleLength = Math.min(minScheduleLength,
					minScheduleLengthCritical);
			minScheduleLength = Math.min(minScheduleLength,
					minScheduleLengthDelay);
			minScheduleLength = Math.min(minScheduleLength,
					minScheduleLengthAdvanced);
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
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_criticalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_criticalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_criticalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_criticalTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_criticalTimes += 1;
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
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation()
					.getScheduleLength()) {
				bl_c_advancedTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevel()
					.getScheduleLength()) {
				bl_advancedTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelIn()
					.getScheduleLength()) {
				bl_in_advancedTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelOut()
					.getScheduleLength()) {
				bl_out_advancedTimes += 1;
			}
			if (minScheduleLength == comparison
					.getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut()
					.getScheduleLength()) {
				bl_inout_advancedTimes += 1;
			}

			// Compare combination to classic
			combinationMaxAcceleration = combinationMaxAcceleration > 1
					- minScheduleLength / classicScheduleLength ? combinationMaxAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			combinationMinAcceleration = combinationMinAcceleration < 1
					- minScheduleLength / classicScheduleLength ? combinationMinAcceleration
					: 1 - minScheduleLength / classicScheduleLength;
			if (comparison.getListSchedulingClassic().getScheduleLength() > minScheduleLength) {
				combinationBetterClassicTimes += 1;
			} else if (comparison.getListSchedulingClassic()
					.getScheduleLength() == minScheduleLength) {
				combinationEqualClassicTimes += 1;
			}
		}
		bl_c_classicRelativeScheduleLength /= statisticTimes;
		bl_classicRelativeScheduleLength /= statisticTimes;
		bl_in_classicRelativeScheduleLength /= statisticTimes;
		bl_out_classicRelativeScheduleLength /= statisticTimes;
		bl_inout_classicRelativeScheduleLength /= statisticTimes;
		combinationRelativeScheduleLength /= statisticTimes;

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
		combinationAverageAcceleration = 1 - combinationRelativeScheduleLength;

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
		System.out.println("combination better than classic times:\t\t"
				+ combinationBetterClassicTimes + "/" + statisticTimes);
		System.out.println("combination equal to classic times:\t\t"
				+ combinationEqualClassicTimes + "/" + statisticTimes);
		System.out
				.println("combination worse than classic times:\t\t"
						+ (statisticTimes - combinationBetterClassicTimes - combinationEqualClassicTimes)
						+ "/" + statisticTimes);
		System.out.println("combination max acceleration:\t\t\t"
				+ combinationMaxAcceleration);
		System.out.println("combination min acceleration:\t\t\t"
				+ combinationMinAcceleration);
		System.out.println("combination average acceleration:\t\t"
				+ combinationAverageAcceleration);
		System.out.println("********************");
		System.out
				.println("\n***** Statistic Random Comparison List Scheduling Combination Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StatisticRandomComparisonListSchedulingCombination();
	}

}
