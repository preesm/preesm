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
        <xsl:value-of select="concat('Tasks = {}',$new_line,'Tasks.p = {}',$new_line,$new_line)"/>
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
                <xsl:variable name="task_duration_decl" select="concat($task_def,'.duration = ','0')" />
                <xsl:variable name="task_mapping_decl" select="concat($task_def,'.CPU_mapping = &quot;',data[@key='Operator'],'&quot;')" />
                <xsl:variable name="task_prioriti_decl" select="concat($task_def,'.priority = ','1')" />
                <xsl:value-of select="concat($task_duration_decl,$new_line)"/>
                <xsl:value-of select="concat($task_mapping_decl,$new_line)"/>
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
