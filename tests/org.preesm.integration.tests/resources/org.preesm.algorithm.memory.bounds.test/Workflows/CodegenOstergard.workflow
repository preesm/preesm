<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow errorOnWarning="true" verboseLevel="INFO" xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="pisdf-mapper.list" taskId="Scheduling">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="true"/>
            <dftools:variable name="Optimize synchronization" value="False"/>
            <dftools:variable name="balanceLoads" value="true"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="fastLocalSearchTime" value="10"/>
            <dftools:variable name="fastTime" value="100"/>
            <dftools:variable name="iterationNr" value="0"/>
            <dftools:variable name="iterationPeriod" value="0"/>
            <dftools:variable name="listType" value="optimised"/>
            <dftools:variable name="simulatorType" value="AccuratelyTimed"/>
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
            <dftools:variable name="Allocator(s)" value="BestFit"/>
            <dftools:variable name="Best/First Fit order" value="Shuffle"/>
            <dftools:variable name="Data alignment" value="Fixed:=8"/>
            <dftools:variable name="Distribution" value="SharedOnly"/>
            <dftools:variable name="Merge broadcasts" value="True"/>
            <dftools:variable name="Nb of Shuffling Tested" value="10"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.codegen.xtend.task.CodegenTask" taskId="Code Generation">
        <dftools:data key="variables">
            <dftools:variable name="Papify" value="false"/>
            <dftools:variable name="Printer" value="C"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-srdag" taskId="pisdf-srdag">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.exclusiongraph.MemExUpdater" taskId="MEG Updater">
        <dftools:data key="variables">
            <dftools:variable name="Suppr Fork/Join" value="False"/>
            <dftools:variable
                name="Update with MemObject lifetime" value="False"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.bounds.MemoryBoundsEstimator" taskId="Memory Bounds Estimator">
        <dftools:data key="variables">
            <dftools:variable name="Solver" value="Ostergard"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.script.MemoryScriptTask" taskId="Memory Scripts">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="Thorough"/>
            <dftools:variable name="Data alignment" value="Fixed:=8"/>
            <dftools:variable name="False Sharing Prevention" value="False"/>
            <dftools:variable name="Log Path" value="log_memoryScripts"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.bounds.SerialMemoryBoundsEstimator" taskId="Serial Memory Bounds Estimator">
        <dftools:data key="variables">
            <dftools:variable name="Solver" value="Ostergard"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="Scheduling"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Scheduling"/>
    <dftools:dataTransfer from="Scheduling" sourceport="DAG"
        targetport="DAG" to="MEG Builder"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="MEG Builder"/>
    <dftools:dataTransfer from="Memory Allocation"
        sourceport="MEGs" targetport="MEGs" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Code Generation"/>
    <dftools:dataTransfer from="scenario"
        sourceport="architecture" targetport="architecture" to="Code Generation"/>
    <dftools:dataTransfer from="Scheduling" sourceport="DAG"
        targetport="DAG" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM"
        targetport="PiMM" to="pisdf-srdag"/>
    <dftools:dataTransfer from="MEG Builder" sourceport="MemEx"
        targetport="MemEx" to="MEG Updater"/>
    <dftools:dataTransfer from="MEG Builder" sourceport="MemEx"
        targetport="MemEx" to="Memory Bounds Estimator"/>
    <dftools:dataTransfer from="Scheduling" sourceport="DAG"
        targetport="DAG" to="MEG Updater"/>
    <dftools:dataTransfer from="MEG Updater" sourceport="MemEx"
        targetport="MemEx" to="Memory Scripts"/>
    <dftools:dataTransfer from="Memory Scripts"
        sourceport="MemEx" targetport="MemEx" to="Memory Allocation"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Memory Scripts"/>
    <dftools:dataTransfer from="Scheduling" sourceport="DAG"
        targetport="DAG" to="Memory Scripts"/>
    <dftools:dataTransfer from="pisdf-srdag" sourceport="PiMM"
        targetport="PiMM" to="Scheduling"/>
    <dftools:dataTransfer from="Memory Allocation"
        sourceport="MEGs" targetport="MEGs" to="Serial Memory Bounds Estimator"/>
</dftools:workflow>
