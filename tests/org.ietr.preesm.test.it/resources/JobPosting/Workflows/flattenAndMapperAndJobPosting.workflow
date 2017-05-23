<?xml version="1.0" encoding="UTF-8"?>
<preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
    <preesm:algorithm/>
    <preesm:architecture/>
    <preesm:scenario/>
    <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
        <data key="variables">
            <variable name="jobPosting" value="true"/>
            <variable name="sourcePath" value="/JobPosting/Code"/>
            <variable name="timedSimulation" value="false"/>
            <variable name="xslLibraryPath" value="/JobPosting/Code/XSL"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
        <data key="variables"/>
    </preesm:task>
    <preesm:task
        pluginId="org.ietr.preesm.plugin.mapper.exporter.ImplExportTransform" taskId="ImplementationExporter">
        <data key="variables">
            <variable name="path" value="/JobPosting/DAG/outDAG.xml"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
        <data key="variables">
            <variable name="displaySolutions" value="false"/>
            <variable name="edgeSchedType" value="Switcher"/>
            <variable name="margIn" value="100"/>
            <variable name="maxCount" value="200"/>
            <variable name="maxStep" value="200"/>
            <variable name="nodesMin" value="5"/>
            <variable name="procNumber" value="1"/>
            <variable name="simulatorType" value="AccuratelyTimed"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
        <data key="variables">
            <variable name="path" value="/JobPosting/DAG/flatten.graphml"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
        <data key="variables"/>
    </preesm:task>
    <preesm:task
        pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
        <data key="variables">
            <variable name="depth" value="2"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.XsltTransform" taskId="jobListGen">
        <data key="variables">
            <variable name="inputFile" value="/JobPosting/Code/JobPosting/Visual/Queues/generated/jobList.xml"/>
            <variable name="outputFile" value="/JobPosting/Code/JobPosting/Visual/Queues/generated/jobList.h"/>
            <variable name="xslFile" value="/JobPosting/Code/XSL/jobList.xslt"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.XsltTransform" taskId="jobDefinesGen">
        <data key="variables">
            <variable name="inputFile" value="/JobPosting/Code/JobPosting/Visual/Queues/generated/jobList.xml"/>
            <variable name="outputFile" value="/JobPosting/Code/JobPosting/Visual/Queues/generated/jobDefines.h"/>
            <variable name="xslFile" value="/JobPosting/Code/XSL/jobDefines.xslt"/>
        </data>
    </preesm:task>
    <preesm:task pluginId="org.ietr.preesm.XsltTransform" taskId="bufferAllocGen">
        <data key="variables">
            <variable name="inputFile" value="/JobPosting/Code/JobPosting/Visual/Queues/generated/jobList.xml"/>
            <variable name="outputFile" value="/JobPosting/Code/JobPosting/Visual/Queues/generated/jobBuffers.h"/>
            <variable name="xslFile" value="/JobPosting/Code/XSL/jobBuffers.xslt"/>
        </data>
    </preesm:task>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="DAG Plotter"/>
    <preesm:dataTransfer from="__architecture" sourceport=""
        targetport="architecture" to="codegen"/>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="__algorithm"/>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="__architecture"/>
    <preesm:dataTransfer from="__algorithm" sourceport=""
        targetport="SDF" to="ImplementationExporter"/>
    <preesm:dataTransfer from="__architecture" sourceport=""
        targetport="architecture" to="ImplementationExporter"/>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="ImplementationExporter"/>
    <preesm:dataTransfer from="FAST scheduler" sourceport="DAG"
        targetport="DAG" to="codegen"/>
    <preesm:dataTransfer from="FAST scheduler" sourceport="DAG"
        targetport="DAG" to="ImplementationExporter"/>
    <preesm:dataTransfer from="FAST scheduler" sourceport="ABC"
        targetport="ABC" to="DAG Plotter"/>
    <preesm:dataTransfer from="__architecture" sourceport=""
        targetport="architecture" to="FAST scheduler"/>
    <preesm:dataTransfer from="__scenario" sourceport=""
        targetport="scenario" to="FAST scheduler"/>
    <preesm:dataTransfer from="__algorithm" sourceport=""
        targetport="SDF" to="HierarchyFlattening"/>
    <preesm:dataTransfer from="HierarchyFlattening" sourceport="SDF"
        targetport="SDF" to="HSDF"/>
    <preesm:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="FAST scheduler"/>
    <preesm:dataTransfer from="codegen" sourceport="xml"
        targetport="xml" to="jobListGen"/>
    <preesm:dataTransfer from="codegen" sourceport="xml"
        targetport="xml" to="jobDefinesGen"/>
    <preesm:dataTransfer from="codegen" sourceport="xml"
        targetport="xml" to="bufferAllocGen"/>
    <preesm:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="Exporter"/>
</preesm:workflow>
