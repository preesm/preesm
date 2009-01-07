/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
package org.ietr.preesm.plugin.mapper.listsched;

import org.ietr.preesm.plugin.mapper.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComponentType;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.OperationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.OperatorDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.SwitchDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.parser.AlgorithmParser;
import org.ietr.preesm.plugin.mapper.listsched.parser.ArchitectureParser;
import org.ietr.preesm.plugin.mapper.listsched.parser.ParameterParser;
import org.ietr.preesm.plugin.mapper.listsched.plotter.GanttPlotter;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.AbstractScheduler;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedBl;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedBlcomp;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedBlin;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedBlinout;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedBlout;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcBl;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcBlcomp;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcBlin;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcBlinout;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcBlout;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcCdBl;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcCdBlcomp;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcCdBlin;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcCdBlinout;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCcCdBlout;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCdBl;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCdBlcomp;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCdBlin;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCdBlinout;
import org.ietr.preesm.plugin.mapper.listsched.scheduler.CDListSchedCdBlout;
import org.jfree.ui.RefineryUtilities;
import org.sdf4j.factories.DAGEdgeFactory;

/**
 * A demo of using different static list scheduling methods
 * 
 * @author pmu
 * 
 */
public class DemoDynListSched {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\listsched\\algorithm.xml";
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\listsched\\architecture.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\listsched\\parameter.xml";

