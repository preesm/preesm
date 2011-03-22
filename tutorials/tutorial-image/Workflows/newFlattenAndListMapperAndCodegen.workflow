<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
        <dftools:data key="variables">
            <dftools:variable name="allocationPolicy" value="Global"/>
            <dftools:variable name="sourcePath" value="/tutorial-image/Code"/>
            <dftools:variable name="xslLibraryPath" value="/tutorial-image/Code/XSL"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.mapper.exporter.ImplExportTransform" taskId="ImplementationExporter">
        <dftools:data key="variables">
            <dftools:variable name="openFile" value="false"/>
            <dftools:variable name="path" value="/tutorial-image/DAG/outDAG.xml"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.mapper.listscheduling" taskId="LIST scheduler">
        <dftools:data key="variables">
            <dftools:variable name="balanceLoads" value="true"/>
            <dftools:variable name="dagExportPath" value="/tutorial_image/DAG"/>
            <dftools:variable name="displaySolutions" value="true"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="simulatorType" value="AccuratelyTimed"/>
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
    <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
        <dftools:data key="variables">
            <dftools:variable name="openFile" value="false"/>
            <dftools:variable name="path" value="/tutorial-image/DAG/flatten.graphml"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="DAG Plotter"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="codegen"/>
    <dftools:dataTransfer from="scenario" sourceport="SDF"
        targetport="SDF" to="ImplementationExporter"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="ImplementationExporter"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="ImplementationExporter"/>
    <dftools:dataTransfer from="LIST scheduler" sourceport="DAG"
        targetport="DAG" to="codegen"/>
    <dftools:dataTransfer from="LIST scheduler" sourceport="DAG"
        targetport="DAG" to="ImplementationExporter"/>
    <dftools:dataTransfer from="LIST scheduler" sourceport="ABC"
        targetport="ABC" to="DAG Plotter"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="LIST scheduler"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="LIST scheduler"/>
    <dftools:dataTransfer from="scenario" sourceport="SDF"
        targetport="SDF" to="HierarchyFlattening"/>
    <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF"
        targetport="SDF" to="HSDF"/>
    <dftools:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="Exporter"/>
    <dftools:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="LIST scheduler"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="codegen"/>
</dftools:workflow>
