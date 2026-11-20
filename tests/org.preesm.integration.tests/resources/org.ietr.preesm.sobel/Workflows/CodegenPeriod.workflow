<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools" errorOnWarning="true" verboseLevel="INFO">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="Display Gantt">
        <dftools:data key="variables"/>
    </dftools:task>
    <dftools:task pluginId="pisdf-mapper.periodic.DAG" taskId="Periodic Scheduling from PiSDF to old DAG">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="True"/>
            <dftools:variable name="Optimize synchronization" value="False"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="simulatorType" value="AccuratelyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraphBuilder" taskId="MEG Builder">
        <dftools:data key="variables">
            <dftools:variable name="Suppr Fork/Join" value="False"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.memory.allocation.MemoryAllocatorTask" taskId="Memory Allocation">
        <dftools:data key="variables">
            <dftools:variable name="Allocator(s)" value="BestFit"/>
            <dftools:variable name="Best/First Fit order" value="LargestFirst"/>
            <dftools:variable name="Data alignment" value="Fixed:=64"/>
            <dftools:variable name="Distribution" value="MixedMerged"/>
            <dftools:variable name="Merge broadcasts" value="True"/>
            <dftools:variable name="Nb of Shuffling Tested" value="10"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.codegen.xtend.task.CodegenTask" taskId="Code Generation">
        <dftools:data key="variables">
            <dftools:variable name="Papify" value="false"/>
            <dftools:variable name="Printer" value="C"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.pimm.algorithm.checker.periods.PeriodsPreschedulingChecker" taskId="Periods Prescheduling Checker">
        <dftools:data key="variables">
            <dftools:variable name="Selection rate (%)" value="100"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.memory.exclusiongraph.MemExUpdater" taskId="MEG Updater">
        <dftools:data key="variables">
            <dftools:variable name="Suppr Fork/Join" value="False"/>
            <dftools:variable name="Update with MemObject lifetime" value="False"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.memory.bounds.MemoryBoundsEstimator" taskId="Memory Bounds Estimator">
        <dftools:data key="variables">
            <dftools:variable name="Solver" value="Heuristic"/>
            <dftools:variable name="Verbose" value="False"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.memory.script.MemoryScriptTask" taskId="Memory Scripts">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="Thorough"/>
            <dftools:variable name="Data alignment" value="Fixed:=64"/>
            <dftools:variable name="False Sharing Prevention" value="True"/>
            <dftools:variable name="Log Path" value="log_memoryScripts"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="pisdf-srdag" taskId="pisdf-srdag">
        <dftools:data key="variables">
            <dftools:variable name="Consistency_Method" value="LCM"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="Display Gantt"/>
    <dftools:dataTransfer from="Periodic Scheduling from PiSDF to old DAG" sourceport="ABC" targetport="ABC" to="Display Gantt"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Periodic Scheduling from PiSDF to old DAG"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="Periodic Scheduling from PiSDF to old DAG"/>
    <dftools:dataTransfer from="Periodic Scheduling from PiSDF to old DAG" sourceport="DAG" targetport="DAG" to="MEG Builder"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="MEG Builder"/>
    <dftools:dataTransfer from="Memory Allocation" sourceport="MEGs" targetport="MEGs" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="Code Generation"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Code Generation"/>
    <dftools:dataTransfer from="Periodic Scheduling from PiSDF to old DAG" sourceport="DAG" targetport="DAG" to="Code Generation"/>
    <dftools:dataTransfer from="MEG Builder" sourceport="MemEx" targetport="MemEx" to="MEG Updater"/>
    <dftools:dataTransfer from="MEG Builder" sourceport="MemEx" targetport="MemEx" to="Memory Bounds Estimator"/>
    <dftools:dataTransfer from="Periodic Scheduling from PiSDF to old DAG" sourceport="DAG" targetport="DAG" to="MEG Updater"/>
    <dftools:dataTransfer from="MEG Updater" sourceport="MemEx" targetport="MemEx" to="Memory Scripts"/>
    <dftools:dataTransfer from="Memory Scripts" sourceport="MemEx" targetport="MemEx" to="Memory Allocation"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="Memory Scripts"/>
    <dftools:dataTransfer from="Periodic Scheduling from PiSDF to old DAG" sourceport="DAG" targetport="DAG" to="Memory Scripts"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="Periods Prescheduling Checker"/>
    <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture" to="Periods Prescheduling Checker"/>
    <dftools:dataTransfer from="scenario" sourceport="PiMM" targetport="PiMM" to="Periods Prescheduling Checker"/>
    <dftools:dataTransfer from="pisdf-srdag" sourceport="PiMM" targetport="PiMM" to="Periodic Scheduling from PiSDF to old DAG"/>
    <dftools:dataTransfer from="Periods Prescheduling Checker" sourceport="PiMM" targetport="PiMM" to="pisdf-srdag"/>
</dftools:workflow>
