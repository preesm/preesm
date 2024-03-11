<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="SimSDPnetworkTask.identifier" taskId="SimSDP network">
        <dftools:data key="variables">
            <dftools:variable name="config ID" value="2"/>
            <dftools:variable name="core frequency" value="2"/>
            <dftools:variable name="number of cores" value="3"/>
            <dftools:variable name="number of nodes" value="2"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="void"
        targetport="void" to="SimSDP network"/>
</dftools:workflow>
