#! /bin/bash

echo "1. Building Demo .apk"
./gradlew :demo:assembleDebug

echo "2. Building SDK .aar"
./gradlew :sdk:assembleRelease

echo "3. Publishing"
./gradlew publishAllPublicationsToTouchPlatformSDKRepository
