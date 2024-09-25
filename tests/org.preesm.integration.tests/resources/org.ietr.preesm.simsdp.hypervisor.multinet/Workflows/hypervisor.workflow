<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="hypervisor.task.identifier" taskId="hypervisor">
        <dftools:data key="variables">
            <dftools:variable name="Iteration" value="1"/>
            <dftools:variable name="Multinet" value="true"/>
            <dftools:variable name="archi path" value="/Scenarios/initialisation.scenario"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="void"
        targetport="void" to="hypervisor"/>
</dftools:workflow>
