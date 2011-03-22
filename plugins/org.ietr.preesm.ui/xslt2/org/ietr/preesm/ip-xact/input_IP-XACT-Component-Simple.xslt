<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:spirit="http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4">
    
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
    
    <!-- Top-level: design -->
    <xsl:template match="spirit:component">
        <xsl:element name="graph">
            <xsl:attribute name="type">Spirit IP-XACT simple component</xsl:attribute>
            
            <!-- VLNV of the design -->
            <xsl:element name="parameters">
                <xsl:call-template name="spirit:vendor"/>
                <xsl:call-template name="spirit:library"/>
                <xsl:call-template name="spirit:name"/>
                <xsl:call-template name="spirit:version"/>
                <xsl:apply-templates select="spirit:memoryMaps"/>
            </xsl:element>
            
            
            <!-- bus interfaces and possible sub-design -->
            <xsl:element name="vertices">
                <xsl:apply-templates select="spirit:busInterfaces"/>
                <xsl:apply-templates select="spirit:model"/>
            </xsl:element>
            
            <!-- Interconnections -->
            <xsl:element name="edges">
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- template for the bus interfaces -->
    <xsl:template match="spirit:busInterface">
        <xsl:element name="vertex">
            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="spirit:name"/>
            </xsl:call-template>
            <xsl:attribute name="type">Port</xsl:attribute>
            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value" select="spirit:name"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">busVendor</xsl:attribute>
                    <xsl:attribute name="value" select="spirit:busType/@spirit:vendor"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">busLibrary</xsl:attribute>
                    <xsl:attribute name="value" select="spirit:busType/@spirit:library"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">busName</xsl:attribute>
                    <xsl:attribute name="value" select="spirit:busType/@spirit:name"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">busVersion</xsl:attribute>
                    <xsl:attribute name="value" select="spirit:busType/@spirit:version"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- template for the addressSpace -->
    <xsl:template match="spirit:memoryMap">
        <xsl:element name="parameter">
            <xsl:attribute name="name">memoryAddress</xsl:attribute>
            <xsl:attribute name="value" select="spirit:addressBlock/spirit:baseAddress"/>
        </xsl:element>
        <xsl:element name="parameter">
            <xsl:attribute name="name">memoryRange</xsl:attribute>
            <xsl:attribute name="value" select="spirit:addressBlock/spirit:range"/>
        </xsl:element>
        <xsl:element name="parameter">
            <xsl:attribute name="name">memoryWidth</xsl:attribute>
            <xsl:attribute name="value" select="spirit:addressBlock/spirit:width"/>
        </xsl:element>
    </xsl:template>
    
    <!-- template for the sub-design -->
    <xsl:template match="spirit:view">
        <!-- only for hierarchy views -->
        <xsl:if test="spirit:envIdentifier='::Hierarchy'">
            <xsl:element name="vertex">
                <xsl:call-template name="getVertexLayoutAttributes">
                    <xsl:with-param name="vertexId" select="spirit:name"/>
                </xsl:call-template>
                <xsl:attribute name="type">subDesign</xsl:attribute>
                <xsl:element name="parameters">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">id</xsl:attribute>
                        <xsl:attribute name="value" select="spirit:name"/>
                    </xsl:element>
                    <xsl:apply-templates select="spirit:hierarchyRef"/>
                    
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <!-- template for VLNV and refinement of a hierarchy reference -->
    <xsl:template match="spirit:hierarchyRef">
        <xsl:element name="parameter">
            <xsl:attribute name="name">vendor</xsl:attribute>
            <xsl:attribute name="value" select="@spirit:vendor"/>
        </xsl:element>
        <xsl:element name="parameter">
            <xsl:attribute name="name">library</xsl:attribute>
            <xsl:attribute name="value" select="@spirit:library"/>
        </xsl:element>
        <xsl:element name="parameter">
            <xsl:attribute name="name">refinement</xsl:attribute>
            <xsl:attribute name="value" select="@spirit:name"/>
        </xsl:element>
        <xsl:element name="parameter">
            <xsl:attribute name="name">version</xsl:attribute>
            <xsl:attribute name="value" select="@spirit:version"/>
        </xsl:element>
    </xsl:template>
    
    <!-- templates for the VLNV of the component -->
    <xsl:template name="spirit:vendor">
        <xsl:element name="parameter">
            <xsl:attribute name="name">cmpVendor</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="spirit:vendor"/></xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="spirit:library">
        <xsl:element name="parameter">
            <xsl:attribute name="name">cmpLibrary</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="spirit:library"/></xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="spirit:name">
        <xsl:element name="parameter">
            <xsl:attribute name="name">cmpName</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="spirit:name"/></xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="spirit:version">
        <xsl:element name="parameter">
            <xsl:attribute name="name">cmpVersion</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="spirit:version"/></xsl:attribute>
        </xsl:element>
    </xsl:template>
   
</xsl:stylesheet>
