<?xml version="1.0" encoding="UTF-8"?>
<preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
    <preesm:algorithm/>
    <preesm:architecture/>
    <preesm:scenario/>
    <preesm:task pluginId="org.ietr.preesm.plugin.scheduling.listsched" taskId="list scheduling">
        <data key="variables"/>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="code generation">
        <data key="variables">
            <variable name="sourcePath" value="/ListSched"/>
            <variable name="xslLibraryPath" value="/ListSched/Code/XSL"/>
        </data>
    </preesm:task>
    <preesm:dataTransfer from="__algorithm" sourceport="SDF"
        targetport="SDF" to="list scheduling"/>
    <preesm:dataTransfer from="__architecture" sourceport="architecture"
        targetport="architecture" to="list scheduling"/>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="list scheduling"/>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="__architecture"/>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="__algorithm"/>
    <preesm:dataTransfer from="list scheduling" sourceport="DAG"
        targetport="DAG" to="code generation"/>
    <preesm:dataTransfer from="__architecture" sourceport="architecture"
        targetport="architecture" to="code generation"/>
    <preesm:dataTransfer from="__scenario" sourceport="scenario"
        targetport="scenario" to="code generation"/>
</preesm:workflow>
