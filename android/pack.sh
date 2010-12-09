#!/bin/bash

ant release && 
jarsigner -verbose -keystore gasfinder.keystore bin/GasFinder-unsigned.apk GasFinder && 
rm bin/GasFinder.apk
zipalign -v 4 bin/GasFinder-unsigned.apk bin/GasFinder.apk && 
adb uninstall com.gasfinder && 
adb install bin/GasFinder.apk &&
echo "final success"

