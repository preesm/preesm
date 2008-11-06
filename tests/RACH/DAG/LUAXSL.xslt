<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:graphml="http://graphml.graphdrawing.org/xmlns"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:output indent="yes" method="text"/>
    <xsl:variable name="new_line" select="'&#xA;'" />

    <!-- Top-level: graph -> graph -->
    <xsl:template match="graphml:graphml">
        <xsl:apply-templates select="graph"/>
    </xsl:template>
    
    <xsl:template match="graph">      
        
--General Config
        
--VPU1={}
--VPU1.enable_preemption=true
--VPU2={}
--VPU2.enable_preemption=true
        
        
EDMA__CCDMA ={}
        
EDMA__CCDMA.evt_queue_priority = {0,1,2,3,4,5}
EDMA__CCDMA.tc_address_map = { 0x02A20000,0x02A28000,0x02A30000,0x02A38000,0x02A40000,0x02A48000 }

Tasks = {}
Tasks.p = {}
        <xsl:value-of select="$new_line"/>
        <xsl:apply-templates select="node"/>
        <xsl:apply-templates select="edge"/>
    </xsl:template>
    
    <xsl:template match="node">
        <xsl:choose >
            <xsl:when test="data[@key='vertexType']='task'" >
                <!-- Name of the task -->
                <xsl:variable name="task_name" select="data[@key='name']" />
                <!-- Name of the task in lua form -->
                <xsl:variable name="task_def" select="concat('Tasks.p.',$task_name)" />
                <!-- declaration of a duration -->
                <xsl:variable name="task_duration_decl" select="concat($task_def,'.duration = ',data[@key='duration'])" />
                <xsl:variable name="task_mapping_decl" select="concat($task_def,'.CPU_mapping = &quot;',data[@key='Operator'],'&quot;')" />
                <xsl:variable name="task_prioriti_decl" select="concat($task_def,'.priority = ','1')" />
                
                <xsl:value-of select="concat($task_duration_decl,$new_line)"/>
                <xsl:value-of select="concat($task_mapping_decl,$new_line)"/>
                <xsl:value-of select="concat($task_prioriti_decl,$new_line)"/>
                <xsl:value-of select="$new_line"/>
            </xsl:when>
            <xsl:when test="data[@key='vertexType']='send'" >
                <!-- Name of the task -->
                <xsl:variable name="task_name" select="data[@key='name']" />
                <!-- Name of the task in lua form -->
                <xsl:variable name="task_def" select="concat('Tasks.p.',$task_name)" />
                
                <!-- Corresponding receiver task -->
                <xsl:variable name="rcv_name" select="concat('rcv',substring-after($task_name,'snd'))" />
                <xsl:variable name="rcv_task" select="//node[data[@key='name']=$rcv_name]" />
                <xsl:variable name="task_mapping_decl" select="concat($task_def,'.CPU_mapping = &quot;','VpuBidon','&quot;')" />
                <xsl:variable name="task_prioriti_decl" select="concat($task_def,'.priority = ','10')" />
                
                <xsl:value-of select="concat($task_mapping_decl,$new_line)"/>
                <xsl:value-of select="concat($task_def,'.resource_mapping = &quot;EDMA__CCDMA&quot;',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.srcAddress = &quot;',data[@key='Operator'],'&quot;',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.destAddress = &quot;',$rcv_task/data[@key='Operator'],'&quot;',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.sam = 0',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.dam = 0',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.ACNT = ',data[@key='dataSize'],$new_line)"/>
                <xsl:value-of select="concat($task_def,'.BCNT = 1',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.CCNT = 1',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.SRCBIDX = false',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.DSTBIDX = 0',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.DSTCIDX = 0',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.tccmode = 0',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.tcchen = false',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.queueNum = 0xFFFF',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.triggersTransfer = true',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.freeAfterCompletion = true',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.isStatic = false',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.reloadTransfer = false',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.isQDMA = false',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.isATriggerOnly = false',$new_line)"/>
                <xsl:value-of select="concat($task_prioriti_decl,$new_line)"/>
                <xsl:value-of select="$new_line"/>
            </xsl:when>
            <xsl:otherwise >
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    <xsl:template match="edge">
    </xsl:template>
    
</xsl:stylesheet>
