<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="text()"/>

    <!-- Module -->
    <xsl:template match="Module">
        <graph type="IDL Module">
            <parameters>
                <parameter name="id" value="{Id/text()}"/>
                <parameter name="module parameter">
                    <xsl:apply-templates select="Parameter"/>
                </parameter>
            </parameters>
            <vertices>
                <xsl:apply-templates select="InputArgument"/>
                <xsl:apply-templates select="OutputArgument"/>
            </vertices>
            <edges/>
        </graph>
    </xsl:template>
    
    <!-- Parameter -->
    <xsl:template match="Parameter">
        <element value="{Id/text()}"/>
    </xsl:template>

    <!-- Input Port -->
    <xsl:template match="InputArgument">
        <vertex type="Input port">
            <parameters>
                <parameter name="id" value="{Id/text()}"/>
            </parameters>
        </vertex>
    </xsl:template>
    
    
    <!-- Input Port -->
    <xsl:template match="OutputArgument">
        <vertex type="Output port">
            <parameters>
                <parameter name="id" value="{Id/text()}"/>
            </parameters>
        </vertex>
    </xsl:template>

</xsl:stylesheet>
