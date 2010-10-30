/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.plugin.mapper.plot.stats;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Plots the performance of a given implementation and compares it to the
 * maximum possible speed ups
 * 
 * @author mpelcat
 */
public class PerformancePlotter extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class SizeListener implements ControlListener {

		Composite composite;

		Container frame;

		public SizeListener(Composite composite, Container frame) {
			super();
			this.composite = composite;
			this.frame = frame;
		}

		@Override
		public void controlMoved(ControlEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void controlResized(ControlEvent e) {
			// TODO Auto-generated method stub

			frame.setSize(composite.getSize().x, composite.getSize().y);
		}

	}

	/**
	 * The data set containing the speedups
	 */
	private DefaultXYDataset speedups;

	/**
	 * Constructs a new demonstration application.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public PerformancePlotter(final String title) {

		super(title);

		JFreeChart chart = createChart(title);
		final JPanel content = new JPanel(new BorderLayout());

		final ChartPanel chartPanel = new ChartPanel(chart);
		content.add(chartPanel);

		chartPanel.setPreferredSize(new java.awt.Dimension(500, 470));
		chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setContentPane(content);

	}

	/**
	 * Creates a chart in order to plot the speed-ups.
	 * 
	 * @return A chart.
	 */
	private JFreeChart createChart(String title) {

		// Creating display domain
		NumberAxis horizontalAxis = new NumberAxis("Number of operators");
		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(
				horizontalAxis);

		// Creating the best speedups subplot
		this.speedups = new DefaultXYDataset();

		final NumberAxis xAxis = new NumberAxis("speedups");

		xAxis.setAutoRangeIncludesZero(false);
		
		XYSplineRenderer renderer = new XYSplineRenderer();
		final XYPlot subplot = new XYPlot(this.speedups, null, xAxis,
				renderer);
		
		subplot.setBackgroundPaint(Color.white);
		subplot.setDomainGridlinePaint(Color.lightGray);
		subplot.setRangeGridlinePaint(Color.lightGray);
		plot.add(subplot);
		
		plot.setForegroundAlpha(0.5f);

		final JFreeChart chart = new JFreeChart(title, plot);

		chart.setBorderPaint(Color.white);
		chart.setBorderVisible(true);
		chart.setBackgroundPaint(Color.white);

		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);

		return chart;

	}

	/**
	 * Creates the graph values for input data:
	 * 
	 * @param workLength
	 *            sum of all the actor timings
	 * @param spanLength
	 *            length of the longest path in the DAG
	 * @param resultTime
	 *            latency of the current simulation
	 * @param resultNbCores
	 *            number of cores for the current simulation
	 * @param resultNbMainCores
	 *            number of cores with type main for the current simulation
	 * 
	 */
	public void setData(long workLength, long spanLength, long resultTime,
			int resultNbCores, int resultNbMainCores) {

		
		double absoluteBestSpeedup = ((double) workLength)
				/ ((double) spanLength);
		int maxCoreNumber = (int) Math.ceil(absoluteBestSpeedup) + 10;

		// Creating point for current speedup
		double[][] currentSpeedup = new double[2][1];
		currentSpeedup[0][0] = resultNbMainCores;
		currentSpeedup[1][0] = ((double) workLength) / ((double) resultTime);
		this.speedups.addSeries("Currently obtained speedup", currentSpeedup);

		// Creating curve for best speedups
		// The speedup is limited y the span length
		double[][] bestSpeedups = new double[2][maxCoreNumber];

		for (int nbCores = 1; nbCores <= maxCoreNumber; nbCores++) {
			bestSpeedups[0][nbCores - 1] = nbCores;

			if (nbCores < absoluteBestSpeedup) {
				bestSpeedups[1][nbCores - 1] = nbCores;
			} else {
				bestSpeedups[1][nbCores - 1] = absoluteBestSpeedup;
			}
		}

		this.speedups.addSeries("Maximum achievable speedups", bestSpeedups);
		
		// Creating curve for best speedups
		// The speedup is limited y the span length
		double[][] reachableSpeedups = new double[2][maxCoreNumber];

		for (int nbCores = 1; nbCores <= maxCoreNumber; nbCores++) {
			reachableSpeedups[0][nbCores - 1] = nbCores;

			reachableSpeedups[1][nbCores - 1] = ((double) (workLength * nbCores))
					/ ((double) (spanLength * nbCores + workLength));
		}

		this.speedups.addSeries("Greedy-Scheduling Theorem bound",
				reachableSpeedups);
	}

	public void windowClosing(WindowEvent event) {
		if (event.equals(WindowEvent.WINDOW_CLOSING)) {

		}
	}

	public void display(Composite parentComposite) {

		Composite composite = new Composite(parentComposite, SWT.EMBEDDED
				| SWT.FILL);
		parentComposite.setLayout(new FillLayout());
		Frame frame = SWT_AWT.new_Frame(composite);
		frame.add(this.getContentPane());

		parentComposite.addControlListener(this.new SizeListener(composite,
				frame));
	}

	public void display() {

		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

}