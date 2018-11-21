<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:spirit="http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4"
    xmlns:slam="http://sourceforge.net/projects/dftools/slam">
    
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
            <xsl:attribute name="type">S-LAM Design</xsl:attribute>
            
            <!-- VLNV of the design -->
            <xsl:element name="parameters">
                <xsl:apply-templates select="spirit:vendor"/>
                <xsl:apply-templates select="spirit:library"/>
                <xsl:apply-templates select="spirit:name"/>
                <xsl:apply-templates select="spirit:version"/>
                
                <xsl:element name="parameter">
                    <xsl:attribute name="name">design parameters</xsl:attribute>
                    <xsl:apply-templates select="spirit:vendorExtensions/slam:designDescription/slam:parameters/slam:parameter" mode="design"/>
                </xsl:element>
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
    
    <!-- template for graph parameters -->
    <xsl:template match="slam:parameter" mode="design">
        <xsl:element name="entry">
            <xsl:attribute name="key" select="@slam:key"/>
            <xsl:attribute name="value" select="@slam:value"/>
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
    
    <!-- returns the type of a component -->
    <xsl:template name="getComponentType">
        <xsl:param name="componentName"/>
        <xsl:value-of select="//slam:componentDescription[@slam:componentRef=$componentName]/@slam:componentType"/>
    </xsl:template>
    
    <!-- returns the refinement of a component -->
    <xsl:template name="getInstanceRefinement">
        <xsl:param name="componentName"/>
        <xsl:value-of select="//slam:componentDescription[@slam:componentRef=$componentName]/@slam:refinement"/>
    </xsl:template>
    
    <!-- template for the s-lam component instances -->
    <xsl:template match="spirit:componentInstance">
        <xsl:element name="vertex">
            <xsl:call-template name="getVertexLayoutAttributes">
                <xsl:with-param name="vertexId" select="spirit:instanceName"/>
            </xsl:call-template>
            <xsl:variable name="componentName" select="spirit:componentRef/@spirit:name"/>
            
            <xsl:variable name="componentType">
                <xsl:value-of select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='parallel']"/>
                    <xsl:call-template name="getComponentType">
                        <xsl:with-param name="componentName" select="$componentName"/>
                    </xsl:call-template>
            </xsl:variable>
            
            <xsl:variable name="refinement">
                <xsl:value-of>
                    <xsl:call-template name="getInstanceRefinement">
                        <xsl:with-param name="componentName" select="$componentName"/>
                    </xsl:call-template>
                </xsl:value-of>
            </xsl:variable>
            
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
                
                <xsl:element name="parameter">
                    <xsl:attribute name="name">custom parameters</xsl:attribute>
                    <xsl:apply-templates select="spirit:configurableElementValues"/>
                </xsl:element>
                
                <!-- operator parameters -->
                <xsl:if test="$componentType='operator'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">dataCopySpeed</xsl:attribute>
                        <xsl:attribute name="value" select="spirit:configurableElementValues/spirit:configurableElementValue[@spirit:referenceId='dataCopySpeed']"/>
                    </xsl:element>
                </xsl:if>
                
                <!-- node parameters -->
                <xsl:if test="$componentType='parallelComNode' or $componentType='contentionComNode'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">speed</xsl:attribute>
                        <xsl:attribute name="value" select="//slam:componentDescription[@slam:componentRef=$componentName]/@slam:speed"/>
                    </xsl:element>
                </xsl:if>
                
                <!-- node parameters -->
                <xsl:if test="$componentType='Mem'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">memSize</xsl:attribute>
                        <xsl:attribute name="value" select="//slam:componentDescription[@slam:componentRef=$componentName]/@slam:size"/>
                    </xsl:element>
                </xsl:if>
                
                <!-- node parameters -->
                <xsl:if test="$componentType='Dma'">
                    <xsl:element name="parameter">
                        <xsl:attribute name="name">setupTime</xsl:attribute>
                        <xsl:attribute name="value" select="//slam:componentDescription[@slam:componentRef=$componentName]/@slam:setupTime"/>
                    </xsl:element>
                </xsl:if>
                
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!-- manage component custom parameters -->
    <xsl:template match="spirit:configurableElementValues">
        <xsl:for-each select="spirit:configurableElementValue">
            <xsl:element name="entry">
                <xsl:attribute name="key" select="@spirit:referenceId"/>
                <xsl:attribute name="value" select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <!-- returns the type of an interconnection -->
    <xsl:template name="getInterconnectionType">
        <xsl:param name="interconnectionID"/>
        <xsl:value-of select="//slam:linkDescription[@slam:referenceId=$interconnectionID]/@slam:linkType"/>
    </xsl:template>
    
    <!-- determines whether an interconnection is directed or not -->
    <xsl:template name="getInterconnectionOrientation">
        <xsl:param name="interconnectionID"/>
        <xsl:value-of select="//slam:linkDescription[@slam:referenceId=$interconnectionID]/@slam:directedLink"/>
    </xsl:template>
    
    <!-- template for the interconnections -->
    <xsl:template match="spirit:interconnection">
        <xsl:element name="edge">
            <xsl:variable name="interconnectionID" select="spirit:name"/>
            <xsl:variable name="interconnectionType">
                <xsl:value-of>
                    <xsl:call-template name="getInterconnectionType">
                        <xsl:with-param name="interconnectionID" select="$interconnectionID"/>
                    </xsl:call-template>
                </xsl:value-of>
            </xsl:variable>
            <xsl:variable name="interconnectionOrientation">
                <xsl:value-of>
                    <xsl:call-template name="getInterconnectionOrientation">
                        <xsl:with-param name="interconnectionID" select="$interconnectionID"/>
                    </xsl:call-template>
                </xsl:value-of>
            </xsl:variable>
            <!-- The edge type is given by the concatenation of the orientation and the type -->
            <xsl:attribute name="type">
                <xsl:if test="$interconnectionType='DataLink'">
                    <xsl:value-of select="$interconnectionOrientation"/>
                </xsl:if>
                <xsl:value-of select="$interconnectionType"/>
            </xsl:attribute>
            
            <xsl:attribute name="source" select="spirit:activeInterface[1]/@spirit:componentRef"/>
            <xsl:attribute name="target" select="spirit:activeInterface[2]/@spirit:componentRef"/>
            <xsl:element name="parameters">
                <xsl:element name="parameter">
                    <xsl:attribute name="name">id</xsl:attribute>
                    <xsl:attribute name="value" select="spirit:name"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">source port</xsl:attribute>
                    <xsl:attribute name="value" select="spirit:activeInterface[1]/@spirit:busRef"/>
                </xsl:element>
                <xsl:element name="parameter">
                    <xsl:attribute name="name">target port</xsl:attribute>
                    <xsl:attribute name="value" select="spirit:activeInterface[2]/@spirit:busRef"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>
