<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
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
   <preesm:dataTransfer from="__architecture" sourceport="" targetport="architecture" to="FAST scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="FAST scheduler"/>
   <preesm:dataTransfer from="__algorithm" sourceport="" targetport="SDF" to="FAST scheduler"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="__algorithm"/>
   <preesm:dataTransfer from="__scenario" sourceport="" targetport="scenario" to="__architecture"/>
</preesm:workflow>
