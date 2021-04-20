#! /bin/bash
echo "1. Building SDK .aar"
./gradlew :sdk:assembleRelease

echo "2. Publishing"
./gradlew publish
