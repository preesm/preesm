cmake_minimum_required(VERSION 2.6)

# *******************************************
# ************* CMake Content ***************
# *******************************************
# This CMake create a workspace containing the following projects
#
# Programs
#  - yuv2dat

# IDE dependent config
if(${CMAKE_GENERATOR} MATCHES "Visual Studio 9 2008")
	MESSAGE("Add definition for ${CMAKE_GENERATOR}")
	add_definitions( -D_CRT_SECURE_NO_WARNINGS )
endif()

project(yuv2dat)
set(LIBS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/lib)
set(CMAKE_C_FLAGS "-std=c99 -DVERBOSE")

# *******************************************
# **************** yuv2dat  ****************
# *******************************************
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)
file(
	GLOB_RECURSE
	source_files
	./src/*.c
	./src/*.h
	./include/*.h
)

add_executable( yuv2dat ${source_files} ${header_files})
