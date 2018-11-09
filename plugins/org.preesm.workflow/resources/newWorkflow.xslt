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
            <xsl:attribute name="pluginId">org.ietr.preesm.scenario.task</xsl:attribute>
        </dftools:scenario>
    </xsl:template>

    <xsl:template match="preesm:task">
        <dftools:task>
            <xsl:copy-of select="@*" />
            <xsl:apply-templates/>
        </dftools:task>
    </xsl:template>

    <xsl:template match="data">
        <data>
            <xsl:copy-of select="@*" />
            <xsl:apply-templates/>
        </data>
    </xsl:template>

    <xsl:template match="variable">
        <dftools:variable>
            <xsl:copy-of select="@*" />
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
                    <xsl:attribute name="from">scenario</xsl:attribute>
                    <xsl:attribute name="sourceport">scenario</xsl:attribute>
                    <xsl:copy-of select="@targetport|@to" />
                </dftools:dataTransfer>
            </xsl:when>
            <xsl:when test="@from='__algorithm'">
                <dftools:dataTransfer>
                    <xsl:attribute name="from">scenario</xsl:attribute>
                    <xsl:attribute name="sourceport">algorithm</xsl:attribute>
                    <xsl:copy-of select="@targetport|@to" />
                </dftools:dataTransfer>
            </xsl:when>
            <xsl:when test="@from='__architecture'">
                <dftools:dataTransfer>
                    <xsl:attribute name="from">scenario</xsl:attribute>
                    <xsl:attribute name="sourceport">architecture</xsl:attribute>
                    <xsl:copy-of select="@targetport|@to" />
                </dftools:dataTransfer>
            </xsl:when>
            <xsl:otherwise>
                <dftools:dataTransfer>
                    <xsl:copy-of select="@*" />
                </dftools:dataTransfer>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>
</xsl:stylesheet>
