<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:template name="setLayout">
        <xsl:element name="layout">
            <xsl:element name="vertices">
                <xsl:for-each select="vertices/vertex">
                    <xsl:element name="vertex">
                        <xsl:attribute name="id" select="parameters/parameter[@name = 'id']/@value"/>
                        <xsl:attribute name="x" select="@x"/>
                        <xsl:attribute name="y" select="@y"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
