<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools" errorOnWarning="false" verboseLevel="INFO">
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
    <dftools:task pluginId="mapper2.hybrid" taskId="Mapping Scheduling">
        <dftools:data key="variables">
            <dftools:variable name="Bottom Scheduler" value="APGAN"/>
            <dftools:variable name="Check" value="True"/>
            <dftools:variable name="Optimize synchronization" value="False"/>
            <dftools:variable name="Top Threshold" value="1"/>
            <dftools:variable name="balanceLoads" value="False"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="simulatorType" value="LooselyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="gantt-output" taskId="gantt">
        <dftools:data key="variables">
            <dftools:variable name="display" value="true"/>
            <dftools:variable name="file path" value="/gantt"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="export dag">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/generated/clustering/srdag/"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="clustering.creation" taskId="Clustering">
        <dftools:data key="variables">
            <dftools:variable name="Balancing heuristic" value="complete balancing"/>
            <dftools:variable name="Horizontal heuristic" value="srv"/>
            <dftools:variable name="Verbose" value="true"/>
            <dftools:variable name="Vertical heuristic" value="None"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="export clustered graph">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/generated/clustering/"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="alloc2.hybrid" taskId="Allocation">
        <dftools:data key="variables">
            <dftools:variable name="Allocator(s)" value="BestFit"/>
            <dftools:variable name="Best/First Fit order" value="LargestFirst"/>
            <dftools:variable name="Bot Allocator" value="simple"/>
            <dftools:variable name="Check" value="Thorough"/>
            <dftools:variable name="Data alignment" value="Fixed:=8"/>
            <dftools:variable name="Distribution" value="MixedMerged"/>
            <dftools:variable name="False Sharing Prevention" value="False"/>
            <dftools:variable name="Log Path" value="log_memoryScripts"/>
            <dftools:variable name="Nb of Shuffling Tested" value="10"/>
            <dftools:variable name="Run memory scripts" value="True"/>
            <dftools:variable name="Top Threshold" value="1"/>
            <dftools:variable name="Update MEG" value="True"/>
            <dftools:variable name="Verbose" value="? C {True, False}"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="Mapping Scheduling"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="Code Generation"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Schedule" targetport="Schedule" to="Code Generation"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Mapping" targetport="Mapping" to="Code Generation"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Schedule" targetport="Schedule" to="gantt"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Mapping" targetport="Mapping" to="gantt"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="export dag"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="gantt"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="Clustering"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Clustering"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM" targetport="PiMM" to="Clustering"/>
    <dftools:dataTransfer from="Clustering" sourceport="PiMM" targetport="PiMM" to="PiMM2SrDAG"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="Mapping Scheduling"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="gantt"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="Code Generation"/>
    <dftools:dataTransfer from="Clustering" sourceport="PiMM" targetport="PiMM" to="export clustered graph"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="gantt"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Mapping Scheduling"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Schedule" targetport="Schedule" to="Allocation"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Mapping" targetport="Mapping" to="Allocation"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Allocation"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="Allocation"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="Allocation"/>
    <dftools:dataTransfer from="Allocation" sourceport="Allocation" targetport="Allocation" to="Code Generation"/>
    <dftools:dataTransfer from="Allocation" sourceport="Allocation" targetport="Allocation" to="gantt"/>
</dftools:workflow>
