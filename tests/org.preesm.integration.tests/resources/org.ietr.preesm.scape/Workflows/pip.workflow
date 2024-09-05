<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="cluster-partitioner-PIP" taskId="Pipeline Partitioner">
        <dftools:data key="variables">
            <dftools:variable name="Non-cluster actor" value=""/>
            <dftools:variable
                name="Number of PEs in compute clusters" value="3"/>
            <dftools:variable name="SCAPE mode" value="0"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="PiSDF Export">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/scape"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Pipeline Partitioner"/>
    <dftools:dataTransfer from="Pipeline Partitioner"
        sourceport="PiMM" targetport="PiMM" to="PiSDF Export"/>
</dftools:workflow>
