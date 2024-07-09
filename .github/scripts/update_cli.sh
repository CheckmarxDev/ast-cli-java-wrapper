#!/bin/bash

set -e  # Exit immediately if a command exits with a non-zero status
set -o pipefail  # Pipelines return the status of the last command to exit with a non-zero status

release=$1
filename_windows=ast-cli_${release}_windows_x64.zip
filename_linux=ast-cli_${release}_linux_x64.tar.gz
filename_darwin=ast-cli_${release}_darwin_x64.tar.gz

# Ensure Git LFS is tracking the large files
git lfs track "src/main/resources/cx.exe"
git lfs track "src/main/resources/cx-linux"
git lfs track "src/main/resources/cx-mac"

# Ensure the target directory exists
mkdir -p src/main/resources

# Windows
echo "Updating Windows binary"
wget https://github.com/checkmarx/ast-cli/releases/download/${release}/${filename_windows}
unzip ${filename_windows} -d tmp
mv tmp/cx.exe src/main/resources/cx.exe
rm -rf tmp
rm ${filename_windows}

# Linux
echo "Updating Linux binary"
wget https://github.com/checkmarx/ast-cli/releases/download/${release}/${filename_linux}
mkdir tmp
tar -xvzf ${filename_linux} -C tmp
mv tmp/cx src/main/resources/cx-linux
rm -rf tmp
rm ${filename_linux}

# Darwin (Mac)
echo "Updating Mac binary"
wget https://github.com/checkmarx/ast-cli/releases/download/${release}/${filename_darwin}
mkdir tmp
tar -xvzf ${filename_darwin} -C tmp
mv tmp/cx src/main/resources/cx-mac
rm -rf tmp
rm ${filename_darwin}

# Add the updated files to Git LFS
git add src/main/resources/cx.exe
git add src/main/resources/cx-linux
git add src/main/resources/cx-mac

# Add the .gitattributes file to ensure LFS tracking is committed
git add .gitattributes

echo "Update completed successfully"
