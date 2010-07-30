<?xml version="1.0" encoding="UTF-8"?>
<preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
    <preesm:algorithm/>
    <preesm:architecture/>
    <preesm:scenario/>
    <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
        <data key="variables">
            <variable name="sourcePath" value="/Decodeur_Type_I_MED_LV/Code"/>
            <variable name="xslLibraryPath" value="/Decodeur_Type_I_MED_LV/Code/XSL"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
        <data key="variables"/>
    </preesm:task>
    <preesm:task
        pluginId="org.ietr.preesm.plugin.mapper.exporter.ImplExportTransform" taskId="ImplementationExporter">
        <data key="variables">
            <variable name="path" value="/Decodeur_Type_I_MED_LV/DAG/outDAG.xml"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST mapper">
        <data key="variables">
            <variable name="balanceLoads" value="false"/>
            <variable name="displaySolutions" value="true"/>
            <variable name="edgeSchedType" value="Simple"/>
            <variable name="fastLocalSearchTime" value="5"/>
            <variable name="fastTime" value="200"/>
            <variable name="simulatorType" value="AccuratelyTimed"/>
        </data>
    </preesm:task>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="DAG Plotter"/>
    <preesm:dataTransfer from="__architecture" sourceport=""
        targetport="architecture" to="codegen"/>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="__algorithm"/>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="__architecture"/>
    <preesm:dataTransfer from="__algorithm" sourceport=""
        targetport="SDF" to="ImplementationExporter"/>
    <preesm:dataTransfer from="__architecture" sourceport=""
        targetport="architecture" to="ImplementationExporter"/>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="ImplementationExporter"/>
    <preesm:dataTransfer from="__architecture" sourceport=""
        targetport="architecture" to="FAST mapper"/>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="FAST mapper"/>
    <preesm:dataTransfer from="FAST mapper" sourceport="ABC"
        targetport="ABC" to="DAG Plotter"/>
    <preesm:dataTransfer from="FAST mapper" sourceport="DAG"
        targetport="DAG" to="codegen"/>
    <preesm:dataTransfer from="FAST mapper" sourceport="DAG"
        targetport="DAG" to="ImplementationExporter"/>
    <preesm:dataTransfer from="__algorithm" sourceport="SDF"
        targetport="SDF" to="FAST mapper"/>
</preesm:workflow>
