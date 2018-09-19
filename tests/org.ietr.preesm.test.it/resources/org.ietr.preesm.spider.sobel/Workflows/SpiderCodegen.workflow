<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task
        pluginId="org.ietr.preesm.pimm.algorithm.spider.codegen.SpiderCodegenTask" taskId="SpiderCodegen">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="SpiderCodegen"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="SpiderCodegen"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="SpiderCodegen"/>
</dftools:workflow>
