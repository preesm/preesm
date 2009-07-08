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
import java.awt.Container;
import java.awt.Frame;
import java.awt.LinearGradientPaint;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * Gantt plotter of a mapperdagvertex using JFreeChart
 * 
 * @author mpelcat
 */
public class GanttPlotter extends ApplicationFrame implements
		IImplementationPlotter {

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
	private static JFreeChart createChart(IntervalCategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createGanttChart("Solution Gantt", // title
				"Operators", // x-axis label
				"Time", // y-axis label
				null, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(500, 500);
		float[] dist = { 0.0f, 0.8f };
		Color[] colors = { Color.BLUE.brighter().brighter(), Color.WHITE };
		LinearGradientPaint p = new LinearGradientPaint(start, end, dist,
				colors);

		chart.setBackgroundPaint(p);

		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.black);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setOrientation(PlotOrientation.HORIZONTAL);

		DateAxis xaxis = (DateAxis) plot.getRangeAxis();
		xaxis.setDateFormatOverride(new VertexDateFormat());
		xaxis.setPositiveArrowVisible(true);

		DefaultDrawingSupplier d = new DefaultDrawingSupplier();

		plot.setDrawingSupplier(d);
		GanttRenderer ren = new MyGanttRenderer();
		ren.setBaseFillPaint(p);
		// ren.setSeriesPaint(0, p);
		ren.setSeriesItemLabelsVisible(0, false);
		ren.setSeriesVisibleInLegend(0, false);
		ren.setSeriesItemLabelGenerator(0,
				new IntervalCategoryItemLabelGenerator());
		ren.setSeriesToolTipGenerator(0, new MapperGanttToolTipGenerator());

		ren.setAutoPopulateSeriesShape(false);

		plot.setRenderer(ren);

		plot.setDataset(dataset);
		return chart;

	}

	/**
	 * Creates a dataset from a MapperDAGVertex.
	 * 
	 * @return The dataset.
	 */
	private static IntervalCategoryDataset createDataset(MapperDAG dag,
			LatencyAbc simulator) {

		simulator.updateFinalCosts();

		TaskSeries series = new TaskSeries("Scheduled");
		Task currenttask;

		// Creating the Operator lines
		List<ArchitectureComponent> cmps = simulator.getArchitecture()
				.getComponents();
		Collections.sort(cmps,
				new ArchitectureComponent.ArchitectureComponentComparator());

		for (ArchitectureComponent c : cmps) {
			long finalCost = simulator.getFinalCost(c);
			if (finalCost > 0) {
				currenttask = new Task(c.getName(), new SimpleTimePeriod(0,
						finalCost));
				series.add(currenttask);
			}

		}

		// Populating the Operator lines
		TopologicalDAGIterator viterator = new TopologicalDAGIterator(dag);

		while (viterator.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex) viterator.next();
			ArchitectureComponent cmp = simulator
					.getEffectiveComponent(currentVertex);

			if (cmp != ArchitectureComponent.NO_COMPONENT) {
				long start = simulator.getTLevel(currentVertex, false);
				long end = simulator.getFinalCost(currentVertex);
				String taskName = currentVertex.getName()
						+ " (x"
						+ currentVertex.getInitialVertexProperty()
								.getNbRepeat() + ")";
				Task t = new Task(taskName, new SimpleTimePeriod(start, end));
				series.get(cmp.getName()).addSubtask(t);
			}
		}

		TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(series);

		return collection;

	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void plot(MapperDAG dag, IAbc simulator) {

		simulator.updateFinalCosts();
		GanttPlotter plot = new GanttPlotter("Solution gantt, latency: "
				+ simulator.getFinalCost(), dag, simulator);

		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);

	}

	/**
	 * Gantt chart plotting function in a given composite
	 */
	public static void plotInComposite(IAbc simulator, Composite parent) {

		IImplementationPlotter plotter = simulator.plotImplementation(true);
		if (plotter instanceof GanttPlotter) {
			GanttPlotter plot = (GanttPlotter) simulator
					.plotImplementation(true);

			Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.FILL);
			parent.setLayout(new FillLayout());
			Frame frame = SWT_AWT.new_Frame(composite);
			frame.add(plot.getContentPane());

			parent.addControlListener(plot.new SizeListener(composite, frame));
		}
	}

	/**
	 * A demonstration application showing how to create a simple time series
	 * chart. This example uses monthly data.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public GanttPlotter(String title, MapperDAG dag, IAbc simulator) {
		super(title);
		if (simulator instanceof LatencyAbc) {
			JFreeChart chart = createChart(createDataset(dag,
					(LatencyAbc) simulator));
			ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			chartPanel.setMouseZoomable(true, false);
			setContentPane(chartPanel);
		} else {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"To display a graph Gantt chart, a latency ABC is needed.");
		}

	}

	public void windowClosing(WindowEvent event) {
		if (event.equals(WindowEvent.WINDOW_CLOSING)) {

		}
	}

}
