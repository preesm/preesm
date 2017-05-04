package org.ietr.preesm.algorithm.transforms;

import java.util.ArrayList;
import java.util.List;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;

public class REPVertex {

	/* left Vertex --> right Vertex */

	/* repeat * ( repeatLeft * leftVertex --> repeatRight * rightVertex ) */

	private SDFAbstractVertex leftVertex = null;
	private List<SDFAbstractVertex> inLeftVertexVertexs = new ArrayList<SDFAbstractVertex>();
	private List<SDFAbstractVertex> outLeftVertexVertexs = new ArrayList<SDFAbstractVertex>();

	private SDFAbstractVertex rightVertex = null;
	private List<SDFAbstractVertex> inRightVertexVertexs = new ArrayList<SDFAbstractVertex>();
	private List<SDFAbstractVertex> outRightVertexVertexs = new ArrayList<SDFAbstractVertex>();

	private int repeat = -1;
	private int repeatLeft = -1;
	private int repeatRight = -1;

	/* repLeftCluster --> this --> repRightCluster */

	private REPVertex repLeftCluster = null;
	private REPVertex repRightCluster = null;

	public SDFAbstractVertex getLeftVertex() {
		return leftVertex;
	}
	public void setLeftVertex(SDFAbstractVertex leftVertex) {
		this.leftVertex = leftVertex;
	}
	public SDFAbstractVertex getRightVertex() {
		return rightVertex;
	}
	public void setRightVertex(SDFAbstractVertex rightVertex) {
		this.rightVertex = rightVertex;
	}
	public int getRepeat() {
		return repeat;
	}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	public int getRepeatLeft() {
		return repeatLeft;
	}
	public void setRepeatLeft(int repeatLeft) {
		this.repeatLeft = repeatLeft;
	}
	public int getRepeatRight() {
		return repeatRight;
	}
	public void setRepeatRight(int repeatRight) {
		this.repeatRight = repeatRight;
	}
	public REPVertex getRepLeftCluster() {
		return repLeftCluster;
	}
	public void setRepLeftCluster(REPVertex repLeftCluster) {
		this.repLeftCluster = repLeftCluster;
	}
	public REPVertex getRepRightCluster() {
		return repRightCluster;
	}
	public void setRepRightCluster(REPVertex repRightCluster) {
		this.repRightCluster = repRightCluster;
	}
	public List<SDFAbstractVertex> getInLeftVertexVertexs() {
		return inLeftVertexVertexs;
	}
	public void setInLeftVertexVertexs(List<SDFAbstractVertex> inLeftVertexVertexs) {
		this.inLeftVertexVertexs = inLeftVertexVertexs;
	}
	public List<SDFAbstractVertex> getOutLeftVertexVertexs() {
		return outLeftVertexVertexs;
	}
	public void setOutLeftVertexVertexs(List<SDFAbstractVertex> outLeftVertexVertexs) {
		this.outLeftVertexVertexs = outLeftVertexVertexs;
	}
	public List<SDFAbstractVertex> getInRightVertexVertexs() {
		return inRightVertexVertexs;
	}
	public void setInRightVertexVertexs(List<SDFAbstractVertex> inRightVertexVertexs) {
		this.inRightVertexVertexs = inRightVertexVertexs;
	}
	public List<SDFAbstractVertex> getOutRightVertexVertexs() {
		return outRightVertexVertexs;
	}
	public void setOutRightVertexVertexs(List<SDFAbstractVertex> outRightVertexVertexs) {
		this.outRightVertexVertexs = outRightVertexVertexs;
	}
}
