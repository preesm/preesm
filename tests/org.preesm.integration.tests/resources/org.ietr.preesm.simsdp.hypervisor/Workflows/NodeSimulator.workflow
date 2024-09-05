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
    <dftools:task
        pluginId="org.ietr.preesm.stats.exporter.StatsExporterTask" taskId="Gantt Exporter">
        <dftools:data key="variables">
            <dftools:variable name="Multinode" value="true"/>
            <dftools:variable name="Top" value="false"/>
            <dftools:variable name="path" value="/Algo/generated/top"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="InternodeExporterTask.identifier" taskId="Internode Stats exporter">
        <dftools:data key="variables">
            <dftools:variable name="Folder Path" value="/Algo/generated/top"/>
            <dftools:variable name="SimGrid AG Path" value="SimGrid/install_simgag.sh"/>
            <dftools:variable name="SimGrid Path" value="SimGrid/install_simgrid.sh"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="RadarExporterTask.identifier" taskId="Multicriteria Stats exporter">
        <dftools:data key="variables"/>
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
        sourceport="ABC" targetport="ABC" to="Gantt Exporter"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Gantt Exporter"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="ABC" targetport="ABC" to="Internode Stats exporter"/>
    <dftools:dataTransfer from="Gantt Exporter" sourceport="void"
        targetport="void" to="Internode Stats exporter"/>
    <dftools:dataTransfer from="Internode Stats exporter"
        sourceport="void" targetport="void" to="Multicriteria Stats exporter"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="ABC" targetport="ABC" to="Multicriteria Stats exporter"/>
</dftools:workflow>
