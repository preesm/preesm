#!/usr/bin/python

import sys

print 'Arguments: Filename', str(sys.argv[1]), 'nb_core', str(sys.argv[2])

def add_component(f,instanceName,spiritName):
	f.write("        <spirit:componentInstance>\n")
	f.write("            <spirit:instanceName>" + instanceName + "</spirit:instanceName>\n")
	f.write("            <spirit:componentRef spirit:library=\"\" spirit:name=\"" + spiritName + "\"\n")
	f.write("            spirit:vendor=\"\" spirit:version=\"\"/>\n")
	f.write("            <spirit:configurableElementValues/>\n")
	f.write("        </spirit:componentInstance>\n")

def add_design_header(f,core_type):
	f.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
	f.write("<spirit:design xmlns:spirit=\"http://www.spiritconsortium.org/XMLSchema/SPIRIT/1.4\">\n")
	f.write("    <spirit:library>preesm</spirit:library>\n")
	f.write("    <spirit:vendor>ietr</spirit:vendor>\n")
	f.write("    <spirit:name>")
	f.write(core_type)
	f.write("</spirit:name>\n")
	f.write("    <spirit:version>1</spirit:version>\n")

def add_design_footer(f):
	f.write("</spirit:design>\n")

def add_component_instances_header(f):
	f.write("    <spirit:componentInstances>\n")

def add_component_instances_footer(f):
	f.write("    </spirit:componentInstances>\n")

def add_interconnection_header(f):
	f.write("    <spirit:interconnections>\n")

def add_interconnection_footer(f):
	f.write("    </spirit:interconnections>\n")

def add_interconnection(f,link_name,memory,core):
	f.write("        <spirit:interconnection>\n")
	f.write("            <spirit:name>" + link_name  + "</spirit:name>\n")
	f.write("            <spirit:activeInterface spirit:busRef=\"" + memory + "\" spirit:componentRef=\"" + memory + "\"/>\n")
	f.write("            <spirit:activeInterface spirit:busRef=\"" + memory + "\" spirit:componentRef=\"" + core  + "\"/>\n")
	f.write("        </spirit:interconnection>\n")

def add_hierConnection_header_footer(f):
	f.write("    <spirit:hierConnections/>\n")

def add_vendorExtension_header(f):
	f.write("    <spirit:vendorExtensions>\n")

def add_vendorExtension_footer(f):
	f.write("    </spirit:vendorExtensions>\n")

def add_componentDescription(f,core_type,memory,memory_speed):
	f.write("        <slam:componentDescriptions xmlns:slam=\"http://sourceforge.net/projects/dftools/slam\">\n")
	f.write("            <slam:componentDescription slam:componentRef=\"" + core_type + "\"\n")
	f.write("                slam:componentType=\"Operator\" slam:refinement=\"\"/>\n")
	f.write("            <slam:componentDescription slam:componentRef=\"" + memory + "\"\n")
	f.write("                slam:componentType=\"parallelComNode\" slam:refinement=\"\" slam:speed=\"" + memory_speed + "\"/>\n")
	f.write("        </slam:componentDescriptions>\n")

def add_linkDescriptions_header(f):
	f.write("    <slam:linkDescriptions xmlns:slam=\"http://sourceforge.net/projects/dftools/slam\">\n")

def add_linkDescriptions_footer(f):
	f.write("    </slam:linkDescriptions>\n")

def add_linkDescription(f,link,directed_link):
	f.write("        <slam:linkDescription slam:directedLink=\"" + directed_link + "\"\n")
	f.write("            slam:linkType=\"DataLink\" slam:referenceId=\"" + link + "\"/>\n")

def add_designDescription_header_footer(f):
	f.write("        <slam:designDescription xmlns:slam=\"http://sourceforge.net/projects/dftools/slam\">\n")
	f.write("            <slam:parameters/>\n")
	f.write("        </slam:designDescription>\n")

def generate_shared_mem_cores(filename,nb_core):
	f = open(filename, "w+")
	core_type = 'MPPA2'
	memory_speed = '1000000000'
	add_design_header(f,core_type)
	add_component_instances_header(f)
	add_component(f,"shared_mem","SHARED_MEM")
	for x in range(nb_core):
		core = "Core" + str(x)
		add_component(f,core,core_type)
	add_component_instances_footer(f)
	add_interconnection_header(f)
	for x in range(nb_core):
		link = "Link" + str(x)
		core = "Core" + str(x)
		add_interconnection(f,link,"shared_mem",core)
	add_interconnection_footer(f)
	add_hierConnection_header_footer(f)
	add_vendorExtension_header(f)
	add_componentDescription(f,core_type,"SHARED_MEM",memory_speed)
	add_linkDescriptions_header(f)
	for x in range(nb_core):
		link = "Link" + str(x)
		add_linkDescription(f,link,"undirected")
	add_linkDescriptions_footer(f)
	add_designDescription_header_footer(f)
	add_vendorExtension_footer(f)
	add_design_footer(f)
	f.write("\n")
	f.close

generate_shared_mem_cores(sys.argv[1], int(sys.argv[2]))
