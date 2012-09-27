<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="Gantt Plotter">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
        <dftools:data key="variables">
            <dftools:variable name="balanceLoads" value="false"/>
            <dftools:variable name="displaySolutions" value="true"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="fastLocalSearchTime" value="10"/>
            <dftools:variable name="fastTime" value="200"/>
            <dftools:variable name="simulatorType" value="AccuratelyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
        <dftools:data key="variables">
            <dftools:variable name="depth" value="5"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
        <dftools:data key="variables">
            <dftools:variable name="ExplodeImplodeSuppr" value="false"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
        <dftools:data key="variables">
            <dftools:variable name="path" value="DAG/flatten.graphml"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.mapper.exporter.DAGExportTransform" taskId="DAG Exporter">
        <dftools:data key="variables">
            <dftools:variable name="openFile" value="false"/>
            <dftools:variable name="path" value="DAG/dag.graphml"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Gantt Plotter"/>
    <dftools:dataTransfer from="FAST scheduler" sourceport="ABC"
        targetport="ABC" to="Gantt Plotter"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="FAST scheduler"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="FAST scheduler"/>
    <dftools:dataTransfer from="scenario" sourceport="SDF"
        targetport="SDF" to="HierarchyFlattening"/>
    <dftools:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="FAST scheduler"/>
    <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF"
        targetport="SDF" to="HSDF"/>
    <dftools:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="Exporter"/>
    <dftools:dataTransfer from="FAST scheduler" sourceport="DAG"
        targetport="DAG" to="DAG Exporter"/>
</dftools:workflow>
