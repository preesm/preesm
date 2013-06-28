@echo off
mkdir bin
cd bin
mkdir CB_main
cd CB_main

copy ..\..\lib\SDL-1.2.14\lib\SDL.dll .\SDL.dll /Y

cmake ..\..\ -G "CodeBlocks - MinGW Makefiles"

pause