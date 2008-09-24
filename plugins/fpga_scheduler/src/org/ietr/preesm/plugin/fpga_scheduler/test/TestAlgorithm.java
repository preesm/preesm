package org.ietr.preesm.plugin.fpga_scheduler.test;

import java.util.Vector;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.parser.AlgorithmParser;

public class TestAlgorithm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";

		int argc = 0;
		argc = Integer.parseInt(args[0]);
		System.out.println("Number of argument: " + argc);
		switch (argc) {
		case 1:
			algorithmFileName = args[1];
			break;
		default: {
			System.out.println("Invalid command! Use default!");
		}
		}
		// new TestFpga(dagFileName, parametersFileName, architectureFileName);

		AlgorithmDescriptor algorithm;

		// Parse the algorithm document
		algorithm = new AlgorithmParser(algorithmFileName).parse();

		System.out.println("Computations in the algorithm:");
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			System.out.println(" Name=" + indexComputation.getName()
					+ "; default computationDuration="
					+ indexComputation.getComputationDuration());
		}
		System.out.println("Communications in the algorithm:");
		for (CommunicationDescriptor indexCommunication : algorithm
				.getCommunications().values()) {
			System.out.println(" Name=" + indexCommunication.getName()
					+ "; default communicationDuration="
					+ indexCommunication.getCommunicationDuration());
		}

		System.out
				.println("Add topComputation, bottomComputation and additional in/out communications:");
		ComputationDescriptor topComputation = algorithm.getTopComputation();
		ComputationDescriptor bottomComputation = algorithm
				.getBottomComputation();
		topComputation.setStartTime(0);
		bottomComputation.setStartTime(Integer.MAX_VALUE);
		String communicationName = null;
		int nbCommunicationIn = 0;
		int nbCommunicationOut = 0;
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			if (indexComputation.getPrecedingCommunications().isEmpty()
					&& indexComputation != topComputation
					&& indexComputation != bottomComputation) {
				communicationName = new String("communication_in")
						.concat(Integer.toString(nbCommunicationIn));
				new CommunicationDescriptor(communicationName, topComputation
						.getName(), indexComputation.getName(), 0, algorithm);
				algorithm.addCommunication(algorithm
						.getCommunication(communicationName));
				algorithm.getCommunication(communicationName).clearExist();
				topComputation.addFollowingCommunication(algorithm
						.getCommunication(communicationName));
				indexComputation.addPrecedingCommunication(algorithm
						.getCommunication(communicationName));
				nbCommunicationIn++;
				System.out.println(communicationName + " : "
						+ topComputation.getName() + " -> "
						+ indexComputation.getName());
			}
			if (indexComputation.getFollowingCommunications().isEmpty()
					&& indexComputation != topComputation
					&& indexComputation != bottomComputation) {
				communicationName = (new String("communication_out"))
						.concat(Integer.toString(nbCommunicationOut));
				new CommunicationDescriptor(communicationName, indexComputation
						.getName(), bottomComputation.getName(), 0, algorithm);
				algorithm.getCommunication(communicationName).clearExist();
				algorithm.addCommunication(algorithm
						.getCommunication(communicationName));
				bottomComputation.addPrecedingCommunication(algorithm
						.getCommunication(communicationName));
				indexComputation.addFollowingCommunication(algorithm
						.getCommunication(communicationName));
				nbCommunicationOut++;
				System.out.println(communicationName + " : "
						+ indexComputation.getName() + " -> "
						+ bottomComputation.getName());
			}
		}
		algorithm.computeBottomLevelComputation();
		algorithm.computeTopLevelComputation();
		algorithm.computeBottomLevel();
		algorithm.computeTopLevel();
		algorithm.computeBottomLevelIn();
		algorithm.computeTopLevelIn();
		algorithm.computeBottomLevelOut();
		algorithm.computeTopLevelOut();
		algorithm.computeBottomLevelInOut();
		algorithm.computeTopLevelInOut();
		System.out
				.println("No.\tNode\tWeitht\ttl-c\tbl-c\ttl\tbl\ttl-in\tbl-in\ttl-out\tbl-out\ttl-io\tbl-io");
		Vector<ComputationDescriptor> computationList = algorithm
				.sortComputationsByBottomLevel();
		for (int i = 0; i < computationList.size(); i++) {
			System.out.println(i + 1 + "\t" + computationList.get(i).getName()
					+ "\t" + computationList.get(i).getComputationDuration()
					+ "\t" + computationList.get(i).getTopLevelComputation()
					+ "\t" + computationList.get(i).getBottomLevelComputation()
					+ "\t" + computationList.get(i).getTopLevel() + "\t"
					+ computationList.get(i).getBottomLevel() + "\t"
					+ computationList.get(i).getTopLevelIn() + "\t"
					+ computationList.get(i).getBottomLevelIn() + "\t"
					+ computationList.get(i).getTopLevelOut() + "\t"
					+ computationList.get(i).getBottomLevelOut() + "\t"
					+ computationList.get(i).getTopLevelInOut() + "\t"
					+ computationList.get(i).getBottomLevelInOut());
		}
	}
}
