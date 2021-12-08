#!/bin/bash

echo "### Comenzando..."
cd fiubamate-plugin
mvn
if [ $? -ne 0 ]; then
    echo "### Error en el build de mvn"
    exit 1
fi
cd ..
echo "### Termino compilacion"
echo "### Copiando plugin compilado..."
pwd
cp ./fiubamate-plugin/target/FIUBAmate-1.0.0.jar ../Fiji.app/plugins/
echo "### Lanzando ImageJ..."
../Fiji.app/ImageJ-linux64 "http://fiji.sc/samples/FakeTracks.tif"
