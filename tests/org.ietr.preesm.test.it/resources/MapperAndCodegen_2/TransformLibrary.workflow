<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="FAST scheduler">
      <data key="variables">
         <variable name="margIn" value="10"/>
         <variable name="maxCount" value="400"/>
         <variable name="maxStep" value="400"/>
         <variable name="simulatorType" value="LooselyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="codegen">
      <data key="variables">
         <variable name="sourcePath" value="D:\Projets\PreesmSourceForge\trunk\plugins\mapper\examples\TestProject\Code"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.pfast" taskId="PFAST scheduler">
      <data key="variables">
         <variable name="margIn" value="8"/>
         <variable name="maxCount" value="20"/>
         <variable name="maxStep" value="16"/>
         <variable name="nodesMin" value="5"/>
         <variable name="procNumber" value="3"/>
         <variable name="simulatorType" value="LooselyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.standardgenetic" taskId="genetic scheduler">
      <data key="variables">
         <variable name="generationNumber" value="100"/>
         <variable name="pfastused2makepopulation" value="false"/>
         <variable name="populationSize" value="25"/>
         <variable name="simulatorType" value="LooselyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.pgenetic" taskId="pgenetic scheduler">
      <data key="variables">
         <variable name="generationNumber" value="100"/>
         <variable name="pfastused2makepopulation" value="true"/>
         <variable name="populationSize" value="10"/>
         <variable name="procNumber" value="3"/>
         <variable name="simulatorType" value="LooselyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.listscheduling" taskId="list scheduler">
      <data key="variables">
         <variable name="simulatorType" value="LooselyTimed"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fpgasched" taskId="FPGA scheduling">
      <data key="variables"/>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.plot" taskId="DAG Plotter">
      <data key="variables">
         <variable name="path" value="D:/IDCT2D/dag.gantt"/>
      </data>
   </preesm:task>
</preesm:workflow>
