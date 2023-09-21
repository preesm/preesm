<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="node.partitioner.task.identifier" taskId="Node Partitioner">
        <dftools:data key="variables">
            <dftools:variable name="archi path" value="/home/orenaud/preesm_dev/preesm/tests/org.preesm.integration.tests/resources/org.ietr.preesm.simsdp/Archi/H_3node_3.3.3_f0.csv"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Node Partitioner"/>
</dftools:workflow>
