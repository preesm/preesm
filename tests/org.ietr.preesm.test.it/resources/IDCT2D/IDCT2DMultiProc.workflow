<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
      <data key="variables">
         <variable name="sourcePath" value="/IDCT2D/Code/MultiPc"/>
         <variable name="xslLibraryPath" value="/IDCT2D/Code/XSL"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables"/>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.exporter.ImplExportTransform" taskId="ImplementationExporter">
      <data key="variables">
         <variable name="path" value="/IDCT2D/Generated/outDAG.xml"/>
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
         <variable name="path" value="/IDCT2D/Generated/MultiProcflatten.graphml"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
      <data key="variables"/>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
      <data key="variables">
         <variable name="depth" value="1"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="codegen"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="__algorithm"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="__architecture"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="ImplementationExporter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="ImplementationExporter"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="ImplementationExporter"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG" to="codegen"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG" to="ImplementationExporter"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="ABC" targetport="ABC" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="FAST scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="FAST scheduler"/>
   <preesm:dataTransfer from="__algorithm" sourceport="SDF" targetport="SDF" to="HSDF"/>
   <preesm:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="FAST scheduler"/>
</preesm:workflow>
