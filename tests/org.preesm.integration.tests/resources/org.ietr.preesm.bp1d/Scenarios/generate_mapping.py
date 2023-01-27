#!/usr/bin/python

import sys

print 'Arguments: Filename', str(sys.argv[1]), 'nb_core', str(sys.argv[2])

actors = [ 
#	"myapp",
#	"myapp/Split",
#	"myapp/Read_YUV",
#	"myapp/display" 
	"myapp/Dilation/dilation",
	"myapp/Erosion",
	"myapp/Sobel/output",
	"myapp/Sobel",
	"myapp/Dilation",
	"myapp/Erosion/input",
	"myapp/Sobel/sobel",
	"myapp/Erosion/output",
	"myapp/Sobel/input",
	"myapp/Erosion/erosion",
	"myapp/Dilation/input",
	"myapp/Dilation/output",
	]

exec_actors = actors

def add_scenario_header(f):
	f.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
	f.write("<scenario>\n")

def add_scenario_footer(f):
	f.write("</scenario>\n")

def add_file(f,algo,archi,codeGenDir):
	f.write("    <files>\n")
	f.write("        <algorithm url=\"" + algo  + "\"/>\n")
	f.write("        <architecture url=\"" + archi  + "\"/>\n")
	f.write("        <codegenDirectory url=\"" + codeGenDir  + "\"/>");
	f.write("    </files>\n")

def add_constraint_exelUrl_header(f):
	f.write("    <constraints excelUrl=\"\">\n")

def add_constraint_exelUrl_footer(f):
	f.write("    </constraints>\n")

def add_constraintGroup(f,nb_core):
	for x in range(nb_core):
		f.write("        <constraintGroup>\n")
		f.write("            <operator name=\"Core" + str(x) + "\"/>\n")
		for w in range(len(exec_actors)):
			f.write("            <task name=\"" + exec_actors[w]  + "\"/>\n")
		f.write("        </constraintGroup>\n")

def add_relativeConstraint(f):
	f.write("    <relativeconstraints excelUrl=\"\"/>\n")

def add_timings(f,opname):
	f.write("    <timings excelUrl=\"\">\n")
	f.write("        <memcpyspeed opname=\"" + opname + "\" setuptime=\"1\" timeperunit=\"1.0E-8\"/>\n")
	f.write("    </timings>\n")

def add_simuParams(f,mainCore,mainComNode,averageDataSize,nb_core):
	f.write("    <simuParams>\n")
	f.write("        <mainCore>" + mainCore + "</mainCore>\n")
	f.write("        <mainComNode>" + mainComNode + "</mainComNode>\n")
	f.write("        <averageDataSize>" + averageDataSize + "</averageDataSize>\n")
	f.write("        <dataTypes>\n")
	f.write("            <dataType name=\"uchar\" size=\"1\"/>\n")
	f.write("        </dataTypes>\n")
	f.write("        <specialVertexOperators>\n")
	for x in range(nb_core):
		core = "Core" + str(x)
		f.write("            <specialVertexOperator path=\"" + core + "\"/>\n")
	f.write("        </specialVertexOperators>\n")
	f.write("        <numberOfTopExecutions>1</numberOfTopExecutions>\n")
	f.write("    </simuParams>\n")


def add_scenario_footer(f):
	f.write("<variables excelUrl=\"\"/>\n")
	f.write("    <parameterValues/>\n")
	f.write("</scenario>\n")

def generate_mapping(filename,nb_core):
	f = open(filename, "w+")
	add_scenario_header(f)
	add_file(f,"/org.ietr.preesm.hierarchical-sobel-mediane/Algo/myapp.pi",
		   "/org.ietr.preesm.hierarchical-sobel-mediane/Archi/MPPA2Gen.slam",
		   "/org.ietr.preesm.hierarchical-sobel-mediane/CodeMPPA2Flat/generated")
	add_constraint_exelUrl_header(f)
	add_constraintGroup(f,nb_core)
	add_constraint_exelUrl_footer(f)
	add_relativeConstraint(f)
	add_timings(f,"MPPA2Explicit")
	add_simuParams(f,"Core0","shared_mem","10000",1)
	add_scenario_footer(f)
	f.write("\n")
	f.close



generate_mapping(sys.argv[1], int(sys.argv[2]))

