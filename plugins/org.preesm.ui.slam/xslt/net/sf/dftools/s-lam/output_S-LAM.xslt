<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:grammar="java:org.ietr.dftools.graphiti.io.GrammarTransformer"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4 http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4/index.xsd" 
    xmlns:spirit="http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4"
    xmlns:slam="http://sourceforge.net/projects/dftools/slam">
    
    <xsl:output indent="yes" method="xml"/>
    
    <xsl:template match="text()"/>
    
    <!-- writes the layout in a file that has the same name as the target document,
        except with .layout extension. -->
    <xsl:param name="path"/>
    <xsl:variable name="file" select="replace($path, '(.+)[.].+', '$1.layout')"/>

	<!-- used to generate a unique Id and incremented at each use -->    
    <xsl:variable name="uniqueId" select="0"/>
    
    <!-- Top-level: graph -> ip-xact -->
    <xsl:template match="graph">
        
        <!-- layout information -->
        <xsl:result-document href="file:///{$file}" method="xml" indent="yes">
            <xsl:element name="layout">
                <xsl:element name="vertices">
                    <xsl:for-each select="vertices/vertex">
                        <xsl:element name="vertex">
                            <xsl:attribute name="id"
                                select="parameters/parameter[@name = 'id']/@value"/>
                            <xsl:attribute name="x" select="@x"/>
                            <xsl:attribute name="y" select="@y"/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:element>
        </xsl:result-document>
        
        <xsl:element name="spirit:design">
            
            <xsl:apply-templates select="parameters/parameter[@name != 'design parameters']" mode="vlnv"/>
            
            <xsl:element name="spirit:componentInstances">
                <xsl:apply-templates select="vertices/vertex[@type != 'hierConnection']"/>
            </xsl:element>    
            
            <xsl:element name="spirit:interconnections">
                <xsl:apply-templates select="edges/edge[@type != 'hierConnection']"/>
            </xsl:element>
            
            <xsl:element name="spirit:hierConnections">
                <xsl:apply-templates select="vertices/vertex[@type = 'hierConnection']"/>
            </xsl:element>
            
            <!-- Managing vendor extensions -->
            <xsl:element name="spirit:vendorExtensions">
                <xsl:element name="slam:componentDescriptions">
                    <xsl:for-each-group select="vertices/vertex[@type != 'hierConnection']" group-by="parameters/parameter[@name = 'definition']/@value">
                        <!-- Even in case of several instances of the same component, there is always 1 vendor extension -->
                        <xsl:apply-templates select="current-group( )[1]" mode="vendorExtensions"/>
                    </xsl:for-each-group>
                </xsl:element>
                
                <!-- Hierarchical connections have no vendor extension -->
                <xsl:element name="slam:linkDescriptions">
                    <xsl:apply-templates select="edges/edge[@type != 'hierConnection']" mode="vendorExtensions"/>
                </xsl:element>
                
                <!-- Hierarchical connections have no vendor extension -->
                <xsl:element name="slam:designDescription">
                    <xsl:element name="slam:parameters">
                        <xsl:apply-templates select="parameters/parameter[@name = 'design parameters']/entry" mode="designparameters"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Design VLNV parameter declarations -->
    <xsl:template match="parameter" mode="vlnv">
        <xsl:element name="spirit:{@name}">
            <xsl:value-of select="@value"/>
        </xsl:element>
    </xsl:template>
    
    <!-- Design custom parameter declarations -->
    <xsl:template match="entry" mode="designparameters">
        <xsl:element name="slam:parameter">
            <xsl:attribute name="slam:key" select="@key"/>
            <xsl:attribute name="slam:value" select="@value"/>
        </xsl:element>
    </xsl:template>
    
    <!-- variables variable -->
    <xsl:template match="entry" mode="variable">
        <variable name="{@key}" value="{@value}"/>
    </xsl:template>
    
    
    <!-- Component instances -->
    <xsl:template match="vertex[@type != 'hierConnection']">
        <xsl:element name="spirit:componentInstance">
            <xsl:element name="spirit:instanceName">
                <xsl:value-of select="parameters/parameter[@name = 'id']/@value"/>
            </xsl:element>
            <xsl:element name="spirit:componentRef">
                <xsl:attribute name="spirit:library" select="parameters/parameter[@name = 'library']/@value"/>
                <!-- Careful! The spirit name is used as definition -->
                <xsl:attribute name="spirit:name" select="parameters/parameter[@name = 'definition']/@value"/>
                <xsl:attribute name="spirit:vendor" select="parameters/parameter[@name = 'vendor']/@value"/>
                <xsl:attribute name="spirit:version" select="parameters/parameter[@name = 'version']/@value"/>
            </xsl:element>
            <xsl:element name="spirit:configurableElementValues">
                <xsl:apply-templates select="parameters/parameter[@name = 'custom parameters']"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- custom parameters declarations -->
    <xsl:template match="parameter[@name = 'custom parameters']">
        <xsl:for-each select="entry">
            <xsl:element name="spirit:configurableElementValue">
                <xsl:attribute name="spirit:referenceId" select="@key"/>
                <xsl:value-of select="@value"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <!-- Component instances vendor extensions -->
    <xsl:template match="vertex[@type != 'hierConnection']" mode="vendorExtensions">
        <xsl:element name="slam:componentDescription">
            <xsl:attribute name="slam:componentRef" select="parameters/parameter[@name = 'definition']/@value"/>
            <xsl:attribute name="slam:componentType" select="@type"/>
            <xsl:attribute name="slam:refinement" select="parameters/parameter[@name = 'refinement']/@value"/>
            
            <xsl:if test="contains(@type,'ComNode')">
                <xsl:attribute name="slam:speed" select="parameters/parameter[@name = 'speed']/@value"/>
            </xsl:if> 
            
            <xsl:if test="contains(@type,'Mem')">
                <xsl:attribute name="slam:size" select="parameters/parameter[@name = 'memSize']/@value"/>
            </xsl:if>  
            
            <xsl:if test="contains(@type,'Dma')">
                <xsl:attribute name="slam:setupTime" select="parameters/parameter[@name = 'setupTime']/@value"/>
            </xsl:if> 
        </xsl:element>
    </xsl:template>
    
    <!-- interconnections -->
    <xsl:template match="edge[@type!='hierConnection']">
        <xsl:element name="spirit:interconnection">
            <xsl:element name="spirit:name">
                <xsl:variable name="linkId" select="parameters/parameter[@name = 'id']/@value"/>
                <!-- An id is generated for the link if not present -->
                <xsl:choose>
                    <xsl:when test="string-length($linkId)=0">
                        <xsl:value-of select="concat(@source,'|',parameters/parameter[@name = 'source port']/@value,'|',@target,'|',parameters/parameter[@name = 'target port']/@value)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$linkId"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
            
            <xsl:element name="spirit:activeInterface">
                <xsl:attribute name="spirit:busRef" select="parameters/parameter[@name = 'source port']/@value"/>
                <xsl:attribute name="spirit:componentRef" select="@source"/>
            </xsl:element>
            <xsl:element name="spirit:activeInterface">
                <xsl:attribute name="spirit:busRef" select="parameters/parameter[@name = 'target port']/@value"/>
                <xsl:attribute name="spirit:componentRef" select="@target"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- interconnections vendor extensions -->
    <xsl:template match="edge" mode="vendorExtensions">
        <xsl:variable name="directedLink">
            <xsl:choose>
                <xsl:when test="contains(@type,'undirected')">undirected</xsl:when>
                <xsl:otherwise>directed</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="linkId" select="parameters/parameter[@name = 'id']/@value"/>
        <xsl:variable name="linkType" select="@type"/>
        <xsl:variable name="processedType" select="replace($linkType,$directedLink,'')"/>
        
        <xsl:element name="slam:linkDescription">
            <xsl:attribute name="slam:linkType" select="$processedType"/>
            <xsl:attribute name="slam:directedLink" select="$directedLink"/>
            <!-- An ID is generated for the link if not present -->
            <xsl:choose>
                <xsl:when test="string-length($linkId)=0">
                    <xsl:attribute name="slam:referenceId" select="concat(@source,'|',parameters/parameter[@name = 'source port']/@value,'|',@target,'|',parameters/parameter[@name = 'target port']/@value)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="slam:referenceId" select="$linkId"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <!-- hierarchical connections -->
    <xsl:template match="vertex[@type = 'hierConnection']">
        <xsl:element name="spirit:hierConnection">
            <xsl:variable name="id" select="./parameters/parameter[@name = 'id']/@value"/>
            <xsl:attribute name="spirit:interfaceRef" select="$id"/>
            <xsl:element name="spirit:activeInterface">
                <xsl:variable name="hierEdge" select="//edges/edge[@type = 'hierConnection' and @target = $id][1]"/>
                <xsl:attribute name="spirit:busRef" select="$hierEdge/parameters/parameter[@name = 'source port']/@value"/>
                <xsl:attribute name="spirit:componentRef" select="$hierEdge/@source"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>
