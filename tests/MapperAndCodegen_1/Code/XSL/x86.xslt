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
    
    <xsl:template match="sourceCode:sourceCode">
        <xsl:variable name="coreType" select="sourceCode:coreType"/>  
        
        <!-- checking the core type of the target core -->
        <xsl:if test="$coreType='x86'">
            <xsl:apply-templates select="sourceCode:SourceFile"/>
        </xsl:if>
    </xsl:template>
    
    <!-- Big blocks level -->
    <xsl:template match="sourceCode:SourceFile">
        
        <xsl:call-template name="includeSection"/>
        <xsl:apply-templates select="sourceCode:bufferContainer">
            <xsl:with-param name="curIndent" select="$curIndent"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="sourceCode:threadDeclaration" mode="prototype"/>
        <xsl:value-of select="$new_line"/>
        <xsl:apply-templates select="sourceCode:threadDeclaration"/>
    </xsl:template>
    
    <!-- includes -->
    <xsl:template name="includeSection"> 
        <xsl:value-of select="concat($curIndent,'#include &quot;../src/x86.h&quot;',$new_line)"/>
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
    
    <xsl:template match="sourceCode:bufferContainer">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'// Buffer declarations',$new_line)"/>
        <xsl:apply-templates select="sourceCode:bufferAllocation"/>
        <xsl:value-of select="$new_line"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:linearCodeContainer">
        <xsl:param name="curIndent"/>
        <xsl:if test="sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive | sourceCode:launchThread | sourceCode:sendInit | sourceCode:receiveInit">
            <xsl:value-of select="concat($curIndent,'{',$new_line)"/>
            
            <xsl:apply-templates select="sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive | sourceCode:launchThread | sourceCode:sendInit | sourceCode:receiveInit">
                <xsl:with-param name="curIndent" select="concat($curIndent,$sglIndent)"/>
            </xsl:apply-templates>
            
            <xsl:value-of select="concat($curIndent,'}',$new_line)"/>
            <xsl:value-of select="$new_line"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="sourceCode:forLoop">
        <xsl:param name="curIndent"/>
        <xsl:if test="sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive">
            <xsl:value-of select="concat($curIndent,'while(1){',$new_line)"/>
            
            <xsl:apply-templates select="sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive">
                <xsl:with-param name="curIndent" select="concat($curIndent,$sglIndent)"/>
            </xsl:apply-templates>
            
            <xsl:value-of select="concat($curIndent,'}',$new_line)"/>
            <xsl:value-of select="$new_line"/>
        </xsl:if>
    </xsl:template>
    
    <!-- Small blocks level -->
    
    <xsl:template match="sourceCode:userFunctionCall">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,@name,'(')"/>
        <!-- adding buffers -->
        <xsl:variable name="buffers">
            <xsl:apply-templates select="sourceCode:buffer | sourceCode:constant"/>
        </xsl:variable>
        <!-- removing last coma -->
        <xsl:variable name="buffers" select="substring($buffers,1,string-length($buffers)-1)"/>
        <xsl:value-of select="concat($buffers,');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:semaphorePost">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'ReleaseSemaphore','(')"/>
        <xsl:value-of select="concat(sourceCode:buffer/@name,'[',@number,']')"/>
        <xsl:value-of select="concat(',1,&amp;previous);',' //',@type,$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:semaphorePend">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'WaitForSingleObject','(')"/>
        <xsl:value-of select="concat(sourceCode:buffer/@name,'[',@number,']')"/>
        <xsl:value-of select="concat(',INFINITE);',' //',@type,$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:send">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'sendData(',@mediumDef,',',$coreName,',',@target,',')"/>
        <!-- adding buffer -->
        <xsl:value-of select="concat(sourceCode:buffer/@name,',',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')',');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:receive">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'receiveData(',@mediumDef,',',@source,',',$coreName,',')"/>
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
        <xsl:value-of select="concat($curIndent,'sendInit(',@mediumId,',',$coreName,',',@connectedCoreId)"/>       
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveInit">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'receiveInit(',@mediumId,',',@connectedCoreId,',',$coreName)"/>       
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <!-- Units level -->
    
    <xsl:template match="sourceCode:buffer">
        <xsl:value-of select="concat(@name,',')"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:constant">
        <xsl:value-of select="concat(@value,'/*',@name,'*/',',')"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:bufferAllocation">
        <xsl:value-of select="concat($curIndent,@type,' ',@name,'[',@size,'];',$new_line)"/>
    </xsl:template>
    
</xsl:stylesheet>
