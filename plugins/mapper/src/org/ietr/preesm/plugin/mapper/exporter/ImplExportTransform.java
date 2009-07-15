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
 
package org.ietr.preesm.plugin.mapper.exporter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
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
 * Block in workflow exporting a DAG that contains all informations of an implementation
 * 
 * @author mpelcat
 * 
 */
public class ImplExportTransform implements IExporter{

	@Override
	public void transform(DirectedAcyclicGraph dag, SDFGraph sdf, MultiCoreArchitecture archi, IScenario scenario, TextParameters params) {

		Path graphmlPath = new Path(params.getVariable("path"));
		
		// Exporting the DAG in a GraphML
		if(!graphmlPath.isEmpty()){
			exportGraphML(dag, graphmlPath);
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
		return true;
	}

	@Override
	public boolean isSDFExporter() {
		return false;
	}

	@Override
	public boolean isArchiExporter() {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked") 
	public void transform(AbstractGraph algorithm, TextParameters params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transform(MultiCoreArchitecture archi, TextParameters params) {
		// TODO Auto-generated method stub
		
	}

}