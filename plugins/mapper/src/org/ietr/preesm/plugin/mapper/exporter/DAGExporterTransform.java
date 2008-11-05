/**
 * 
 */
package org.ietr.preesm.plugin.mapper.exporter;

import java.io.ByteArrayInputStream;

import javax.xml.transform.TransformerConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.IExporter;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;
/**
 * Block in workflow exporting a DAG as a graphml graph and possibly as an additional LUA file
 * for Nerios compatibility
 * 
 * @author mpelcat
 * 
 */
public class DAGExporterTransform implements IExporter{

	@Override
	public void transform(DirectedAcyclicGraph dag, SDFGraph sdf, MultiCoreArchitecture archi, IScenario scenario, TextParameters params) {

		Path graphmlPath = new Path(params.getVariable("path"));
		Path transformedPath = new Path(params.getVariable("transformedPath"));
		Path xslPath = new Path(params.getVariable("xslPath"));
		
		// Exporting the DAG in a GraphML
		if(!graphmlPath.isEmpty() && !transformedPath.isEmpty()){
			exportGraphML(dag, graphmlPath);
		}
		
		if(!graphmlPath.isEmpty() && !transformedPath.isEmpty() && !xslPath.isEmpty()){
			try {
				XsltTransformer xsltTransfo = new XsltTransformer(xslPath.toOSString());
				xsltTransfo.transformFileToFile(graphmlPath.toOSString(), transformedPath.toOSString());
				
				//xsltTransfo.
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void exportGraphML(DirectedAcyclicGraph dag, Path path){

		MapperDAG mapperDag = (MapperDAG)dag;
		GMLMapperDAGExporter exporter = new GMLMapperDAGExporter() ;
		MapperDAG clone = mapperDag.clone() ;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile iGraphMLFile = workspace.getRoot().getFile(path);
		exporter.export(clone, iGraphMLFile.getLocation().toOSString());
	}

	@Override
	public boolean isDAGExporter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSDFExporter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void transform(AbstractGraph algorithm, TextParameters params) {
		// TODO Auto-generated method stub
		
	}

}