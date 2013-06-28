@echo off
mkdir bin
cd bin
mkdir VS_main
cd VS_main
mkdir Debug
mkdir Release

copy ..\..\lib\pthread-2.8.0\lib\pthreadVC2.dll .\Debug\pthreadVC2.dll /Y
copy ..\..\lib\SDL-1.2.14\lib\SDL.dll .\Debug\SDL.dll /Y

cmake ..\..\ -G "Visual Studio 9 2008"

pause