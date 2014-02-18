<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:dftools="http://net.sf.dftools"
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

    <!-- Top-level: dftools:workflow -> graph -->
    <xsl:template match="dftools:workflow">

        <xsl:element name="graph">
            <xsl:attribute name="type">DFTools Workflow</xsl:attribute>

            <xsl:element name="parameters"/>

            <xsl:element name="vertices">
                <xsl:apply-templates select="dftools:scenario"/>
                <xsl:apply-templates select="dftools:task"/>
            </xsl:element>

            <xsl:element name="edges">
                <xsl:apply-templates select="dftools:dataTransfer"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- scenario -->
    <xsl:template match="dftools:scenario">
        <xsl:element name="vertex">
            <xsl:attribute name="type" select="'Scenario source'"/>
            
            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="'scenario'"/>
            </xsl:call-template>
            
            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name" select="'id'"/>
                    <xsl:attribute name="value" select="'scenario'"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">plugin identifier</xsl:attribute>
                    <xsl:attribute name="value" select="@pluginId"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- tasks -->
    <xsl:template match="dftools:task">
        <xsl:element name="vertex">
            <xsl:attribute name="type">Task</xsl:attribute>

            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="@taskId"/>
            </xsl:call-template>

            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value" select="@taskId"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">plugin identifier</xsl:attribute>
                    <xsl:attribute name="value" select="@pluginId"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">variable declaration</xsl:attribute>
                    <xsl:apply-templates select="dftools:data[@key = 'variables']"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- Parameter instantiations -->
    <xsl:template match="dftools:variable">
        <xsl:element name="entry">
            <xsl:attribute name="key" select="@name"/>
            <xsl:attribute name="value" select="@value"/>
        </xsl:element>
    </xsl:template>

    <!-- dftools:dataTransfer -->
    <xsl:template match="dftools:dataTransfer">
        <xsl:element name="edge">
            <xsl:attribute name="type">Data transfer</xsl:attribute>
            <xsl:attribute name="source" select="@from"/>
            <xsl:attribute name="target" select="@to"/>
            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">source port</xsl:attribute>
                    <xsl:attribute name="value" select="@sourceport"/>
                </xsl:element>

                <xsl:element name="parameter">
                    <xsl:attribute name="name">target port</xsl:attribute>
                    <xsl:attribute name="value" select="@targetport"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
