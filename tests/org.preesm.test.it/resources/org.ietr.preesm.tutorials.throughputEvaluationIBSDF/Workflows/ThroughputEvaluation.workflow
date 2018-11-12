<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task
        pluginId="org.ietr.preesm.experiment.pimm2sdf.StaticPiMM2SDFTask" taskId="StaticPiMM2SDF">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.throughput.ThroughputPlugin" taskId="ThroughputEvaluation">
        <dftools:data key="variables">
            <dftools:variable name="method" value="SR"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="StaticPiMM2SDF"/>
    <dftools:dataTransfer from="StaticPiMM2SDF" sourceport="SDF"
        targetport="SDF" to="ThroughputEvaluation"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="StaticPiMM2SDF"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="ThroughputEvaluation"/>
</dftools:workflow>