		new DemoDynListSched(algorithmFileName, parameterFileName,
				architectureFileName);
	}

	/**
	 * The AlgorithmDescriptor
	 */
	private AlgorithmDescriptor algorithm = null;

	/**
	 * The ArchitectureDescriptor
	 */
	private ArchitectureDescriptor architecture = null;

	/**
	 * Construct the DemoListSched using three files of algorithm, parameters
	 * and architecture
	 * 
	 * @param algorithmFileName
	 *            File name of algorithm
	 * @param parameterFileName
	 *            File name of parameters
	 * @param architectureFileName
	 *            File name of architecture
	 */
	public DemoDynListSched(String algorithmFileName, String parameterFileName,
			String architectureFileName) {
		System.out.println("\n***** DemoListScheduling begins! *****");

		/* classic */
		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedBlcomp scheduler1 = new CDListSchedBlcomp(algorithm,
				architecture);
		scheduler1.schedule();
		testScheduler(scheduler1, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedBl scheduler2 = new CDListSchedBl(algorithm, architecture);
		scheduler2.schedule();
		testScheduler(scheduler2, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedBlin scheduler3 = new CDListSchedBlin(algorithm,
				architecture);
		scheduler3.schedule();
		testScheduler(scheduler3, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedBlout scheduler4 = new CDListSchedBlout(algorithm,
				architecture);
		scheduler4.schedule();
		testScheduler(scheduler4, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedBlinout scheduler5 = new CDListSchedBlinout(algorithm,
				architecture);
		scheduler5.schedule();
		testScheduler(scheduler5, algorithm, architecture);

		/* critical child */
		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcBlcomp schedulerCc1 = new CDListSchedCcBlcomp(algorithm,
				architecture);
		schedulerCc1.schedule();
		testScheduler(schedulerCc1, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcBl schedulerCc2 = new CDListSchedCcBl(algorithm,
				architecture);
		schedulerCc2.schedule();
		testScheduler(schedulerCc2, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcBlin schedulerCc3 = new CDListSchedCcBlin(algorithm,
				architecture);
		schedulerCc3.schedule();
		testScheduler(schedulerCc3, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcBlout schedulerCc4 = new CDListSchedCcBlout(algorithm,
				architecture);
		schedulerCc4.schedule();
		testScheduler(schedulerCc4, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcBlinout schedulerCc5 = new CDListSchedCcBlinout(algorithm,
				architecture);
		schedulerCc5.schedule();
		testScheduler(schedulerCc5, algorithm, architecture);

		/* communication delay */
		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCdBlcomp schedulerCd1 = new CDListSchedCdBlcomp(algorithm,
				architecture);
		schedulerCd1.schedule();
		testScheduler(schedulerCd1, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCdBl schedulerCd2 = new CDListSchedCdBl(algorithm,
				architecture);
		schedulerCd2.schedule();
		testScheduler(schedulerCd2, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCdBlin schedulerCd3 = new CDListSchedCdBlin(algorithm,
				architecture);
		schedulerCd3.schedule();
		testScheduler(schedulerCd3, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCdBlout schedulerCd4 = new CDListSchedCdBlout(algorithm,
				architecture);
		schedulerCd4.schedule();
		testScheduler(schedulerCd4, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCdBlinout schedulerCd5 = new CDListSchedCdBlinout(algorithm,
				architecture);
		schedulerCd5.schedule();
		testScheduler(schedulerCd5, algorithm, architecture);

		/* critical child and communication delay */
		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcCdBlcomp schedulerCcCd1 = new CDListSchedCcCdBlcomp(
				algorithm, architecture);
		schedulerCcCd1.schedule();
		testScheduler(schedulerCcCd1, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcCdBl schedulerCcCd2 = new CDListSchedCcCdBl(algorithm,
				architecture);
		schedulerCcCd2.schedule();
		testScheduler(schedulerCcCd2, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcCdBlin schedulerCcCd3 = new CDListSchedCcCdBlin(algorithm,
				architecture);
		schedulerCcCd3.schedule();
		testScheduler(schedulerCcCd3, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcCdBlout schedulerCcCd4 = new CDListSchedCcCdBlout(
				algorithm, architecture);
		schedulerCcCd4.schedule();
		testScheduler(schedulerCcCd4, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		CDListSchedCcCdBlinout schedulerCcCd5 = new CDListSchedCcCdBlinout(
				algorithm, architecture);
		schedulerCcCd5.schedule();
		testScheduler(schedulerCcCd5, algorithm, architecture);

		System.out.println("***Compared Results***");

		System.out
				.print("No.\tScheduling Method\t\t\t\t\t\t\t\t\t\t\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

		System.out.print("\n1\t" + scheduler1.getName() + "\t\t\t\t\t\t"
				+ scheduler1.getScheduleLength() + "\t\t\t"
				+ scheduler1.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler1
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot1 = new GanttPlotter(scheduler1.getName()
				+ " -> Schedule Length=" + scheduler1.getScheduleLength(),
				scheduler1);
		plot1.pack();
		RefineryUtilities.centerFrameOnScreen(plot1);
		plot1.setVisible(true);

		System.out.print("\n2\t" + scheduler2.getName() + "\t\t\t\t\t\t\t"
				+ scheduler2.getScheduleLength() + "\t\t\t"
				+ scheduler2.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler2
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		GanttPlotter plot2 = new GanttPlotter(scheduler2.getName()
				+ " -> Schedule Length=" + scheduler2.getScheduleLength(),
				scheduler2);
		plot2.pack();
		RefineryUtilities.centerFrameOnScreen(plot2);
		plot2.setVisible(true);

		System.out.print("\n3\t" + scheduler3.getName() + "\t\t\t\t\t\t\t"
				+ scheduler3.getScheduleLength() + "\t\t\t"
				+ scheduler3.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler3
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot3 = new GanttPlotter(scheduler3.getName()
				+ " -> Schedule Length=" + scheduler3.getScheduleLength(),
				scheduler3);
		plot3.pack();
		RefineryUtilities.centerFrameOnScreen(plot3);
		plot3.setVisible(true);

		System.out.print("\n4\t" + scheduler4.getName() + "\t\t\t\t\t\t"
				+ scheduler4.getScheduleLength() + "\t\t\t"
				+ scheduler4.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler4
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot4 = new GanttPlotter(scheduler4.getName()
				+ " -> Schedule Length=" + scheduler4.getScheduleLength(),
				scheduler4);
		plot4.pack();
		RefineryUtilities.centerFrameOnScreen(plot4);
		plot4.setVisible(true);

		System.out.print("\n5\t" + scheduler5.getName() + "\t\t\t\t\t\t"
				+ scheduler5.getScheduleLength() + "\t\t\t"
				+ scheduler5.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler5
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot5 = new GanttPlotter(scheduler5.getName()
				+ " -> Schedule Length=" + scheduler5.getScheduleLength(),
				scheduler5);
		plot5.pack();
		RefineryUtilities.centerFrameOnScreen(plot5);
		plot5.setVisible(true);

		System.out.print("\nCc:1\t" + schedulerCc1.getName() + "\t\t\t"
				+ schedulerCc1.getScheduleLength() + "\t\t\t"
				+ schedulerCc1.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCc1
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCc1 = new GanttPlotter(schedulerCc1.getName()
				+ " -> Schedule Length=" + schedulerCc1.getScheduleLength(),
				schedulerCc1);
		plotCc1.pack();
		RefineryUtilities.centerFrameOnScreen(plotCc1);
		plotCc1.setVisible(true);

		System.out.print("\nCc:2\t" + schedulerCc2.getName() + "\t\t\t\t\t"
				+ schedulerCc2.getScheduleLength() + "\t\t\t"
				+ schedulerCc2.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCc2
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		GanttPlotter plotCc2 = new GanttPlotter(schedulerCc2.getName()
				+ " -> Schedule Length=" + schedulerCc2.getScheduleLength(),
				schedulerCc2);
		plotCc2.pack();
		RefineryUtilities.centerFrameOnScreen(plotCc2);
		plotCc2.setVisible(true);

		System.out.print("\nCc:3\t" + schedulerCc3.getName() + "\t\t\t\t"
				+ schedulerCc3.getScheduleLength() + "\t\t\t"
				+ schedulerCc3.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCc3
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCc3 = new GanttPlotter(schedulerCc3.getName()
				+ " -> Schedule Length=" + schedulerCc3.getScheduleLength(),
				schedulerCc3);
		plotCc3.pack();
		RefineryUtilities.centerFrameOnScreen(plotCc3);
		plotCc3.setVisible(true);

		System.out.print("\nCc:4\t" + schedulerCc4.getName() + "\t\t\t\t"
				+ schedulerCc4.getScheduleLength() + "\t\t\t"
				+ schedulerCc4.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCc4
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCc4 = new GanttPlotter(schedulerCc4.getName()
				+ " -> Schedule Length=" + schedulerCc4.getScheduleLength(),
				schedulerCc4);
		plotCc4.pack();
		RefineryUtilities.centerFrameOnScreen(plotCc4);
		plotCc4.setVisible(true);

		System.out.print("\nCc:5\t" + schedulerCc5.getName() + "\t\t\t"
				+ schedulerCc5.getScheduleLength() + "\t\t\t"
				+ schedulerCc5.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCc5
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCc5 = new GanttPlotter(schedulerCc5.getName()
				+ " -> Schedule Length=" + schedulerCc5.getScheduleLength(),
				schedulerCc5);
		plotCc5.pack();
		RefineryUtilities.centerFrameOnScreen(plotCc5);
		plotCc5.setVisible(true);

		System.out.print("\nCd:1\t" + schedulerCd1.getName() + "\t\t\t"
				+ schedulerCd1.getScheduleLength() + "\t\t\t"
				+ schedulerCd1.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCd1
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCd1 = new GanttPlotter(schedulerCd1.getName()
				+ " -> Schedule Length=" + schedulerCd1.getScheduleLength(),
				schedulerCd1);
		plotCd1.pack();
		RefineryUtilities.centerFrameOnScreen(plotCd1);
		plotCd1.setVisible(true);

		System.out.print("\nCd:2\t" + schedulerCd2.getName() + "\t\t\t\t"
				+ schedulerCd2.getScheduleLength() + "\t\t\t"
				+ schedulerCd2.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCd2
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		GanttPlotter plotCd2 = new GanttPlotter(schedulerCd2.getName()
				+ " -> Schedule Length=" + schedulerCd2.getScheduleLength(),
				schedulerCd2);
		plotCd2.pack();
		RefineryUtilities.centerFrameOnScreen(plotCd2);
		plotCd2.setVisible(true);

		System.out.print("\nCd:3\t" + schedulerCd3.getName() + "\t\t\t\t"
				+ schedulerCd3.getScheduleLength() + "\t\t\t"
				+ schedulerCd3.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCd3
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCd3 = new GanttPlotter(schedulerCd3.getName()
				+ " -> Schedule Length=" + schedulerCd3.getScheduleLength(),
				schedulerCd3);
		plotCd3.pack();
		RefineryUtilities.centerFrameOnScreen(plotCd3);
		plotCd3.setVisible(true);

		System.out.print("\nCd:4\t" + schedulerCd4.getName() + "\t\t\t"
				+ schedulerCd4.getScheduleLength() + "\t\t\t"
				+ schedulerCd4.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCd4
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCd4 = new GanttPlotter(schedulerCd4.getName()
				+ " -> Schedule Length=" + schedulerCd4.getScheduleLength(),
				schedulerCd4);
		plotCd4.pack();
		RefineryUtilities.centerFrameOnScreen(plotCd4);
		plotCd4.setVisible(true);

		System.out.print("\nCd:5\t" + schedulerCd5.getName() + "\t\t\t"
				+ schedulerCd5.getScheduleLength() + "\t\t\t"
				+ schedulerCd5.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCd5
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCd5 = new GanttPlotter(schedulerCd5.getName()
				+ " -> Schedule Length=" + schedulerCd5.getScheduleLength(),
				schedulerCd5);
		plotCd5.pack();
		RefineryUtilities.centerFrameOnScreen(plotCd5);
		plotCd5.setVisible(true);

		System.out.print("\nCcCd:1\t" + schedulerCcCd1.getName() + "\t"
				+ schedulerCcCd1.getScheduleLength() + "\t\t\t"
				+ schedulerCcCd1.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCcCd1
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCcCd1 = new GanttPlotter(schedulerCcCd1.getName()
				+ " -> Schedule Length=" + schedulerCcCd1.getScheduleLength(),
				schedulerCcCd1);
		plotCcCd1.pack();
		RefineryUtilities.centerFrameOnScreen(plotCcCd1);
		plotCcCd1.setVisible(true);

		System.out.print("\nCcCd:2\t" + schedulerCcCd2.getName() + "\t\t"
				+ schedulerCcCd2.getScheduleLength() + "\t\t\t"
				+ schedulerCcCd2.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCcCd2
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		GanttPlotter plotCcCd2 = new GanttPlotter(schedulerCcCd2.getName()
				+ " -> Schedule Length=" + schedulerCcCd2.getScheduleLength(),
				schedulerCcCd2);
		plotCcCd2.pack();
		RefineryUtilities.centerFrameOnScreen(plotCcCd2);
		plotCcCd2.setVisible(true);

		System.out.print("\nCcCd:3\t" + schedulerCcCd3.getName() + "\t\t"
				+ schedulerCcCd3.getScheduleLength() + "\t\t\t"
				+ schedulerCcCd3.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCcCd3
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCcCd3 = new GanttPlotter(schedulerCcCd3.getName()
				+ " -> Schedule Length=" + schedulerCcCd3.getScheduleLength(),
				schedulerCcCd3);
		plotCcCd3.pack();
		RefineryUtilities.centerFrameOnScreen(plotCcCd3);
		plotCcCd3.setVisible(true);

		System.out.print("\nCcCd:4\t" + schedulerCcCd4.getName() + "\t"
				+ schedulerCcCd4.getScheduleLength() + "\t\t\t"
				+ schedulerCcCd4.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCcCd4
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCcCd4 = new GanttPlotter(schedulerCcCd4.getName()
				+ " -> Schedule Length=" + schedulerCcCd4.getScheduleLength(),
				schedulerCcCd4);
		plotCcCd4.pack();
		RefineryUtilities.centerFrameOnScreen(plotCcCd4);
		plotCcCd4.setVisible(true);

		System.out.print("\nCcCd:5\t" + schedulerCcCd5.getName() + "\t"
				+ schedulerCcCd5.getScheduleLength() + "\t\t\t"
				+ schedulerCcCd5.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerCcCd5
				.getDynamicOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plotCcCd5 = new GanttPlotter(schedulerCcCd5.getName()
				+ " -> Schedule Length=" + schedulerCcCd5.getScheduleLength(),
				schedulerCcCd5);
		plotCcCd5.pack();
		RefineryUtilities.centerFrameOnScreen(plotCcCd5);
		plotCcCd5.setVisible(true);

		System.out.println("\n\n*****DemoListScheduling finishes!*****");
	}

	/**
	 * Parse algorithm, architecture and parameters
	 * 
	 * @param algorithmFileName
	 *            File name of algorithm
	 * @param architectureFileName
	 *            File name of architecture
	 * @param parameterFileName
	 *            File name of parameters
	 */
	private void parse(String algorithmFileName, String architectureFileName,
			String parameterFileName) {
		algorithm = new AlgorithmDescriptor(new DAGEdgeFactory());
		architecture = new ArchitectureDescriptor();
		// Parse the design algorithm document
		new AlgorithmParser(algorithmFileName, algorithm).parse();
		// Parse the design parameter document
		new ParameterParser(parameterFileName, architecture, algorithm).parse();
		// Parse the architecture document
		new ArchitectureParser(architectureFileName, architecture).parse();

		OperatorDescriptor defaultOperator = null;
		SwitchDescriptor defaultNetwork = null;
		for (ComponentDescriptor indexComponent : architecture.getComponents()
				.values()) {
			if ((indexComponent.getType() == ComponentType.Ip || indexComponent
					.getType() == ComponentType.Processor)
					&& indexComponent.getId().equalsIgnoreCase(
							indexComponent.getName())) {
				defaultOperator = (OperatorDescriptor) indexComponent;
			} else if (indexComponent.getType() == ComponentType.Switch
					&& indexComponent.getId().equalsIgnoreCase(
							indexComponent.getName())) {
				defaultNetwork = (SwitchDescriptor) indexComponent;
			}
		}

		System.out.println(" default operator: Id=" + defaultOperator.getId()
				+ "; Name=" + defaultOperator.getName());
		System.out.println(" default network: Id=" + defaultNetwork.getId()
				+ "; Name=" + defaultNetwork.getName());
		System.out.println("Computations in the algorithm:");
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			// Allow a computation to be executed on each operator
			for (OperatorDescriptor indexOperator : architecture
					.getAllOperators().values()) {
				indexComputation.addOperator(indexOperator);
			}
			if (!indexComputation.getComputationDurations().containsKey(
					defaultOperator)) {
				indexComputation.addComputationDuration(defaultOperator,
						indexComputation.getTime());
				System.out
						.println(" Name="
								+ indexComputation.getName()
								+ "; default computationDuration="
								+ indexComputation
										.getComputationDuration(defaultOperator
												.getId()) + "; nbTotalRepeate="
								+ indexComputation.getNbTotalRepeat());
			}
		}
		System.out.println("Communications in the algorithm:");
		for (CommunicationDescriptor indexCommunication : algorithm
				.getCommunications().values()) {
			if (!indexCommunication.getCommunicationDurations().containsKey(
					defaultNetwork)) {
				indexCommunication.addCommunicationDuration(defaultNetwork,
						indexCommunication.getWeight());
				System.out.println(" Name="
						+ indexCommunication.getName()
						+ "; default communicationDuration="
						+ indexCommunication
								.getCommunicationDuration(defaultNetwork));
			}
		}
		System.out.println("Operators in the architecture:");
		for (OperatorDescriptor indexOperator : architecture.getAllOperators()
				.values()) {
			System.out.println(" Id=" + indexOperator.getId() + "; Name="
					+ indexOperator.getName());
		}
	}

	/**
	 * Test the performance of a scheduler
	 * 
	 * @param scheduler
	 *            The scheduler
	 * @param algorithm
	 *            The algorithm
	 * @param architecture
	 *            The architecture
	 */
	private void testScheduler(AbstractScheduler scheduler,
			AlgorithmDescriptor algorithm, ArchitectureDescriptor architecture) {

		System.out.println("\nSchedule method: " + scheduler.getName());
		System.out.println("\n***** Schedule results *****");
		for (OperatorDescriptor indexOperator : architecture.getAllOperators()
				.values()) {
			System.out.println("\n Operator: Id=" + indexOperator.getId()
					+ "; Name=" + indexOperator.getName());
			for (OperationDescriptor indexOperation : indexOperator
					.getOperations()) {
				if (indexOperation != scheduler.getTopCommunication()
						&& indexOperation != scheduler.getBottomCommunication()) {
					if (indexOperator.getComputations()
							.contains(indexOperation)) {
						System.out.println("  computation: Name="
								+ indexOperation.getName()
								+ "\n   1> startTime="
								+ indexOperator.getOccupiedTimeInterval(
										indexOperation.getName())
										.getStartTime()
								+ "\n   2> finishTime="
								+ indexOperator.getOccupiedTimeInterval(
										indexOperation.getName())
										.getFinishTime());
					} else {
						if (indexOperator.getSendCommunications().contains(
								indexOperation)) {
							System.out
									.println("  sendCommunication: Name="
											+ indexOperation.getName()
											+ "\n   1> startTimeOnSendOperator="
											+ indexOperator
													.getOccupiedTimeInterval(
															indexOperation
																	.getName())
													.getStartTime()
											+ "\n   2> finishTimeOnSendOperator="
											+ indexOperator
													.getOccupiedTimeInterval(
															indexOperation
																	.getName())
													.getFinishTime()
											+ "\n   3> startTimeOnLink="
											+ ((CommunicationDescriptor) indexOperation)
													.getStartTimeOnLink()
											+ "\n   4> finishTimeOnLink="
											+ ((CommunicationDescriptor) indexOperation)
													.getFinishTimeOnLink());
						} else {
							System.out
									.println("  receiveCommunication: Name="
											+ indexOperation.getName()
											+ "\n   1> startTimeOnReceiveOperator="
											+ indexOperator
													.getOccupiedTimeInterval(
															indexOperation
																	.getName())
													.getStartTime()
											+ "\n   2> finishTimeOnReceiveOperator="
											+ indexOperator
													.getOccupiedTimeInterval(
															indexOperation
																	.getName())
													.getFinishTime()
											+ "\n   3> startTimeOnLink="
											+ ((CommunicationDescriptor) indexOperation)
													.getStartTimeOnLink()
											+ "\n   4> finishTimeOnLink="
											+ ((CommunicationDescriptor) indexOperation)
													.getFinishTimeOnLink());
						}
					}
				}
			}
			for (LinkDescriptor indexLink : indexOperator.getOutputLinks()) {
				System.out.println(" outputLink: Id=" + indexLink.getId()
						+ "; Name=" + indexLink.getName());
				for (CommunicationDescriptor indexCommunication : indexLink
						.getCommunications()) {
					if (indexCommunication.getSendLink() == indexLink) {
						System.out.println("  sendCommunication: Name="
								+ indexCommunication.getName()
								+ "\n   1> startTimeOnLink="
								+ indexCommunication.getStartTimeOnLink()
								+ "\n   2> finishTimeOnLink="
								+ indexCommunication.getFinishTimeOnLink());
					}
				}
			}
			for (LinkDescriptor indexLink : indexOperator.getInputLinks()) {
				System.out.println(" inputLink: Id=" + indexLink.getId()
						+ "; Name=" + indexLink.getName());
				for (CommunicationDescriptor indexCommunication : indexLink
						.getCommunications()) {
					if (indexCommunication.getReceiveLink() == indexLink) {
						System.out.println("  receiveCommunication: Name="
								+ indexCommunication.getName()
								+ "\n   1> startTimeOnLink="
								+ indexCommunication.getStartTimeOnLink()
								+ "\n   2> finishTimeOnLink="
								+ indexCommunication.getFinishTimeOnLink());
					}
				}
			}
		}
		System.out.println("\n***** Schedule Length="
				+ scheduler.getScheduleLength() + " *****\n");
	}
}
