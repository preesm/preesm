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
        <xsl:if test="$coreType='Tesla'">
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
        <xsl:value-of select="concat($curIndent,'#include &quot;../include/x86.h&quot;',$new_line)"/>
        <xsl:value-of select="$new_line"/>
    </xsl:template>
    
    <!-- Declaring thread functions prototypes -->
    <xsl:template match="sourceCode:threadDeclaration" mode="prototype">
        <xsl:value-of select="concat($curIndent,'void *',@name,'_',$coreName,'(void *arg);',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:threadDeclaration">
        <xsl:value-of select="concat($curIndent,'void *',@name,'_',$coreName,'(void *arg){',$new_line)"/>
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
        <xsl:apply-templates select="sourceCode:bufferAllocation | sourceCode:variableAllocation | sourceCode:subBufferAllocation">
            <xsl:with-param name="curIndent" select="$curIndent"/> 
        </xsl:apply-templates>
        <xsl:value-of select="$new_line"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:linearCodeContainer">
        <xsl:param name="curIndent"/>
        <xsl:if test="sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:semaphoreInit | sourceCode:sendMsg | sourceCode:receiveMsg | sourceCode:launchThread | sourceCode:sendInit | sourceCode:receiveInit">
            <xsl:value-of select="concat($curIndent,'{',$new_line)"/>
            
            <xsl:apply-templates select="sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:semaphoreInit | sourceCode:sendMsg | sourceCode:receiveMsg | sourceCode:launchThread | sourceCode:sendInit | sourceCode:receiveInit">
                <xsl:with-param name="curIndent" select="concat($curIndent,$sglIndent)"/>
            </xsl:apply-templates>
            
            <xsl:value-of select="concat($curIndent,'}',$new_line)"/>
            <xsl:value-of select="$new_line"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="sourceCode:forLoop">
        <xsl:param name="curIndent"/>
        <xsl:if test="sourceCode:subBufferAllocation | sourceCode:variableAllocation | sourceCode:CompoundCode | sourceCode:finiteForLoop | sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost |  sourceCode:sendMsg | sourceCode:receiveMsg | sourceCode:specialBehavior">
            <xsl:value-of select="concat($curIndent,'for(;;){',$new_line)"/>
            
            <xsl:apply-templates select="sourceCode:subBufferAllocation | sourceCode:variableAllocation | sourceCode:CompoundCode | sourceCode:finiteForLoop | sourceCode:userFunctionCall | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:sendMsg | sourceCode:receiveMsg | sourceCode:specialBehavior">
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
        <xsl:if test="sourceCode:CompoundCode | sourceCode:finiteForLoop | sourceCode:userFunctionCall | sourceCode:Assignment | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive">
            <xsl:value-of select="concat($curIndent,'{','//',@name,$new_line)"/>
            <xsl:apply-templates select="sourceCode:subBufferAllocation | sourceCode:variableAllocation | sourceCode:bufferAllocation | sourceCode:CompoundCode | sourceCode:finiteForLoop | sourceCode:userFunctionCall | sourceCode:Assignment | sourceCode:semaphorePend | sourceCode:semaphorePost | sourceCode:send | sourceCode:receive">
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
            <xsl:apply-templates select="sourceCode:buffer | sourceCode:subBuffer | sourceCode:variable |sourceCode:constant | sourceCode:bufferAtIndex"/>
        </xsl:variable>
        <!-- removing last coma -->
        <xsl:variable name="buffers2" select="substring($buffers,1,string-length($buffers)-2)"/>
        <xsl:value-of select="concat($buffers2,'); //',@comment,$new_line)"/>
    </xsl:template>
    
    <!-- Small blocks for special assignment, assign a variable a value -->
    <xsl:template match="sourceCode:Assignment">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,@var,' = ', text(),';',$new_line)"/>
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
        <xsl:variable name="buffers2" select="substring($buffers,1,string-length($buffers)-1)"/>
        <xsl:value-of select="concat($buffers2,');',$new_line)"/>
    </xsl:template>
    
    <!-- Small blocks for special call like broadcast, fork and join -->
    <xsl:template match="sourceCode:inputBuffers">
        <xsl:apply-templates select="sourceCode:buffer | sourceCode:subBuffer | sourceCode:constant"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:outputBuffers">
        <xsl:apply-templates select="sourceCode:buffer | sourceCode:subBuffer | sourceCode:constant"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:sendMsg">
        <xsl:param name="curIndent"/>
		<xsl:if  test="@phase='Start'">
	        <xsl:value-of select="concat($curIndent,'sendData(')"/>
	        <xsl:apply-templates select="sourceCode:routeStep"/>
	        <xsl:value-of select="concat(',',$coreName,',',@target,',')"/>
	        <!-- adding buffer -->
	        <xsl:value-of select="concat(sourceCode:buffer/@name,',',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')','); // ',@comment,$new_line)"/>
   		</xsl:if>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveMsg">
        <xsl:param name="curIndent"/>
        <xsl:if  test="@phase='End'">
	        <xsl:value-of select="concat($curIndent,'receiveData(')"/>
	        <xsl:apply-templates select="sourceCode:routeStep"/>
	        <xsl:value-of select="concat(',',@source,',',$coreName,',')"/>
	        <!-- adding buffer -->
	        <xsl:value-of select="concat(sourceCode:buffer/@name,',',sourceCode:buffer/@size,'*sizeof(',sourceCode:buffer/@type,')','); // ',@comment,$new_line)"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="sourceCode:sendInit">
        <xsl:param name="curIndent"/>
        <xsl:choose>
            <xsl:when test="sourceCode:routeStep/@type='med' and sourceCode:routeStep/@mediumDef='tcp_c64'">
                <xsl:value-of select="concat($curIndent,'initC64TcpServer(7')"/>  
                <xsl:value-of select="concat(');',$new_line)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat($curIndent,'comInit(MEDIUM_SEND,')"/>
                <xsl:apply-templates select="sourceCode:routeStep"/>
                <xsl:value-of select="concat(',',$coreName,',',@connectedCoreId)"/>       
                <xsl:value-of select="concat(');',$new_line)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="sourceCode:receiveInit">
        <xsl:param name="curIndent"/>
        <xsl:choose>
            <xsl:when test="sourceCode:routeStep/@type='med' and sourceCode:routeStep/@mediumDef='tcp_c64'">
                <xsl:value-of select="concat($curIndent,'initC64TcpServer(7')"/>  
                <xsl:value-of select="concat(');',$new_line)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat($curIndent,'comInit(MEDIUM_RCV,')"/>
                <xsl:apply-templates select="sourceCode:routeStep"/>
                <xsl:value-of select="concat(',',@connectedCoreId,',',$coreName)"/> 
                <xsl:value-of select="concat(');',$new_line)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="sourceCode:launchThread">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,'pthread_create(&amp;comThread, NULL, ',@threadName,'_',$coreName,', NULL')"/>       
        <xsl:value-of select="concat(');',$new_line)"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:routeStep">
        <xsl:choose>
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
    
    <xsl:template match="sourceCode:variable">
        <xsl:value-of select="concat(@name,', ')"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:constant">
        <xsl:value-of select="concat(@value,'/*',@name,'*/',', ')"/>
    </xsl:template>
    
    <xsl:template match="sourceCode:bufferAllocation">
        <xsl:param name="curIndent"/>
        <xsl:choose>
            <xsl:when test="@size = '0' ">
                <xsl:value-of select="concat($curIndent,@type,' *',@name,'; //',@comment,$new_line)"/>
            </xsl:when>
            <xsl:otherwise>   
                <xsl:value-of select="concat($curIndent,@type,' ',@name,'[',@size,']; //',@comment,$new_line)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    <xsl:template match="sourceCode:subBufferAllocation">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,@type,' *',@name,' = &amp;',@parentBuffer,' [',@index,']; //',@comment,$new_line)"/>  
 <!--       <xsl:choose>
            <xsl:when test="@modulo">
                <xsl:choose>
                    <xsl:when test="offset = '0'">
                        <xsl:value-of select="concat($curIndent,@type,' *',@name,' = &amp;',@parentBuffer,' [','(',@index,')','];',$new_line)"/>  
                    </xsl:when>
                    <xsl:when test="@modulo = '0'">
                        <xsl:value-of select="concat($curIndent,@type,' *',@name,' = &amp;',@parentBuffer,' [','(',@index,' * ',@offset,')','];',$new_line)"/>  
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($curIndent,@type,' *',@name,' = &amp;',@parentBuffer,' [','(',@index,' * ',@offset,')',' % ',@modulo,'];',$new_line)"/>  
                    </xsl:otherwise>    
                </xsl:choose>
            </xsl:when>
            <xsl:when test="@offset = '0'">
                <xsl:value-of select="concat($curIndent,@type,' *',@name,' = &amp;',@parentBuffer,' [','(',@index,')','];',$new_line)"/>  
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat($curIndent,@type,' *',@name,' = &amp;',@parentBuffer,' [','(',@index,' * ',@offset,')','];',$new_line)"/>  
            </xsl:otherwise>
        </xsl:choose> -->
    </xsl:template>
    
    <xsl:template match="sourceCode:variableAllocation">
        <xsl:param name="curIndent"/>
        <xsl:value-of select="concat($curIndent,@type,' ',@name,' ;',$new_line)"/>
    </xsl:template>
    
</xsl:stylesheet>
