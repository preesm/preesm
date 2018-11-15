<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:scenario pluginId="TestScenario"/>
   <preesm:task pluginId="TestNewWorkflow1" taskId="a">
      <data key="variables">
         <variable name="duration" value="short"/>
         <variable name="size" value="25"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="TestNewWorkflow2" taskId="b">
      <data key="variables">
         <variable name="duration" value="long"/>
         <variable name="size" value="10"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="TestNewWorkflow1" taskId="a2">
      <data key="variables">
         <variable name="duration" value="short"/>
         <variable name="size" value="25"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="a" sourceport="superData" targetport="superData" to="b"/>
   <preesm:dataTransfer from="__scenario" sourceport="algo" targetport="algo" to="a"/>
   <preesm:dataTransfer from="__scenario" sourceport="archi" targetport="archture" to="a"/>
   <preesm:dataTransfer from="__scenario" sourceport="algo" targetport="algo" to="b"/>
   <preesm:dataTransfer from="__scenario" sourceport="algo" targetport="algo" to="a2"/>
   <preesm:dataTransfer from="__scenario" sourceport="archi" targetport="archture" to="a2"/>
   <preesm:dataTransfer from="a2" sourceport="superData" targetport="superData" to="b"/>
</preesm:workflow>
