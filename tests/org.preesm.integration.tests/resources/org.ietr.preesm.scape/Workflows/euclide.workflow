<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="euclide.transfo.task.identifier" taskId="euclide">
        <dftools:data key="variables">
            <dftools:variable name="Level number" value="1"/>
            <dftools:variable name="SCAPE mode" value="2"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-export" taskId="PiSDF Export">
        <dftools:data key="variables">
            <dftools:variable name="hierarchical" value="true"/>
            <dftools:variable name="path" value="/Algo/scape"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="euclide"/>
    <dftools:dataTransfer from="euclide" sourceport="PiMM"
        targetport="PiMM" to="PiSDF Export"/>
</dftools:workflow>
