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
        
--************--
--* Scenario *--
--************--

-- initializations

BaseCategories = { }
UserCategories = { }
Connections={}
Users={}
Tasks={}

--  defining base category. This category can then be used directly or reused in a parent category

BaseCategories.single_trace_category = "nerios.graphml"

-- Instantiating a user

Users.p ="single_trace_category"

-- Configuring the tasks

Tasks.p={}
        
        <xsl:value-of select="$new_line"/>
        <xsl:apply-templates select="node"/>
        <xsl:apply-templates select="edge"/>
    </xsl:template>
    
    <xsl:template name="addXin">
        <xsl:param name="currentTask"></xsl:param>
        <xsl:variable name="task_def" select="concat('Tasks.p.',$currentTask)" />
        <xsl:variable name="input_transfers" select="//node[data[@key='vertexType']='receive' and data[@key='receiverGraphName']=$currentTask]" />
        
        <xsl:if test="$input_transfers/last()!=0">
            <xsl:value-of select="concat($task_def,'.Xin= {')"/>
            <xsl:for-each select="$input_transfers">
                <xsl:variable name="transfer_def" select="concat('Tasks.p.s_',substring(data[@key='name'],3))" />
                <xsl:value-of select="concat('&quot;',$transfer_def,'&quot;')"/>
                <xsl:if test="position()!=last()">,</xsl:if>
            </xsl:for-each>
            <xsl:value-of select="concat('}',$new_line)"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="addXout">
        <xsl:param name="currentTask"></xsl:param>
        <xsl:variable name="task_def" select="concat('Tasks.p.',$currentTask)" />
        <xsl:variable name="output_transfers" select="//node[data[@key='vertexType']='send' and data[@key='senderGraphName']=$currentTask]" />
        
        <xsl:if test="$output_transfers/last()!=0">
            <xsl:value-of select="concat($task_def,'.Xout= {')"/>
            <xsl:for-each select="$output_transfers">
                <xsl:variable name="transfer_def" select="concat('Tasks.p.',data[@key='name'])" />
                <xsl:value-of select="concat('&quot;',$transfer_def,'&quot;')"/>
                <xsl:if test="position()!=last()">,</xsl:if>
            </xsl:for-each>
            <xsl:value-of select="concat('}',$new_line)"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="node">
        <xsl:choose >
            <xsl:when test="data[@key='vertexType']='task'" >
                <!-- Name of the task -->
                <xsl:variable name="task_name" select="data[@key='name']" />
                <!-- Name of the task in lua form -->
                <xsl:variable name="task_def" select="concat('Tasks.p.',$task_name)" />
                
                <xsl:variable name="task_decl" select="concat($task_def,' = {}')" />
                <xsl:variable name="task_duration_decl" select="concat($task_def,'.duration = ',data[@key='duration'])" />
                <xsl:variable name="task_mapping_decl" select="concat($task_def,'.CPU_mapping = &quot;',data[@key='Operator'],'&quot;')" />
                <xsl:variable name="task_prioriti_decl" select="concat($task_def,'.priority = ','1')" />
                
                <xsl:value-of select="concat($task_decl,$new_line)"/>
                <xsl:value-of select="concat($task_duration_decl,$new_line)"/>
                <xsl:value-of select="concat($task_mapping_decl,$new_line)"/>
                <xsl:value-of select="concat($task_prioriti_decl,$new_line)"/>
                
                <!-- Removed transfer generation -->
                <xsl:if test="1=1">
                <xsl:call-template name="addXin">
                    <xsl:with-param name="currentTask" select="$task_name"/>
                </xsl:call-template>
                <xsl:call-template name="addXout">
                    <xsl:with-param name="currentTask" select="$task_name"/>
                </xsl:call-template>
                <xsl:value-of select="$new_line"/>
                </xsl:if>
            </xsl:when>
            <!-- Removed transfer generation -->
            <xsl:when test="data[@key='vertexType']='send'" >
                <!-- Name of the task -->
                <xsl:variable name="task_name" select="data[@key='name']" />
                <!-- Name of the task in lua form -->
                <xsl:variable name="task_def" select="concat('Tasks.p.',$task_name)" />
                
                <!-- Corresponding receiver task -->
                <xsl:variable name="rcv_name" select="concat('r_',substring-after($task_name,'s_'))" />
                <xsl:variable name="rcv_task" select="//node[data[@key='name']=$rcv_name]" />
                <xsl:variable name="task_decl" select="concat($task_def,' = {}')" />
                <xsl:variable name="task_mapping_decl" select="concat($task_def,'.CPU_mapping = &quot;','VpuBidon','&quot;')" />
                <xsl:variable name="task_prioriti_decl" select="concat($task_def,'.priority = ','10')" />
                
                <xsl:value-of select="concat($task_decl,$new_line)"/>
                <xsl:value-of select="concat($task_mapping_decl,$new_line)"/>
                <xsl:value-of select="concat($task_def,'.resource_mapping = &quot;EDMA__CCDMA&quot;',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.srcAddress = &quot;',data[@key='Operator_address'],'&quot;',$new_line)"/>
                <xsl:value-of select="concat($task_def,'.destAddress = &quot;',$rcv_task/data[@key='Operator_address'],'&quot;',$new_line)"/>
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
