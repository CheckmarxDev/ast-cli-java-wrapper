#!/bin/bash

release=$1
filename_windows=ast-cli_${release}_windows_x64.zip
filename_linux=ast-cli_${release}_linux_x64.tar.gz
filename_linuxarm=ast-cli_${release}_linux_arm64.tar.gz
filename_darwin=ast-cli_${release}_darwin_x64.tar.gz

#Windows
echo "Updating windows binary"
wget https://github.com/checkmarx/ast-cli/releases/download/${release}/${filename_windows}
unzip ${filename_windows} -d tmp
mv ./tmp/cx.exe ./src/main/resources/cx.exe
rm -r tmp
rm ${filename_windows}

#linux
echo "Updating linux binary"
wget https://github.com/checkmarx/ast-cli/releases/download/${release}/${filename_linux}
mkdir ./tmp/
tar -xvzf  ${filename_linux} -C ./tmp/
mv ./tmp/cx ./src/main/resources/cx-linux
rm -r tmp
rm ${filename_linux}

#linuxarm
echo "Updating linuxarm binary"
wget https://github.com/checkmarx/ast-cli/releases/download/${release}/${filename_linuxarm}
mkdir ./tmp/
tar -xvzf  ${filename_linuxarm} -C ./tmp/
mv ./tmp/cx ./src/main/resources/cx-linux-arm
rm -r tmp
rm ${filename_linuxarm}

#darwin
echo "Updating mac binary"
wget https://github.com/checkmarx/ast-cli/releases/download/${release}/${filename_darwin}
mkdir ./tmp/
tar -xvzf  ${filename_darwin} -C ./tmp/
mv ./tmp/cx ./src/main/resources/cx-mac
rm -r tmp
rm ${filename_darwin}
