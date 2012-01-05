<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
        <dftools:data key="variables">
            <dftools:variable name="depth" value="2"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.codegen" taskId="CodeGen">
        <dftools:data key="variables">
            <dftools:variable name="allocationPolicy" value="Global"/>
            <dftools:variable name="printer" value="xml"/>
            <dftools:variable name="sourcePath" value="PSDF-test-case"/>
            <dftools:variable name="xslLibraryPath" value="PSDF-test-case/Code/XSL"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
        <dftools:data key="variables">
            <dftools:variable name="path" value="/IDCT2D/flattenTest.graphml"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="Mapper">
        <dftools:data key="variables">
            <dftools:variable name="balanceLoads" value="false"/>
            <dftools:variable name="dagExportPath" value="/home/jpiat/tedag.xml"/>
            <dftools:variable name="displaySolutions" value="false"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="fastLocalSearchTime" value="10"/>
            <dftools:variable name="fastTime" value="100"/>
            <dftools:variable name="margIn" value="10"/>
            <dftools:variable name="maxCount" value="10"/>
            <dftools:variable name="maxStep" value="10"/>
            <dftools:variable name="simulatorType" value="LooselyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
        <dftools:data key="variables">
            <dftools:variable name="balanceLoads" value="false"/>
            <dftools:variable name="dagExportPath" value=""/>
            <dftools:variable name="displaySolutions" value="false"/>
            <dftools:variable name="edgeSchedType" value="Switcher"/>
            <dftools:variable name="fastLocalSearchTime" value="10"/>
            <dftools:variable name="fastTime" value="100"/>
            <dftools:variable name="margIn" value="30"/>
            <dftools:variable name="maxCount" value="800"/>
            <dftools:variable name="maxStep" value="800"/>
            <dftools:variable name="simulatorType" value="AccuratelyTimed"/>
            <dftools:variable name="switchTask" value="false"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="Plotter">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="Mapper"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Mapper"/>
    <dftools:dataTransfer from="Mapper" sourceport="DAG"
        targetport="DAG" to="CodeGen"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="CodeGen"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="CodeGen"/>
    <dftools:dataTransfer from="Mapper" sourceport="ABC"
        targetport="ABC" to="Plotter"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Plotter"/>
    <dftools:dataTransfer from="scenario" sourceport="SDF"
        targetport="SDF" to="Mapper"/>
</dftools:workflow>
