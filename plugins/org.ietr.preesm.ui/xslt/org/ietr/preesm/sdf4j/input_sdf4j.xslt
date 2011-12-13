<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:graphml="http://graphml.graphdrawing.org/xmlns"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="text()"/>

    <!-- reads the layout in a file that has the same name as the source document,
        except with .layout extension. -->
    <xsl:param name="path"/>
    <xsl:variable name="file" select="replace($path, '(.+)[.].+', '$1.layout')"/>
    <xsl:variable name="layout" select="document($file)"/>

    <!-- returns two attributes x and y that contains the position of the vertex,
        if specified in $layout -->
    <xsl:template name="getVertexLayoutAttributes">
        <xsl:param name="vertexId"/>
        <xsl:if test="not(empty($layout))">
            <xsl:variable name="vertex" select="$layout/layout/vertices/vertex[@id = $vertexId]"/>
            <xsl:if test="not(empty($vertex))">
                <xsl:attribute name="x" select="$vertex/@x"/>
                <xsl:attribute name="y" select="$vertex/@y"/>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <!-- Top-level: graph -> graph -->
    <xsl:template match="graphml:graph[position() = 1 and @edgedefault = 'directed']">
        <xsl:element name="graph">
            <xsl:attribute name="type">Dataflow Graph</xsl:attribute>

            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">name</xsl:attribute>
                    <xsl:attribute name="value" select="graphml:data[@key = 'name']/text()"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">kind</xsl:attribute>
                    <xsl:choose>
                   	 	<xsl:when test="string(graphml:data[@key='kind'])">
                   	 		<xsl:attribute name="value" select="graphml:data[@key = 'kind']/text()"/>
                    	</xsl:when>
                    	<xsl:otherwise>
                    		<xsl:attribute name="value">sdf</xsl:attribute>
                    	</xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">graph parameter</xsl:attribute>
                    <xsl:apply-templates select="graphml:data[@key = 'parameters']"/>
                </xsl:element>

                <xsl:element name="parameter">
                    <xsl:attribute name="name">graph variable</xsl:attribute>
                    <xsl:apply-templates select="graphml:data[@key = 'variables']"/>
                </xsl:element>
            </xsl:element>

            <xsl:element name="vertices">
                <xsl:apply-templates select="graphml:node"/>
            </xsl:element>

            <xsl:element name="edges">
                <xsl:apply-templates select="graphml:edge"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- Parameter declarations -->
    <xsl:template match="graphml:data[@key = 'parameters']">
        <xsl:apply-templates select="graphml:parameter"/>
    </xsl:template>

    <!-- Variable declarations -->
    <xsl:template match="graphml:data[@key = 'variables']">
        <xsl:apply-templates select="graphml:variable"/>
    </xsl:template>

    <!-- node -->
    <xsl:template match="graphml:node">
        <xsl:element name="vertex">
            
            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="@id"/>
            </xsl:call-template>
		
		
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="@kind = 'vertex'">Vertex</xsl:when>
 					<xsl:when test="@kind = 'Broadcast'">Broadcast</xsl:when>
 					<xsl:when test="@kind = 'fork'">fork</xsl:when>
 					<xsl:when test="@kind = 'join'">join</xsl:when>
 					<xsl:when test="@kind = 'port'">
 						<xsl:choose>
 							<xsl:when test="@port_direction = 'Input'">Input port</xsl:when>
 							<xsl:otherwise>Output port</xsl:otherwise>
 						</xsl:choose>
 					</xsl:when>
					<xsl:when test="graphml:data[@key = 'kind']/text() = 'vertex'">Vertex</xsl:when>
 					<xsl:when test="graphml:data[@key = 'kind']/text() = 'Broadcast'">Broadcast</xsl:when>
 					<xsl:when test="graphml:data[@key = 'kind']/text() = 'fork'">fork</xsl:when>
 					<xsl:when test="graphml:data[@key = 'kind']/text() = 'join'">join</xsl:when>
 					<xsl:when test="graphml:data[@key = 'kind']/text() = 'port'">
 						<xsl:choose>
 							<xsl:when test="graphml:data[@key = 'port_direction']/text() = 'Input'">Input port</xsl:when>
 							<xsl:otherwise>Output port</xsl:otherwise>
 						</xsl:choose>
 					</xsl:when>
  					<xsl:otherwise>SpecificType</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="graphml:data[@key = 'kind']/text() = vertex">
				</xsl:if>
			
			</xsl:attribute>
			
            <xsl:element name="parameters">

                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value" select="@id"/>
                </xsl:element>

                <xsl:element name="parameter">
            		<xsl:attribute name="name">kind</xsl:attribute>
            		<xsl:choose>
            			<xsl:when test="@kind" >
            				<xsl:attribute name="value" select="@kind"/>
            			</xsl:when>
            			<xsl:otherwise>
            				<xsl:attribute name="value" select="graphml:data[@key = 'kind']/text()"/>
            			</xsl:otherwise>
            		</xsl:choose>
            	</xsl:element>

                <xsl:element name="parameter">
                    <xsl:attribute name="name">refinement</xsl:attribute>
                    <xsl:attribute name="value" select="graphml:data[@key = 'graph_desc']/text()"/>
                </xsl:element>

                <xsl:element name="parameter">
                    <xsl:attribute name="name">instance argument</xsl:attribute>
                    <xsl:apply-templates select="graphml:data[@key = 'arguments']"/>
                </xsl:element>
            </xsl:element>
         
        </xsl:element>
    </xsl:template>
 

	<!-- node instance arguments -->
	<xsl:template match="graphml:data[@key = 'arguments']">
    		<xsl:apply-templates select="graphml:argument"/>
	</xsl:template>

    <!-- instance arguments entry -->
    <xsl:template match="graphml:argument">
		<xsl:element name="entry">
			<xsl:attribute name="key" select="@name"/>
			<xsl:attribute name="value" select="@value"/>
		</xsl:element>
    </xsl:template>
    
    <!-- parameters element -->
    <xsl:template match="graphml:parameter">
        <xsl:element name="element">
            <xsl:attribute name="value" select="@name"/>
        </xsl:element>
    </xsl:template>
    
    <!-- variables entry -->
    <xsl:template match="graphml:variable">
        <xsl:element name="entry">
            <xsl:attribute name="key" select="@name"/>
            <xsl:attribute name="value" select="@value"/>
        </xsl:element>
    </xsl:template>

    <!-- input/output port -->
