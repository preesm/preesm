@echo off
mkdir bin
cd bin
mkdir VS_main
cd VS_main
mkdir Debug
mkdir Release
pushd .

cd ..\..\lib\pthread-*\lib
copy .\pthreadVC2.dll ..\..\..\bin\VS_main\Debug\pthreadVC2.dll /Y
copy .\pthreadVC2.dll ..\..\..\bin\VS_main\Release\pthreadVC2.dll /Y

popd
cmake ..\..\ -G "Visual Studio 9 2008"

pause