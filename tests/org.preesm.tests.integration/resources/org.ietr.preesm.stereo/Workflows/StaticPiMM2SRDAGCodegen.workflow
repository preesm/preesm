<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task
        pluginId="org.ietr.preesm.experiment.pimm2srdag.StaticPiMM2SrDAGTask" taskId="PiMM2SrDaGTask">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.mapper.exporter.DAGExportTransform" taskId="DAG Exporter">
        <dftools:data key="variables">
            <dftools:variable name="openFile" value="false"/>
            <dftools:variable name="path" value="Algo/generated/dag/dag.graphml"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.mapper.listschedulingfromdag" taskId="Scheduling">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="true"/>
            <dftools:variable name="Optimize synchronization" value="False"/>
            <dftools:variable name="balanceLoads" value="true"/>
            <dftools:variable name="displaySolutions" value="true"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="fastLocalSearchTime" value="10"/>
            <dftools:variable name="fastTime" value="100"/>
            <dftools:variable name="iterationNr" value="0"/>
            <dftools:variable name="iterationPeriod" value="0"/>
            <dftools:variable name="listType" value="optimised"/>
            <dftools:variable name="simulatorType" value="LooselyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="Display Gantt">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.codegen.xtend.task.CodegenTask" taskId="Code Generation">
        <dftools:data key="variables">
            <dftools:variable name="Printer" value="C"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraphBuilder" taskId="MEG Builder">
        <dftools:data key="variables">
            <dftools:variable name="Suppr Fork/Join" value="False"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.allocation.MemoryAllocatorTask" taskId="Memory Allocation">
        <dftools:data key="variables">
            <dftools:variable name="Allocator(s)" value="Basic"/>
            <dftools:variable name="Best/First Fit order" value="LargestFirst"/>
            <dftools:variable name="Data alignment" value="None"/>
            <dftools:variable name="Distribution" value="SharedOnly"/>
            <dftools:variable name="Merge broadcasts" value="True"/>
            <dftools:variable name="Nb of Shuffling Tested" value="10"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="PiMM2SrDaGTask"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="PiMM2SrDaGTask"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Scheduling"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="Scheduling"/>
    <dftools:dataTransfer from="Scheduling" sourceport="DAG"
        targetport="DAG" to="DAG Exporter"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="PiMM2SrDaGTask"/>
    <dftools:dataTransfer from="Scheduling" sourceport="ABC"
        targetport="ABC" to="Display Gantt"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Display Gantt"/>
    <dftools:dataTransfer from="DAG Exporter" sourceport="void"
        targetport="void" to="MEG Builder"/>
    <dftools:dataTransfer from="MEG Builder" sourceport="MemEx"
        targetport="MemEx" to="Memory Allocation"/>
    <dftools:dataTransfer from="Scheduling" sourceport="DAG"
        targetport="DAG" to="MEG Builder"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="MEG Builder"/>
    <dftools:dataTransfer from="Scheduling" sourceport="DAG"
        targetport="DAG" to="Code Generation"/>
    <dftools:dataTransfer from="Memory Allocation" sourceport="MEGs"
        targetport="MEGs" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="Code Generation"/>
    <dftools:dataTransfer from="PiMM2SrDaGTask" sourceport="DAG"
        targetport="DAG" to="Scheduling"/>
</dftools:workflow>
