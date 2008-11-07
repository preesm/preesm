<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
      <data key="variables">
         <variable name="margIn" value="10"/>
         <variable name="maxCount" value="400"/>
         <variable name="maxStep" value="400"/>
         <variable name="simulatorType" value="AccuratelyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
      <data key="variables">
         <variable name="sourcePath" value="/RACH/Code"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables"/>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.graphtransfo.DAGExporterTransform" taskId="DAGExporter">
      <data key="variables">
         <variable name="path" value="/RACH/DAG/outDAG.xml"/>
         <variable name="transformedPath" value="/RACH/DAG/outDAG.lua"/>
         <variable name="xslPath" value="/RACH/DAG/LUAXSL.xslt"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="FAST scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="FAST scheduler"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="FAST scheduler"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG" to="codegen"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="DAG Plotter"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="DAG Plotter"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="DAG Plotter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="codegen"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="__algorithm"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="__architecture"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="DAGExporter"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="DAG" targetport="DAG" to="DAGExporter"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="DAGExporter"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="DAGExporter"/>
</preesm:workflow>
