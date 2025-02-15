<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="scape.task.identifier" taskId="SCAPE">
        <dftools:data key="variables">
            <dftools:variable name="Level number" value="1"/>
            <dftools:variable name="Memory optimization" value="False"/>
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
        pluginId="org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraphBuilder" taskId="MEG Builder">
        <dftools:data key="variables">
            <dftools:variable name="Suppr Fork/Join" value="False"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="Gantt Display">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="IntranodeExporterTask.identifier" taskId="Intranode Stats exporter">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="TopTimingExporterTask.identifier" taskId="Top Timing Exporter">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="PiSDF Export">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/scape"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="SCAPE"/>
    <dftools:dataTransfer from="SCAPE" sourceport="PiMM"
        targetport="PiMM" to="PiMM2SrDAG"/>
    <dftools:dataTransfer from="SCAPE" sourceport="scenario"
        targetport="scenario" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM"
        targetport="PiMM" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="DAG" targetport="DAG" to="MEG Builder"/>
    <dftools:dataTransfer from="SCAPE" sourceport="scenario"
        targetport="scenario" to="MEG Builder"/>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="PiSDF Scheduling"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="ABC" targetport="ABC" to="Gantt Display"/>
    <dftools:dataTransfer from="SCAPE" sourceport="scenario"
        targetport="scenario" to="Gantt Display"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="ABC" targetport="ABC" to="Intranode Stats exporter"/>
    <dftools:dataTransfer from="PiSDF Scheduling"
        sourceport="ABC" targetport="ABC" to="Top Timing Exporter"/>
    <dftools:dataTransfer from="SCAPE" sourceport="cMem"
        targetport="cMem" to="Intranode Stats exporter"/>
    <dftools:dataTransfer from="SCAPE" sourceport="PiMM"
        targetport="PiMM" to="PiSDF Export"/>
</dftools:workflow>
