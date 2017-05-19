/*******************************************************************************
 * Copyright or © or Copr. 2011 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2011)
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
/**
 * 
 */
package org.ietr.workflow.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.transform.TransformerConfigurationException;

/**
 * @author mpelcat
 * 
 */
public class WorkflowConverter {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		File currentDir = new File(".");

		File[] files = currentDir.listFiles();

		System.out.println("converting workflow in directory: "
				+ currentDir.getCanonicalPath());

		for (File file : files) {
			if (file.getCanonicalPath().endsWith(".workflow")) {

				if (!isNewWorkflow(file)) {

					String inputPath = file.getCanonicalPath();
					String outputPath = file.getCanonicalPath().replaceFirst(".workflow", "_new.workflow");
					String xslPath = currentDir.getCanonicalPath()
							+ "/newWorkflow.xslt";

					if (!inputPath.isEmpty() && !outputPath.isEmpty()
							&& !xslPath.isEmpty()) {
						try {
							XsltTransformer xsltTransfo = new XsltTransformer();
							if (xsltTransfo.setXSLFile(xslPath)) {
								System.out.println("Generating file: "
										+ outputPath);
								xsltTransfo.transformFileToFile(inputPath,
										outputPath);
							}

							// xsltTransfo.
						} catch (TransformerConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	private static boolean isNewWorkflow(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		String thisLine;
		BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
		while ((thisLine = myInput.readLine()) != null) {
			if (thisLine.contains("xmlns:dftools")) {
				return true;
			}
		}
		
		return false;
	}
}
