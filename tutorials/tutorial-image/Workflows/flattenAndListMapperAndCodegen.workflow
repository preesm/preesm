<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
      <data key="variables">
         <variable name="sourcePath" value="/tutorial-image/Code"/>
         <variable name="xslLibraryPath" value="/tutorial-image/Code/XSL"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables"/>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.exporter.ImplExportTransform" taskId="ImplementationExporter">
      <data key="variables">
         <variable name="openFile" value="false"/>
         <variable name="path" value="/tutorial-image/DAG/outDAG.xml"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.listscheduling" taskId="LIST scheduler">
      <data key="variables">
         <variable name="balanceLoads" value="true"/>
         <variable name="displaySolutions" value="true"/>
         <variable name="edgeSchedType" value="Simple"/>
         <variable name="simulatorType" value="AccuratelyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
      <data key="variables"/>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
      <data key="variables">
         <variable name="depth" value="2"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
      <data key="variables">
         <variable name="openFile" value="false"/>
         <variable name="path" value="/tutorial-image/DAG/flatten.graphml"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="codegen"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="__algorithm"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="__architecture"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="ImplementationExporter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="ImplementationExporter"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="ImplementationExporter"/>
   <preesm:dataTransfer from="LIST scheduler" sourceport="DAG" targetport="DAG" to="codegen"/>
   <preesm:dataTransfer from="LIST scheduler" sourceport="DAG" targetport="DAG" to="ImplementationExporter"/>
   <preesm:dataTransfer from="LIST scheduler" sourceport="ABC" targetport="ABC" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="LIST scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="LIST scheduler"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="HierarchyFlattening"/>
   <preesm:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF" to="HSDF"/>
   <preesm:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="Exporter"/>
   <preesm:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="LIST scheduler"/>
</preesm:workflow>
