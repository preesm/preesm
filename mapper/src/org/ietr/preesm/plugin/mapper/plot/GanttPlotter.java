package org.ietr.preesm.plugin.mapper.plot;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.looselytimed.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.tools.BLevelIterator;
import org.ietr.preesm.plugin.mapper.tools.TLevelIterator;
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
public class GanttPlotter extends ApplicationFrame {

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
			IAbc simulator) {

		TaskSeries series = new TaskSeries("Scheduled");
		Task currenttask;

		// Creating the Operator lines
		TopologicalDAGIterator iterator = new TopologicalDAGIterator(dag);
		Set<ArchitectureComponent> components = new HashSet<ArchitectureComponent>();

		while (iterator.hasNext()) {
			MapperDAGVertex currentvertex = (MapperDAGVertex)iterator.next();

			ArchitectureComponent currentComponent = currentvertex
					.getImplementationVertexProperty().getEffectiveComponent();

			if (currentComponent != ArchitectureComponent.NO_COMPONENT) {
				if (!components.contains(currentComponent)) {
					components.add(currentComponent);
					currenttask = new Task(currentComponent.getName(),
							new SimpleTimePeriod(0, simulator
									.getFinalTime(currentComponent)));
					series.add(currenttask);
				}
			}
		}

		// Creating the Operator lines
		TopologicalDAGIterator viterator = new TopologicalDAGIterator(dag);

		while (viterator.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex)viterator.next();
			ArchitectureComponent cmp = simulator
					.getEffectiveComponent(currentVertex);

			if (cmp != ArchitectureComponent.NO_COMPONENT) {
				long start = simulator.getTLevel(currentVertex);
				long end = simulator.getFinalTime(currentVertex);
				Task t = new Task(currentVertex.getName(), new SimpleTimePeriod(
						start, end));
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
	public static void main(String[] args) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.ALL);

		logger.log(Level.FINEST, "Creating archi");
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample2(archi);

		IAbc simulator = new LooselyTimedAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");
		// simulator.implantAllVerticesOnOperator(archi.getMainOperator());
		simulator.implant(dag.getMapperDAGVertex("n1"), archi.getOperator("c64x_1"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n3"), archi.getOperator("c64x_1"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n2"), archi.getOperator("c64x_1"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n7"), archi.getOperator("c64x_1"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n6"), archi.getOperator("c64x_2"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n5"), archi.getOperator("c64x_4"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n4"), archi.getOperator("c64x_3"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n8"), archi.getOperator("c64x_4"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n9"), archi.getOperator("c64x_4"),
				true);

		simulator.getFinalTime(archi.getOperator("c64x_1"));
		simulator.getFinalTime(archi.getOperator("c64x_2"));
		simulator.getFinalTime(archi.getOperator("c64x_3"));
		simulator.getFinalTime(archi.getOperator("c64x_4"));

		logger.log(Level.FINEST, "Iterating in t order");

		TLevelIterator titerator = new TLevelIterator(dag, simulator, false);

		while (titerator.hasNext()) {
			MapperDAGVertex currentvertex = (MapperDAGVertex) titerator.next();

			logger.log(Level.FINEST, "vertex " + currentvertex.getName()
					+ ", t-level: " + simulator.getTLevel(currentvertex));
		}

		logger.log(Level.FINEST, "Iterating in b order");

		BLevelIterator biterator = new BLevelIterator(dag, simulator, false);

		while (biterator.hasNext()) {
			MapperDAGVertex currentvertex = (MapperDAGVertex) biterator.next();

			logger.log(Level.FINEST, "vertex " + currentvertex.getName()
					+ ", b-level: "
					+ currentvertex.getTimingVertexProperty().getValidBlevel());
		}

		logger.log(Level.FINEST, "Getting finishing times");

		int test;

		simulator.setDAG(dag);

		test = simulator.getFinalTime(dag.getMapperDAGVertex("n1"));
		logger.log(Level.FINEST, "n1: " + test);

		test = simulator.getFinalTime(dag.getMapperDAGVertex("n5"));
		logger.log(Level.FINEST, "n5: " + test);

		test = simulator.getFinalTime(dag.getMapperDAGVertex("n8"));
		logger.log(Level.FINEST, "n8: " + test);

		test = simulator.getFinalTime(dag.getMapperDAGVertex("n9"));
		logger.log(Level.FINEST, "n9: " + test);

		test = simulator.getFinalTime();
		logger.log(Level.FINEST, "final: " + test);

		logger.log(Level.FINEST, "Test finished");

		GanttPlotter plot = new GanttPlotter("Solution cost evolution", dag,
				simulator);

		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);

	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void plot(MapperDAG dag, IAbc simulator) {

		GanttPlotter plot = new GanttPlotter("Solution gantt, latency: "
				+ simulator.getFinalTime(), dag, simulator);

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
	public GanttPlotter(String title, MapperDAG dag,
			IAbc simulator) {
		super(title);
		JFreeChart chart = createChart(createDataset(dag, simulator));
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
	}

}
