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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
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

	public class SizeListener implements ControlListener{

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

			frame.setSize(composite.getSize().x,composite.getSize().y);
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
				String taskName = currentVertex.getName() + " (x" + currentVertex.getInitialVertexProperty().getNbRepeat() + ")";
				Task t = new Task(taskName, new SimpleTimePeriod(
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
		MultiCoreArchitecture archi = Examples.get2C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample2(archi);

		IAbc simulator = new LooselyTimedAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");
		// simulator.implantAllVerticesOnOperator(archi.getMainOperator());
		simulator.implant(dag.getMapperDAGVertex("n1"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n3"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n2"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n7"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n6"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_2"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n5"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n4"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_3"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n8"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

		simulator.implant(dag.getMapperDAGVertex("n9"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"));
		simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"));

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


		simulator.retrieveTotalOrder();
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

		//plot.pack();
		//RefineryUtilities.centerFrameOnScreen(plot);
		//plot.setVisible(true);
		
		Display display = Display.getDefault();
		
		Shell shell = new Shell();
	    shell.setLayout(new FillLayout());
	    shell.setText("test");
	    shell.setSize(new Point(240, 460));
	    shell.open();
	    
	    Composite composite = new Composite(shell, SWT.EMBEDDED | SWT.FILL);
	    Frame frame = SWT_AWT.new_Frame(composite);
	    frame.add(plot.getContentPane());

	    shell.addControlListener(plot.new SizeListener(composite,frame));


		while (!shell.isDisposed()) {

			if (!display.readAndDispatch())
				display.sleep();
		}

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
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void plotInComposite(IAbc simulator, Composite parent) {

		GanttPlotter plot = simulator.plotImplementation(true);
		
		//GanttPlotter plot = new GanttPlotter("Solution gantt, latency: "
		//		+ simulator.getFinalTime(), dag, simulator);

	    Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.FILL);
	    parent.setLayout(new FillLayout());
	    Frame frame = SWT_AWT.new_Frame(composite);
	    frame.add(plot.getContentPane());

	    parent.addControlListener(plot.new SizeListener(composite,frame));
		
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

	public void windowClosing(WindowEvent event){
		if(event.equals(WindowEvent.WINDOW_CLOSING)){
			
		}
	}
	
	
}
