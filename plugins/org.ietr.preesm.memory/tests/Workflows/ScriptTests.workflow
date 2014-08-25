<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:dftools="http://net.sf.dftools">
    <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.mapper.listscheduling" taskId="LIST scheduler">
        <dftools:data key="variables">
            <dftools:variable name="balanceLoads" value="true"/>
            <dftools:variable name="displaySolutions" value="true"/>
            <dftools:variable name="edgeSchedType" value="Simple"/>
            <dftools:variable name="simulatorType" value="LooselyTimed"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="srSDF">
        <dftools:data key="variables">
            <dftools:variable name="ExplodeImplodeSuppr" value="false"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
        <dftools:data key="variables">
            <dftools:variable name="depth" value="1"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
        <dftools:data key="variables">
            <dftools:variable name="openFile" value="false"/>
            <dftools:variable name="path" value="DAG/singlerate.graphml"/>
        </dftools:data>
    </dftools:task>
    <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter2">
        <dftools:data key="variables">
            <dftools:variable name="openFile" value="false"/>
            <dftools:variable name="path" value="DAG/flatten.graphml"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.mapper.exporter.DAGExportTransform" taskId="DAGExporter">
        <dftools:data key="variables">
            <dftools:variable name="openFile" value="false"/>
            <dftools:variable name="path" value="DAG/dag.graphml"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraphBuilder" taskId="MemEx Builder">
        <dftools:data key="variables">
            <dftools:variable name="Suppr Fork/Join" value="False"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.allocation.MemoryAllocatorTask" taskId="Mem Alloc">
        <dftools:data key="variables">
            <dftools:variable name="Allocator(s)" value="Basic"/>
            <dftools:variable name="Best/First Fit order" value="LargestFirst"/>
            <dftools:variable name="Data alignment" value="None"/>
            <dftools:variable name="Merge broadcasts" value="False"/>
            <dftools:variable name="Nb of Shuffling Tested" value="10"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.script.MemoryScriptTask" taskId="Scripts">
        <dftools:data key="variables">
            <dftools:variable name="Check" value="Thorough"/>
            <dftools:variable name="Data alignment" value="None"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.allocation.MemoryAllocatorTask" taskId="Mem Alloc 2">
        <dftools:data key="variables">
            <dftools:variable name="Allocator(s)" value="Basic"/>
            <dftools:variable name="Best/First Fit order" value="LargestFirst"/>
            <dftools:variable name="Data alignment" value="None"/>
            <dftools:variable name="Merge broadcasts" value="False"/>
            <dftools:variable name="Nb of Shuffling Tested" value="10"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraphBuilder" taskId="MemEx Builder1">
        <dftools:data key="variables">
            <dftools:variable name="Suppr Fork/Join" value="False"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.exclusiongraph.MemExUpdater" taskId="MemEx Updater">
        <dftools:data key="variables">
            <dftools:variable name="Suppr Fork/Join" value="False"/>
            <dftools:variable name="Update with MemObject lifetime" value="False"/>
            <dftools:variable name="Verbose" value="True"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.bounds.MemoryBoundsEstimator" taskId="Mem Bounds 0">
        <dftools:data key="variables">
            <dftools:variable name="Solver" value="Heuristic"/>
            <dftools:variable name="Verbose" value="? C {True, False}"/>
        </dftools:data>
    </dftools:task>
    <dftools:task
        pluginId="org.ietr.preesm.memory.bounds.MemoryBoundsEstimator" taskId="Mem Bounds 1">
        <dftools:data key="variables">
            <dftools:variable name="Solver" value="Heuristic"/>
            <dftools:variable name="Verbose" value="? C {True, False}"/>
        </dftools:data>
    </dftools:task>
    <dftools:dataTransfer from="scenario" sourceport="architecture"
        targetport="architecture" to="LIST scheduler"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="LIST scheduler"/>
    <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF"
        targetport="SDF" to="srSDF"/>
    <dftools:dataTransfer from="srSDF" sourceport="SDF" targetport="SDF" to="Exporter"/>
    <dftools:dataTransfer from="srSDF" sourceport="SDF" targetport="SDF" to="LIST scheduler"/>
    <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF"
        targetport="SDF" to="Exporter2"/>
    <dftools:dataTransfer from="LIST scheduler" sourceport="DAG"
        targetport="DAG" to="DAGExporter"/>
    <dftools:dataTransfer from="LIST scheduler" sourceport="DAG"
        targetport="DAG" to="MemEx Builder"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="MemEx Builder"/>
    <dftools:dataTransfer from="MemEx Builder" sourceport="MemEx"
        targetport="MemEx" to="Mem Alloc"/>
    <dftools:dataTransfer from="scenario" sourceport="SDF"
        targetport="SDF" to="HierarchyFlattening"/>
    <dftools:dataTransfer from="LIST scheduler" sourceport="DAG"
        targetport="DAG" to="Scripts"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="Scripts"/>
    <dftools:dataTransfer from="Scripts" sourceport="MemEx"
        targetport="MemEx" to="Mem Alloc 2"/>
    <dftools:dataTransfer from="LIST scheduler" sourceport="DAG"
        targetport="DAG" to="MemEx Builder1"/>
    <dftools:dataTransfer from="scenario" sourceport="scenario"
        targetport="scenario" to="MemEx Builder1"/>
    <dftools:dataTransfer from="MemEx Builder1" sourceport="MemEx"
        targetport="MemEx" to="MemEx Updater"/>
    <dftools:dataTransfer from="MemEx Updater" sourceport="MemEx"
        targetport="MemEx" to="Scripts"/>
    <dftools:dataTransfer from="LIST scheduler" sourceport="DAG"
        targetport="DAG" to="MemEx Updater"/>
    <dftools:dataTransfer from="MemEx Builder" sourceport="MemEx"
        targetport="MemEx" to="Mem Bounds 0"/>
    <dftools:dataTransfer from="Scripts" sourceport="MemEx"
        targetport="MemEx" to="Mem Bounds 1"/>
    <dftools:dataTransfer from="Mem Bounds 0" sourceport="void"
        targetport="void" to="Mem Alloc"/>
    <dftools:dataTransfer from="Mem Bounds 1" sourceport="void"
        targetport="void" to="Mem Alloc 2"/>
</dftools:workflow>
