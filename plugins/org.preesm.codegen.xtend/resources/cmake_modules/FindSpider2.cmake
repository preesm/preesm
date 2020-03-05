set(SPIDER2_SEARCH_PATHS
	~/Library/Frameworks
	/Library/Frameworks
	/usr/local
	/usr
	/sw # Fink
	/opt/local # DarwinPorts
	/opt/csw # Blastwave
	/opt
    ${CMAKE_SOURCE_DIR}/lib/spider
)
find_path(SPIDER2_INCLUDE_DIR api/spider.h
	HINTS
	$ENV{SPIDER2DIR}
	PATH_SUFFIXES include
	PATHS ${SPIDER2_SEARCH_PATHS}
)

message("SPIDER2_INCLUDE_DIR : ${SPIDER2_INCLUDE_DIR}")
find_library(SPIDER2_LIBRARY
	NAMES spider2
	HINTS
	$ENV{SPIDER2DIR}
	PATH_SUFFIXES lib
	PATHS ${SPIDER2_SEARCH_PATHS}
)
message("SPIDER2_LIBRARY : ${SPIDER2_LIBRARY}")


include(FindPackageHandleStandardArgs)

find_package_handle_standard_args(SPIDER2 REQUIRED_VARS SPIDER2_LIBRARY SPIDER2_INCLUDE_DIR)
