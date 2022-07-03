#!/bin/bash

##
## This script generate the class JavaExpressionParserTest from the expression.traces
## file (traced generate from an old Preesm run using an instrumented version).
##

JavaFile=`( cd $(dirname expressionTracesGenerator.sh) && pwd)`/../src/org/ietr/preesm/experiment/model/pimm/JavaExpressionParserTest.java

echo $JavaFile
cat > $JavaFile << "EOF"
package org.ietr.preesm.experiment.model.pimm;

import org.junit.Assert;
import org.junit.Test;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class JavaExpressionParserTest {

EOF

COUNTER=0

while read -r line
do
	let COUNTER+=1
	EXPR=`echo $line | cut -d':' -f1`
	VARS=`echo $line | cut -d'[' -f2 | cut -d']' -f1`

	cat >> $JavaFile << EOF
	//$line
	@Test
	public void testExpression$COUNTER() throws ParseException {
		final String expr = "$EXPR";
		JEP jep = new JEP();
EOF

	for VAR in $VARS; do
		VARNAME=`echo $VAR | cut -d',' -f1 | cut -d'=' -f1`
		VALUE=`echo $VAR | cut -d',' -f1 | cut -d'=' -f2`
		echo "jep.addVariable(\"$VARNAME\",$VALUE);" >> $JavaFile
	done

	cat >> $JavaFile << EOF
		final Node parse = jep.parse(expr);
		final Object evaluate = jep.evaluate(parse);
		Assert.assertTrue(evaluate instanceof Double);
	}
EOF
done < `( cd $(dirname expressionTracesGenerator.sh) && pwd)`/expression.traces


cat >> $JavaFile << "EOF"
}

EOF

