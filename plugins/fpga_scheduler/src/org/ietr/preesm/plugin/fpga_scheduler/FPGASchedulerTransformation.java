/**
 * 
 */
package org.ietr.preesm.plugin.fpga_scheduler;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.transformation.IMapping;
import org.ietr.preesm.core.transformation.TextParameters;
import org.ietr.preesm.core.transformation.TransformationResult;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * @author pmenuet
 * 
 * FAST Kwok algorithm
 */
public class FPGASchedulerTransformation implements IMapping {

	public static void main(String[] args) {
		// FPGASchedulerTransformation transformation = new
		// FPGASchedulerTransformation();
		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);
		logger.log(Level.FINER, "Test fast finished");
	}

	/**
	 * 
	 */
	public FPGASchedulerTransformation() {
	}

	@Override
	public TransformationResult transform(SDFGraph algorithm,
			IArchitecture architecture, TextParameters algorithmParameters,
			IScenario implantationconstraints) {
		TransformationResult result = new TransformationResult();
		// TODO: add details

		return result;
	}

	@Override
	public void transform(SDFGraph algorithm, SDFGraph transformedAlgorithm) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCodeGeneration() {
		return false;
	}

	@Override
	public boolean isCodeTranslation() {
		return false;
	}

	@Override
	public boolean isMapping() {
		return true;
	}

	@Override
	public boolean isGraphTransformation() {
		// TODO Auto-generated method stub
		return false;
	}
}
