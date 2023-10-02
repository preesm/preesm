<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="pisdf-srdag" taskId="PiMM2SrDAG">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-mapper.list" taskId="PiSDF Scheduling">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="True"/>
            <dftools:variable name="Optimize synchronization" value="True"/>
            <dftools:variable name="balanceLoads" value="true"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="simulatorType" value="AccuratelyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="NodeStatsExporterTask.identifier" taskId="CSV exporter">
        <dftools:data key="variables">
            <dftools:variable name="Multinode" value="false"/>
            <dftools:variable name="Top" value="true"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="PiMM2SrDAG"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM"
        targetport="PiMM" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="ABC" targetport="ABC" to="CSV exporter"/>
</dftools:workflow>
