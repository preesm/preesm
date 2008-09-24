package org.ietr.preesm.plugin.mapper.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.ietr.preesm.plugin.mapper.fastalgo.FastAlgorithm;
import org.ietr.preesm.plugin.mapper.geneticalgo.StandardGeneticAlgorithm;
import org.ietr.preesm.plugin.mapper.pfastalgo.PFastAlgorithm;
import org.ietr.preesm.plugin.mapper.pgeneticalgo.PGeneticAlgo;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * Plots the best latency found versus scheduling time
 * 
 * @author pmenuet
 */
public class PlotBestLatency extends ApplicationFrame implements
		ActionListener, Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6939533490316310961L;

	/** The number of subplots. */
	private int SUBPLOT_COUNT = 1;
	private int actionType = 0;

	/** The datasets. */
	private TimeSeriesCollection[] datasets;

	/** The most recent value added to series 1. */
	private double[] lastValue = new double[SUBPLOT_COUNT];

	/**
	 * Constructs a new demonstration application.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public PlotBestLatency(final String title) {

		super(title);

		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(
				new DateAxis("Time"));
		this.datasets = new TimeSeriesCollection[SUBPLOT_COUNT];

		for (int i = 0; i < SUBPLOT_COUNT; i++) {
			this.lastValue[i] = 100.0;
			final TimeSeries series = new TimeSeries("Real Time",
					Millisecond.class);
			this.datasets[i] = new TimeSeriesCollection(series);
			final NumberAxis rangeAxis = new NumberAxis("Schedule");
			rangeAxis.setAutoRangeIncludesZero(false);
			final XYPlot subplot = new XYPlot(this.datasets[i], null,
					rangeAxis, new StandardXYItemRenderer());
			subplot.setBackgroundPaint(Color.white);
			subplot.setDomainGridlinePaint(Color.lightGray);
			subplot.setRangeGridlinePaint(Color.lightGray);
			plot.add(subplot);
		}

		final JFreeChart chart = new JFreeChart(title, plot);
		// chart.getLegend().setAnchor(Legend.EAST);
		chart.setBorderPaint(Color.black);
		chart.setBorderVisible(true);
		chart.setBackgroundPaint(Color.white);

		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 4, 4, 4, 4));
		final ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		// axis.setFixedAutoRange(60000.0); // 60 seconds

		final JPanel content = new JPanel(new BorderLayout());

		final ChartPanel chartPanel = new ChartPanel(chart);
		content.add(chartPanel);

		final JPanel buttonPanel = new JPanel(new FlowLayout());

		/*
		 * for (int i = 0; i < SUBPLOT_COUNT; i++) { final JButton button = new
		 * JButton("Series " + i); button.setActionCommand("ADD_DATA_" + i);
		 * //button.addActionListener(this); buttonPanel.add(button); }
		 */

		final JButton buttonPause = new JButton("Pause");
		buttonPause.setActionCommand("pause");
		buttonPause.addActionListener(this);
		buttonPanel.add(buttonPause);

		final JButton buttonAll = new JButton("Stop");
		buttonAll.setActionCommand("ADD_ALL");
		buttonAll.addActionListener(this);
		buttonPanel.add(buttonAll);

		final JButton buttonLecture = new JButton("Lecture");
		buttonLecture.setActionCommand("Lecture");
		buttonLecture.addActionListener(this);
		buttonPanel.add(buttonLecture);

		content.add(buttonPanel, BorderLayout.SOUTH);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 470));
		chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setContentPane(content);

	}

	/**
	 * Handles a click on the button and perform the wanted action.
	 * 
	 * @param e
	 *            the action event.
	 */
	public void actionPerformed(final ActionEvent e) {

		for (int i = 0; i < SUBPLOT_COUNT; i++) {
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

			if (arg instanceof Integer) {

				int i = 0;
				// final Millisecond now = new Millisecond();
				// System.out.println("Now = " + now.toString());
				this.lastValue[i] = ((Integer) arg).doubleValue();

				Millisecond milli = new Millisecond();

				this.datasets[i].getSeries(0).addOrUpdate(milli,
						this.lastValue[i]);

			}
		}

		// PFAST
		if (o instanceof PFastAlgorithm) {
			PFastAlgorithm pfast = (PFastAlgorithm) o;
			pfast.countObservers();

			if (arg instanceof Integer) {

				int i = 0;
				// final Millisecond now = new Millisecond();
				// System.out.println("Now = " + now.toString());
				this.lastValue[i] = ((Integer) arg).doubleValue();
				this.datasets[i].getSeries(0).addOrUpdate(new Millisecond(),
						this.lastValue[i]);

			}
		}

		// Genetic algo
		if (o instanceof StandardGeneticAlgorithm) {
			StandardGeneticAlgorithm geneticAlgorithm = (StandardGeneticAlgorithm) o;
			geneticAlgorithm.countObservers();

			if (arg instanceof Integer) {

				int i = 0;
				// final Millisecond now = new Millisecond();
				// System.out.println("Now = " + now.toString());
				this.lastValue[i] = ((Integer) arg).doubleValue();
				this.datasets[i].getSeries(0).addOrUpdate(new Millisecond(),
						this.lastValue[i]);

			}
		}

		// Pgenetic algo
		if (o instanceof PGeneticAlgo) {
			PGeneticAlgo geneticAlgorithm = (PGeneticAlgo) o;
			geneticAlgorithm.countObservers();

			if (arg instanceof Integer) {

				int i = 0;
				// final Millisecond now = new Millisecond();
				// System.out.println("Now = " + now.toString());
				this.lastValue[i] = ((Integer) arg).doubleValue();
				this.datasets[i].getSeries(0).addOrUpdate(new Millisecond(),
						this.lastValue[i]);

			}
		}

	}

	/**
	 * Getters and setters
	 */

	public int getSUBPLOT_COUNT() {
		return SUBPLOT_COUNT;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public void setSUBPLOT_COUNT(int subplot_count) {
		SUBPLOT_COUNT = subplot_count;
	}

}