<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task
        pluginId="org.preesm.codegen.xtend.Spider2CodegenTask" taskId="SpiderCodegen">
        <dftools:data key="variables">
            <dftools:variable name="generate cmakelist" value="false"/>
            <dftools:variable name="move includes" value="false"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="SpiderCodegen"/>
</dftools:workflow>