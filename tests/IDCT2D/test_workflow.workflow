<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
      <data key="variables">
         <variable name="depth" value="2"/>
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
         <variable name="path" value="/IDCT2D/flatten2.graphml"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="Mapper">
      <data key="variables">
         <variable name="margIn" value="10"/>
         <variable name="maxCount" value="10"/>
         <variable name="maxStep" value="10"/>
         <variable name="simulatorType" value="SendReceive"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
      <data key="variables"/>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
      <data key="variables">
         <variable name="edgeSchedType" value="Switcher"/>
         <variable name="margIn" value="30"/>
         <variable name="maxCount" value="800"/>
         <variable name="maxStep" value="800"/>
         <variable name="simulatorType" value="AccuratelyTimed"/>
         <variable name="switchTask" value="false"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler2">
      <data key="variables">
         <variable name="displaySolutions" value="true"/>
         <variable name="edgeSchedType" value="Switcher"/>
         <variable name="margIn" value="16"/>
         <variable name="maxCount" value="60"/>
         <variable name="maxStep" value="60"/>
         <variable name="nodesMin" value="5"/>
         <variable name="procNumber" value="1"/>
         <variable name="simulatorType" value="AccuratelyTimed"/>
         <variable name="switchTask" value="true"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="__scenario" sourceport="scenario" targetport="scenario" to="__algorithm"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="" to="__architecture"/>
   <preesm:dataTransfer from="__algorithm" sourceport="SDF" targetport="SDF" to="HierarchyFlattening"/>
   <preesm:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF" to="HSDF"/>
   <preesm:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="FAST scheduler2"/>
   <preesm:dataTransfer from="__architecture" sourceport="architecture" targetport="architecture" to="FAST scheduler2"/>
   <preesm:dataTransfer from="__scenario" sourceport="scenario" targetport="scenario" to="FAST scheduler2"/>
   <preesm:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="Exporter"/>
</preesm:workflow>
