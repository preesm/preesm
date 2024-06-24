<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="codegen2" taskId="Code Generation">
        <dftools:data key="variables">
            <dftools:variable name="Papify" value="false"/>
            <dftools:variable name="Printer" value="C"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-srdag" taskId="PiMM2SrDAG">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-synthesis.simple" taskId="Synthesis ">
        <dftools:data key="variables">
            <dftools:variable name="allocation" value="legacy"/>
            <dftools:variable name="scheduler" value="legacy"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Code Generation"/>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="Code Generation"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM"
        targetport="PiMM" to="Synthesis "/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="PiMM2SrDAG"/>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="Synthesis "/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Synthesis "/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM"
        targetport="PiMM" to="Code Generation"/>
    <dftools:dataTransfer from="Synthesis " sourceport="Schedule"
        targetport="Schedule" to="Code Generation"/>
    <dftools:dataTransfer from="Synthesis " sourceport="Mapping"
        targetport="Mapping" to="Code Generation"/>
    <dftools:dataTransfer from="Synthesis "
        sourceport="Allocation" targetport="Allocation" to="Code Generation"/>
</dftools:workflow>
