<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns="http://graphml.graphdrawing.org/xmlns"
    xmlns:sourceCode="http://org.ietr.preesm.sourceCode"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    
    <xsl:output indent="yes" method="text"/>
    <xsl:variable name="new_line" select="'&#xA;'" />
    <xsl:variable name="coreName" select="sourceCode:sourceCode/sourceCode:coreName"/>  
    <xsl:template match="text()"/>
    
    <!-- defining globally useful variables -->
    <xsl:variable name="sglIndent" select="'    '" />
    <xsl:variable name="curIndent" select="$sglIndent" />
    
    
    <!-- includes -->
    <xsl:template name="includeSection"> 
        <!-- add your include here in the following form -->
        <!--<xsl:value-of select="concat($curIndent,'#include &quot; your include here&quot;',$new_line)"/> -->
        <xsl:value-of select="$new_line"/>
    </xsl:template>
    
    <!-- Declaring thread functions prototypes -->
    <xsl:template match="sourceCode:threadDeclaration" mode="prototype">
        <xsl:value-of select="concat($curIndent,'DWORD WINAPI ',@name,'_',$coreName,'( LPVOID lpParam );',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:threadDeclaration">
        <xsl:value-of select="concat($curIndent,'DWORD WINAPI ',@name,'_',$coreName,'( LPVOID lpParam ){',$new_line)"/>
        <xsl:apply-templates select="sourceCode:bufferContainer | sourceCode:linearCodeContainer | sourceCode:forLoop">
            <xsl:with-param name="curIndent" select="concat($curIndent,$sglIndent)"/>
        </xsl:apply-templates>
        <xsl:value-of select="concat($curIndent,$sglIndent,'return 0;',$new_line)"/>
        <xsl:value-of select="concat($curIndent,'}//',@name,$new_line)"/>
        <xsl:value-of select="$new_line"/>
    </xsl:template>
    
    <!-- Middle blocks level -->
 
    
    <xsl:template match="sourceCode:semaphorePost">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'ReleaseSemaphore','(')"/>
        <xsl:value-of select="concat(sourceCode:buffer/@name,'[',@number,']')"/>
        <xsl:value-of select="concat(',1,NULL);',' //',@type,$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:semaphorePend">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'WaitForSingleObject','(')"/>
        <xsl:value-of select="concat(sourceCode:buffer/@name,'[',@number,']')"/>
        <xsl:value-of select="concat(',INFINITE);',' //',@type,$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:semaphoreInit">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'semaphoreInit','(')"/>
        <!-- adding buffers -->
        <xsl:variable name="buffers">
            <xsl:apply-templates select="sourceCode:buffer | sourceCode:constant"/>
        </xsl:variable>
        <!-- removing last coma -->
        <xsl:value-of select="concat($buffers,$coreName,');',$new_line)"/>
    </xsl:template>
    
    
    <xsl:template match="sourceCode:sendMsg">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'sendData(',sourceCode:routeStep/@mediumName,',',$coreName,',',@target,',')"/>
        <!-- adding buffer -->
        <xsl:value-of select="concat(sourceCode:buffer/@name,',',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')',');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveMsg">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'receiveData(',sourceCode:routeStep/@mediumName,',',@source,',',$coreName,',')"/>
        <!-- adding buffer -->
        <xsl:value-of select="concat(sourceCode:buffer/@name,',',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')',');',$new_line)"/>
    </xsl:template>
    
    
    <xsl:template match="sourceCode:launchThread">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'CreateThread(NULL,',@stackSize,',',@threadName,'_',$coreName,',NULL,0,NULL')"/>       
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:sendInit">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'Com_Init(MEDIUM_SEND,',sourceCode:routeStep/@mediumName,',',$coreName,',',@connectedCoreId)"/>       
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveInit">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'Com_Init(MEDIUM_RCV,',sourceCode:routeStep/@mediumName,',',@connectedCoreId,',',$coreName)"/>       
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
</xsl:stylesheet>
