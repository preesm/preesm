<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="PipelineCycleInfoTask.identifier" taskId="dependency break identifier">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="PiSDF Export">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/generated"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="dependency break identifier"/>
    <dftools:dataTransfer from="dependency break identifier"
        sourceport="PiMM" targetport="PiMM" to="PiSDF Export"/>
</dftools:workflow>
