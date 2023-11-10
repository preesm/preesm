<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="hypervisor.task.identifier" taskId="hypervisor">
        <dftools:data key="variables">
            <dftools:variable name="Deviation Target" value="1"/>
            <dftools:variable name="Latency Target" value="1"/>
            <dftools:variable name="Round" value="5"/>
            <dftools:variable name="SimGrid" value="false"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="DeviationChartTask.identifier" taskId="chart">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="void"
        targetport="void" to="hypervisor"/>
    <dftools:dataTransfer from="hypervisor" sourceport="void"
        targetport="void" to="chart"/>
</dftools:workflow>
