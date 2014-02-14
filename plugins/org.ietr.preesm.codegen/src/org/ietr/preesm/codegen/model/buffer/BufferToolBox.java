/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
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
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.codegen.model.buffer;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.Variable;

import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.psdf.parameters.DynamicParameterRange;
import org.ietr.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;
import org.ietr.dftools.algorithm.model.psdf.types.PSDFEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.codegen.model.expression.BinaryExpression;
import org.ietr.preesm.codegen.model.expression.ConstantExpression;
import org.ietr.preesm.codegen.model.expression.IExpression;
import org.ietr.preesm.codegen.model.expression.StringExpression;
import org.ietr.preesm.codegen.model.main.LoopIndex;

public class BufferToolBox {

	public static int getBufferSize(SDFEdge edge) {
		if (!(edge.getProd() instanceof SDFIntEdgePropertyType && edge
				.getCons() instanceof SDFIntEdgePropertyType)) {
			if (edge.getCons() instanceof PSDFEdgePropertyType) {
				PSDFEdgePropertyType psdfProp = (PSDFEdgePropertyType) edge
						.getCons();
				PSDFDynamicParameter dParam = getCorrespondingParameter(edge,
						psdfProp.getSymbolicName());
				if (dParam != null
						&& dParam.getDomain() instanceof DynamicParameterRange) {
					int nbRepeat = 1;
					try {
						if (edge.getTarget().getNbRepeat() instanceof Integer) {
							nbRepeat = (Integer) edge.getTarget().getNbRepeat();
						} else {
							Expression expr = (Expression) edge.getTarget()
									.getNbRepeat();
							Generic newExpr = expr;
							Variable[] vars = expr.variables();
							for (int i = 0; i < vars.length; i++) {
								if (getCorrespondingParameter(edge,
										vars[i].name()) != null) {
									PSDFDynamicParameter dynParam = getCorrespondingParameter(
											edge, vars[i].name());
									if (dynParam.getDomain() instanceof DynamicParameterRange) {
										int max = ((DynamicParameterRange) dynParam
												.getDomain()).getMaxValue();
										newExpr = newExpr.substitute(vars[i],
												JSCLInteger.valueOf(max));
									}
								}
							}
							if (newExpr.simplify() instanceof JSCLInteger) {
								nbRepeat = newExpr.simplify().integerValue()
										.intValue();
							} else {
								return 0;
							}
						}
					} catch (Exception e) {

					}
					if (dParam.getDomain() instanceof DynamicParameterRange) {
						return nbRepeat
								* ((DynamicParameterRange) dParam.getDomain())
										.getMaxValue();
					}
				}
			} else if (edge.getProd() instanceof PSDFEdgePropertyType) {
				PSDFEdgePropertyType psdfProp = (PSDFEdgePropertyType) edge
						.getProd();
				PSDFDynamicParameter dParam = getCorrespondingParameter(edge,
						psdfProp.getSymbolicName());
				if (dParam != null
						&& dParam.getDomain() instanceof DynamicParameterRange) {
					int nbRepeat = 1;
					try {
						if (edge.getSource().getNbRepeat() instanceof Integer) {
							nbRepeat = (Integer) edge.getSource().getNbRepeat();
						} else {
							Expression expr = (Expression) edge.getSource()
									.getNbRepeat();
							Generic newExpr = expr;
							Variable[] vars = expr.variables();
							for (int i = 0; i < vars.length; i++) {
								if (getCorrespondingParameter(edge,
										vars[i].name()) != null) {
									PSDFDynamicParameter dynParam = getCorrespondingParameter(
											edge, vars[i].name());
									if (dynParam.getDomain() instanceof DynamicParameterRange) {
										int max = ((DynamicParameterRange) dynParam
												.getDomain()).getMaxValue();
										newExpr = newExpr.substitute(vars[i],
												JSCLInteger.valueOf(max));
									}
								}
							}
							if (newExpr.simplify() instanceof JSCLInteger) {
								nbRepeat = newExpr.simplify().integerValue()
										.intValue();
							} else {
								return 0;
							}
						}
					} catch (Exception e) {

					}
					if (dParam.getDomain() instanceof DynamicParameterRange) {
						return nbRepeat
								* ((DynamicParameterRange) dParam.getDomain())
										.getMaxValue();
					}

				}
			}
			return 0;
		}
		try {
			if (edge.getSource().equals(edge.getTarget())) {
				return edge.getProd().intValue();
			} else {

				return Math.max(edge.getProd().intValue()
						* edge.getSource().getNbRepeatAsInteger(), edge
						.getCons().intValue()
						* edge.getTarget().getNbRepeatAsInteger());

			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static IExpression getBufferSymbolicSize(SDFEdge edge) {
		if (!(edge.getProd() instanceof SDFIntEdgePropertyType && edge
				.getCons() instanceof SDFIntEdgePropertyType)) {
			try {
				if (edge.getCons() instanceof PSDFEdgePropertyType) {
					return new BinaryExpression("*", new StringExpression(edge
							.getCons().toString()), new StringExpression(edge
							.getTarget().getNbRepeat().toString()));
				} else {
					return new BinaryExpression("*", new StringExpression(edge
							.getProd().toString()), new StringExpression(edge
							.getSource().getNbRepeat().toString()));
				}
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static IExpression createBufferIndex(LoopIndex index, SDFEdge edge,
			AbstractBufferContainer parentContainer, boolean prod) {
		if (prod) {
			IExpression size = new StringExpression(edge.getProd().getValue()
					.toString());
			IExpression modulo;
			if (parentContainer.getBuffer(edge) instanceof Pointer) {
				modulo = ((Pointer) parentContainer.getBuffer(edge))
						.getSymbolicSize();
			} else {
				modulo = new ConstantExpression(parentContainer.getBuffer(edge)
						.getAllocatedSize());
			}
			return new BinaryExpression("%", new BinaryExpression("*", index,
					size), modulo);
		} else {
			IExpression size = new StringExpression(edge.getCons().getValue()
					.toString());
			IExpression modulo;
			if (parentContainer.getBuffer(edge) instanceof Pointer) {
				modulo = ((Pointer) parentContainer.getBuffer(edge))
						.getSymbolicSize();
			} else {
				modulo = new ConstantExpression(parentContainer.getBuffer(edge)
						.getAllocatedSize());
			}
			return new BinaryExpression("%", new BinaryExpression("*", index,
					size), modulo);
		}

	}

	public static PSDFDynamicParameter getCorrespondingParameter(SDFEdge edge,
			String pName) {
		if (edge.getBase().getParameter(pName) == null) {
			if (edge.getSource().getGraphDescription() != null
					&& edge.getSource().getGraphDescription()
							.getParameter(pName) != null) {
				return (PSDFDynamicParameter) edge.getSource()
						.getGraphDescription().getParameter(pName);
			} else if (edge.getTarget().getGraphDescription() != null
					&& edge.getTarget().getGraphDescription()
							.getParameter(pName) != null) {
				return (PSDFDynamicParameter) edge.getTarget()
						.getGraphDescription().getParameter(pName);
			} else {
				return null;
			}
		} else {
			return (PSDFDynamicParameter) edge.getBase().getParameter(pName);
		}
	}

}
