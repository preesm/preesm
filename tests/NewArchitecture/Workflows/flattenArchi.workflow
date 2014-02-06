<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://org.ietr.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.ibsdfandslam"/>
    <dftools:task pluginId="org.ietr.preesm.archi.slam.flattening" taskId="flatten">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="S-LAM"
        targetport="S-LAM" to="flatten"/>
</dftools:workflow>
