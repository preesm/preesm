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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.task.IFileConversion;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class provides methods to transform an XML file or a DOM element to a string
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 * 
 */
public class XsltTransform implements IFileConversion {

	private Transformer transformer;

	/**
	 * Creates a new {@link XsltTransform}
	 */
	public XsltTransform(){
		super();
	}

	/**
	 * Sets an XSLT stylesheet contained
	 * in the file whose name is <code>fileName</code>.
	 * 
	 * @param fileName
	 *            The XSLT stylesheet file name.
	 * @throws TransformerConfigurationException
	 *             Thrown if there are errors when parsing the Source or it is
	 *             not possible to create a {@link Transformer} instance.
	 */
	public void setXSLFile(String fileName)
			throws TransformerConfigurationException {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		Path relativePath = new Path(fileName);
		
		IFile file = workspace.getRoot().getFile(relativePath);
		
		TransformerFactory factory = TransformerFactory.newInstance(
				"net.sf.saxon.TransformerFactoryImpl", null);
		
		StreamSource xsltSource;
		try {
			xsltSource = new StreamSource(file.getContents());
			transformer = factory.newTransformer(xsltSource);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Transforms the given input file and generates the output file
	 */
	public void transformFileToFile(String sourceFilePath, String destFilePath)  {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		Path relativePath = new Path(sourceFilePath);
		IFile inputFile = null;
		Document source = null;
		DocumentBuilder db = null;
		
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			inputFile = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);
			// Using factory get an instance of document builder
			db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			source = db.parse(inputFile.getContents());

		} catch (ParserConfigurationException pce) {
			PreesmLogger.getLogger().log(Level.SEVERE,pce.getMessage());
		} catch (SAXException se) {
			PreesmLogger.getLogger().log(Level.SEVERE,se.getMessage());
		} catch (IOException ioe) {
			PreesmLogger.getLogger().log(Level.SEVERE,ioe.getMessage());
		} catch (CoreException e) {
			PreesmLogger.getLogger().log(Level.SEVERE,e.getMessage());
		}

		IFile outputIFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(destFilePath));
		File outputFile= new File(outputIFile.getLocation().toOSString());
		Source xmlSource = new DOMSource(source.getDocumentElement());
		Result outputTarget = new StreamResult(outputFile);
		try {
			transformer.transform(xmlSource, outputTarget);
		} catch (TransformerException e) {
			PreesmLogger.getLogger().log(Level.SEVERE,e.getMessage());
		}

	}

	@Override
	public TaskResult transform(TextParameters params) {

		Path inputPath = new Path(params.getVariable("inputFile"));
		Path outputPath = new Path(params.getVariable("outputFile"));
		Path xslPath = new Path(params.getVariable("xslFile"));
		
		if(!inputPath.isEmpty() && !outputPath.isEmpty() && !xslPath.isEmpty()){
			try {
				XsltTransform xsltTransfo = new XsltTransform();
				xsltTransfo.setXSLFile(xslPath.toOSString());
				xsltTransfo.transformFileToFile(inputPath.toOSString(), outputPath.toOSString());
				
				//xsltTransfo.
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return new TaskResult();
	}

}
