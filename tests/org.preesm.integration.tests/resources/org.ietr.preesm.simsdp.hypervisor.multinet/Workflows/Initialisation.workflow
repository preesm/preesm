<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="scape.task.identifier" taskId="SCAPE">
        <dftools:data key="variables">
            <dftools:variable name="Level number" value="1"/>
            <dftools:variable name="Non-cluster actor" value=""/>
            <dftools:variable name="SCAPE mode" value="2"/>
            <dftools:variable name="Stack size" value="1000000"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-srdag" taskId="PiMM2SrDAG">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-mapper.list" taskId="PiSDF Scheduling">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="true"/>
            <dftools:variable name="Optimize synchronization" value="true"/>
            <dftools:variable name="balanceLoads" value="true"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="simulatorType" value="approximatelyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="InitialisationExporterTask.identifier" taskId="Initialisation Stats exporter">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="RadarExporterTask.identifier" taskId="Multicriteria Stats exporter">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="SCAPE"/>
    <dftools:dataTransfer from="SCAPE" sourceport="PiMM"
        targetport="PiMM" to="PiMM2SrDAG"/>
    <dftools:dataTransfer from="SCAPE" sourceport="scenario"
        targetport="scenario" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM"
        targetport="PiMM" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="ABC" targetport="ABC" to="Initialisation Stats exporter"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="ABC" targetport="ABC" to="Multicriteria Stats exporter"/>
    <dftools:dataTransfer from="Initialisation Stats exporter"
        sourceport="void" targetport="void" to="Multicriteria Stats exporter"/>
</dftools:workflow>
