<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:jobPostingCode="http://org.ietr.preesm.jobPostingCode"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    
    <xsl:output indent="yes" method="text"/>
    <xsl:variable name="new_line" select="'&#xA;'" />
    <!-- defining globally useful variables -->
    <xsl:variable name="sglIndent" select="'    '" />
    <xsl:variable name="curIndent" select="$sglIndent" />
    
    <xsl:template match="jobPostingCode:BufferContainer">
        <xsl:value-of select="$new_line"/>
        <xsl:apply-templates select="jobPostingCode:BufferAllocation"/>
    </xsl:template>
    
    <xsl:template match="jobPostingCode:BufferAllocation">
        <xsl:value-of select="concat(@type,' ',@name,'[',@size,'];',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="jobPostingCode:jobs"/>
</xsl:stylesheet>
