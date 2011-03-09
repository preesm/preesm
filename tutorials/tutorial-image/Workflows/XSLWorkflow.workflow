<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario"/>
    <dftools:task pluginId="org.ietr.preesm.XsltTransform" taskId="XSL">
        <dftools:data key="variables">
            <dftools:variable name="inputFile" value="/tutorial-image/Workflows/flattenAndListMapper.workflow"/>
            <dftools:variable name="outputFile" value="/tutorial-image/Workflows/flattenAndListMapper2.workflow"/>
            <dftools:variable name="xslFile" value="/tutorial-image/Workflows/newWorkflow.xslt"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="void"
        targetport="void" to="XSL"/>
</dftools:workflow>