<!--    <xsl:template match="graphml:node[@kind = 'port']">
        <xsl:element name="vertex">
            <xsl:attribute name="type" select="concat(@port_direction, ' port')"/>
            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="@id"/>
            </xsl:call-template>

            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value" select="@id"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>-->

    <!-- edge -->
    <xsl:template match="graphml:edge">
        <xsl:element name="edge">
            <xsl:attribute name="type">Dataflow edge</xsl:attribute>
            <xsl:attribute name="source" select="@source"/>
            <xsl:attribute name="target" select="@target"/>
            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">source port</xsl:attribute>
                    <xsl:attribute name="value" select="@sourceport"/>
                </xsl:element>

                <xsl:element name="parameter">
                    <xsl:attribute name="name">target port</xsl:attribute>
                    <xsl:attribute name="value" select="@targetport"/>
                </xsl:element>

                <xsl:element name="parameter">
                    <xsl:attribute name="name">source production</xsl:attribute>
                    <xsl:attribute name="value" select="graphml:data[@key = 'edge_prod']/text()"/>
                </xsl:element>

                <xsl:element name="parameter">
                    <xsl:attribute name="name">target consumption</xsl:attribute>
                    <xsl:attribute name="value" select="graphml:data[@key = 'edge_cons']/text()"/>
                </xsl:element>

                <xsl:element name="parameter">
                    <xsl:attribute name="name">delay</xsl:attribute>
                    <xsl:attribute name="value" select="graphml:data[@key = 'edge_delay']/text()"/>
                </xsl:element>
                
                <xsl:element name="parameter">
                    <xsl:attribute name="name">data type</xsl:attribute>
                    <xsl:attribute name="value" select="graphml:data[@key = 'data_type']/text()"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
