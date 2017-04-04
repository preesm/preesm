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
        <xsl:apply-templates select="node"/>
    </xsl:template>
    
    <xsl:template match="node">
        <xsl:choose >
            <xsl:when test="data[@key='vertexType']='task'" >
                <!-- Name of the task -->
                <xsl:variable name="task_name" select="data[@key='name']" />
                <xsl:value-of select="concat($task_name,$new_line)"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>
