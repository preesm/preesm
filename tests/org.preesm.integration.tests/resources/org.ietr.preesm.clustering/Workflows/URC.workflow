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
    <dftools:task pluginId="mapper2.list" taskId="Mapping Scheduling">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="True"/>
            <dftools:variable name="Optimize synchronization" value="False"/>
            <dftools:variable name="balanceLoads" value="False"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="simulatorType" value="LooselyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="gantt-output" taskId="gantt">
        <dftools:data key="variables">
            <dftools:variable name="display" value="true"/>
            <dftools:variable name="file path" value="gantt"/>
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
            <dftools:variable name="Allocation heuristic" value="simple allocation"/>
            <dftools:variable name="Balancing heuristic" value="complete balancing"/>
            <dftools:variable name="Debug" value="false"/>
            <dftools:variable name="Horizontal heuristic" value="urc"/>
            <dftools:variable name="Mapping heuristic" value="classic mapper"/>
            <dftools:variable name="Scheduling heuristic" value="apgan scheduling"/>
            <dftools:variable name="Verbose" value="true"/>
            <dftools:variable name="Vertical heuristic" value=""/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="export clustered graph">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/generated/clustering/"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="clustering.codegen" taskId="Cluster codegen">
        <dftools:data key="variables">
            <dftools:variable name="Debug" value="false"/>
            <dftools:variable name="Printer" value="C"/>
            <dftools:variable name="Verbose" value="true"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="alloc2.megbuilder" taskId="MEG Builder">
        <dftools:data key="variables">
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="alloc2.megupdater" taskId="MEG Updater">
        <dftools:data key="variables">
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="alloc2.memoryscript" taskId="Memory Scripts">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="Thorough"/>
            <dftools:variable name="Data alignment" value="Fixed:=64"/>
            <dftools:variable name="False Sharing Prevention" value="True"/>
            <dftools:variable name="Log Path" value="log_memoryScripts"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="alloc2.memalloc" taskId="Memory Allocation">
        <dftools:data key="variables">
            <dftools:variable name="Allocator(s)" value="BestFit"/>
            <dftools:variable name="Best/First Fit order" value="LargestFirst"/>
            <dftools:variable name="Data alignment" value="Fixed:=64"/>
            <dftools:variable name="Distribution" value="MixedMerged"/>
            <dftools:variable name="Nb of Shuffling Tested" value="10"/>
            <dftools:variable name="Verbose" value="True"/>
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
    <dftools:dataTransfer from="Clustering" sourceport="clusters" targetport="clusters" to="Cluster codegen"/>
    <dftools:dataTransfer from="Clustering" sourceport="schedules" targetport="schedules" to="Cluster codegen"/>
    <dftools:dataTransfer from="Clustering" sourceport="allocations" targetport="allocations" to="Cluster codegen"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="Cluster codegen"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="MEG Builder"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="MEG Builder"/>
    <dftools:dataTransfer from="MEG Builder" sourceport="MemEx" targetport="MemEx" to="MEG Updater"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="MEG Updater"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Schedule" targetport="Schedule" to="MEG Updater"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Mapping" targetport="Mapping" to="MEG Updater"/>
    <dftools:dataTransfer from="MEG Updater" sourceport="MemEx" targetport="MemEx" to="Memory Scripts"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="Memory Scripts"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="Memory Scripts"/>
    <dftools:dataTransfer from="Memory Scripts" sourceport="MemEx" targetport="MemEx" to="Memory Allocation"/>
    <dftools:dataTransfer from="Mapping Scheduling" sourceport="Mapping" targetport="Mapping" to="Memory Allocation"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="Memory Allocation"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="Memory Allocation"/>
    <dftools:dataTransfer from="Memory Allocation" sourceport="Allocation" targetport="Allocation" to="gantt"/>
    <dftools:dataTransfer from="Memory Allocation" sourceport="Allocation" targetport="Allocation" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Memory Allocation"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="gantt"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Mapping Scheduling"/>
</dftools:workflow>
