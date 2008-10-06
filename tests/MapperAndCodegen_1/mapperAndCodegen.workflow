<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
      <data key="variables">
         <variable name="maxStep" value="10"/>
         <variable name="margIn" value="1"/>
         <variable name="simulatorType" value="AccuratelyTimed"/>
         <variable name="maxCount" value="10"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
      <data key="variables">
         <variable name="sourcePath" value="${CodeGenSourcePath}"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables">
         <variable name="path" value="D:/IDCT2D/dag.gantt"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="SDF" to="FAST scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="FAST scheduler"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="FAST scheduler"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG" to="codegen"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="DAG Plotter"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="DAG Plotter"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="codegen"/>
</preesm:workflow>
