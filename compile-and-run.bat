@echo off
echo ### Comenzando...
cd fiubamate-plugin
@REM call mvn clean
call mvn
echo ### Maven Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b
echo ### Termino compilacion
cd ..
echo ### Copiando plugin compilado...
copy /y .\fiubamate-plugin\target\FIUBAmate-1.0.0.jar ..\Fiji.app\plugins
echo ### Lanzando ImageJ...
..\Fiji.app\ImageJ-win64.exe -file-name "http://fiji.sc/samples/FakeTracks.tif"
