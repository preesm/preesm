<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
      <data key="variables"/>
   </preesm:task>
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
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="Mapper">
      <data key="variables">
         <variable name="maxStep" value="10"/>
         <variable name="margIn" value="10"/>
         <variable name="simulatorType" value="LooselyTimed"/>
         <variable name="maxCount" value="10"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables">
         <variable name="path" value="D:/IDCT2D/dag.gantt"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="Mapper" sourceport="DAG" targetport="DAG" to="CodeGen"/>
   <preesm:dataTransfer from="__scenario" sourceport="src port" targetport="scenario" to="Mapper"/>
   <preesm:dataTransfer from="__architecture" sourceport="src port" targetport="architecture" to="Mapper"/>
   <preesm:dataTransfer from="Mapper" sourceport="DAG" targetport="DAG" to="DAG Plotter"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="DAG Plotter"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="DAG Plotter"/>
   <preesm:dataTransfer from="__algorithm" sourceport="SDF" targetport="SDF" to="HSDF"/>
   <preesm:dataTransfer from="HSDF" sourceport="SDF" targetport="SDF" to="HierarchyFlattening"/>
   <preesm:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF" to="Mapper"/>
</preesm:workflow>
