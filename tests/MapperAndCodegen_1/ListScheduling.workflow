<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
      <data key="variables">
         <variable name="maxStep" value="400"/>
         <variable name="margIn" value="10"/>
         <variable name="simulatorType" value="SendReceive"/>
         <variable name="maxCount" value="400"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
      <data key="variables">
         <variable name="sourcePath" value="${CodeGenSourcePath}"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.pfast" taskId="PFAST scheduler">
      <data key="variables">
         <variable name="procNumber" value="3"/>
         <variable name="maxStep" value="16"/>
         <variable name="margIn" value="8"/>
         <variable name="simulatorType" value="LooselyTimed"/>
         <variable name="nodesMin" value="5"/>
         <variable name="maxCount" value="20"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.standardgenetic" taskId="genetic scheduler">
      <data key="variables">
         <variable name="pfastused2makepopulation" value="false"/>
         <variable name="generationNumber" value="100"/>
         <variable name="simulatorType" value="LooselyTimed"/>
         <variable name="populationSize" value="25"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.pgenetic" taskId="pgenetic scheduler">
      <data key="variables">
         <variable name="procNumber" value="3"/>
         <variable name="pfastused2makepopulation" value="true"/>
         <variable name="generationNumber" value="100"/>
         <variable name="simulatorType" value="LooselyTimed"/>
         <variable name="populationSize" value="10"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.listscheduling" taskId="list scheduler">
      <data key="variables">
         <variable name="simulatorType" value="LooselyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.commcontenlistsched" taskId="communication contentious list scheduler">
      <data key="variables">
         <variable name="simulatorType" value="LooselyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables">
         <variable name="path" value="D:/IDCT2D/dag.gantt"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="__algorithm" sourceport="SDF" targetport="SDF" to="communication contentious list scheduler"/>
   <preesm:dataTransfer from="__architecture" sourceport="architecture" targetport="architecture" to="communication contentious list scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="scenario" targetport="scenario" to="communication contentious list scheduler"/>
</preesm:workflow>
