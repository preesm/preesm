<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns="http://ietr-image.insa-rennes.fr/projects/Preesm"
    xmlns:preesm="http://ietr-image.insa-rennes.fr/projects/Preesm"
    xmlns:dftools="http://net.sf.dftools"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    
    <xsl:output indent="yes" method="xml"/>
    <xsl:template match="text()"/>
    
    <xsl:template match="preesm:workflow">
        <dftools:workflow>
            <xsl:apply-templates/>
        </dftools:workflow>
    </xsl:template>
    
    <xsl:template match="preesm:scenario">
        <dftools:scenario>
            <xsl:attribute name="pluginId" select="'org.ietr.preesm.scenario.task'"/>
        </dftools:scenario>
    </xsl:template>
    
    <xsl:template match="preesm:task">
        <dftools:task>
            <xsl:attribute name="pluginId" select="@pluginId"/>
            <xsl:attribute name="taskId" select="@taskId"/>
            <dftools:data>
                <xsl:attribute name="key" select="data/@key"/>
                <xsl:apply-templates/>
            </dftools:data>
        </dftools:task>
    </xsl:template>
    
    <xsl:template match="variable">
        <dftools:variable>
            <xsl:attribute name="name" select="@name"/>
            <xsl:attribute name="value" select="@value"/>
        </dftools:variable>
    </xsl:template>
    
    <xsl:template match="preesm:dataTransfer">
        <xsl:choose>
            <xsl:when test="@from='__scenario' and @to='__algorithm'">
            </xsl:when>
            <xsl:when test="@from='__scenario' and @to='__architecture'">
            </xsl:when>
            <xsl:when test="@from='__scenario'">
                <dftools:dataTransfer>
                    <xsl:attribute name="from" select="'scenario'"/>
                    <xsl:attribute name="sourceport" select="@targetport"/>
                    <xsl:attribute name="targetport" select="@targetport"/>
                    <xsl:attribute name="to" select="@to"/>
                </dftools:dataTransfer>
            </xsl:when>
            <xsl:when test="@from='__algorithm'">
                <dftools:dataTransfer>
                    <xsl:attribute name="from" select="'scenario'"/>
                    <xsl:attribute name="sourceport" select="@targetport"/>
                    <xsl:attribute name="targetport" select="@targetport"/>
                    <xsl:attribute name="to" select="@to"/>
                </dftools:dataTransfer>
            </xsl:when>
            <xsl:when test="@from='__architecture'">
                <dftools:dataTransfer>
                    <xsl:attribute name="from" select="'scenario'"/>
                    <xsl:attribute name="sourceport" select="@targetport"/>
                    <xsl:attribute name="targetport" select="@targetport"/>
                    <xsl:attribute name="to" select="@to"/>
                </dftools:dataTransfer>
            </xsl:when>
            <xsl:otherwise>
                <dftools:dataTransfer>
                        <xsl:attribute name="from" select="@from"/>
                        <xsl:attribute name="sourceport" select="@sourceport"/>
                        <xsl:attribute name="targetport" select="@targetport"/>
                        <xsl:attribute name="to" select="@to"/>
                </dftools:dataTransfer>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
</xsl:stylesheet>
