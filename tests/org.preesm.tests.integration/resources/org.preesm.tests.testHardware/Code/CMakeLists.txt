cmake_minimum_required(VERSION 2.6)

# *******************************************
# ************* CMake Content ***************
# *******************************************
# This CMake create a workspace containing the following projects
# 
# Programs
#  - test_matmul_kernels

# IDE dependent config
if(${CMAKE_GENERATOR} MATCHES "Visual Studio.*")
	MESSAGE("Add definition for ${CMAKE_GENERATOR}")
	add_definitions( -D_CRT_SECURE_NO_WARNINGS )
endif()

project(test_addvector)
set(LIBS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/lib)

# Add definition for relative path into project
add_definitions( -DPROJECT_ROOT_PATH="${CMAKE_CURRENT_SOURCE_DIR}")

# Set to 1 to activate debug compilation (0 for release)
if(NOT ${CMAKE_GENERATOR} MATCHES "Visual Studio.*")
	set(DEBUG 0)

	if(${DEBUG})
			MESSAGE("Generate Debug project")
			set(CMAKE_BUILD_TYPE Debug)
			set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/Debug)
			set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -g -pg -Wall")
	else()
			MESSAGE("Generate Release project")
			set(CMAKE_BUILD_TYPE Release)
			set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/Release)
			set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -Wall")
	endif()
else()
	set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR})
endif()

#~ if(WIN32)
	#~ set(LIBS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/lib)
#~ endif()
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_CURRENT_SOURCE_DIR}/lib/cmake_modules/")

# *******************************************
# ************** SDL2 LIBRARY ***************
# *******************************************
# Set variables before finding SDL Library
# Helps to find where SDL libraries are saved (win32 only)
#~ if(WIN32)
    #~ # find the SDL2-x.y.z folder in the lib directory.
	#~ file(GLOB SDL2DIR "${LIBS_DIR}/SDL2-[\\.|0-9]*")
	#~ set(ENV{SDL2DIR} ${SDL2DIR})
#~ endif()


#~ Find_Package (SDL2 REQUIRED )

#~ if(NOT SDL2_FOUND)
  #~ MESSAGE(FATAL_ERROR "SDL not found !")
#~ endif()

#~ if (WIN32)
	#~ file(GLOB
		#~ SDL2_DLL
		#~ ${SDL2DIR}/bin/*.dll
		#~ ${SDL2DIR}/lib/*.dll
	#~ )
	
	#~ MESSAGE("Copy SDL DLLs into ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}")
	#~ if(NOT ${CMAKE_GENERATOR} MATCHES "Visual Studio.*")
		#~ file(COPY ${SDL2_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY})
	#~ else()
		#~ file(COPY ${SDL2_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/Debug)
		#~ file(COPY ${SDL2_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/Release)
	#~ endif()
#~ endif()

# *******************************************
# ************ SDL2TTF LIBRARY **************
# ******************************************
# Set variables before finding SDL2TTF Library
# Helps to find where SDL2TTF libraries are saved (win32 only)
#~ if(WIN32)
    #~ # find the SDL2_ttf-x.y.z folder in the lib directory.
	#~ file(GLOB SDL2TTFDIR "${LIBS_DIR}/SDL2_ttf-[\\.|0-9]*")
	#~ set(ENV{SDL2TTFDIR} ${SDL2TTFDIR})
#~ endif()


#~ Find_Package (SDL2TTF REQUIRED )

#~ if(NOT SDL2TTF_FOUND)
  #~ MESSAGE(FATAL_ERROR "SDL2TTF not found !")
#~ endif()

#~ if (WIN32)
	#~ file(GLOB
		#~ SDL2TTF_DLL
		#~ ${SDL2TTFDIR}/bin/*.dll
		#~ ${SDL2TTFDIR}/lib/*.dll
	#~ )
	
	#~ MESSAGE("Copy SDL2_ttf DLLs into ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}")
	#~ if(NOT ${CMAKE_GENERATOR} MATCHES "Visual Studio.*")
		#~ file(COPY ${SDL2TTF_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY})
	#~ else()
		#~ file(COPY ${SDL2TTF_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/Debug)
		#~ file(COPY ${SDL2TTF_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/Release)
	#~ endif()
#~ endif()

# *******************************************
# ************ Pthread LIBRARY **************
# *******************************************
#~ if (WIN32)
  #~ set(THREADS_USE_PTHREADS_WIN32 true)
	#~ # pthread included AFTER Sdl to avoid unnecessary warnings
	#~ file(GLOB PTHREADDIR "${LIBS_DIR}/pthread-[\\.|0-9]*")
	#~ set(CMAKE_PREFIX_PATH ${CMAKE_PREFIX_PATH} ${PTHREADDIR})
	#~ Find_Package ( THREADS REQUIRED )
#~ endif()

find_package( Threads )

if(NOT THREADS_FOUND)
	MESSAGE(FATAL_ERROR "Pthread not found !")
endif()


#~ if (WIN32)
	#~ file(GLOB
		#~ PTHREADS_DLL
		#~ ${CMAKE_PREFIX_PATH}/lib/*.dll
	#~ )

	#~ MESSAGE("Copy Pthreads DLLs into ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}")
	#~ if(NOT ${CMAKE_GENERATOR} MATCHES "Visual Studio.*")
		#~ file(COPY ${PTHREADS_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY})
	#~ else()
		#~ file(COPY ${PTHREADS_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/Debug)
		#~ file(COPY ${PTHREADS_DLL} DESTINATION ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/Release)
	#~ endif()
#~ endif()


# *******************************************
# **************** test_addvector.exe  ****************
# *******************************************
#~ include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include ${SDL2_INCLUDE_DIR} ${SDL2TTF_INCLUDE_DIR} ${THREADS_PTHREADS_INCLUDE_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include ${THREADS_PTHREADS_INCLUDE_DIR})
file(
	GLOB_RECURSE
	source_files
	./src/*.c
	./src/*.h
	./generated/*.c
	./include/*.h
)
add_definitions(-DZYNQMP)
add_executable( test_matmul_kernels ${source_files} ${header_files})
target_link_libraries(test_matmul_kernels ${CMAKE_THREAD_LIBS_INIT})
#~ target_link_libraries(test_matmul_kernels ${SDL2_LIBRARY} ${SDL2TTF_LIBRARY} ${CMAKE_THREAD_LIBS_INIT})
