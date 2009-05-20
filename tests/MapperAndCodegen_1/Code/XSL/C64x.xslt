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
        <xsl:if test="$coreType='C64x'">
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
        <xsl:value-of select="concat($curIndent,'#include &quot;C64x+.h&quot;',$new_line)"/>
        <xsl:value-of select="$new_line"/>
    </xsl:template>
    
    <!-- Declaring thread functions prototypes -->
    <xsl:template match="sourceCode:threadDeclaration" mode="prototype">
        <xsl:value-of select="concat($curIndent,'void ',@name,'();',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:threadDeclaration">
        <xsl:value-of select="concat($curIndent,'void ',@name,'(){',$new_line)"/>
        <xsl:apply-templates select="sourceCode:bufferContainer | sourceCode:linearCodeContainer | sourceCode:forLoop">
            <xsl:with-param name="curIndent" select="concat($curIndent,$sglIndent)"/>
        </xsl:apply-templates>
        <xsl:value-of select="concat($curIndent,$sglIndent,$new_line)"/>
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
        <xsl:if test="sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:semaphoreInit | sourceCode:sendDma | sourceCode:receiveDma | sourceCode:sendMsg | sourceCode:receiveMsg | sourceCode:launchThread | sourceCode:sendInit | sourceCode:receiveInit | sourceCode:waitForCore | sourceCode:linearCodeContainer | sourceCode:sendAddress | sourceCode:receiveAddress">
            <xsl:value-of select="concat($curIndent,'{',$new_line)"/>
            
            <xsl:apply-templates select="sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:semaphoreInit | sourceCode:sendDma | sourceCode:receiveDma | sourceCode:sendMsg | sourceCode:receiveMsg | sourceCode:launchThread | sourceCode:sendInit | sourceCode:receiveInit | sourceCode:waitForCore | sourceCode:linearCodeContainer | sourceCode:sendAddress | sourceCode:receiveAddress">
                <xsl:with-param name="curIndent" select="concat($curIndent,$sglIndent)"/>
            </xsl:apply-templates>
            
            <xsl:value-of select="concat($curIndent,'}',$new_line)"/>
            <xsl:value-of select="$new_line"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="sourceCode:forLoop">
        <xsl:param name="curIndent"/>
        <xsl:if test="sourceCode:subBufferAllocation | sourceCode:variableAllocation | sourceCode:CompoundCode | sourceCode:finiteForLoop |sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:sendDma | sourceCode:receiveDma | sourceCode:sendMsg | sourceCode:receiveMsg">
            <xsl:value-of select="concat($curIndent,'while(1){',$new_line)"/>
            
            <xsl:apply-templates select="sourceCode:subBufferAllocation | sourceCode:variableAllocation | sourceCode:CompoundCode | sourceCode:finiteForLoop |sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:sendDma | sourceCode:receiveDma | sourceCode:sendMsg | sourceCode:receiveMsg">
                <xsl:with-param name="curIndent" select="concat($curIndent,$sglIndent)"/>
            </xsl:apply-templates>
            
            <xsl:value-of select="concat($curIndent,'}',$new_line)"/>
            <xsl:value-of select="$new_line"/>
        </xsl:if>
    </xsl:template>
    
    <!-- finite for loops -->
    <xsl:template match="sourceCode:finiteForLoop">
        <xsl:param name="curIndent"/>
        <xsl:if test="sourceCode:CompoundCode | sourceCode:finiteForLoop |sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive">
            <xsl:value-of select="concat($curIndent,'for(',@index,' = 0; ',@index,'&lt;',@domain,' ; ',@index,' ++)',$new_line)"/>
            <xsl:apply-templates select="sourceCode:CompoundCode | sourceCode:finiteForLoop | sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive">
                <xsl:with-param name="curIndent" select="$curIndent"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <!-- compound code -->
    <xsl:template match="sourceCode:CompoundCode">
        <xsl:param name="curIndent"/>
        <xsl:if test="sourceCode:CompoundCode | sourceCode:finiteForLoop | sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive">
            <xsl:value-of select="concat($curIndent,'{','//',@name,$new_line)"/>
            <xsl:apply-templates select="sourceCode:subBufferAllocation | sourceCode:variableAllocation | sourceCode:bufferAllocation | sourceCode:CompoundCode | sourceCode:finiteForLoop | sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive">
                <xsl:with-param name="curIndent" select="concat($curIndent,$sglIndent)"/>
            </xsl:apply-templates>
            <xsl:value-of select="concat($curIndent,'}',$new_line)"/>
        </xsl:if>
    </xsl:template>
    
    <!-- Small blocks level -->
    
    <xsl:template match="sourceCode:userFunctionCall">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,@name,'(')"/>
        <!-- adding buffers -->
        <xsl:variable name="buffers">
            <xsl:apply-templates select="sourceCode:buffer | sourceCode:subBuffer | sourceCode:constant | sourceCode:bufferAtIndex"/>
        </xsl:variable>
        <!-- removing last coma -->
        <xsl:variable name="buffers" select="substring($buffers,1,string-length($buffers)-2)"/>
        <xsl:value-of select="concat($buffers,');',$new_line)"/>
    </xsl:template>
    
    <!-- Small blocks for special call like broadcast, fork and join -->
    <xsl:template match="sourceCode:specialBehavior">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,@behavior,'(')"/>
        <!-- adding buffers -->
        <xsl:variable name="buffers">
            <xsl:apply-templates select="sourceCode:inputBuffers | sourceCode:outputBuffers "/>
        </xsl:variable>
        <!-- removing last coma -->
        <xsl:variable name="buffers" select="substring($buffers,1,string-length($buffers)-1)"/>
        <xsl:value-of select="concat($buffers,');',$new_line)"/>
    </xsl:template>
    
    <!-- Small blocks for special call like broadcast, fork and join -->
    <xsl:template match="sourceCode:inputBuffers">
        <xsl:apply-templates select="sourceCode:buffer | sourceCode:subBuffer | sourceCode:constant"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:outputBuffers">
        <xsl:apply-templates select="sourceCode:buffer | sourceCode:subBuffer | sourceCode:constant"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:semaphorePost">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'SEM_post','(')"/>
        <xsl:value-of select="concat(sourceCode:buffer/@name,'[',@number,']')"/>
        <xsl:value-of select="concat(');',' //',@type,$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:semaphorePend">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'SEM_pend','(')"/>
        <xsl:value-of select="concat(sourceCode:buffer/@name,'[',@number,']')"/>
        <xsl:value-of select="concat(',SYS_FOREVER);',' //',@type,$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:semaphoreInit">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'semaphoreInit','(')"/>
        <!-- adding buffers -->
        <xsl:variable name="buffers">
            <xsl:apply-templates select="sourceCode:buffer | sourceCode:constant"/>
        </xsl:variable>
        <!-- removing last coma -->
        <xsl:variable name="buffers" select="substring($buffers,1,string-length($buffers)-1)"/>
        <xsl:value-of select="concat($buffers,');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:sendDma">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'sendData(')"/>
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(',',$coreName,',',@target,',')"/>
        <!-- adding buffer -->
        <xsl:value-of select="concat(sourceCode:buffer/@name,',',sourceCode:addressBuffer/sourceCode:buffer/@name,',',@index,',',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')',');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveDma">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'receiveData(')"/>
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(',',@source,',',$coreName,',')"/>
        <!-- adding buffer -->
        <xsl:value-of select="concat(@index,',',sourceCode:buffer/@name,',',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')',');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:sendMsg">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'sendData(')"/>
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(',',$coreName,',',@target,',')"/>
        <!-- adding buffer -->
        <xsl:value-of select="concat(sourceCode:buffer/@name,', NULL , NULL ,',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')',');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveMsg">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'receiveData(')"/>
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(',',@source,',',$coreName,',')"/>
        <!-- adding buffer -->
        <xsl:value-of select="concat(sourceCode:buffer/@name,', NULL,',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')',');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:launchThread">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'createThread(',@stackSize,',',@threadName,',&quot;',@threadName,'&quot;')"/>       
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:sendInit">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'Com_Init(MEDIUM_SEND,')"/>
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(',',$coreName,',',@connectedCoreId)"/>         
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveInit">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'Com_Init(MEDIUM_RCV,')"/>
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(',',@connectedCoreId,',',$coreName)"/>         
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:sendAddress">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'sendAddress(')"/>
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(', (void *)',sourceCode:buffer/@name,',',@connectedCoreId,',',$coreName)"/>         
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveAddress">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,sourceCode:buffer/@name,'[',@index,'] = (void *)','receiveAddress(')"/>
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(',',@index,',',@connectedCoreId,',',$coreName)"/>         
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:waitForCore">
        <xsl:param name="curIndent"/>
        <xsl:variable name="direction">
        <xsl:choose>
            <xsl:when test="sourceCode:routeStep/sourceCode:sender/@name=$coreName">SEND</xsl:when>
            <xsl:otherwise>RECEIVE</xsl:otherwise>
        </xsl:choose>
        </xsl:variable>
        <xsl:variable name="connectedCoreId">
            <xsl:choose>
                <xsl:when test="$direction='SEND'">
                    <xsl:value-of select="sourceCode:routeStep/sourceCode:receiver/@name"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="sourceCode:routeStep/sourceCode:sender/@name"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="concat($curIndent,'waitForOtherCore(',$direction,',')"/> 
        <xsl:apply-templates select="sourceCode:routeStep"/>
        <xsl:value-of select="concat(',',$connectedCoreId,',',$coreName)"/> 
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:routeStep">
        <xsl:choose>
            <xsl:when test="@type='dma'">
                <xsl:choose>
                    <xsl:when test="sourceCode:node/@def='RIO'">RIO</xsl:when>
                    <xsl:otherwise>EDMA3</xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="@type='msg'">
                <xsl:choose>
                    <xsl:when test="sourceCode:node/@def='TCP'">TCP</xsl:when>
                    <xsl:otherwise>msg</xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="@type='med'">
                <xsl:value-of select="@mediumDef"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <!-- Units level -->
    
    <xsl:template match="sourceCode:buffer">
        <xsl:value-of select="concat(@name,', ')"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:subBuffer">
        <xsl:value-of select="concat(@name,', ')"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:bufferAtIndex">
        <xsl:value-of select="concat('&amp;',@name,'[',@index,']',', ')"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:constant">
        <xsl:value-of select="concat(@value,'/*',@name,'*/',', ')"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:bufferAllocation">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,@type,' ',@name,'[',@size,'];',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:subBufferAllocation">
        <xsl:param name="curIndent"/>
        <xsl:choose>
            <xsl:when test="@modulo">
                <xsl:value-of select="concat($curIndent,@type,' *',@name,' = &amp;',@parentBuffer,' [','(',@index,' * ',@size,')',' % ',@modulo,'];',$new_line)"/>  
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat($curIndent,@type,' *',@name,' = &amp;',@parentBuffer,' [','(',@index,' * ',@size,')','];',$new_line)"/>  
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="sourceCode:variableAllocation">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,@type,' ',@name,' ;',$new_line)"/>
    </xsl:template>
    
</xsl:stylesheet>
