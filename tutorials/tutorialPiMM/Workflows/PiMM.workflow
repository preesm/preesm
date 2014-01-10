<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.experiment.model.transformation.PiMMAndS-LAM"/>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.architransfo.transforms.ArchitectureExporter" taskId="ArchiExporter">
        <dftools:data key="variables">
            <dftools:variable name="path" value=""/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.experiment.model.transformation.taskExpression" taskId="PiMM2IBSDF">
        <dftools:data key="variables">
            <dftools:variable name="size" value="2"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf.task.export2c.ExportToC" taskId="Export2C">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="ArchiExporter"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="PiMM2IBSDF"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiSDF" to="Export2C"/>
</dftools:workflow>
