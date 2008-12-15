<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
      <data key="variables">
         <variable name="depth" value="-1"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="CodeGen">
      <data key="variables">
         <variable name="sourcePath" value="${CodeGenSourcePath}"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables">
         <variable name="path" value="D:/IDCT2D/dag.gantt"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
      <data key="variables">
         <variable name="path" value="D:/Preesm/trunk/tests/IDCT2D/flatten.graphml"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="Mapper">
      <data key="variables">
         <variable name="margIn" value="10"/>
         <variable name="maxCount" value="10"/>
         <variable name="maxStep" value="10"/>
         <variable name="simulatorType" value="LooselyTimed"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF" to="Exporter"/>
   <preesm:dataTransfer from="__algorithm" sourceport="SDF" targetport="SDF" to="HierarchyFlattening"/>
   <preesm:dataTransfer from="__scenario" sourceport="scenario" targetport="scenario" to="__algorithm"/>
</preesm:workflow>
