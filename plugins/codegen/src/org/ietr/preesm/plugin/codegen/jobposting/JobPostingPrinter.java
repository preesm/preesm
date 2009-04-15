/**
 * 
 */
package org.ietr.preesm.plugin.codegen.jobposting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.ietr.preesm.core.codegen.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.AbstractCodeElement;
import org.ietr.preesm.core.codegen.CompoundCodeElement;
import org.ietr.preesm.core.codegen.Constant;
import org.ietr.preesm.core.codegen.FiniteForLoop;
import org.ietr.preesm.core.codegen.ForLoop;
import org.ietr.preesm.core.codegen.LaunchThread;
import org.ietr.preesm.core.codegen.LinearCodeContainer;
import org.ietr.preesm.core.codegen.LoopIndex;
import org.ietr.preesm.core.codegen.SourceFile;
import org.ietr.preesm.core.codegen.SpecialBehaviorCall;
import org.ietr.preesm.core.codegen.ThreadDeclaration;
import org.ietr.preesm.core.codegen.UserFunctionCall;
import org.ietr.preesm.core.codegen.VariableAllocation;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.buffer.BufferAtIndex;
import org.ietr.preesm.core.codegen.buffer.SubBuffer;
import org.ietr.preesm.core.codegen.buffer.SubBufferAllocation;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionInit;
import org.ietr.preesm.core.codegen.com.ReceiveDma;
import org.ietr.preesm.core.codegen.com.ReceiveMsg;
import org.ietr.preesm.core.codegen.com.SendDma;
import org.ietr.preesm.core.codegen.com.SendMsg;
import org.ietr.preesm.core.codegen.com.WaitForCore;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.semaphore.Semaphore;
import org.ietr.preesm.core.codegen.semaphore.SemaphoreInit;
import org.ietr.preesm.core.codegen.semaphore.SemaphorePend;
import org.ietr.preesm.core.codegen.semaphore.SemaphorePost;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * Prints in XML the code of the job posting model
 * 
 * @author mpelcat
 */
public class JobPostingPrinter {

	/**
	 * Current document
	 */
	private Document dom;

	public JobPostingPrinter() {
		super();

		try {
			DOMImplementation impl;
			impl = DOMImplementationRegistry.newInstance()
					.getDOMImplementation("Core 3.0 XML 3.0 LS");
			dom = impl.createDocument("http://org.ietr.preesm.jobPostingCode",
					"jobPostingCode", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeDom(IFile file) {

		try {
			// Gets the DOM implementation of document
			DOMImplementation impl = dom.getImplementation();
			DOMImplementationLS implLS = (DOMImplementationLS) impl;

			LSOutput output = implLS.createLSOutput();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			output.setByteStream(out);

			LSSerializer serializer = implLS.createLSSerializer();
			serializer.getDomConfig().setParameter("format-pretty-print", true);
			serializer.write(dom, output);

			file.setContents(new ByteArrayInputStream(out.toByteArray()), true,
					false, new NullProgressMonitor());
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addData(JobPostingSource source) {

		Element root = dom.getDocumentElement();

		addBuffers(root, source);

		addDescriptors(root, source);
	}

	/**
	 * Adds in xml structure every data from the job posting source
	 */
	public void addBuffers(Element elt, JobPostingSource source) {

		Element buffers = dom.createElement("BufferContainer");
		elt.appendChild(buffers);

		for (BufferAllocation alloc : source.getBufferAllocations()) {
			addBufferAllocation(buffers, alloc);
		}
	}

	public void addBufferAllocation(Element elt, BufferAllocation alloc) {
		
		Element allocElt = dom.createElement("BufferAllocation");
		elt.appendChild(allocElt);
		
		allocElt.setAttribute("name", alloc.getBuffer().getName());
		allocElt.setAttribute("size", alloc.getBuffer().getSize().toString());
		allocElt.setAttribute("type", alloc.getBuffer().getType().getTypeName());
	}

	/**
	 * Adds in xml structure every data from the job posting source
	 */
	public void addDescriptors(Element elt, JobPostingSource source) {

		Element jobs = dom.createElement("jobs");
		elt.appendChild(jobs);

		for (JobDescriptor desc : source.getDescriptors()) {
			addDescriptor(jobs, desc);
		}
	}

	public void addDescriptor(Element elt, JobDescriptor desc) {
		
		Element job = dom.createElement("job");
		elt.appendChild(job);
		job.setAttribute("id", String.valueOf(desc.getId()));
		job.setAttribute("time", String.valueOf(desc.getTime()));

		Element callName = dom.createElement("callName");
		job.appendChild(callName);
		callName.setTextContent(desc.getVertexName());

		Element functionName = dom.createElement("functionName");
		job.appendChild(functionName);
		functionName.setTextContent(desc.getFunctionName());

		addPredecessors(job, desc.getPrededessors());

		addBuffers(job, desc.getBuffers());
		addConstants(job, desc.getConstants());
	}

	public void addPredecessors(Element elt, List<JobDescriptor> preds) {

		Element predecessors = dom.createElement("predecessors");
		elt.appendChild(predecessors);

		for (JobDescriptor pred : preds) {
			addPredecessor(predecessors, pred);
		}
	}

	public void addPredecessor(Element elt, JobDescriptor pred) {

		Element predElt = dom.createElement("pred");
		elt.appendChild(predElt);
		predElt.setAttribute("id", String.valueOf(pred.getId()));
	}

	public void addBuffers(Element elt, List<Buffer> bufs) {

		Element buffers = dom.createElement("buffers");
		elt.appendChild(buffers);

		for (Buffer buf : bufs) {
			addBuffer(buffers, buf);
		}
	}

	public void addBuffer(Element elt, Buffer buf) {

		Element predElt = dom.createElement("buffer");
		elt.appendChild(predElt);
		predElt.setAttribute("name", String.valueOf(buf.getName()));
		predElt.setAttribute("type", String.valueOf(buf.getType().getTypeName()));
		predElt.setAttribute("size", String.valueOf(buf.getSize()));
	}

	public void addConstants(Element elt, List<Constant> consts) {

		Element constants = dom.createElement("constants");
		elt.appendChild(constants);

		for (Constant cst : consts) {
			addConstant(constants, cst);
		}
	}

	public void addConstant(Element elt, Constant cst) {

		Element cstElt = dom.createElement("constant");
		elt.appendChild(cstElt);
		cstElt.setAttribute("name", String.valueOf(cst.getName()));
		cstElt.setAttribute("value", String.valueOf(cst.getValue()));
	}

}
