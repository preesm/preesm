cmake_minimum_required(VERSION 2.6)

# *******************************************
# ************* CMake Content ***************
# *******************************************
# This CMake create a workspace containing the following projects
# 
# Programs
#  - sobel

# IDE dependent config
if(${CMAKE_GENERATOR} MATCHES "Visual Studio 9 2008")
	MESSAGE("Add definition for ${CMAKE_GENERATOR}")
	add_definitions( -D_CRT_SECURE_NO_WARNINGS )
endif()

project(Sobel)
set(LIBS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/lib)
set(CMAKE_C_FLAGS "-std=c99 -DX1_CORE") 

# *******************************************
# ************** SDL LIBRARY ****************
# *******************************************
# Set variables before finding SDL Library
# Helps to find where SDL libraries are saved (win32 only)
if(WIN32)
	set(ENV{SDLDIR} ${LIBS_DIR}/SDL-*)
	#set(ENV{SDLIMAGEDIR} ${LIBS_DIR}/SDL_image-*)
endif()

Find_Package ( SDL REQUIRED )
#Find_Package ( SDL_image REQUIRED ) # if using SDL_image

if(NOT SDL_FOUND)
  MESSAGE(FATAL_ERROR "SDL not found !")
endif()

#if(NOT SDLIMAGE_FOUND)
#  MESSAGE(FATAL_ERROR "SDL image not found !")
#endif()

# *******************************************
# ************ Pthread LIBRARY **************
# *******************************************
if (WIN32)
  set(THREADS_USE_PTHREADS_WIN32 true)
# pthread included AFTER Sdl to avoid unnecessary warnings
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_CURRENT_SOURCE_DIR}/lib/cmake_modules/")
set(CMAKE_PREFIX_PATH ${CMAKE_PREFIX_PATH} "${CMAKE_CURRENT_SOURCE_DIR}/lib/pthread-2.8.0/")
Find_Package ( THREADS REQUIRED )
endif()

if(NOT THREADS_FOUND)
	MESSAGE(FATAL_ERROR "Pthread not found !")
endif()

# *******************************************
# **************** Sobel.exe  ****************
# *******************************************
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include ${THREADS_PTHREADS_INCLUDE_DIR} ${SDL_INCLUDE_DIR} ${SDLIMAGE_INCLUDE_DIR})
file(
	GLOB_RECURSE
	source_files
	./src/*.c
	./src/*.h
	./generated/*.c
	./include/*.h
)

add_executable( sobel ${source_files} ${header_files})
target_link_libraries(sobel ${CMAKE_THREAD_LIBS_INIT} ${SDL_LIBRARY} ${SDL_IMAGE})
