<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="scape.task.identifier" taskId="SCAPE1">
        <dftools:data key="variables">
            <dftools:variable name="Level number" value="1"/>
            <dftools:variable name="Non-cluster actor" value=""/>
            <dftools:variable name="SCAPE mode" value="0"/>
            <dftools:variable name="Stack size" value="1000000"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="SCAPE1"/>
</dftools:workflow>
