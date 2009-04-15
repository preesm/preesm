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
        <xsl:value-of select="concat($new_line,'job_descriptor jobs[JOB_NUMBER] = {',$new_line)"/>
        <xsl:variable name="jobs">
        <xsl:apply-templates select="jobPostingCode:job"/>
        </xsl:variable>
        <xsl:value-of select="substring($jobs,1,string-length($jobs)-2)"/>
        <xsl:value-of select="concat($new_line,'};',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="jobPostingCode:job">  
        <xsl:value-of select="concat($sglIndent,'{',@id,',',@time,',',./jobPostingCode:functionName,',')"/>
        <xsl:value-of select="'{'"/>
        <xsl:variable name="preds">
            <xsl:apply-templates select="jobPostingCode:predecessors/jobPostingCode:pred"/>
        </xsl:variable>
        <xsl:value-of select="substring($preds,1,string-length($preds)-1)"/>
        <xsl:value-of select="'},{'"/>
        <xsl:variable name="buffers">
            <xsl:apply-templates select="jobPostingCode:buffers/jobPostingCode:buffer"/>
        </xsl:variable>
        <xsl:value-of select="substring($buffers,1,string-length($buffers)-1)"/>
        <xsl:value-of select="'},{'"/>
        <xsl:variable name="constants">
            <xsl:apply-templates select="jobPostingCode:constants/jobPostingCode:constant"/>
        </xsl:variable>
        <xsl:value-of select="substring($constants,1,string-length($constants)-1)"/>
        <xsl:value-of select="'}'"/>
        <xsl:value-of select="concat('},',$new_line)"/>
    </xsl:template> 
    
    <xsl:template match="jobPostingCode:pred">  
        <xsl:value-of select="concat(@id,',')"/>
    </xsl:template> 
    
    <xsl:template match="jobPostingCode:buffer">  
        <xsl:value-of select="concat(@name,',')"/>
    </xsl:template> 
    
    <xsl:template match="jobPostingCode:constant">  
        <xsl:value-of select="concat(@value,'/*',@name,'*/',',')"/>
    </xsl:template> 
    
    
</xsl:stylesheet>
