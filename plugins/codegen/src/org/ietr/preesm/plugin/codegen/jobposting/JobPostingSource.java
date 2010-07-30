/**
 * 
 */
package org.ietr.preesm.plugin.codegen.jobposting;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;

/**
 * Represents the data to put in the source code that will be used by a job posting system
 * 
 * @author mpelcat
 */
public class JobPostingSource extends AbstractBufferContainer {

	
	private List<JobDescriptor> descriptors = null;
	public JobPostingSource() {
		super(null);
		descriptors = new ArrayList<JobDescriptor>();
	}

	@Override
	public String getName() {
		return "jobPosting";
	}
	
	public void addDescriptor(JobDescriptor desc){
		descriptors.add(desc);
		
	}
	
	public List<JobDescriptor> getDescriptors() {
		return descriptors;
	}

	public JobDescriptor getJobDescriptorByVertexName(String name){
		for(JobDescriptor desc : descriptors){
			if(desc.getVertexName().equals(name)){
				return desc;
			}
		}
		
		return null;
	}

}
