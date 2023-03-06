<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="false" verboseLevel="FINE" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="pisdf-synthesis.fpga-estimations" taskId="FPGA analysis">
        <dftools:data key="variables">
            <dftools:variable name="Fifo evaluator: " value="adfgFifoEvalExact"/>
            <dftools:variable name="Pack tokens ?" value="false"/>
            <dftools:variable name="Show schedule ?" value="false"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="FPGA analysis"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="FPGA analysis"/>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="FPGA analysis"/>
</dftools:workflow>
