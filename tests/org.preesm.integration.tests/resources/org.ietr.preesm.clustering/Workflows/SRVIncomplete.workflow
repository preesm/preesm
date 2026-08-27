<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools" errorOnWarning="false" verboseLevel="INFO">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="pisdf-srdag" taskId="PiMM2SrDAG">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
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
            <dftools:variable name="Horizontal heuristic" value="srv"/>
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
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="export dag"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="Clustering"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Clustering"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM" targetport="PiMM" to="Clustering"/>
    <dftools:dataTransfer from="Clustering" sourceport="PiMM" targetport="PiMM" to="PiMM2SrDAG"/>
    <dftools:dataTransfer from="Clustering" sourceport="PiMM" targetport="PiMM" to="export clustered graph"/>
    <dftools:dataTransfer from="Clustering" sourceport="clusters" targetport="clusters" to="Cluster codegen"/>
    <dftools:dataTransfer from="Clustering" sourceport="schedules" targetport="schedules" to="Cluster codegen"/>
    <dftools:dataTransfer from="Clustering" sourceport="allocations" targetport="allocations" to="Cluster codegen"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="Cluster codegen"/>
</dftools:workflow>
