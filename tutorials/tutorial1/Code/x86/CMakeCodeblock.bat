@echo off
mkdir bin
cd bin
mkdir CB_main
cd CB_main
pushd .

cd ..\..\lib\pthread-*\lib
copy .\pthreadGC2.dll ..\..\..\bin\CB_main\pthreadGC2.dll /Y
copy .\pthreadGCE2.dll ..\..\..\bin\CB_main\pthreadGCE2.dll /Y

popd
cmake ..\..\ -G "CodeBlocks - MinGW Makefiles"

pause