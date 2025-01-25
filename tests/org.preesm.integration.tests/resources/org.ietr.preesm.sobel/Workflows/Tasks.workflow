<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="pisdf-mapper.list" taskId="Scheduling">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="True"/>
            <dftools:variable name="Optimize synchronization" value="false"/>
            <dftools:variable name="balanceLoads" value="true"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="fastLocalSearchTime" value="10"/>
            <dftools:variable name="fastTime" value="100"/>
            <dftools:variable name="iterationNr" value="0"/>
            <dftools:variable name="iterationPeriod" value="0"/>
            <dftools:variable name="listType" value="optimised"/>
            <dftools:variable name="simulatorType" value="LooselyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-srdag" taskId="PiMM to SRDAG">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.algorithm.moa.activity.ActivityExporter" taskId="ActivityExporter">
        <dftools:data key="variables">
            <dftools:variable name="human_readable" value="Yes"/>
            <dftools:variable name="path" value="stats/mat/activity"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.algorithm.moa.activity.ActivityExporter" taskId="ActivityExporter2">
        <dftools:data key="variables">
            <dftools:variable name="human_readable" value="no"/>
            <dftools:variable name="path" value="stats/mat/activity"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.algorithm.moa.activity.ActivityExporter" taskId="MonoActivityExporter">
        <dftools:data key="variables">
            <dftools:variable name="human_readable" value="Yes"/>
            <dftools:variable name="path" value="stats/mat/activity"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.algorithm.moa.activity.ActivityExporter" taskId="MonoActivityExporter2">
        <dftools:data key="variables">
            <dftools:variable name="human_readable" value="no"/>
            <dftools:variable name="path" value="stats/mat/activity"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-brv-export" taskId="PiSDF BRV Exporter">
        <dftools:data key="variables">
            <dftools:variable name="path" value="/stat/xml/"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="Scheduling"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Scheduling"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="PiMM to SRDAG"/>
    <dftools:dataTransfer from="PiMM to SRDAG" sourceport="PiMM"
        targetport="PiMM" to="Scheduling"/>
    <dftools:dataTransfer from="Scheduling" sourceport="ABC"
        targetport="ABC" to="ActivityExporter"/>
    <dftools:dataTransfer from="Scheduling" sourceport="ABC"
        targetport="ABC" to="ActivityExporter2"/>
    <dftools:dataTransfer from="Scheduling" sourceport="ABC"
        targetport="ABC" to="MonoActivityExporter"/>
    <dftools:dataTransfer from="Scheduling" sourceport="ABC"
        targetport="ABC" to="MonoActivityExporter2"/>
    <dftools:dataTransfer from="PiMM to SRDAG" sourceport="PiMM"
        targetport="PiMM" to="PiSDF BRV Exporter"/>
</dftools:workflow>
