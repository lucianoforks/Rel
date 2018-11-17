#!/bin/bash

relversion=3.013
javaversion=jdk-11.0.1
jredir=~/Documents/OpenJDKs
proddir=~/git/Rel/_Deployment/product

# Clear
./productClear.sh

# Grammar
~/bin/jjdoc ../ServerV0000/src/org/reldb/rel/v0/languages/tutoriald/parser/TutorialD.jj
mv TutorialD.html $proddir

# Scripts
rm -rf Scripts/.DS_Store
cp -R Scripts $proddir/RelScripts
pushd $proddir/
zip -9r Rel_ExamplesAndUtilities_$relversion.zip RelScripts
popd

# Build JREs
pushd MakeJRE
./build.sh
popd

# Linux GTK 64bit
echo "---------------------- Linux Build ----------------------"
linuxtarget=linux.gtk.x86_64
linuxtargetRel=$linuxtarget/Rel
mkdir -p $proddir/$linuxtargetRel
mv MakeJRE/linux_jre $proddir/$linuxtargetRel/jre
cp nativeLaunchers/Linux/Rel $proddir/$linuxtargetRel
cp -R lib $proddir/$linuxtargetRel
rm -rf $proddir/$linuxtargetRel/lib/swt/win_64
rm -rf $proddir/$linuxtargetRel/lib/swt/macos_64
cp nativeLaunchers/Linux/Rel.ini $proddir/$linuxtargetRel/lib
cp splash.png $proddir/$linuxtargetRel/lib
chmod +x $proddir/$linuxtargetRel/jre/bin/*
pushd $proddir/$linuxtarget
tar cfz ../Rel$relversion.$linuxtarget.tar.gz Rel
popd

# Windows 64bit
echo "---------------------- Windows Build ----------------------"
wintarget=win32.win32.x86_64
wintargetRel=$wintarget/Rel
mkdir -p $proddir/$wintargetRel
mv MakeJRE/windows_jre $proddir/$wintargetRel/jre
cp nativeLaunchers/Windows/x64/Release/Rel.exe $proddir/$wintargetRel
cp -R lib $proddir/$wintargetRel
rm -rf $proddir/$wintargetRel/lib/swt/linux_64
rm -rf $proddir/$wintargetRel/lib/swt/macos_64
cp nativeLaunchers/Windows/Rel.ini $proddir/$wintargetRel/lib
cp splash.png $proddir/$wintargetRel/lib
pushd $proddir/$wintarget
zip -9r ../Rel$relversion.$wintarget.zip Rel
popd

# MacOS (64bit)
echo "---------------------- MacOS Build ----------------------"
mactarget=macosx.cocoa.x86_64
mkdir $proddir/$mactarget
cp -R nativeLaunchers/MacOS/Rel.app $proddir/$mactarget
cp nativeLaunchers/MacOS/launchBinSrc/Rel $proddir/$mactarget/Rel.app/Contents/MacOS
mv MakeJRE/osx_jre $proddir/$mactarget/Rel.app/Contents/MacOS/jre
cp -R lib $proddir/$mactarget/Rel.app/Contents/MacOS/
rm -rf $proddir/$mactarget/Rel.app/Contents/MacOS/lib/swt/linux_64
rm -rf $proddir/$mactarget/Rel.app/Contents/MacOS/lib/swt/win_64
cp nativeLaunchers/MacOS/Rel.ini $proddir/$mactarget/Rel.app/Contents/MacOS/lib
cp splash.png $proddir/$mactarget/Rel.app/Contents/MacOS/lib
cp OSXPackager/Background.png $proddir/$mactarget
cp OSXPackager/Package.command $proddir/$mactarget
pushd $proddir/$mactarget
./Package.command $relversion
mv *.dmg $proddir
rm Background.png
rm Package.command
popd

# Standalone Rel DBMS (Java)
echo "---------------------- Standalone DBMS Build ----------------------"
tar cf $proddir/Rel$relversion.DBMS.tar LICENSE.txt AUTHORS.txt CHANGES.txt LIBRARIES.txt TODO.txt README.txt lib/jdt/* lib/misc/* lib/rel/RelDBMS.jar lib/rel/RelTest.jar lib/rel/relshared.jar lib/rel/rel0000.jar lib/rel/relclient.jar
pushd launchDBMSScripts
tar rf $proddir/Rel$relversion.DBMS.tar *
popd
pushd $proddir
gzip -9 Rel$relversion.DBMS.tar
popd