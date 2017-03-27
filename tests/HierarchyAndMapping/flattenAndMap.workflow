<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.flathierarchy" taskId="HierarchyFlattening">
      <data key="variables">
         <variable name="depth" value="2"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="Exporter">
      <data key="variables">
         <variable name="path" value="D:\Projets\Preesm\trunk\tests\HierarchyAndMapping\flatten.graphml"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
      <data key="variables">
         <variable name="displaySolutions" value="true"/>
         <variable name="edgeSchedType" value="Switcher"/>
         <variable name="margIn" value="30"/>
         <variable name="maxCount" value="800"/>
         <variable name="maxStep" value="800"/>
         <variable name="simulatorType" value="AccuratelyTimed"/>
         <variable name="switchTask" value="true"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables">
         <variable name="path" value="D:/IDCT2D/dag.gantt"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="__scenario" sourceport="scenario" targetport="scenario" to="__algorithm"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="" to="__architecture"/>
   <preesm:dataTransfer from="__algorithm" sourceport="SDF" targetport="SDF" to="HierarchyFlattening"/>
   <preesm:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF" to="Exporter"/>
   <preesm:dataTransfer from="HierarchyFlattening" sourceport="SDF" targetport="SDF" to="FAST scheduler"/>
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="FAST scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="scenario" targetport="scenario" to="FAST scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="scenario" targetport="scenario" to="DAG Plotter"/>
   <preesm:dataTransfer from="FAST scheduler" sourceport="customData" targetport="customData" to="DAG Plotter"/>
</preesm:workflow>
