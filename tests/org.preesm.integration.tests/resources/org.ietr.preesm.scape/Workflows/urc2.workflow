<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="cluster-partitioner-URC" taskId="urc2">
        <dftools:data key="variables">
            <dftools:variable name="Non-cluster actor" value=""/>
            <dftools:variable
                name="Number of PEs in compute clusters" value="3"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="urc2"/>
</dftools:workflow>
