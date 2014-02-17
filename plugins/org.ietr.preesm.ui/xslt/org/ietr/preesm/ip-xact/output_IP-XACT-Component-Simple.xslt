<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:grammar="java:org.ietr.dftools.graphiti.io.GrammarTransformer"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4 http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4/index.xsd" 
    xmlns:spirit="http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4">
    
    <xsl:output indent="yes" method="xml"/>
    
    <xsl:template match="text()"/>
    
    <!-- writes the layout in a file that has the same name as the target document,
        except with .layout extension. -->
    <xsl:param name="path"/>
    <xsl:variable name="file" select="replace($path, '(.+)[.].+', '$1.layout')"/>
    
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
        
        <xsl:element name="spirit:component">
            <xsl:apply-templates select="parameters" mode="vlnv"/>
            <xsl:element name="spirit:busInterfaces">
                <xsl:apply-templates select="vertices/vertex[@type='Port']"/>
            </xsl:element>
            <xsl:element name="spirit:model">
                <xsl:apply-templates select="vertices/vertex[@type='subDesign']"/>
            </xsl:element>
            <xsl:call-template name="memoryMap"/>
        </xsl:element>
    </xsl:template>
    
    <!-- Design parameter declarations -->
    <xsl:template match="parameter" mode="vlnv">
        <xsl:choose>
            <xsl:when test="@name='cmpVendor'">
                <xsl:element name="spirit:vendor">
                    <xsl:value-of select="@value"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@name='cmpLibrary'">
                <xsl:element name="spirit:library">
                    <xsl:value-of select="@value"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@name='cmpName'">
                <xsl:element name="spirit:name">
                    <xsl:value-of select="@value"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@name='cmpVersion'">
                <xsl:element name="spirit:version">
                    <xsl:value-of select="@value"/>
                </xsl:element>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <!-- memoryMap declarations -->
    <xsl:template name="memoryMap">
        <xsl:element name="spirit:memoryMaps">
            <xsl:element name="spirit:memoryMap">
                <xsl:element name="spirit:name">mainMM</xsl:element>
                <xsl:element name="spirit:addressBlock">
                    <xsl:element name="spirit:name">memory</xsl:element>
                    <xsl:element name="spirit:baseAddress">
                        <xsl:value-of select="parameters/parameter[@name='memoryAddress']/@value"/>
                    </xsl:element>
                    <xsl:element name="spirit:range">
                        <xsl:value-of select="parameters/parameter[@name='memoryRange']/@value"/>
                    </xsl:element>
                    <xsl:element name="spirit:width">
                        <xsl:value-of select="parameters/parameter[@name='memoryWidth']/@value"/>
                    </xsl:element>
                    <xsl:element name="spirit:usage">memory</xsl:element>
                    <xsl:element name="spirit:access">read-write</xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Component instances -->
    <xsl:template match="vertex[@type='Port']">
        <xsl:element name="spirit:busInterface">
            <xsl:element name="spirit:name">
                <xsl:value-of select="parameters/parameter[@name = 'id']/@value"/>
            </xsl:element>
            <xsl:element name="spirit:busType">
                <xsl:attribute name="spirit:library" select="parameters/parameter[@name = 'busLibrary']/@value"/>
                <xsl:attribute name="spirit:name" select="parameters/parameter[@name = 'busName']/@value"/>
                <xsl:attribute name="spirit:vendor" select="parameters/parameter[@name = 'busVendor']/@value"/>
                <xsl:attribute name="spirit:version" select="parameters/parameter[@name = 'busVersion']/@value"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- Component instances -->
    <xsl:template match="vertex[@type='subDesign']">
        <xsl:element name="spirit:views">
            <xsl:element name="spirit:view">
                <xsl:element name="spirit:name">
                    <xsl:value-of select="parameters/parameter[@name = 'id']/@value"/>
                </xsl:element>
                <xsl:element name="spirit:envIdentifier">::Hierarchy</xsl:element>
                <xsl:element name="spirit:hierarchyRef">
                    <xsl:attribute name="spirit:library" select="parameters/parameter[@name = 'library']/@value"/>
                    <xsl:attribute name="spirit:name" select="parameters/parameter[@name = 'refinement']/@value"/>
                    <xsl:attribute name="spirit:vendor" select="parameters/parameter[@name = 'vendor']/@value"/>
                    <xsl:attribute name="spirit:version" select="parameters/parameter[@name = 'version']/@value"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>
