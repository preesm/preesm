<?xml version="1.0" encoding="UTF-8"?>
<dftools:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm"
                  xmlns:dftools="http://net.sf.dftools"
                  xmlns="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <dftools:scenario pluginId="org.ietr.preesm.scenario.task"/>
   <dftools:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables"/>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.mapper.listscheduling" taskId="LIST scheduler">
      <data key="variables">
         <variable name="balanceLoads" value="true"/>
         <variable name="displaySolutions" value="true"/>
         <variable name="edgeSchedType" value="Simple"/>
         <variable name="simulatorType" value="AccuratelyTimed"/>
      </data>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
      <data key="variables"/>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.transforms.flathierarchy"
                 taskId="HierarchyFlattening">
      <data key="variables">
         <variable name="depth" value="2"/>
      </data>
   </dftools:task>
   <dftools:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
      <data key="variables">
         <variable name="openFile" value="true"/>
         <variable name="path" value="/tutorial-image/DAG/flatten.graphml"/>
      </data>
   </dftools:task>
   <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="DAG Plotter"/>
   <dftools:dataTransfer from="LIST scheduler" sourceport="ABC" targetport="ABC" to="DAG Plotter"/>
   <dftools:dataTransfer from="scenario" sourceport="architecture" targetport="architecture"
                         to="LIST scheduler"/>
   <dftools:dataTransfer from="scenario" sourceport="scenario" targetport="scenario" to="LIST scheduler"/>
   <dftools:dataTransfer from="scenario" sourceport="SDF" targetport="SDF" to="HierarchyFlattening"/>
   <dftools:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF" to="HSDF"/>
   <dftools:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="Exporter"/>
   <dftools:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="LIST scheduler"/>
</dftools:workflow>