@echo off
echo ### Comenzando...
cd fiubamate-plugin
call mvn
echo ### Termino compilacion
cd ..
echo ### Copiando plugin compilado...
copy .\fiubamate-plugin\target\FIUBAmate-1.0.0.jar ..\Fiji.app\plugins
echo ### Lanzando ImageJ...
..\Fiji.app\ImageJ-win64.exe -file-name "http://fiji.sc/samples/FakeTracks.tif"
exit