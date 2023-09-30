<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="cluster-partitioner-loop2" taskId="loop">
        <dftools:data key="variables">
            <dftools:variable
                name="Number of PEs in compute clusters" value="3"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="loop"/>
</dftools:workflow>
