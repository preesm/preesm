<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools" errorOnWarning="true" verboseLevel="INFO">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="heterogeneous-synthesis" taskId="Synthesis ">
        <dftools:data key="variables">
            <dftools:variable name="Cycle max" value="100000"/>
            <dftools:variable name="K value" value="1"/>
            <dftools:variable name="allocation" value="legacy"/>
            <dftools:variable name="clusterize" value="true"/>
            <dftools:variable name="fpgascheduler" value="constraintscheduler"/>
            <dftools:variable name="scheduler" value="legacy"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="PiSDF Exporter">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/generated/pisdf/"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-srdag" taskId="PiMM2SrDAG">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="clustering" taskId="Clustering">
        <dftools:data key="variables">
            <dftools:variable name="clusterize" value="true"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="SRDAG Exporter">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/generated/pisdf/"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Synthesis "/>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="Clustering"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Clustering"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM" targetport="PiMM" to="Clustering"/>
    <dftools:dataTransfer from="Clustering" sourceport="subgraphs" targetport="subgraphs" to="Synthesis "/>
    <dftools:dataTransfer from="Clustering" sourceport="PiMM" targetport="PiMM" to="PiMM2SrDAG"/>
    <dftools:dataTransfer from="Clustering" sourceport="scenario" targetport="scenario" to="Synthesis "/>
    <dftools:dataTransfer from="Clustering" sourceport="PiMM" targetport="PiMM" to="PiSDF Exporter"/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="Synthesis "/>
    <dftools:dataTransfer from="PiMM2SrDAG" sourceport="PiMM" targetport="PiMM" to="SRDAG Exporter"/>
</dftools:workflow>
