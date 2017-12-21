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
    <xsl:template match="spirit:design">
        <xsl:element name="graph">
            <xsl:attribute name="type">Spirit IP-XACT simple design</xsl:attribute>
            
            <!-- VLNV of the design -->
            <xsl:element name="parameters">
                <xsl:apply-templates select="spirit:vendor"/>
                <xsl:apply-templates select="spirit:library"/>
                <xsl:apply-templates select="spirit:name"/>
                <xsl:apply-templates select="spirit:version"/>
            </xsl:element>
            
            <!-- hierarchical connections and component references -->
            <xsl:element name="vertices">
                <xsl:apply-templates select="spirit:componentInstances/spirit:componentInstance"/>
                <xsl:apply-templates select="spirit:hierConnections" mode="vertex"/>
            </xsl:element>
            
            <!-- Interconnections -->
            <xsl:element name="edges">
                <xsl:apply-templates select="spirit:interconnections"/>
                <xsl:apply-templates select="spirit:hierConnections" mode="edge"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- template for the hierarchical connections -->
    <!-- Adding not only a hierarchical port but also  -->
    <!-- a connection to the associated component reference -->
    <xsl:template match="spirit:hierConnection" mode="vertex">
        <xsl:element name="vertex">
            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="@spirit:interfaceRef"/>
            </xsl:call-template>
            <xsl:attribute name="type">hierConnection</xsl:attribute>
            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value" select="@spirit:interfaceRef"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="spirit:hierConnection" mode="edge">
        <xsl:element name="edge">
            <xsl:attribute name="type">hierConnection</xsl:attribute>
            <xsl:attribute name="source" select="spirit:activeInterface[1]/@spirit:componentRef"/>
            <xsl:attribute name="target" select="@spirit:interfaceRef"/>
            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">source port</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:activeInterface[1]/@spirit:busRef"/></xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- templates for the VLNV of the design -->
    <xsl:template match="spirit:vendor">
        <xsl:element name="parameter">
            <xsl:attribute name="name">vendor</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="spirit:library">
        <xsl:element name="parameter">
            <xsl:attribute name="name">library</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="spirit:name">
        <xsl:element name="parameter">
            <xsl:attribute name="name">name</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="spirit:version">
        <xsl:element name="parameter">
            <xsl:attribute name="name">version</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <!-- template for the component instances -->
    <xsl:template match="spirit:componentInstance" mode="#default">
        <xsl:variable name="componentType" select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='componentType']"/>
        <xsl:choose>
            <xsl:when test="not(empty($componentType))">
                <xsl:apply-templates select="." mode="specific"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="." mode="generic"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- template for the generic component instances -->
    <xsl:template match="spirit:componentInstance" mode="generic">
        <xsl:element name="vertex">
            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="spirit:instanceName"/>
            </xsl:call-template>
            <xsl:variable name="componentType" select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='componentType']"/>
            <xsl:variable name="refinement" select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='refinement']"/>
            
            <xsl:attribute name="type">componentInstance</xsl:attribute>
            <xsl:element name="parameters">
                <!-- Generic components have no component type -->
                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:instanceName"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">vendor</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:componentRef/@spirit:vendor"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">library</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:componentRef/@spirit:library"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">name</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:componentRef/@spirit:name"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">version</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:componentRef/@spirit:version"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">refinement</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="$refinement"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">componentType</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="$componentType"/></xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- template for the specific preesm component instances -->
    <xsl:template match="spirit:componentInstance" mode="specific">
        <xsl:element name="vertex">
            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="spirit:instanceName"/>
            </xsl:call-template>
            <xsl:variable name="componentType" select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='componentType']"/>
            <xsl:variable name="refinement" select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='refinement']"/>
            <xsl:attribute name="type" select="$componentType"/>
            <xsl:element name="parameters">
                
                <!-- The spirit name parameter is used as a component definition in preesm -->
                <xsl:element name="parameter">
                    <xsl:attribute name="name">definition</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:componentRef/@spirit:name"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:instanceName"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">refinement</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="$refinement"/></xsl:attribute>
                </xsl:element>
                
                
                <!-- operator parameters -->
                <xsl:if test="$componentType='operator'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">dataCopySpeed</xsl:attribute>
                        <xsl:attribute name="value"><xsl:value-of select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='dataCopySpeed']"/></xsl:attribute>
                    </xsl:element>
                </xsl:if>
                <!-- medium parameters -->
                <xsl:if test="$componentType='medium'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">medium_dataRate</xsl:attribute>
                        <xsl:attribute name="value"><xsl:value-of select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='medium_dataRate']"/></xsl:attribute>
                    </xsl:element>
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">medium_overhead</xsl:attribute>
                        <xsl:attribute name="value"><xsl:value-of select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='medium_overhead']"/></xsl:attribute>
                    </xsl:element>
                </xsl:if>
                <!-- node parameters -->
                <xsl:if test="$componentType='parallelNode' or $componentType='contentionNode'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">dataRate</xsl:attribute>
                        <xsl:attribute name="value"><xsl:value-of select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='node_dataRate']"/></xsl:attribute>
                    </xsl:element>
                </xsl:if>
                
                <!-- dma variables are stored like parameters with prefix drivenLink_-->
                <!--  <xsl:if test="$componentType='dma'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">dma driven link</xsl:attribute>
                        <xsl:apply-templates select="spirit:configurableElementValues/spirit:configurableElementValue[starts-with(@spirit:referenceId,'drivenLink_')]" mode="dma_drivenLinks"/>
                    </xsl:element>
                </xsl:if>-->
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- variables entry -->
    <xsl:template match="spirit:configurableElementValue" mode="dma_drivenLinks">
        <xsl:element name="entry">
            <xsl:attribute name="key" select="replace(@spirit:referenceId,'drivenLink_','')"/>
            <xsl:attribute name="value" select="."/>
        </xsl:element>
    </xsl:template>
    
    <!-- template for the interconnections -->
    <xsl:template match="spirit:interconnection">
        <xsl:element name="edge">
            <xsl:choose>
                <xsl:when test="spirit:displayName='setup'">
                    <xsl:attribute name="type">setup</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="type">interconnection</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            
            <xsl:attribute name="source" select="spirit:activeInterface[1]/@spirit:componentRef"/>
            <xsl:attribute name="target" select="spirit:activeInterface[2]/@spirit:componentRef"/>
            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:name"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">source port</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:activeInterface[1]/@spirit:busRef"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">target port</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="spirit:activeInterface[2]/@spirit:busRef"/></xsl:attribute>
                </xsl:element>
                <xsl:if test="spirit:displayName='setup'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">setupTime</xsl:attribute>
                        <xsl:attribute name="value"><xsl:value-of select="spirit:description"/></xsl:attribute>
                    </xsl:element>
                </xsl:if>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>
