<?xml version="1.0" encoding="UTF-8"?><preesm:workflow xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm">
   <preesm:algorithm/>
   <preesm:architecture/>
   <preesm:scenario/>
   <preesm:task pluginId="org.ietr.preesm.plugin.transforms.sdf2hsdf" taskId="HSDF">
      <data key="variables"/>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.exportXml.sdf4jgml" taskId="export">
      <data key="variables">
         <variable name="path" value="D:\IDCT2D\WorkOut.xml"/>
      </data>
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.codegen" taskId="CodeGen">
   </preesm:task>
   <preesm:task pluginId="org.ietr.preesm.plugin.mapper.fast" taskId="Mapper">
      <data key="variables">
         <variable name="maxStep" value="400"/>
         <variable name="margIn" value="10"/>
         <variable name="simulatorType" value="LooselyTimed"/>
         <variable name="maxCount" value="400"/>
      </data>
   </preesm:task>
   <preesm:dataTransfer from="__algorithm" sourceport="src port" targetport="SDF" to="Mapper"/>
   <preesm:dataTransfer from="Mapper" sourceport="DAG" targetport="DAG" to="CodeGen"/>
   <preesm:dataTransfer from="__scenario" sourceport="src port" targetport="scenario" to="Mapper"/>
   <preesm:dataTransfer from="__architecture" sourceport="src port" targetport="architecture" to="Mapper"/>
</preesm:workflow>
