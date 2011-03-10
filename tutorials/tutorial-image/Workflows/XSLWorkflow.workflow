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
    <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="SDF Exporter">
        <dftools:data key="variables">
            <dftools:variable name="path" value="/tutorial-image/DAG/test"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
        <dftools:data key="variables">
            <dftools:variable name="depth" value="2"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="void"
        targetport="void" to="XSL"/>
    <dftools:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="SDF Exporter"/>
    <dftools:dataTransfer from="scenario" sourceport="SDF"
        targetport="SDF" to="HierarchyFlattening"/>
    <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF"
        targetport="SDF" to="HSDF"/>
</dftools:workflow>
