@echo off
mkdir bin
cd bin
mkdir CB_main
mkdir CB_main_Release
cd CB_main
pushd .

cd ..\..\lib\SDL-*
cd .\lib\
copy x86\SDL.dll  ..\..\..\bin\CB_main\SDL.dll /Y
copy x86\SDL.dll  ..\..\..\bin\CB_main_Release\SDL.dll /Y

cd ..\..\pthread-*
cd .\lib\
copy .\pthreadGC2.dll ..\..\..\bin\CB_main\pthreadGC2.dll /Y
copy .\pthreadGCE2.dll ..\..\..\bin\CB_main\pthreadGCE2.dll /Y
copy .\pthreadGC2.dll ..\..\..\bin\CB_main_Release\pthreadGC2.dll /Y
copy .\pthreadGCE2.dll ..\..\..\bin\CB_main_Release\pthreadGCE2.dll /Y

popd
cmake ..\..\ -G "CodeBlocks - MinGW Makefiles"
cd ..
cd CB_main_Release
cmake -DCMAKE_BUILD_TYPE=Release ..\..\ -G "CodeBlocks - MinGW Makefiles"
pause