<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="scape.scedule.task.identifier" taskId="APGAN">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="APGAN"/>
</dftools:workflow>
