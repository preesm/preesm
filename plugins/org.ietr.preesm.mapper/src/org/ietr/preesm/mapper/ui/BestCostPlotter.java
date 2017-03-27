/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.mapper.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.ietr.preesm.mapper.algo.fast.FastAlgorithm;
import org.ietr.preesm.mapper.algo.pfast.PFastAlgorithm;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Plots the best cost found versus scheduling time. Can be latency or else
 * 
 * @author pmenuet
 */
public class BestCostPlotter extends ApplicationFrame implements
		ActionListener, Observer {

	private static final long serialVersionUID = -6939533490316310961L;

	/** The number of subplots. */
	private int subplotCount = 1;
	private int actionType = 0;

	/** The datasets. */
	private TimeSeriesCollection[] datasets;

	/** The most recent value added to series 1. */
	private double[] lastValue = new double[subplotCount];

	/** Display panel. */
	private ChartPanel chartPanel;

	/**
	 * Semaphore de pause
	 */
	private Semaphore pauseSemaphore = null;

	/**
	 * Constructs the latency plotter
	 * 
	 * @param title
	 *            the frame title.
	 */
	public BestCostPlotter(final String title, Semaphore pauseSemaphore) {

		super(title);

		JFreeChart chart = createChart(title);
		final JPanel content = new JPanel(new BorderLayout());

		chartPanel = new ChartPanel(chart);
		content.add(chartPanel);

		final JPanel buttonPanel = new JPanel(new FlowLayout());

		final JButton buttonPause = new JButton("Pause");
		buttonPause.setActionCommand("pause");
		buttonPause.addActionListener(this);
		buttonPanel.add(buttonPause);

		final JButton buttonAll = new JButton("Stop");
		buttonAll.setActionCommand("ADD_ALL");
		buttonAll.addActionListener(this);
		buttonPanel.add(buttonAll);

		final JButton buttonLecture = new JButton("Resume");
		buttonLecture.setActionCommand("Resume");
		buttonLecture.addActionListener(this);
		buttonPanel.add(buttonLecture);

		content.add(buttonPanel, BorderLayout.SOUTH);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 470));
		chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setContentPane(content);

		this.pauseSemaphore = pauseSemaphore;
	}

	/**
	 * Creates a chart.
	 * 
	 * @return A chart.
	 */
	private JFreeChart createChart(String title) {

		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(
				new DateAxis("Time"));
		this.datasets = new TimeSeriesCollection[subplotCount];

		for (int i = 0; i < subplotCount; i++) {
			this.lastValue[i] = 100.0;
			final TimeSeries series = new TimeSeries("Real Time",
					Millisecond.class);
			this.datasets[i] = new TimeSeriesCollection(series);
			final NumberAxis rangeAxis = new NumberAxis("Schedule");
			rangeAxis.setAutoRangeIncludesZero(false);
			final XYPlot subplot = new XYPlot(this.datasets[i], null,
					rangeAxis, new XYLineAndShapeRenderer());

			subplot.setBackgroundPaint(Color.white);
			subplot.setDomainGridlinePaint(Color.lightGray);
			subplot.setRangeGridlinePaint(Color.lightGray);
			plot.add(subplot);
		}

		final JFreeChart chart = new JFreeChart(title, plot);

		chart.removeLegend();
		// chart.getLegend().setPosition(RectangleEdge.BOTTOM);

		chart.setBorderPaint(Color.lightGray);
		chart.setBorderVisible(true);

		Paint p = GanttPlotter.getBackgroundColorGradient();
		chart.setBackgroundPaint(p);

		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.black);

		final ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);

		return chart;

	}

	/**
	 * Handles a click on the button and perform the wanted action.
	 * 
	 * @param e
	 *            the action event.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {

		for (int i = 0; i < subplotCount; i++) {
			if (e.getActionCommand().endsWith(String.valueOf(i))) {
				// final Millisecond now = new Millisecond();
				// System.out.println("Now = " + now.toString());
				this.lastValue[i] = this.lastValue[i]
						* (0.90 + 0.2 * Math.random());
				this.datasets[i].getSeries(0).add(new Millisecond(),
						this.lastValue[i]);
			}
		}

		if (e.getActionCommand().equals("pause")) {

			this.setActionType(2);

		}

		if (e.getActionCommand().equals("ADD_ALL")) {
			this.setActionType(1);
		}

		if (e.getActionCommand().equals("Lecture")) {
			this.setActionType(0);
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		// FAST
		if (o instanceof FastAlgorithm) {
			FastAlgorithm fast = (FastAlgorithm) o;
			fast.countObservers();

			if (arg instanceof Long) {

				int i = 0;
				// final Millisecond now = new Millisecond();
				// System.out.println("Now = " + now.toString());
				this.lastValue[i] = ((Long) arg).doubleValue();

				Millisecond milli = new Millisecond();

				this.datasets[i].getSeries(0).addOrUpdate(milli,
						this.lastValue[i]);

			}
		}

		// PFAST
		if (o instanceof PFastAlgorithm) {
			PFastAlgorithm pfast = (PFastAlgorithm) o;
			pfast.countObservers();

			if (arg instanceof Long) {

				int i = 0;
				// final Millisecond now = new Millisecond();
				// System.out.println("Now = " + now.toString());
				this.lastValue[i] = ((Long) arg).doubleValue();
				this.datasets[i].getSeries(0).addOrUpdate(new Millisecond(),
						this.lastValue[i]);

			}
		}

	}

	/**
	 * Getters and setters
	 */

	public int getSubplotCount() {
		return subplotCount;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;

		if (pauseSemaphore != null) {
			if (actionType == 2) {
				pauseSemaphore.tryAcquire();
			} else {
				pauseSemaphore.release();
			}
		}
	}

	public void setSUBPLOT_COUNT(int subplot_count) {
		subplotCount = subplot_count;
	}

	@Override
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

		MouseClickedListener listener = new MouseClickedListener(frame);
		chartPanel.addChartMouseListener(listener);
		chartPanel.addMouseMotionListener(listener);
		chartPanel.addMouseListener(listener);
	}

	public void display() {

		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

}