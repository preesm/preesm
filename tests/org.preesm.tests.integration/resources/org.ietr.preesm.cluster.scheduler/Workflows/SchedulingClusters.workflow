<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="cluster-scheduler" taskId="Cluster Scheduler">
        <dftools:data key="variables">
            <dftools:variable name="Optimization criteria" value="Performance"/>
            <dftools:variable name="Target" value="Cluster"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="PiSDF-Exporter">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/generated/cluster/"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="Cluster Scheduler"
        sourceport="PiMM" targetport="PiMM" to="PiSDF-Exporter"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="Cluster Scheduler"/>
</dftools:workflow>
