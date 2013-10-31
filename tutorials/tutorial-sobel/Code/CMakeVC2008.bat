@echo off
mkdir bin
cd bin
mkdir VS_main
cd VS_main
mkdir Debug
mkdir Release
pushd .

cd ..\..\lib\SDL-*
cd .\lib\
copy x86\SDL.dll  ..\..\..\bin\VS_main\Debug\SDL.dll /Y
copy x86\SDL.dll  ..\..\..\bin\VS_main\Release\SDL.dll /Y

cd ..\..\pthread-*
cd .\lib\
copy .\pthreadVC2.dll ..\..\..\bin\VS_main\Debug\pthreadVC2.dll /Y
copy .\pthreadVC2.dll ..\..\..\bin\VS_main\Release\pthreadVC2.dll /Y

popd
cmake ..\..\ -G "Visual Studio 9 2008"

pause