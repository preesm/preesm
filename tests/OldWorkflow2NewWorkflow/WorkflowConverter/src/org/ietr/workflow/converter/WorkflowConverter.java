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
