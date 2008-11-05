/*
 * Copyright (c) 2008, IETR/INSA of Rennes
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the IETR/INSA of Rennes nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.ietr.preesm.plugin.mapper.exporter;

import java.io.ByteArrayOutputStream;
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
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.log.PreesmLogger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.xml.sax.SAXException;

/**
 * This class provides methods to transform an XML file or a DOM element to a string
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 * 
 */
public class XsltTransformer {

	private Transformer transformer;

	/**
	 * Creates a new {@link XsltTransformer} with an XSLT stylesheet contained
	 * in the file whose name is <code>fileName</code>.
	 * 
	 * @param fileName
	 *            The XSLT stylesheet file name.
	 * @throws TransformerConfigurationException
	 *             Thrown if there are errors when parsing the Source or it is
	 *             not possible to create a {@link Transformer} instance.
	 */
	public XsltTransformer(String fileName)
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
	 * Calls {@link Transformer#setParameter(String, Object)} on the underlying
	 * {@link #transformer}.
	 */
	public void setParameter(String name, Object value) {
		transformer.setParameter(name, value);
	}

	/**
	 * Transforms the given input file and generates the output file
	 */
	public void transformFileToFile(String sourceFilePath, String destFilePath)  {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		Path relativePath = new Path(sourceFilePath);
		IFile inputFile = null;
		Document source = null;
		Document dest = null;
		DocumentBuilder db = null;
		
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			inputFile = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);
			// Using factory get an instance of document builder
			db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			source = db.parse(inputFile.getContents());
			dest = db.newDocument();

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

	/**
	 * Transforms the given DOM element (and its children) and returns the
	 * result as a string. The string may contain text or XML.
	 * 
	 * @param element
	 *            The source element to transform.
	 * @return The string resulting from the transformation.
	 * @throws TransformerException
	 *             If an unrecoverable error occurs during the course of the
	 *             transformation.
	 */
	public String transformDomToString(Element element)
			throws TransformerException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DOMSource source = new DOMSource(element);
		StreamResult result = new StreamResult(os);
		transformer.transform(source, result);
		try {
			os.close();
		} catch (IOException e) {
			// never happens on a byte array output stream
		}

		String value = os.toString();
		return value;
	}

}
