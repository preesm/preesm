<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://graphml.graphdrawing.org/xmlns"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    
    <xsl:import href="output_layout.xslt"/>

    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="text()"/>

    <!-- writes the layout in a file that has the same name as the target document,
        except with .layout extension. -->
    <xsl:param name="path"/>
    <xsl:variable name="file" select="replace($path, '(.+)[.].+', '$1.layout')"/>

    <!-- Top-level: graph -> graph -->
    <xsl:template match="graph">
        
        <!-- layout information -->
        <xsl:result-document href="file:///{$file}" method="xml" indent="yes">
            <xsl:call-template name="setLayout"/>
        </xsl:result-document>

        <!-- graph -->
        <graphml>
            <key attr.name="graph_desc" attr.type="string" for="node" id="graph_desc"/>
            <key attr.name="name" attr.type="string" for="graph" id="name"/>
            <key attr.name="name" attr.type="string" for="node" id="name"/>
            <key attr.name="arguments" attr.type="string" for="node" id="arguments"/>
            <key attr.name="parameters" attr.type="string" for="graph" id="parameters"/>
            <key attr.name="variables" attr.type="string" for="graph" id="variables"/>
            <key attr.name="edge_prod" attr.type="string" for="edge" id="edge_prod">
                <desc>net.sf.dftools.algorithm.model.sdf.types.SDFNumericalEdgePropertyTypeFactory</desc>
            </key>
            <key attr.name="edge_delay" attr.type="string" for="edge" id="edge_delay">
                <desc>net.sf.dftools.algorithm.model.sdf.types.SDFNumericalEdgePropertyTypeFactory</desc>
            </key>
            <key attr.name="edge_cons" attr.type="string" for="edge" id="edge_cons">
                <desc>net.sf.dftools.algorithm.model.sdf.types.SDFNumericalEdgePropertyTypeFactory</desc>
            </key>
            <key attr.name="data_type" attr.type="string" for="edge" id="data_type">
                <desc>net.sf.dftools.algorithm.model.sdf.types.SDFTextualEdgePropertyTypeFactory</desc>
            </key>

            <graph edgedefault="directed">
                <data key="name">
                    <xsl:value-of select="parameters/parameter[@name = 'name']/@value"/>
                </data>
                <xsl:apply-templates select="parameters/parameter[@name = 'graph parameter']"/>
                <xsl:apply-templates select="parameters/parameter[@name = 'graph variable']"/>

                <xsl:apply-templates select="vertices/vertex"/>
                <xsl:apply-templates select="edges/edge"/>
            </graph>
        </graphml>
    </xsl:template>

    <!-- Parameter declarations -->
    <xsl:template match="parameter[@name = 'graph parameter']">
            <data key="parameters">
                <xsl:apply-templates select="element"/>
            </data>
    </xsl:template>

    <!-- Variable declarations -->
    <xsl:template match="parameter[@name = 'graph variable']">
        <data key="variables">
            <xsl:apply-templates select="entry" mode="variable"/>
        </data>
    </xsl:template>

    <!-- node -->
    <xsl:template match="vertex[@type = 'Vertex']">
        <node kind="vertex" id="{parameters/parameter[@name = 'id']/@value}">
            <data key="graph_desc">
                <xsl:value-of select="parameters/parameter[@name = 'refinement']/@value"/>
            </data>
            
            <data key="name">
                <xsl:value-of select="parameters/parameter[@name = 'id']/@value"/>
            </data>

            <xsl:apply-templates select="parameters/parameter[@name = 'instance argument']"/>

        </node>
    </xsl:template>
    
    <!-- node parameter -->
    <xsl:template match="parameter[@name = 'instance argument']">
        <data key="arguments">
            <xsl:apply-templates select="entry" mode="argument"/>
        </data>
    </xsl:template>
    
    <!-- arguments argument -->
    <xsl:template match="entry" mode="argument">
        <argument name="{@key}" value="{@value}"/>
    </xsl:template>
    
    <!-- variables variable -->
    <xsl:template match="entry" mode="variable">
        <variable name="{@key}" value="{@value}"/>
    </xsl:template>
 
    
    <!-- parameters parameter -->
    <xsl:template match="element">
        <parameter name="{@value}"/>
    </xsl:template>

    <!-- input/output port -->
    <xsl:template match="vertex[@type = 'Input port' or @type = 'Output port']">
        <node id="{parameters/parameter[@name = 'id']/@value}" kind="port"
            port_direction="{replace(@type, '(Input|Output) port', '$1')}"/>
    </xsl:template>

    <!-- broadcast vertex -->
    <xsl:template match="vertex[@type = 'Broadcast']">
        <node id="{parameters/parameter[@name = 'id']/@value}" kind="Broadcast"/>
    </xsl:template>

    <!-- fork vertex -->
    <xsl:template match="vertex[@type = 'fork']">
        <node id="{parameters/parameter[@name = 'id']/@value}" kind="fork"/>
    </xsl:template>

    <!-- join  vertex -->
    <xsl:template match="vertex[@type = 'join']">
        <node id="{parameters/parameter[@name = 'id']/@value}" kind="join"/>
    </xsl:template>

    <!-- edge -->
    <xsl:template match="edge">
        <edge source="{@source}" target="{@target}"
            sourceport="{parameters/parameter[@name = 'source port']/@value}"
            targetport="{parameters/parameter[@name = 'target port']/@value}">
            <data key="edge_prod">
                <xsl:value-of select="parameters/parameter[@name = 'source production']/@value"/>
            </data>

            <data key="edge_delay">
                <xsl:value-of select="parameters/parameter[@name = 'delay']/@value"/>
            </data>

            <data key="edge_cons">
                <xsl:value-of select="parameters/parameter[@name = 'target consumption']/@value"/>
            </data>
            
            <data key="data_type">
                <xsl:variable name="dataType" select="parameters/parameter[@name = 'data type']/@value"/>
                <xsl:value-of select="$dataType"/>
            </data>
        </edge>
    </xsl:template>

</xsl:stylesheet>
