package org.ietr.preesm.core.codegen;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.sdf4j.model.AbstractEdge;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.AbstractVertex;

public class FiniteForLoop extends AbstractBufferContainer implements
		ICodeElement {

	private static List<String> indexs ;
	@SuppressWarnings("unchecked")
	private Map<AbstractEdge, Buffer> buffers ;
	
	private int startIndex ;
	private int stopIndex;
	private int increment;
	private String index ;
	private AbstractVertex<?> vertexDescription ;
	private List<ICodeElement> calls ;
	
	@SuppressWarnings("unchecked")
	public FiniteForLoop(AbstractBufferContainer parentContainer, AbstractVertex vertex, int nbIter) {
		super(parentContainer);
		startIndex = 0 ;
		stopIndex = nbIter ;
		increment = 1;
		vertexDescription = vertex ;
		calls = new ArrayList<ICodeElement>() ;
		buffers = new HashMap<AbstractEdge, Buffer>();
		if(index == null){
			indexs = new ArrayList<String>() ;
			indexs.add("i0");
			index = "i1";
		}else{
			index = "i"+indexs.size()+1;
			indexs.add(index);
		}
		for(AbstractEdge edge : ((AbstractVertex<AbstractGraph<AbstractVertex, AbstractEdge>>) vertexDescription).getBase().edgesOf(vertex)){
			for(Buffer buf : parentContainer.getBuffers(edge)){
				SubBuffer subBuff = new SubBuffer(buf.getName(), buf.getSize(), buf.getType(), buf.getAggregate()) ;
				subBuff.setParentBuffer(buf);
				subBuff.setIndex(index);
				this.addBuffer(subBuff, edge);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Set<Buffer> getBuffers(AbstractEdge edge) {
		Set<Buffer> buffer = new TreeSet<Buffer>() ;
		buffer.add(buffers.get(edge));
		return buffer ;
	}
	
	@SuppressWarnings("unchecked")
	public void addBuffer(Buffer buffer, AbstractEdge edge){
		buffers.put(edge, buffer);
	}

	@Override
	public void accept(AbstractPrinter printer) {
		printer.visit(this, 0);
		for(ICodeElement call : calls){
			call.accept(printer);
		}
		printer.visit(this, 1);
	}

	@Override
	public AbstractVertex<?> getCorrespondingVertex() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Sets the loop start index
	 * @param start The start index of the loop
	 */
	public void setStartIndex(int start){
		this.startIndex = start ;
	}
	
	/**
	 * Sets the stop index of the loop
	 * @param stop The stop index of the loop
	 */
	public void setStopIndex(int stop){
		this.stopIndex = stop ;
	}
	
	/**
	 * Sets the increment of the loop
	 * @param inc The increment of the loop
	 */
	public void setIncrement(int inc){
		this.increment = inc ;
	}
	
	public void addCall(ICodeElement call){
		calls.add(call);
	}
	
	public int getStartIndex(){
		return startIndex ;
	}
	public int getStopIndex(){
		return stopIndex ;
	}
	
	public int getIncrement(){
		return increment ;
	}
	
	public String getIndex(){
		return index ;
	}
	
	public String toString(){
		String result = new String() ;
		result += "for(int i ="+startIndex+" ; i < "+stopIndex+" ; i +="+increment+" ){\n";
		for(ICodeElement call : calls){
			result += call.toString()+";\n";
		}
		result +=" }";
		return result ;
	}

}
