<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="euclide.transfo.task.identifier" taskId="euclide">
        <dftools:data key="variables">
            <dftools:variable name="Level number" value="1"/>
            <dftools:variable name="SCAPE mode" value="2"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="euclide"/>
</dftools:workflow>
