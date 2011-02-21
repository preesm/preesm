<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    
    <xsl:import href="output_layout.xslt"/>

    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="text()"/>

    <!-- writes the layout in a file that has the same name as the target document,
        except with .layout extension. -->
    <xsl:param name="path"/>
    <xsl:variable name="file" select="replace($path, '(.+)[.].+', '$1.layout')"/>

    <!-- Top-level: graph -> preesm:workflow -->
    <xsl:template match="graph">
        
        <!-- layout information -->
        <xsl:result-document href="file:///{$file}" method="xml" indent="yes">
            <xsl:call-template name="setLayout"/>
        </xsl:result-document>

        <preesm:workflow>
            <xsl:if test="not(empty(vertices/vertex[@type = 'Algorithm source']))">
                <preesm:algorithm/>
            </xsl:if>

            <xsl:if test="not(empty(vertices/vertex[@type = 'Architecture source']))">
                <preesm:architecture/>
            </xsl:if>
            
            <xsl:apply-templates select="vertices/vertex[@type = 'Scenario source']"/>
            <xsl:apply-templates select="vertices/vertex[@type = 'Task']"/>
            <xsl:apply-templates select="edges/edge"/>
        </preesm:workflow>
    </xsl:template>

    <!-- scenario -->
    <xsl:template match="vertex[@type = 'Scenario source']">
        <preesm:scenario>
            <xsl:attribute name="pluginId"
                select="parameters/parameter[@name = 'plugin identifier']/@value"/>
        </preesm:scenario>
    </xsl:template>
    
    <!-- tasks -->
    <xsl:template match="vertex[@type = 'Task']">
        <preesm:task>
            <xsl:attribute name="pluginId"
                select="parameters/parameter[@name = 'plugin identifier']/@value"/>
            <xsl:attribute name="taskId" select="parameters/parameter[@name = 'id']/@value"/>
            <data key="variables">
                <xsl:apply-templates
                    select="parameters/parameter[@name = 'variable declaration']/entry"/>
            </data>
        </preesm:task>
    </xsl:template>
    
    <!-- variable -->
    <xsl:template match="entry">
        <xsl:element name="variable">
            <xsl:attribute name="name" select="@key"/>
            <xsl:attribute name="value" select="@value"/>
        </xsl:element>
    </xsl:template>

    <!-- data transfers -->
    <xsl:template match="edge">
        <preesm:dataTransfer>
            <xsl:attribute name="from" select="@source"/>
            <xsl:attribute name="to" select="@target"/>
            <xsl:attribute name="sourceport"
                select="parameters/parameter[@name = 'source port']/@value"/>
            <xsl:attribute name="targetport"
                select="parameters/parameter[@name = 'target port']/@value"/>
        </preesm:dataTransfer>
    </xsl:template>

</xsl:stylesheet>
