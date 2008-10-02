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


package org.ietr.preesm.plugin.mapper.plot;

import java.awt.Color;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of a time series chart. For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 */
public class TimePlotter extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return A chart.
	 */
	private static JFreeChart createChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYLineChart(
				"Solution cost evolution", // title
				"Iteration number", // x-axis label
				"Solution cost", // y-axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}

		NumberAxis axis = (NumberAxis) plot.getDomainAxis();
		axis.setAutoRange(true);

		return chart;

	}

	/**
	 * Creates a dataset, consisting of two series of monthly data.
	 * 
	 * @return The dataset.
	 */
	private static XYDataset createDataset() {

		XYSeries s1 = new XYSeries("Mapping time", true);
		s1.add(1, 181.8);
		s1.add(2, 167.3);
		s1.add(3, 153.8);
		s1.add(4, 167.6);
		s1.add(5, 158.8);
		s1.add(6, 148.3);
		s1.add(7, 153.9);
		s1.add(8, 142.7);
		s1.add(9, 123.2);
		s1.add(10, 131.8);
		s1.add(11, 139.6);
		s1.add(12, 142.9);
		s1.add(13, 138.7);
		s1.add(14, 137.3);
		s1.add(15, 143.9);
		s1.add(16, 139.8);
		s1.add(17, 137.0);
		s1.add(18, 132.8);

		XYSeries s2 = new XYSeries("Solution cost", true);
		s2.add(1, 129.6);
		s2.add(2, 123.2);
		s2.add(4, 117.2);
		s2.add(6, 124.1);
		s2.add(8, 122.6);
		s2.add(9, 119.2);
		s2.add(10, 116.5);

		// ******************************************************************
		// More than 150 demo applications are included with the JFreeChart
		// Developer Guide...for more information, see:
		//
		// > http://www.object-refinery.com/jfreechart/guide.html
		//
		// ******************************************************************

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);

		return dataset;

	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 * 
	 * @return A panel.
	 */
	public static JPanel createDemoPanel() {
		JFreeChart chart = createChart(createDataset());
		return new ChartPanel(chart);
	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {

		TimePlotter plot = new TimePlotter("Solution cost evolution");
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);

	}

	/**
	 * A demonstration application showing how to create a simple time series
	 * chart. This example uses monthly data.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public TimePlotter(String title) {
		super(title);
		ChartPanel chartPanel = (ChartPanel) createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);

	}
	
	public void windowClosing(WindowEvent event){
		if(event.equals(WindowEvent.WINDOW_CLOSING)){
			
		}
	}
	

}
