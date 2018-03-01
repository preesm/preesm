<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
        <dftools:data key="variables">
            <dftools:variable name="depth" value="1"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.experiment.pimm2sdf.StaticPiMM2SDFTask" taskId="StaticPiMM2SDF">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="fi.abo.preesm.dataparallel.DataParallel" taskId="Data-Parallel Check">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="CySDF Exporter">
        <dftools:data key="variables">
            <dftools:variable name="path" value="Algo/generated/cySDF"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="StaticPiMM2SDF"/>
    <dftools:dataTransfer from="StaticPiMM2SDF" sourceport="SDF"
        targetport="SDF" to="HierarchyFlattening"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="StaticPiMM2SDF"/>
    <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF"
        targetport="SDF" to="Data-Parallel Check"/>
    <dftools:dataTransfer from="Data-Parallel Check" sourceport="CySDF"
        targetport="SDF" to="CySDF Exporter"/>
</dftools:workflow>
