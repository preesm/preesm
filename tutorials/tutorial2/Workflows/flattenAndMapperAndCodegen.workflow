<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm"
                  xmlns:dftools="http://net.sf.dftools"
                  xmlns="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
   <dftools:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
      <dftools:data key="variables">
         <dftools:variable name="sourcePath" value="/tutorial2/Code/CodeGenerate"/>
         <dftools:variable name="xslLibraryPath" value="/tutorial2/Code/XSL"/>
      </dftools:data>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <dftools:data key="variables"/>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.mapper.exporter.ImplExportTransform"
                 taskId="ImplementationExporter">
      <dftools:data key="variables">
         <dftools:variable name="path" value="/tutorial2/DAG/outDAG.xml"/>
      </dftools:data>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
      <dftools:data key="variables">
         <dftools:variable name="displaySolutions" value="false"/>
         <dftools:variable name="edgeSchedType" value="Switcher"/>
         <dftools:variable name="margIn" value="100"/>
         <dftools:variable name="maxCount" value="200"/>
         <dftools:variable name="maxStep" value="200"/>
         <dftools:variable name="nodesMin" value="5"/>
         <dftools:variable name="procNumber" value="1"/>
         <dftools:variable name="simulatorType" value="AccuratelyTimed"/>
      </dftools:data>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
      <dftools:data key="variables"/>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
      <dftools:data key="variables">
         <dftools:variable name="path" value="/tutorial2/DAG/flatten.graphml"/>
      </dftools:data>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.transforms.flathierarchy"
                 taskId="HierarchyFlattening">
      <dftools:data key="variables">
         <dftools:variable name="depth" value="2"/>
      </dftools:data>
   </dftools:task>
   <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="DAG Plotter"/>
   <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture"
                         to="codegen"/>
   <dftools:dataTransfer from="scenario" sourceport="SDF" targetport="SDF" to="ImplementationExporter"/>
   <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture"
                         to="ImplementationExporter"/>
   <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario"
                         to="ImplementationExporter"/>
   <dftools:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG" to="codegen"/>
   <dftools:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG"
                         to="ImplementationExporter"/>
   <dftools:dataTransfer from="FAST scheduler" sourceport="ABC" targetport="ABC" to="DAG Plotter"/>
   <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture"
                         to="FAST scheduler"/>
   <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="FAST scheduler"/>
   <dftools:dataTransfer from="scenario" sourceport="SDF" targetport="SDF" to="HierarchyFlattening"/>
   <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF" to="Exporter"/>
   <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF"
                         to="FAST scheduler"/>
</dftools:workflow>