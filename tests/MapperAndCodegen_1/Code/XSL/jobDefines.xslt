<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:jobPostingCode="http://org.ietr.preesm.jobPostingCode"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    
    <xsl:output indent="yes" method="text"/>
    <xsl:variable name="new_line" select="'&#xA;'" />
    <!-- defining globally useful variables -->
    <xsl:variable name="sglIndent" select="'    '" />
    <xsl:variable name="curIndent" select="$sglIndent" />
    
    <xsl:template match="jobPostingCode:BufferContainer"/>
    
    <xsl:template match="jobPostingCode:jobs">   
        <xsl:value-of select="$new_line"/>
        <xsl:value-of select="'#define JOB_NUMBER '"/>
        <xsl:value-of select="count(jobPostingCode:job)"/>
        <xsl:value-of select="concat(' // Number of jobs in the queue',$new_line)"/>
        
        <xsl:value-of select="'#define MAX_PRED '"/>
        <xsl:value-of select="max(//jobPostingCode:predecessors/count(jobPostingCode:pred))"/>
        <xsl:value-of select="concat(' // Maximal number of predecessors for a job',$new_line)"/>
        
        <xsl:value-of select="'#define MAX_BUF '"/>
        <xsl:value-of select="max(//jobPostingCode:buffers/count(jobPostingCode:buffer))"/>
        <xsl:value-of select="concat(' // Maximal number of buffers for a job',$new_line)"/>
        
        <xsl:value-of select="'#define MAX_PARAM '"/>
        <xsl:value-of select="max(//jobPostingCode:constants/count(jobPostingCode:constant))"/>
        <xsl:value-of select="concat(' // Maximal number of constant parameters for a job',$new_line)"/>
    </xsl:template>    
    
</xsl:stylesheet>
