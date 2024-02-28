<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="node.partitioner.task.identifier" taskId="NodePartitioner">
        <dftools:data key="variables">
            <dftools:variable name="Partitioning mode" value="equivalentTimed"/>
<<<<<<< HEAD
            <dftools:variable name="archi name" value="SimSDP_node.csv"/>
=======
            <dftools:variable name="archi path" value="SimSDP_node.csv"/>
>>>>>>> 6bf9253c0ec5bb1c8e2fde4dd76200b472d940d7
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="NodePartitioner"/>
</dftools:workflow>
