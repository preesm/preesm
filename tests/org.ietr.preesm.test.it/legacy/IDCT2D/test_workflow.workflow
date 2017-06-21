<?xml version="1.0" encoding="UTF-8"?>
<preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
    <preesm:algorithm/>
    <preesm:architecture/>
    <preesm:scenario/>
    <preesm:task
        pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
        <data key="variables">
            <variable name="depth" value="2"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="CodeGen">
        <data key="variables">
            <variable name="allocationPolicy" value="Global"/>
            <variable name="sourcePath" value="IDCT2D"/>
            <variable name="xslLibraryPath" value="IDCT2D/Code/XSL"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
        <data key="variables">
            <variable name="path" value="D:/IDCT2D/dag.gantt"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
        <data key="variables">
            <variable name="path" value="/IDCT2D/flattenTest.graphml"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="Mapper">
        <data key="variables">
            <variable name="margIn" value="10"/>
            <variable name="maxCount" value="10"/>
            <variable name="maxStep" value="10"/>
            <variable name="simulatorType" value="LooselyTimed"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
        <data key="variables"/>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
        <data key="variables">
            <variable name="edgeSchedType" value="Switcher"/>
            <variable name="margIn" value="30"/>
            <variable name="maxCount" value="800"/>
            <variable name="maxStep" value="800"/>
            <variable name="simulatorType" value="AccuratelyTimed"/>
            <variable name="switchTask" value="false"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="Plotter">
        <data key="variables"/>
    </preesm:task>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="__algorithm"/>
    <preesm:dataTransfer from="__scenario" sourceport="" targetport="" to="__architecture"/>
    <preesm:dataTransfer from="__architecture" sourceport="architecture"
        targetport="architecture" to="Mapper"/>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="Mapper"/>
    <preesm:dataTransfer from="Mapper" sourceport="customData"
        targetport="customData" to="DAG Plotter"/>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="DAG Plotter"/>
    <preesm:dataTransfer from="Mapper" sourceport="DAG" targetport="DAG" to="CodeGen"/>
    <preesm:dataTransfer from="__architecture" sourceport="architecture"
        targetport="architecture" to="CodeGen"/>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="CodeGen"/>
    <preesm:dataTransfer from="Mapper" sourceport="ABC" targetport="ABC" to="Plotter"/>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="Plotter"/>
    <preesm:dataTransfer from="__algorithm" sourceport="SDF"
        targetport="SDF" to="Mapper"/>
</preesm:workflow>
