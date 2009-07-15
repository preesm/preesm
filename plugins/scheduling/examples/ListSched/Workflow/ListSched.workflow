<?xml version="1.0" encoding="UTF-8"?>
<preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
    <preesm:algorithm/>
    <preesm:architecture/>
    <preesm:scenario/>
    <preesm:task pluginId="org.ietr.preesm.plugin.scheduling.listsched" taskId="list scheduling">
        <data key="variables"/>
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
</preesm:workflow>
